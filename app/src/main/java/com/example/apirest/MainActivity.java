package com.example.apirest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText et;
    private TextView tv;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et = ((EditText) findViewById(R.id.editText));
        tv = ((TextView) findViewById(R.id.textView));
        et.setText("http://10.0.2.2:8080/");
        context = MainActivity.this;
    }

    public void llamada(View v) {
        if(et.getText().toString().length()>0) {
            MyATaskCliente myATaskYW = new MyATaskCliente();
            myATaskYW.execute(et.getText().toString());
        } else {
            Toast.makeText(context, "Escribe la URL a atacar", Toast.LENGTH_LONG).show();
        }
    }

    class MyATaskCliente extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String pp = et.getText().toString();
            StringBuffer sb = new StringBuffer();

            try {
                URL url = new URL(pp);
                HttpURLConnection myConnection = (HttpURLConnection) url.openConnection();
                myConnection.setRequestMethod("GET");
                Log.i("test", "holaaaa");
                if(myConnection.getResponseCode() == 200) {
                    Log.i("test", "connection succeed");
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody,"UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginArray();
                    jsonReader.setLenient(true);
                    while(jsonReader.hasNext()) {
                        jsonReader.beginObject();
                        while(jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            if (key.equals("id")) {
                                sb.append("{id:" +jsonReader.nextString()+",");
                            } else if (key.equals("name")) {
                                sb.append("name:" + jsonReader.nextString() +"}\n");
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject();
                    }
                } else {
                    Log.i("failed api connection", "a");
                    tv.setText("No ha ido");
                }
            } catch (Exception e) {
                Log.i("test", "exception");
                System.out.println(e.getMessage());
            }
            Log.i("test", "return");
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String value) {
            tv.setText(value);
        }
    }
}

