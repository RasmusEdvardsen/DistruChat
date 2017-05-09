package kappapride.distruchat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
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
    String prependedUserName = "";
    EmoteController emoteController = EmoteController.getInstance();
    NotificationCompat.Builder mBuilder;
    PendingIntent resultPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Config cfg = Config.getInstance();

        //test
        Log.i("emotelist", emoteController.getEmotesNamesIdsLoaded() + emoteController.emoteNameIdList.toString());

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
            socket = IO.socket(cfg.socketUrl);
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

        mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.kappa)
                        .setContentTitle("You received a new text")
                        .setContentText("Go to KappaChat to read new Messages");
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
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

        //Initial notifications.
        if(!text.startsWith(prependedUserName)){
            // Sets an ID for the notification
            int mNotificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
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
        tv.setBackgroundColor(Color.BLUE);
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