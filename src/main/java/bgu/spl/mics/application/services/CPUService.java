package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.DataBatch;

import java.util.LinkedList;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    public CPU myCPU;


    public CPUService(String name,CPU cpu) {
        super(name);
        // TODO Implement this
        myCPU=cpu;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        register();
        subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            @Override
            public void call(TickBroadcast c) {
                myCPU.ticks=myCPU.ticks+1;

                if(!myCPU.isProcessing)
                {
                    myCPU.pullUnprocessedBatch();
                }

                myCPU.processBatch();

            }
        });

        subscribeBroadcast(TerminateAllBroadcast.class, new Callback<TerminateAllBroadcast>() {
            @Override
            public void call(TerminateAllBroadcast c) {
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
