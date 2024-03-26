package datasource.dto

import java.sql.Timestamp
import java.util.*

data class Job(
    val id: Int,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val scheduleType: String,
    val scheduleExpression: String,
    val state: String,
    val className: String,
    val methodName: String,
    val retries: Int
)