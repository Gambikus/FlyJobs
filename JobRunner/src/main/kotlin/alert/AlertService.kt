package alert

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.entities.ChatId
import datasource.model.JobLaunch

class AlertService(
    private val chatIds: List<Long>
) {

    private val bot = bot {
        token = Companion.token
    }


    companion object {
        private const val token = "7125609006:AAHGF4V3spooozo6TYC0nisQmqiaznpayvE"
    }
    fun sentAlert(jobLaunch: JobLaunch) {
        chatIds.forEach {
            bot.sendMessage(
                chatId = ChatId.fromId(it),
                text = jobLaunch.toString()
            )
        }
    }
}