package kappapride.distruchat;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by buller on 23/04/2017.
 */
public class HelperMethods extends AppCompatActivity{

    static Context context;
    static RelativeLayout relativeLayout;
    static View v;

    public HelperMethods(Context context, RelativeLayout relativeLayout){
        this.relativeLayout = relativeLayout;
        this.context = context;
    }

    public static void createMessage(String text, boolean isSelf, Context ctx, RelativeLayout relativeLayout){
        TextView tv = new TextView(ctx);
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
        tv.setText(text);
        v = tv;
        relativeLayout.addView(tv);
    }
}
