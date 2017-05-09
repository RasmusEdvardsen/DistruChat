package kappapride.distruchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by buller on 03/05/2017.
 */
public class EmoteController {

    private static EmoteController instance = null;

    private static boolean emotesNamesIdsLoaded = false;

    private static boolean emoteImgsLoaded = false;

    public ArrayList<String> emoteNameIdList = new ArrayList<String>();

    // TODO: 09/05/2017 Create SQLite DB for this.
    public ArrayList<Bitmap> emoteImgArray = new ArrayList<Bitmap>();

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


    // TODO: 09/05/2017 FIX NO MULTIPLE OCCURENCES!.
    // TODO: 09/05/2017 FIX INTERACTION WITH '+'!.
    // TODO: 09/05/2017 PUT LOADING IMAGES IN WELCOMEPAGE!.
    public SpannableString createSpannableString(Context ctx, String string) {
        String[] words = string.split(" ");
        ArrayList<String> toMatch = new ArrayList<>();
        for(int i = 0; i < emoteNameIdList.size(); i++){
            String[] temp = emoteNameIdList.get(i).split("@");
            toMatch.add(temp[0]);
        }
        Log.i("toMatch", toMatch.toString());
        
        SpannableString ss = new SpannableString(string);
        Log.i("test", emoteNameIdList.toString());
        for (int i = 0; i < words.length; i++) {
            Log.i("words", words[i].toString());
            for (int j = 0; j < toMatch.size(); j++) {
                if (toMatch.get(j).matches(words[i])) {

                    ImageSpan is = new ImageSpan(ctx, emoteImgArray.get(j));
                    ss.setSpan(is, string.indexOf(words[i]), string.indexOf(words[i])+words[i].length(), 0);
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
