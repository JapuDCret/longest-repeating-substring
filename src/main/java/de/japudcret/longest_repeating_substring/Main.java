package de.japudcret.longest_repeating_substring;

import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.japudcret.longest_repeating_substring.work.Analyzer;

public class Main {
	private static final Logger log = LogManager.getLogger(Main.class);
	
	public static void main(String[] args) throws Exception {
		log.info("main(): Starting..");
		
		Analyzer distributor = new Analyzer("input/characters.bd2a09f6.txt");
		
		distributor.run();
		
		log.info("main(): Stopping..");
		
		return;
	}
}
