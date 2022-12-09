package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {

    static public List<String> modelNames;
    static public AtomicInteger processedBatches;
    static public AtomicInteger CPUtime;
    static public AtomicInteger GPUtime;



    public Statistics() {
        this.modelNames = new LinkedList<>();
        this.processedBatches = new AtomicInteger(0);
        this.CPUtime = new AtomicInteger(0);
        this.GPUtime = new AtomicInteger(0);
    }



    public int getProcessedBatches() {
        return processedBatches.get();
    }

    public void increaseProcessedBatches()
    {
        processedBatches.getAndIncrement();
    }

    public void increaseCPUTime(int ticks)
    {
        CPUtime.getAndAdd(ticks);
    }

    public void increaseGPUTime(int ticks)
    {
        GPUtime.getAndAdd(ticks);
    }
}
