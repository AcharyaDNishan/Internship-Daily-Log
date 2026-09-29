class JsonObject(private val properties: Map<String, Any?>) {
    override fun toString(): String = properties.entries.joinToString(
        separator = ", ",
        prefix = "{",
        postfix = "}"
    ) { (name, value) -> "${encodeString(name)}: ${encodeValue(value)}" }

    private fun encodeValue(value: Any?): String = when (value) {
        is String -> encodeString(value)
        is Number, is Boolean -> value.toString()
        null -> "null"
        else -> throw IllegalArgumentException("Unsupported JSON value: ${value::class.simpleName}")
    }

    private fun encodeString(value: String): String = "\"" + value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t") + "\""
}

class JsonBuilder {
    private val properties = linkedMapOf<String, Any?>()

    fun property(name: String, value: Any?) {
        properties[name] = value
    }

    fun build(): JsonObject = JsonObject(properties.toMap())
}

fun json(init: JsonBuilder.() -> Unit): JsonObject {
    val builder = JsonBuilder()
    builder.init()
    return builder.build()
}

fun main() {
    val result = json {
        property("name", "Ram")
        property("age", 20)
    }
    println(result)
}
