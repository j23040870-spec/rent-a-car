# Rent a Car – Android Assignment 2

A proof-of-concept Android app for a car rental company built in Kotlin for COS30017 (Software Development for Mobile Devices). Users can browse five cars, rent them using an in-app credit system, mark favourites, search, sort, and toggle dark mode.

## Features

- Browse 5 cars with details: name, model, year, rating, kilometres, daily cost
- Rent vehicles with a credit balance (500 cr) and per-rental limit (≤400 cr)
- Mark favourites with heart icon or long-press; quick‑access favourites strip
- Search by name or model, sort by rating/year/cost
- Dark mode toggle (Switch) that works across both activities
- All data held in memory (no persistent storage)
- Espresso UI tests covering core functionality

## Tech Stack

- **Language:** Kotlin
- **UI:** XML layouts with Material Design Components (CardView, Slider, RatingBar, Switch)
- **Architecture:** Two activities, in‑memory singleton repository, manual Parcelable
- **Testing:** Espresso (10 test cases)
- **Build:** Gradle with ViewBinding

## How to Run

1. Clone the repository
2. Open in Android Studio (Ladybug or later)
3. Sync Gradle
4. Run on an emulator or device (min SDK 24)

## Report

The full report documenting design decisions, UX sketches, feature implementation, testing, and challenges is available upon request (submitted as a PDF for the assignment).
