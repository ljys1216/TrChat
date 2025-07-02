package me.arasple.mc.trchat.util

import com.eatthepath.uuid.FastUUID
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import taboolib.common.platform.function.console
import java.util.*

private val jsonParser = JsonParser()
private val reportedErrors = mutableListOf<String>()
val nilUUID = UUID(0, 0)
val papiRegex = "(%)(.+?)(%)|(:)(.+?)(:)|(?!\\{\")((\\{)(.+?)(}))".toRegex()

fun Throwable.print(title: String, printStackTrace: Boolean = true) {
    console().sendMessage("§c[TrChat] §7$title")
    console().sendMessage("§7${javaClass.name}: $localizedMessage")
    if (printStackTrace){
        stackTrace.forEach { console().sendMessage("§8\tat $it") }
        printCause()
    }
}

private fun Throwable.printCause() {
    val cause = cause
    if (cause != null) {
        console().sendMessage("§7Caused by: ${javaClass.name}: ${cause.localizedMessage}")
        cause.stackTrace.forEach { console().sendMessage("§8\tat $it") }
        cause.printCause()
    }
}

fun Throwable.reportOnce(title: String, printStackTrace: Boolean = true) {
    if (title !in reportedErrors) {
        print(title, printStackTrace)
        reportedErrors += title
    }
}

fun String.parseJson(): JsonElement = jsonParser.parse(this)!!

fun String.toUUID(): UUID = FastUUID.parseUUID(this)

fun UUID.parseString(): String = FastUUID.toString(this)