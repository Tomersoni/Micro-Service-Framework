package bgu.spl.mics.application.objects;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    int cores;
    Collection<DataBatch> data;
    Cluster cluster;
    public boolean isProcessing=false;
    public int ticks=0;

    public CPU(int cores, Cluster cluster) {
        this.cores = cores;
        this.cluster = cluster;
        cluster.CPUS.add(this);
        this.data= new LinkedList<DataBatch>();
    }

    public void pullUnprocessedBatch() {
        if (cores == 1 || cores == 2 || cores == 4) {
            if (!cluster.weakCPUsBatches.isEmpty()) {
                DataBatch batch = cluster.weakCPUsBatches.poll();
                if (batch != null) {
                    data.add(batch);
                    isProcessing = true;
                }
            } else {
                if (!cluster.strongCPUsBatches.isEmpty()) {
                    DataBatch batch = cluster.strongCPUsBatches.poll();
                    if (batch != null) {
                        data.add(batch);
                        isProcessing = true;
                    }
                }

            }

        } else {
            if (!cluster.strongCPUsBatches.isEmpty()) {
                DataBatch batch = cluster.strongCPUsBatches.poll();
                if (batch != null) {
                    data.add(batch);
                    isProcessing = true;
                }
            } else {
                if (!cluster.weakCPUsBatches.isEmpty()) {
                    DataBatch batch = cluster.weakCPUsBatches.poll();
                    if (batch != null) {
                        data.add(batch);
                        isProcessing = true;
                    }
                }

            }
        }
    }

    public void processBatch()
    {
        if(isProcessing)
        {
            DataBatch batch= (DataBatch) ((LinkedList)(data)).peekFirst();
            int ticksToProcess= requiredTicks(batch);

            if(ticks>=ticksToProcess)
            {
                data.remove(batch);
                isProcessing=false;
                ticks=ticks-ticksToProcess;
                cluster.stats.increaseProcessedBatches();
                cluster.stats.increaseCPUTime(ticksToProcess);
                cluster.returnProcessedBatch(batch);

            }

        }
    }

    public int requiredTicks(DataBatch batch){

        int requiredTicks;

        if (batch.getData().getType().equals(Data.Type.Images)) {
            requiredTicks = 32 / cores * 4;
        } else if (batch.getData().getType().equals(Data.Type.Text)) {
            requiredTicks = 32 / cores * 2;
        } else {
            requiredTicks = 32 / cores;
        }

        return requiredTicks;

    }
}
