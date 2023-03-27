package minesweeper

import kotlin.random.Random

const val SUDOKU_SIZE = 9

class MineField(val amountOfMines: Int = 10) {
    private val map: Array<CharArray> = Array(SUDOKU_SIZE, { CharArray(SUDOKU_SIZE) {'.'} })
    private val minesAround: Array<IntArray> = Array(SUDOKU_SIZE, { IntArray(SUDOKU_SIZE) {0} })
    private val markedMines: Array<BooleanArray> = Array(SUDOKU_SIZE, { BooleanArray(SUDOKU_SIZE) {false} })
    private var minesLeftToMark = amountOfMines
    private var amountOfMarkedCells = 0
    private var openedSafeCells = 0
    private var initializedField = false

    private fun putMines(y: Int, x: Int) {
        var remainingCells = SUDOKU_SIZE * SUDOKU_SIZE
        var requestedAmountOfMines = amountOfMines
        for (i in 0 until SUDOKU_SIZE) {
            for (j in 0 until SUDOKU_SIZE) {
                if (i == y && j == x) {
                    remainingCells--
                    continue
                }
                if (Random.nextInt(0, remainingCells) < requestedAmountOfMines) {
                    map[i][j] = ('X')
                    requestedAmountOfMines--
                }
                remainingCells--
            }
        }
        while (requestedAmountOfMines > 0) {
            var tmpX = Random.nextInt(0, SUDOKU_SIZE)
            var tmpY = Random.nextInt(0, SUDOKU_SIZE)
            if (map[tmpY][tmpX] != 'X' && tmpX != x && tmpY != y) {
                map[tmpY][tmpX] = 'X'
                requestedAmountOfMines--
            }
        }
    }
    private fun checkBoundsAndUpdateMinesAround(i: Int, j: Int) {
        if (i in 0 until SUDOKU_SIZE && j in 0 until SUDOKU_SIZE && map[i][j] != 'X') minesAround[i][j]++
    }
    private fun findMinesAround() {
        for (i in 0 until SUDOKU_SIZE) {
            for (j in 0 until SUDOKU_SIZE) {
                if (map[i][j] == 'X') {
                    for (x in i-1..i+1) for (y in j-1..j+1) checkBoundsAndUpdateMinesAround(x, y)
                }
            }
        }
    }
    private fun openNearestCells(y: Int, x: Int) {
        if (x !in 0 until SUDOKU_SIZE || y !in 0 until SUDOKU_SIZE || map[x][y] != '.') return
        if (markedMines[x][y]) {
            markedMines[x][y] = false
            amountOfMarkedCells--
        }
        if (minesAround[x][y] > 0) {
            map[x][y] = '0' + minesAround[x][y]
            openedSafeCells++
            return
        }
        map[x][y] = '/'
        openedSafeCells++
        for (i in x-1..x+1) for (j in y-1..y+1) openNearestCells(j, i)
    }
    private fun checkWin(): Boolean {
        return if (amountOfMarkedCells == amountOfMines && minesLeftToMark == 0) true
        else amountOfMines == SUDOKU_SIZE * SUDOKU_SIZE - openedSafeCells
    }
    private fun markCellAsMine(y: Int, x: Int) {
        if (markedMines[x][y]) {
            if (map[x][y] == 'X') minesLeftToMark++
            markedMines[x][y] = false
            --amountOfMarkedCells
        } else {
            if (map[x][y] == 'X') --minesLeftToMark
            ++amountOfMarkedCells
            markedMines[x][y] = true
        }
    }
    private fun markCellAsFree(y: Int, x: Int): MarkResponse {
        return if (map[x][y] == 'X') MarkResponse.LOSS
        else if (map[x][y] == '.') {
            if (minesAround[x][y] > 0) {
                map[x][y] = '0' + minesAround[x][y]
                if (markedMines[x][y]) {
                    markedMines[x][y] = false
                    amountOfMarkedCells--
                }
                openedSafeCells++
            }
            else openNearestCells(y, x)
            MarkResponse.ACTION_SUCCEEDED
        }
        else MarkResponse.ACTION_SUCCEEDED
    }

    fun markCell(y: Int, x: Int, option: String): MarkResponse {
        if (!initializedField) {
            initializedField = true
            putMines(y, x)
            findMinesAround()
        }
        return if (option == "mine") {
            markCellAsMine(y, x)
            if (checkWin()) MarkResponse.WIN
            else MarkResponse.ACTION_SUCCEEDED
        }
        else if (option == "free") {
            var curResponse = markCellAsFree(y, x)
            if (curResponse != MarkResponse.LOSS && checkWin()) MarkResponse.WIN
            else curResponse
        }
        else MarkResponse.ACTION_SUCCEEDED
    }
    fun printField(isLoss: Boolean = false) {
        println(" │123456789│")
        println("—│—————————│")
        for (i in 0 until SUDOKU_SIZE) {
            print("${i+1}│")
            for (j in 0 until SUDOKU_SIZE) {
                print(
                    if (!isLoss && markedMines[i][j]) '*'
                    else if (!isLoss && map[i][j] == 'X') '.'
                    else map[i][j]
                )
            }
            println("│")
        }
        println("—│—————————│")
    }

    fun printFieldWithHints(showMines: Boolean = false) {
        println(" │123456789│")
        println("—│—————————│")
        for (i in 0 until SUDOKU_SIZE) {
            print("${i+1}│")
            for (j in 0 until SUDOKU_SIZE) {
                print(
                    if (minesAround[i][j] > 0) minesAround[i][j]
                    else if (showMines) map[i][j]
                    else if (minesAround[i][j] == -1) '*'
                    else '.'
                )
            }
            println("│")
        }
        println("—│—————————│")
    }
}