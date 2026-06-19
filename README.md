*COMPANY*: CODTECH IT SOLUTIONS

*NAME*:KOTHAPALLI RAGHAVENDRA

*INTERN ID*:CTIS9780

*DOMAIN*:JAVA PROGRAMMING

*DURATION*:8 WEEKS

*MENTOR*:NEELAM SANTOSH

# Nimbus - Java Weather Client Dashboard

A complete, production-ready Java application that consumes the Open-Meteo REST API, parses the JSON response using Jackson Databind, and serves a modern, glassmorphic Web User Interface from a lightweight embedded HTTP Server.

---

## 🌟 Key Features

- **Embedded HTTP Web Server**: Built using the native JDK `com.sun.net.httpserver.HttpServer`.
- **Premium Glassmorphic UI**: Serves a responsive dashboard styled with CSS gradients, blur effects, Outfit typography, and dynamic animations.
- **REST API Endpoint (`/api/weather`)**: Standardized endpoint allowing the UI to query weather conditions for any custom latitude and longitude coordinates.
- **Quick Presets**: Selectable buttons for major cities (New Delhi, New York, London, Tokyo, and Sydney) to retrieve weather data instantly.
- **Robust Client Integration**: Built using Java 11+ standard `HttpClient` with connection timeout handling, HTTP status code validation, and diagnostic request timing.
- **Enterprise-grade Exception Handling**: Custom `ApiException` wrapping networking, timeouts, invalid coordinates, and JSON parsing failures.

---

## 📂 Project Structure

```
Rest API Client/
├── pom.xml                               # Maven project configuration and dependencies
├── run.ps1                               # Automation script to compile & run without preinstalled Maven
├── README.md                             # Project documentation (this file)
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── weatherclient/
        │               ├── Main.java     # Entrypoint starting the web server
        │               ├── client/
        │               │   └── ApiClient.java       # HTTP GET engine using HttpClient
        │               ├── config/
        │               │   └── ConfigLoader.java    # Dynamically loads application.properties
        │               ├── exception/
        │               │   └── ApiException.java    # Custom Checked exception class
        │               ├── model/
        │               │   ├── CurrentWeather.java  # POJO mapping weather details
        │               │   └── WeatherResponse.java # POJO mapping API root response
        │               ├── server/
        │               │   └── WebServer.java       # Exposes endpoints and serves web assets
        │               └── service/
        │                   └── WeatherService.java  # Orchestrates client calls and JSON mapping
        └── resources/
            ├── application.properties    # Stores server port and external API URLs
            └── web/
                ├── index.html            # Web interface layout
                ├── style.css             # Glassmorphism styling rules
                └── app.js                # Javascript controller & mapping codes
```

---

## ⚙️ Tech Stack & Requirements

- **Java Version**: Java 17+ (Java 24 verified compatible).
- **HTTP Client**: `java.net.http.HttpClient` (no external HTTP dependencies).
- **JSON Parser**: Jackson Databind `2.17.1`.
- **Build Tool**: Maven (optional, fallback launcher script included).

---

## 🚀 How to Run the Application

You can choose one of the following two methods to compile and run the application:

### Option A: Using the Automated PowerShell Script (Recommended if Maven is not installed)
If you don't have Maven (`mvn`) configured in your system Path, you can run the automated script. It downloads the required Jackson JAR libraries, compiles the project, and boots the application:

1. Open PowerShell and navigate to the project directory:
   ```powershell
   cd "c:\Users\ragha\OneDrive\Desktop\23VV1A1221\CodeTech Internship Tasks\Rest API Client"
   ```
2. Execute the runner:
   ```powershell
   .\run.ps1
   ```
3. Open your browser and visit: **[http://localhost:8080](http://localhost:8080)**

---

### Option B: Using Maven
If you have Apache Maven installed and configured:

1. Build the project and package dependencies:
   ```bash
   mvn clean install
   ```
2. Run the application:
   ```bash
   mvn exec:java
   ```
3. Open your browser and visit: **[http://localhost:8080](http://localhost:8080)**

---

## 🏛️ Architectural Overview

This system utilizes a clean **Layered Architecture**:

1. **Config Layer (`config/`)**: Logs and loads parameters from `application.properties`.
2. **API Client Layer (`client/`)**: Reusable HTTP GET engine wrapping the standard `HttpClient`. Captures performance latency (Response Time) and validates response codes.
3. **Data Model Layer (`model/`)**: Structured Java POJOs mapping JSON responses. Robust against changes using `@JsonIgnoreProperties(ignoreUnknown = true)`.
4. **Service Layer (`service/`)**: Integrates config URLs, coordinates requests, and deserializes the JSON string response via Jackson's `ObjectMapper`.
5. **Server Layer (`server/`)**: An embedded `HttpServer` serving static web files from standard resources and exposing `/api/weather`.

---

## 📝 API Response Mapping Emojis
The frontend dynamically maps [Open-Meteo Weather Codes](https://open-meteo.com/en/docs) to clean user-friendly emojis and status texts:
- **0**: ☀️ Clear Sky
- **1, 2, 3**: ⛅ Clouds / Partly Cloudy
- **45, 48**: 🌫️ Fog
- **51 - 67**: 🌧️ Rain / Drizzle
- **71 - 77, 85, 86**: ❄️ Snowfall / Snow Showers
- **80, 81, 82**: 🌦️ Rain Showers
- **95, 96, 99**: ⛈️ Thunderstorms

## Output
<img width="1908" height="967" alt="Image" src="https://github.com/user-attachments/assets/6a5322e9-3047-44bf-ba2f-b068b61e9dc3" />
