package com.sg.ocr.fsm;

import java.util.HashMap;
import java.util.Map;

/*
 * Represents a state in the seven segment state machine.
 */
public class State {
	private final boolean isFinal;
	private Map<Character,State> stateTransformMap;
	private String value;
	
	
	public State() {
		this(null, false);
	}
	
	public State(String value, boolean isFinal) {
		this.value = value;
		this.isFinal = isFinal;
		stateTransformMap = new HashMap<>();
	}
	
	/**
	 * Add transformation to next State toState
	 * @param toState
	 */
	public void addStateTransform(char ch, State toState) {
		stateTransformMap.put(ch, toState);
	}
	
	/**
	 * Add transformation on character ch.
	 */
	public State addStateTransform(char ch) {
		State toState = nextState(ch);
		if(toState == null) {
			toState = new State();
		}
		addStateTransform(ch, toState);
		return toState;
	}
	
	/*
	 * Returns next state on specified character ch.
	 */
	public State nextState(char ch) {
		return stateTransformMap.get(ch);
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isFinal() {
		return isFinal;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("State: [value=\'").append(value).append("\']");
		sb.append(" to=[");
		stateTransformMap.entrySet().forEach(e -> {
			sb.append("\'").append(e.getKey()).append("\'->");
			sb.append(e.getValue());
		});
		sb.append("]");
		return sb.toString();
	}
	
}
