<h2 align="center"><u>KethiCare Android App</u></h2>

![Smart Farming Assistant](https://cdn-icons-png.flaticon.com/128/7361/7361791.png)
<h4 align="center">Empowering Farmers with AI for Smart Agriculture 🌾</h4>

<p align="center">
    <img src="https://img.shields.io/github/stars/panwar-harsh/KethiCare?style=for-the-badge&color=orange">
    <img src="https://img.shields.io/github/forks/panwar-harsh/KethiCare?style=for-the-badge&color=purple">
    <img src="https://img.shields.io/github/license/panwar-harsh/KethiCare?style=for-the-badge&color=blue">
    <img src="https://img.shields.io/github/issues/panwar-harsh/KethiCare?style=for-the-badge&color=red">
    <img src="https://img.shields.io/github/contributors/panwar-harsh/KethiCare?style=for-the-badge&color=cyan">
<br>
    <img src="https://img.shields.io/badge/Author-Harsh%20Panwar-magenta?style=flat-square">
    <img src="https://img.shields.io/badge/Open%20Source-Yes-brightgreen?style=flat-square">
    <img src="https://img.shields.io/badge/Maintained-Yes-cyan?style=flat-square">
    <img src="https://img.shields.io/badge/Made%20In-India-green?style=flat-square">
    <img src="https://img.shields.io/badge/Written%20In-Kotlin,%20OpenCV,%20TensorFlow,%20Firebase-blue?style=flat-square">
</p>

---

## 📝 Description

🌿 **KethiCare** is a smart Android app designed for farmers, enabling them to harness the power of AI for healthier crops, better equipment management, and smarter decision-making. From disease detection to a farming marketplace, it's your all-in-one agriculture companion.

---

## 🔑 Key Features

- 🌱 **Plant Disease Detection**: Instantly detect plant diseases using AI (TensorFlow + OpenCV)
- 🔍 **Plant Identifier**: Take a picture and identify the plant species
- 🤖 **AI Chatbot**: Ask farming-related questions and get instant smart responses
- 🛒 **Marketplace**: Buy/sell farming equipment directly with other farmers
- 🌦️ **Weather-Based Advice**: Get watering and fertilizer suggestions based on real-time weather data
- 👤 **Profile Management**: Farmers can update their name, contact, and view listings
- 🔐 **Firebase Secure Storage**: Store user data and listings securely in Firebase
- ☁️ **Cloudinary Integration**: Upload and host equipment images via Cloudinary

---

## 🚀 Getting Started

### ✅ Prerequisites

Make sure you have the following:

- Android Studio (Giraffe or newer)
- JDK 11 or higher
- Git installed
- Firebase account (for backend services)
- Cloudinary account (for image uploads)
- Internet connection

---

### 📁 Clone the Repository

```bash
git clone https://github.com/panwar-harsh/KethiCare.git
cd KethiCare
```

---

### 📂 Open in Android Studio

1. Open Android Studio
2. Click **File > Open**
3. Choose the `KethiCare/` folder
4. Let Gradle sync and build the project

---

### 🔧 Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use an existing one
3. Add an Android app with your package name (e.g., `com.example.kethicare`)
4. Download the `google-services.json` file
5. Place it inside the `app/` directory:
   ```
   KethiCare/app/google-services.json
   ```
6. Enable Firebase Authentication, Firestore, and Storage
7. Sync Gradle again

---

### ☁️ Cloudinary Setup

1. Create an account at [Cloudinary](https://cloudinary.com/)
2. Get your:
   - Cloud Name
   - API Key
   - API Secret
3. Add these credentials securely in your code (or use a config class or constants file)

---

### ▶️ Run the App

1. Connect a real Android device or start an emulator
2. Press **Run ▶️** or use **Shift + F10**
3. Enjoy using KethiCare!

---

### 🔁 Syncing with Upstream

If you forked the project and want to stay updated:

```bash
git remote add upstream https://github.com/panwar-harsh/KethiCare.git
git fetch upstream
git merge upstream/main
```

---

## 👨‍💻 Author

**Harsh Panwar**  
🔗 [GitHub Profile](https://github.com/panwar-harsh)

---


## 🙌 Support

If you found this project helpful:

- ⭐ Star the repo
- 🍴 Fork it
- 🧑‍🌾 Share it with fellow developers or farmers!
