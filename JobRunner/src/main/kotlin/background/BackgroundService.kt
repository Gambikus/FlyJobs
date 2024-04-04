package background

import alert.AlertService
import datasource.JobsRepository
import datasource.model.JobLambdaDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.sql.Timestamp

class BackgroundService(
    val jobRepo: JobsRepository,
    val alertService: AlertService
) {
    private fun checkJobsToExecute() {
        val currentTime = Timestamp(System.currentTimeMillis())

        val jobs = jobRepo.getNewJobs(currentTime)

        if (jobs.isEmpty()) {
            return
        }

        val u = ::startJobs
        startJobs(jobs)
    }

    fun startChecking() = CoroutineScope(Dispatchers.IO).launch {
        while (isActive) {
            try {
                checkJobsToExecute()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(60_000) // Задержка на 1 минуту
        }
    }

    private fun startJobs(jobs : List<JobLambdaDto>) {
        jobs.forEach {
            CoroutineScope(Dispatchers.IO).launch {
                var retries = 3
                val executor = JobExecutor(it, jobRepo, alertService)

                val ids = jobs.map(JobLambdaDto::id)
                while (retries > 0 && !executor.execute()){
                    retries -= 1
                }

                jobRepo.changeJobStatus(ids)

            }
        }
    }
}
