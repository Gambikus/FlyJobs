package ru.hse.sampleproject.job

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.entities.ChatId
import java.sql.Timestamp

class MomentalJob {
    private val bot = bot {
        token = Companion.token
    }


    companion object {
        private const val token = "6635945511:AAEqhO9Zsw-zCiziWAiyrg7WbUfoy09-eBQ"

        private const val demoChatId = -4109698531L
    }
    fun sendMessage() {
        bot.sendMessage(
            chatId = ChatId.fromId(demoChatId),
            text = "Momental Job, время - ${Timestamp(System.currentTimeMillis())}"
        )
    }
}