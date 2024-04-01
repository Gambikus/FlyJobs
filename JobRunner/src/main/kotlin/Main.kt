import background.BackgroundService
import datasource.JobMapper
import datasource.JobsRepository
import kotlinx.coroutines.delay
import scheduler.SchedulerService
import java.sql.Timestamp

fun main() {
    val jobMapper = JobMapper()
    val repository = JobsRepository("jdbc:postgresql://localhost:5432/lib_db", "user", "password", jobMapper)
    val schedulerService = SchedulerService(jobMapper, repository)
    val backgroundService = BackgroundService(repository)
    schedulerService.scheduleJob(
        "*/2 * * * *",
        Second::print
    )

    backgroundService.startChecking()

    // val jobs = repository.getAllJobs()
    // println(jobs)
    while (true) {

    }
}



class Second {
    public fun print() {
        println("hello")
    }
}