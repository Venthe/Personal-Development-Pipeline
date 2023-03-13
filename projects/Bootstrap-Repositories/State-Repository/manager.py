import argparse

import yaml

from library.application import update_helm_property, update_helm_chart_version
from library.deploy import deploy, deploy_all
from library.git import push
from library.templating import render_merged_value_files, render_release_value_files, template_application
from library.utilities import yaml_dump

parser = argparse.ArgumentParser(
    prog='main.py',
    description='What the program does',
    epilog='Text at the bottom of help')
parser.add_argument("--verbose", "-v", action='store_true')
parser.add_argument("--dry-run", action='store_true')

subparsers = parser.add_subparsers(help="Subparsers help", dest="command")


def add_namespace(parser):
    parser.add_argument("namespace", help='namespace help')


def add_application_name(parser):
    parser.add_argument("application_name", help='application_name help')


# render_merged_value_files
parser_render_merged_value_files = subparsers.add_parser("render_merged_value_files",
                                                         help='render_merged_value_files help')
parser_render_merged_value_files.add_argument("namespace", help='namespace help')
add_namespace(parser_render_merged_value_files)

# render_release_value_files
parser_render_release_value_files = subparsers.add_parser("render_release_value_files",
                                                          help='render_release_value_files help')
add_namespace(parser_render_release_value_files)

# template_application
parser_template_application = subparsers.add_parser("template_application", help='template_application help')
add_namespace(parser_template_application)

# deploy
parser_deploy = subparsers.add_parser("deploy", help='deploy help')
add_namespace(parser_deploy)

# deploy_all
parser_deploy_all = subparsers.add_parser("deploy_all", help='deploy_all help')

# push
parser_push = subparsers.add_parser("push", help='push help')
parser_push.add_argument("message", help='message help')
parser_push.add_argument("branch", help='branch help')

# update_helm_property
parser_update_helm_property = subparsers.add_parser("update_helm_property", help='update_helm_property help')
add_namespace(parser_update_helm_property)
add_application_name(parser_update_helm_property)
parser_update_helm_property.add_argument("property_key", help='property_key help')
parser_update_helm_property.add_argument("property_value", help='property_value help')

# update_helm_chart_version
parser_update_helm_chart_version = subparsers.add_parser("update_helm_chart_version",
                                                         help='update_helm_chart_version help')
add_namespace(parser_update_helm_chart_version)
add_application_name(parser_update_helm_chart_version)
parser_update_helm_chart_version.add_argument("chart_version", help='chart_version help')

args = parser.parse_args()

if args.command == "render_merged_value_files":
    print(yaml_dump(render_merged_value_files(args.namespace)))
if args.command == "render_release_value_files":
    print(yaml_dump(render_release_value_files(args.namespace)))
if args.command == "template_application":
    print(yaml.dump_all(template_application(args.namespace)))
if args.command == "deploy":
    print(deploy(args.namespace))
if args.command == "deploy_all":
    deploy_all()
if args.command == "push":
    push(args.message, args.branch)
if args.command == "update_helm_property":
    update_helm_property(args.namespace, args.application_name, args.property_key, args.property_value)
if args.command == "update_helm_chart_version":
    update_helm_chart_version(args.namespace, args.application_name, args.chart_version)

# ./main.py updateHelmChartVersion integration argocd-example 1.0.1
# ./main.py updateHelmProperty integration argocd-example imageTag 20230205-190544-a467a10
