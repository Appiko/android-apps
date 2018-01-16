package org.appiko.announcer;

import com.orm.SugarRecord;

/**
 * Created by praneeth on 24/08/14.
 */

public class Store extends SugarRecord {
    String FilesToPlay;
    int Interval;
    int Count;
    int MinSinceLastPlay;
    String CommandingMobileNumber;
    int CompletedPlays;

    public Store(){
    }

    public Store(String FilesToPlay, int Interval, int Count, int MinSinceLastPlay, String CommandingMobileNumber, int CompletedPlays){
        this.FilesToPlay = FilesToPlay;
        this.Interval = Interval;
        this.Count = Count;
        this.MinSinceLastPlay = MinSinceLastPlay;
        this.CommandingMobileNumber = CommandingMobileNumber;
        this.CompletedPlays = CompletedPlays;
    }
}