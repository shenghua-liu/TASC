package com.ict.sentimentclassify.classify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ict.sentimentclassify.classifyresult.ClassifyResultEvaluation;
import com.ict.sentimentclassify.util.AppendContent2File;
import com.ict.sentimentclassify.util.FileUtil;
import com.ict.sentimentclassify.util.ReadContentFromFile;

public class CotrainingFrame {
	public static Logger logger = Logger.getLogger(CotrainingFrame.class
			.getName());

	/**
	 * split feature file into two feature file : text feature and non-text
	 * feature
	 * 
	 * @param fileDir
	 * @param fileName
	 * @param textFeatureFileName
	 * @param nontextFeatureFileName
	 * @return
	 */
	public static boolean splitFeatureIntoTextAndNontextFeatures(
			String fileDir, String fileName, String textFeatureFileName,
			String nontextFeatureFileName, int numPublicSentimentWord,
			int numPrivateSentimentWord, int numNontextFeature) {
		String splitFile = FileUtil.mergeFileDirAndName(fileDir, fileName);
		logger.info("split " + splitFile
				+ " into text feature non-text feature file");
		ReadContentFromFile splitFileReader = new ReadContentFromFile(splitFile);

		String textFeatureFile = FileUtil.mergeFileDirAndName(fileDir,
				textFeatureFileName);
		AppendContent2File textFeatureWriter = new AppendContent2File(
				textFeatureFile, false);

		String nontextFeatureFile = FileUtil.mergeFileDirAndName(fileDir,
				nontextFeatureFileName);
		AppendContent2File nontextFeatureWriter = new AppendContent2File(
				nontextFeatureFile, false);

		String readContent = null;
		while (splitFileReader.readyToRead()) {
			readContent = splitFileReader.readLineFromFile();
			textFeatureWriter.appendContent2File(readContent);
			nontextFeatureWriter.appendContent2File(readContent);
			if (readContent.equals("@data"))
				break;
		}
		String dataFeatureVector = null;
		while (splitFileReader.readyToRead()) {
			readContent = splitFileReader.readLineFromFile();

			dataFeatureVector = splitFeature(readContent, true,
					numPublicSentimentWord, numPrivateSentimentWord,
					numNontextFeature);
			textFeatureWriter.appendContent2File(dataFeatureVector);

			dataFeatureVector = splitFeature(readContent, false,
					numPublicSentimentWord, numPrivateSentimentWord,
					numNontextFeature);
			nontextFeatureWriter.appendContent2File(dataFeatureVector);
		}
		splitFileReader.closeFileReader();
		textFeatureWriter.closeFileWriter();
		nontextFeatureWriter.closeFileWriter();
		return true;
	}

	/**
	 * 
	 * split tweet feature Vector into text or non-text feature vector
	 * 
	 * @param tweetFeature
	 *            such as {5930 0,6093 0,6094 0,6095 positive}
	 * @param textFeatured
	 *            true or false
	 * @return if textFeatured is true then return text feature else return
	 *         nontext feature
	 */
	private static String splitFeature(String tweetFeature,
			boolean textFeatured, int numPublicSentimentWord,
			int numPrivateSentimentWord, int numNontextFeature) {
		String retFeautre = "";
		if (tweetFeature.length() == 0)
			return retFeautre;
		/**
		 * set scopeStart and scopeEnd to indicate which part should be removed
		 * removed feature value will be set 0
		 */
		int scopeStart = 0, scopeEnd = 0;
		int numSentimentWordFeature = numPublicSentimentWord
				+ numPrivateSentimentWord;
		// int numSentimentWordFeature = configInfo.getNumPublicSentimentWord()
		// + configInfo.getNumPrivateSentimentWord();
		if (textFeatured == false) {
			scopeStart = 1;
			scopeEnd = numSentimentWordFeature;
		} else {
			scopeStart = numSentimentWordFeature + 1;
			scopeEnd = numSentimentWordFeature + numNontextFeature;
			// + configInfo.getNumNontextFeature();
		}

		int lastcomma = tweetFeature.lastIndexOf(',');
		String tweetFeatures = tweetFeature.substring(1, lastcomma);
		String tweetLabel = tweetFeature.substring(lastcomma + 1,
				tweetFeature.length());

		String[] featureArray = tweetFeatures.split(",");
		for (int featureNo = 0; featureNo < featureArray.length; featureNo++) {
			/**
			 * test whether feature position is in scope or not
			 */
			String[] featureAndvalue = featureArray[featureNo].split(" ");
			if (Integer.valueOf(featureAndvalue[0]) >= scopeStart
					&& Integer.valueOf(featureAndvalue[0]) <= scopeEnd) {
				featureAndvalue[1] = String.valueOf(0);
			}
			featureArray[featureNo] = featureAndvalue[0] + " "
					+ featureAndvalue[1];
		}
		for (int featureNo = 0; featureNo < featureArray.length; featureNo++) {
			retFeautre += featureArray[featureNo];
			retFeautre += ",";
		}
		retFeautre = '{' + retFeautre + tweetLabel;
		return retFeautre;
	}

