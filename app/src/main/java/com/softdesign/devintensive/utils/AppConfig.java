package com.softdesign.devintensive.utils;

/**
 * Created by Ageev Evgeny on 11.07.2016.
 */
public interface AppConfig {
    String BASE_URI = "http://devintensive.softdesign-apps.ru/api/";
    //String AVATAR_URI = "/publicValues/profilePhoto";
    String RESUME = "https://m.hh.ru/applicant/resume/5e6479eeff0327c4e20039ed1f6e7679434766";

    int MAX_CONNECT_TIMEOUT = 5000;
    int MAX_READ_TIMEOUT = 5000;
    int START_DELAY = 1500;
    int SEARCH_DELAY = 3000;
    int SPLASH_DELAY = 3000;
    int ERROR_VIBRATE_TIME = 2000;
}
