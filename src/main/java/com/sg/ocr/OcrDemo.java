package com.sg.ocr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sg.ocr.SevenSegmentScanner.ScanResult;

public class OcrDemo {
	private static final int SUPPORTED_DIGIT_COUNT = 9;
	private SevenSegmentScanner scanner;
	private static final Set<String> SUPPORTED_OPTIONS = new HashSet<>(Arrays.asList("-verify", "-detailed", "-fix"));
	
	OcrDemo() {
		init();
	}

	private void init() {
		scanner = new SevenSegmentScanner(SUPPORTED_DIGIT_COUNT);
	}

	void verifyLines(int lineStart, List<String> lines) {
		for (int i = 0; i < 3; i++) {
			try {
				scanner.verifyLine(lines.get(i));
			} catch (InvalidDataException e) {
				throw new RuntimeException(String.format("Error scanning line %s", lineStart + i), e);
			}
		}
	}

	public List<ScanResult> scanFile(String path,Set<String> options) throws IOException {
		return scan(new FileReader(path),options);
	}
	
	public List<ScanResult> scan(InputStreamReader inStreamReader,Set<String> options) throws IOException {
		BufferedReader reader = new BufferedReader(inStreamReader);
		List<ScanResult> results = new ArrayList<>();
		String line1 = null;
		int lineNumber = 1;
		try {
			while ((line1 = reader.readLine()) != null) {
				String line2 = reader.readLine();
				String line3 = reader.readLine();
				// skip the next line
				reader.readLine();
				List<String> lines = Arrays.asList(line1, line2, line3);
				verifyLines(lineNumber, lines);
				results.add(scanner.scanLines(lines, options.contains("-verify"), options.contains("-fix")));
				lineNumber += 4;
			}
		}
		finally {
			try {
				reader.close();
			} catch (IOException e) {

			}
		}
		return results;
	}
	
	private static Set<String> parseParams(String[] args) {
		return args.length > 1 ? 
				new HashSet<>(Arrays.asList(args).subList(1, args.length)) :
					Collections.emptySet();
    }
	
	private static boolean verifyParams(Set<String> params) {
		for(String param : params) {
			if(!SUPPORTED_OPTIONS.contains(param)) {
				System.out.println(String.format("Error: Unknown option %s", param));
				return false;
			}
		}
		return true;
	}
	
	private static void printUsage() {
		System.out.println(" Usage: OcrDemo <path to file> [options]");	
		System.out.println(" where options can be - ");
		System.out.println(" -verify - to verify scanned account numbers with checksum");
		System.out.println(" -detailed - to print details, account number with any error status");
	}

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			System.out.println("Error: No file provided");
			printUsage();
			System.exit(0);
		} 
		
		Set<String> params = parseParams(args);
		if(!verifyParams(params)) {
			printUsage();
			System.exit(0);
		}
		OcrDemo ocrDemo = new OcrDemo();
		try {
			List<ScanResult> results = ocrDemo.scanFile(args[0],params);
			OutputWriter outWriter = new OutputWriter(params.contains("-verify"), params.contains("-detailed"));
			outWriter.print(results);
		} catch (IOException e) {
			System.out.println("Error while scanning file - "  + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	
}
