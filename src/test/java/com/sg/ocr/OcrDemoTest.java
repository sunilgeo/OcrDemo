package com.sg.ocr;

import org.junit.Test;
import static org.junit.Assert.*;

public class OcrDemoTest {
    @Test public void testAppHasAGreeting() {
        OcrDemo classUnderTest = new OcrDemo();
        assertNotNull("app should have a greeting", classUnderTest.getGreeting());
    }
}
