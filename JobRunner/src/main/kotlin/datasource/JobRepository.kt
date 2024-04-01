package datasource

import datasource.model.Job
import datasource.model.JobLambdaDto
import datasource.model.JobLaunch
import java.util.ArrayList
import java.util.UUID
import com.cronutils.model.Cron
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import java.sql.*
import java.time.ZonedDateTime

fun isJobToExecute(resultSet: ResultSet): Boolean {

    val cronExpression =resultSet.getNullableString("cron_expression")

    if (cronExpression != null && isTimeForCronJob(cronExpression)) {
        return true
    }
    else if (cronExpression == null ){
        return true
    }

    return false

}
fun isTimeForCronJob(cronExpression: String): Boolean {
    val cronParser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX))
    val cron: Cron = cronParser.parse(cronExpression)
    val executionTime = ExecutionTime.forCron(cron)

    // Получение текущего времени
    val now = ZonedDateTime.now()

    // Расчет только времени следующего выполнения задачи
    val nextExecution = executionTime.nextExecution(now.minusSeconds(60))

    println(now)
    println(nextExecution)

    // Проверка, пришло ли время выполнения задачи
    // Если следующий запуск задачи запланирован на время, "меньшее или равное" текущему, задача должна быть запущена
    return nextExecution.isPresent && !now.isBefore(nextExecution.get())
}

fun ResultSet.getNullableString(columnLabel: String): String? =
    this.getString(columnLabel)?.let { if (this.wasNull()) null else it }
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
                execute_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL,
                cron_expression VARCHAR(255) DEFAULT NULL,
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
                INSERT INTO jobs (id, execute_at, cron_expression, class_name, method_name, status)
                VALUES (
                    CAST(? AS uuid),
                    CAST(? AS timestamp), 
                    ?,
                    ?, 
                    ?,
                    ?) RETURNING id;
            """.trimIndent()
            val statement = conn.prepareStatement(sql)
            statement.setString(1, job.id.toString())
            statement.setString(3, job.cronExpression)
            statement.setString(4, job.className)
            statement.setString(5, job.methodName)
            statement.setString(6, "new")

            if (job.executeAt != null) {
                statement.setString(2, job.executeAt.toString()) // executeAt это String или LocalDateTime
            } else {
                statement.setNull(2, Types.TIMESTAMP)
            }

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
            WHERE (execute_at <  ?::timestamp + INTERVAL '1' MINUTE and status != 'OLD') OR cron_expression IS NOT NULL;
        """.trimIndent()
            val statement = conn.prepareStatement(sql)
            statement.setString(1, timestamp.toString())

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                if (isJobToExecute(resultSet)) {
                    res.add(
                        jobMapper.dtoToLambdaJob(
                            resultSet
                        )
                    )
                }
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
            if (ids.isNotEmpty()) {
                val stringIds = ids.joinToString(",") { "?::uuid" }
                println(stringIds)
                val sql = """
                    UPDATE jobs
                    SET status = 'OLD'
                    WHERE id IN ($stringIds);
                """.trimIndent()
                val statement = conn.prepareStatement(sql)
                for (i in ids.indices) {
                    statement.setObject(i + 1, ids[i])
                }

                statement.execute() // Это правильный способ выполнить запрос без получения ResultSet
            } else {
                println("Список ids пуст.")
            }
        }
    }

}