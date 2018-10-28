package com.sg.ocr;

import java.util.ArrayList;
import java.util.List;

import com.sg.ocr.SevenSegmentScanner.ScanResult;

/*
 * A class to format and pretty print scan results.
 */
public class OutputWriter {
		private final boolean group;
		private final boolean detailed;
		
		OutputWriter(boolean group, boolean detailed) {
			this.group = group;
			this.detailed = detailed;
		}
	
		void print(List<ScanResult> results) {
			if(detailed) {
				System.out.println(detailedOutput(results));
			} else if(group) {
				System.out.println(groupOutput(results));
			} else {
				System.out.println(simpleOutput(results));
			}
		}
		
		private String simpleOutput(List<ScanResult> results) {
			StringBuilder sb = new StringBuilder();
			results.forEach(r -> sb.append(r.getAccountNumber()).append(System.lineSeparator()));
			return sb.toString();
		}
		
		private String groupOutput(List<ScanResult> results) {
	    	List<ScanResult> passed = new ArrayList<>();
	    	List<ScanResult> failed = new ArrayList<>();
	    	results.forEach(r -> {
	    		if(r.getErrorMsg() == null) {
	    			passed.add(r);
	    		} else {
	    			failed.add(r);
	    		}
	    	});
	    	StringBuilder sb = new StringBuilder(); 
	    	sb.append("Valid:").append(System.lineSeparator());
	    	passed.forEach(r -> sb.append(r.getAccountNumber()).append(System.lineSeparator()));
	    	sb.append(System.lineSeparator());
	    	sb.append("Invalid:").append(System.lineSeparator());
	    	failed.forEach(r -> sb.append(r.getAccountNumber()).append(System.lineSeparator()));
	    	return sb.toString();
	    }
		
		private String detailedOutput(List<ScanResult> results) {
			StringBuilder sb = new StringBuilder();
			results.forEach(r -> sb.append(r.toString()).append(System.lineSeparator()));
			return sb.toString();
		}

}
