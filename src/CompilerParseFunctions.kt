fun Compiler.parseInstruction(orig: String): Sentence {
    val hexOffset = java.lang.Long.toHexString(currentOffset.toLong()).padStart(6, '0')
    pushad.matchEntire(orig)?.run {
        val (label) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val bytesString = "60".padEnd(32)
        val sizeString = "1".padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 1)
    }
    neg.matchEntire(orig)?.run {
        val (label, reg) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val modrm = MODRM(0b11, 3, Reg32(reg).code)
        val bytesString = "f7 ${java.lang.Integer.toHexString(modrm.code).padStart(2, '0')}".padEnd(32)
        val sizeString = "2".padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 2)
    }
    stos1.matchEntire(orig)?.run {
        val (label, type, ptr, ident) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val variableInstance =
            findVariable(ident) ?: return ErrorSentence(orig, currentOffset, "$label doesn't exist")
        val prefixOpcodeString = when (type) {
            "byte" -> "aa"
            "word" -> "66| ab"
            "dword" -> "ab"
            else -> when (variableInstance.size) {
                1 -> "aa"
                2 -> "66| ab"
                else -> "ab"
            }
        }
        val bytesString = "$prefixOpcodeString".padEnd(32)
        val sizeString = "1".padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 1)
    }
    stos2.matchEntire(orig)?.run {
        val (label, type, ptr, ident, reg, num) = destructured
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        NumberConstant(num)?.run{
            if(int.toInt() != 1 && int.toInt()!=2 && int.toInt()!=4 &&int.toInt()!=8) return ErrorSentence(orig, currentOffset, "wrong scale value")
        }
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val variableInstance =
            findVariable(ident) ?: return ErrorSentence(orig, currentOffset, "$label doesn't exist")
        val prefixOpcodeString = when (type) {
            "byte" -> "aa"
            "word" -> "66| ab"
            "dword" -> "ab"
            else -> when (variableInstance.size) {
                1 -> "aa"
                2 -> "66| ab"
                else -> "ab"
            }
        }
        val bytesString = "$prefixOpcodeString".padEnd(32)
        val sizeString = "1".padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 1)
    }
    stos3.matchEntire(orig)?.run {
        val (label, type, ptr, seg, col, reg, num) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        if(seg!="" && col=="") return ErrorSentence(orig, currentOffset, "`:` expected")
        NumberConstant(num)?.run{
            if(int.toInt() != 1 && int.toInt()!=2 && int.toInt()!=4 &&int.toInt()!=8) return ErrorSentence(orig, currentOffset, "wrong scale value")
        }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val segmentString = when (seg) {
            "" -> ""
            else -> java.lang.Integer.toHexString(RegSeg(seg).code).padStart(2, '0')
        }
        val prefixOpcodeString = when (type) {
            "byte" -> "aa"
            "word" -> "66| ab"
            else -> "ab"
        }
        val bytesString = "${when (segmentString) {
            "" -> ""
            else -> "$segmentString: "
        }}$prefixOpcodeString".padEnd(32)
        val sizeString = "1".padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 1)
    }
    cmp.matchEntire(orig)?.run {
        val (label, reg, imm) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val modrm = MODRM(0b11, 7, Reg32(reg).code)
        val immVal = when {
            Regex(expr, RegexOption.IGNORE_CASE).matches(imm) -> NumberConstant(imm)
            else -> StringConstant(imm)
        }
        val opcodeString = when (immVal.size) {
            1 -> "83"
            else -> "81"
        }
        val sizeString = (2 + immVal.size).toString().padStart(4)
        val bytesString = "$opcodeString ${java.lang.Integer.toHexString(modrm.code)
            .padStart(2, '0')} ${java.lang.Long.toHexString(immVal.int.toLong()).padStart(2, '0')}".padEnd(32)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 2 + immVal.size)
    }
    or1.matchEntire(orig)?.run {
        val (label, reg, type, ptr, ident) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val variableInstance =
            findVariable(ident) ?: return ErrorSentence(orig, currentOffset, "$label doesn't exist")
        val modrm = MODRM(0b00, Reg32(reg).code, 0b101)
        val segmentString = java.lang.Integer.toHexString(RegSeg("es").code).padStart(2, '0')
        val prefixOpcodeString = "0b"
        val bytesString = "$segmentString: $prefixOpcodeString ${java.lang.Integer.toHexString(modrm.code)
            .padStart(2, '0')} ${java.lang.Long.toHexString(variableInstance.offset.toLong())
            .padStart(8, '0')}".padEnd(32)
        val sizeAdd = when (type) {
            "byte" -> 1
            "dword" -> 4
            else -> variableInstance.typeInt
        }
        val sizeString = (3 + sizeAdd).toString().padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 3 + sizeAdd)
    }
    or2.matchEntire(orig)?.run {
        val (label, reg, type, ptr, ident, ind, num) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        NumberConstant(num)?.run{
            if(int.toInt() != 1 && int.toInt()!=2 && int.toInt()!=4 &&int.toInt()!=8) return ErrorSentence(orig, currentOffset, "wrong scale value")
        }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val variableInstance =
            findVariable(ident) ?: return ErrorSentence(orig, currentOffset, "$label doesn't exist")
        val modrm = MODRM(0b00, Reg32(reg).code, 0b100)
        val sib = SIB(NumberConstant(num).typeInt, Reg32(ind).code)
        val segmentString = java.lang.Integer.toHexString(RegSeg("es").code).padStart(2, '0')
        val prefixOpcodeString = "0b"
        val bytesString = "$segmentString: $prefixOpcodeString ${java.lang.Integer.toHexString(modrm.code)
            .padStart(2, '0')} ${java.lang.Integer.toHexString(sib.code)
            .padStart(2, '0')} ${java.lang.Long.toHexString(variableInstance.offset.toLong())
            .padStart(8, '0')}\"".padEnd(32)
        val sizeAdd = when (type) {
            "byte" -> 1
            "dword" -> 4
            else -> variableInstance.typeInt
        }
        val sizeString = (3 + sizeAdd).toString().padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 3 + sizeAdd)
    }
    or3.matchEntire(orig)?.run {
        val (label, reg, type, ptr, seg, col, ind, num) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        if(seg!="" && col=="") return ErrorSentence(orig, currentOffset, "`:` expected")
        NumberConstant(num)?.run{
            if(int.toInt() != 1 && int.toInt()!=2 && int.toInt()!=4 &&int.toInt()!=8) return ErrorSentence(orig, currentOffset, "wrong scale value")
        }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val modrm = MODRM(0b00, Reg32(reg).code, 0b100)
        val sib = SIB(NumberConstant(num).typeInt, Reg32(ind).code)
        val segmentString = when (seg) {
            "" -> ""
            else -> java.lang.Integer.toHexString(RegSeg(seg).code).padStart(2, '0')
        }
        val prefixOpcodeString = "0b"
        val bytesString = "$segmentString: $prefixOpcodeString ${java.lang.Integer.toHexString(modrm.code)
            .padStart(2, '0')} ${java.lang.Integer.toHexString(sib.code).padStart(2, '0')} ${"0".padStart(
            8,
            '0'
        )}\"".padEnd(32)
        var sizeAdd = when (type) {
            "byte" -> 1
            "dword" -> 4
            else -> 4
        }
        if (seg != "") sizeAdd++
        val sizeString = (3 + sizeAdd).toString().padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 3 + sizeAdd)
    }
    and1.matchEntire(orig)?.run {
        val (label, type, ptr, ident, reg) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val variableInstance =
            findVariable(ident) ?: return ErrorSentence(orig, currentOffset, "$label doesn't exist")
        val modrm = MODRM(0b00, Reg32(reg).code, 0b101)
        val segmentString = java.lang.Integer.toHexString(RegSeg("es").code).padStart(2, '0')
        val prefixOpcodeString = "21"
        val bytesString = "$segmentString: $prefixOpcodeString ${java.lang.Integer.toHexString(modrm.code)
            .padStart(2, '0')} ${java.lang.Long.toHexString(variableInstance.offset.toLong())
            .padStart(8, '0')}".padEnd(32)
        val sizeAdd = when (type) {
            "byte" -> 1
            "dword" -> 4
            else -> variableInstance.typeInt
        }
        val sizeString = (3 + sizeAdd).toString().padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 3 + sizeAdd)
    }
    and2.matchEntire(orig)?.run {
        val (label, type, ptr, ident, ind, num, reg) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        NumberConstant(num)?.run{
            if(int.toInt() != 1 && int.toInt()!=2 && int.toInt()!=4 &&int.toInt()!=8) return ErrorSentence(orig, currentOffset, "wrong scale value")
        }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val variableInstance =
            findVariable(ident) ?: return ErrorSentence(orig, currentOffset, "$label doesn't exist")
        val modrm = MODRM(0b00, Reg32(reg).code, 0b100)
        val sib = SIB(NumberConstant(num).typeInt, Reg32(ind).code)
        val segmentString = java.lang.Integer.toHexString(RegSeg("es").code).padStart(2, '0')
        val prefixOpcodeString = "21"
        val bytesString = "$segmentString: $prefixOpcodeString ${java.lang.Integer.toHexString(modrm.code)
            .padStart(2, '0')} ${java.lang.Integer.toHexString(sib.code)
            .padStart(2, '0')} ${java.lang.Long.toHexString(variableInstance.offset.toLong())
            .padStart(8, '0')}\"".padEnd(32)
        val sizeAdd = when (type) {
            "byte" -> 1
            "dword" -> 4
            else -> variableInstance.typeInt
        }
        val sizeString = (3 + sizeAdd).toString().padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 3 + sizeAdd)
    }
    and3.matchEntire(orig)?.run {
        val (label, type, ptr, seg, col, ind, num, reg) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        if(seg!="" && col=="") return ErrorSentence(orig, currentOffset, "`:` expected")
        NumberConstant(num)?.run{
            if(int.toInt() != 1 && int.toInt()!=2 && int.toInt()!=4 &&int.toInt()!=8) return ErrorSentence(orig, currentOffset, "wrong scale value")
        }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val modrm = MODRM(0b00, Reg32(reg).code, 0b100)
        val sib = SIB(NumberConstant(num).typeInt, Reg32(ind).code)
        val segmentString = when (seg) {
            "" -> ""
            else -> java.lang.Integer.toHexString(RegSeg(seg).code).padStart(2, '0')
        }
        val prefixOpcodeString = "21"
        val bytesString = "${when (segmentString) {
            "" -> ""
            else -> "$segmentString: "
        }}$prefixOpcodeString ${java.lang.Integer.toHexString(modrm.code)
            .padStart(2, '0')} ${java.lang.Integer.toHexString(sib.code).padStart(2, '0')} ${"0".padStart(
            8,
            '0'
        )}\"".padEnd(32)
        var sizeAdd = when (type) {
            "byte" -> 1
            "dword" -> 4
            else -> 4
        }
        if (seg != "") sizeAdd++
        val sizeString = (3 + sizeAdd).toString().padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 3 + sizeAdd)
    }
    xchg.matchEntire(orig)?.run {
        val (label, reg1, reg2) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val modrm = MODRM(0b11, Reg32(reg1).code, Reg32(reg2).code)
        val bytesString = "87 ${java.lang.Integer.toHexString(modrm.code).padStart(2, '0')}".padEnd(32)
        val sizeString = "2".padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 2)
    }
    adc1.matchEntire(orig)?.run {
        val (label, type, ptr, ident, imm) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val variableInstance =
            findVariable(ident) ?: return ErrorSentence(orig, currentOffset, "$label doesn't exist")
        val modrm = MODRM(0b00, 2, 0b101)
        val immVal = when {
            Regex(number, RegexOption.IGNORE_CASE).matches(imm) -> NumberConstant(imm)
            else -> StringConstant(imm)
        }
        val prefixOpcodeString = when (type) {
            "byte" -> "80"
            else -> "81"
        }
        val segmentString = java.lang.Integer.toHexString(RegSeg("es").code).padStart(2, '0')
        val bytesString = "$segmentString: $prefixOpcodeString ${java.lang.Integer.toHexString(modrm.code)
            .padStart(2, '0')} ${java.lang.Long.toHexString(variableInstance.offset.toLong())
            .padStart(8, '0')} ${java.lang.Long.toHexString(immVal.int.toLong()).padStart(2, '0')}".padEnd(32)
        val sizeAdd = when (type) {
            "byte" -> 1
            "dword" -> 4
            else -> variableInstance.typeInt
        }
        val sizeString = (2 + sizeAdd + immVal.size).toString().padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = "$hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 2 + sizeAdd + immVal.size)
    }
    adc2.matchEntire(orig)?.run {
        val (label, type, ptr, ident, ind, num, imm) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        NumberConstant(num)?.run{
            if(int.toInt() != 1 && int.toInt()!=2 && int.toInt()!=4 &&int.toInt()!=8) return ErrorSentence(orig, currentOffset, "wrong scale value")
        }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val variableInstance =
            findVariable(ident) ?: return ErrorSentence(orig, currentOffset, "$label doesn't exist")
        val modrm = MODRM(0b00, 2, 0b101)
        val immVal = when {
            Regex(number, RegexOption.IGNORE_CASE).matches(imm) -> NumberConstant(imm)
            else -> StringConstant(imm)
        }
        val sib = SIB(NumberConstant(num).typeInt, Reg32(ind).code)
        val prefixOpcodeString = when (type) {
            "byte" -> "80"
            else -> "81"
        }
        val segmentString = java.lang.Integer.toHexString(RegSeg("es").code).padStart(2, '0')
        val bytesString = "$segmentString: $prefixOpcodeString ${java.lang.Integer.toHexString(modrm.code)
            .padStart(2, '0')} ${java.lang.Integer.toHexString(sib.code)
            .padStart(2, '0')} ${java.lang.Long.toHexString(variableInstance.offset.toLong())
            .padStart(8, '0')} ${java.lang.Long.toHexString(immVal.int.toLong()).padStart(2, '0')}".padEnd(32)
        val sizeAdd = when (type) {
            "byte" -> 1
            "dword" -> 4
            else -> variableInstance.typeInt
        }
        val sizeString = (3 + sizeAdd + immVal.size).toString().padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 3 + sizeAdd + immVal.size)
    }
    adc3.matchEntire(orig)?.run {
        val (label, type, ptr, seg, col, ind, num, imm) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        if(type!="" && ptr=="") return ErrorSentence(orig, currentOffset, "`ptr` expected")
        if(type=="" && ptr!="") return ErrorSentence(orig, currentOffset,  "expected `byte|word|dword` before `ptr`")
        if(seg!="" && col=="") return ErrorSentence(orig, currentOffset, "`:` expected")
        NumberConstant(num)?.run{
            if(int.toInt() != 1 && int.toInt()!=2 && int.toInt()!=4 &&int.toInt()!=8) return ErrorSentence(orig, currentOffset, "wrong scale value")
        }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val modrm = MODRM(0b00, 2, 0b101)
        val immVal = when {
            Regex(number, RegexOption.IGNORE_CASE).matches(imm) -> NumberConstant(imm)
            else -> StringConstant(imm)
        }
        val sib = SIB(NumberConstant(num).typeInt, Reg32(ind).code)
        val prefixOpcodeString = when (type) {
            "byte" -> "80"
            else -> "81"
        }
        val segmentString = when (seg) {
            "" -> ""
            else -> java.lang.Integer.toHexString(RegSeg(seg).code).padStart(2, '0')
        }
        val bytesString = "${when (segmentString) {
            "" -> ""
            else -> "$segmentString: "
        }}$prefixOpcodeString ${java.lang.Integer.toHexString(modrm.code)
            .padStart(2, '0')} ${java.lang.Integer.toHexString(sib.code).padStart(2, '0')} ${"0".padStart(
            8,
            '0'
        )} ${java.lang.Long.toHexString(immVal.int.toLong()).padStart(2, '0')}".padEnd(32)
        var sizeAdd = 4
        if (seg != "") sizeAdd += 1
        val sizeString = (3 + sizeAdd + immVal.size).toString().padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 3 + sizeAdd + immVal.size)
    }
    jnz.matchEntire(orig)?.run {
        val (label, target) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        if (labelInstance !is NoLabel) labelsList.add(labelInstance)
        val targetInstance = findLabel(target) ?: NoLabel()
        val prefixOpcodeString = "75"
        val sizeString = when (targetInstance) {
            is NoLabel -> "6"
            else -> "2"
        }.padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | ${prefixOpcodeString.padEnd(32)}"
        return InstructionSentence(
            orig, firstPass, secondPass, when (targetInstance) {
                is NoLabel -> 6
                else -> 2
            }
        )
    }
    segment.matchEntire(orig)?.run {
        val (name) = destructured
        val segmentInstance =
            findSegment(name)?.run { return ErrorSentence(orig, currentOffset, "$name segment already exists") }
                ?: Segment(name)
        currentOffset = 0.toBigInteger()
        activeSegment = segmentInstance
        segmentsList.add(segmentInstance)
        val sizeString = "0".padStart(4)
        val firstPass = " ${java.lang.Integer.toHexString(0).padStart(6, '0')} | $sizeString"
        val secondPass = " ${java.lang.Integer.toHexString(0).padStart(6, '0')} | ${" ".padEnd(32)}"
        return InstructionSentence(orig, firstPass, secondPass, 0)
    }
    ends.matchEntire(orig)?.run {
        val (name) = destructured
        val segmentInstance =
            findSegment(name) ?: return ErrorSentence(orig, currentOffset, "$name segment does not exist")
        segmentInstance.size = currentOffset.toInt()
        val sizeString = "0".padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val secondPass = " $hexOffset | ${" ".padEnd(32)}"
        return InstructionSentence(orig, firstPass, secondPass, 0)
    }
    end.matchEntire(orig)?.run {
        val sizeString = " ".padStart(4)
        val firstPass = " ${" ".padStart(6)} | $sizeString"
        val secondPass = " ${" ".padStart(6)} | ${" ".padEnd(32)}"
        return InstructionSentence(orig, firstPass, secondPass, 0)
    }
    vardef.matchEntire(orig)?.run {
        val (name, type, value) = destructured
        val varInstance =
            findVariable(name)?.run { return ErrorSentence(orig, currentOffset, "$name variable already exists") }
                ?: Variable(name, type, value, activeSegment, currentOffset)
        variablesList.add(varInstance)
        val sizeString = varInstance.size.toString().padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val bytesString = varInstance.str.padEnd(32)
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, varInstance.size)
    }
    Regex("""\s*($labelPattern)\s*""", RegexOption.IGNORE_CASE).matchEntire(orig)?.run {
        val (label) = destructured
        findVariable(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
        val labelInstance =
            findLabel(label)?.run { return ErrorSentence(orig, currentOffset, "$label is already exists") }
                ?: parseLabel(label, currentOffset)
        labelsList.add(labelInstance)
        val sizeString = "0".padStart(4)
        val firstPass = " $hexOffset | $sizeString"
        val bytesString = " ".padEnd(32)
        val secondPass = " $hexOffset | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 0)
    }
    assign.matchEntire(orig)?.run {
        val (name, value) = destructured
        findVariable(name)?.run { return ErrorSentence(orig, currentOffset, "$name is already different type") }
        findLabel(name)?.run { return ErrorSentence(orig, currentOffset, "$name is already different type") }
        val labelInstance = findLabelVar(name) ?: Variable(
            name,
            "",
            value,
            activeSegment,
            Variable("", "", value, activeSegment, currentOffset).value[0].int
        )
        labelVariablesList.add(labelInstance)
        val sizeString = " ".padStart(4)
        val firstPass = " ${" ".padStart(6)} | $sizeString"
        val bytesString =
            "=${java.lang.Long.toHexString(labelInstance.offset.toLong()).padStart(4, '0')}".padEnd(32)
        val secondPass = " ${" ".padStart(6)} | $bytesString"
        return InstructionSentence(orig, firstPass, secondPass, 0)
    }
    ifsent.matchEntire(orig)?.run {
        val (smth) = destructured
        findVariable(smth)?.run { return IfSentence(orig, currentOffset, this, null) }
        findLabelVar(smth)?.run { return IfSentence(orig, currentOffset, this, null) }
        Regex(expr).matchEntire(smth)?.run { return IfSentence(orig, currentOffset, null, NumberConstant(smth)) }
        Regex(string).matchEntire(smth)?.run { return IfSentence(orig, currentOffset, null, StringConstant(smth)) }
        return ErrorSentence(orig, currentOffset, "$smth - wrong condition expression")
    }
    return ErrorSentence(orig, currentOffset, "Wrong operand format")
}
fun Compiler.parseProgram(): List<Sentence> {
    linesList.removeIf { Regex("\\s*").matches(it) }
    var currentLine = 1
    val list = linesList.toMutableList()
    linesList.forEach {
        if (list.contains(it)) {
            val sentence = parseInstruction(it)
            currentLine++
            if (sentence is IfSentence) {
                currentLine--
                if (sentence.condition) {
                    list.removeAt(currentLine + 3)
                    list.removeAt(currentLine + 2)
                    list.removeAt(currentLine + 1)
                } else {
                    list.removeAt(currentLine + 3)
                    list.removeAt(currentLine + 1)
                    list.removeAt(currentLine)
                }
            } else {
                sentencesList.add(sentence)
                currentOffset += sentence.size.toBigInteger()
            }
        }
    }
    return sentencesList.toList()
}