package com.zhaoyan.ladderball.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.activeandroid.query.Select;
import com.zhaoyan.ladderball.http.response.LoginResponse;
import com.zhaoyan.ladderball.model.User;

/**
 *
 * Created by Yuri on 2015/12/2.
 */
public class UserManager {

    /**
     * 根据手机号码获取当前用户保存在本地的usertoken
     * @param phone 手机号码
     * @return userToken
     */
    public String getTokenByPhone(String phone) {
        String userToken = "";
        if (!TextUtils.isEmpty(phone)) {
            User u = new Select().from(User.class)
                    .where("Phone=?", phone).executeSingle();
            if (u != null) {
                userToken = getDecryptedToken(u.mPhone);
            }
        }
        return userToken;
    }

    public static void saveOrUpdateUser(LoginResponse loginResponse) {
        String phone = loginResponse.phone;
        User user = new Select().from(User.class).where("phone=?", phone).executeSingle();
        if (user == null) {
            user = new User();
        }
        user.mUserName = loginResponse.name;
        user.mAddress = loginResponse.address;
        user.mPhone = loginResponse.phone;
        user.mUserToken = loginResponse.userToken;
        user.mGender = loginResponse.gender;

        if (!TextUtils.isEmpty(user.mUserToken)) {
            user.save();
        }
    }

    /**
     * 加密算法默认的密码
     */
    private static final String DEFAULT_PASSWORD = "ladderball";

    /**
     * 获取加密后的token
     *
     * @return 加密后的token，加密失败返回null
     */
    private static String getEncryptedToken(@NonNull String token) {
        try {
            String encrypted = AESCrypt.encrypt(DEFAULT_PASSWORD, token);
            Log.d("after encrypte:" + encrypted);
            return encrypted;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取解密后的userToken
     *
     * @param encrypedToken 已加密的token
     * @return 解密的token，如果失败返回null
     */
    private static String getDecryptedToken(String encrypedToken) {
        try {
            String decryptedText = AESCrypt.decrypt(DEFAULT_PASSWORD, encrypedToken);
            Log.d("after decrypte:" + decryptedText);
            return decryptedText;
        } catch (Exception e) {
        }
        return null;
    }
}
