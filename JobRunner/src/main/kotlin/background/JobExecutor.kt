package background

import datasource.JobsRepository
import datasource.model.Job
import datasource.model.JobLambdaDto
import datasource.model.JobLaunch
import datasource.model.JobStatus
import kotlinx.coroutines.*
import java.sql.Timestamp

class JobExecutor(
    val job: JobLambdaDto,
    val jobRepo: JobsRepository
) {
    private fun start(currantJobLaunch: JobLaunch): Deferred<Boolean> {
        val result = CompletableDeferred<Boolean>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                job.job.invoke()
                currantJobLaunch.jobStatus = JobStatus.FINISHED
                result.complete(true) // Возвращаем true, если задача успешно завершена
            } catch (e: Exception) {
                currantJobLaunch.jobStatus = JobStatus.FAILED
                result.complete(false) // Возвращаем false, если задача завершилась с ошибкой
            }
            currantJobLaunch.timestamp = Timestamp(System.currentTimeMillis())
            jobRepo.saveJobLaunch(currantJobLaunch)
        }

        return result
    }

    suspend fun execute() : Boolean {
        val jobLaunch = JobLaunch(job.id, jobStatus=JobStatus.STARTED, timestamp = Timestamp(System.currentTimeMillis()))
        jobRepo.saveJobLaunch(jobLaunch)

        return start(jobLaunch).await()
    }
}