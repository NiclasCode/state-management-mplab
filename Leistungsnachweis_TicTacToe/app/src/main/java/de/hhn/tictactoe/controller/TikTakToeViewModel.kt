package de.hhn.tictactoe.controller

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import de.hhn.tictactoe.model.Field
import de.hhn.tictactoe.model.GameModel
import de.hhn.tictactoe.model.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TikTakToeViewModel : ViewModel() {
    // Variable zum Speichern der Felder und des Zustandes der Felder
    private var _gameField = MutableStateFlow(
        List(3) { column ->
            List(3) { row ->
                Field(
                    indexRow = row,
                    indexColumn = column
                )
            }
        })
    val fieldStates = _gameField.asStateFlow()

    // Variable zum Speichern des SpielZustandes
    private var _gameState = MutableStateFlow(GameModel())
    val gameState = _gameState.asStateFlow()

    /**
     * startet das Spiel neu, indem alle Felder leer neuerstellt werden und ein neues Game Model erstellt wird
     */
    fun resetGame() {
        _gameField.value = List(3) { column ->
            List(3) { row ->
                Field(
                    indexRow = row,
                    indexColumn = column
                )
            }
        }
        _gameState.value = GameModel()
    }

    /**
     * setzt den Status des übergebenen Feldes auf den Status des aktuellen Spielers
     * gibt ein Toast aus, falls das Spiel beendet ist oder das Feld schon ausgewählt ist
     * @param field Feld, das ausgewählt wurde
     * @param context Appcontext, um ein Toast anzuzeigen
     */
    fun selectField(field: Field, context: Context) {
        if (field.status == Status.Empty && !gameState.value.isGameEnding) {
            _gameField.value = returnUpdatedList(field)
            _gameState.value.currentPlayer = _gameState.value.currentPlayer.next()
        } else if (gameState.value.isGameEnding) {
            Toast.makeText(
                context,
                "Game already finished! Restart for a new round!",
                Toast.LENGTH_LONG
            ).show()
        } else if (field.status != Status.Empty) {
            Toast.makeText(context, "Select an Empty field", Toast.LENGTH_SHORT).show()
        }
        checkEndingGame()
    }

    /**
     * überprüft, ob das Spiel gemäß den TikTakToe Regeln beendet ist
     * setzt den Spielstand auf beendet und setzt den Spieler, der gewonnen hat
     */
    private fun checkEndingGame() {
        if (checkRows().first) {
            _gameState.value.isGameEnding = true
            _gameState.value.winningPlayer = checkRows().second
        } else if (checkColumns().first) {
            _gameState.value.isGameEnding = true
            _gameState.value.winningPlayer = checkColumns().second
        } else if (checkDiagonals().first) {
            _gameState.value.isGameEnding = true
            _gameState.value.winningPlayer = checkDiagonals().second
        }
    }

    /**
     * überprüft die Diagonalen
     */
    private fun checkDiagonals(): Pair<Boolean, Status> {
        if (_gameField.value[0][0].status == _gameField.value[1][1].status
            && _gameField.value[1][1].status == _gameField.value[2][2].status
            && _gameField.value[0][0].status != Status.Empty
        ) {
            return Pair(true, _gameField.value[0][0].status)
        } else if (_gameField.value[2][0].status == _gameField.value[1][1].status
            && _gameField.value[1][1].status == _gameField.value[0][2].status
            && _gameField.value[2][0].status != Status.Empty
        ) {
            return Pair(true, _gameField.value[2][0].status)
        }
        return Pair(false, Status.Empty)
    }

    /**
     * überprüft die Spalten
     */
    private fun checkColumns(): Pair<Boolean, Status> {
        for (i in 0..2) {
            if (_gameField.value[0][i].status == _gameField.value[1][i].status
                && _gameField.value[1][i].status == _gameField.value[2][i].status
                && _gameField.value[0][i].status != Status.Empty
            ) {
                return Pair(true, _gameField.value[0][i].status)
            }
        }
        return Pair(false, Status.Empty)
    }

    /**
     * überprüft die Zeilen
     */
    private fun checkRows(): Pair<Boolean, Status> {
        for (row in _gameField.value) {
            if (row[0].status == row[1].status
                && row[1].status == row[2].status
                && row[0].status != Status.Empty
            ) {
                return Pair(true, row[0].status)
            }
        }
        return Pair(false, Status.Empty)
    }

    /**
     * returnt eine Kopie der _gameField.value Liste, in der das übergebene Feld gesetzt ist
     * @param field Feld, dessen Status gesetzt werden soll
     * @return kopierte Liste mit abgeändertem Feld
     */
    private fun returnUpdatedList(field: Field): List<List<Field>> {
        val newlist = arrayListOf<ArrayList<Field>>()
        for (row in _gameField.value) {
            val smalllist = arrayListOf<Field>()
            for (fieldInRow in row) {
                if (fieldInRow != field) {
                    smalllist.add(
                        Field(
                            fieldInRow.status,
                            fieldInRow.indexColumn,
                            fieldInRow.indexRow
                        )
                    )
                } else {
                    smalllist.add(
                        Field(
                            _gameState.value.currentPlayer,
                            fieldInRow.indexColumn,
                            fieldInRow.indexRow
                        )
                    )
                }
            }
            newlist.add(smalllist)
        }
        return newlist.toList()
    }
}