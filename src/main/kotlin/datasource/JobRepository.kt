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

    init {
        val init_job = """
            CREATE TABLE IF NOT EXISTS jobs (
                id uuid PRIMARY KEY,
                execute_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                class_name VARCHAR(255) NOT NULL,
                method_name VARCHAR(255) NOT NULL,
                status VARCHAR(20)
            );
        """.trimIndent()

        val init_job_launch = """
            CREATE TABLE IF NOT EXISTS job_launch (
                id SERIAL PRIMARY KEY,
                job_id uuid,
                timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                job_status TEXT NOT NULL
            );
        """.trimIndent()

        connect().use { conn ->
            conn.prepareStatement(init_job).execute()
            conn.prepareStatement(init_job_launch).execute()
        }
    }
    fun insertJob(job: Job): UUID? {
        connect().use { conn ->
            val sql = """
                INSERT INTO jobs (id, execute_at, class_name, method_name, status)
                VALUES (
                    CAST(? AS uuid),
                    CAST(? AS timestamp), 
                    ?, 
                    ?,
                    ?) RETURNING id;
            """.trimIndent()
            val statement = conn.prepareStatement(sql)
            statement.setString(1, job.id.toString())
            statement.setString(2, job.executeAt.toString())
            statement.setString(3, job.className)
            statement.setString(4, job.methodName)
            statement.setString(5, "new")

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
                WHERE (execute_at >  ?::timestamp - INTERVAL '1' MINUTE
                  or execute_at <=  ?::timestamp) and status != 'OLD';
            """.trimIndent()
            val statement = conn.prepareStatement(sql)
            statement.setString(1, timestamp.toString())
            statement.setString(2, timestamp.toString())

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
                INSERT INTO job_launch (job_id, job_status, timestamp)
                VALUES (
                    CAST(? AS uuid),
                    ?, 
                    CAST(? AS timestamp)
                ) RETURNING id;
            """.trimIndent()
            val statement = conn.prepareStatement(sql)
            statement.setString(1, jobLaunch.jobId.toString())
            statement.setString(2, jobLaunch.jobStatus.toString())
            statement.setString(3, jobLaunch.timestamp.toString())

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                return resultSet.getInt(1)
            }
        }
        return 0
    }

    fun changeJobState(ids: List<UUID>) {
        connect().use { conn ->
            val stringIds = ids.joinToString(",") { "?"}
            println(stringIds)
            val sql = """
                UPDATE jobs
                SET status = 'OLD'
                WHERE id IN ($stringIds);
            """.trimIndent()
            val statement = conn.prepareStatement(sql)
            for (i in 1..ids.size) {
                statement.setObject(i, ids[i - 1])
            }

            val resultSet = statement.execute()
        }
    }

}