package background

import datasource.JobRepo
import datasource.model.Job
import datasource.model.JobLaunch
import datasource.model.JobStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class JobExecutor(
    val job: Job,
    val jobRepo: JobRepo
) {
    private fun start(currantJobLaunch: JobLaunch) = CoroutineScope(Dispatchers.IO).launch {
        try {
            job.runner.run()

            currantJobLaunch.jobStatus = JobStatus.FINISHED
        } catch (e: Exception) {
            currantJobLaunch.jobStatus = JobStatus.FAILED
        }
        jobRepo.saveJobLaunch(currantJobLaunch)
    }

    fun execute() {
        var jobLaunch = JobLaunch(job.id, jobStatus=JobStatus.STARTED)
        jobRepo.saveJobLaunch(jobLaunch)
        start(jobLaunch)
    }
}