# Kitabu: Library Book Rental & Reservation System

**MADB372 Mobile Application Development B, SF1 project**
Corbin Benjamin · STADIO School of Information Technology

Kitabu ("book" in Swahili) is an Android app for STADIO students. You can see what's on the shelf, reserve a book, collect it, and keep track of when it's due, all offline.

Built with **Kotlin**, **Jetpack Compose (Material 3)**, **Room**, **MVVM**, **Coroutines** and **Flow**.

---

## Features

**Catalogue**
- Books on one swipeable shelf per STADIO school, each drawn as a cover in the school's colour
- Green **Available** / orange **Borrowed** badges that update the moment something changes
- Search by title or author (SQL `LIKE`) with a "no books match" message
- **All / Available / Borrowed** filter chips (backed by `getBooksByAvailability()`)
- School chips that jump straight to a shelf, and "4 of 5 available" on each shelf
- Reserve dialog with name validation and 7 / 14 / 21-day loans, plus a pop-up confirming the reservation once the database has saved it

**My bookings**
- Pending and active bookings with reserved date, return date and days left
- Days left is colour-coded: green, orange when 3 days or less, red when overdue
- **Collect** (PENDING → ACTIVE), **Renew** (+7 days, max 42), **Return**, **Cancel** (pending only), each with an icon
- A "How Kitabu works" guide for first-time users

**Look and feel**
- Dark theme with the eight STADIO school colours and a school-colour strip under the top bar
- Lora (titles) and Inter (body) fonts, bundled so they work offline
- Custom app icon and splash screen

## Screenshots

| Catalog | Search | Reserve | My bookings |
|---|---|---|---|
| <img src="docs/screenshots/01-catalog.png" width="200"> | <img src="docs/screenshots/02-search.png" width="200"> | <img src="docs/screenshots/03-reserve-dialog.png" width="200"> | <img src="docs/screenshots/04-bookings-pending.png" width="200"> |

<!-- Add final screenshots here: 05-filters.png, 06-reserved-popup.png, 07-guide.png, 08-active-renewed.png -->

### CRUD

| | Where in the app | DAO method |
|---|---|---|
| **Create** | Reserve a book | `BookingDao.reserveBook` (`@Transaction`), `BookDao.insertBooks` (sample data) |
| **Read** | Catalogue, search, filters, My bookings | `BookDao.searchBooks`, `BookDao.getBooksByAvailability`, `BookingDao.getBookingsWithBook` (JOIN) |
| **Update** | Collect, Renew, Return | `BookingDao.updateStatus(id, from, to)`, `updateReturnDeadline`, `returnBook` (`@Transaction`) |
| **Delete** | Cancel a pending booking | `BookingDao.cancelBooking` (`@Transaction`) |

## Architecture

MVVM with a repository and manual dependency injection.

![Architecture](docs/architecture.png)

```
Compose screens ──taps──▶ ViewModel ──▶ Repository ──▶ DAO ──▶ Room (SQLite)
       ▲                       │
       └──── StateFlow ◀──── Flow ◀┘
```

- **UI (`ui/`)**: each screen is a `Route` (collects state with `collectAsStateWithLifecycle()`) and a stateless `Screen`.
- **ViewModels**: Room `Flow`s become `StateFlow` via `stateIn(WhileSubscribed(5000))`; writes run in `viewModelScope.launch`.
- **Repository + rules (`data/repository`, `domain/`)**: one place for data access; `BookingRules` checks names and loan lengths.
- **Data (`data/local`)**: `KitabuDatabase`, `BookDao`, `BookingDao`, entities, type converters.
- **DI (`di/`)**: `AppContainer` builds the database and repository once.

### Database

![ERD](docs/erd.png)

- `BookEntity`: `bookId` (PK), `title`, `author`, `category`, `isAvailable` (default `true`)
- `BookingEntity`: `bookingId` (PK), `bookOwnerId` (FK → `bookId`, **CASCADE**, indexed), `userName`, `bookingDate`, `returnDeadline`, `status` (`PENDING` / `ACTIVE` / `RETURNED`)
- `@Database(version = 1)`, `@TypeConverters` (enum ↔ TEXT, `Date` ↔ millis), `@Volatile` + `synchronized` singleton, sample books added on first run on `Dispatchers.IO`
- Reserve, return and cancel run in `@Transaction`s, and status changes only happen from the expected status

### Project structure

```
app/src/main/java/com/stadiolinks/kitabu
├── data/
│   ├── local/          KitabuDatabase, Converters, SampleBooks
│   │   ├── dao/        BookDao, BookingDao
│   │   ├── entity/     BookEntity, BookingEntity, BookingStatus
│   │   └── model/      BookingWithBook
│   └── repository/     LibraryRepository
├── di/                 AppContainer
├── domain/             BookingRules, Schools
└── ui/
    ├── catalog/        CatalogScreen, CatalogViewModel
    ├── bookings/       BookingsScreen, BookingsViewModel
    ├── navigation/     KitabuApp (NavHost)
    └── theme/          Color, SchoolColor, StadioStrip, Theme, Type
app/src/main/res/font   Lora and Inter (Google Fonts, OFL)
design/                 Wireframes (SVG)
docs/                   Diagrams and screenshots for this README
```

## Design

Wireframes were drawn before development (`design/kitabu-wireframes.svg`). The full design story (palette, typography, mind map, Figma mock-up and how the app changed) is in the SF1 document.

![Wireframes](docs/wireframes-v1.png)

## Getting started

**Needs:** Android Studio (Ladybug or newer), JDK 17+, an emulator or phone on **API 26+**.

1. `git clone https://github.com/UserBac0n03/Kitabu_MADB372_SF1.git`
2. Open the folder in Android Studio (**File → Open**) and let Gradle sync.
3. Pick a device and press **Run ▶**.

The database is created and filled with sample books on first launch.

## Tech stack

| | |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Database | Room 2.8 (SQLite) with KSP |
| Async | Coroutines, Flow, StateFlow |
| Architecture | MVVM + repository, manual DI |
| Min / target SDK | 26 / 37 |

## What's next

"Back on …" dates for borrowed books, a next-in-line queue, QR code collection at the desk, due-date reminders, logins, multiple copies per title, and unit tests for `BookingRules`.

## AI use

Claude (Anthropic) was used for review, bug fixing, polish ideas and help with this README and the project document, as allowed (Amber) by the assignment's AI Traffic Light Matrix. All prompts are in the submitted document.
