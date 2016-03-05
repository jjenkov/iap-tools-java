package com.jenkov.iap.message;

/**
 * Created by jjenkov on 21-02-2016.
 */
public class IapMessageHeaders {

    /* Routing related headers */
    public static final int SENDER_ID_KEY_FIELD_VALUE                 = 1;
    public static final int RECEIVER_ID_KEY_FIELD_VALUE               = 2;

    public static final int CONNECTION_ID_KEY_FIELD_VALUE             = 3; //will this ever be used? Still reserve it?
    public static final int CHANNEL_ID_KEY_FIELD_VALUE                = 4;

    public static final int SEQUENCE_ID_KEY_FIELD_VALUE               = 5;
    public static final int SEQUENCE_INDEX_KEY_FIELD_VALUE            = 6;
    public static final int IS_LAST_IN_SEQUENCE_KEY_FIELD_VALUE       = 7;

    /* Action related headers */
    public static final int SEMANTIC_PROTOCOL_ID_KEY_FIELD_VALUE      = 16;
    public static final int SEMANTIC_PROTOCOL_VERSION_KEY_FIELD_VALUE = 17;

    public static final int MESSAGE_TYPE_KEY_FIELD_VALUE              = 18;








}
