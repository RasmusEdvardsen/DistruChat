package kappapride.distruchat;

import android.content.Intent;
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

import cz.msebera.android.httpclient.Header;

public class LoginPage extends AppCompatActivity implements IAuthenticate {

    EditText studieNummer;
    EditText password;
    Button login;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        studieNummer = (EditText) findViewById(R.id.editTextStudieNummer);
        password = (EditText) findViewById(R.id.editTextPassword);
        login = (Button) findViewById(R.id.loginButton);

        final Authenticate auth = new Authenticate(this);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //s130022 hejven123
                auth.AuthAndRetrieve(studieNummer.getText().toString(), password.getText().toString());

                if(!user.username.isEmpty()){
                    Toast.makeText(getBaseContext(), "Connecting to service...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                }
            }
        });
    }
    @Override
    public void callback(User user){
        this.user = user;
        Log.i("userTestNew", this.user.username);
    }
}
