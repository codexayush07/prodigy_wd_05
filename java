const apiKey = "1cc2ce07a379f16987add885b228bdba";
const form = document.getElementById("weatherForm");
const cityInput = document.getElementById("cityInput");
const resultDiv = document.getElementById("weatherResult");
const forecastDiv = document.getElementById("forecast");
const locationBtn = document.getElementById("locationBtn");

// Manual city search
form.addEventListener("submit", (e) => {
  e.preventDefault();
  const city = cityInput.value.trim();
  if (city !== "") {
    fetchWeatherByCity(city);
  }
});

// GPS location weather
locationBtn.addEventListener("click", () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        fetchWeatherByCoords(latitude, longitude);
      },
      () => {
        resultDiv.innerHTML = `<p style="color:red;">Location access denied.</p>`;
      }
    );
  } else {
    resultDiv.innerHTML = `<p style="color:red;">Geolocation not supported.</p>`;
  }
});

// Fetch using city
function fetchWeatherByCity(city) {
  const currentURL = `https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${apiKey}&units=metric`;
  const forecastURL = `https://api.openweathermap.org/data/2.5/forecast?q=${city}&appid=${apiKey}&units=metric`;
  getWeather(currentURL);
  getForecast(forecastURL);
}

// Fetch using coordinates
function fetchWeatherByCoords(lat, lon) {
  const currentURL = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${apiKey}&units=metric`;
  const forecastURL = `https://api.openweathermap.org/data/2.5/forecast?lat=${lat}&lon=${lon}&appid=${apiKey}&units=metric`;
  getWeather(currentURL);
  getForecast(forecastURL);
}

// Show current weather
function getWeather(apiURL) {
  fetch(apiURL)
    .then(res => res.json())
    .then(data => {
      const { name, main, weather, wind } = data;
      const iconURL = `https://openweathermap.org/img/wn/${weather[0].icon}@2x.png`;
      resultDiv.innerHTML = `
        <h2>${name}</h2>
        <img src="${iconURL}" alt="${weather[0].description}" />
        <p><strong>🌡️ Temperature:</strong> ${main.temp}°C</p>
        <p><strong>🌥️ Condition:</strong> ${weather[0].description}</p>
        <p><strong>💨 Wind Speed:</strong> ${wind.speed} m/s</p>
        <p><strong>💧 Humidity:</strong> ${main.humidity}%</p>
      `;
    })
    .catch(err => {
      resultDiv.innerHTML = `<p style="color:red;">Error: ${err.message}</p>`;
    });
}

// Show 3-day forecast (every 24 hrs at 12:00)
function getForecast(apiURL) {
  fetch(apiURL)
    .then(res => res.json())
    .then(data => {
      forecastDiv.innerHTML = "";
      const filtered = data.list.filter(item => item.dt_txt.includes("12:00:00")).slice(0, 3);
      filtered.forEach(day => {
        const date = new Date(day.dt_txt).toDateString();
        const icon = `https://openweathermap.org/img/wn/${day.weather[0].icon}@2x.png`;
        const temp = day.main.temp.toFixed(1);
        const desc = day.weather[0].main;

        const card = `
          <div class="forecast-card">
            <h4>${date.split(" ").slice(0, 2).join(" ")}</h4>
            <img src="${icon}" alt="${desc}" />
            <p>${temp}°C</p>
            <p>${desc}</p>
          </div>
        `;
        forecastDiv.innerHTML += card;
      });
    })
    .catch(() => {
      forecastDiv.innerHTML = `<p style="color:red;">Forecast not available.</p>`;
    });
}
