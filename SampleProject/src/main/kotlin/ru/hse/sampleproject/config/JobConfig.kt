package ru.hse.sampleproject.config

import config.FlyJobConfigurator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import scheduler.SchedulerService

@Configuration
class JobConfig {

    @Bean
    fun flyJobsConfigurator() : FlyJobConfigurator {
        return FlyJobConfigurator(
            "jdbc:postgresql://localhost:5432/lib_db",
            "user", "password",
            listOf(-1002065857636))
    }

    @Bean
    fun jobScheduler(flyJobsConfigurator: FlyJobConfigurator) : SchedulerService {
        return flyJobsConfigurator.getScheduler()
    }
}