package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    public Type getType() {
        return type;
    }

//    public boolean isCurrentlyTrainingModel() {
//        return currentlyTrainingModel;
//    }

    public Model getModel() {
        return model;
    }

    public Cluster getCluster() {
        return cluster;
    }


    public ConcurrentLinkedQueue<DataBatch> getUnproccessedData() {
        return unproccessedData;
    }

    public ConcurrentLinkedQueue<DataBatch> getProccessedData() {
        return proccessedData;
    }
//
//    public int getVramCapacity() {
//        return VramCapacity;
//    }
//
//    public int getProcessTime() {
//        return processTime;
//    }

    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    public boolean currentlyTrainingModel;
    private Model model;
    private Cluster cluster;

    private ConcurrentLinkedQueue<DataBatch> unproccessedData;
    private ConcurrentLinkedQueue<DataBatch> proccessedData;
    private int VramCapacity;
    private int processTime;
    public int ticks=0;
    public int space;
    public boolean isTesting=false;
    public Model testingModel=null;

    public GPU(Type type, Cluster cluster) {
        this.type = type;
        this.model = null;
        this.cluster = cluster;
        cluster.GPUS.add(this);
        unproccessedData= new ConcurrentLinkedQueue<>();
        currentlyTrainingModel=false;

        if(type.equals(Type.RTX3090)) {
            VramCapacity = 32;
            processTime=1;
        }
        else if(type.equals(Type.RTX2080)) {
            VramCapacity = 16;
            processTime=2;
        }
        else {
            VramCapacity = 8;
            processTime=4;
        }

        space=VramCapacity;

        proccessedData=new ConcurrentLinkedQueue<>();
    }





    /**
     *
     * @param model
     * @pre: model.getStatus()==Model.Status.Trained
     * @post: model.getStatus()==Model.Status.Tested
     */
    public void testModel(Model model)
    {
        Student s = model.getStudent();
        Student.Degree degree= s.getStatus();

        Random rnd= new Random();
        double rndNumber= rnd.nextDouble();

        if(degree==Student.Degree.MSc)
        {

            if(rndNumber<=0.6)
            {

                model.result=Model.Result.Good;
            }
            else
                model.result=Model.Result.Bad;
        }
        else if(degree==Student.Degree.PhD)
        {
            if(rndNumber<=0.8)
            {
                model.result=Model.Result.Good;
            }
            else
                model.result=Model.Result.Bad;
        }
    }

    public void divideToBatches()
    {

        for(int i=0; i<model.getData().getSize(); i=i+1000)
        {
            DataBatch batch = new DataBatch(model.data,i);
            unproccessedData.add(batch);
        }
    }


    public void sendUnprocessedBatches()
    {
            for(int i=0; !(unproccessedData.isEmpty()) && i<space; i++) {
                    DataBatch batch = unproccessedData.remove();
                    cluster.insertUnprocessedBatchToLists(batch, this);
                }
    }

    public void pullProcessedBatch()
    {
        LinkedBlockingQueue<DataBatch> batches = (LinkedBlockingQueue<DataBatch>) getCluster().getGPUtoQueue().get(this);
        DataBatch batch = batches.poll();
        if(batch!=null) {
            proccessedData.add(batch);
        }
    }

    public void trainModelWithProcessedBatch()
    {
        while(!proccessedData.isEmpty() && ticks>=processTime)
        {
            DataBatch batch = proccessedData.poll();
            if(batch!=null) {
                updateSpace();
                model.getData().increaseProcessed(1000);
                ticks = ticks - processTime;
                cluster.stats.increaseGPUTime(processTime);
//                System.out.println(getModel().getName() + " " + getModel().getData().getProcessed() + " out of " + getModel().getData().getSize() + " Trained");

                if (model.getData().getProcessed() == model.getData().getSize())
                    break;
            }
        }
    }

    private void updateSpace()
    {
        this.space=VramCapacity-proccessedData.size();
    }



    }




