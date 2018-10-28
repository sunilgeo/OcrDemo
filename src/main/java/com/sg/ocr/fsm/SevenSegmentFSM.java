package com.sg.ocr.fsm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A finite state machine for handling characters made up of seven segments using spaces, pipes and underscores. Although, this 
 * is a seven segment character, we use 9 character states - includes two additional spaces on the first line - to represent a digit.
 *   
 * @author sg
 *
 */
public class SevenSegmentFSM {
	public final static Character ERROR_DIGIT = '?';
	public final static State STATE_ERROR = new State("" + ERROR_DIGIT, true);
	public final static Set<Character> SUPPORTED_CHARACTERS = new HashSet<>(Arrays.asList(' ', '|', '_'));
	
	private final State initial;
	private State currState;
	private int transitions;
	
	
	SevenSegmentFSM() {
		this(new State());
	}
	
	private SevenSegmentFSM(State initial) {
		this.initial = initial;
		reset();
	}
	
	public void reset() {
		currState = initial;
		transitions = 0;
	}
	
	public void nextState(List<Character> segments) {
		segments.forEach( s -> {
			nextState(s);
		});
	}
	
	
	/*
	 * Transform to next state based to the specified segment. If the current state is an error state then
	 * no transformation is done.
	 * If the specified segment is an invalid character state is transformed to an error state. 
	 * If no possible transformations can happen then NoSuchStateException is thrown.
	 */
	public void nextState(char segment) {
		if(currState != STATE_ERROR) {
			State nextState = SUPPORTED_CHARACTERS.contains(segment) ?  currState.nextState(segment) : null;
			if(nextState == null) {
				nextState = STATE_ERROR;
			}
			transitions++;
			currState = nextState;
		}
	}
	
	/**
	 * Transform to next non empty state. This move is ideal for skipping missing input character.
	 */
	public boolean nextNonEmptyState() {
		//pick a state to move to for any character other than space.
		State nextState = null;
		for(char segment : SUPPORTED_CHARACTERS) {
			if(' ' != segment) {
				nextState = currState.nextState(segment);
				if(nextState != null) {
					break;
				}
			}
		}
		if(nextState != null) {
			transitions++;
			currState = nextState;
			return true;
		}
		return false;
	}
	
	/**
	 * Transform to next empty state. This move is ideal for skipping incorrectly scanned  input character.
	 */
	public boolean nextEmptyState() {
		//pick a state to move to for any character other than space.
		State nextState = currState.nextState(' ');
		if(nextState != null) {
			transitions++;
			currState = nextState;
			return true;
		}
		return false;
	}
	
	public int transitionCount() {
		return transitions;
	}
	
	public boolean isOnFinalState() {
		return currState.isFinal();
	}
	
	public boolean isError() {
		return currState == STATE_ERROR;
	}
	
	public String getValue() {
		return currState.getValue();
	}
	
	public boolean isInitialState() {
		return currState == initial;
	}
	
	State getIntial() {
		return initial;
	}
	
	SevenSegmentFSM copy() {
		SevenSegmentFSM ssfsm = new SevenSegmentFSM(initial);
		ssfsm.currState = this.currState;
		ssfsm.transitions = this.transitions;
		return ssfsm;
	}
}
