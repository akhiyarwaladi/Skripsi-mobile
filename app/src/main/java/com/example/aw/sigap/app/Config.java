package com.example.aw.sigap.app;

/**
 * Created by AW on 2/15/2017.
 */

public class Config {
    // flag to identify whether to show single line
    // or multi line text in push notification tray
    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    public static final String USER_AUTHORIZATION = "ed7edb7931ff62ca7275630ddedfa617";
    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "user";
    public static final String SHARED_PREF_API = "api";

    //This would be used to store the username of current logged in user
    public static final String USERNAME_SHARED_PREF = "username";
    public static final String APIKEY_SHARED_PREF = "apiKey";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
    public static final String API_SHARED_PREF = "api";
}
