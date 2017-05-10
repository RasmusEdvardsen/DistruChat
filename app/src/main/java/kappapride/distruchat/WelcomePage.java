package kappapride.distruchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class WelcomePage extends AppCompatActivity {

    EmoteController emoteController = EmoteController.getInstance();

    final String emoteURL = Config.emoteNameIdUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new LoadEmotes().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, emoteURL);
        } else {
            new LoadEmotes().execute(emoteURL);
        }

        Thread thread = new Thread(){
            public void run(){
                try{
                    Thread.sleep(3000);
                    startActivity(new Intent(getBaseContext(), LoginPage.class));
                    finish();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
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
                String string = HelperMethods.readStream(in);
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
            emoteController.emoteNameIdList = list;
            emoteController.setEmotesNamesIdsLoaded(true);
            emoteController.loadImages();
        }
    }
}
