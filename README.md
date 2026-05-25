# Nudgie — Cozy Lifestyle & Habit Tracker

## Team Configuration
* **Team Name:** Night Owl Crew (Cycle 2605)
* **Team Members:** 
* Kenneth William Hanks
* Jared Delgado 
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

- **Main Navigation Router Integration (Card 6):** Updated the dashboard routing tree inside `NudgieDashboard.kt` to bind index position `4` to the completed `SettingsScreen` wrapper so that the visual configuration screen loads dynamically in place of placeholder strings.
- **UI Contrast & Accessibility Cleanup:** Refactored theme color logic to ensure high contrast for all 5 core balance categories. Fixed Steampunk "invisible text" issue on habit cards and improved progress bar visibility.
- **IDE Noise Reduction:** Silenced widespread Kotlin analysis errors originating from the code snippet fragments in the project documentation.

### 🛠️ Architecture & Data Layer Infrastructure
* **Room DAO Persistence Layer:** Native Data Access Objects are fully established to govern structured SQL transaction threads securely over the application lifecycle.
* **Repository Pattern & MVVM Architecture:** Built a clean `HabitRepository` bridge to feed local database events into a central `NudgieViewModel`, exposing safe, Unidirectional Data Flow states (`uiState.activities`) to visual screens.
* **Isolated Feature Lineage:** Renamed and consolidated all workspace history into an organized, personal tracking matrix (`kenneth-personal/`) on the remote team repository.

### 🎨 Settings Panel Module Engineering
* **Stateless UI Layout Shell:** Implemented isolated layout components inside `SettingsContent.kt` containing localized custom entry blocks and input text field capture layouts.
* **Animate Accordion Sections:** Filtered habit items dynamically under the 5 life-balance headers, wrapping them in smooth expanding and collapsing container cards.
* **Dynamic Form Validation:** State structures automatically enforce text constraint rules, disabling creation submit prompts if the input name is blank or empty, and rendering placeholder italicized blocks if a list section is empty.
* **Screen Time Digital Balance Widget:** Integrated an operational snapping slider scale bounded between 0 and 8+ hours to allow dynamic manual tracking of screen exposures.
* **Cozy Emoji Shortcut Selector:** Implemented a horizontal scrolling item tray enabling users to assign a clear visual symbol (`💧`, `💊`, `🧘`, `🪥`, `☕`, etc.) to custom entries upon database insertion.

---

## Current Workspace Roadmap

### 🗂️ In Progress
* **Coming Soon:** Placeholder for upcoming features.

### 📋 Pending Tasks
Design the Stateless Learn UI Layout Shell
Implement Educational Category Row Filters
Populate Static Balance Insight Templates
Bind Learn Panel to Live View Stream Data
Update Router Configuration for index 3
Integrate Pixel Art App Launcher Icon Configuration (`nudgie.png`)
Construct Brand Loading Splash Activity Layout Shell (`Nudgie-loadingsplash.jpg`)

### Everything below this was already here.

---

## Prototype Sandboxes
* `index.html` - Local browser-based student roster editor landing interface.
* `roster-v2-sample.json` - High-level sample profile model structure used to reference team properties.# Class Project Template

Start here. Keep this README updated with your team name, members, and a short plan.

Prototype pages:

- `index.html` - roster editor prototype landing page
- `roster-v2-sample.json` - sample editable roster shape for future class setup work
