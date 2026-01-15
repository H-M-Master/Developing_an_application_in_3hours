# Wellness Tracker App

## Overview
A simple Android app to record daily steps, mood, and notes, then review weekly progress. Data is stored locally with Room and SharedPreferences. Built for the EE5415 programming test context.

## Key Features
- Main screen to input steps, mood (Energic, Normal, Tired), optional remark, and date (defaults to today).
- Displays greeting with nickname and shows today summary against the daily step goal.
- Saves or updates records in Room; duplicate dates aggregate steps, refresh mood, and append remarks.
- History screen lists records (most recent first) and shows weekly goal completion rate (Sunday start). Tapping an item can show the remark.
- Settings to update nickname and daily step goal (SharedPreferences) with changes reflected on return.
- Clear All option wipes Room data and resets SharedPreferences to defaults, confirming with a Toast.

## Technical Stack
- Language: Java (Android)
- Persistence: Room for wellness records; SharedPreferences for nickname and daily goal
- UI: Activities with standard Android views, Toolbar menu actions

## Data Model
```
WellnessRecord(
  date: String (e.g., "2025-03-15"),
  steps: Int,
  mood: String,
  note: String
)
```

SharedPreferences keys (defaults):
- nickname: "Guest"
- dailyStepGoal: 8000

## Screens
- MainActivity: input form, today summary, Save Today button with validation and Toast feedback.
- HistoryActivity: weekly completion rate text, list of records, optional remark dialog/Toast on item tap, back/home navigation.
- SettingsActivity: edit nickname and daily goal; back/home navigation.

## Build and Run
1. Open the project in Android Studio (preferred IDE for this app).
2. Use the included Gradle wrapper to sync and build (`./gradlew assembleDebug` if needed).
3. Run on an emulator or physical device with a recent Android SDK.

## Usage Notes
- Steps input must be a positive integer before saving.
- If a date already exists, new steps are added to the existing value; mood is replaced; remarks are appended.
- Weekly completion rate counts days meeting the goal from Sunday to Saturday.
- Clear All removes Room records and resets nickname and daily goal to defaults.

## Reference Constraints (from the test brief)
- Time box: 3 hours; platform: Android; language: Kotlin or Java allowed (this project uses Java).
- Reuse of previous templates is allowed; web search for documentation is allowed.
- AI tools were disallowed in the original test conditions; Android Studio was the only permitted IDE.
- Priority order: functionality first, then UI.

## Project Structure (high level)
- app/src/main/java/com/example/programming_quiz: activities, data layer (Room entities, DAO, repository)
- app/src/main/res: layouts, menus, drawables, strings, themes
- Gradle wrapper and configuration in the project root
