package background

import datasource.JobRepo
import datasource.model.Job
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.time.LocalDateTime

class BackgroundService(
    val jobRepo: JobRepo
) {
    private fun checkNewJobs() {
        val currentTime = Timestamp.valueOf(LocalDateTime.now().plusMinutes(1))

        val jobs = jobRepo.getNewJobs(currentTime)

        startJobs(jobs)
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

    private fun startJobs(jobs : List<Job>) = CoroutineScope(Dispatchers.IO).launch {
        jobs.forEach {
            JobExecutor(it, jobRepo).execute()
        }
    }
}
