package com.sg.ocr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.sg.ocr.SevenSegmentScanner.ScanResult;

public class OcrDemoTest {

	

	@Test
	public void testScan() throws Exception {
		String lines = "    _  _     _  _  _  _  _ " + System.lineSeparator();
		      lines += "  | _| _||_||_ |_   ||_||_|" + System.lineSeparator();
		      lines += "  ||_  _|  | _||_|  ||_| _|" + System.lineSeparator();
		      lines += System.lineSeparator();
		      lines += " _     _  _     _  _  _  _ " + System.lineSeparator();
		      lines += "| |  | _| _||_||_ |_   ||_|" + System.lineSeparator();
		      lines += "|_|  ||_  _|  | _||_|  ||_|" + System.lineSeparator(); 
		
		ByteArrayInputStream in = new ByteArrayInputStream(lines.getBytes());
		OcrDemo ocrdemo = new OcrDemo();
		List<ScanResult> results = ocrdemo.scan(new InputStreamReader(in), Collections.emptySet());
		assertEquals(2,results.size());
		assertEquals("123456789", results.get(0).getAccountNumber());
		assertEquals("012345678", results.get(1).getAccountNumber());
		
	}

	@Test
	public void testVerifyLines_withValidLines() throws Exception {
		String line1 = "    _  _     _  _  _  _  _ ";
		String line2 = "  | _| _||_||_ |_   ||_||_|";
		String line3 = "  ||_  _|  | _||_|  ||_| _|";
			  
	    OcrDemo ocrdemo = new OcrDemo();	  
	    ocrdemo.verifyLines(1, Arrays.asList(line1,line2,line3));
			  
	}
	
	@Test
	public void testVerifyLines_withMissingCharacter() throws Exception {
		//first line missing a character
		String line1 = "    _  _    _  _  _  _  _ ";
		String line2 = "  | _| _||_||_ |_   ||_||_|";
		String line3 = "  ||_  _|  | _||_|  ||_| _|";
			  
	    OcrDemo ocrdemo = new OcrDemo();
	    try {
	    	ocrdemo.verifyLines(1, Arrays.asList(line1,line2,line3));
	    	fail("Expected exception to be thrown when lines contain errors");
	    } catch (RuntimeException e) {}
			  
	}
	
	@Test
	public void testVerifyLines_withInvalidLines2() throws Exception {
		//second line has invalid character
		String line1 = "    _  _     _  _  _  _  _ ";
		String line2 = "  | _| _||_||_ |.   ||_||_|";
		String line3 = "  ||_  _|  | _||_|  ||_| _|";
			  
	    OcrDemo ocrdemo = new OcrDemo();
	    try {
	    	ocrdemo.verifyLines(1, Arrays.asList(line1,line2,line3));
	    	fail("Expected exception to be thrown when lines contain errors");
	    } catch (RuntimeException e) {}
			  
	}
   
}
