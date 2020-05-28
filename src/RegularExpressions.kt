val reg32 = "eax|ecx|edx|ebx|esp|ebp|esi|edi|al|cl|dl|bl|ah|ch|dh|bh|ax|cx|dx|bx|sp|bp|si|di"
val regSeg = "es|cs|ss|ds|fs|gs"
val identifier = "[a-z][a-z0-9]*"
val number = "[0-9a-f]+h"
val string = "\".*\""
val expr = """$number\s*([\\+\\-\\/\\*]\s*$number)*"""
val labelPattern = """$identifier\s*:"""

val memory1 = "(dword|word|byte)?\\s*(ptr)?\\s*($identifier)"
val memory2 = "(dword|word|byte)?\\s*(ptr)?\\s*($identifier)\\[($reg32)\\*($number)\\]"
val memory3 = "(dword|word|byte)?\\s*(ptr)?\\s*($regSeg)?(:)?\\[($reg32)\\*($number)\\]"

val pushad = Regex("""\s*($labelPattern)?\s*pushad\s*""", RegexOption.IGNORE_CASE)
val neg = Regex("""\s*($labelPattern)?\s*neg\s*($reg32)\s*""", RegexOption.IGNORE_CASE)
val stos1 = Regex("""\s*($labelPattern)?\s*stos\s*$memory1\s*""", RegexOption.IGNORE_CASE)
val stos2 = Regex("""\s*($labelPattern)?\s*stos\s*$memory2\s*""", RegexOption.IGNORE_CASE)
val stos3 = Regex("""\s*($labelPattern)?\s*stos\s*$memory3\s*""", RegexOption.IGNORE_CASE)
val cmp = Regex("""\s*($labelPattern)?\s*cmp\s*($reg32)\s*,\s*($expr|$string)\s*""", RegexOption.IGNORE_CASE)
val or1 = Regex("""\s*($labelPattern)?\s*or\s*($reg32)\s*,\s*$memory1\s*""", RegexOption.IGNORE_CASE)
val or2 = Regex("""\s*($labelPattern)?\s*or\s*($reg32)\s*,\s*$memory2\s*""", RegexOption.IGNORE_CASE)
val or3 = Regex("""\s*($labelPattern)?\s*or\s*($reg32)\s*,\s*$memory3\s*""", RegexOption.IGNORE_CASE)
val and1 = Regex("""\s*($labelPattern)?\s*and\s*$memory1\s*,\s*($reg32)\s*""", RegexOption.IGNORE_CASE)
val and2 = Regex("""\s*($labelPattern)?\s*and\s*$memory2\s*,\s*($reg32)\s*""", RegexOption.IGNORE_CASE)
val and3 = Regex("""\s*($labelPattern)?\s*and\s*$memory3\s*,\s*($reg32)\s*""", RegexOption.IGNORE_CASE)
val xchg = Regex("""\s*($labelPattern)?\s*xchg\s*($reg32)\s*,\s*($reg32)\s*""", RegexOption.IGNORE_CASE)
val adc1 = Regex("""\s*($labelPattern)?\s*adc\s*$memory1\s*,\s*($expr|$string)\s*""", RegexOption.IGNORE_CASE)
val adc2 = Regex("""\s*($labelPattern)?\s*adc\s*$memory2\s*,\s*($expr|$string)\s*""", RegexOption.IGNORE_CASE)
val adc3 = Regex("""\s*($labelPattern)?\s*adc\s*$memory3\s*,\s*($expr|$string)\s*""", RegexOption.IGNORE_CASE)
val jnz = Regex("""\s*($labelPattern)?\s*jnz\s*($identifier)\s*""", RegexOption.IGNORE_CASE)
val segment = Regex("""\s*($identifier)\s*segment\s*""", RegexOption.IGNORE_CASE)
val ends = Regex("""\s*($identifier)\s*ends\s*""", RegexOption.IGNORE_CASE)
val end = Regex("""\s*end\s*""", RegexOption.IGNORE_CASE)
val assign = Regex("""\s*($identifier)\s*=\s*($number|$string)\s*""", RegexOption.IGNORE_CASE)
val ifsent = Regex("""\s*if\s*($expr|$identifier|$string)\s*""", RegexOption.IGNORE_CASE)
val vardef = Regex(
    """\s*($identifier)\s*(db|dw|dd)\s*(($string\s*(,\s*$string)*)|($expr\s*(,\s*$expr)*))\s*""",
    RegexOption.IGNORE_CASE
)