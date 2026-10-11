package com.stadiolinks.kitabu.ui.catalog

import android.widget.Toast
import androidx.collection.emptyLongSet
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stadiolinks.kitabu.R
import com.stadiolinks.kitabu.data.local.SampleBooks
import com.stadiolinks.kitabu.data.local.entity.BookEntity
import com.stadiolinks.kitabu.domain.BookingRules
import com.stadiolinks.kitabu.domain.Schools
import com.stadiolinks.kitabu.ui.theme.KitabuTheme
import com.stadiolinks.kitabu.ui.theme.SchoolCommerce
import com.stadiolinks.kitabu.ui.theme.SchoolLaw
import com.stadiolinks.kitabu.ui.theme.schoolColor

/**
 * The link between the catalog and its ViewModel.
 * It listens to the ViewModel's state and hands it to CatalogScreen, and passes
 * the user's actions back. Only this function knows the ViewModel exists.
 */
@Composable
fun CatalogRoute(
    viewModel: CatalogViewModel = viewModel(factory = CatalogViewModel.Factory)
) {
    // collectAsStateWithLifecycle stops listening while the app is in the background
    val books by viewModel.books.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    CatalogScreen(
        books = books,
        query = query,
        onQueryChange = viewModel::onQueryChange,
        onReserve = viewModel::reserveBook
    )
}

/**
 * The catalog: a search box above a scrolling list of books.
 * It only draws what it is given, which is why it also works in the preview.
 */
