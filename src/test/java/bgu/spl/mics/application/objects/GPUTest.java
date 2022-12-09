package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.StudentService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {

    GPU gpu;
    Cluster cluster;
    Model model;
    GPU.Type type;
    Data data;
    Student student;
    MessageBusImpl mb;
    StudentService studentService;

    @Before
    public void setUp() throws Exception {
        mb=MessageBusImpl.getInstance();
        cluster=Cluster.getInstance();
        data = new Data(Data.Type.Images,5000);
        student=new Student("me","computer science",Student.Degree.MSc);
        studentService=new StudentService("myService",student);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void trainModel() {
        model = new Model("Shir",data, student);
        gpu = new GPU(GPU.Type.RTX3090,cluster);
        DataBatch batch=new DataBatch(data,0);
        batch.status= DataBatch.Status.Processed;
        gpu.getProccessedData().clear();
        gpu.getProccessedData().add(batch);
        int size=gpu.getProccessedData().size();
        gpu.trainModelWithProcessedBatch();
        assertEquals(gpu.getProccessedData().size()-1,size-1);
    }

    @Test
    public void testModel() {
        model = new Model("Shir",data, student);
        gpu = new GPU(GPU.Type.RTX3090,cluster);
        gpu.testModel(model);
        assertTrue(model.getResult()!=Model.Result.None);
    }
}