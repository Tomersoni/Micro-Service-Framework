package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    static Future<String> future;
    static String result;

    @Before
    public void setUp() throws Exception {
        future= new Future<>();
        result="result";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        assertFalse(future.isDone());
        future.resolve(result);
        assertEquals(future.get(),result);
        assertTrue(future.isDone());
    }

    @Test
    public void resolve() {

        future.resolve(result);
        assertTrue(future.isDone());
        assertTrue("Should be same results: ", future.get()==result);
    }

    @Test
    public void isDone() {
        assertFalse(future.isDone());
        future.resolve(result);
        assertTrue(future.isDone());
    }

    @Test
    public void testGet() {
        assertFalse(future.isDone());
        String ans= future.get(1000, TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        assertEquals(ans,null);
        future.resolve(result);
        assertEquals(future.get(1000,TimeUnit.MILLISECONDS),result);
        assertTrue(future.isDone());
    }
}