package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.SynchronousQueue;

import static org.junit.Assert.*;

public class CPUTest {
    static CPU cpu;
    Cluster cluster;
    Collection<DataBatch> db;
    Data data;

    @Before
    public void setUp() throws Exception {
        db=new SynchronousQueue<DataBatch>();
        cluster=Cluster.getInstance();
        cpu= new CPU(4,cluster);
        data= new Data(Data.Type.Images,5000);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void processBatch() {
        DataBatch b = new DataBatch(data,0);
        cpu.data.add(b);
        boolean needToProcess=false;
        if(cpu.ticks>= cpu.requiredTicks(b)){
            needToProcess=true;
        }
        assertEquals(b.getStatus(), DataBatch.Status.Unprocessed);
        cpu.processBatch();
        if(needToProcess)
            assertEquals(b.getStatus(), DataBatch.Status.Processed);
        else
            assertEquals(b.getStatus(), DataBatch.Status.Unprocessed);

    }
}