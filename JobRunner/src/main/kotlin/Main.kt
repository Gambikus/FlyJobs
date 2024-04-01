import background.BackgroundService
import config.FlyJobConfigurator
import datasource.JobMapper
import datasource.JobsRepository
import kotlinx.coroutines.delay
import scheduler.SchedulerService
import java.sql.Timestamp

fun main() {
    val config = FlyJobConfigurator("jdbc:postgresql://localhost:5432/lib_db", "user", "password")
    config.getScheduler().scheduleJob(
        "*/2 * * * *",
        Second::print
    )

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