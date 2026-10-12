package com.stadiolinks.kitabu.ui.bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stadiolinks.kitabu.R
import com.stadiolinks.kitabu.data.local.entity.BookingEntity
import com.stadiolinks.kitabu.data.local.entity.BookingStatus
import com.stadiolinks.kitabu.data.local.model.BookingWithBook
import com.stadiolinks.kitabu.domain.BookingRules
import com.stadiolinks.kitabu.domain.Schools
import com.stadiolinks.kitabu.ui.theme.KitabuTheme
import com.stadiolinks.kitabu.ui.theme.SchoolCommerce
import com.stadiolinks.kitabu.ui.theme.SchoolFashion
import com.stadiolinks.kitabu.ui.theme.SchoolLaw
import com.stadiolinks.kitabu.ui.theme.SchoolPublicSafety
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** The link between the bookings list and its ViewModel. */
@Composable
fun BookingsRoute(
    viewModel: BookingsViewModel = viewModel(factory = BookingsViewModel.Factory)
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()

    BookingsScreen(
        bookings = bookings,
        onCollect = viewModel::collectBooking,
        onRenew = viewModel::renewBooking,
        onReturn = viewModel::returnBook,
        onCancel = viewModel::cancelBooking
    )
}

/** The student's current bookings, or a short message when there are none. */
@Composable
fun BookingsScreen(
    bookings: List<BookingWithBook>,
    onCollect: (BookingWithBook) -> Unit,
    onRenew: (BookingWithBook) -> Unit,
    onReturn: (BookingWithBook) -> Unit,
    onCancel: (BookingWithBook) -> Unit
) {
    // Is the guide open. rememberSaveable so it doesn't reset on rotation
    var guideExpanded by rememberSaveable { mutableStateOf(false) }

    // Nothing booked yet, so show the guide open so new users know where to start
    if (bookings.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HowItWorksCard(expanded = true, onToggle = null)
            Text(
                text = stringResource(R.string.bookings_empty),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )
        }
        return
    }

    LazyColumn {
        // Guide stays at the top but closed, so it doesn't get in the way once you've got bookings
        item {
            HowItWorksCard(
                expanded = guideExpanded,
                onToggle = { guideExpanded = !guideExpanded }
            )
        }
        items(bookings, key = { item -> item.booking.bookingId }) { item ->
            BookingCard(
                item = item,
                onCollect = { onCollect(item) },
                onRenew = { onRenew(item) },
                onReturn = { onReturn(item) },
                onCancel = { onCancel(item) }
            )
        }
    }
}

