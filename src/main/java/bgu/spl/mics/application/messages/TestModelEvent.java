package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;

public class TestModelEvent implements Event<Model> {

    private Model m;
    private Future<Model> future;


    public TestModelEvent(Model m)
    {
        this.m = m;
    }

    public Model getModel() {
        return m;
    }

    @Override
    public Future<Model> getFuture() {
        return future;
    }

    @Override
    public void setFuture(Future<Model> future) {
        this.future=future;
    }
}
