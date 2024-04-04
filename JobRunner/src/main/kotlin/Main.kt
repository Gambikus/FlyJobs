import background.BackgroundService
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.entities.ChatId
import config.FlyJobConfigurator
import datasource.JobMapper
import datasource.JobsRepository
import datasource.model.JobLaunch
import kotlinx.coroutines.delay
import scheduler.SchedulerService
import java.sql.Timestamp

fun main() {
    val config = FlyJobConfigurator("jdbc:postgresql://localhost:5432/lib_db",
        "user", "password", listOf(-1002065857636)
    )
    // config.getScheduler().scheduleJob(
    //     "*/1 * * * *",
    //     Second::print
    // )
    //
    // config.getScheduler().scheduleJob(
    //     "*/1 * * * *",
    //     Second::fail
    // )

    config.getScheduler().scheduleJob(
        Timestamp(System.currentTimeMillis()),
        Second::print
    )

    config.getScheduler().scheduleJob(
        Timestamp(System.currentTimeMillis()),
        Second::fail
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

    public fun fail() {
        throw IllegalArgumentException()
    }
}