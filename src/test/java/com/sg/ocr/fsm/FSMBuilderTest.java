package com.sg.ocr.fsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FSMBuilderTest {

	@Test
	public void testGetFormattedSegments() throws Exception {
		for(int i = 0; i < 10; i++) {
			System.out.println(FSMBuilder.getFormattedSegments(i));
			System.out.println("");
		}
	}

	@Test
	public void testBuildStateMachineForDigits() throws Exception {
		FSMBuilder dsmb = new FSMBuilder();
		SevenSegmentFSM sssm = dsmb.buildStateMachineForDigits();
		assertNotNull(sssm);
		for(int i =0 ; i < 10; i++) {
			sssm.reset();
			sssm.nextState(FSMBuilder.getSegmentsForDigit(i));
			assertFalse(sssm.isError());
			assertTrue(sssm.isOnFinalState());
			assertEquals(Integer.toString(i), sssm.getValue());
		}
	}

	@Test
	public void testCopyOf() throws Exception {
		FSMBuilder fsmbuilder = new FSMBuilder();
		SevenSegmentFSM ssfsm = fsmbuilder.buildStateMachineForDigits();
		ssfsm.nextState(FSMBuilder.getSegmentsForDigit(5));
		SevenSegmentFSM ssfmCopy = fsmbuilder.copyOf(ssfsm);
		assertEquals(ssfsm.getIntial(), ssfmCopy.getIntial());
		assertEquals(ssfsm.getValue(), ssfmCopy.getValue());
		
	}
}
