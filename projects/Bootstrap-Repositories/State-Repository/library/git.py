from library.system import git
from library.utilities import eprint


def push(commit, branch):
    eprint(git(["add", "--all"]))
    eprint(git(["commit", "-m", commit]))
    eprint(git(["push", "--set-upstream", "origin", branch]))
