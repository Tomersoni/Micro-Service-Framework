package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;

import java.util.LinkedList;
import java.util.Objects;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {

    public void setPublications(int publications) {
        this.publications = publications;
    }

    public void setPapersRead(int papersRead) {
        this.papersRead = papersRead;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public void setModels(LinkedList<Model> models) {
        this.models = models;
    }

    public Degree getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return publications == student.publications &&
                papersRead == student.papersRead &&
                name.equals(student.name) &&
                department.equals(student.department) &&
                status == student.status &&
                models.equals(student.models);
    }

    public LinkedList<Model> getModels() {
        return models;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "        {\n" +
                "            \"name\":" + "\""+name + "\"\n" +
                "            \"department\": \"" + department + "\"\n" +
                "            \"status\":\"" + status +"\"\n" +
                "            \"publications\":" + publications +"\n" +
                "            \"papersRead\":" + papersRead +"\n" +
                "            \"trainedModels\": [\n" + trainedmodels +"\n" +"            ]\n"+
                "        },\n";
    }


    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications;
    private int papersRead;
    private LinkedList<Model> models;
    public LinkedList<Model> trainedmodels;



    public Student(String name, String department, Degree status) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.publications = 0;
        this.papersRead = 0;
        this.models=new LinkedList<>();
        this.trainedmodels= new LinkedList<>();
    }



}
