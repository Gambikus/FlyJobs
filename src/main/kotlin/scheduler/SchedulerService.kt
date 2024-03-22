package scheduler

import datasource.JobRepo
import java.beans.Expression

class SchedulerService(
    val jobRepo: JobRepo
) {
    fun schedule(cronExpression: Expression, job: () -> Unit) {
        TODO("обернуть в дто и отправить в сервис, еще проверить передачу джобы в качестве лямбды")
    }
}