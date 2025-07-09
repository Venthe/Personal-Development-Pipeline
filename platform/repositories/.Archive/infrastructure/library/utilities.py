import sys

import yaml
from yaml import Dumper


def write_string_to_file(value, file):
    file.write(str.encode(value))
    file.flush()


def yaml_load(render):
    return yaml.load(
        render,
        Loader=yaml.Loader
    )


def yaml_dump(current):
    return yaml.dump(current, Dumper=Dumper)


def eprint(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)
