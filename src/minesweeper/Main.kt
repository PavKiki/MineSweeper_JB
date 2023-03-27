package minesweeper

import java.util.Scanner

fun initAskUser(): Int {
    print("How many mines do you want on the field? ")
    return readln().toInt()
}

fun main() {
    val scanner = Scanner(System.`in`)
    val amountOfMines = initAskUser()
    val mineField = MineField(amountOfMines)
    var curResponse: MarkResponse
    do {
        mineField.printField()
        print("Set/unset mine marks or claim a cell as free: ")
        curResponse = mineField.markCell(scanner.nextInt() - 1, scanner.nextInt() - 1, scanner.next())
        scanner.nextLine()
        println()
    } while(curResponse != MarkResponse.WIN && curResponse != MarkResponse.LOSS)
    if (curResponse == MarkResponse.LOSS) mineField.printField(isLoss = true)
    else mineField.printField()
    println(curResponse.getMessage())
}