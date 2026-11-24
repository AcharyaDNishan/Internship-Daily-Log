package com.nishan.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nishan.tictactoe.ui.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var currentPlayer by remember { mutableStateOf("X") }

    val winner = checkWinner(board)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val statusText = when (winner) {
            "Draw" -> "It's a Draw!"
            "X", "O" -> "Player $winner Wins!"
            else -> "Player $currentPlayer's Turn"
        }

        Text(
            text = statusText,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Box(
            modifier = Modifier.size(300.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (row in 0 until 3) {
                    if (row > 0) {
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        for (col in 0 until 3) {
                            val index = row * 3 + col
                            if (col > 0) {
                                VerticalDivider(
                                    thickness = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(enabled = board[index].isEmpty() && winner == null) {
                                        board = board.toMutableList().also {
                                            it[index] = currentPlayer
                                        }
                                        currentPlayer = if (currentPlayer == "X") "O" else "X"
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = board[index],
                                    fontSize = 44.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                board = List(9) { "" }
                currentPlayer = "X"
            }
        ) {
            Text(text = "Reset Game", fontSize = 18.sp)
        }
    }
}

fun checkWinner(board: List<String>): String? {
    val winPatterns = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        listOf(0, 4, 8), listOf(2, 4, 6)
    )
    for (pattern in winPatterns) {
        val (a, b, c) = pattern
        if (board[a].isNotEmpty() && board[a] == board[b] && board[a] == board[c]) {
            return board[a]
        }
    }
    if (board.none { it.isEmpty() }) {
        return "Draw"
    }
    return null
}
