
package com.brahma.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

public class SHA256 {
    static String TAG = "SHA256";

    private static String createSha256(String str) {
        MessageDigest mMDigest;
        FileInputStream Input;
        File file = new File(str);
        byte buffer[] = new byte[1024];
        int len;
        if (!file.exists())
            return null;
        try {
            mMDigest = MessageDigest.getInstance("SHA-256");
            Input = new FileInputStream(file);
            while ((len = Input.read(buffer, 0, 1024)) != -1) {
                mMDigest.update(buffer, 0, len);
            }
            Input.close();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        BigInteger mBInteger = new BigInteger(1, mMDigest.digest());

        Log.v(TAG, "create_SHA256=" + String.format("%064x", mBInteger));
        return String.format("%064x", mBInteger);

        //return mBInteger.toString(16);

    }

    public static boolean checkSha256(String Sha256, String file) {
        String str = createSha256(file);
        Log.d(TAG,"sha256sum = " + str);
        if(Sha256.compareTo(str) == 0) return true;
        else
         return false;
    }
}
