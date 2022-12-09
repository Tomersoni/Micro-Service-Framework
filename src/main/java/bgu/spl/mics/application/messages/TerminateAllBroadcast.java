package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;

public class TerminateAllBroadcast implements Broadcast {

    Future<Model> future;
    public TerminateAllBroadcast() {
    }

    public void setFuture(Future<Model> future) {
        this.future = future;
    }
}
