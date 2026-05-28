# Nudgie App Presentation Script
**Duration:** ~10 Minutes
**Speaker:** Developer

---

## 1. Introduction (0:00 - 1:00)
- **Hook:** "Hello everyone. Today I'm presenting **Nudgie**, a habit-tracking application designed to help users stay productive while managing their digital wellbeing."
- **Team:** Developed by the Night Owl Crew.
- **Core Purpose:** Nudgie isn't just a list; it’s an interactive dashboard where a digital 'buddy' nudges you toward your goals and monitors your screen time to ensure a healthy balance.

## 2. Technical Foundation & SDKs (1:00 - 2:30)
- **Language:** Built entirely with **Kotlin**, utilizing modern declarative UI with **Jetpack Compose**.
- **SDK Versions:**
    - **Min SDK: 26 (Android 8.0 Oreo)** - Ensures compatibility with the vast majority of active Android devices.
    - **Target & Compile SDK: 36 (Android Baklava)** - We are targeting the latest Android 15/16 previews to leverage the newest platform optimizations and security features.
- **Key Libraries:**
    - **Room:** For robust local data persistence.
    - **WorkManager:** For reliable background tasks (specifically screen time synchronization).
    - **Material 3:** For a modern, accessible UI/UX.

## 3. Permissions (2:30 - 3:30)
To function effectively, Nudgie requires specific permissions:
- **INTERNET:** Used for fetching external resources like fonts or potential cloud sync.
- **PACKAGE_USAGE_STATS:** This is a sensitive permission. It allows us to track how much time you spend in other apps, which powers our screen-time "nudge" feature.
- **POST_NOTIFICATIONS:** Essential for the app to actually "nudge" the user when they've been scrolling too long.
- **RECEIVE_BOOT_COMPLETED:** Allows the app to reschedule its background monitoring workers automatically if the phone is restarted.

## 4. Project Structure & Files (3:30 - 5:00)
- **NudgieApplication.kt:** The entry point where we initialize our database and repository.
- **MainActivity.kt:** The single activity hosting our Compose Navigation and the main UI entry point.
- **UI Package:** 
    - `NudgieDashboard.kt`: The main screen.
    - `AnimatedAlien.kt` & `AnimatedKitty.kt`: Interactive characters that provide feedback based on your progress.
    - `NudgieViewModel.kt`: The "brain" of the UI, managing state and communicating with the repository.
- **Workers:**
    - `ScreenTimeSyncWorker.kt`: Runs in the background to fetch usage data from the system without draining the battery.

## 5. The Data Layer: Entities & DAO Interaction (5:00 - 8:00)
*This is the heart of our data persistence.*

- **Entities (The Blueprints):**
    - **HabitEntity:** Represents a user-defined habit (e.g., "Drink Water"). It stores the title, icon, and target frequency.
    - **HabitLogEntity:** This is a 'child' table. Every time you complete a habit, a log is created. 
    - **Relationship:** We use a **ForeignKey** between them. A key design choice here is `onDelete = ForeignKey.SET_NULL`. This ensures that even if a user deletes a Habit, the historical records of when they were active stay in the database for long-term analytics, now labeled as "orphaned logs."

- **The Relationship Class:**
    - **HabitWithLogs:** Using Room's `@Embedded` and `@Relation` annotations, we can fetch a Habit and all its associated logs in a single transaction, making our UI updates highly efficient.

- **The DAO (The Bridge):**
    - **HabitDao:** This interface defines how the code talks to the database.
    - It uses **Kotlin Coroutines (`suspend` functions)** for writing data (Insert/Delete) to keep the UI thread smooth.
    - It returns **Flows** for queries. This means the UI automatically "reacts" and updates the moment any data in the database changes.

## 6. Architecture: Repository Pattern (8:00 - 9:00)
- We use `HabitRepository` and `HabitRepositoryImpl` to abstract the data source.
- Whether data comes from the Room database or (in the future) an API, the ViewModel doesn't care. It only talks to the Repository. This makes the code modular and easy to test.

## 7. Conclusion (9:00 - 10:00)
- **Summary:** Nudgie combines a reactive UI, a sophisticated local database structure with orphaned log preservation, and background system monitoring to create a cohesive productivity tool.
- **Closing:** "By leveraging the latest Android 36 APIs and a clean MVVM architecture, we've built a foundation that is both performant and maintainable. Thank you!"
