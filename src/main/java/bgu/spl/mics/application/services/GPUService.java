package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService<T> extends MicroService {

    GPU myGPU;
    private Deque<TrainModelEvent> remainingModels;
    int time=1;
    TrainModelEvent currentTrainModelEvent;
    TestModelEvent currentTestModelEvent;



    public GPUService(String name, GPU gpu) {
        super(name);
        myGPU=gpu;
        remainingModels= new LinkedList<>();
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        // TODO Implement this

        register();

        LinkedList<GPU> GPUs= (LinkedList<GPU>) myGPU.getCluster().getGPUS();

        for(GPU gpu: GPUs)
        {
            myGPU.getCluster().getGPUtoQueue().put(gpu,new LinkedBlockingQueue<>());
        }

        subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            @Override
            public void call(TickBroadcast c) {
                myGPU.ticks=myGPU.ticks+1;

                if(myGPU.isTesting){
                    Model m=myGPU.testingModel;
                    myGPU.testModel(m);
                    complete(currentTestModelEvent,m);
                    currentTestModelEvent=null;
                    myGPU.isTesting=false;
                    myGPU.testingModel=null;
                }

                if(myGPU.currentlyTrainingModel)
                {

                    myGPU.sendUnprocessedBatches();

                    myGPU.pullProcessedBatch();

                    myGPU.trainModelWithProcessedBatch();

                    if(myGPU.getModel().getData().getProcessed()==myGPU.getModel().getData().getSize())
                    {
                        myGPU.getModel().setStatus(Model.Status.Trained);
                        complete(currentTrainModelEvent, myGPU.getModel());
                        if(!remainingModels.isEmpty())
                        {
                            currentTrainModelEvent=remainingModels.removeFirst();
                            myGPU.setModel(currentTrainModelEvent.getModel());
                            myGPU.divideToBatches();
                            myGPU.getModel().setStatus(Model.Status.Training);
                        }
                        else
                        {
                            myGPU.currentlyTrainingModel=false;
                            myGPU.setModel(null);
                        }
                    }
                }
            }
        });

        subscribeEvent(TrainModelEvent.class, new Callback<TrainModelEvent>() {
            @Override
            public void call(TrainModelEvent c) {
                if(myGPU.currentlyTrainingModel) {
                    remainingModels.addLast(c);
                }
                else
                {
                    if(remainingModels.isEmpty())
                    {
                        currentTrainModelEvent=c;
                    }
                    else
                    {
                        remainingModels.addLast(c);
                        currentTrainModelEvent = remainingModels.removeFirst();
                    }

                    myGPU.setModel(currentTrainModelEvent.getModel());
                    myGPU.currentlyTrainingModel=true;
                    myGPU.divideToBatches();
                    myGPU.getModel().setStatus(Model.Status.Training);
                }
            }
        });

        subscribeEvent(TestModelEvent.class, new Callback<TestModelEvent>() {
            @Override
            public void call(TestModelEvent c) {
                myGPU.isTesting=true;
                myGPU.testingModel=c.getModel();
                currentTestModelEvent=c;
            }
        });

        subscribeBroadcast(TerminateAllBroadcast.class, new Callback<TerminateAllBroadcast>() {
            @Override
            public void call(TerminateAllBroadcast c) {

                if(myGPU.currentlyTrainingModel)
                {
                    myGPU.getModel().getEvent().getFuture().resolve(null);
                }

                while(!remainingModels.isEmpty())
                {
                    remainingModels.removeFirst().getFuture().resolve(null);
                }
                terminate();

            }
        });

        CRMSRunner.countDownLatch.countDown();
        try {
            CRMSRunner.countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
