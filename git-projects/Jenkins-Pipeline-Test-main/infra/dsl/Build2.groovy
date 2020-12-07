job("${BRANCH}/Build2") {
	description()
	keepDependencies(false)
	parameters {
		stringParam("Revision", BRANCH, "")
	}
	scm {
		git {
			remote {
				github("ssh://admin@192.168.99.100:29418/test", "ssh")
				credentials("5fc4f3ef-9d9c-4b4e-a933-cfe2a0dc4429")
			}
			branch("\${REVISION}")
		}
	}
	disabled(false)
	concurrentBuild(false)
	steps {
		shell("""ls
git checkout master
git pull""")
	}
	wrappers {
		timestamps()
	}
}
