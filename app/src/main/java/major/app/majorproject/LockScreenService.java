package major.app.majorproject;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by Harnirvair Singh on 3/18/2017.
 */

public class LockScreenService extends Service{
 BroadcastReceiver receiver;
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(){
        KeyguardManager.KeyguardLock key;
        KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);

        //This is deprecated, but it is a simple way to disable the lockscreen in code
        key = km.newKeyguardLock("IN");

        key.disableKeyguard();

        //Start listening for the Screen On, Screen Off, and Boot completed actions
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);

        //Set up a receiver to listen for the Intents in this Service
        receiver = new LockScreenReceiver();
        registerReceiver(receiver, filter);

        super.onCreate();
    }



    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();

    }
}

