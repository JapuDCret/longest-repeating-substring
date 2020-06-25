package de.japudcret.longest_repeating_substring.work;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Analyzer implements Runnable {
	private static final Logger log = LogManager.getLogger(Analyzer.class);
	
	private final String input;

	public Analyzer(String inputFilePath) throws IllegalStateException {
		StringBuilder inputBuilder = new StringBuilder();
		
		log.info("constructor(): reading inputFile \"{}\"", inputFilePath);

		URL inputFilePathUrl = Analyzer.class.getClassLoader().getResource(inputFilePath);
		File inputFile;
		try {
			inputFile = new File(inputFilePathUrl.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}

		try (Scanner inputFileScanner = new Scanner(inputFile)) {
			while (inputFileScanner.hasNextLine()) {
				String line = inputFileScanner.nextLine();

				inputBuilder.append(line);
			}
			
			this.input = inputBuilder.toString();

			log.info("constructor(): successfully read inputFile");
			log.debug("constructor(): input is {}", this.input);
			log.debug("constructor(): input is {} characters long", this.input.length());
		} catch (FileNotFoundException e) {
			log.error("could not read inputFile \"{}\", an exception occurred: {}", inputFilePath, e);

			throw new IllegalStateException(e);
		}

		log.info("possible arrangements for  6 - 3: {}", this.calculatePositionArrangements(6, 3));
		log.info("possible arrangements for  8 - 3: {}", this.calculatePositionArrangements(7, 3));
		log.info("possible arrangements for  8 - 3: {}", this.calculatePositionArrangements(8, 3));
		log.info("possible arrangements for  8 - 3: {}", this.calculatePositionArrangements(9, 3));
		log.info("possible arrangements for 10 - 3: {}", this.calculatePositionArrangements(10, 3));
	}

	@Override
	public void run() {
		log.info("run(): starting..");
		
		this.calculateTheoreticalDistributions();
		
		int longestRepeatingSubstring = 1;

		this.calculateLongestRepeatingSubstring();
		
		log.info("run(): stopping..");
	}

	public void calculateTheoreticalDistributions() {
		log.info("calculateTheoreticalDistributions(): starting..");
		
		final int inputLength = this.input.length();
		// Assumption #001: We only need to look for substrings smaller than (inputLength / 2),
		//  because any substring larger than that cannot repeat

		int totalPositions = 0;
		
		int lastSubstringLength = -1;
		for(int i=1; i < inputLength / 2; i++) {
			int currentSubstringLength = inputLength / i;
			if (lastSubstringLength != currentSubstringLength) {
				lastSubstringLength = currentSubstringLength;
			} else {
				continue;
			}
			
			int positions = calculatePositionArrangements(inputLength, currentSubstringLength);
			totalPositions += positions;
			
			log.info("calculateTheoreticalDistributions(): there are {} positions for substrings of length {}", positions, currentSubstringLength);
		}
		
		log.info("calculateTheoreticalDistributions(): total count of positions is {}", totalPositions);
		
		log.info("calculateTheoreticalDistributions(): stopping..");
	}

	public static int calculatePositionArrangements(int inputLength, int substringLength) {
		int quotient = inputLength / substringLength;
		int remainder = inputLength % substringLength;
		
		// this formula was produced by checking the positioning for small substrings on paper
		return quotient * substringLength - (substringLength - (remainder + 1));
	}

	public void calculateLongestRepeatingSubstring() {
		log.info("calculateLongestRepeatingSubstring(): starting..");
		
		final int inputLength = this.input.length();

		Entry<String, Long> previousBiggestEntry = null;
		
		int lastSubstringLength = -1;
		for(int i=2; i < inputLength / 2; i++) {
			int currentSubstringLength = inputLength / i;
			if (lastSubstringLength != currentSubstringLength) {
				lastSubstringLength = currentSubstringLength;
			} else {
				continue;
			}
			
			// check all arrangements
			// The offset can move between 0 and i-1, because at position i we just repeat offset=0
			for(int offset = 0; offset < i - 1; offset++) {
				String[] substrings = buildSubstringArray(this.input, currentSubstringLength, offset);
				
				Map<String, Long> countMap =
												Stream
												.of(substrings)
												.collect(
													Collectors.groupingBy(Function.identity(), Collectors.counting())
												);
				
				System.out.println(countMap); //output the results of the Map
				
				Entry<String, Long> currentBiggestEntry =
												Collections
												.max(countMap.entrySet(),
													(entry1, entry2) -> (int) (entry1.getValue() - entry2.getValue())
												);
				
				// we are not interested in non-repeating substrings
				if(currentBiggestEntry.getValue() == 1) {
					continue;
				}
				
				log.debug("calculateLongestRepeatingSubstring(): biggest entry for substring length {} is {}", currentSubstringLength, currentBiggestEntry);
				
				if (previousBiggestEntry == null || (previousBiggestEntry.getValue() < currentBiggestEntry.getValue())) {
					previousBiggestEntry = currentBiggestEntry;
				}
			}
		}
		
		log.info("calculateLongestRepeatingSubstring(): the biggest entry of the input is {}", previousBiggestEntry);
		
		log.info("calculateLongestRepeatingSubstring(): stopping..");
	}

	private static String[] buildSubstringArray(String input, int currentSubstringLength, int offset) {
		final int inputLength = input.length();
		
		List<String> substrings = new LinkedList<String>();
		
		for(int i=offset; i < inputLength - currentSubstringLength; i++) {
			String substring = input.substring(i, i + currentSubstringLength);
			
			substrings.add(substring);
		}
		
		return substrings.toArray(new String[substrings.size()]);
	}
}
