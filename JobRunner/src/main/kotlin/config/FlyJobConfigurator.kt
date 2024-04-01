package config

import background.BackgroundService
import datasource.JobMapper
import datasource.JobsRepository
import scheduler.SchedulerService

class FlyJobConfigurator(
    dbUrl: String,
    dbUser: String,
    dbPassword: String,
) {
    private val jobMapper: JobMapper = JobMapper()
    private val jobsRepository: JobsRepository = JobsRepository(dbUrl, dbUser, dbPassword, jobMapper)
    private val schedulerService: SchedulerService = SchedulerService(jobMapper, jobsRepository)
    private val backgroundService : BackgroundService = BackgroundService(jobsRepository)

    init {
        backgroundService.startChecking()
    }

    fun getScheduler() : SchedulerService = schedulerService
}