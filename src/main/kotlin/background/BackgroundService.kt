package background

import datasource.JobsRepository
import datasource.model.Job
import datasource.model.JobLambdaDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.sql.Timestamp

class BackgroundService(
    val jobRepo: JobsRepository
) {
    private fun checkNewJobs() {
        val currentTime = Timestamp(System.currentTimeMillis())

        val jobs = jobRepo.getNewJobs(currentTime)
        val ids = jobs.map(JobLambdaDto::id)
        val u = ::startJobs
        startJobs(jobs)
        jobRepo.changeJobState(ids)
    }

    fun startChecking() = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            try {
                checkNewJobs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(60_000) // Задержка на 1 минуту
        }
    }

    private fun startJobs(jobs : List<JobLambdaDto>) = CoroutineScope(Dispatchers.IO).launch {
        jobs.forEach {
            JobExecutor(it, jobRepo).execute()
        }
    }
}
