package me.arasple.mc.trchat.util

import taboolib.common.util.asList
import taboolib.library.configuration.Converter

class ArrayConverter : Converter<Array<String>, Any> {
    override fun convertToField(value: Any): Array<String> {
        return value.asList().toTypedArray()
    }

    override fun convertFromField(value: Array<String>): Any {
        return if (value.size == 1) value[0] else value.toList()
    }
}