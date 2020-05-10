var sourceFile = "D:\\test1.asm"
var outputFile = "D:\\lst.lst"

fun main(args: Array<String>) {
//    if (args.size == 1 && args[0]!="") {
//        sourceFile = args[0]
//        Compiler.parseProgram()
//        Compiler.printSecondPass()
//    }
//    else if (args.size == 3 && args[0]!="" && args[1]=="-o" && args[2]!=""){
//        sourceFile = args[0]
//        outputFile = args[2]
//        Compiler.parseProgram()
//        Compiler.printSecondPass()
//    }
//    else {
//        println("wrong kasmc usage")
//        println("kasm compiler has following usage:")
//        println("kasmc <input_file.asm> [-o <output_file>]")
//        println("is output_file option absent, listing will be shown in console and written to default `lst.lst` file")
//    }
    Compiler.parseProgram()
    Compiler.printSecondPass()
}