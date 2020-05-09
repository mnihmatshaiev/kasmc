fun Compiler.findSegment(name: String): Segment? {
    return segmentsList.find { it.name.toLowerCase() == name.toLowerCase() }
}

fun Compiler.findVariable(name: String): Variable? {
    return variablesList.find { it.name.toLowerCase() == name.toLowerCase() }
}

fun Compiler.findLabel(name: String): Label? {
    return labelsList.find { it.name.toLowerCase() == name.toLowerCase() }
}

fun Compiler.findLabelVar(name: String): Variable? {
    return labelVariablesList.find { it.name.toLowerCase() == name.toLowerCase() }
}