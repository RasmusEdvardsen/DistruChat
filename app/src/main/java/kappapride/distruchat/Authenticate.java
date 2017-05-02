package kappapride.distruchat;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by buller on 02/05/2017.
 */
public class Authenticate {
    public IAuthenticate iAuthenticate;
    public Authenticate(IAuthenticate authInterface){
        this.iAuthenticate = authInterface;
    }
    public void AuthAndRetrieve(String username, String pass){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.43.13:3000/chat/api/auth/"+username+"/"+pass, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    JSONObject json = new JSONObject(new String(responseBody));
                    User user = new User(json.getJSONObject("user").getString("userName"));
                    Log.i("json", json.toString());
                    Log.i("json", user.username);
                    iAuthenticate.callback(user);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                Log.i("status", String.valueOf(statusCode));
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("status", String.valueOf(statusCode));
            }
        });
    }
}
