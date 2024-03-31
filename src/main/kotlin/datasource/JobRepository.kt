package datasource

import datasource.model.Job
import datasource.model.JobLambdaDto
import datasource.model.JobLaunch
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp
import java.util.ArrayList
import java.util.UUID

class JobsRepository(
    private val dbUrl: String,
    private val dbUser: String,
    private val dbPassword: String,
    private val jobMapper: JobMapper) {
    fun connect(): Connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)

    fun insertJob(job: Job): UUID? {
        connect().use { conn ->
            val sql = """
                INSERT INTO jobs (id, execute_at, class_name, method_name)
                VALUES (?, ?, ?, ?) RETURNING id;
            """.trimIndent()
            val statement = conn.prepareStatement(sql)
            statement.setString(1, job.id.toString())
            statement.setString(2, job.executeAt.toString())
            statement.setString(3, job.className)
            statement.setString(4, job.methodName)

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString(1))
            }
        }
        return null  // Возвращаем 0, если вставка не удалась
    }

    fun getNewJobs(timestamp: Timestamp): List<JobLambdaDto> {
        val res = ArrayList<JobLambdaDto>()

        connect().use { conn ->
            val sql = """
                SELECT *
                FROM jobs
                WHERE (execute_at > ? - INTERVAL '1' MINUTE
                  or execute_at <= ?) and status != 'OLD';
            """.trimIndent()
            val statement = conn.prepareStatement(sql)
            statement.setString(1, timestamp.toString())

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                res.add(
                    jobMapper.dtoToLambdaJob(
                        resultSet
                    )
                )
            }
        }

        return res
    }

    fun saveJobLaunch(jobLaunch: JobLaunch) : Int {
        connect().use { conn ->
            val sql = """
                INSERT INTO job_launch (job_id, job_status, timestamp, status)
                VALUES (?, ?, ?) RETURNING id;
            """.trimIndent()
            val statement = conn.prepareStatement(sql)
            statement.setString(1, jobLaunch.jobId.toString())
            statement.setString(2, jobLaunch.timestamp.toString())
            statement.setString(3, jobLaunch.jobStatus.toString())
            statement.setString(4, "new")

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                return resultSet.getInt(1)
            }
        }
        return 0
    }

    fun changeJobState(ids: List<UUID>) {
        connect().use { conn ->
            val stringIds = ids.joinToString(",")
            val sql = """
                UPDATE jobs
                SET status = 'OLD'
                WHERE id IN ($stringIds);
            """.trimIndent()
            val statement = conn.prepareStatement(sql)

            val resultSet = statement.executeQuery()
        }
    }


    fun getAllJobs(): List<Map<String, Any>> {
        connect().use { conn ->
            val jobsList = mutableListOf<Map<String, Any>>()
            val sql = "SELECT * FROM jobs;"
            val statement = conn.createStatement()
            val resultSet = statement.executeQuery(sql)

            while (resultSet.next()) {
                val job = mapOf(
                    "id" to resultSet.getInt("id"),
                    "executeAt" to resultSet.getTimestamp("execute_at"),
                    "class_name" to resultSet.getString("class_name"),
                    "method_name" to resultSet.getString("method_name"),
                )
                jobsList.add(job)
            }
            return jobsList
        }
    }
}