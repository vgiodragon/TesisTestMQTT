package com.smartcity.giodev.tesistestmqtt;


import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
    private static final String TAG = "GIODEBUG_UTILS";
    private static String userMqtt ="CTIC-SMARTCITY";
    private static String passMqtt ="YTICTRAMS-CITC";
    private static String topicMqtt ="C008/S0001/Contador";
    private static String topicMqttBACK ="C008/tesis/3g";
    //private static String TABLE_NAME="beagonslocal";
    //private static final String ip ="192.168.1.105"; //laptop
    private static String TABLE_NAME="beagonsglobal";
    private static final String ip ="beagons.uni.edu.pe";


    public static String getIp() {
        return ip;
    }

    public static String getUserMqtt() {
        return userMqtt;
    }

    public static String getTopicMqttBACK() {
        return topicMqttBACK;
    }

    public static String getPassMqtt() {
        return passMqtt;
    }

    public static String getTopicMqtt() {
        return topicMqtt;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static void SendCurrentHour(Context ctx){

        int random= (int) (Math.random()*12345);

        final MqttAndroidClient mqttAndroidClient
                = new MqttAndroidClient(ctx, "tcp://"+
                getIp()+":1883", "GIOTESTIS"+random);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        options.setUserName("Raspberry");
        options.setPassword("yrrebpsaR".toCharArray());
        options.setAutomaticReconnect(true);
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        try {
            IMqttToken token = mqttAndroidClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    //ready=true;
                    try {
                        mqttAndroidClient.publish(getTopicMqtt(), new MqttMessage(
                                sdf.format(new Date()).getBytes()));
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    //Log.d(TAG, "No conectaste onFailure");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }

        //Log.d(TAG, "Listo para mandar");
    }

}
