package kappapride.distruchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {

    EditText studieNummer;
    EditText password;
    Button login;

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
                //studieNummer.getText().toString().equals("s134527") && password.getText().toString().equals("yeezy")
                if(true){
                    Toast.makeText(getBaseContext(), "Connecting to service...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                }
            }
        });
    }
}
