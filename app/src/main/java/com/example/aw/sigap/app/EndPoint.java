package com.example.aw.sigap.app;

/**
 * Created by AW on 2/15/2017.
 */

public class EndPoint {

//    public static final String BASE_URL = "http://192.168.1.115/Sigap-Server/v1";
//    public static final String URL_ALAT = BASE_URL + "/getalatuser";
//    public static final String URL_DATA = BASE_URL + "/databyidalat";
//    public static final String URL_LOGIN = BASE_URL + "/login";
//    public static final String URL_REGISTER = BASE_URL + "/register";
//    public static final String URL_SETTINGS = BASE_URL + "/settingsalat";
//    public static final String URL_PREDICTION = "http://192.168.43.98:33/predict";
//    public static final String URL_ALAT = BASE_URL + "/device/list";

    public static final String BASE_URL = "http://192.168.0.108:3000/api";
    public static final String URL_ALAT = BASE_URL + "/devices";
    public static final String URL_CREATE_ALAT = BASE_URL + "/device/create";
    public static final String URL_DELETE_ALAT = BASE_URL + "/device";

    public static final String URL_NODES = BASE_URL + "/sensornode";
    public static final String URL_NODES2 = BASE_URL + "/sensornodes";
    public static final String URL_CREATE_NODE = BASE_URL + "/sensornode/create";
    public static final String URL_DELETE_NODE = BASE_URL + "/sensornode";


    public static final String URL_DATA = BASE_URL + "/dataset";
    public static final String URL_LOGIN = BASE_URL + "/signin";
    public static final String URL_REGISTER = BASE_URL + "/signup";


    public static final String URL_TYPES = BASE_URL + "/sensortype";

    public static final String URL_PREDICTION = BASE_URL + "/prediction/gets";
    public static final String URL_CONTROL = BASE_URL + "/control";
    public static final String URL_USER = BASE_URL + "/user";
}