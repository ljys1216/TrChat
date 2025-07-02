package me.arasple.mc.trchat.module.conf

/**
 * @author Arasple
 * @date 2021/2/11 17:26
 */
enum class Property(val regex: Regex, val group: Int) {

    CONDITION("\\{condition[:=] ?(.+)}"),

    EXACT("\\{exact[:=] ?(.+)}"),

    RELOCATE("\\{relocate[:=] ?(.+)}"),

    COOLDOWN("\\{cooldown[:=] ?(.+)}");

    constructor(regex: String, group: Int = 1) : this(regex.toRegex(RegexOption.IGNORE_CASE), group)

    companion object {

        fun from(string: String): Pair<String, Map<Property, String>> {
            var content = string
            val map = values().mapNotNull {
                val value = it.regex.find(content)?.groupValues?.get(it.group)
                value?.let { v ->
                    content = content.replace(it.regex, "")
                    it to v
                }
            }.toMap()

            return content to map
        }

        fun serialize(contents: Any): List<Pair<String, Map<Property, String>>> {
            return when (contents) {
                is String -> listOf(from(contents))
                is List<*> -> contents.map { from(it!!.toString()) }
                else -> error("Unexpected part: $contents")
            }
        }

    }

}