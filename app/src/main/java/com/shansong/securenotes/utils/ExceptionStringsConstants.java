package com.shansong.securenotes.utils;

/**
 * Created by sunshine on 7/5/17.
 */

public class ExceptionStringsConstants {

    /**
     * Generic exception message for an invalid parameter.
     */
    public static final String EXCEPTION_INVALID_PARAM = "Invalid parameter.";

    /**
     * Generic exception message for maximum cards allowed limitation exceeded.
     */
    public static final String MAXIMUM_CARDS_ALLOWED = "Exceed the maximum number of installed cards.";


    /**
     * Exception message when the tokenized card id is either null or empty.
     */
    public static final String EXCEPTION_INVALID_TOKENIZED_CARD_ID = "Invalid tokenized card id.";

    /**
     * Exception message when the tokenized card id already exists, and it is
     * being provisioned again.
     */
    public static final String EXCEPTION_TOKENIZED_CARD_ID_EXISTS = "Tokenized card id previously provisioned.  Unable to provision again.";

    /**
     * Message when the module is unable to properly retrieve a digitalized card
     * id.
     */
    public static final String EXCEPTION_TOKENIZED_CARD_ID_RETRIEVE_FAIL = "Unable to retrieve tokenized card id.";

    /**
     * Exception message when attempting to read/write to the secure storage
     * using an invalid SecureStorageKeyType
     */
    public static final String EXCEPTION_UNSUPPORTED_STORAGE_KEY_TYPE = "Unknown or unsupported SecureStorageKeyType.";

    /**
     * Exception message when the SDK is unable to retrieve the correct storage
     * key.
     */
    public static final String EXCEPTION_UNABLE_TO_GENERATE_STORAGE_KEY = "Unable to retrieve correct secure storage key.";

    /**
     * Exception message when an invalid Android context is used.
     */
    public static final String EXCEPTION_INVALID_CONTEXT = "Cannot resolve context";

    /**
     * Exception message when the secure storage failed to initialize.
     */
    public static final String EXCEPTION_SS_INITIALIZE_FAIL = "Unable to initialize secure storage.";

    /**
     * Exception message when the secure storage failed to remove a key-value.
     */
    public static final String EXCEPTION_SS_WIPE_FAIL = "Unable to clear properties storage.";

    /**
     * Exception message when the secure storage failed to retrieve a key-value
     * pair.
     */
    public static final String EXCEPTION_SS_READ_FAIL = "Unable to retrieve key-value pair.";

    /**
     * Exception message when the secure storage failed to write a key-value
     * pair.
     */
    public static final String EXCEPTION_SS_WRITE_FAIL = "Unable to store key-value pair.";

    /**
     * Exception message when the SDK is unable to generate SHA256 data.
     */
    public static final String EXCEPTION_CRYPTO_SHA256 = "Unable to generate SHA256.";

    /**
     * Exception message when the SDK is unable to retrieve the value of a given
     * corresponding key from a given json string.
     */
    public static final String EXCEPTION_JSON_INVALID_OBJECT = "Invalid json object.";

    /**
     * Message when attempting to read or write a key-value pair for an
     * unsupported payment type.
     */
    public static final String EXCEPTION_UNSUPPORTED_PAYMENT_TYPE = "Unsupported payment type.";

    /**
     * Message when retrieving the card detail profile fails.
     */
    public static final String EXCEPTION_CARD_PROFILE_FAIL = "Unable to retrieve proper card detail profile.";

    /**
     * Message when update additional data
     */
    public static final String EXCEPTION_CARD_PROFILE_UPDATED_NOT_ALLOWED = "Update is not allowed for the productID and PAN last 4 digits.";

    private ExceptionStringsConstants() {
    }

}
