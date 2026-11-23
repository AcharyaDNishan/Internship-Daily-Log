package com.nishan.librarymanagment

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nishan.librarymanagment.ui.theme.LibraryManagmentTheme
import java.io.File

data class BookItem(
    val title: String,
    val author: String,
    val rackno: String = "",
    val isBorrowed: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LibraryManagmentTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onNavigateToPage1 = { navController.navigate("page_1") },
                            onNavigateToPage2 = { navController.navigate("page_2") },
                            onNavigateToPage3 = { navController.navigate("page_3") }
                        )
                    }
                    composable("page_1") { Book(onBack = { navController.popBackStack() }) }
                    composable("page_2") { BookList(onBack = { navController.popBackStack() }) }
                    composable("page_3") { Search(onBack = { navController.popBackStack() }) }
                }
            }
        }
    }
}

// File handling helper functions
private fun getBookFile(context: Context): File {
    val file = File(context.filesDir, "books.txt")
    if (!file.exists()) {
        file.createNewFile()
    }
    return file
}

private fun saveBookToFile(
    context: Context,
    title: String,
    author: String,
    rackno: String,
    isBorrowed: Boolean
): Boolean {
    return try {
        val file = getBookFile(context)
        file.appendText("$title|$author|$rackno|$isBorrowed\n")
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun readBooksFromFile(context: Context): List<BookItem> {
    return try {
        val file = getBookFile(context)
        file.readLines()
            .filter { it.isNotBlank() }
            .mapNotNull { line ->
                val parts = line.split("|")
                if (parts.size >= 2) {
                    val title = parts[0].trim()
                    val author = parts[1].trim()
                    val rackno = if (parts.size >= 4) parts[2].trim() else if (parts.size == 3 && !parts[2].trim().toBooleanStrictOrNull().let { it != null }) parts[2].trim() else ""
                    val isBorrowed = if (parts.size >= 4) {
                        parts[3].trim().toBoolean()
                    } else if (parts.size == 3 && parts[2].trim().toBooleanStrictOrNull() != null) {
                        parts[2].trim().toBoolean()
                    } else false

                    BookItem(
                        title = title,
                        author = author,
                        rackno = rackno,
                        isBorrowed = isBorrowed
                    )
                } else null
            }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

private fun updateBookBorrowedStatus(context: Context, targetBook: BookItem, isBorrowed: Boolean): List<BookItem> {
    return try {
        val books = readBooksFromFile(context).map { book ->
            if (book.title == targetBook.title && book.author == targetBook.author && book.rackno == targetBook.rackno) {
                book.copy(isBorrowed = isBorrowed)
            } else {
                book
            }
        }
        val file = getBookFile(context)
        val content = books.joinToString("\n") { "${it.title}|${it.author}|${it.rackno}|${it.isBorrowed}" }
        file.writeText(if (content.isNotEmpty()) "$content\n" else "")
        books
    } catch (e: Exception) {
        e.printStackTrace()
        readBooksFromFile(context)
    }
}

@Composable
fun HomeScreen(
    onNavigateToPage1: () -> Unit,
    onNavigateToPage2: () -> Unit,
    onNavigateToPage3: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Library Management",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onNavigateToPage1,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        ) {
            Text("Create Book", fontSize = 18.sp)
        }

        Button(
            onClick = onNavigateToPage2,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        ) {
            Text("Book List", fontSize = 18.sp)
        }

        Button(
            onClick = onNavigateToPage3,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp)
        ) {
            Text("Search Book", fontSize = 18.sp)
        }
    }
}

@Composable
fun Book(onBack: () -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var rackno by remember { mutableStateOf("") }
    var isBorrowed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add New Book",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Book Title") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author Name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = rackno,
            onValueChange = { rackno = it },
            label = { Text("Rack No") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Is Borrowed?", fontSize = 16.sp)
            Switch(
                checked = isBorrowed,
                onCheckedChange = { isBorrowed = it }
            )
        }

        Button(
            onClick = {
                if (title.isBlank() || author.isBlank()) {
                    Toast.makeText(context, "Please enter both Title and Author", Toast.LENGTH_SHORT).show()
                } else {
                    val success = saveBookToFile(context, title.trim(), author.trim(), rackno.trim(), isBorrowed)
                    if (success) {
                        Toast.makeText(context, "Book saved successfully!", Toast.LENGTH_SHORT).show()
                        title = ""
                        author = ""
                        rackno = ""
                        isBorrowed = false
                    } else {
                        Toast.makeText(context, "Failed to save book", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Text("Save Book", fontSize = 16.sp)
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
fun BookList(onBack: () -> Unit) {
    val context = LocalContext.current
    var books by remember { mutableStateOf(readBooksFromFile(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Book List",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (books.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No books found in library.", fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books) { book ->
                    BookCard(
                        book = book,
                        onToggleBorrowed = { newStatus ->
                            books = updateBookBorrowedStatus(context, book, newStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Search(onBack: () -> Unit) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var filterOnlyBorrowed by remember { mutableStateOf(false) }
    var allBooks by remember { mutableStateOf(readBooksFromFile(context)) }

    val filteredBooks = allBooks.filter { book ->
        val matchesQuery = searchQuery.isBlank() ||
                book.title.contains(searchQuery, ignoreCase = true) ||
                book.author.contains(searchQuery, ignoreCase = true) ||
                book.rackno.contains(searchQuery, ignoreCase = true)
        val matchesBorrowedFilter = !filterOnlyBorrowed || book.isBorrowed
        matchesQuery && matchesBorrowedFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Search Books",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by Title, Author, or Rack No") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter Only Borrowed",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Switch(
                checked = filterOnlyBorrowed,
                onCheckedChange = { filterOnlyBorrowed = it }
            )
        }

        if (filteredBooks.isEmpty()) {
            Text(
                text = if (searchQuery.isBlank() && !filterOnlyBorrowed) "Type in search box or switch filter to find books." else "No matching books found.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredBooks) { book ->
                    BookCard(
                        book = book,
                        onToggleBorrowed = { newStatus ->
                            allBooks = updateBookBorrowedStatus(context, book, newStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookCard(
    book: BookItem,
    onToggleBorrowed: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "By: ${book.author}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (book.rackno.isNotBlank()) {
                    Text(
                        text = "Rack No: ${book.rackno}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (book.isBorrowed) "Status: Borrowed" else "Status: Available",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (book.isBorrowed) Color(0xFFD32F2F) else Color(0xFF388E3C)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Borrowed",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Switch(
                        checked = book.isBorrowed,
                        onCheckedChange = { onToggleBorrowed(it) }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { onToggleBorrowed(!book.isBorrowed) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (book.isBorrowed) Color(0xFF388E3C) else Color(0xFFD32F2F)
                    )
                ) {
                    Text(text = if (book.isBorrowed) "Return" else "Borrow")
                }
            }
        }
    }
}
