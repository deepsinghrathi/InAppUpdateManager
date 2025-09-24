# InAppUpdateManager

A **plug-and-play Android dependency** for seamless real-time app updates **without relying on Play Store or third-party services**.  
Works with any backend or shared hosting by fetching update info from a simple JSON file on your own server.

---

## Features

- Real-time update checking from your own server  
- Display new version number and detailed “What’s New” changelog  
- Show download progress of the update APK  
- Track installation progress  
- Automatic permission management (including unknown sources install permission)  
- Easy integration with minimal setup  
- Works on shared hosting and any backend serving static or dynamic JSON  
- Fully customizable UI to match your app style

---

## How It Works

1. Host an `update.json` file on your server with the latest version info, changelog, and APK URL.  
2. Add `InAppUpdateManager` dependency to your Android app.  
3. Configure the URL pointing to your hosted `update.json`.  
4. The library checks for updates at app launch or periodically, shows the update screen if a new version is available, downloads the APK, and manages installation with progress tracking.

---

## Sample update.json format

```json
{
  "latest_version": "2.0.1",
  "mandatory": true,
  "update_url": "https://yourdomain.com/downloads/app-v2.0.1.apk",
  "title": "Version 2.0.1 Released!",
  "description": "Bug fixes and performance improvements."
}

## Installation

Add the dependency to your `build.gradle` file:

```gradle
implementation 'com.yourdomain:inappupdatemanager:1.0.0'

## Usage

To integrate `InAppUpdateManager` into your Android app, follow these steps:

1. **Initialize the update manager** by providing the app context.
2. **Set the URL** pointing to your hosted update JSON file.
3. **Call the checkForUpdate() method** to trigger the update check.

```kotlin
// Create an instance of the update manager
val updateManager = InAppUpdateManager(context)

// Set the URL of your update JSON hosted on your server
updateManager.setUpdateJsonUrl("https://yourdomain.com/update.json")

// Check for updates (call this at app launch or whenever appropriate)
updateManager.checkForUpdate()

## Permissions

The following permissions are required for **InAppUpdateManager** to function correctly:

### 1. INTERNET Permission

```xml
<uses-permission android:name="android.permission.INTERNET" />


<!-- Required to request permission to install APKs from unknown sources (Android 8.0+) -->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />


