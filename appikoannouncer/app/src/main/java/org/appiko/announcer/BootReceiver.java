package org.appiko.announcer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static org.appiko.announcer.Common.TAG;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
      //  if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.d(TAG, "BOOTED");
            Intent i = new Intent(context, LaunchActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        //}
    }
}
