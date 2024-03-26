package datasource

import datasource.dto.Job
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp

class JobsRepository(private val dbUrl: String, private val dbUser: String, private val dbPassword: String) {
    fun connect(): Connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)

    fun insertJob(scheduleType: String, scheduleExpression: String, state: String, className: String, methodName: String): Int {
        connect().use { conn ->
            val sql = """
                INSERT INTO jobs (created_at, updated_at, schedule_type, schedule_expression, state, class_name, method_name, retries)
                VALUES (?, ?, CAST(? AS schedule_type), ?, CAST(? AS job_state), ?, ?, DEFAULT) RETURNING id;
            """.trimIndent()
            val statement = conn.prepareStatement(sql)
            statement.setTimestamp(1, Timestamp(System.currentTimeMillis()))
            statement.setTimestamp(2, Timestamp(System.currentTimeMillis()))
            statement.setString(3, scheduleType)
            statement.setString(4, scheduleExpression)
            statement.setString(5, state)
            statement.setString(6, className)
            statement.setString(7, methodName)

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                return resultSet.getInt(1)
            }
        }
        return 0  // Возвращаем 0, если вставка не удалась
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
                    "created_at" to resultSet.getTimestamp("created_at"),
                    "updated_at" to resultSet.getTimestamp("updated_at"),
                    "schedule_type" to resultSet.getString("schedule_type"),
                    "schedule_expression" to resultSet.getString("schedule_expression"),
                    "state" to resultSet.getString("state"),
                    "class_name" to resultSet.getString("class_name"),
                    "method_name" to resultSet.getString("method_name"),
                    "retries" to resultSet.getInt("retries")
                )
                jobsList.add(job)
            }
            return jobsList
        }
    }
}