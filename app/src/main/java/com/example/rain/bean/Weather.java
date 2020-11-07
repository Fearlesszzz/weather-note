package com.example.rain.bean;

public class Weather {
    private String today_weather = null;  // 当前天气
    private String tmp = null;  // 当前气温
    private String city = null;  // 当前城市
    private String air = null;  // 当前空气质量
    private String today_tmp_high = null;  // 今天最高气温
    private String today_tmp_low = null;  // 今天最底气温

    public String getToday_weather() {
        return today_weather;
    }

    public void setToday_weather(String today_weather) {
        this.today_weather = today_weather;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAir() {
        return air;
    }

    public void setAir(String air) {
        this.air = air;
    }

    public String getToday_tmp_high() {
        return today_tmp_high;
    }

    public void setToday_tmp_high(String today_tmp_high) {
        this.today_tmp_high = today_tmp_high;
    }

    public String getToday_tmp_low() {
        return today_tmp_low;
    }

    public void setToday_tmp_low(String today_tmp_low) {
        this.today_tmp_low = today_tmp_low;
    }
}
