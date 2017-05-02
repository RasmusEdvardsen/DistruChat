package kappapride.distruchat;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        try{
            socket = IO.socket("http://192.168.43.13:3000");
        }catch (URISyntaxException e){
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on("chat message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String receivedMsg = args[0].toString().trim();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!receivedMsg.isEmpty()){
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
        //TODO: socket on event message received "chat message" probably.
        socket.connect();
        socket.emit("chat message", "Welcome to the hive. Cancer runs rampant here. Good luck.");
    }

    //Run when self posts. This will also send messages to server.
    public void selfMessage(View v){
        //TODO: If cr/nl, then just print msg.
        String message = et.getText().toString().trim();
        if (!message.isEmpty()){
            et.setText("");
            et.setHint("Write a message");
            try {
                socket.emit("chat message", message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
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
    public void createMessage(String text){
        TextView tv = new TextView(this);
        RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(v != null){
            tvParams.addRule(RelativeLayout.BELOW, v.getId());
            tv.setId(View.generateViewId());
        }else{
            tv.setId(View.generateViewId());
        }
        //DER SKAL CENTRERES!
        tvParams.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
        tvParams.topMargin = 20;
        tv.setPadding(20, 20, 20, 20);
        tv.setBackgroundColor(Color.BLUE);
        tv.setTextColor(Color.WHITE);
        tv.setLayoutParams(tvParams);

        //Initial handling of emojis.
        //https://stackoverflow.com/questions/3341702/displaying-emoticons-in-android'
        /*SpannableString spannableString = new SpannableString("abc");
        Drawable drawable = getResources().getDrawable(R.drawable.kappa);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannableString.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString);*/

        tv.setText(text);

        v = tv;
        relativeLayout.addView(tv);
    }

    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            socket.emit("chat message", socket.id()+" connected.");
        }
    };
}