package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TrainModelSentBroadcast implements Broadcast {

    TrainModelEvent myEvent;


    public TrainModelSentBroadcast(TrainModelEvent myEvent) {
        this.myEvent = myEvent;
    }

    public TrainModelEvent getMyEvent() {
        return myEvent;
    }
}
