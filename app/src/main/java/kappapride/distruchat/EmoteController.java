package kappapride.distruchat;

import java.util.ArrayList;

/**
 * Created by buller on 03/05/2017.
 */
public class EmoteController {

    public static EmoteController instance = null;

    public ArrayList<String> emoteList = new ArrayList<String>();

    public static EmoteController getInstance() {

        if (instance == null) {
            instance = new EmoteController();
            // Log
        }

        return instance;
    }
}
