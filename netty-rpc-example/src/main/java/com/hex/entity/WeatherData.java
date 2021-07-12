package com.hex.entity;


/**
 * @author guohs
 * @date 2021/7/12
 */
public class WeatherData {

    private String weather;
    private String address;
    private int temperature;

    public String getWeather() {
        return weather;
    }

    public WeatherData setWeather(String weather) {
        this.weather = weather;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public WeatherData setAddress(String address) {
        this.address = address;
        return this;
    }

    public int getTemperature() {
        return temperature;
    }

    public WeatherData setTemperature(int temperature) {
        this.temperature = temperature;
        return this;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "weather='" + weather + '\'' +
                ", address='" + address + '\'' +
                ", temperature=" + temperature +
                '}';
    }
}
