package com.softdesign.devintensive.utils;

import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ageev Evgeny on 19.07.2016.
 */
public class Validator {
    //private final List<String> mPropList;
    private final List<EditText> mEditList;
    private final List<String> mErrorList;

    public static final String[] resultCodes = {
            "1.1 Less than minimum numbers in phone",
            "1.2 More than maximum numbers in phone",
            "1.3 Empty phone",
            "1.4 Entered letter in phone number",
            "2.1 Incorrect email",
            "2.2 Empty email",
            "3.1 Incorrect VK",
            "3.2 Empty VK",
            "3.3 Enter a character before vk.com is illegal",
            "4.1 Incorrect Github",
            "4.2 Empty Github",
            "4.3 Enter a character before github.com is illegal"
    };

    public Validator(List<EditText> list) {
        mEditList = list;
        mErrorList = new ArrayList<>();
    }

    public boolean validate() {
        boolean res = true;
        if (mEditList.size() != 5) {
            Log.e("VALIDATOR", "ERROR: size of array - " + mEditList.size());
        } else {
            if (!validatePhone(mEditList.get(0))) res = false;
            if (!validateMail(mEditList.get(1))) res = false;
            if (!validateVK(mEditList.get(2))) res = false;
            if (!validateGit(mEditList.get(3))) res = false;
        }
        return res;
    }

    public List<String> getErrorList() {
        return mErrorList;
    }

    private static String getPhoneNumber(String phone) {
        phone = phone.replace("(","");
        phone = phone.replace(")","");
        phone = phone.replace("+","");
        phone = phone.replace("-","");
        phone = phone.replace(" ","");
        Log.d("VALIDATOR", "Phone edited - " + phone);
        return phone;
    }

    public boolean validatePhone(EditText phoneEt) {
        String phone = phoneEt.getText().toString().trim();
        if (phone.isEmpty()) {
            mErrorList.add(resultCodes[2]);
            return false;
        }

        phone = getPhoneNumber(phone);
        if (phone.length() < 11) {
            mErrorList.add(resultCodes[0]);
            return false;
        }
        else if (phone.length() > 20) {
            mErrorList.add(resultCodes[1]);
            return false;
        }
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(phone);
        if (!m.matches()) {
            mErrorList.add(resultCodes[3]);
            return false;
        }
        return true;
    }

    private boolean validateMail(EditText emailEt) {
        String email = emailEt.getText().toString().trim().toLowerCase();
        if (email.isEmpty()) {
            mErrorList.add(resultCodes[5]);
            return false;
        }

        Pattern p = Pattern.compile("^[a-z0-9_-]{3,}@[a-z0-9_-]{2,}.[a-z]{2,6}$");
        Matcher m = p.matcher(email);
        if (!m.matches()) {
            mErrorList.add(resultCodes[4]);
            return false;
        }
        return true;
    }

    private boolean validateVK(EditText vkAddrEt) {
        String vkAddr = vkAddrEt.getText().toString().trim().toLowerCase();
        if (vkAddr.isEmpty()) {
            mErrorList.add(resultCodes[7]);
            return false;
        }

        int pos = vkAddr.indexOf("http://");
        if (pos == 0) {
            vkAddr = vkAddr.substring(7);
            vkAddrEt.setText(vkAddr);
        } else {
            pos = vkAddr.indexOf("https://");
            if (pos == 0) {
                vkAddr = vkAddr.substring(8);
                vkAddrEt.setText(vkAddr);
            }
        }

        pos = vkAddr.indexOf("vk.com");
        if (pos == -1) {
            mErrorList.add(resultCodes[6]);
            return false;
        }
        else if (pos != 0) {
            mErrorList.add(resultCodes[8]);
            return false;
        }
        return true;
    }

    private boolean validateGit(EditText gitAddrEt) {
        String gitAddr = gitAddrEt.getText().toString().trim().toLowerCase();
        if (gitAddr.isEmpty()) {
            mErrorList.add(resultCodes[10]);
            return false;
        }

        int pos = gitAddr.indexOf("http://");
        if (pos == 0) {
            gitAddr = gitAddr.substring(7);
            gitAddrEt.setText(gitAddr);
        } else {
            pos = gitAddr.indexOf("https://");
            if (pos == 0) {
                gitAddr = gitAddr.substring(8);
                gitAddrEt.setText(gitAddr);
            }
        }

        pos = gitAddr.indexOf("github.com");
        if (pos == -1) {
            mErrorList.add(resultCodes[9]);
            return false;
        }
        else if (pos != 0) {
            mErrorList.add(resultCodes[11]);
            return false;
        }
        return true;
    }
}
