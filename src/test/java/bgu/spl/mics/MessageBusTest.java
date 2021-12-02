package bgu.spl.mics;
import static org.junit.Assert.*;

import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import  org.junit.Before;
import  org.junit.After;
import  org.junit.Test;

public class MessageBusTest {
    private MessageBus mb;
    private MicroService ms;

    @Before
    public void setUp(){
        mb = new MessageBusImpl();
        ms = new StudentService("sharon");

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
        Future<String> futureObject = (Future<String>)sendEvent(new ExampleEvent(""));
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
        if(mb.isMicroServiceRegistered(ms)){
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
    }
}
