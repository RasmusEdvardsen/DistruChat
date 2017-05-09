package kappapride.distruchat;

/**
 * Created by buller on 09/05/2017.
 */
public class Config {

    //AmountEmotes : 164.

    private static Config instance = null;

    static final String socketUrl = "http://130.226.195.227:30022";

    static final String emoteNameIdUrl = "https://twitchemotes.com/api_cache/v2/global.json";

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }
}
