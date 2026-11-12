package se.grillkoket.tv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;

/** Every few minutes during opening hours, brings the offers on top of whatever is playing. */
public class AdScheduler extends BroadcastReceiver {
    static final int EVERY_MINUTES = 15;   // how often the ad pops up
    static final int SHOW_SECONDS = 60;    // how long it stays before going back
    static final int FROM_HOUR = 10, UNTIL_HOUR = 22;

    static void schedule(Context c) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        int flags = Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pi = PendingIntent.getBroadcast(c, 1, new Intent(c, AdScheduler.class), flags);
        long every = EVERY_MINUTES * 60_000L;
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + every, every, pi);
    }

    @Override
    public void onReceive(Context c, Intent intent) {
        if (MainActivity.visible) return;
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (h < FROM_HOUR || h >= UNTIL_HOUR) return;
        Intent i = new Intent(c, MainActivity.class);
        i.putExtra("popup", true);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        try { c.startActivity(i); } catch (Exception ignored) { }
    }
}
