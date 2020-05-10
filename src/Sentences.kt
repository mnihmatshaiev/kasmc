import java.math.BigInteger

abstract class Sentence(val orig: String, val label: Label, val offset: BigInteger) {
    abstract val size: Int
    abstract fun firstPassPrint(): String
    abstract fun secondPassPrint(): String
}

class IfSentence(orig: String, offset: BigInteger, val variable: Variable?, val imm: Constant?) :
    Sentence(orig, NoLabel(), offset) {
    override val size = 0
    val condition: Boolean = parseValue()
    fun parseValue(): Boolean {
        if (variable != null) {
            if (variable.value.size == 0) return false
            else if (variable.value.size == 1 && variable.value[0] is NumberConstant) return variable.value[0].int != 0.toBigInteger()
            else return true
        } else if (imm != null) return imm.int != 0.toBigInteger()
        else return false
    }

    override fun firstPassPrint(): String {
        return ""
    }

    override fun secondPassPrint(): String {
        return ""
    }
}

class InstructionSentence(
    orig: String,
    val firstPassString: String,
    var secondPassString: String,
    override val size: Int
) : Sentence(orig, NoLabel(), 0.toBigInteger()) {
    override fun firstPassPrint(): String {
        return "$firstPassString | $orig"
    }

    override fun secondPassPrint(): String {
        return "$secondPassString | $orig"
    }
}

class ErrorSentence(orig: String, offset: BigInteger, val message: String) : Sentence(orig, NoLabel(), offset) {
    override val size = 0
    override fun firstPassPrint(): String {
        return " ${java.lang.Long.toHexString(offset.toLong()).padStart(6, '0')} | ${size.toString()
            .padStart(4)} | $orig\n $message"
    }

    override fun secondPassPrint(): String {
        return " ${java.lang.Long.toHexString(offset.toLong()).padStart(6, '0')} | ${" ".padEnd(32)} | $orig\n $message"
    }
}