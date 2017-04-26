package kappapride.distruchat;

import android.graphics.Color;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ScrollView scrollView;
    RelativeLayout relativeLayout;
    View v = null;
    EditText et;
    Socket socket;
    ImageView selfMessageButton;
    Response response = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        et = (EditText) findViewById(R.id.messageFieldMessage);
        selfMessageButton = (ImageView) findViewById(R.id.selfMessageButton);



        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        try{
            socket = IO.socket("http://192.168.43.21:3000");
        }catch (URISyntaxException e){
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, onConnect);
        //TODO: socket on event message received "chat message" probably.
        socket.connect();
        socket.emit("chat message", "Welcome to the hive. Cancer runs rampant here. Good luck.");}

    public void selfMessage(View v){
        //TODO: If cr/nl, then just print msg.
        String message = et.getText().toString().trim();
        if (!message.isEmpty()){
            createMessage(message, true);
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

    public void createMessage(String text, boolean isSelf){
        TextView tv = new TextView(this);
        RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(v != null){
            tvParams.addRule(RelativeLayout.BELOW, v.getId());
            tv.setId(View.generateViewId());
        }else{
            tv.setId(View.generateViewId());
        }
        if(isSelf){
            tvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tvParams.topMargin = 20;
            tv.setPadding(20, 20, 20, 20);
            tv.setBackgroundColor(Color.BLUE);
            tv.setTextColor(Color.WHITE);
        }else{
            tvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            tvParams.topMargin = 20;
            tv.setPadding(20, 20, 20, 20);
            tv.setBackgroundColor(Color.LTGRAY);
            tv.setTextColor(Color.argb(255, 0, 0, 0));
        }
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