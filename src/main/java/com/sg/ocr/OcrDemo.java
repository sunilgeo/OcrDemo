package com.sg.ocr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sg.ocr.SevenSegmentScanner.ScanResult;

public class OcrDemo {
	private static final int SUPPORTED_DIGIT_COUNT = 9;
	private SevenSegmentScanner scanner;
	
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

	public List<ScanResult> scanFile(String path) throws IOException {
		return scan(new FileReader(path));
	}
	
	public List<ScanResult> scan(InputStreamReader inStreamReader) throws IOException {
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
				results.add(scanner.scanLines(lines));
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

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			System.out.println("Error: No file provided");
		} else {
			OcrDemo ocrDemo = new OcrDemo();
			try {
				List<ScanResult> results = ocrDemo.scanFile(args[0]);
				results.forEach(System.out::println);
			} catch (IOException e) {
				System.out.println("Error while scanning file - "  + e.getMessage());
				e.printStackTrace(System.err);
			}
		}
	}
}
