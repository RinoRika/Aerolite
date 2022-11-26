package net.ccbluex.liquidbounce.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.ccbluex.liquidbounce.utils.render.Translate

/**
 * Float value represents a value with a float
 */
open class FloatValue(name: String, value: Float, val minimum: Float = 0F, val maximum: Float = Float.MAX_VALUE) : Value<Float>(name, value) {

    val translate = Translate(0F, 0F)
    fun set(newValue: Number) {
        set(newValue.toFloat())
    }

    override fun toJson() = JsonPrimitive(value)


    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) {
            value = element.asFloat
        }
    }
}
/**
 * Float value represents a value with a float
 */
open class FloatValue2(name: String, value: Float, val minimum: Float = 0F, val maximum: Float = Float.MAX_VALUE, val suffix: String, displayable: () -> Boolean)
    : Value<Float>(name, value) {

    constructor(name: String, value: Float, minimum: Float, maximum: Float, displayable: () -> Boolean) : this(
        name,
        value,
        minimum,
        maximum,
        "",
        displayable
    )

    constructor(name: String, value: Float, minimum: Float, maximum: Float, suffix: String) : this(
        name,
        value,
        minimum,
        maximum,
        suffix,
        { true })

    constructor(name: String, value: Float, minimum: Float, maximum: Float) : this(
        name,
        value,
        minimum,
        maximum,
        { true })

    fun set(newValue: Number) {
        set(newValue.toFloat())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asFloat
    }
}

