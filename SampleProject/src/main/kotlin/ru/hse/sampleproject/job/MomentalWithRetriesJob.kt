package ru.hse.sampleproject.job

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.entities.ChatId
import java.sql.Timestamp

class MomentalWithRetriesJob {
    private val bot = bot {
        token = Companion.token
    }


    companion object {
        private var numb = 0
        private const val token = "6635945511:AAEqhO9Zsw-zCiziWAiyrg7WbUfoy09-eBQ"

        private const val demoChatId = -4109698531L
    }
    fun sendMessage() {
        Thread.sleep(1000)

        if (numb == 0) {
            numb = 1
            throw IllegalArgumentException()
        }
        numb = 0

        bot.sendMessage(
            chatId = ChatId.fromId(demoChatId),
            text = "Momental Job with retries, время - ${Timestamp(System.currentTimeMillis())}"
        )
    }
}