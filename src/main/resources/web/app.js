// Weather codes mapping to emoji and description
const weatherCodes = {
    0: { emoji: "☀️", desc: "Clear Sky" },
    1: { emoji: "🌤️", desc: "Mainly Clear" },
    2: { emoji: "⛅", desc: "Partly Cloudy" },
    3: { emoji: "☁️", desc: "Overcast" },
    45: { emoji: "🌫️", desc: "Fog" },
    48: { emoji: "🌫️", desc: "Depositing Rime Fog" },
    51: { emoji: "🌧️", desc: "Light Drizzle" },
    53: { emoji: "🌧️", desc: "Moderate Drizzle" },
    55: { emoji: "🌧️", desc: "Dense Drizzle" },
    56: { emoji: "🌧️", desc: "Light Freezing Drizzle" },
    57: { emoji: "🌧️", desc: "Dense Freezing Drizzle" },
    61: { emoji: "🌧️", desc: "Slight Rain" },
    63: { emoji: "🌧️", desc: "Moderate Rain" },
    65: { emoji: "🌧️", desc: "Heavy Rain" },
    66: { emoji: "🌧️", desc: "Light Freezing Rain" },
    67: { emoji: "🌧️", desc: "Heavy Freezing Rain" },
    71: { emoji: "❄️", desc: "Slight Snowfall" },
    73: { emoji: "❄️", desc: "Moderate Snowfall" },
    75: { emoji: "❄️", desc: "Heavy Snowfall" },
    77: { emoji: "❄️", desc: "Snow Grains" },
    80: { emoji: "🌦️", desc: "Slight Rain Showers" },
    81: { emoji: "🌦️", desc: "Moderate Rain Showers" },
    82: { emoji: "🌦️", desc: "Violent Rain Showers" },
    85: { emoji: "❄️", desc: "Slight Snow Showers" },
    86: { emoji: "❄️", desc: "Heavy Snow Showers" },
    95: { emoji: "⛈️", desc: "Thunderstorm" },
    96: { emoji: "⛈️", desc: "Thunderstorm with Slight Hail" },
    99: { emoji: "⛈️", desc: "Thunderstorm with Heavy Hail" }
};

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("weather-form");
    const fetchBtn = document.getElementById("fetch-btn");
    const latInput = document.getElementById("latitude");
    const lonInput = document.getElementById("longitude");
    const presetButtons = document.querySelectorAll(".btn-preset");
    
    const placeholder = document.getElementById("weather-placeholder");
    const resultContainer = document.getElementById("weather-result");
    
    // UI elements to update
    const emojiEl = document.getElementById("weather-emoji");
    const descEl = document.getElementById("weather-desc");
    const tempEl = document.getElementById("weather-temp");
    const timeEl = document.getElementById("weather-time");
    const windspeedEl = document.getElementById("weather-windspeed");
    const winddirectionEl = document.getElementById("weather-winddirection");
    const codeEl = document.getElementById("weather-code");

    // Fetch Weather Data function
    async function fetchWeather(lat, lon) {
        // Toggle Loading State
        fetchBtn.classList.add("loading");
        fetchBtn.disabled = true;
        
        // Remove old error messages if any
        const existingError = document.querySelector(".error-message");
        if (existingError) {
            existingError.remove();
        }

        try {
            const response = await fetch(`/api/weather?latitude=${lat}&longitude=${lon}`);
            
            if (!response.ok) {
                const errorData = await response.text();
                throw new Error(errorData || `HTTP error! Status: ${response.status}`);
            }
            
            const data = await response.json();
            
            if (!data.current_weather) {
                throw new Error("Invalid response format received from backend.");
            }
            
            displayWeather(data.current_weather);
        } catch (error) {
            console.error("Fetch error:", error);
            showError(error.message);
        } finally {
            fetchBtn.classList.remove("loading");
            fetchBtn.disabled = false;
        }
    }

    // Display Weather Details in UI
    function displayWeather(current) {
        const info = weatherCodes[current.weathercode] || { emoji: "🌡️", desc: "Unknown Condition" };
        
        emojiEl.textContent = info.emoji;
        descEl.textContent = info.desc;
        tempEl.textContent = current.temperature.toFixed(1);
        
        // Format ISO String Time: 2026-06-18T16:15 -> 2026-06-18 16:15
        const formattedTime = current.time ? current.time.replace("T", " ") : "--";
        timeEl.textContent = formattedTime;
        
        windspeedEl.textContent = `${current.windspeed} km/h`;
        winddirectionEl.textContent = `${current.winddirection}°`;
        codeEl.textContent = current.weathercode;
        
        placeholder.classList.add("hidden");
        resultContainer.classList.remove("hidden");
    }

    // Display Error UI
    function showError(message) {
        placeholder.classList.add("hidden");
        resultContainer.classList.add("hidden");
        
        const errorDiv = document.createElement("div");
        errorDiv.className = "error-message";
        errorDiv.innerHTML = `⚠️ <strong>Error:</strong> ${message}`;
        
        // Append error right inside the display card
        document.querySelector(".display-card").appendChild(errorDiv);
    }

    // Form submission handler
    form.addEventListener("submit", (e) => {
        e.preventDefault();
        const lat = parseFloat(latInput.value);
        const lon = parseFloat(lonInput.value);
        
        // Remove active class from presets
        presetButtons.forEach(btn => btn.classList.remove("active"));
        
        fetchWeather(lat, lon);
    });

    // Preset location clicks
    presetButtons.forEach(button => {
        button.addEventListener("click", () => {
            // Update active styling
            presetButtons.forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");
            
            // Set input values
            const lat = button.getAttribute("data-lat");
            const lon = button.getAttribute("data-lon");
            latInput.value = lat;
            lonInput.value = lon;
            
            // Fetch Automatically
            fetchWeather(parseFloat(lat), parseFloat(lon));
        });
    });

    // Initial load: Fetch weather for default active preset
    const activePreset = document.querySelector(".btn-preset.active");
    if (activePreset) {
        const lat = parseFloat(activePreset.getAttribute("data-lat"));
        const lon = parseFloat(activePreset.getAttribute("data-lon"));
        fetchWeather(lat, lon);
    }
});
