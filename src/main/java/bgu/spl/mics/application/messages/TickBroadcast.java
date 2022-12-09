package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    public int timer;


    public TickBroadcast(int timer) {
        this.timer = timer;
    }

    public int getTimer() {
        return timer;
    }

}
