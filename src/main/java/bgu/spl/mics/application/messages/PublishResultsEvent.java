package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class PublishResultsEvent implements Event<Model.Result> {


    private Student student;
    private Model model;
    private Future<Model.Result> future;

    public PublishResultsEvent(Student student, Model model) {
        this.student=student;
        this.model=model;
    }


    @Override
    public void setFuture(Future<Model.Result> future) {
        this.future=future;
    }

    @Override
    public Future<Model.Result> getFuture() {
        return future;
    }


    public Student getStudent() {
        return student;
    }

    public Model getModel() {
        return model;
    }
}
