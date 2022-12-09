package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.HashMap;
import java.util.List;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private ConfrenceInformation myConference;
    private HashMap<Student, Integer> studentToAmount;
    int time=1;
    public int totalResults=0;

    public ConferenceService(String name, ConfrenceInformation confrenceInformation) {
        super(name);
        // TODO Implement this
        studentToAmount=new HashMap<>();
        myConference=confrenceInformation;
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        register();
        subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            @Override
            public void call(TickBroadcast c) {
                time=c.getTimer();
                if(time>=myConference.getDate()){
                    PublishConferenceBroadcast publish= new PublishConferenceBroadcast(studentToAmount,totalResults);
                    sendBroadcast(publish);
                    terminateAndUnregister();
                }
            }
        });
        subscribeEvent(PublishResultsEvent.class, new Callback<PublishResultsEvent>() {
            @Override
            public void call(PublishResultsEvent c) {
                Model.Result result=c.getModel().getResult();

                Student student=c.getStudent();
                if(studentToAmount.get(student)==null){
                    studentToAmount.put(student,0);
                }
                if(result== Model.Result.Good){
                    int i=studentToAmount.get(student);
                    i++;
                    totalResults++;
                    studentToAmount.replace(student,i);
                }
                myConference.publishedModels.addLast(c.getModel());
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
