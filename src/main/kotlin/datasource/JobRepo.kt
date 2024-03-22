package datasource

import datasource.model.Job
import datasource.model.JobDto
import datasource.model.JobLaunch
import java.sql.Timestamp

class JobRepo {

    fun addJob(job: JobDto) : Long {
        TODO("save job data")
    }

    fun getNewJobs(timestamp: Timestamp) : List<Job> {
        TODO("get jobs that will start in 1 minute or less")
    }

    fun saveJobLaunch(jobLaunch: JobLaunch) : Long {
        TODO("")
    }
}