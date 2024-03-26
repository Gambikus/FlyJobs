package scheduler

import datasource.JobsRepository
import java.sql.Timestamp
import java.util.*
import kotlin.reflect.KFunction
class SchedulerService(private val jobRepository: JobsRepository) {

    fun scheduleJob(
    executeAt: Timestamp,
    action: () -> Unit
    ) {
        //val method = action.reflect()?.javaMethod
        //val className = method?.declaringClass?.kotlin?.qualifiedName ?: "UnknownClass"
        //val methodName = method?.name ?: "UnknownMethod"

        jobRepository.insertJob(scheduleType = "ONE_TIME",
            scheduleExpression = executeAt.toString(),
            state = "SCHEDULED",
            className = "className",
            methodName = "methodName")

        // TODO: запланировать
    }
}