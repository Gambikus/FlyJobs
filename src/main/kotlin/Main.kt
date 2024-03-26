import datasource.JobsRepository
import datasource.dto.Job
import java.util.*

fun main() {
    val repository = JobsRepository("jdbc:postgresql://localhost:5432/lib_db", "user", "password")

    val jobId = repository.insertJob(scheduleType = "CRON",
        scheduleExpression = "0 0 * * * ?",
        state = "SCHEDULED",
        className = "SomeClass",
        methodName = "someMethod")

    val jobs = repository.getAllJobs()
    println(jobs)
}