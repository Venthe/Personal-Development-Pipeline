#!/usr/bin/env python3

# pip install requests urllib3
import requests
import argparse
import urllib3
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

parser = argparse.ArgumentParser(description="Just an example",
                                 formatter_class=argparse.ArgumentDefaultsHelpFormatter)
parser.add_argument("-l", "--disable-tls",
                    action="store_false", help="Should verify TLS?")
parser.add_argument("-v", "--verbose", action="store_true",
                    help="increase verbosity")
# parser.add_argument("-B", "--block-size", help="checksum blocksize")
# parser.add_argument("--ignore-existing", action="store_true", help="skip files that exist")
# parser.add_argument("--exclude", help="files to exclude")
parser.add_argument("-u", "--url", action="store",
                    default="https://gerrit.home.arpa", help="checksum blocksize")
parser.add_argument("action", help="Action")
# parser.add_argument("dest", help="Destination location")
args = parser.parse_args()
config = vars(args)

if config["verbose"]:
    print(config)


def print_response_request(response):
    def print_response(response):
        print(response.__dict__.keys())
        print(response.content)
        print(response.headers)
        print(response.cookies)
        print(response.status_code)

    def print_request(request):
        print(request.__dict__.keys())
        print(request.headers)
        print(request.body)
        print(request._cookies)

    print_response(response)
    print_request(response.request)


session = requests.Session()


def login():
    def login_url():
        login_url = "/login/%2F"
        return config["url"] + login_url

    session.get(login_url(), verify=config["disable_tls"])
    response = session.post(
        login_url(),
        verify=config["disable_tls"],
        data=dict(username='admin', password='secret')
    )

    if config["verbose"]:
        print_response_request(response)
