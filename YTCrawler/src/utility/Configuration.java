package utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashSet;

/**
 * Created by larcuser on 2/10/14.
 */
public class Configuration {
    private static Configuration ourInstance = new Configuration();

    public static Configuration getInstance() {
        return ourInstance;
    }

    private int delayTimeInSeconds;
    private GsonBuilder gsonBuilder = null;
    private Gson gson = null;
    private HashSet<String> dict = null;

    private Configuration() {
        delayTimeInSeconds = 5;
        gsonBuilder = new GsonBuilder();
        gsonBuilder.disableHtmlEscaping();
        gson = gsonBuilder.create();
        dict = new HashSet<String>();
    }

    public int getDelayTimeInSeconds() {
        return delayTimeInSeconds;
    }

    public GsonBuilder getGsonBuilder()
    {
        return gsonBuilder;
    }

    public Gson getGson()
    {
        return gson;
    }

    public HashSet<String> getDict() { return dict; }

    public void setDelayTimeInSeconds(int delayTimeInSeconds) {
        this.delayTimeInSeconds = delayTimeInSeconds;
    }
}
