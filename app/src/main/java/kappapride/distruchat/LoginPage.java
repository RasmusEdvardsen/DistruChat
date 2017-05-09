package kappapride.distruchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoginPage extends AppCompatActivity {

    final String emoteURL = "https://twitchemotes.com/api_cache/v2/global.json";

    EditText studieNummer;
    EditText password;
    Button login;

    static AuthAndRetrieve authAndRetrieve;
    static LoadEmotes loadEmotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loadEmotes = new LoadEmotes();
        loadEmotes.execute(emoteURL);

        studieNummer = (EditText) findViewById(R.id.editTextStudieNummer);
        password = (EditText) findViewById(R.id.editTextPassword);
        login = (Button) findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = studieNummer.getText().toString();
                String pass = password.getText().toString();

                authAndRetrieve = new AuthAndRetrieve();
                authAndRetrieve.execute("http://130.226.195.227:30022/chat/api/auth/" + user + "/" + pass);
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

    //Loading emotename@emoteid into EmoteController for all emotes.
    public class LoadEmotes extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> jsonEmotes = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String string = readStream(in);
                JSONObject json = new JSONObject(string).getJSONObject("emotes");
                Iterator<String> temp = json.keys();
                while (temp.hasNext()) {
                    String key = temp.next();
                    String id = json.getJSONObject(key).getString("image_id");
                    jsonEmotes.add(key+"@"+id);
                }
                urlConnection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonEmotes;
        }

        @Override
        protected void onPostExecute(ArrayList<String> list) {
            super.onPostExecute(list);
            EmoteController emoteController = EmoteController.getInstance();
            emoteController.emoteList = list;
        }
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}