package kappapride.distruchat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    ScrollView scrollView;
    RelativeLayout relativeLayout;
    View v = null;
    EditText et;
    Socket socket;
    ImageView selfMessageButton;
    EmoteController emoteController = EmoteController.getInstance();
    String prependedUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO: 10/05/2017 Maybe find other way to do following 2 lines (center align app name + custom text).
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        // TODO: 09/05/2017 Retrieve messages from server when reentering again

        //Response msg from AuthAndRetrieve functionality.
        try {
            JSONObject json = new JSONObject(getIntent().getExtras().getString("responseBody"));
            prependedUserName = json.getJSONObject("user").getString("userName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Pointing objects to XML.
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        et = (EditText) findViewById(R.id.messageFieldMessage);
        selfMessageButton = (ImageView) findViewById(R.id.selfMessageButton);

        //Forcing down focus from start. Limited functionality, one in createMessage does the bulk work.
        //Mostly for posterity - in case we support fetching messages.
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        //Socket initialization.
        try {
            socket = IO.socket(Config.socketUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on("server info", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String receivedMsg = args[0].toString().trim();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!receivedMsg.isEmpty()) {
                            createMessage(receivedMsg);
                        }
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                });
            }
        });
        socket.on("chat message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String receivedMsg = args[0].toString().trim();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!receivedMsg.isEmpty()) {
                            createMessage(receivedMsg);
                        }
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                });
            }
        });
        socket.connect();
    }

    //Run when self posts. This will also send messages to server.
    public void selfMessage(View v) {
        //TODO: If cr/nl, then just print msg.
        String message = et.getText().toString().trim();
        if (!message.isEmpty()) {
            et.setText("");
            et.setHint("Write a message");
            try {
                socket.emit("chat message", prependedUserName + ": " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getBaseContext(), "    The fuck you doing?\nWrite something you fool.", Toast.LENGTH_SHORT).show();
            et.setText("");
            et.setHint("Write a message");
        }
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    //Creating messages to be viewed.
    public void createMessage(String text) {

        // TODO: 10/05/2017 Handle only sending notifications when actual message from user!.
        if (!text.startsWith(prependedUserName)) {
            HelperMethods.generateNotification(getBaseContext(), prependedUserName, text);
        }

        TextView tv = new TextView(this);
        RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (v != null) {
            tvParams.addRule(RelativeLayout.BELOW, v.getId());
            tv.setId(View.generateViewId());
        } else {
            tv.setId(View.generateViewId());
        }
        //DER SKAL CENTRERES!
        tvParams.topMargin = 20;
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(20, 20, 20, 20);
        tv.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorNewPrimary));
        tv.setTextColor(Color.WHITE);
        tv.setLayoutParams(tvParams);

        if (emoteController.getEmoteImgsLoaded()) {
            tv.setText(emoteController.createSpannableString(getBaseContext(), text));
        } else {
            Toast.makeText(getBaseContext(), "Still fetching emotes...", Toast.LENGTH_SHORT).show();
            tv.setText(text);
        }

        v = tv;
        relativeLayout.addView(tv);
    }
}