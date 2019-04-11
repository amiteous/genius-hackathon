import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Settings {
    private static Settings instance;
    private static Context ctx;
    private static final String SHARED_PREFS_NAME = "genius";
    public void init(Context ctx){
        Settings.ctx = ctx;
    }
    public Settings getInstance() throws Exception{
        if (ctx == null){
            throw new Exception("Settings not initilized with context, call consider calling init(ctx)");
        }
        if (instance == null){
            instance = new Settings();
        }
        return instance;
    }

    public void setSendLocation(boolean value){
        SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_PREFS_NAME, ctx.MODE_PRIVATE).edit();
        editor.putBoolean("send_loc", value);
    }
    public boolean getSendLoaction(){
        SharedPreferences shared = ctx.getSharedPreferences(SHARED_PREFS_NAME, ctx.MODE_PRIVATE);
        return shared.getBoolean("send_loc", true);
    }

    public void setContanctsToText(String[] phones){
        Set<String> phoneSet = new HashSet<String>(Arrays.asList(phones));
        SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_PREFS_NAME, ctx.MODE_PRIVATE).edit();
        editor.putStringSet("emergency_phones", phoneSet);
    }

    public String[] getContactsToText(){
        SharedPreferences shared = ctx.getSharedPreferences(SHARED_PREFS_NAME, ctx.MODE_PRIVATE);
        Set<String> phones = shared.getStringSet("emergency_phones", new HashSet<String>());
        String[] result = new String[phones.size()];
        result = phones.toArray(result);
        return result;
    }
    public void setAlaramTimeInSeconds(int alarmTime){
        SharedPreferences.Editor editor = ctx.getSharedPreferences(SHARED_PREFS_NAME, ctx.MODE_PRIVATE).edit();
        editor.putInt("alarm_time", alarmTime);
    }
    
    public int getAlarmTimeInSeconds(){
        SharedPreferences shared = ctx.getSharedPreferences(SHARED_PREFS_NAME, ctx.MODE_PRIVATE);
        return shared.getInt("alarm_time", 60);
    }
}
