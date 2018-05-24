package com.smartcity.giodev.tesistestmqtt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcity.giodev.tesistestmqtt.AccessDataBase.FeedReaderContract;
import com.smartcity.giodev.tesistestmqtt.AccessDataBase.FeedReaderDBHelper;

import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FeedReaderDBHelper mDbHelper;

    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    int PERMISSION_ALL = 5;
    private final int code_request=1234;
    Suscriptor subcriptor;
    TextView norecibido;
    private final String TAG="GIODEBUG_main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!hasPermissions(this, PERMISSIONS))
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

        norecibido =  findViewById(R.id.tVNoRecibido);
        norecibido.setText("");
        TextView zona = (TextView) findViewById(R.id.tvZona);
        zona.setText(Utils.getTableName());
    }

    public void startService(View view){
        //Intent intent = new Intent(this, MyServiceMQTT.class);
        //startService(intent);
        subcriptor = new Suscriptor(getApplicationContext(), Utils.getIp(), Utils.getTableName()
                //,new MqttDefaultFilePersistence(getDir("mqtt", MODE_PRIVATE).getAbsolutePath())
                ,norecibido);
        subcriptor.creoClienteMQTT();

    }

    public void stopService(View view){
        //Intent intent = new Intent(this, MyServiceMQTT.class);
        //stopService(intent);
        if(subcriptor!=null){
            subcriptor.CancelarSuscripcion();
        }
    }

    public void saveFile(View view){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Beagons");
        String filename = Utils.getTableName()+".csv";

        File file = new File (myDir, filename);
        try {
            FileOutputStream out = new FileOutputStream(file);
            //out.write(string.getBytes());
            ArrayList<Publicacion> publicacions = Read(Utils.getTableName());
            String titlesCSV = "fecha_llegada, hora_llegada, fecha_llegada_ntp, hora_llegada_ntp, fecha_envio, hora_envio, value\n";
            out.write(titlesCSV.getBytes());
            for (Publicacion publicacion : publicacions)
                out.write(publicacion.toString().getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Saved!!", Toast.LENGTH_SHORT)
                .show();
    }



    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case code_request:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "COARSE LOCATION permitido", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "COARSE LOCATION no permitido", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public ArrayList<Publicacion> Read(String TABLE_NAME){
        mDbHelper = new FeedReaderDBHelper(getApplicationContext(),TABLE_NAME);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // How you want the results sorted in the resulting Cursor

        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
// Define a projection that specifies which columns from the database
// you will actually use after this query.

        ArrayList <Publicacion>elemtns = new ArrayList<>();

        while(cursor.moveToNext()) {
            String name =cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE));
            String horaNTP =cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NTP));

            String hora =cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE));

            String horat[] = hora.split("_");
            String horat2[] = horaNTP.split("_");
            try {
                JSONObject jsonObject = new JSONObject(name);
                String hora2[]=jsonObject.getString("date").split(" ");
                String fecha_llegada = horat[0].substring(0,2)+"/"
                        +horat[0].substring(2,4)+"/"+horat[0].substring(4);
                String hora_llegada = horat[1];

                String fecha_llegada_ntp = horat2[0].substring(0,2)+"/"
                        +horat2[0].substring(2,4)+"/"+horat2[0].substring(4);
                String hora_llegada_ntp = horat2[1];

                String fecha_envio2 = hora2[0].replace("-","/");
                Publicacion mpublicacion =new Publicacion(fecha_llegada,hora_llegada,
                        fecha_llegada_ntp,hora_llegada_ntp,
                        fecha_envio2.substring(2),hora2[1],jsonObject.getDouble("value"));
                elemtns.add(mpublicacion);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Log.d(TAG,"size: "+elemtns.size());
        cursor.close();
        return elemtns;
    }

    public void RestartAll(View view){
        DropTable(Utils.getTableName());
        if(subcriptor!=null){
            subcriptor.restart();
            //subcriptor.CancelarSuscripcion();
        }
    }

    public void DropTable(String TABLE_NAME){
        mDbHelper = new FeedReaderDBHelper(getApplicationContext(),TABLE_NAME);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //db.delete(TABLE_NAME,"",new String[1]);
        mDbHelper.onUpgrade(db,mDbHelper.getDatabaseVersion(),2);

        if(subcriptor!=null){
            subcriptor.CancelarSuscripcion();
        }

    }


}
