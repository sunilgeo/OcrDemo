package com.sg.ocr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sg.ocr.fsm.FSMBuilder;
import com.sg.ocr.fsm.SevenSegmentFSM;


public class SevenSegmentScanner {
	private static final int CHAR_PER_LINE = 3;
	private final int numberOfDigits;
	private final Pattern illegalCharPattern = Pattern.compile("[^ |_]");
	private final SevenSegmentFSM ssfsm;
	private final FSMBuilder dsmBuilder;
	
	SevenSegmentScanner(int nDigits) {
		numberOfDigits = nDigits;
		dsmBuilder =  new FSMBuilder();
		ssfsm = dsmBuilder.buildStateMachineForDigits();
	}
	
	/**
	 * Scan full number from the given list of lines.
	 */
	public ScanResult scanLines(List<String> lines) {
		StringBuilder sb = new StringBuilder();
		for(int i =0; i < numberOfDigits;i++) {
			sb.append(scanDigit(lines, i));
		}
    	return generateScanResult(lines, sb.toString());
    }
	
	
	/*
	 * Scan the nth digit from seven segment strings.
	 */
	private String scanDigit(List<String> lines, int nth) {
		ssfsm.reset();
		//move through each state for every character for this digit.
		getDigitSegments(lines, nth).forEach(s -> ssfsm.nextState(s));
		return ssfsm.getValue();
	}
	
	private List<Character> getDigitSegments(List<String> lines, int nth) {
		List<Character> segments = new ArrayList<>();
		int startPos = nth * CHAR_PER_LINE;
		lines.forEach(line -> {
			line.substring(startPos, startPos + CHAR_PER_LINE).chars()
			  .forEach(c -> segments.add((char) c));
		});
		return segments;
	}
	
	private ScanResult generateScanResult(List<String> lines, String accNum) {
		String errMsg = null;
		if(accNum.contains("?")) { 
			errMsg = "ILL";
		}
		return new ScanResult(lines, accNum, errMsg);
	}
	
	
	
	void verifyLine(String line) throws InvalidDataException {
		if(line.length() != numberOfDigits * CHAR_PER_LINE) 
			throw new InvalidDataException("Unexpected number of characters");
		if(illegalCharPattern.matcher(line).find()) 
			throw new InvalidDataException("Illegal characters");
	}
	
	/*
	 * Checksum to verify scanned numbers.
	 * checksum is calculated as
	 * ((1*d1) + (2*d2) + (3*d3) + ... + (9*d9)) % 11 == 0
	 */
	public boolean checksum(String number) {
		List<Integer> digits = number.chars()
				.mapToObj(c -> Character.getNumericValue(c)).collect(Collectors.toList());
		Collections.reverse(digits);
		int checksum = 0;
		for(int i =0; i < digits.size(); i++) {
			checksum += (digits.get(i) * (i + 1));
		}
		return checksum % 11 == 0;
	}
	
	public static class ScanResult {
		private final List<String> inputLines;
		private final String accountNumber;
		private final String errorMsg;
		
		private ScanResult(List<String> lines, String accNum, String errMsg) {
			this.inputLines = lines;
			this.accountNumber = accNum;
			this.errorMsg =errMsg;
		}

		public List<String> getInputLines() {
			return inputLines;
		}

		public String getAccountNumber() {
			return accountNumber;
		}

		public String getErrorMsg() {
			return errorMsg;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(accountNumber);
			if(errorMsg != null) {
				sb.append(" ").append(errorMsg);
			}
			return sb.toString();
		}
	}
}

