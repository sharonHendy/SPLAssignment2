package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleEvent;
import junit.framework.TestCase;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class FutureTest extends TestCase {
    private static MessageBus mb;
    private static MicroService ms;
    private static Future<String> future;
    private static ExampleEvent e;

    public void setUp(){
        //creates event, future for that event, messageBus and MicroService
        e = new ExampleEvent("test");
        future = mb.sendEvent(e);
        mb = MessageBusImpl.getInstance();
        ms = new ExampleMicroService("name");
        mb.register(ms);
    }

    public void tearDown() throws Exception {
    }

    public void testGet() {
        AtomicBoolean flag = new AtomicBoolean(false);
        Thread t = new Thread(() -> {future.resolve("result"); flag.set(true);});
        t.start();
        String result = future.get();

        assertTrue("result".equals(result) && flag.get()); //checks that the result was placed only when future has been resolved
    }

    public void testResolve() {
        future.resolve("result");
        assertEquals("result", future.get());
    }

    public void testIsDone() {
        future.resolve("result");
        assertTrue(future.isDone());
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
    }
}