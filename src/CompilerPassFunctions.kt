import java.io.File

fun Compiler.printFirstPass() {
    var filestr = ""
    filestr+="####| offset | size | source\n"
    //println("####| offset | size | source")
    var count = 1
    sentencesList.forEach {
        filestr+=count.toString().padStart(4, ' ') + "|" + it.firstPassPrint()+"\n"
        //println(count.toString().padStart(4, ' ') + "|" + it.firstPassPrint())
        count++
    }
    filestr+="\nsegment registers\n"
    //println("\nsegment registers")
    filestr+="0 |  cs    | ${segmentsList[1].name}\n"
    //println("0 |  cs    | ${segmentsList[1].name}")
    filestr+="1 |  ds    | ${segmentsList[0].name}\n"
    //println("1 |  ds    | ${segmentsList[0].name}")
    filestr+="2 |  ss    | nothing\n"
    //println("2 |  ss    | nothing")
    filestr+="3 |  es    | nothing\n"
    //println("3 |  es    | nothing")
    filestr+="4 |  gs    | nothing\n"
    //println("4 |  gs    | nothing")
    filestr+="5 |  fs    | nothing\n"
    //println("5 |  fs    | nothing")

    filestr+="\nsegments\n"
    //println("\nsegments")
    filestr+="segment name | size\n"
    //println("segment name | size")

    segmentsList.forEach {
        filestr+=it.toString() + "\n"
    }
    filestr+="\n"
    //println("")
    labelsList.forEach {
        filestr+="${it.name.padEnd(10)}  ${if (it.type == "near") "near    ${segmentsList[1].name.padStart(8)}:${java.lang.Integer.toHexString(
                it.offset.toInt()
        ).padStart(4, '0')}"
        else "${it.type.padEnd(6)}${java.lang.Integer.toHexString(it.offset.toInt()).padStart(8)}"}\n"
//        println(
//            "${it.name.padEnd(10)}  ${if (it.type == "near") "near    ${segmentsList[1].name.padStart(8)}:${java.lang.Integer.toHexString(
//                it.offset.toInt()
//            ).padStart(4, '0')}"
//            else "${it.type.padEnd(6)}${java.lang.Integer.toHexString(it.offset.toInt()).padStart(8)}"}"
//        )
    }
    labelVariablesList.forEach {
        filestr+="${it.name.padEnd(10)}  ${"${if (it.value[0] is NumberConstant) "number" else "string"}      ${java.lang.Integer.toHexString(
                it.offset.toInt()
        ).padStart(4, '0')}"}\n"
//        println(
//            "${it.name.padEnd(10)}  ${"${if (it.value[0] is NumberConstant) "number" else "string"}      ${java.lang.Integer.toHexString(
//                it.offset.toInt()
//            ).padStart(4, '0')}"}"
//        )
    }
    variablesList.forEach {
        filestr+="${it.name.padEnd(10)}  ${it.typeStr.toString()
                .padEnd(6)}  ${it.segment.name.padStart(8)}:${java.lang.Integer.toHexString(it.offset.toInt())
                .padStart(4, '0')}\n"
//        println(
//            "${it.name.padEnd(10)}  ${it.typeStr.toString()
//                .padEnd(6)}  ${it.segment.name.padStart(8)}:${java.lang.Integer.toHexString(it.offset.toInt())
//                .padStart(4, '0')}"
//        )
    }
    File(outputFile).writeText(filestr)
}
fun Compiler.printSecondPass() {
    var filestr = ""
    filestr+="####| offset | ${"bytes".padEnd(32)} | source\n"
    //println("####| offset | ${"bytes".padEnd(32)} | source")
    var count = 1
    sentencesList.forEach {
        filestr+=count.toString().padStart(4, ' ') + "|" + it.secondPassPrint() + "\n"
        //println(count.toString().padStart(4, ' ') + "|" + it.secondPassPrint())
        count++
    }

    filestr+="\nsegment registers\n"
    //println("\nsegment registers")
    filestr+="0 |  cs    | ${segmentsList[1].name}\n"
    //println("0 |  cs    | ${segmentsList[1].name}")
    filestr+="1 |  ds    | ${segmentsList[0].name}\n"
    //println("1 |  ds    | ${segmentsList[0].name}")
    filestr+="2 |  ss    | nothing\n"
    //println("2 |  ss    | nothing")
    filestr+="3 |  es    | nothing\n"
    //println("3 |  es    | nothing")
    filestr+="4 |  gs    | nothing\n"
    //println("4 |  gs    | nothing")
    filestr+="5 |  fs    | nothing\n"
    //println("5 |  fs    | nothing")

    filestr+="\nsegments\n"
    //println("\nsegments")
    filestr+="segment name | size\n"
    //println("segment name | size")

    segmentsList.forEach {
        filestr+=it.toString() + "\n"
    }
    filestr+="\n"
    //println("")
    labelsList.forEach {
        filestr+="${it.name.padEnd(10)}  ${if (it.type == "near") "near    ${segmentsList[1].name.padStart(8)}:${java.lang.Integer.toHexString(
                it.offset.toInt()
        ).padStart(4, '0')}"
        else "${it.type.padEnd(6)}${java.lang.Integer.toHexString(it.offset.toInt()).padStart(8)}"}\n"
//        println(
//            "${it.name.padEnd(10)}  ${if (it.type == "near") "near    ${segmentsList[1].name.padStart(8)}:${java.lang.Integer.toHexString(
//                it.offset.toInt()
//            ).padStart(4, '0')}"
//            else "${it.type.padEnd(6)}${java.lang.Integer.toHexString(it.offset.toInt()).padStart(8)}"}"
//        )
    }
    labelVariablesList.forEach {
        filestr+="${it.name.padEnd(10)}  ${"${if (it.value[0] is NumberConstant) "number" else "string"}      ${java.lang.Integer.toHexString(
                it.offset.toInt()
        ).padStart(4, '0')}"}\n"
//        println(
//            "${it.name.padEnd(10)}  ${"${if (it.value[0] is NumberConstant) "number" else "string"}      ${java.lang.Integer.toHexString(
//                it.offset.toInt()
//            ).padStart(4, '0')}"}"
//        )
    }
    variablesList.forEach {
        filestr+="${it.name.padEnd(10)}  ${it.typeStr.toString()
                .padEnd(6)}  ${it.segment.name.padStart(8)}:${java.lang.Integer.toHexString(it.offset.toInt())
                .padStart(4, '0')}\n"
//        println(
//            "${it.name.padEnd(10)}  ${it.typeStr.toString()
//                .padEnd(6)}  ${it.segment.name.padStart(8)}:${java.lang.Integer.toHexString(it.offset.toInt())
//                .padStart(4, '0')}"
//        )
    }
    File(outputFile).writeText(filestr)
}