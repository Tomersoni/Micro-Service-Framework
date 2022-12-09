package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.TrainModelEvent;

import java.util.Objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    String name;
    Data data;
    Student student;
    TrainModelEvent event;

    public TrainModelEvent getEvent() {
        return event;
    }

    public void setEvent(TrainModelEvent event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public Status getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }


    public Model(String name, Data data, Student student) {
        this.name = name;
        this.data = data;
        this.status = Status.PreTrained;
        this.result = Result.None;
        this.student=student;
        this.isPublished=false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return name.equals(model.name) &&
                data.equals(model.data) &&
                student.equals(model.student) &&
                status == model.status &&
                result == model.result;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String statusS="PreTrained";
        if(status==Status.Trained)
            statusS="Trained";
        else if(status==Status.Tested)
            statusS="Tested";
        else if(status==Status.Training)
            statusS="Training";
        String resultS= "None";
        if(result==Result.Bad)
            resultS="Bad";
        else if(result==Result.Good)
            resultS="Good";
        return "                {\n" +
                "                    \"name\": " + "\""+name + "\"\n" +
                "                    \"data\":" + data.toString() +"\n" +
                "                    \"status\":\"" + statusS +"\"\n" +
                "                    \"results\":\"" + resultS +"\"\n" +
                "                }"+"\n" ;
    }


    public enum Status{PreTrained,Training,Trained,Tested};
    public enum Result{None,Good,Bad};
    Status status;
    Result result;
    public boolean isPublished;

}
