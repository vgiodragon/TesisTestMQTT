package com.smartcity.giodev.tesistestmqtt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcity.giodev.tesistestmqtt.AccessDataBase.FeedReaderContract;
import com.smartcity.giodev.tesistestmqtt.AccessDataBase.FeedReaderDBHelper;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Suscriptor{
    private final String TAG="GIODEBUG_Sub";
    private Context ctx;
    private static MqttAndroidClient mqttAndroidClient;
    private String ip;
    FeedReaderDBHelper mDbHelper;
    String TABLE_NAME;
    TextView norecibidos;
    static double lastvalue;
    //static String lastvalue;

    public Suscriptor(Context ctx, String ip,String TABLE_NAME,
                      //MqttDefaultFilePersistence filePersistence,
                      TextView norecibidos) {
        this.ctx = ctx;
        this.ip = ip;
        this.TABLE_NAME = TABLE_NAME;
        mDbHelper = new FeedReaderDBHelper(ctx,TABLE_NAME);
        //this.filePersistence = filePersistence;
        this.norecibidos = norecibidos;
        lastvalue = -0.9f;

        int random= (int) (Math.random()*123456);
        mqttAndroidClient
                = new MqttAndroidClient(ctx,  "tcp://"+
                ip+":1883", "GioMovil"+random);

        /*TrueTimeRx.build()
                .initializeRx("time.google.com")
                .subscribeOn(Schedulers.io())
                .subscribe(date -> {
                    Log.d(TAG, "output_3 TrueTime was initialized and we have a time: " + date);
                }, throwable -> {
                    throwable.printStackTrace();
                });
                */
    }

    public void restart(){
        lastvalue = -0.9f;
        norecibidos.setText("");
    }

    public void creoClienteMQTT(){
        if(! mqttAndroidClient.isConnected()) {

            MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            options.setUserName(Utils.getUserMqtt());
            options.setPassword(Utils.getPassMqtt().toCharArray());
            options.setCleanSession(false);

            options.setAutomaticReconnect(true);
            //options.setKeepAliveInterval(20000);
            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG, "Llego del topic " + topic + ": " + new String(message.getPayload()));
                    Date date = new Date(System.currentTimeMillis());
                    Insert(TABLE_NAME, new String(message.getPayload()), date);
                    //Read(TABLE_NAME);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            try {
                IMqttToken token = mqttAndroidClient.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d(TAG, "IMqttActionListener_onSuccess_" + Utils.getTopicMqtt() + " on");

                        Toast.makeText(ctx, "CONECTADO!", Toast.LENGTH_LONG).show();
                        try {
                            mqttAndroidClient.subscribe(Utils.getTopicMqtt(), 0);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("GIODEBUG_MQTT_SA", "IMqttActionListener_onFailure_" + asyncActionToken.toString());
                    }

                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Suscribiendo");
        }else{

            Toast.makeText(ctx, "Ya estas suscrito", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Ya estas suscrito");
        }
    }

    public void Insert(String TABLE_NAME, String mnsj, Date ntp){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currentDateandTime = sdf.format(new Date());
        String ntpTime = sdf.format(ntp);
// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NTP, String.valueOf(ntpTime));
        values.put(FeedReaderContract.FeedEntry.COLUMN_HORA_LLEGADA, String.valueOf(currentDateandTime));
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE,mnsj);

        // Insert the new row, returning the primary key value of the new row
        db.insert(TABLE_NAME, null, values);
        try {
            JSONObject jsonObject = new JSONObject(mnsj);
            Double nuevo = jsonObject.getDouble("value");
            String norecived = "\n";
            lastvalue += 1.f;

            while(lastvalue < nuevo){
                norecived = norecived+"\n"+String.format ("%.1f",lastvalue-0.1f) +" "+String.valueOf(currentDateandTime);
                lastvalue +=1.f;
            }

            norecibidos.setText(norecibidos.getText()+norecived);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(ctx,"insertado",Toast.LENGTH_SHORT).show();
    }

    private void Unsuscribe(){
        try {
            IMqttToken unsubToken = mqttAndroidClient.unsubscribe(Utils.getTopicMqtt());
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                    Log.d("GIODEBUG_MQTT", "Unsubcribe: onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("GIODEBUG_MQTT", "Unsubcribe: onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void Disconnect(){
        try {
            IMqttToken disconToken = mqttAndroidClient.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                    Log.d("GIODEBUG_MQTT", "Disconnect: onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    Log.d("GIODEBUG_MQTT", "Disconnect: onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void CancelarSuscripcion(){
        if(mqttAndroidClient!=null)
            if (mqttAndroidClient.isConnected()){
                Unsuscribe();
                Disconnect();
            }
    }
}