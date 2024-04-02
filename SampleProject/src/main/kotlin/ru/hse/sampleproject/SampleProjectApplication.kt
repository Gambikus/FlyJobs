package ru.hse.sampleproject

import config.FlyJobConfigurator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SampleProjectApplication

fun main(args: Array<String>) {

    val config = FlyJobConfigurator("jdbc:postgresql://localhost:5432/lib_db", "user", "password")
    config.getScheduler().scheduleJob(
        "*/2 * * * *",
        Second::print
    )
    runApplication<SampleProjectApplication>(*args)
}


class Second {
    public fun print() {
        println("hello")
    }
}