package scheduler

import datasource.JobMapper
import datasource.JobsRepository
import java.sql.Timestamp
import java.util.*
import kotlin.reflect.KFunction
class SchedulerService(
    private val jobMapper: JobMapper,
    private val jobRepository: JobsRepository) {

    fun scheduleJob(
    executeAt: Timestamp,
    kFunction: KFunction<Unit>
    ) {

        jobRepository.insertJob(
            jobMapper.kvalueToDto(kFunction, executeAt)
        )
    }

    fun scheduleJob(
        cronExpression: String,
        kFunction: KFunction<Unit>
    ) {

        jobRepository.insertJob(
            jobMapper.kvalueToDto(kFunction, executeAt = null, cronExpression = cronExpression)
        )
    }
}