def call() {
    def jobName = currentBuild.fullProjectName
    def job = Jenkins.instance.getItem(jobName)
    job.getBuilds().remove(job.getBuilds().size() - 1);
    job.save()
}
