package kappapride.distruchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.protocol.HTTP;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginPage extends AppCompatActivity {

    EditText studieNummer;
    EditText password;
    Button login;

    User userGlobal;

    static AuthAndRetrieve authAndRetrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        studieNummer = (EditText) findViewById(R.id.editTextStudieNummer);
        password = (EditText) findViewById(R.id.editTextPassword);
        login = (Button) findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String user = studieNummer.getText().toString();
                String pass = password.getText().toString();

                authAndRetrieve = new AuthAndRetrieve();
                authAndRetrieve.execute("http://130.226.195.227:30022/chat/api/auth/"+user+"/"+pass);
                Toast.makeText(getBaseContext(), "Connecting to service...", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO: 03/05/2017 ALSO CHECK FOR IF USER ALREADY LOGGED IN!
    public class AuthAndRetrieve extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String string = "";
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                string = readStream(in);
                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return string;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject json = new JSONObject(s);
                if(json.getString("authenticated").equals("true")){
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("responseBody", s);
                    startActivity(intent);
                }else{
                    // TODO: 03/05/2017 FIND PLACE TO CREATE THIS TOAST!
                    //Toast.makeText(getBaseContext(), "Failed authentication. Try again", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}