@Composable
fun CatalogScreen(
    books: List<BookEntity>,
    query: String,
    onQueryChange: (String) -> Unit,
    onReserve: (bookId: Long, userName: String, days: Int) -> Unit
) {
    // The book whose reserve dialog is open. Null means no dialog is showing.
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val borrowedMessage = stringResource(R.string.message_book_borrowed)
    val onBooksClick: (BookEntity) -> Unit ={ book ->
        focusManager.clearFocus()
        if(book.isAvailable){
            selectedBook = book
            } else {
            Toast.makeText(context, borrowedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Column {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text(stringResource(R.string.search_label)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // The key lets the list tell books apart, so it only redraws the card that changed
        if (query.isBlank()) {
            // Browsing: one shelf per school
            LazyColumn {
                items(Schools.all) { school ->
                    val schoolBooks = books.filter { it.category == school }
                    BookShelf(
                        schoolName = school,
                        books = schoolBooks,
                        onBooksClick = onBooksClick
                    )
                }
            }
        } else {
            // Searching: a flat list of the matches
            LazyColumn {
                items(books, key = { book -> book.bookId }) { book ->
                    BookCard(
                        title = book.title,
                        author = book.author,
                        category = book.category,
                        isAvailable = book.isAvailable,
                        onClick = { onBooksClick(book) }
                    )
                }
            }
        }
    }

    // Shown on top of the list whenever a book has been picked
    selectedBook?.let { book ->
        ReserveDialog(
            book = book,
            onDismiss = { selectedBook = null },
            onReserve = { userName, days ->
                onReserve(book.bookId, userName, days)
                selectedBook = null
            }
        )
    }
}

/** One book in the list: title and status on the first line, then author and category. */
@Composable
fun BookCard(
    title: String,
    author: String,
    category: String,
    isAvailable: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // weight(1f) gives the title all the spare width, which pushes the badge to the right
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                AvailabilityBadge(isAvailable = isAvailable)
            }
            Text(text = author, style = MaterialTheme.typography.bodyMedium)
            Text(text = category,
                style = MaterialTheme.typography.bodyMedium,
                color = schoolColor(category)
            )
        }
    }
}

/** A coloured pill: green when the book can be reserved, orange when someone has it. */
@Composable
fun AvailabilityBadge(isAvailable: Boolean) {
    Box(
        // Background before padding, so the colour reaches around the text
        modifier = Modifier
            .background(
                color = if (isAvailable) SchoolCommerce else SchoolLaw,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = stringResource(if (isAvailable) R.string.badge_available else R.string.badge_borrowed),
            color = Color.Black
        )
    }
}

/** Asks who is borrowing [book] and for how long, before it is reserved. */
@Composable
fun ReserveDialog(
    book: BookEntity,
    onDismiss: () -> Unit,
    onReserve: (userName: String, days: Int) -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var days by remember { mutableIntStateOf(BookingRules.RENTAL_DURATIONS_DAYS.first()) }
    val isNameValid = BookingRules.isValidName(userName)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = book.title) },
        text = {
            Column {
                OutlinedTextField(
                    value = userName,
                    // Digits and symbols are thrown away as they are typed
                    onValueChange = { userName = BookingRules.cleanNameInput(it) },
                    label = { Text(stringResource(R.string.reserve_name_label)) },
                    supportingText = {
                        Text(stringResource(R.string.reserve_name_hint, BookingRules.MIN_NAME_LENGTH))
                    },
                    // Only complain once the user has started typing
                    isError = userName.isNotEmpty() && !isNameValid,
                    singleLine = true
                )
                Text(
                    text = stringResource(R.string.reserve_duration_label),
                    modifier = Modifier.padding(top = 16.dp)
                )
                // One chip per allowed loan length; the chosen one is highlighted
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BookingRules.RENTAL_DURATIONS_DAYS.forEach { option ->
                        FilterChip(
                            selected = option == days,
                            onClick = { days = option },
                            label = { Text(stringResource(R.string.duration_days, option)) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            // Stays greyed out until the name is acceptable
            Button(
                onClick = { onReserve(userName, days) },
                enabled = isNameValid
            ) {
                Text(stringResource(R.string.action_reserve))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
/** A book drawn as a cover, for the sideways-scrolling shelves. */
@Composable
fun BookCover(
    title: String,
    author: String,
    category: String,
    isAvailable: Boolean,
    onClick: () -> Unit,
) {
    val coverColor = schoolColor(category)
    // Dark text on pale colours, white text on deep ones, so the title is always readable
    val titleColor = if (coverColor.luminance() > 0.5f) Color.Black else Color.White

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .padding(end = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(coverColor)
                .padding(10.dp)
        ) {
            Text(
                text = title,
                color = titleColor,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
            AvailabilityBadge(isAvailable = isAvailable)
        }

    }
}
@Composable
fun BookShelf(
    schoolName: String,
    books: List<BookEntity>,
    onBooksClick: (BookEntity) -> Unit
    ) {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = schoolName,
            style = MaterialTheme.typography.titleMedium,
            color = schoolColor(schoolName),
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 10.dp)) {
            items(books) { book ->
                BookCover(
                    title = book.title,
                    author = book.author,
                    category = book.category,
                    isAvailable = book.isAvailable,
                    onClick = { onBooksClick(book) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogScreenPreview() {
    // Sample books have not been saved yet, so they all share ID 0.
    // The list needs a different ID per book, so number them here.
    val previewBooks = SampleBooks.all.mapIndexed { index, book ->
        book.copy(bookId = index + 1L, isAvailable = index != 1)
    }
    KitabuTheme {
        CatalogScreen(
            books = previewBooks,
            query = "",
            onQueryChange = {},
            onReserve = { _, _, _ -> }
        )
    }
}
@Preview
@Composable
fun BookCoverPreview() {
    KitabuTheme {
        BookCover(
            title = "Computer Networking: A Top-Down Approach",
            author = "James F. Kurose",
            category = Schools.INFORMATION_TECHNOLOGY,
            isAvailable = true,
            onClick = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun BookShelfPreview() {
    val itBooks = SampleBooks.all.filter { it.category == Schools.INFORMATION_TECHNOLOGY }
    KitabuTheme {
        BookShelf(
            schoolName = Schools.INFORMATION_TECHNOLOGY,
            books = itBooks,
            onBooksClick ={}
        )
    }
}
