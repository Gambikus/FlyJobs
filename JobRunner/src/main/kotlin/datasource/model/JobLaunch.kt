package datasource.model

import java.sql.Timestamp
import java.util.UUID

data class JobLaunch(
    val jobId: UUID,
    var jobStatus: JobStatus,
    var timestamp: Timestamp
)
