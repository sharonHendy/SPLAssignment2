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
            assertNull(futureObject.get(3l, TimeUnit.SECONDS));

            mb.complete(message,"result");

            assertTrue(futureObject.isDone());
            assertEquals(futureObject.get(),"result");
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    public void sendBroadcast() {
        int before = mb.numOfBroadcasts();
        ExampleBroadcast b= new ExampleBroadcast("");
        mb.subscribeBroadcast(b.getClass(),ms);
        mb.sendBroadcast(b);
        assertEquals(mb.numOfBroadcasts(), before + 1);
        try{
            assertEquals(mb.awaitMessage(ms),b);
        }
        catch (InterruptedException ignored){}
    }


    @Test
    public void sendEvent(){
        int before = mb.numOfEvents();
        ExampleEvent e= new ExampleEvent("");
        mb.subscribeEvent(e.getClass(),ms);
        mb.sendEvent(e);
        assertEquals(mb.numOfEvents(), before + 1);
        try{
            assertEquals(mb.awaitMessage(ms),e);
        }
        catch (InterruptedException ignored){}

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
        //mb.register(MessageBusTest.ms);
        mb.unregister(MessageBusTest.ms);
        boolean flag = false;
        try {
            mb.awaitMessage(MessageBusTest.ms);
        }
        catch (IllegalStateException e){
            flag = true;
        }
        catch (InterruptedException ignored) {}
        assertTrue(flag);
        mb.register(MessageBusTest.ms);
        mb.subscribeEvent(ExampleEvent.class, MessageBusTest.ms);
        mb.sendEvent(new ExampleEvent(""));
        try{
            Message m = mb.awaitMessage(MessageBusTest.ms);
            assertTrue(m instanceof ExampleEvent);
        }
        catch(InterruptedException ignored){}
    }

    @After
    public void tearDown() throws Exception {
    }
}