package com.softdesign.devintensive.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ageev Evgeny on 19.07.2016.
 */
public class Validator {
    public static String[] resultCodes = {
            "1.1 Less than minimum numbers in phone",
            "1.2 More than maximum numbers in phone",
            "1.3 Empty phone",
            "2.1 Incorrect email",
            "2.2 Empty email",
            "3.1 Incorrect VK",
            "3.2 Empty VK",
            "3.3 Enter a character before vk.com is illegal",
            "4.1 Incorrect Github",
            "4.2 Empty Github",
            "4.3 Enter a character before github.com is illegal"
    };

    private static String getPhoneNumber(String phone) {
        phone = phone.replace("(","");
        phone = phone.replace(")","");
        phone = phone.replace("-","");
        phone = phone.replace(" ","");

        return phone;
    }

    public static int validatePhone(String phone) {
        if (phone.isEmpty()) return 3;

        phone = getPhoneNumber(phone);
        if (phone.length() < 11) return 1;
        else if (phone.length() > 20) return 2;
        return 0;//OK
    }

    private static int validateMail(String email) {
        if (email.isEmpty()) return 2;

        //Pattern p = Pattern.compile("^[a-z0-9_-]{3,}@[a-z0-9_-]{2,}\.^[a-z]{2,6}$");
        //Matcher m = p.matcher(email);
        //if (!m.matches()) return 1;
        return 0;//OK
    }

    private static int validateVk(String vkAddr) {
        if (vkAddr.isEmpty()) return 2;

        int pos = vkAddr.indexOf("vk.com");
        if (pos == -1) return 1;
        else if (pos != 0) return 3;
        return 0;//OK
    }

    private static int validateGithub(String gitAddr) {
        if (gitAddr.isEmpty()) return 2;

        int pos = gitAddr.indexOf("github.com");
        if (pos == -1) return 1;
        else if (pos != 0) return 3;
        return 0;//OK
    }
}
