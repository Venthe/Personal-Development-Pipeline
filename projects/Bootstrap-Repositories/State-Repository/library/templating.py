import tempfile
import textwrap

import yaml
from jinja2 import Environment, FileSystemLoader

from library.constants import argocd_namespace
from library.system import yq, helm
from library.utilities import write_string_to_file, yaml_load, yaml_dump, eprint


def __get_application_file__(application_path):
    application_file = f"{application_path}/applications.yml"
    return application_file


def __get_application_path__(namespace):
    application_path = f"./environment/{namespace}"
    return application_path


__global_values_file__ = "./globals.yml"
__values_template_file__ = "values-template.yml"


def __prepare_template_environment__():
    def to_yaml(value):
        return yaml_dump(value)

    def render_key(value, key):
        if type(value) is dict or type(value) is list:
            with tempfile.NamedTemporaryFile() as tmp:
                write_string_to_file(yaml_dump(value),
                                     tmp)
                obj = {}
                obj[key] = yaml_load(yq(['.', tmp.name, '--output-format=yaml', '--indent=2']))
                return yaml_dump(obj)
        return ""

    environment = Environment(loader=FileSystemLoader("./"))
    environment.filters["to_yaml"] = to_yaml
    environment.filters["render_key"] = render_key
    return environment


__template_environment__ = __prepare_template_environment__()


def __render_static_values__(application_file, global_values_file, namespace):
    operations = textwrap.dedent(f"""\
    . *= load("{global_values_file}") |
    .global.namespace="{namespace}" |
    .helm[] |= pick(["chart", "path", "chartRevision", "properties"]) |
    ... comments=""\
    """)
    static_values = yq(["--output-format=yaml", "--indent=2", operations, application_file])
    return static_values


def __render_dynamic_values__(application_file, application_path):
    def load_files(key: str, files: str, accumulator: str, operation: str) -> str:
        return textwrap.dedent(f"""\
        .{key} = (
          .{files} // [] |
            map(load("{application_path}/" + $path + "/" + .)) |
          .[] as $item ireduce ({accumulator}; . {operation} $item)
        ) |
        del(.{files}) |
        del(.{key} | select(. | length == 0))\
        """)

    operations = textwrap.dedent(f"""\
    .helm[] |= (
      pick(["path","valueFiles", "kustomizationFiles","additionalManifestFiles"]) |
      .path as $path |
      {load_files("values", "valueFiles", "{}", "*+")} |
      {load_files("kustomizations", "kustomizationFiles", "{}", "*+")} |
      {load_files("additionalManifests", "additionalManifestFiles", "[]", "+")}
    )\
    """)
    dynamic_values = yq(["--output-format=yaml", "--indent=2", operations, application_file])
    return dynamic_values


def __merge_dynamic_and_static_values__(dynamic_values, static_values):
    def merge_values():
        with tempfile.NamedTemporaryFile() as dynamic_values_file, tempfile.NamedTemporaryFile() as static_values_file:
            write_string_to_file(dynamic_values, dynamic_values_file)
            write_string_to_file(static_values, static_values_file)
            operations = '. as $item ireduce ({}; . * $item )'
            return yq(
                ["eval-all", "--output-format=yaml", "--indent=2", operations, static_values_file.name,
                 dynamic_values_file.name])

    merged_values = yaml_load(merge_values())
    for key in merged_values["helm"]:
        current = merged_values["helm"][key]
        render = __template_environment__.from_string(yaml_dump(current)).render(
            {"app": current})
        merged_values["helm"][key] = yaml_load(render)
    return merged_values


def __minimize_template__(rendered_template):
    with tempfile.NamedTemporaryFile() as tmp:
        write_string_to_file(rendered_template,
                             tmp)
        return yaml_load(yq(["--output-format=yaml", "--indent=2", '.', tmp.name]))


def __render_merged_value_files__(namespace: str) -> str:
    if not namespace:
        print("Namespace lacking")
        exit(1)
    application_path = __get_application_path__(namespace)
    application_file = __get_application_file__(application_path)

    static_values = __render_static_values__(application_file, __global_values_file__, namespace)
    dynamic_values = __render_dynamic_values__(application_file, application_path)
    merged_values = __merge_dynamic_and_static_values__(dynamic_values, static_values)
    return merged_values


def render_merged_value_files(namespace: str) -> str:
    return __render_merged_value_files__(namespace)


def render_release_value_files(namespace: str) -> str:
    merged_values = __render_merged_value_files__(namespace)
    rendered_template = __template_environment__.get_template(__values_template_file__).render(merged_values)
    return __minimize_template__(rendered_template)


def template_application(namespace):
    eprint(f"Templating for {namespace}")
    with tempfile.NamedTemporaryFile() as tmp:
        write_string_to_file(yaml_dump(render_release_value_files(namespace)),
                             tmp)
        templated_application = helm(
            ["template", f"--namespace={argocd_namespace}", "--debug", f"--values={tmp.name}",
             f"argocd-{namespace}", "./deployment"])
        eprint(templated_application)
        return yaml.load_all(
            templated_application
        )
