package net.ccbluex.liquidbounce.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.jvm.internal.Intrinsics

private val <T> Array<T>.length: Int
    get() {
        return length
    }
/**
 * List value represents a selectable list of values
 */
open class ListValue(name: String, val values: Array<String>, value: String) : Value<String>(name, value) {
    @JvmField
    var openList = false

    init {
        this.value = value
    }

    fun containsValue(string: String): Boolean {
        return Arrays.stream(values).anyMatch { it.equals(string, ignoreCase = true) }
    }

    override fun changeValue(value: String) {
        for (element in values) {
            if (element.equals(value, ignoreCase = true)) {
                this.value = element
                break
            }
        }
    }

    fun getModeListNumber(@NotNull modeName: String?): Int {
        Intrinsics.checkParameterIsNotNull(modeName, "modeName")
        var b: Byte
        val i: Int
        b = 0
        i = values.length
        while (b < i) {
            if (Intrinsics.areEqual(values[b.toInt()], modeName)) return b.toInt()
            b++
        }
        return 0
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) changeValue(element.asString)
    }
}