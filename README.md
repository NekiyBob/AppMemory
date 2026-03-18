# AppMemory

AppMemory is a Java Android application for creating and managing notes with smart reminders.  
The project was built as a study project with a focus on Java, local data persistence, and reminder scheduling.

## Features

- Create, edit, and delete notes
- Store note title and text
- Add extra note attributes such as color, image, and web link
- Save notes locally on the device
- Schedule multiple reminders for a note
- Simple and clean mobile interface

## Tech Stack

- Java
- Android SDK
- Room
- SQLite
- AlarmManager
- BroadcastReceiver

## Project Structure

- `activities/` — screens and UI logic
- `dao/` — data access layer
- `database/` — Room database configuration
- `entities/` — application data models
- `receiver/` — reminder handling

## How It Works

The application allows users to store notes locally and schedule reminders after a note is created.  
It combines a simple note-taking workflow with automated reminder logic inspired by spaced repetition ideas.

## Screenshots


### Home screen
<img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/7962a3e2-a416-4df0-a1ea-7fa49ea48fc2" />

### Create note
<img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/a9211a77-961a-4765-afaa-b361661a3381" />


### Note details
<img width="360" height="800" alt="image" src="https://github.com/user-attachments/assets/0cbf6f57-3220-4a02-b838-9682ab8bd4cf" />


### Reminder example
<img width="360" height="116" alt="image" src="https://github.com/user-attachments/assets/9426a537-8488-43c8-80c0-fb63a0f49c62" />
