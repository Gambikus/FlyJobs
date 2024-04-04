package background

import alert.AlertService
import datasource.JobsRepository
import datasource.model.JobLambdaDto
import datasource.model.JobLaunch
import datasource.model.JobStatus
import kotlinx.coroutines.*
import java.sql.Timestamp

class JobExecutor(
    val job: JobLambdaDto,
    val jobRepo: JobsRepository,
    val alertService: AlertService
) {
    private fun start(currentJobLaunch: JobLaunch): Deferred<Boolean> {
        val result = CompletableDeferred<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                job.job.invoke()
                currentJobLaunch.jobStatus = JobStatus.FINISHED
                result.complete(true) // Возвращаем true, если задача успешно завершена
            } catch (e: Exception) {
                currentJobLaunch.jobStatus = JobStatus.FAILED
                result.complete(false) // Возвращаем false, если задача завершилась с ошибкой
            }
            currentJobLaunch.timestamp = Timestamp(System.currentTimeMillis())
            jobRepo.saveJobLaunch(currentJobLaunch)
            alertService.sentAlert(currentJobLaunch)
        }

        return result
    }

    suspend fun execute() : Boolean {
        val jobLaunch = JobLaunch(job.id, jobStatus=JobStatus.STARTED, timestamp = Timestamp(System.currentTimeMillis()))
        jobRepo.saveJobLaunch(jobLaunch)
        alertService.sentAlert(jobLaunch)
        return start(jobLaunch).await()
    }
}