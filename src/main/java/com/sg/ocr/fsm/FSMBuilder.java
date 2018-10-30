package com.sg.ocr.fsm;

import java.util.Arrays;
import java.util.List;

/*
 * A builder/factory class to build SevenSegment state machine, and other helper methods.
 */
public class FSMBuilder {
	
	private static List<Character> ZERO = Arrays.asList(' ','_',' ','|',' ','|','|','_','|');
	private static List<Character> ONE = Arrays.asList(' ',' ',' ',' ',' ','|',' ',' ','|');
	private static List<Character> TWO = Arrays.asList(' ','_',' ',' ','_','|','|','_',' ');
	private static List<Character> THREE = Arrays.asList(' ','_',' ',' ','_','|',' ','_','|');
	private static List<Character> FOUR = Arrays.asList(' ',' ',' ','|','_','|',' ',' ','|');
	private static List<Character> FIVE = Arrays.asList(' ','_',' ','|','_',' ',' ','_','|');
	private static List<Character> SIX = Arrays.asList(' ','_',' ','|','_',' ','|','_','|');
	private static List<Character> SEVEN = Arrays.asList(' ','_',' ',' ',' ','|',' ',' ','|');
	private static List<Character> EIGHT = Arrays.asList(' ','_',' ','|','_','|','|','_','|');
	private static List<Character> NINE = Arrays.asList(' ','_',' ','|','_','|',' ','_','|');
	
	
	private static List<List<Character>> DIGIT_LIST = Arrays.asList(ZERO,ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE); 
	
	
	/**
	 * Build a state machine for the given digit with the list of specified sevenSegmentCharacters.
	 */
	private void addDigit(SevenSegmentFSM sssm,  String digit, List<Character> ssDigitCharacters) {
		State lastState = sssm.getIntial();
		//add state transformation for each character
		for(int i = 0 ; i < 8; i++) {
			//handle transformation for any digit other than the specified which will
			//transform to an error.
			lastState.addStateTransform(SevenSegmentFSM.ERROR_DIGIT);
			lastState = lastState.addStateTransform(ssDigitCharacters.get(i));
		}
		//finally add the last state which represents the digit.
		State state = new State(digit, true);
		lastState.addStateTransform(ssDigitCharacters.get(8), state);
	}
	
	/*
	 * Creates a state machine for scanning seven segment digits.
	 */
	public SevenSegmentFSM buildStateMachineForDigits() {
		SevenSegmentFSM sssm = new SevenSegmentFSM();
		for(int i =0; i < 10; i++) {
			addDigit(sssm, Integer.toString(i), DIGIT_LIST.get(i));
		}
		return sssm;
	}
	
	public SevenSegmentFSM copyOf(SevenSegmentFSM sssm) {
		return sssm.copy();
	}
	
	static List<Character> getSegmentsForDigit(int digit) {
		return DIGIT_LIST.get(digit);
	}
	
	/*
	 * Returns the seven segment version of the given digit.
	 */
	public static String getFormattedSegments(int digit) {
		StringBuilder sb = new StringBuilder();
		List<Character> segments = getSegmentsForDigit(digit);
		for(int i = 0 ;i < 9; i++) {
			if(i % 3 == 0) {
				sb.append(System.lineSeparator());
			}
			sb.append(segments.get(i));
		}
		return sb.toString();
	}
	

}
