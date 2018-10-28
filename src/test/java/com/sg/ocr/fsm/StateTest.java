package com.sg.ocr.fsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StateTest {

	@Test
	public void testStateConstructor() throws Exception {
		State state = new State();
		assertNull(state.getValue());
		assertFalse("By default state is not final",  state.isFinal());
	}
	
	@Test
	public void testStateChar_withArguments() throws Exception {
		State state = new State("8", true);
		assertEquals("8", state.getValue());
		assertTrue(state.isFinal());
		
		state = new State("3", false);
		assertEquals("3", state.getValue());
		assertFalse(state.isFinal());
	}

	@Test
	public void testAddStateTransform_withState() throws Exception {
		State fromState = new State("0",false);
		State toState1 = new State("1",false);
		State toState2 = new State("2", true);
		
		fromState.addStateTransform('_' ,toState1);
		fromState.addStateTransform('|', toState2);
		
		assertEquals(toState1, fromState.nextState('_'));
		assertEquals(toState2, fromState.nextState('|'));
		assertNull(fromState.nextState('?'));
		
	}
	
	@Test
	public void testAddStateTransform_withCharacter() throws Exception {
		State fromState = new State("0",false);

		State toState1 = fromState.addStateTransform('|');
		State toState2 = fromState.addStateTransform('_');

		assertEquals(toState1, fromState.nextState('|'));
		assertEquals(toState2, fromState.nextState('_'));
		assertNull(fromState.nextState('?'));
	}

	@Test
	public void testNextState() throws Exception {
		State fromState = new State("0",false);
		State toState1 = new State("1",true);
		fromState.addStateTransform('_', toState1);
		
		assertEquals(toState1, fromState.nextState('_'));
		assertNull(fromState.nextState('='));
	}
	
	@Test
	public void testSetValue() throws Exception {
		State fromState = new State("0",false);
		State toState1 =  fromState.addStateTransform('_');
		toState1.setValue("test");
		
		assertEquals("test", fromState.nextState('_').getValue());
		
		toState1.setValue("anotherValue");
		assertEquals("anotherValue", fromState.nextState('_').getValue());
	}
}
