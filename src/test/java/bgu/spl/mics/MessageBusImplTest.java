package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.*;

public class MessageBusImplTest {


    static MessageBusImpl mb;
    static MicroService m;
    String [] args= {"event"};


    private static interface randomType <T> extends Event<T>
    {

    }

    private static class Type<randomType>{};

    static Type<randomType> type;

    @Before
    public void setUp() throws Exception {
        mb= MessageBusImpl.getInstance();
        m= new ExampleMessageSenderService("Shir",args);
        mb.unregister(m);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribeEvent() {
        Event<String> event = new ExampleEvent("Shir");
        Class eventType= ExampleEvent.class;
        mb.subscribeEvent(eventType,m);
        assertFalse(mb.hasQueue(m)); ;
        mb.register(m);
        mb.subscribeEvent(eventType,m);
        mb.sendEvent(event);
//        assertTrue(mb.hasEvent(m,event));
    }

    @Test
    public void subscribeBroadcast() {
        Broadcast broad = new ExampleBroadcast("77");
        Class broadType= ExampleBroadcast.class;
        mb.subscribeBroadcast(broadType,m);
        assertFalse(mb.hasQueue(m));
        mb.register(m);
        mb.subscribeBroadcast(broadType,m);
        mb.sendBroadcast(broad);
        assertTrue(mb.hasBroadcast(m,broad));
    }

    @Test
    public void complete() {
        String result= "result";
        m.register();
        Event<String> event= new ExampleEvent("Shir");
        mb.subscribeEvent(ExampleEvent.class,m);
        Future<String> future= mb.sendEvent(event);
        event.setFuture(future);
        assertFalse(future.isDone());
        //mb.complete(event,result);
//        assertTrue("Should return true if Future object is resolved: ",future.isDone());
//        assertEquals(future.get(),result);
    }

    @Test
    public void sendBroadcast() {
        Broadcast broad = new ExampleBroadcast("77");
        Class broadType= ExampleBroadcast.class;
        m.register();
        mb.subscribeBroadcast(broadType,m);
        mb.sendBroadcast(broad);
        assertTrue(mb.hasBroadcast(m,broad));
    }

    @Test
    public void sendEvent() {
        Event<String> event = new ExampleEvent("Shir");
        Class eventType= ExampleEvent.class;
        mb.register(m);
        mb.subscribeEvent(eventType,m);
        m.sendEvent(event);
        //assertTrue(mb.getMicroToQueue().get(m).contains(event));
    }

    @Test
    public void register() {
        assertFalse(mb.hasQueue(m));
        mb.register(m);
        assertTrue(mb.hasQueue(m));
    }

    @Test
    public void unregister() {
        mb.register(m);
        mb.unregister(m);
        assertFalse(mb.hasQueue(m));
    }

    @Test
    public void awaitMessage() { //Finish Exception when interrupted
        try{
            mb.awaitMessage(m);
            fail();
        }
        catch (IllegalStateException | InterruptedException e){
            mb.register(m);
            Event<String> event = new ExampleEvent("Shir");
            mb.subscribeEvent(ExampleEvent.class,m);
            mb.sendEvent(event);
//            try {
//                //mb.awaitMessage(m);
//                assertFalse("Excepts queue to lose one element:",
//                        mb.hasEvent(m, event));
//            }
////            catch(InterruptedException d) {
//                fail();
//            }

        }

    }
}