/** One booking: the book, its dates, the days left, and the buttons that fit its stage. */
@Composable
fun BookingCard(
    item: BookingWithBook,
    onCollect: () -> Unit,
    onRenew: () -> Unit,
    onReturn: () -> Unit,
    onCancel: () -> Unit
) {
    val booking = item.booking
    // Worked out each time the card is drawn, so it is never out of date
    val daysLeft = BookingRules.daysRemaining(booking.returnDeadline).toInt()
    val canRenew = BookingRules.canRenew(booking.bookingDate, booking.returnDeadline)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(status = booking.status)
            }
            Text(
                text = stringResource(R.string.booking_author_category, item.author, item.category),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(text = stringResource(R.string.booking_booked_by, booking.userName))
            Text(text = stringResource(R.string.booking_reserved_on, formatDate(booking.bookingDate)))
            Text(text = stringResource(R.string.booking_return_by, formatDate(booking.returnDeadline)))
            Text(
                text = if (daysLeft >= 0) {
                    pluralStringResource(R.plurals.booking_days_left, daysLeft, daysLeft)
                } else {
                    pluralStringResource(R.plurals.booking_days_overdue, -daysLeft, -daysLeft)
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                // Green = fine, orange = due soon, red = overdue
                color = deadlineColor(daysLeft)
            )

            // A booking waiting for pickup can be collected or cancelled.
            // One the student already holds can be renewed or returned.
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                when (booking.status) {
                    BookingStatus.PENDING -> {
                        Button(onClick = onCollect) {
                            ButtonIcon(Icons.Filled.CheckCircle)
                            Text(stringResource(R.string.action_collect))
                        }
                        OutlinedButton(onClick = onCancel) {
                            ButtonIcon(Icons.Filled.Close)
                            Text(stringResource(R.string.action_cancel))
                        }
                    }
                    BookingStatus.ACTIVE -> {
                        // Greyed out once the book has been kept for the longest time allowed
                        Button(onClick = onRenew, enabled = canRenew) {
                            ButtonIcon(Icons.Filled.Refresh)
                            Text(stringResource(R.string.action_renew))
                        }
                        OutlinedButton(onClick = onReturn) {
                            ButtonIcon(Icons.Filled.Check)
                            Text(stringResource(R.string.action_return))
                        }
                    }
                    // Returned bookings are not listed, so there is nothing to show
                    BookingStatus.RETURNED -> Unit
                }
            }
            if (booking.status == BookingStatus.ACTIVE && !canRenew) {
                Text(
                    text = stringResource(R.string.booking_renewal_limit, BookingRules.MAX_LOAN_DAYS),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * "How Kitabu works" card for new users: what the app is for and the 3 steps.
 * Pass null for [onToggle] to keep it open with no Show/Hide button.
 */
@Composable
private fun HowItWorksCard(expanded: Boolean, onToggle: (() -> Unit)?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.guide_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (onToggle != null) {
                    TextButton(onClick = onToggle) {
                        Text(stringResource(if (expanded) R.string.guide_hide else R.string.guide_show))
                    }
                }
            }
            if (expanded) {
                Text(
                    text = stringResource(R.string.guide_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                GuideStep(
                    icon = Icons.Filled.Search,
                    title = R.string.guide_step_find_title,
                    body = R.string.guide_step_find_body
                )
                GuideStep(
                    icon = Icons.Filled.CheckCircle,
                    title = R.string.guide_step_reserve_title,
                    body = R.string.guide_step_reserve_body
                )
                GuideStep(
                    icon = Icons.Filled.DateRange,
                    title = R.string.guide_step_due_title,
                    body = R.string.guide_step_due_body
                )
            }
        }
    }
}

/** One step in the guide: icon, title, one line of text. */
@Composable
private fun GuideStep(icon: ImageVector, title: Int, body: Int) {
    Row(modifier = Modifier.padding(vertical = 6.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SchoolCommerce,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = stringResource(title), style = MaterialTheme.typography.titleSmall)
            Text(
                text = stringResource(body),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Colour for the days-left line, so you can see at a glance if a book's nearly due. */
private fun deadlineColor(daysLeft: Int): Color = when {
    daysLeft < 0 -> SchoolPublicSafety
    daysLeft <= BookingRules.DUE_SOON_DAYS -> SchoolLaw
    else -> SchoolCommerce
}

/** Icon in front of a button label. Uses Material's icon size and spacing. */
@Composable
private fun ButtonIcon(icon: ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(ButtonDefaults.IconSize)
    )
    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
}

/** A coloured pill showing the stage of a booking: yellow while waiting, green once collected. */
@Composable
fun StatusBadge(status: BookingStatus) {
    val badgeColor = when (status) {
        BookingStatus.PENDING -> SchoolFashion
        BookingStatus.ACTIVE -> SchoolCommerce
        BookingStatus.RETURNED -> Color.Gray
    }
    val label = when (status) {
        BookingStatus.PENDING -> R.string.status_pending
        BookingStatus.ACTIVE -> R.string.status_active
        BookingStatus.RETURNED -> R.string.status_returned
    }

    Text(
        text = stringResource(label),
        color = Color.Black,
        // Background before padding, so the colour reaches around the text
        modifier = Modifier
            .background(badgeColor, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

/** Turns a stored timestamp into a readable date such as "9 Oct 2026". */
private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(timestamp))

@Preview(showBackground = true)
@Composable
fun BookingsEmptyPreview() {
    KitabuTheme {
        BookingsScreen(
            bookings = emptyList(),
            onCollect = {},
            onRenew = {},
            onReturn = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookingsScreenPreview() {
    val now = System.currentTimeMillis()
    val sample = BookingWithBook(
        booking = BookingEntity(
            bookingId = 1,
            bookOwnerId = 1,
            userName = "Corbin",
            bookingDate = now,
            returnDeadline = BookingRules.deadlineFrom(now, 7)
        ),
        title = "Clean Code",
        author = "Robert C. Martin",
        category = Schools.INFORMATION_TECHNOLOGY
    )
    KitabuTheme {
        BookingsScreen(
            bookings = listOf(sample),
            onCollect = {},
            onRenew = {},
            onReturn = {},
            onCancel = {}
        )
    }
}
