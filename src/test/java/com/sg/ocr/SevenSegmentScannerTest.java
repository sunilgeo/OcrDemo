package com.sg.ocr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

public class SevenSegmentScannerTest {

	
	@Test
	public void testScanLines_singleDigitNumber() throws Exception {
		String line1 = " _ ";
		String line2 = "|_ ";
		String line3 = " _|";
		
		SevenSegmentScanner scanner = new SevenSegmentScanner(1);
		SevenSegmentScanner.ScanResult scanResult = scanner.scanLines(Arrays.asList(line1,line2,line3)); 
		assertEquals("5",scanResult.getAccountNumber());
	}
	
	
	@Test
	public void testScanLines() throws Exception {
		String line1 = " _     _  _     _  _  _  _  _ ";
		String line2 = "| |  | _| _||_||_ |_   ||_||_|";
		String line3 = "|_|  ||_  _|  | _||_|  ||_| _|";
		
		SevenSegmentScanner scanner = new SevenSegmentScanner(10);
		SevenSegmentScanner.ScanResult scanResult = scanner.scanLines(Arrays.asList(line1,line2,line3)); 
		assertEquals("0123456789",scanResult.getAccountNumber());
	}
	
	@Test
	public void testScanLines_withValidNumber() throws Exception {
		String line1 = " _     _  _     _  _  _  _  _ ";
		String line2 = "| |  | _| _||_||_ |_   ||_||_|";
		String line3 = "|_|  ||_  _|  | _||_|  ||_| _|";
		
		SevenSegmentScanner scanner = new SevenSegmentScanner(10);
		SevenSegmentScanner.ScanResult scanResult = scanner.scanLines(Arrays.asList(line1,line2,line3), true); 
		assertNull(scanResult.getErrorMsg());
	}
	
	@Test
	public void testScanLines_withInValidNumber() throws Exception {
		String line1 = " _  _     _  _        _  _  _ ";
		String line2 = "|_ |_ |_| _|  |  ||_| _||_||_ ";
		String line3 = "|_||_|  | _|  |  |  | _| _| _|";
		
		SevenSegmentScanner scanner = new SevenSegmentScanner(10);
		SevenSegmentScanner.ScanResult scanResult = scanner.scanLines(Arrays.asList(line1,line2,line3), true); 
		assertEquals("ERR", scanResult.getErrorMsg());
	}
	
	@Test
	public void testScanLines_withIllegalCharacters() throws Exception {
		String line1 = " _  _     _  _        _  _  _ ";
		String line2 = "|_ |_ |_| _|  |  ||_| _||_||_ ";
		String line3 = "|_||_|  | _|  |  | _| _  _| _|";
		
		SevenSegmentScanner scanner = new SevenSegmentScanner(10);
		SevenSegmentScanner.ScanResult scanResult = scanner.scanLines(Arrays.asList(line1,line2,line3), true); 
		assertEquals("ILL", scanResult.getErrorMsg());
		assertEquals("664371??95", scanResult.getAccountNumber());
	}


	@Test
	public void testVerifyLine_withValidLines() throws Exception {
		String line1 = " _    ";
		String line2 = "|_||_|";
		String line3 = " _|  |";
		SevenSegmentScanner scanner = new SevenSegmentScanner(2);
		
		scanner.verifyLine(line1);
		scanner.verifyLine(line2);
		scanner.verifyLine(line3);
	}
	
	@Test
	public void testVerifyLine_withInValidLines() throws Exception {
		String line1 = " _     ";
		String line2 = "|_||_.";
		String line3 = " _|  ";
		SevenSegmentScanner scanner = new SevenSegmentScanner(2);
		try {
			scanner.verifyLine(line1);
			fail("Expected InvalidDataException when line contains more characters than allowed");
		}catch (InvalidDataException e) {}
		
		try {
			scanner.verifyLine(line2);
			fail("Expected InvalidDataException when line contains illegal characters");
		}catch (InvalidDataException e) {}
		
		try {
			scanner.verifyLine(line3);
			scanner.verifyLine("Expected InvalidDataException when line contains less characters than required");
		}catch (InvalidDataException e) {}
	}
	
	@Test
	public void testChecksum_withValidNumber() throws Exception {
		SevenSegmentScanner scanner = new SevenSegmentScanner(9);
		assertTrue(scanner.checksum("711111111"));
		assertTrue(scanner.checksum("123456789"));
		assertTrue(scanner.checksum("490867715"));
		
	}

	@Test
	public void testChecksum_withInValidNumber() throws Exception {
		SevenSegmentScanner scanner = new SevenSegmentScanner(9);
		assertFalse(scanner.checksum("888888888"));
		assertFalse(scanner.checksum("490067715"));
		assertFalse(scanner.checksum("012345678"));
	}
	
	
}

