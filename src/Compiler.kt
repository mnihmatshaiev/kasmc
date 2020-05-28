import java.io.File
import java.math.BigInteger

object Compiler {
    val segmentsList = mutableListOf<Segment>()
    val variablesList = mutableListOf<Variable>()
    val labelVariablesList = mutableListOf<Variable>()
    val labelsList = mutableListOf<Label>()
    val sentencesList = mutableListOf<Sentence>()
    val linesList = File(sourceFile).readText().split('\n').toMutableList()
    var currentOffset: BigInteger = 0.toBigInteger()
    val forwardLabels = mutableMapOf<String, MutableList<InstructionSentence>>()
    lateinit var activeSegment: Segment
}