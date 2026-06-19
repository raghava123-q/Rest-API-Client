package com.example.weatherclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO mapping for the "current_weather" JSON block.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeather {

    @JsonProperty("time")
    private String time;

    @JsonProperty("temperature")
    private double temperature;

    @JsonProperty("windspeed")
    private double windSpeed;

    @JsonProperty("winddirection")
    private double windDirection;

    @JsonProperty("weathercode")
    private int weatherCode;

    // Getters and Setters
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "time='" + time + '\'' +
                ", temperature=" + temperature +
                ", windSpeed=" + windSpeed +
                ", windDirection=" + windDirection +
                ", weatherCode=" + weatherCode +
                '}';
    }
}
