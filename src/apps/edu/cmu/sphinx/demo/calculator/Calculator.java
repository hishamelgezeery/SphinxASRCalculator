/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.demo.calculator;

import java.util.HashMap;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

/**
 * A simple HelloWorld demo showing a simple speech application built using
 * Sphinx-4. This application uses the Sphinx-4 endpointer, which automatically
 * segments incoming audio into utterances and silences.
 */
public class Calculator {
	
	private Recognizer recognizer;
    private Microphone microphone;
    private HashMap<String, Double> translations;
    private HashMap<String, Double> variables;
	private String status;
	private double lastResult;
	private double result;
	
	public Calculator() {
		//setting up Speech Recognition
		ConfigurationManager cm = new ConfigurationManager(
				Calculator.class.getResource("calculator.config.xml"));
		recognizer = (Recognizer) cm.lookup("recognizer");
		recognizer.allocate();
		// start the microphone or exit if the programm if this is not possible
		microphone = (Microphone) cm.lookup("microphone");
		if (!microphone.startRecording()) {
			status = "Couldn't start microphone!";
			recognizer.deallocate();
		}
		
		//Initializing variables
		initializeTranslations();
		variables = new HashMap<String, Double>();
        variables.put("pie", Math.PI);
        variables.put("e", Math.E);
	}
	
	private void initializeTranslations() {
		translations = new HashMap<String, Double>();
		translations.put("one", 1.0);
		translations.put("two", 2.0);
		translations.put("three", 3.0);
		translations.put("four", 4.0);
		translations.put("five", 5.0);
		translations.put("six", 6.0);
		translations.put("seven", 7.0);
		translations.put("eight", 8.0);
		translations.put("nine", 9.0);
		translations.put("zero", 0.0);
		translations.put("oh", 0.0);
		///////////////
		translations.put("eleven", 11.0);
		translations.put("twelve", 12.0);
		translations.put("thirteen", 13.0);
		translations.put("fourteen", 14.0);
		translations.put("fifteen", 15.0);
		translations.put("sixteen", 16.0);
		translations.put("seventeen", 17.0);
		translations.put("eighteen", 18.0);
		translations.put("nineteen", 19.0);
		//////////////////
		translations.put("ten", 10.0);
		translations.put("twenty", 20.0);
		translations.put("thirty", 30.0);
		translations.put("fourty", 40.0);
		translations.put("fifty", 50.0);
		translations.put("sixty", 60.0);
		translations.put("seventy", 70.0);
		translations.put("eighty", 80.0);
		translations.put("ninety", 90.0);
	}
	
	public void parse(String operationSentence){
		String [] sentenceWords = operationSentence.split(" ");
		
		//case of defining variable
		if(sentenceWords[0].equalsIgnoreCase("define")){
			defineVariable(operationSentence);
			return;
		}
		//case of storing value in variable
		if(sentenceWords[1].equalsIgnoreCase("equals")){
			storeInVariable(operationSentence);
			return;
		}
		//case of retrieving variable
		if(sentenceWords[0].equalsIgnoreCase("get")){
			if(sentenceWords[1].equals("last"))
				result = lastResult;
			else
				result = variables.get(sentenceWords[1]);
			return;
		}
		//storing last result
		if(sentenceWords[0].equalsIgnoreCase("store")){
			lastResult = result;
			return;
		}
		
		
		
	}
	
	private void storeInVariable(String operationSentence) {
		String [] sentenceWords = operationSentence.split(" ");
		variables.put(sentenceWords[0], Double.parseDouble(sentenceWords[2]));
	}

	private void defineVariable(String operationSentence) {
		String [] sentenceWords = operationSentence.split(" ");
		variables.put(sentenceWords[2], -1.0);
		
	}

	public static void main(String[] args) {

	}
}
