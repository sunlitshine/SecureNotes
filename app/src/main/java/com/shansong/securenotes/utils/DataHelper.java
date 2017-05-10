package com.shansong.securenotes.utils;

import java.util.Arrays;
import java.util.Locale;

/**
 * Utility class to manage byte arrays.
 */
public final class DataHelper {

	private final static String TAG = DataHelper.class.getName();

	/**
	 * Using this variable to check single byte arrays to prevent other pmd
	 * warnings.
	 */
	private static final int SINGLE_ENTRY = 1;

	private DataHelper() {
	}

	// ======================================
	// Byte arrays helper methods
	// ======================================

	/**
	 * Creates a copy of the given byte[]
	 *
	 * @param data
	 *            a copy of the given byte[]
	 */
	public static byte[] copy(final byte[] data) {
		if (data == null) {
			return null;
		}
		return Arrays.copyOf(data, data.length);
	}

	/**
	 * Method to clear contents of byte array from memory
	 *
	 * @param data
	 *            array of byte arrays to be cleared
	 */
	public static void clearData(final byte[]... data) { // NOPMD
		/**
		 * [nebarle] PMD flags the for-each loop below as an anomaly for the
		 * 'dataToClear' variable. Changing to the old for-loop will remove this
		 * warning. However, this specific scenario has been flagged as a
		 * false-positive. http://sourceforge.net/p/pmd/bugs/1190/
		 */
		if (data != null && data.length > 0) {
			for (final byte[] dataToClear : data) {
				clearData(dataToClear);
			}
		}

	}

	/**
	 * Method to clear the byte array from memory.
	 *
	 * @param data
	 *            Byte array to be cleared
	 */
	// [nebarle] Method only has one parameter, unable to use vargs for byte[]
	public static void clearData(byte[] data) { // NOPMD

		if (data == null) {
			return;
		}

		final int dataLen = data.length;
		if (dataLen == 0) {
			return;
		}
		for (int i = 0; i < dataLen; i++) {
			data[i] = (byte) 0x00;
		}
	}

	/**
	 * Method to concatenate byte arrays.
	 *
	 * @param data
	 *            All byte arrays to be concatenated.
	 * @return the new byte array with the concatenated values
	 */
	public static byte[] concatenate(final byte[]... data) { // NOPMD
		/**
		 * [nebarle] PMD flags the for-each loop below as an anomaly for the
		 * 'entry' variable. Changing to the old for-loop will remove this
		 * warning. However, this specific scenario has been flagged as a
		 * false-positive. http://sourceforge.net/p/pmd/bugs/1190/
		 */
		byte[] finalData;

		if (data == null) {
			finalData = new byte[0];
		} else {
			if (data.length == SINGLE_ENTRY) {
				finalData = data[0];
			}

			else {
				// get total length of concatenated byte array
				int totalLen = 0;
				for (final byte[] entry : data) {
					if (entry != null) {
						totalLen += entry.length;
					}
				}

				finalData = new byte[totalLen];
				/**
				 * [nebarle] NOPMD DU anomaly for variable 'offset'
				 *
				 * PMD assumes that the value of offset will be undefined after
				 * the for-loop. The int offset will only be used within the
				 * for-loop for proper concatenation of the byte arrays. After
				 * which, it is no longer used.
				 */
				int offset = 0; // NOPMD
				for (final byte[] entry : data) {
					if (entry != null) {
						System.arraycopy(entry, 0, finalData, offset,
								entry.length);
						offset += entry.length;
					}
				}
			}
		}

		return finalData;
	}

	// ======================================
	// Byte arrays to integer (vice-versa)
	// ======================================

	/**
	 * Method to convert value to a byte array
	 *
	 * @param value
	 *            the int to be converted
	 * @param precise
	 *            if this value is set to true, the byte array that will be
	 *            returned will have the exact length needed to convert value.
	 *            If set to false, the byte array will always be 4 bytes long.
	 * @return byte array representing the given value
	 */
	public static byte[] intToByteArray(final int value, final boolean precise) {
		byte[] data = null;
		if (precise) {
			if (value <= 255) {
				data = new byte[1];
			} else if (value > 255 && value <= 65535) {
				data = new byte[2];
			} else if (value > 65535 && value <= 16777215) {
				data = new byte[3];
			} else {
				data = new byte[4];
			}
		} else {
			data = new byte[4];
		}

		final int dataLen = data.length;
		for (int i = 0; i < dataLen; i++) {
			data[i] = (byte) (value >> ((dataLen - 1 - i) * 8));
		}

		return data;
	}

	/**
	 * Converts the given int value to byte array having 4 bytes.
	 *
	 * @param value
	 *            the int to be converted
	 * @return byte array representing the give value. The byte array is 4 bytes
	 *         long
	 */
	public static byte[] intToByteArray(final int value) {
		return intToByteArray(value, false);
	}

