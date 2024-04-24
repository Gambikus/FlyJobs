package ru.hse.sampleproject.controller

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.hse.sampleproject.job.CronJob
import ru.hse.sampleproject.job.MomentalJob
import ru.hse.sampleproject.job.MomentalWithRetriesJob
import scheduler.SchedulerService
import java.sql.Timestamp
import java.time.LocalDateTime

@RestController
@RequestMapping("/jobs")
class JobController(
    private var jobScheduler : SchedulerService
) {


    @PostMapping("/momental-job")
    fun scheduleMomentalJob(
        @RequestParam("timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dateTime: LocalDateTime
    ) {
        jobScheduler.scheduleJob(
            Timestamp.valueOf(dateTime),
            MomentalJob::sendMessage
        )
    }

    // Дополнительный метод для демонстрации принятия параметров в URL
    @PostMapping("/momental-job-with-retries")
    fun scheduleMomentalJobWithRetries(
        @RequestParam("timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) dateTime: LocalDateTime
    ) {
        jobScheduler.scheduleJob(
            Timestamp.valueOf(dateTime),
            MomentalWithRetriesJob::sendMessage
        )
    }

    @PostMapping("cron-job")
    fun scheduleCronJob(@RequestParam("cron") cron: String) {
        jobScheduler.scheduleJob(
            cron,
            CronJob::sendMessage
        )
    }
}