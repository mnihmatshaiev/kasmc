import java.math.BigInteger
import javax.script.ScriptEngineManager

abstract class Constant {
    abstract val string: String
    abstract var int: BigInteger
    abstract val size: Int
}

class NumberConstant(name: String, type: String = "") : Constant() {
    private val value = ScriptEngineManager().getEngineByName("JavaScript").eval(
        name.replace(Regex(number)) { "0x${it.value}" }.replace("h", "").trim()
    ).toString().toInt()
    override val string = when {
        type == "word" -> java.lang.Integer.toHexString(value).padStart(4, '0')
        type == "dword" -> java.lang.Integer.toHexString(value).padStart(8, '0')
        type == "byte" -> java.lang.Integer.toHexString(value).padStart(2, '0')
        value <= 255 -> java.lang.Integer.toHexString(value).padStart(2, '0')
        else -> java.lang.Integer.toHexString(value).padStart(8, '0')
    }
    override val size = when {
        value <= 255 -> 1
        else -> 4
    }
    val typeInt = when {
        value == 1 -> 0b00
        value == 2 -> 0b01
        value == 4 -> 0b10
        else -> 0b11
    }
    override var int = value.toBigInteger()
}

class StringConstant(name: String) : Constant() {
    private val value = let {
        val byteImpl = mutableListOf<Int>()
        name.toList().forEach {
            if (it != '"') byteImpl.add(it.toInt())
        }
        byteImpl.toList()
    }
    override val string = let {
        var str = ""
        value.forEach {
            str += java.lang.Integer.toHexString(it).padStart(2, '0') + " "
        }
        str
    }
    override val size = value.size
    override var int = string.replace(" ", "").let {
        when (it) {
            "" -> 0.toBigInteger()
            else -> it.toBigInteger(16)
        }
    }
}

class Variable(val name: String, type: String, values: String, val segment: Segment, val offset: BigInteger) {
    val typeStr = when (type) {
        "db" -> "byte"
        "dw" -> "word"
        else -> "dword"
    }
    val typeInt = when (typeStr) {
        "byte" -> 1
        "word" -> 2
        else -> 4
    }
    val value = let {
        val list = mutableListOf<Constant>()
        if (values[0] == '"') Regex(string).findAll(values).forEach {
            it.run { list.add(StringConstant(value)) }
        }
        else Regex(expr).findAll(values).forEach {
            it.run { list.add(NumberConstant(value, typeStr)) }
        }
        list.toList()
    }
    val str = let {
        var str = ""
        value.forEach {
            str += "${it.string} "
        }
        str
    }
    val size = if (value[0] is StringConstant) value[0].size else when (this.typeStr) {
        "byte" -> 1
        "word" -> 2
        else -> 4
    } * value.size
}

open class Label(val name: String, var offset: BigInteger, val type: String = "near")
class NoLabel : Label("", 0.toBigInteger())

class Segment(val name: String) {
    var size = 0
    override fun toString(): String {
        return "${name.padEnd(12)} | ${java.lang.Integer.toHexString(size).padStart(4, '0')}"
    }
}

fun parseLabel(string: String, offset: BigInteger): Label {
    Regex("($identifier)\\s*:", RegexOption.IGNORE_CASE).matchEntire(string)?.run {
        val (name) = destructured
        val labelInstance = Label(name, offset)
        Compiler.forwardLabels[name.toLowerCase()]?.forEach {
            println(it)
            it.secondPassString = it.secondPassString + " " + (java.lang.Long.toHexString((labelInstance.offset.toLong() - it.offset.toLong() - 2).let{num -> if(num<0) 0xFF-num else num}).padStart(2, '0') + " 90 90 90 90").padEnd(29)
            println(it.secondPassString)
        }
        return labelInstance
    }
    return NoLabel()
}

class SIB(val scale: Int, val index: Int) {
    val base = 0b101
    val code = (when (scale) {
        1 -> 0b00
        2 -> 0b01
        4 -> 0b10
        else -> 0b11
    } shl 6) + (index shl 3) + base
}

class MODRM(val mod: Int, val reg: Int, val rm: Int) {
    val code = (mod shl 6) + (reg shl 3) + rm
}

class Reg32(name: String) {
    val code = when (name) {
        "eax", "ax", "al" -> 0b000
        "ecx", "cx", "cl" -> 0b001
        "edx", "dx", "dl" -> 0b010
        "ebx", "bx", "bl" -> 0b011
        "esp", "sp", "ah" -> 0b100
        "ebp", "bp", "ch" -> 0b101
        "esi", "si", "dh" -> 0b110
        else -> 0b111
    }
}

class RegSeg(name: String) {
    val code = when (name) {
        "es" -> 0x26
        "cs" -> 0x2e
        "ss" -> 0x36
        "ds" -> 0x3e
        "fs" -> 0x64
        else -> 0x65
    }
}