	/**
	 * Based on classify result from two classifier decide which test data
	 * belongs to and its confidence
	 * 
	 * @param textClassifyResultEvaluation
	 * @param nontextClassifyResultEvaluation
	 * @param classifyResultPositive
	 * @param classifyResultNeutral
	 * @param classifyResultNegative
	 * @return
	 */
	public static List<Map<Integer, Double>> getClassifyResult(
			ClassifyResultEvaluation textClassifyResultEvaluation,
			ClassifyResultEvaluation nontextClassifyResultEvaluation,
			HashMap<Integer, Double> classifyResultPositive,
			HashMap<Integer, Double> classifyResultNeutral,
			HashMap<Integer, Double> classifyResultNegative) {

		final int numTestData = textClassifyResultEvaluation.getNumTestData();
		for (int i = 0; i < numTestData; i++) {
			final double textClassifyLabel = textClassifyResultEvaluation
					.getMapTestData2ClassifyResult().get(i)
					.getClassifyResultLabel();
			final double nontextClassifyLabel = nontextClassifyResultEvaluation
					.getMapTestData2ClassifyResult().get(i)
					.getClassifyResultLabel();

			if (textClassifyLabel == nontextClassifyLabel) {
				double textClassifyResultConfidence = 0.0;
				double nontextClassifyResultConfidence = 0.0;
				double classifyResultConfidence = 0.0;
				if (0.0 == textClassifyLabel) {
					textClassifyResultConfidence = textClassifyResultEvaluation
							.getMapTestData2ClassifyResult().get(i)
							.getClassifyResultDistribute()[0];
					nontextClassifyResultConfidence = nontextClassifyResultEvaluation
							.getMapTestData2ClassifyResult().get(i)
							.getClassifyResultDistribute()[0];

					classifyResultConfidence = textClassifyResultConfidence > nontextClassifyResultConfidence ? nontextClassifyResultConfidence
							: textClassifyResultConfidence;
					classifyResultPositive.put(i + 1, classifyResultConfidence);

				} else if (1.0 == textClassifyLabel) {
					textClassifyResultConfidence = textClassifyResultEvaluation
							.getMapTestData2ClassifyResult().get(i)
							.getClassifyResultDistribute()[1];
					nontextClassifyResultConfidence = nontextClassifyResultEvaluation
							.getMapTestData2ClassifyResult().get(i)
							.getClassifyResultDistribute()[1];

					classifyResultConfidence = textClassifyResultConfidence > nontextClassifyResultConfidence ? nontextClassifyResultConfidence
							: textClassifyResultConfidence;
					classifyResultNeutral.put(i + 1, classifyResultConfidence);

				} else if (2.0 == textClassifyLabel) {
					textClassifyResultConfidence = textClassifyResultEvaluation
							.getMapTestData2ClassifyResult().get(i)
							.getClassifyResultDistribute()[2];
					nontextClassifyResultConfidence = nontextClassifyResultEvaluation
							.getMapTestData2ClassifyResult().get(i)
							.getClassifyResultDistribute()[2];

					classifyResultConfidence = textClassifyResultConfidence > nontextClassifyResultConfidence ? nontextClassifyResultConfidence
							: textClassifyResultConfidence;
					classifyResultNegative.put(i + 1, classifyResultConfidence);
				}
			}
		}// end for
		List<Map<Integer, Double>> classifyResultList = new ArrayList<Map<Integer, Double>>();
		classifyResultList.add(classifyResultPositive);
		classifyResultList.add(classifyResultNeutral);
		classifyResultList.add(classifyResultNegative);
		return classifyResultList;
	}
}
