package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Student;

import java.util.HashMap;

public class PublishConferenceBroadcast implements Broadcast {


    HashMap<Student, Integer> studentToAmount;
    int totalResults;


    public PublishConferenceBroadcast(HashMap<Student, Integer> studentToAmount, int totalResults) {
        this.studentToAmount = studentToAmount;
        this.totalResults=totalResults;
    }

    public HashMap<Student, Integer> getStudentToAmount() {
        return studentToAmount;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
