fun Compiler.printFirstPass() {
    println("####| offset | size | source")
    var count = 1
    sentencesList.forEach {
        println(count.toString().padStart(4, ' ') + "|" + it.firstPassPrint())
        count++
    }

    println("\nsegment registers")
    println("0 |  cs    | ${segmentsList[1].name}")
    println("1 |  ds    | ${segmentsList[0].name}")
    println("2 |  ss    | nothing")
    println("3 |  es    | nothing")
    println("4 |  gs    | nothing")
    println("5 |  fs    | nothing")

    println("\nsegments")
    println("segment name | size")
    segmentsList.forEach {
        println(it)
    }
    println("")
    labelsList.forEach {
        println(
            "${it.name.padEnd(10)}  ${if (it.type == "near") "near    ${segmentsList[1].name.padStart(8)}:${java.lang.Integer.toHexString(
                it.offset.toInt()
            ).padStart(4, '0')}"
            else "${it.type.padEnd(6)}${java.lang.Integer.toHexString(it.offset.toInt()).padStart(8)}"}"
        )
    }
    labelVariablesList.forEach {
        println(
            "${it.name.padEnd(10)}  ${"${if (it.value[0] is NumberConstant) "number" else "string"}      ${java.lang.Integer.toHexString(
                it.offset.toInt()
            ).padStart(4, '0')}"}"
        )
    }
    variablesList.forEach {
        println(
            "${it.name.padEnd(10)}  ${it.typeStr.toString()
                .padEnd(6)}  ${it.segment.name.padStart(8)}:${java.lang.Integer.toHexString(it.offset.toInt())
                .padStart(4, '0')}"
        )
    }
}
fun Compiler.printSecondPass() {
    println("####| offset | ${"bytes".padEnd(24)} | source")
    var count = 1
    sentencesList.forEach {
        println(count.toString().padStart(4, ' ') + "|" + it.secondPassPrint())
        count++
    }

    println("\nsegment registers")
    println("0 |  cs    | ${segmentsList[1].name}")
    println("1 |  ds    | ${segmentsList[0].name}")
    println("2 |  ss    | nothing")
    println("3 |  es    | nothing")
    println("4 |  gs    | nothing")
    println("5 |  fs    | nothing")

    println("\nsegments")
    println("segment name | size")
    segmentsList.forEach {
        println(it)
    }
    println("")
    labelsList.forEach {
        println(
            "${it.name.padEnd(10)}  ${if (it.type == "near") "near    ${segmentsList[1].name.padStart(8)}:${java.lang.Integer.toHexString(
                it.offset.toInt()
            ).padStart(4, '0')}"
            else "${it.type.padEnd(6)}${java.lang.Integer.toHexString(it.offset.toInt()).padStart(8)}"}"
        )
    }
    labelVariablesList.forEach {
        println(
            "${it.name.padEnd(10)}  ${"${if (it.value[0] is NumberConstant) "number" else "string"}      ${java.lang.Integer.toHexString(
                it.offset.toInt()
            ).padStart(4, '0')}"}"
        )
    }
    variablesList.forEach {
        println(
            "${it.name.padEnd(10)}  ${it.typeStr.toString()
                .padEnd(6)}  ${it.segment.name.padStart(8)}:${java.lang.Integer.toHexString(it.offset.toInt())
                .padStart(4, '0')}"
        )
    }
}