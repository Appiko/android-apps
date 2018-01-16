package org.appiko.comms.alerter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import static org.appiko.comms.alerter.Common.TAG;

/**
 * Created by priya on 13/1/18.
 */

public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        Log.d(TAG,"Item selected was " + parent.getItemAtPosition(pos) );
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        Log.d(TAG,"Nothing was selected ");
    }
}
