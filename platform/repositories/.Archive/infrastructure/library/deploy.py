import os
import tempfile

from library.constants import argocd_namespace
from library.system import helm
from library.templating import render_release_value_files
from library.utilities import write_string_to_file, eprint, yaml_dump


def deploy(namespace):
    eprint(f"Deploying for {namespace}")
    with tempfile.NamedTemporaryFile() as tmp:
        write_string_to_file(yaml_dump(render_release_value_files(namespace)),
                             tmp)
        return helm(
            ["upgrade", "--install", f"--namespace={argocd_namespace}", f"--values={tmp.name}", f"argocd-{namespace}",
             "./deployment"])


def deploy_all():
    eprint(f"Deploying all")
    directories = os.listdir("./environment/")
    for directory in directories:
        eprint(deploy(directory))
