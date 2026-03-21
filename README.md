# 🌐 EventSphere

> A Firebase-powered Event Management Android App built with Kotlin & XML

![Android](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple?style=flat-square&logo=kotlin)
![Firebase](https://img.shields.io/badge/Backend-Firebase-orange?style=flat-square&logo=firebase)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue?style=flat-square)

---

## 📱 About

**EventSphere** is a modern Android application that allows users to discover, create, and RSVP to events. Built with a clean dark UI and powered by Firebase, it provides a seamless event management experience for both organizers and attendees.

---

## ✨ Features

- 🔐 **Authentication** — Register & Login with Email/Password via Firebase Auth
- 📅 **Browse Events** — View all upcoming events in a clean card-based feed
- ➕ **Create Events** — Organizers can create events with title, description, location, date, time and category
- ✅ **RSVP System** — Users can RSVP to events and see how many people are going
- 🔔 **Push Notifications** — Receive notifications via Firebase Cloud Messaging (FCM)
- 👤 **User Profile** — View your profile, events created and events joined
- 🔍 **Search** — Search events by title, location or category
- 🔑 **Forgot Password** — Reset password via email link

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| **Kotlin** | Primary programming language |
| **XML** | UI Layouts |
| **Firebase Auth** | User authentication |
| **Firebase Firestore** | Cloud database for events & users |
| **Firebase Messaging (FCM)** | Push notifications |
| **Glide** | Image loading |
| **ViewBinding** | View access |
| **Material Design 3** | UI components |

---

## 📁 Project Structure

```
EventSphere/
└── app/src/main/
    ├── java/com/example/eventsphere/
    │   ├── activities/
    │   │   ├── SplashActivity.kt
    │   │   ├── LoginActivity.kt
    │   │   ├── RegisterActivity.kt
    │   │   ├── MainActivity.kt
    │   │   ├── CreateEventActivity.kt
    │   │   ├── EventDetailActivity.kt
    │   │   ├── ProfileActivity.kt
    │   │   └── ForgotPasswordActivity.kt
    │   ├── adapters/
    │   │   └── EventAdapter.kt
    │   ├── models/
    │   │   ├── Event.kt
    │   │   └── User.kt
    │   └── notifications/
    │       └── MyFirebaseMessagingService.kt
    └── res/
        ├── layout/       # All XML layouts
        ├── drawable/     # Icons & backgrounds
        └── values/       # Colors, themes, strings
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android device or emulator (API 24+)
- Firebase account

### Setup Instructions

**1. Clone the repository**
```bash
git clone https://github.com/YOURUSERNAME/EventSphere.git
cd EventSphere
```

**2. Firebase Setup**
- Go to [console.firebase.google.com](https://console.firebase.google.com)
- Create a new project named `EventSphere`
- Add an Android app with package name `com.example.eventsphere`
- Download `google-services.json` and place it in the `app/` folder
- Enable **Email/Password** Authentication
- Create a **Firestore Database** in test mode
- Enable **Firebase Cloud Messaging**

**3. Build & Run**
- Open the project in Android Studio
- Click **Sync Now** when prompted
- Run on a device or emulator

---

## 🔥 Firebase Structure

### Firestore Collections

**`users`**
```
{
  uid: String,
  name: String,
  email: String,
  fcmToken: String
}
```

**`events`**
```
{
  id: String,
  title: String,
  description: String,
  location: String,
  date: String,
  time: String,
  category: String,
  organizerId: String,
  organizerName: String,
  rsvpList: List<String>,
  createdAt: Long
}
```

---

## 🎨 UI Theme

The app uses a **Dark & Modern** design language:

| Color | Hex | Usage |
|---|---|---|
| Background | `#0A0A0F` | Main background |
| Card | `#1A1A2E` | Card surfaces |
| Purple | `#7C3AED` | Primary accent |
| Pink | `#EC4899` | Secondary accent |
| Light Purple | `#A78BFA` | Text highlights |

---

## 📸 Screenshots

> Coming soon

---

## 🔒 Security Notes

- `google-services.json` is excluded from this repository for security
- Firestore rules require authentication for all read/write operations
- FCM tokens are stored per user for targeted notifications

---

## 📄 Firestore Rules

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## 👨‍💻 Author

**Ryker**
- GitHub: [@sreekanthteegala](https://github.com/sreekanthteegala)
- GitHub: [@aayushpatel2533](https://github.com/aayushpatel2533)

---



<p align="center">Built with ❤️ using Kotlin & Firebase</p>
