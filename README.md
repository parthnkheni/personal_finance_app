# 📈 Mock Investor App

Mock Investor is a virtual stock trading Android application that allows users to simulate buying and selling stocks in real-time. Designed as a personal finance learning tool, the app enables users to build a mock portfolio, track total portfolio value, and receive alerts when stock prices hit configured thresholds.

## 🚀 Features

### 📊 Portfolio Management
- Add U.S. stocks to a personal portfolio.
- Specify quantity of shares held for each symbol.
- Save and load portfolio data locally across app sessions.
- Remove stocks from portfolio at any time.
- View current price and total value (`price × quantity`) for each stock.
- Automatically calculates total portfolio value.

### 🔎 Search and Browse
- Search for stock symbols by name or ticker.
- Browse all available U.S. stocks via integrated API.
- Filter by security type or description.

### 🔔 Price Alerts
- Configure price alerts (above or below a user-specified threshold).
- Get Android system notifications when price crosses the threshold.

---

## 🛠️ Installation & Setup

### Prerequisites
- Java (Latest JDK) — [Download](https://www.oracle.com/java/technologies/downloads/)
- Android Studio — [Download](https://developer.android.com/studio)

### Clone the Repository
1. Clone the repository:
```bash
git clone https://github.com/parthnkheni/personal_finance_app
```  
2. Create a local.properties file in the root directory: stock_app/local.properties should be the file path   
3. Copy and paste the following line into local.properties and then save the file: 
```txt
sdk.dir=C\:\\Users\\YourWindowsUserNameHere\\AppData\\Local\\Android\\Sdk
```
- Replace YourWindowsUserNameHere with your windows username. The default installation path of Sdk is above, if your installation path is any different you will have to change that.  
4. Build the gradle files (a 'sync files' should pop up at the top of the screen')
5. If you get an error and are unable to build the gradle files, you may need to create a gradle.properties file in the root directory and have it include the following:
```
android.useAndroidX=true
```
5. Run the app using the run button 
6. If necessary, change the compile sdk version from 33 to 34 in the build.gradle.kts file
```kts
android {
    namespace = "com.example.mockinvestor"
    compileSdk = 34
```
