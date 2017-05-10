package kappapride.distruchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginPage extends AppCompatActivity {

    Config cfg = Config.getInstance();

    EditText studieNummer;
    EditText password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        studieNummer = (EditText) findViewById(R.id.editTextStudieNummer);
        password = (EditText) findViewById(R.id.editTextPassword);
        login = (Button) findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = studieNummer.getText().toString();
                String pass = password.getText().toString();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new AuthAndRetrieve().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://130.226.195.227:30022/chat/api/auth/" + user + "/" + pass);
                } else {
                    new AuthAndRetrieve().execute("http://130.226.195.227:30022/chat/api/auth/" + user + "/" + pass);
                }

                Toast.makeText(getBaseContext(), "Connecting to service...", Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO: 03/05/2017 ALSO CHECK FOR IF USER ALREADY LOGGED IN!
    //Authenticating and retrieving User attempting to log in.
    public class AuthAndRetrieve extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String string = "";
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                string = HelperMethods.readStream(in);
                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return string;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject json = new JSONObject(s);
                if (json.getString("authenticated").equals("true")) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("responseBody", s);
                    startActivity(intent);
                } /*else {
                     TODO: 03/05/2017 FIND PLACE TO CREATE THIS TOAST!
                    Toast.makeText(getBaseContext(), "Failed authentication. Try again", Toast.LENGTH_SHORT).show();
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}