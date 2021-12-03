package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleEvent;
import junit.framework.TestCase;

import java.util.concurrent.TimeUnit;


public class FutureTest extends TestCase {
    private MessageBus mb;
    private MicroService ms;
    private Future<String> future;
    private ExampleEvent e;

    public void setUp() throws Exception {
        super.setUp();
        //creates event, future for that event, messageBus and MicroService
        e = new ExampleEvent("test");
        future = mb.sendEvent(e);
        mb = new MessageBusImpl();
        ms = new ExampleMicroService("name");
        mb.register(ms);
    }

    public void tearDown() throws Exception {
    }

    public void testGet() {
        //mb.complete(e,"result");
        future.resolve("result");
        String result = future.get();
        assertEquals("result", result);
    }

    public void testResolve() {
        if(future.isDone()){
            future = mb.sendEvent(e); //creates new future it has already been resolved
        }
        future.resolve("result");
        assertEquals("result", future.get());
    }

    public void testIsDone() {
        if(future.isDone()){
            assertSame("result",future.get());
        }else{
            future.resolve("result");
            assertEquals("result", future.get());
        }
    }

    public void testTestGet() { //test get with timeout
        long timeout = 3L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        future = mb.sendEvent(e);
        assertNull(future.get(timeout, timeUnit)); //if future not resolve should to return null
        future.resolve("result");
        assertEquals(future.get(timeout,timeUnit),"result");
        assertTrue(future.isDone());
    }
}