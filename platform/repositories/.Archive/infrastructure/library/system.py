import subprocess


def yq(operations):
    return subprocess.check_output(["yq"] + operations, text=True)


def helm(operations):
    return subprocess.check_output(["helm"] + operations, text=True)


def git(operations):
    return subprocess.check_output(["git"] + operations, text=True)
