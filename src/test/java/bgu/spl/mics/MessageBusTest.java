package bgu.spl.mics;
import static org.junit.Assert.*;

import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import junit.framework.TestCase;
import  org.junit.Before;
import  org.junit.After;
import  org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MessageBusTest extends TestCase {
    private MessageBus mb;
    private MicroService ms;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mb = new MessageBusImpl();
        ms = new ExampleMicroService("name");
        mb.register(ms);
    }

    @Test
    public void subscribeEvent(){
        mb.subscribeEvent(ExampleEvent.class,ms);
        assertEquals(true, mb.isMicroServiceSubscribedEvent(ms,ExampleEvent.class));

    }

    @Test
    public void subscribeBroadcast(){
        mb.subscribeBroadcast(ExampleBroadcast.class,ms);
        assertEquals(true, mb.isMicroServiceSubscribedBroadcast(ms,ExampleBroadcast.class));
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
        assertEquals(mb.numOfEvents(), before + 1);
    }

    @Test
    public void register() {
        if(!mb.isMicroServiceRegistered(ms)){
            mb.register(ms);
            assertTrue(mb.isMicroServiceRegistered(ms));
        }
    }

    @Test
    public void unregister() {
        if(mb.isMicroServiceRegistered(ms)){
            mb.unregister(ms);
            assertFalse(mb.isMicroServiceRegistered(ms));
        }
    }

    @Test
    public void awaitMessage() {
        if(mb.isMicroServiceRegistered(ms)){ //checks that exception is throw if ms is not registered
            mb.unregister(ms);
            boolean flag = false;
            try {
                mb.awaitMessage(ms);
            } catch (InterruptedException ignored) {
            }catch (IllegalStateException e){
                flag = true;
            }
            assertTrue(flag);
        }

        mb.register(ms);
        mb.subscribeEvent(ExampleEvent.class,ms);
        mb.sendEvent(new ExampleEvent(""));
        try{
             Message m = mb.awaitMessage(ms);
             assertTrue(m instanceof ExampleEvent);
        }catch(InterruptedException ignored){}

    }

    @Test
    public void isMicroServiceRegisteredEvent() {
    }

    @Test
    public void isMicroServiceRegisteredBroadcast() {
    }


    @After
    public void tearDown() throws Exception {
        ms.terminate();
    }
}
