package net.ccbluex.liquidbounce.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.ccbluex.liquidbounce.utils.render.Translate

/**
 * Integer value represents a value with a integer
 */
open class IntegerValue(name: String, value: Int, val minimum: Int = 0, val maximum: Int = Integer.MAX_VALUE) : Value<Int>(name, value) {

    val translate = Translate(0F, 0F)
    fun set(newValue: Number) {
        set(newValue.toInt())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) {
            value = element.asInt
        }
    }
}