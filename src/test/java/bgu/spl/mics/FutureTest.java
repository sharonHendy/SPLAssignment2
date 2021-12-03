package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleEvent;
import junit.framework.TestCase;

import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


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
        future = mb.sendEvent(e);
        AtomicBoolean flag = new AtomicBoolean(false);
        Thread t = new Thread(() -> {future.resolve("result"); flag.set(true);});

        String result = future.get();
        assertTrue("result".equals(result) && flag.get());
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
        LocalTime beforeTime = LocalTime.now();
        assertNull(future.get(timeout, timeUnit)); //if future is not resolved should to return null
        LocalTime afterTime = LocalTime.now();
        assertTrue(beforeTime.until(afterTime, ChronoUnit.MILLIS) < 100); //checks that the get took <timeout> seconds
        future.resolve("result");
        assertEquals(future.get(timeout,timeUnit),"result");
        assertTrue(future.isDone());
    }
}