import background.BackgroundService
import datasource.JobMapper
import datasource.JobsRepository
import scheduler.SchedulerService
import java.sql.Timestamp

fun main() {
    val jobMapper = JobMapper()
    val repository = JobsRepository("jdbc:postgresql://db:5432/lib_db", "user", "password", jobMapper)
    val schedulerService = SchedulerService(jobMapper, repository)
    val backgroundService = BackgroundService(repository)
    schedulerService.scheduleJob(
        Timestamp(System.currentTimeMillis()),
        Second::print
    )

    backgroundService.startChecking()

    val jobs = repository.getAllJobs()
    println(jobs)




}



class Second {
    public fun print() {
        println("hello")
    }
}