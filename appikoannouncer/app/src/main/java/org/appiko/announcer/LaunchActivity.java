package org.appiko.announcer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.Answers;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.view.View;

import static org.appiko.announcer.Common.TAG;

public class LaunchActivity extends Activity {

    AlarmManager cron;
    BroadcastReceiver cronReceiver;
    PendingIntent cronPendingIntent;
    MediaPlayer cronMediaPlayer = new MediaPlayer();
    final static private long ONE_SECOND = 1000;
    final static private long SIXTY_SECONDS = ONE_SECOND * 60;
    public int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate called");
        }

        while(!(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)))
        {
            try { Thread.sleep(1000L); } // Wait for SD CARD
            catch(InterruptedException e) { e.printStackTrace(); }
        }

        Log.d(TAG,"SDCARD OK!");
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); // WiFi Manager
        wifiManager.setWifiEnabled(true); // Enable WiFi
        Store store = Store.findById(Store.class, (long)1 );
        if(store instanceof Store){
            Log.d(TAG, "Initialized DB");
            Log.d(TAG, "Last Received SMS "+store.FilesToPlay);
        }else{
            Log.d(TAG,"Empty DB - Initializing");
            store = new Store();
            store.FilesToPlay="";
            store.Interval=0;
            store.Count=0;
            store.MinSinceLastPlay=0;
            store.CommandingMobileNumber="";
            store.CompletedPlays=0;
            store.save();

            // Add Gargoyle network
            WifiConfiguration gargoyle = new WifiConfiguration();
            gargoyle.SSID = "\"Gargoyle\"";
            gargoyle.preSharedKey = "\"letmeinnow\"";
            gargoyle.status = WifiConfiguration.Status.ENABLED;
            gargoyle.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            gargoyle.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            gargoyle.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            gargoyle.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            gargoyle.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            gargoyle.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

            // Add and enable the connection
            int gargoylenetId = wifiManager.addNetwork(gargoyle);
            wifiManager.enableNetwork(gargoylenetId, true);

            // Add NCFBB network
            WifiConfiguration ncfbb = new WifiConfiguration();
            ncfbb.SSID = "\"NCFBB\"";
            ncfbb.preSharedKey = "\"letmeinnow\"";
            ncfbb.status = WifiConfiguration.Status.ENABLED;
            ncfbb.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            ncfbb.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            ncfbb.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            ncfbb.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            ncfbb.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            ncfbb.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            // Add and enable the connection
            int ncfbbnetId = wifiManager.addNetwork(ncfbb);
            wifiManager.enableNetwork(ncfbbnetId, true);

        }

        Fabric.with(this, new Crashlytics());


        ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_SILENT); // Set phone to silent

        int maxStreamVolume= ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).getStreamMaxVolume(AudioManager.STREAM_MUSIC); // Get max volume for media

        ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC,maxStreamVolume,AudioManager.FLAG_SHOW_UI); // Set Volume of output to max


        cronReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context c, Intent i) {

                boolean sleep = Boolean.TRUE; // This needs to be set to false to play.
                Log.d(TAG,"CRON!");

                ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setRingerMode(AudioManager.RINGER_MODE_SILENT); // Set phone to silent

                Store store = Store.findById(Store.class, (long)1 );
                if(store instanceof Store) {
                    if (store.Count > 0){
                        if (store.MinSinceLastPlay < store.Interval) {
                            store.MinSinceLastPlay++;
                        }else{
                            store.MinSinceLastPlay=1; // reset MinSinceLastPlay
                            store.Count--; // Reduce the count
                            store.CompletedPlays++; // Increase the count of plays
                            sleep=Boolean.FALSE;//Trigger the play of sound files.
                            Answers.getInstance().logCustom(new CustomEvent("Played"));
                        }
                        store.save();
                    }
                }

                List<String> FilesToPlay = new ArrayList<String>();
                for (String File : store.FilesToPlay.split(",")){ // These are pre-verified to be clean from SmsReceiver.java
                    try {
                        //pnarasim
                        // FilesToPlay.add(Environment.getExternalStorageDirectory().getPath().concat("/BusBroadcast/" + File + ".m4a"));
                        FilesToPlay.add(Environment.getExternalStorageDirectory().getPath().concat("/Download/GA1.mp3"));
                    } catch(Exception e){
                       Log.d(TAG,"Exception loading audio files");
                    }
                }

                final String[] toPlay= new String[ FilesToPlay.size() ];
                FilesToPlay.toArray(toPlay); // Finalize the array to play

                if (!sleep) {
                    if (toPlay.length > 0) { // Play only if we have a queued audio list.

                        Log.d(TAG, "Sound!");

                        counter = 1;

                        try {
                            cronMediaPlayer.setDataSource(toPlay[0]);
                        } catch (IllegalArgumentException e) {
                            Log.d(TAG, "You might not set the URI correctly!");
                        } catch (SecurityException e) {
                            Log.d(TAG, "You might not set the URI correctly!");
                        } catch (IllegalStateException e) {
                            Log.d(TAG, "You might not set the URI correctly!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            cronMediaPlayer.prepare();
                        } catch (IllegalStateException e) {
                            Log.d(TAG, "You might not set the URI correctly!");
                        } catch (IOException e) {
                            Log.d(TAG, "You might not set the URI correctly!");
                        }

                        cronMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                cronMediaPlayer.stop();
                                cronMediaPlayer.reset();
                                if (counter < toPlay.length) {
                                    try {
                                        cronMediaPlayer.setDataSource(toPlay[counter]);
                                    } catch (IllegalArgumentException e) {
                                        Log.d(TAG, "You might not set the URI1 correctly!");
                                    } catch (SecurityException e) {
                                        Log.d(TAG, "You might not set the URI1 correctly!");
                                    } catch (IllegalStateException e) {
                                        Log.d(TAG, "You might not set the URI1 correctly!");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        cronMediaPlayer.prepare();
                                    } catch (IllegalStateException e) {
                                        Log.d(TAG, "You might not set the URI1 correctly!");
                                    } catch (IOException e) {
                                        Log.d(TAG, "You might not set the URI1 correctly!");
                                    }
                                    cronMediaPlayer.start();
                                    counter++;
                                }
                            }
                        });
                        cronMediaPlayer.start(); //Kickstart the Playing.
                    }
                    if(store.CompletedPlays==1){ // Send SMS back on first play.
                        Time now = new Time();
                        now.setToNow();
                        String sTime = now.format("%H:%M:%S %d/%m/%Y");
                        SmsManager.getDefault().sendTextMessage(store.CommandingMobileNumber, null, "Executed 1st play at: "+ sTime , null, null);
                        Log.d(TAG, "Sending back confirmation!");
                    }
                }
            }
        };

        registerReceiver(cronReceiver, new IntentFilter("org.appiko.cronservice"));
        cronPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent("org.appiko.cronservice"), 0);

        cron = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));

        cron.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + SIXTY_SECONDS, SIXTY_SECONDS, cronPendingIntent);

    }

    /** Called when the user touches the setup device button */
    public void setupDevice(View view) {
        Log.d(TAG, "setupDevice called");
        //redirect to cloud form to auth, register phone num, village name,

    }

    /** Called when the user touches the download messages button */
    public void downloadMessages(View view) {
        Log.d(TAG, "Download Messages called");
        //redirect to cloud form to download new messages

    }

    @Override
    protected void onDestroy() {
        cron.cancel(cronPendingIntent);
        unregisterReceiver(cronReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.launch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
