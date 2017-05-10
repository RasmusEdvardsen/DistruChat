package kappapride.distruchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by buller on 03/05/2017.
 */
public class EmoteController {

    private static EmoteController instance = null;

    private static boolean emotesNamesIdsLoaded = false;

    private static boolean emoteImgsLoaded = false;

    public ArrayList<String> emoteNameIdList = new ArrayList<>();

    // TODO: 09/05/2017 Create SQLite DB for this.
    public ArrayList<Bitmap> emoteImgArray = new ArrayList<>();

    static LoadEmoteImgs loadEmoteImgs;

    public static EmoteController getInstance() {
        if (instance == null) {
            instance = new EmoteController();
        }
        return instance;
    }

    public boolean getEmotesNamesIdsLoaded() {
        return emotesNamesIdsLoaded;
    }

    public void setEmotesNamesIdsLoaded(boolean bool) {
        emotesNamesIdsLoaded = bool;
    }

    public boolean getEmoteImgsLoaded() {
        return emoteImgsLoaded;
    }

    public void setEmoteImgsLoaded(boolean bool) {
        emoteImgsLoaded = bool;
    }
    
    public SpannableString createSpannableString(Context ctx, String string) {
        String[] words = string.split(" ");
        ArrayList<String> toMatch = new ArrayList<>();
        for(int i = 0; i < emoteNameIdList.size(); i++){
            String[] temp = emoteNameIdList.get(i).split("@");
            toMatch.add(temp[0]);
        }
        Log.i("toMatch", toMatch.toString());
        Log.i("length", String.valueOf(words.length));
        SpannableString ss = new SpannableString(string);
        Log.i("test", emoteNameIdList.toString());

        // TODO: 09/05/2017 OPTIMIZE!.
        //This is very inefficient. The for loop iterates through all words, even though same words
        //are caught by inner loop.
        for (int i = 0; i < words.length; i++) {
            if(words[i].matches("[^a-zA-Z]+")){
                continue;
            }
            for (int j = 0; j < toMatch.size(); j++) {
                if (toMatch.get(j).matches(words[i])) {
                    ArrayList<Integer> temp = new ArrayList<>();
                    Integer index = string.indexOf(words[i]);
                    while(index >= 0) {
                        ss.setSpan(new ImageSpan(ctx, emoteImgArray.get(j)), index, index+words[i].length(), 0);
                        temp.add(index);
                        Log.i("test", temp.toString());
                        index = string.indexOf(words[i], index+1);
                    }
                    Log.i("test", temp.toString());
                }
            }
        }
        return ss;
    }

    public void loadImages() {
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 0; i < emoteNameIdList.size(); i++) {
            String[] temptemp = emoteNameIdList.get(i).split("@");
            temp.add(temptemp[1]);
            Log.i("emoteid", temptemp[1]);
        }
        loadEmoteImgs = new LoadEmoteImgs();
        loadEmoteImgs.execute(temp);
    }

    public class LoadEmoteImgs extends AsyncTask<ArrayList<String>, Void, Void> {
        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            for (int i = 0; i < params[0].size(); i++) {
                Log.i("qwerty", params[0].get(i));
                try {
                    URL imgUrl = new URL("https://static-cdn.jtvnw.net/emoticons/v1/" + params[0].get(i) + "/2.0");
                    Bitmap bmp = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
                    emoteImgArray.add(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setEmoteImgsLoaded(true);
            Log.i("loaded images", String.valueOf(emoteImgsLoaded));
        }
    }
}
