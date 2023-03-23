#!/usr/bin/env python3

import json
import re
from requests.auth import HTTPBasicAuth
import requests
import argparse
import os

os.environ["REQUESTS_CA_BUNDLE"] = "/etc/ssl/certs/ca-certificates.crt"

parser = argparse.ArgumentParser(
                    prog='ProgramName',
                    description='What the program does',
                    epilog='Text at the bottom of help')

parser.add_argument('--nexus_url', '-U', dest='nexus_url', default="https://nexus.home.arpa/service/rest") 
parser.add_argument('--username', '-u', dest='username', default="admin") 
parser.add_argument('--password', '-p', dest='password', default="secret") 

args = parser.parse_args()

basic_auth=HTTPBasicAuth(args.username, args.password)

# def POST_blobstores_file():
#   print("POST_blobstores_file")

def DELETE_repositories(repository):
  result = requests.delete(f"{args.nexus_url}/v1/repositories/{repository}", auth=basic_auth)
  print(result)

def POST_repositories(repositoryType, payload):
  url=f'{args.nexus_url}/v1/repositories/{repositoryType}'
  print(f'Posting repository {repositoryType}/{payload["name"]}: {url}')
  return requests.post(url, data=json.dumps(payload), auth=basic_auth, headers={'Content-type': 'application/json', 'Accept': 'application/json'})

def POST_repository_apt_group(repositories):
  result = POST_repositories("apt/proxy", payload)
  print(result)

def POST_repository_apt_proxy(line, name):
  pattern_text = r"^(?P<type>\w+)"\
                 r"[\s]*"\
                 r"(\["\
                   r"([\s]*arch=(?P<arch>[^ ]+)[\s]*)?"\
                   r"([\s]*signed-by=(?P<signedBy>[^ ]+)[\s]*)?"\
                 r"\])?"\
                 r"[\s]*"\
                 r"(?P<url>[^ ]*)"\
                 r"[\s]*"\
                 r"(?P<distribution>[^ ]*)"\
                 r"[\s]*"\
                 r"($|(?P<component>.*)$)"
  pattern = re.compile(pattern_text)
  match = pattern.match(line)

  type=match.group("type")
  arch=match.group("arch")
  signedBy=match.group("signedBy")
  url=match.group("url")
  distribution=match.group("distribution")
  component=match.group("component")

  payload = {
    'apt': {
      'flat': False,
      'distribution': distribution
    },
    'httpClient': {
      'blocked': False,
      'autoBlock': True
      # connection
      # authentication
    },
    'name': f"apt-proxy-{name}",
    'negativeCache': {
      'enabled': True,
      'timeToLive': 1440
    },
    'online': True,
    'proxy': {
      'contentMaxAge': 1440,
      'metadataMaxAge': 1440,
      'remoteUrl': url
    },
    'storage': {
      'blobStoreName': "apt-proxy",
      'strictContentTypeValidation': True
    }
    # cleanup: {'policyNames': []}
    # routingRule: ""
    # replication: {
    #   preemptivePullEnabled: True,
    #   assetPathRegex: ""
    # }
  }

  result = POST_repositories("apt/proxy", payload)
  print(result)

POST_repository_apt_proxy("deb http://archive.ubuntu.com/ubuntu/ kinetic", "ubuntu-kinetic")
POST_repository_apt_proxy("deb http://archive.ubuntu.com/ubuntu/ kinetic-backports", "ubuntu-kinetic-backports")
POST_repository_apt_proxy("deb http://archive.ubuntu.com/ubuntu/ kinetic-updates", "ubuntu-kinetic-updates")
POST_repository_apt_proxy("deb http://security.ubuntu.com/ubuntu/ kinetic-security", "ubuntu-kinetic-security")