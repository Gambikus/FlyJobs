package datasource.model

data class JobLaunch(
    val jobId: Long,
    var jobStatus: JobStatus,
)
