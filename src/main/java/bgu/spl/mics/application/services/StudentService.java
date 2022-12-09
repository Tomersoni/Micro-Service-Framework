package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.Deque;
import java.util.concurrent.TimeUnit;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private Student myStudent;


    public StudentService(String name, Student student) {
        super(name);
        // TODO Implement this
        this.myStudent=student;
    }

    @Override
    protected void initialize() {
        // TODO Implement this

        register();
        subscribeBroadcast(PublishConferenceBroadcast.class, new Callback<PublishConferenceBroadcast>() {
            @Override
            public void call(PublishConferenceBroadcast c) {
                int goodResults=0;
                if(c.getStudentToAmount().get(myStudent)!=null) {
                    goodResults = c.getStudentToAmount().get(myStudent);
                }
                myStudent.setPublications(myStudent.getPublications()+goodResults);
                myStudent.setPapersRead(myStudent.getPapersRead()+c.getTotalResults()-goodResults);
                }

        });

        subscribeBroadcast(TrainModelSentBroadcast.class, new Callback<TrainModelSentBroadcast>() {
            @Override
            public void call(TrainModelSentBroadcast c) {
                if(c.getMyEvent().getModel().getStudent().equals(myStudent)){
                    Model result =c.getMyEvent().getFuture().get();
                    if(result==null)
                        terminate();
                    else {

                        myStudent.trainedmodels.addLast(result);
                        Future<Model> testFuture = sendTestModelEvent(result);
                        result = testFuture.get();
                        result.setStatus(Model.Status.Tested);
                        sendPublishResultsEvent(result);
                        result.isPublished = true;

                        if (!myStudent.getModels().isEmpty()) {
                            sendTrainModelEvent(myStudent.getModels().removeFirst());
                        }
                    }
                }
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

        if(!myStudent.getModels().isEmpty()){
            sendTrainModelEvent(myStudent.getModels().removeFirst());

        }



    }

    public void sendTrainModelEvent(Model m){
        TrainModelEvent trainModel= new TrainModelEvent(m,myStudent);
        trainModel.setFuture(sendEvent(trainModel));
        m.setEvent(trainModel);
        TrainModelSentBroadcast b=new TrainModelSentBroadcast(trainModel);
        sendBroadcast(b);
    }

    public Future<Model> sendTestModelEvent(Model m){
        TestModelEvent testModel= new TestModelEvent(m);
        testModel.setFuture(sendEvent(testModel));
        return testModel.getFuture();

    }
     public Future<Model.Result> sendPublishResultsEvent(Model m){
         PublishResultsEvent publishResult= new PublishResultsEvent(myStudent,m);
         publishResult.setFuture(sendEvent(publishResult));
         return publishResult.getFuture();

     }
}