	/**
	 * Converts a given int value to byte array with a given len. If the
	 * byteArrLen is less than the required length to convert a given value,
	 * then then this method returns null. Example, passing int value 300 and
	 * expecting a 1 byte long return buffer is not acceptable. In this
	 * scenario, this method will return null.
	 *
	 * @param value
	 *            int to be converted
	 * @param byteArrLen
	 *            the desired length of the buffer to be returened. This is
	 *            expected to have values from 1-4. Other values will return a
	 *            null value.
	 * @return The byte array representing the given value.
	 */
	public static byte[] nonNegativeIntToByteArray(final int value,
												   final int byteArrLen) {
		byte[] buffer = new byte[0];

		// check value is negative or not
		if (value >= 0 && byteArrLen >= 0) {
			// byteArrLen is expected to have values from 1-4

			// Check if desired byte array len in able to accommodate given
			// value
			switch (byteArrLen) {
				case 1: {
					if (value <= 255) {
						buffer = new byte[byteArrLen];
					}
				}
				break;
				case 2: {
					if (value <= 65535) {
						buffer = new byte[byteArrLen];
					}
				}
				break;
				case 3: {
					if (value <= 16777215) {
						buffer = new byte[byteArrLen];
					}
				}
				break;
				case 4: {
					buffer = new byte[byteArrLen];
				}
				break;
				default:
					// default value is added here
					buffer = new byte[0];
					break;
			}

			if (buffer.length == byteArrLen) {
				final int dataLen = byteArrLen;
				for (int i = 0; i < dataLen; i++) {
					buffer[i] = (byte) (value >> ((dataLen - 1 - i) * 8));
				}
			}

		} else {
			throw new IllegalArgumentException("Unexpected negative value");

		}

		return buffer;
	}

	/**
	 * Method to convert a byte array to integer
	 *
	 * @param value
	 *            the byte array to be converted
	 * @return
	 */
	// [nebarle] Suppressing warning for UseVarags for the last (ONLY) parameter
	// of this method
	public static int byteArrayToInt(final byte[] value) { // NOPMD
		return value[3] & 0xFF | (value[2] & 0xFF) << 8
				| (value[1] & 0xFF) << 16 | (value[0] & 0xFF) << 24;
	}

	/**
	 * Converts an hexadecimal string into an array of bytes (each bytes
	 * representing the value of a hexadecimal value Examples: "313233" => "123"
	 * = 0x31, 0x32, 0x33
	 *
	 * @param hexaString
	 *            hexadecimal string (2 characters represents 1 hexadecimal
	 *            number)
	 * @return the corresponding byte value
	 */
	public static byte[] hexaStrToByteArray(final String hexaString) {
		String finalHexa = hexaString;
		if (finalHexa.length() == 1) {
			finalHexa = "0".concat(finalHexa);
		}

		final int lByte = (finalHexa.length() + 1) / 2;
		byte[] baRes = new byte[lByte];

		for (int i = 0; i < lByte; i++) {
			baRes[i] = (byte) Integer.parseInt(
					finalHexa.substring(2 * i, 2 * i + 2), 16);
		}

		return baRes;
	}

	/**
	 * Converts byte array to a hexadecimal string.
	 *
	 * @param dataToPrint
	 *            the byte array to be converted
	 * @return The hexadecimal string representing the byte array.
	 */
	public static String byteArrayToHexaStr(final byte[] dataToPrint) {
		if (dataToPrint == null) {
			return "";
		}

		final int dataLen = dataToPrint.length;
		final StringBuffer buff = new StringBuffer(dataLen * 2);

		for (int i = 0; i < dataLen; i++) {
			buff.append(convertDigit(dataToPrint[i] >> 4));
			buff.append(convertDigit(dataToPrint[i] & 15));
		}

		return buff.toString().toUpperCase();
	}

	/**
	 * Determines if the given byte array has been previously wiped.
	 *
	 * @param array
	 *            the byte array to be checked
	 * @return true, if the byte array is empty. Otherwise, false.
	 */
	public static boolean isByteArrayAllZeros(final byte[] array) {
		boolean isEmpty = true;

		if (array == null) {
			return isEmpty;
		}

		final int len = array.length;
		for (int i = 0; i < len; i++) {
			if (array[i] != (byte) 0x00) {
				isEmpty = false;
				break;
			}
		}

		return isEmpty;
	}

	public static boolean isByteArrayEmpty(final byte[] array) {
		return (array == null || array.length == 0);
	}

	/**
	 * Converts an integer into an hexadecimal string. <BR>
	 * Examples: -1 => "FFFFFFFF"
	 *
	 * @param num
	 *            The integer to be converted.
	 * @return The corresponding hexadecimal string.
	 */
	public static String integerToHexaStr(final int num) {

		final StringBuffer strBuf = new StringBuffer();
		strBuf.append(Integer.toHexString(num).toUpperCase(Locale.getDefault()));

		if ((strBuf.length() % 2) != 0) {
			strBuf.insert(0, '0');
		}

		return strBuf.toString();
	}

