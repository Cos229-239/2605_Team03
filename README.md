# Nudgie — Cozy Lifestyle & Habit Tracker

## Team Configuration
* **Team Name:** Night Owl Crew (Cycle 2605)
* **Team Members:** 
* Kenneth William Hanks
* Jared Delgato
* Alec Duncan
* Bailey Kimmel
---

## Project Overview & Intent
Nudgie is a modern, responsive Android application built using Kotlin and Jetpack Compose. The platform is engineered to encourage mindful, personal development across 5 core lifestyle balance categories:
1. **Body & Vitality:** Hydration, medication tracking, and somatic routines.
2. **Mind & Space:** Daily mindfulness, journaling, and workspace organization.
3. **Daily Rhythms:** Consistent household management, study blocks, and productivity tasks.
4. **Self-Care Rituals:** Sleep prep, hygiene habits, and electronic device boundaries.
5. **Connections:** Family check-ins, plant cultivation, and pet care tracking.

The system utilizes an automated background integration workflow to push build metrics and development updates seamlessly to a central monitoring pipeline via GitHub Actions.

---

## Accomplished System Architecture

- **Gamified Pet Dashboard:** Integrated a Retro-Space themed pet mascot ("Zorg") that reacts to habit completion. Includes dynamic stat tracking for Happiness, Energy, and XP levels.
- **Dynamic Task Management:** Implemented a categorized task list with archive/restore functionality, XP rewards, and full-card click completion.
- **Digital Balance Integration:** Created a high-visibility Digital Balance tracker on the main dashboard with a quick-access double-tap gesture to adjust daily screen time goals.
- **Dynamic App Icon:** Integrated support for multiple app icons, allowing users to choose from the 4 primary nudgie mascots to represent the application on their home screen.
- **Main Navigation Router Integration:** Updated the dashboard routing tree inside `NudgieDashboard.kt` to bind all core screens (Home, Pet, Profile, Stats, Tasks, Settings) dynamically.
- **UI Contrast & Accessibility Cleanup:** Refactored theme color logic to ensure high contrast for all 5 core balance categories across different app themes (Retro-Space, Cyberpunk, Steampunk, Goth).

### 🛠️ Architecture & Data Layer Infrastructure
* **Room DAO Persistence Layer:** Native Data Access Objects are fully established to govern structured SQL transaction threads securely over the application lifecycle.
* **Repository Pattern & MVVM Architecture:** Built a clean `HabitRepository` bridge to feed local database events into a central `NudgieViewModel`, exposing safe, Unidirectional Data Flow states (`uiState.activities`) to visual screens.
* **Navigation & Deep Linking:** Implemented query-parameter based navigation to allow direct access to specific UI states (e.g., opening the screen time slider directly from the dashboard).
* **Branch Strategy & Individual Workspace:** The project maintains isolated personal development branches to manage experimental feature sets:
    * `baileypersonal` — Implementing Pet customization modules (Food, Play, Bath interactions) and mascot animation state tracking.
    * `alecpersonal` — Theme selection engine (Cyberpunk, Steampunk, Goth), UI accessibility/contrast cleanup, and **App Icon Customization** (allowing the app icon to be any of the 4 core nudgies instead of just the default blue mascot).
    * `jaredpersonal` — Notification system architecture, Daily Greetings, and Pet Dialogue (Buddy) interaction components.
    * `kennethpersonal` — Core Data Persistence (Room), Repository Pattern, MVVM state management, and Digital Balance/Screen Time logic.

### 🎨 Feature Modules
* **Digital Balance Widget:** Integrated an operational slider scale (1-12 hours) with an "Unlimited ∞" mode for flexible screen time goal setting.
* **Tasks Content Hub:** A dedicated space for daily routines with a sticky progress header, category filtering chips, and persistent "Add Task" dialogs with emoji selection.
* **Nudgie Splash Screen:** A brand loading activity featuring a vertical gradient background, the Nudgie logo, and a rotating cast of random mascots (Nudgie, Trashpanda, Dragon) on each launch.
* **Settings Panel:** Filtered habit items dynamically under the 5 life-balance headers, allowing for template-based habit creation and theme switching.
* **Cozy Emoji Shortcut Selector:** Horizontal tray enabling users to assign symbols (`💧`, `💊`, `🧘`, `🪥`, `☕`, etc.) to custom entries.

---

## Current Workspace Roadmap

### 🗂️ In Progress
* **Pet Customization:** Implementing "Food", "Play", and "Bath" interactions for the Nudgie mascot.
* **Stats Tracking:** Building the visual stream for long-term habit data and currency balance.

### 📋 Pending Tasks
* Design the Stateless Learn UI Layout Shell
* Implement Educational Category Row Filters
* Populate Static Balance Insight Templates
* Integrate Pixel Art App Launcher Icon Configuration (`nudgie.png`)

---

## Prototype Sandboxes
* `index.html` - Local browser-based student roster editor landing interface.
* `roster-v2-sample.json` - High-level sample profile model structure used to reference team properties.
