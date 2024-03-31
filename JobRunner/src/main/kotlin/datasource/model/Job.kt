package datasource.model

import java.sql.Timestamp
import java.util.UUID

data class Job(
    val id: UUID,
    val executeAt: Timestamp,
    val className: String,
    val methodName: String
)