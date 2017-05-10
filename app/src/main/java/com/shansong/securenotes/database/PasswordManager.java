package com.shansong.securenotes.database;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.Log;

import com.shansong.securenotes.crypto.CryptoUtil;
import com.shansong.securenotes.utils.APPEnv;
import com.shansong.securenotes.utils.DataHelper;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.SecureRandom;


public final class PasswordManager {

    private static PasswordManager instance;

    public static final String TAG = PasswordManager.class.getName();

    private static final byte[] FP_SALT_A = { (byte) 0x2A, (byte) 0x66, (byte) 0xBD, (byte) 0xD1, (byte) 0x1F,
            (byte) 0x02, (byte) 0x34, (byte) 0xC1, (byte) 0x35, (byte) 0xEC, (byte) 0x23, (byte) 0x47, (byte) 0xF4,
            (byte) 0x46, (byte) 0xE2, (byte) 0x85 };

    private static final byte[] FP_SALT_B = { (byte) 0xCD, (byte) 0x9D, (byte) 0x76, (byte) 0x73, (byte) 0x98,
            (byte) 0x11, (byte) 0x3E, (byte) 0x64, (byte) 0xDA, (byte) 0xB0, (byte) 0x09, (byte) 0x05, (byte) 0x33,
            (byte) 0x06, (byte) 0x0A, (byte) 0xf4 };


    private Context mContext;

    public PasswordManager(final Context context){
        this.mContext = context;
    }


    public static PasswordManager getInstance(final Context context){
        if(instance ==null){
            instance = new PasswordManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * A method to get the secure password based on the device
     *
     * @return
     */
    public final String getSecurePassword() {
        try {
            return DataHelper.byteArrayToHexaStr(CryptoUtil.sha256(getBasicPassword()));
        } catch (Exception e) {
            if (APPEnv.DEBUG) {
                Log.e(TAG, "Failed to generate secure password returning a dummy one", e);
            }
            // Return a dummy fingerprint if an exception is
            // encountered
            final SecureRandom random = new SecureRandom();
            final byte[] dummyFingerprint = new byte[37];
            random.nextBytes(dummyFingerprint);
            return DataHelper.byteArrayToHexaStr(CryptoUtil.sha256(dummyFingerprint));
        }
    }


    private byte[] getBasicPassword() throws IOException {


        // Part 1: Constant Salt to diversify the Fingerprint
        final byte[] salt = DataHelper.encryptByXor(FP_SALT_A, FP_SALT_B);

        // Part 2: Android ID
        final String android_id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);

        // Part 3: List of Feature Availability
        byte[] features = null;

        features = getFeatures();


        final byte[] manufacture = Build.MANUFACTURER.getBytes();
        final byte[] model = Build.MODEL.getBytes();
        final byte[] hardware = Build.HARDWARE.getBytes();

        final byte[] serialNo = Build.SERIAL.getBytes();

        // Concatenate all the parts and
        final byte[] basicFingerprint = DataHelper.concatenate( salt, (android_id).getBytes(), features, manufacture,
                model, hardware, serialNo);

        if (APPEnv.DEBUG) {
            Log.d(TAG, "Salt: " + DataHelper.byteArrayToHexaStr(salt));
            Log.d(TAG, "ANDROID_ID:" + (android_id));
            Log.d(TAG, "features:" + DataHelper.byteArrayToHexaStr(features));
            Log.d(TAG, "manufacture:" + DataHelper.byteArrayToHexaStr(manufacture));
            Log.d(TAG, "model:" + DataHelper.byteArrayToHexaStr(model));
            Log.d(TAG, "hardware:" + DataHelper.byteArrayToHexaStr(hardware));
            Log.d(TAG, "serialNo:" + DataHelper.byteArrayToHexaStr(serialNo));
            Log.d(TAG, "Basic Fingerprint: " + DataHelper.byteArrayToHexaStr(basicFingerprint));
        }

        return basicFingerprint;
    }

    private byte[] getFeatures() throws IOException { // NOPMD

        final Features features = new Features();

        final PackageManager packageManager = mContext.getPackageManager();

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_LIVE_WALLPAPER));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_NETWORK));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_NFC));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_SIP));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_SIP_VOIP));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_JAZZHAND));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_SCREEN_LANDSCAPE));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_SCREEN_PORTRAIT));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST));

        features.append(packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT));

        return features.getFeatures();
    }

    private static class Features {

        private long container;

        private void append(final boolean hasSystemFeature) {
            container <<= 1;
            container = hasSystemFeature ? container | 0x1 : container;
        }

        private byte[] getFeatures() throws IOException {
            ByteArrayOutputStream baos = null;
            DataOutputStream dos = null;

            try {
                baos = new ByteArrayOutputStream();
                dos = new DataOutputStream(baos);
                dos.writeLong(container);
                return baos.toByteArray();
            } finally {
                if (dos != null) {
                    dos.close();
                }
                if (baos != null) {
                    baos.close();
                }
            }
        }
    }

}
