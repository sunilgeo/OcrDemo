package com.sg.ocr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sg.ocr.fsm.FSMBuilder;
import com.sg.ocr.fsm.SevenSegmentFSM;

/*
 * Scanner class for parsing seven segment digits from lines.
 */
public class SevenSegmentScanner {
	private static final int CHAR_PER_LINE = 3;
	private final Pattern ILLEGAL_CHAR_PATTERN = Pattern.compile("[^ |_]");
	private final String ILLEGAL_NUM = "ILL";
	private final String ERROR_NUM = "ERR";
	private final String AMBIGOUS_NUM = "AMB";
	
	private final SevenSegmentFSM ssfsm;
	private final FSMBuilder dsmBuilder;
	private final int numberOfDigits;
	
	SevenSegmentScanner(int nDigits) {
		numberOfDigits = nDigits;
		dsmBuilder =  new FSMBuilder();
		ssfsm = dsmBuilder.buildStateMachineForDigits();
	}
	
	/**
	 * Scan full number from the given list of lines.
	 */
	public ScanResult scanLines(List<String> lines) {
		return scanLines(lines,false);
    }
	
	/**
	 * Scan full number from the given list of lines.
	 */
	public ScanResult scanLines(List<String> lines, boolean verify) {
		return scanLines(lines,verify,false);
    }
	
	
	/**
	 * Scan full number from the given list of lines.
	 */
	public ScanResult scanLines(List<String> lines, boolean verify, boolean fix) {
		StringBuilder sb = new StringBuilder();
		for(int i =0; i < numberOfDigits;i++) {
			sb.append(scanDigit(lines, i));
		}
		ScanResult scanResult = generateScanResult(lines, sb.toString(), verify);
		if(fix && scanResult.getErrorMsg() != null) {
			if(scanResult.getErrorMsg().startsWith(ILLEGAL_NUM))
				scanResult = tryFixingForIllegalDigits(scanResult);
			else if(scanResult.getErrorMsg().startsWith(ERROR_NUM))
				scanResult = tryFixingForErrorDigits(scanResult);
		}
		return scanResult;
    }
	
	/*
	 * Create alternate result by trying to fix error number. 
	 */
	ScanResult tryFixingForErrorDigits(ScanResult scanResult) {
		String accNum = scanResult.getAccountNumber();
		List<String> alts = new ArrayList<>();
		for(int i =0; i < numberOfDigits;i++) {
			final int digitPos = i;
			alts.addAll(getAlternateDigits(scanResult.getInputLines(), digitPos).stream()
				.map(alt -> replaceWithAltDigit(accNum,alt,digitPos)).filter(this::checksum).collect(Collectors.toList()));
		}
		return checkAndCreateAltResult(scanResult,alts);
	}
	
	
	private String replaceWithAltDigit(String num, String alt, int pos) {
		StringBuilder sb = new StringBuilder();
		sb.append(num.substring(0,pos));
		sb.append(alt);
		sb.append(num.substring(pos + 1));
		return sb.toString();
	}

	/*
	 * Create alternate result by trying to fix illegal digits in account number.
	 * Nothing is done if more than one illegal digit is found in the number. 
	 */
	ScanResult tryFixingForIllegalDigits(ScanResult scanResult) {
		String accNum = scanResult.getAccountNumber();
		List<Integer> errorDigits = getErrorDigist(accNum);
		if(errorDigits.size() > 1) {
			//only one error allowed.
			return scanResult;
		}
		if(errorDigits.size() == 1) {
			int altPos = errorDigits.get(0);
			List<String> altDigits = getAlternateDigits(scanResult.getInputLines(), altPos);
			//create alternate numbers substituting illegal digit.
			List<String> altNumbers = altDigits.stream().map(alt -> 
				replaceWithAltDigit(accNum,alt,altPos)).filter(this::checksum).collect(Collectors.toList());
			return checkAndCreateAltResult(scanResult,altNumbers);
		}
		return scanResult;
	}
	
	private ScanResult checkAndCreateAltResult(ScanResult result, List<String> altNumbers) {
		if(!altNumbers.isEmpty()) {
			//if more than 1 alternate then don't fix but report it as ambiguous.
			if(altNumbers.size() > 1) {
				return new ScanResult(result.getInputLines(), result.getAccountNumber(), 
						formatAmbigiousMessage(result.getAccountNumber(), altNumbers));
			} 
			return new ScanResult(result.getInputLines(), altNumbers.get(0), null);
		}
		return result;
	}
	
	private String formatAmbigiousMessage(String accNum, List<String> altNumbers) {
		StringBuilder sb = new StringBuilder(accNum);
		sb.append(" ").append(AMBIGOUS_NUM).append(" [");
		altNumbers.forEach(n -> sb.append("'").append(n).append("', "));
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]");
		return sb.toString();
	}
	
	private List<Integer> getErrorDigist(String accNum) {
		List<Integer> errorDigits = new ArrayList<>();
		for(int i =0; i <numberOfDigits;i++) {
			if(accNum.charAt(i) == (char)SevenSegmentFSM.ERROR_DIGIT) {
				errorDigits.add(i);
			}
		}
		return errorDigits;
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
	
	private ScanResult generateScanResult(List<String> lines, String accNum, boolean verify) {
		String errMsg = null;
		if(accNum.contains("?")) { 
			errMsg = ILLEGAL_NUM;
		} else if(verify && !checksum(accNum)) {
			errMsg = ERROR_NUM;
		}
		return new ScanResult(lines, accNum, errMsg);
	}
	
	
	
	void verifyLine(String line) throws InvalidDataException {
		if(line.length() != numberOfDigits * CHAR_PER_LINE) 
			throw new InvalidDataException("Unexpected number of characters");
		if(ILLEGAL_CHAR_PATTERN.matcher(line).find()) 
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
	
	List<String> getAlternateDigits(List<String> lines, int nth) {
		return getAlternateDigits(getDigitSegments(lines, nth));
	}
	
	List<String> getAlternateDigits(List<Character> segments) {
		List<SevenSegmentFSM> alternateFSMs = new ArrayList<>();
		ssfsm.reset();
		segments.forEach(c -> {
			SevenSegmentFSM alt = dsmBuilder.copyOf(ssfsm);
			if(c == ' ' ? alt.nextNonEmptyState() : alt.nextEmptyState()) {
				alternateFSMs.add(alt);
			}
			ssfsm.nextState(c);
		});
		List<String> possibleDigits = new ArrayList<>();
		if(!ssfsm.isError()) 
			possibleDigits.add(ssfsm.getValue());
		//transition all alternates and collect all possible results
		alternateFSMs.forEach(sm -> {
			for(int i = sm.transitionCount(); i < segments.size();i++)
				sm.nextState(segments.get(i));
			if(!sm.isError())
				possibleDigits.add(sm.getValue());
		});
		return possibleDigits;
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

