package background

import datasource.JobsRepository
import datasource.model.Job
import datasource.model.JobLambdaDto
import datasource.model.JobLaunch
import datasource.model.JobStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp

class JobExecutor(
    val job: JobLambdaDto,
    val jobRepo: JobsRepository
) {
    private fun start(currantJobLaunch: JobLaunch) = CoroutineScope(Dispatchers.IO).launch {
        try {
            job.job.invoke()

            currantJobLaunch.jobStatus = JobStatus.FINISHED
        } catch (e: Exception) {
            currantJobLaunch.jobStatus = JobStatus.FAILED
        }
        currantJobLaunch.timestamp=Timestamp(System.currentTimeMillis())
        jobRepo.saveJobLaunch(currantJobLaunch)
    }

    fun execute() {
        var jobLaunch = JobLaunch(job.id, jobStatus=JobStatus.STARTED, timestamp = Timestamp(System.currentTimeMillis()))
        jobRepo.saveJobLaunch(jobLaunch)
        start(jobLaunch)
    }
}