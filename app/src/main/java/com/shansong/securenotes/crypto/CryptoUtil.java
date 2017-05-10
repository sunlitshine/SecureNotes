package com.shansong.securenotes.crypto;

import android.util.Log;

import com.shansong.securenotes.utils.APPEnv;
import com.shansong.securenotes.utils.DataHelper;

public final class CryptoUtil {

	private final static String TAG = CryptoUtil.class.getName();

	public static byte[] sha256(final byte[] data) {
		final byte[] result = new byte[32];
		final SHA256Digest digest = new SHA256Digest();
		digest.update(data, 0, data.length);
		digest.doFinal(result, 0);

		if(APPEnv.DEBUG){
			Log.d(TAG, "Input data: "+ DataHelper.byteArrayToHexaStr(data));
			Log.d(TAG, "Output data(after sha256): "+ DataHelper.byteArrayToHexaStr(result));
		}
		return result;
	}

	static void arrayCopy(final byte[] src, final int srcStart,
			final byte[] dest, final int destStart, final int length) {
		for (int i = 0; i < length; i++) {
			dest[i + destStart] = src[i + srcStart];
		}
	}

	static void arrayCopy(final int[] src, final int srcStart,
			final int[] dest, final int destStart, final int length) {
		for (int i = 0; i < length; i++) {
			dest[i + destStart] = src[i + srcStart];
		}
	}

}
