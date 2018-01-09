package in.lifeeth.busbroadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.io.File;

public class SmsReceiver extends BroadcastReceiver
{


    public void onReceive( Context context, Intent intent )
    {
        // Get SMS map from Intent
        Bundle extras = intent.getExtras();

        if ( extras != null )
        {
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get( "pdus" );

            for ( int i = 0; i < smsExtra.length; ++i )
            {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);

                // Here you can add any your code to work with incoming SMS
                if (BuildConfig.DEBUG) {
                    Log.d("BBROAD:", "SMS Received");

                }
                processSms( context, sms );
            }


        }

        // WARNING!!!
        // If you uncomment next line then received SMS will not be put to incoming.
        // Be careful!
        //this.abortBroadcast();
    }

    private void processSms(Context context, SmsMessage sms )
    {
        String messages = "";
        String body = sms.getMessageBody().toString();
        String address = sms.getOriginatingAddress();

        if (body.split(":")[0] instanceof String) {  // TODO: We need to return a Response for invalid SMS?
            if ( body.split(":").length == 4) {
                Log.d("BBROAD:","No of Tokens - Check");
                if (body.split(":")[0].equals("NCFBB")) { // Validate the SMS and place in the DB
                    Log.d("BBROAD:","NCFBB Keyword - Check");

                    messages += "SMS from " + address + " :\n";
                    messages += body + "\n";

                    Store store = Store.findById(Store.class, (long) 1);

                    store.FilesToPlay = body.split(":")[1];

                    for (String File : store.FilesToPlay.split(",")){
                        try {
                           Integer.parseInt(File);
                           File file = new File(Environment.getExternalStorageDirectory(), "/BusBroadcast/"+File+".m4a" );
                           if (!file.exists()) {
                                Log.d("BBROAD:","Invalid File - Asked to play "+File+".m4a which does not exist in BusBroadcast directory in SDCARD");
                                return;
                           }
                           Log.d("BBROAD:","File to play: "+ File);

                        } catch (NumberFormatException e) {
                            Log.d("BBROAD:","Files Parse Error");
                            return;
                        }
                    }

                    try {
                        store.Interval = Integer.parseInt(body.split(":")[2]);
                        store.MinSinceLastPlay = store.Interval;
                        Log.d("BBROAD:","Interval: "+ store.Interval);
                    } catch (NumberFormatException e) {
                        Log.d("BBROAD:","Interval Parse Error");
                        return;
                    }
                    try {
                        store.Count = Integer.parseInt(body.split(":")[3]);
                        Log.d("BBROAD:","Count: "+ store.Count);
                    } catch (NumberFormatException e) {
                        Log.d("BBROAD:","Count Parse Error");
                        return;
                    }

                    store.CommandingMobileNumber = address;
                    store.CompletedPlays = 0;
                    Log.d("BBROAD:","CommandingMobileNumber: " + store.CommandingMobileNumber);
                    Answers.getInstance().logCustom(new CustomEvent("SMS Received"));
                    Log.d("BBROAD:","All keywords - Check! - Scheduling NOW!");
                    store.save();
                    // Display SMS message
                    Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
