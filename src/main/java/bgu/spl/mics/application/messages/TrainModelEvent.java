package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TrainModelEvent implements Event<Model> {

    private Model m;
    private Future<Model> future;
    private Student student;

    public Student getStudent() {
        return student;
    }

    public TrainModelEvent(Model m, Student student) {
        this.m = m;
        this.student=student;
    }

    @Override
    public Future<Model> getFuture(){
        return future;
    }

    @Override
    public void setFuture(Future<Model> future) {
        this.future=future;
    }

    public Model getModel() {
        return m;
    }
}
