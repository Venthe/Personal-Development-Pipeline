job("Seed job") {
	description()
	keepDependencies(false)
	scm {
		git {
			remote {
				github("ssh://admin@192.168.99.100:29418/test.git", "ssh")
				credentials("5fc4f3ef-9d9c-4b4e-a933-cfe2a0dc4429")
			}
			branch("**")
		}
	}
	disabled(false)
	concurrentBuild(false)
	steps {
		shell("""git ls-remote --heads origin \\
| sed 's/\\t/ /g' | awk '{print \$2}' \\
| sed 's/refs\\/heads\\///g' \\
| sed 's/\\//-/g' \\
>> branches.list
echo "Branches found on ORIGIN"
cat branches.list
echo """"")
		dsl {
			text("""def branches = readFileFromWorkspace("branches.list").split("\\r?\\n")

branches.each{
  def remoteBranch = it
  
  folder(remoteBranch) {
    displayName(remoteBranch)
    description("Folder for branch \${remoteBranch}")
  }
  
  job("\${remoteBranch}/Seed") {
    description("Seed job for \${remoteBranch}")
    keepDependencies(false)
    
    parameters {
      stringParam("BRANCH", remoteBranch)
    }
    
    scm {
	  git {
	    remote {
          url("ssh://admin@192.168.99.100:29418/test.git")
	      credentials("5fc4f3ef-9d9c-4b4e-a933-cfe2a0dc4429")
	    }
		branch(remoteBranch)
	  }
	}
    
    triggers {
      scm("H/5 * * * *") {
        ignorePostCommitHooks(false)
      }
    }
    
    steps {
      dsl {
        external('infra/dsl/*.groovy') 
        external('infra/dsl/*.Jenkinsfile') 
      }
    }

    wrappers {
      timestamps()
    }
  }
}""")
			ignoreExisting(false)
			removeAction("DELETE")
			removeViewAction("DELETE")
			lookupStrategy("JENKINS_ROOT")
		}
	}
	wrappers {
		preBuildCleanup {
			deleteDirectories(false)
			cleanupParameter()
		}
		timestamps()
	}
	configure {
		it / 'properties' / 'com.sonyericsson.rebuild.RebuildSettings' {
			'autoRebuild'('false')
			'rebuildDisabled'('false')
		}
	}
}