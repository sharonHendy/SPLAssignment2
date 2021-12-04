package bgu.spl.mics;

import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import  org.junit.Before;
import  org.junit.After;
import  org.junit.Test;
import static org.junit.Assert.*;
import java.util.concurrent.TimeUnit;


public class MessageBusTest {
    private static MessageBus mb;
    private static MicroService ms;

    @Before
    public void setUp(){
        mb = new MessageBusImpl();
        ms = new StudentService("name");
        mb.register(ms);
    }

    @Test
    public void subscribeEvent(){
        assertFalse(mb.isMicroServiceSubscribedEvent(ms,ExampleEvent.class));
        mb.subscribeEvent(ExampleEvent.class,ms);
        assertTrue(mb.isMicroServiceSubscribedEvent(ms,ExampleEvent.class));
    }

    @Test
    public void subscribeBroadcast(){
        assertFalse(mb.isMicroServiceSubscribedBroadcast(ms,ExampleBroadcast.class));
        mb.subscribeBroadcast(ExampleBroadcast.class,ms);
        assertTrue(mb.isMicroServiceSubscribedBroadcast(ms,ExampleBroadcast.class));
    }

    @Test
    public void Complete() {
        if(!mb.isMicroServiceSubscribedEvent(ms,ExampleEvent.class)){
            mb.subscribeEvent(ExampleEvent.class,ms);
        }
        Future<String> futureObject = mb.sendEvent(new ExampleEvent("test"));
        try {
            ExampleEvent message = (ExampleEvent) mb.awaitMessage(ms);
            assertFalse(futureObject.isDone());
            assertEquals(futureObject.get(3l, TimeUnit.SECONDS),null);

            mb.complete(message,"result");

            assertTrue(futureObject.isDone());
            assertEquals(futureObject.get(),"result");
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void sendBroadcast() {
        int before = mb.numOfBroadcasts();
        mb.sendBroadcast(new ExampleBroadcast(""));
        assertTrue(mb.numOfBroadcasts() == before +1);
    }


    @Test
    public void sendEvent() {
        int before = mb.numOfEvents();
        mb.sendEvent(new ExampleEvent(""));
        assertTrue(mb.numOfEvents() == before +1);
    }

    @Test
    public void register() {
        assertTrue(mb.isMicroServiceRegistered(ms));
    }

    @Test
    public void unregister() {
        mb.unregister(ms);
        assertFalse(mb.isMicroServiceRegistered(ms));
    }

    @Test
    public void awaitMessage() {
        mb.register(ms);
        mb.unregister(ms);
        boolean flag = false;
        try {
            mb.awaitMessage(ms);
        }
        catch (IllegalStateException e){
            flag = true;
        }
        catch (InterruptedException ignored) {}
        assertTrue(flag);
        mb.register(ms);
        mb.subscribeEvent(ExampleEvent.class,ms);
        mb.sendEvent(new ExampleEvent(""));
        try{
            Message m = mb.awaitMessage(ms);
            assertTrue(m instanceof ExampleEvent);
        }
        catch(InterruptedException ignored){}
    }

    @After
    public void tearDown() throws Exception {
    }
}