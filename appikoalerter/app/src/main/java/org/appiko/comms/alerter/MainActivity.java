package org.appiko.comms.alerter;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.util.Log;

import com.example.priya.priya1.R;

import static org.appiko.comms.alerter.Common.TAG;

public class MainActivity extends AppCompatActivity {

    private Spinner alertsSpinner, villagesSpinner, freqSpinner, repeatsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create alerts spinner
        alertsSpinner = (Spinner) findViewById(R.id.alerts_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.alerts_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        alertsSpinner.setAdapter(adapter);
        alertsSpinner.setOnItemSelectedListener(new SpinnerActivity());

        //create  villages spinner
        villagesSpinner = (Spinner) findViewById(R.id.villages_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> villagesadapter = ArrayAdapter.createFromResource(this,
                R.array.villages_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        villagesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        villagesSpinner.setAdapter(villagesadapter);
        villagesSpinner.setOnItemSelectedListener(new SpinnerActivity());

        //create freq spinner
        freqSpinner = (Spinner) findViewById(R.id.freq_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> freqadapter = ArrayAdapter.createFromResource(this,
                R.array.freq_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        freqadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        freqSpinner.setAdapter(freqadapter);
        freqSpinner.setOnItemSelectedListener(new SpinnerActivity());

        //create repeats spinner
        repeatsSpinner = (Spinner) findViewById(R.id.repeat_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> repsadapter = ArrayAdapter.createFromResource(this,
                R.array.repeat_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        repsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        repeatsSpinner.setAdapter(repsadapter);
        repeatsSpinner.setOnItemSelectedListener(new SpinnerActivity());
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);

        final Button button = findViewById(R.id.send_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.d(TAG, "Send Alert clicked : sending sms");

                String messageToSend = Common.validationString+":1,2,13:1:2";
                String number = "9742566746";

                SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null,null);

            }
        });
    }


}

