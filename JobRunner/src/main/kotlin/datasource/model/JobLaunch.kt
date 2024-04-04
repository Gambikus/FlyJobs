package datasource.model

import java.sql.Timestamp
import java.time.OffsetDateTime
import java.util.Date
import java.util.UUID

data class JobLaunch(
    val jobId: UUID,
    var jobStatus: JobStatus,
    var timestamp: Timestamp
) {
    override fun toString(): String {
        return "Job with id $jobId has finished with status $jobStatus at ${Date(timestamp.time)}"
    }
}
