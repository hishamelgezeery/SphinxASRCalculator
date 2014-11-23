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

import java.util.ArrayList;
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
		// setting up Speech Recognition
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

		// Initializing variables
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
		// /////////////
		translations.put("eleven", 11.0);
		translations.put("twelve", 12.0);
		translations.put("thirteen", 13.0);
		translations.put("fourteen", 14.0);
		translations.put("fifteen", 15.0);
		translations.put("sixteen", 16.0);
		translations.put("seventeen", 17.0);
		translations.put("eighteen", 18.0);
		translations.put("nineteen", 19.0);
		// ////////////////
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

	public void parse(String operationSentence) {
		String[] sentenceWords = operationSentence.split(" ");

		// case of defining variable
		if (sentenceWords[0].equalsIgnoreCase("define")) {
			defineVariable(operationSentence);
			return;
		}
		// case of storing value in variable
		if (sentenceWords[1].equalsIgnoreCase("equals")) {
			storeInVariable(operationSentence);
			return;
		}
		// case of retrieving variable
		if (sentenceWords[0].equalsIgnoreCase("get")) {
			if (sentenceWords[1].equals("last"))
				result = lastResult;
			else
				result = variables.get(sentenceWords[1]);
			return;
		}
		// storing last result
		if (sentenceWords[0].equalsIgnoreCase("store")) {
			lastResult = result;
			return;
		}
		// execute log operation
		if (sentenceWords[0].equalsIgnoreCase("log")) {
			performLogOperation(sentenceWords);
			return;
		}
		// otherwise split the operation into operands and operators
		// and solve
		String[] operandsArray = operationSentence
				.split("(plus)|(minus)|(over)|(times)");
		ArrayList<String> operations = new ArrayList<String>();
		for (int i = 0; i < sentenceWords.length; i++) {
			if (sentenceWords[i].equals("plus")
					|| sentenceWords[i].equals("minus")
					|| sentenceWords[i].equals("over")
					|| sentenceWords[i].equals("times"))
				operations.add(sentenceWords[i]);
		}
		double[] translatedOperands = translateOperands(operandsArray);
		if (translatedOperands.length == 2) {
			switch (operations.get(0)) {
			case "plus":
				result = translatedOperands[0] + translatedOperands[1];
				break;
			case "minus":
				result = translatedOperands[0] - translatedOperands[1];
				break;
			case "times":
				result = translatedOperands[0] * translatedOperands[1];
				break;
			case "over":
				result = translatedOperands[0] / translatedOperands[1];
				break;
			}
		} else {
			// if 3 operands check if there is times or over and e
			double tempValue = -1.0;
			if (operations.get(0).equals("times")
					|| operations.get(0).equals("over")) {
				if (operations.get(0).equals("times"))
					tempValue = translatedOperands[0] * translatedOperands[1];
				if (operations.get(0).equals("over"))
					tempValue = translatedOperands[0] / translatedOperands[1];
				switch (operations.get(1)) {
				case "plus":
					result = tempValue + translatedOperands[2];
					break;
				case "minus":
					result = tempValue - translatedOperands[2];
					break;
				case "times":
					result = tempValue * translatedOperands[2];
					break;
				case "over":
					result = tempValue / translatedOperands[2];
					break;
				}
			} else {

				if (operations.get(1).equals("times"))
					tempValue = translatedOperands[1] * translatedOperands[2];
				else if (operations.get(1).equals("over"))
					tempValue = translatedOperands[1] / translatedOperands[2];
				else if (operations.get(1).equals("plus"))
					tempValue = translatedOperands[1] + translatedOperands[2];
				else if (operations.get(1).equals("minus"))
					tempValue = translatedOperands[1] - translatedOperands[2];
				switch (operations.get(0)) {
				case "plus":
					result = tempValue + translatedOperands[0];
					break;
				case "minus":
					result = tempValue - translatedOperands[0];
					break;
				case "times":
					result = tempValue * translatedOperands[0];
					break;
				case "over":
					result = tempValue / translatedOperands[0];
					break;
				}
			}
		}

	}

	private double[] translateOperands(String[] operandsArray) {
		double[] translatedOperands = new double[operandsArray.length];
		for (int i = 0; i < operandsArray.length; i++) {
			translatedOperands[i] = translateOperandsHelper(operandsArray[i]);
		}
		return translatedOperands;

	}

	private double translateOperandsHelper(String string) {
		double firstElement = 0;
		double secondElement = 0;
		String[] stringElements;
		// if operand contains power
		if (string.contains("power")) {
			stringElements = string.split("power");
			if (stringElements[0].length() > 2)
				firstElement = translateNumber(stringElements[0].split(" "));
			else
				firstElement = variables.get(stringElements[0]);
			System.out.println("stringElements[0] = " + stringElements[0]);
			System.out.println("stringElements[1] = " + stringElements[1]);
			secondElement = translateNumber(stringElements[1].trim().split(" "));
			return Math.pow(firstElement, secondElement);
		}

		if (string.contains("squared")) {
			string = string.replace("squared", "");
			if (variables.containsKey(string.trim()))
				return Math.pow(variables.get(string.trim()), 2);
			else
				return Math.pow(translateNumber(string.split(" ")), 2);
		}
		if (string.contains("cubed")) {
			string = string.replace("cubed", "");
			if (variables.containsKey(string.trim()))
				return Math.pow(variables.get(string.trim()), 3);
			else
				return Math.pow(translateNumber(string.split(" ")), 3);
		}

		return translateNumber(string.trim().split(" "));
	}

	public boolean isDoubleParsable(String string) {
		try {
			Double.parseDouble(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private double translateNumber(String[] numberElements) {
		ArrayList<String> translatedValues = new ArrayList<String>();
		// putting available translations instead of words from translations and
		// variables
		// Hashmaps
		for (int i = 0; i < numberElements.length; i++) {
			if (translations.containsKey(numberElements[i])) {
				double value = translations.get(numberElements[i]);
				translatedValues.add("" + value);
			} else if (variables.containsKey(numberElements[i])) {
				double value = variables.get(numberElements[i]);
				translatedValues.add("" + value);
			} else {
				translatedValues.add(numberElements[i]);
			}
		}
		translatedValues.remove("and");
		// grouping two digit numbers
		for (int i = 0; i < translatedValues.size() - 1; i++) {
			if (isDoubleParsable(translatedValues.get(i))
					&& Double.parseDouble(translatedValues.get(i)) >= 20
					&& isDoubleParsable(translatedValues.get(i + 1))
					&& Double.parseDouble(translatedValues.get(i + 1)) < 10) {
				translatedValues
						.set(i,
								""
										+ (Double.parseDouble(translatedValues
												.get(i)) + Double
												.parseDouble(translatedValues
														.get(i + 1))));
				translatedValues.remove(i + 1);
			}
		}
		// replacing thousand and hundred by 000 and 00 respectively
		for (int i = 0; i < translatedValues.size(); i++) {
			if (translatedValues.get(i).equalsIgnoreCase("thousand")) {
				translatedValues.set(i, "000");
			} else if (translatedValues.get(i).equalsIgnoreCase("hundred"))
				translatedValues.set(i, "00");
		}
		// grouping thousands and hundreds together to be ready to add all
		// elements
		// in Arraylist
		if (translatedValues.contains("000")) {
			int indexOfThousand = translatedValues.indexOf("000");
			double temp = Double.parseDouble(translatedValues
					.get(indexOfThousand - 1)) * 1000;
			translatedValues.set(indexOfThousand, "" + temp);
			translatedValues.remove(indexOfThousand - 1);

		}
		if (translatedValues.contains("00")) {
			int indexOfHundred = translatedValues.indexOf("00");
			double temp = Double.parseDouble(translatedValues
					.get(indexOfHundred - 1)) * 100;
			translatedValues.set(indexOfHundred, "" + temp);
			translatedValues.remove(indexOfHundred - 1);

		}
		System.out.println(translatedValues);
		boolean allDigits = true;
		translatedValues.remove("");
		for (int i = 0; i < translatedValues.size(); i++) {
			if (Double.parseDouble(translatedValues.get(i)) > 9) {
				allDigits = false;
				break;
			}
		}
		boolean allTwoDigitNumbers = true;
		double temp;
		for (int i = 0; i < translatedValues.size(); i++) {
			temp = Double.parseDouble(translatedValues.get(i));
			if (temp < 9 || temp > 100) {
				allTwoDigitNumbers = false;
				break;
			}
		}
		double value = 0;
		if (allDigits | allTwoDigitNumbers) {
			String number = "";
			for (int i = 0; i < translatedValues.size(); i++) {
				number += ((int) (Double.parseDouble(translatedValues.get(i))));
			}
			value = Double.parseDouble(number);
		} else {
			for (int i = 0; i < translatedValues.size(); i++) {
				value += Double.parseDouble(translatedValues.get(i));
			}
		}

		return value;
	}

	private void performLogOperation(String[] sentenceWords) {
		ArrayList<Double> operands = new ArrayList<Double>();
		boolean basePresent = false;
		int baseWordIndex = 0;
		for (int i = 0; i < sentenceWords.length; i++) {
			if (sentenceWords[i].equalsIgnoreCase("base")) {
				basePresent = true;
				baseWordIndex = i;
				break;
			}
		}
		if (basePresent) {
			String operand1 = "";
			String operand2 = "";
			for (int i = 1; i < baseWordIndex; i++) {
				if (i == baseWordIndex - 1)
					operand1 += sentenceWords[i];
				else
					operand1 += sentenceWords[i] + " ";
			}
			for (int i = baseWordIndex + 1; i < sentenceWords.length; i++) {
				if (i == sentenceWords.length-1)
					operand2 += sentenceWords[i];
				else
					operand2 += sentenceWords[i] + " ";
			}
			result = log(translateNumber(operand1.split(" ")), translateNumber(operand2.split(" ")));
		}
		else{
			String operand = "";
			for (int i = 1; i < sentenceWords.length; i++) {
				if (i == sentenceWords.length-1)
					operand += sentenceWords[i];
				else
					operand += sentenceWords[i] + " ";
			}
			result =  log(translateNumber(operand.split(" ")), 10.0);
		}

	}
	
	static double log(double x, double base)
	{
	    return Math.log(x) / Math.log(base);
	}

	private void storeInVariable(String operationSentence) {
		String[] sentenceWords = operationSentence.split(" ");
		variables.put(sentenceWords[0], Double.parseDouble(sentenceWords[2]));
	}

	private void defineVariable(String operationSentence) {
		String[] sentenceWords = operationSentence.split(" ");
		variables.put(sentenceWords[2], -1.0);

	}

	public static void main(String[] args) {
		/*
		 * test blockCalculator c = new Calculator(); String[] temp =
		 * {"four","two","six", "three"}; String[] temp2 = {"eleven", "twenty",
		 * "three"}; String[] temp3 = {"eleven", "hundred"}; String[] temp4 =
		 * {"sixteen", "hundred","and", "sixty", "nine"}; String[] temp5 =
		 * {"ninety", "nine", "thousand", "sixteen", "hundred","and", "sixty",
		 * "nine"}; System.out.println(c.translateNumber(temp));
		 * System.out.println(c.translateNumber(temp2));
		 * System.out.println(c.translateNumber(temp3));
		 * System.out.println(c.translateNumber(temp4));
		 * System.out.println(c.translateNumber(temp5));
		 */
	}
}