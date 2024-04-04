package config

import alert.AlertService
import background.BackgroundService
import datasource.JobMapper
import datasource.JobsRepository
import scheduler.SchedulerService

class FlyJobConfigurator(
    dbUrl: String,
    dbUser: String,
    dbPassword: String,
    chatIds: List<Long> = listOf()
) {
    private val jobMapper: JobMapper = JobMapper()
    private val jobsRepository: JobsRepository = JobsRepository(dbUrl, dbUser, dbPassword, jobMapper)
    private val schedulerService: SchedulerService = SchedulerService(jobMapper, jobsRepository)
    private val alertService: AlertService = AlertService(chatIds)
    private val backgroundService : BackgroundService = BackgroundService(jobsRepository, alertService)

    init {
        backgroundService.startChecking()
    }

    fun getScheduler() : SchedulerService = schedulerService
}