	/**
	 * Converts an integer into an hexadecimal string. <BR>
	 * Examples: -1 => "FFFFFFFF"
	 *
	 * @param num
	 *            The integer to be converted.
	 * @param numberOfDigits
	 *            number of digits expected in the result
	 * @return The corresponding hexadecimal string.
	 */
	public static String integerToHexaStr(final int num,
                                          final int numberOfDigits) {

		final StringBuffer strBuf = new StringBuffer();
		strBuf.append(Integer.toHexString(num).toUpperCase(Locale.getDefault()));
		if ((strBuf.length() % 2) != 0) {
			strBuf.insert(0, '0');
		}

		final int strBufLen = strBuf.length();
		if (strBufLen % 2 > numberOfDigits) {
			strBuf.delete(0, (strBufLen - numberOfDigits));
		}

		return strBuf.toString();
	}

	/**
	 * Function to perform XOR encryption / decryption on the given data by a
	 * given key, using ECB mode for the block size of the size of the key.
	 *
	 * @param dataA
	 *            the data byte array that shall be encrypted (or decrypted) by
	 *            XOR.
	 * @param dataB
	 *            the key byte array
	 * @return The resulting byte array of the xor operation.
	 */
	public static byte[] deriveByXor(final byte[] dataA, final byte[] dataB) {
		byte[] result;

		if (dataA == null || dataA.length == 0 || dataB == null
				|| dataB.length == 0) {
			return new byte[0];
		}

		final int aLen = dataA.length;
		final int bLen = dataB.length;
		byte[] longer;
		byte[] shorter;

		if (aLen > bLen) {
			longer = new byte[aLen];
			shorter = new byte[bLen];

			System.arraycopy(dataA, 0, longer, 0, aLen);
			System.arraycopy(dataB, 0, shorter, 0, bLen);
		} else {
			longer = new byte[bLen];
			shorter = new byte[aLen];

			System.arraycopy(dataA, 0, shorter, 0, aLen);
			System.arraycopy(dataB, 0, longer, 0, bLen);
		}

		final int shortLen = shorter.length;
		final int longerLen = longer.length;
		result = new byte[longerLen];

		for (int i = 0; i < longerLen; i++) {
			result[i] = (byte) (longer[i] ^ shorter[i % shortLen]);
		}

		return result;
	}

	/**
	 * <p>
	 * Encrypts data with xor. The length of the resulting byte array will be
	 * the same as the length of the data.
	 * </p>
	 * <p>
	 * If the length of the data is shorter than the key then only a part of the
	 * key will be used.
	 * </p>
	 * <p>
	 * If the data is longer than the key, then the key is rotated until the
	 * data is fully encrypted.
	 * </p>
	 *
	 * @throws IllegalArgumentException
	 *             if the key is null
	 *
	 * @param data
	 *            first byte array.
	 * @param key
	 *            second byte array.
	 * @return byte[] resulting byte array of the XOR operation or null if the
	 *         data is null
	 */

	public static byte[] encryptByXor(final byte[] data, final byte[] key) {
		if (data == null) {
			return null;
		}

		if (key == null) {
			throw new IllegalArgumentException("Invalid arguments");
		}

		byte[] result = new byte[data.length];

		int index = 0;
		for (final byte b : data) {
			result[index] = (byte) (b ^ key[index % key.length]);
			index++;
		}
		return result;
	}

	/**
	 * Convert long value to byte array
	 *
	 * @param value
	 * @return
	 */
	public static byte[] longToByteArray(final long value) {
		return new byte[] { (byte) (value >> 56), (byte) (value >> 48),
				(byte) (value >> 40), (byte) (value >> 32),
				(byte) (value >> 24), (byte) (value >> 16),
				(byte) (value >> 8), (byte) value };
	}

	// ================================================================================
	// Private methods
	// ================================================================================
	/**
	 * Converts an integer to ascii character
	 *
	 * @param value
	 *            the int to convert
	 * @return char converted string
	 */
	private static char convertDigit(final int value) {
		int convertedValue = value;
		convertedValue &= 15;

		if (convertedValue >= 10) {
			return (char) (convertedValue - 10 + 'a');
		} else {
			return (char) (convertedValue + '0');
		}
	}


	/**
	 * Return a byte in the binary format string
	 *
	 * @param value
	 * @return
	 */
	public static String byteToBinaryFormat(final byte value) {
		final String output = "-> "
				+ String.format("%8s", Integer.toBinaryString(value & 0xFF))
				.replace(' ', '0');
		return output;

	}
}
