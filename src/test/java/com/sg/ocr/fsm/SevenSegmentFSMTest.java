package com.sg.ocr.fsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SevenSegmentFSMTest {

	@Test
	public void testSevenSegmentStateMachine() throws Exception {
		SevenSegmentFSM ssfsm = new SevenSegmentFSM();
		assertNotNull(ssfsm.getIntial());
		assertTrue("New state machine must be in intial state", ssfsm.isInitialState());
	}

	@Test
	public void testReset() throws Exception {
		SevenSegmentFSM ssfsm = new SevenSegmentFSM();
		State initialState = ssfsm.getIntial();
		initialState.addStateTransform('_');
		ssfsm.nextState('_');
		assertFalse(ssfsm.isInitialState());
		ssfsm.reset();
		assertTrue("After resetting state machine must be in intial state", ssfsm.isInitialState());
		
	}

	@Test
	public void testIsOnFinalState() throws Exception {
		SevenSegmentFSM ssfsm = new SevenSegmentFSM();
		State initialState = ssfsm.getIntial();
		initialState.addStateTransform('_', new State("test",true));
		ssfsm.reset();
		assertFalse(ssfsm.isOnFinalState());
		ssfsm.nextState('_');
		assertTrue( ssfsm.isOnFinalState());
	}

	@Test
	public void testNextState_withValidMoves() throws Exception {
		SevenSegmentFSM ssfsm = new SevenSegmentFSM();
		State initialState = ssfsm.getIntial();
		State nextState = initialState.addStateTransform('_');
		nextState = nextState.addStateTransform('|');
		nextState.addStateTransform('_', new State("Final",true));
		ssfsm.nextState('_');
		ssfsm.nextState('|');
		ssfsm.nextState('_');
		assertEquals("Final", ssfsm.getValue());
	}
	
	@Test
	public void testNextState_withInValidMoves() throws Exception {
		SevenSegmentFSM ssfsm = new SevenSegmentFSM();
		State initialState = ssfsm.getIntial();
		State nextState = initialState.addStateTransform('_');
		nextState = nextState.addStateTransform('|');
		nextState.addStateTransform('_', new State("Final",true));
		ssfsm.nextState('_');
		ssfsm.nextState('_'); //not a valid move
		assertTrue(ssfsm.isError());
	}

	@Test
	public void testCopy() throws Exception {
		SevenSegmentFSM ssfsm = new SevenSegmentFSM();
		State initialState = ssfsm.getIntial();
		State nextState = initialState.addStateTransform('_');
		nextState.setValue("FirstState");
		nextState = nextState.addStateTransform('|');
		nextState.addStateTransform('_', new State("Final",true));
		ssfsm.nextState('_');
		
		SevenSegmentFSM copy = ssfsm.copy();
		assertEquals(ssfsm.getIntial(),copy.getIntial());
		assertEquals(ssfsm.getValue(),copy.getValue());
		assertEquals(ssfsm.transitionCount(),copy.transitionCount());
	}	

	@Test
	public void testNextNonEmptyState() throws Exception {
		SevenSegmentFSM ssfsm = new SevenSegmentFSM();
		State initialState = ssfsm.getIntial();
		State nextState = initialState.addStateTransform('_');
		nextState = nextState.addStateTransform('|');
		nextState.addStateTransform('_', new State("Final",true));
		ssfsm.nextState('_');
		assertTrue(ssfsm.nextNonEmptyState());
		ssfsm.nextState('_');
		assertEquals("Final", ssfsm.getValue());
	}

}
