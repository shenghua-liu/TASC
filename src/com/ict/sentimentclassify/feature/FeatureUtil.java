package com.ict.sentimentclassify.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ict.sentimentclassify.util.AppendContent2File;
import com.ict.sentimentclassify.util.FileUtil;
import com.ict.sentimentclassify.util.MapSortUtil;
import com.ict.sentimentclassify.util.ReadContentFromFile;

public class FeatureUtil {
	public static Logger logger = Logger.getLogger(FeatureUtil.class.getName());

	/**
	 * statistic word frequency in tweet content
	 * 
	 * @param tweetContent
	 * @param word
	 * @return
	 */
	public static int frequentStat(String tweetContent, String word) {
		int frequency = 0;
		if (tweetContent.length() == 0)
			return frequency;
		String[] words = tweetContent.split(" ");
		for (int wordNo = 0; wordNo < words.length; wordNo++) {
			if (words[wordNo].equals(word))
				frequency++;
		}
		return frequency;
	}

	/**
	 * retrieve feature vectors from data file
	 * 
	 * @param fileDir
	 * @param fileName
	 * @return map tweetId to feature vector
	 */
	public static Map<Integer, String> getFeatureVectors(String fileDir,
			String fileName) {
		Map<Integer, String> tweetId2FeatureVector = new HashMap<Integer, String>();
		String readFile = FileUtil.mergeFileDirAndName(fileDir, fileName);
		ReadContentFromFile fileReader = new ReadContentFromFile(readFile);
		String readContent = null;
		while (fileReader.readyToRead()) {
			readContent = fileReader.readLineFromFile();
			if (readContent.equals("@data"))
				break;
		}
		int tweetNo = 1;
		while (fileReader.readyToRead()) {
			readContent = fileReader.readLineFromFile();
			tweetId2FeatureVector.put(tweetNo, readContent);
			tweetNo++;
		}
		if (tweetId2FeatureVector.size() == 0)
			logger.warn("no data to read");
		return tweetId2FeatureVector;
	}

	/**
	 * take attention: evne though some pmi-ir value is 0, but it appears in
	 * tweet since weka can't appear once more sentiment word feature, frequency
	 * only be 1 if appeared
	 * 
	 * @param tweetFeature
	 *            such as {6093 0,6094 0,6095 8,6096 autumn,6097 saturday,6098
	 *            dawn,6099 34,6100 negative}
	 * @param privateWordNo
	 *            such as 143
	 * @return
	 * 
	 */
	public static int sentimentWordFrequencyStat(String tweetFeature,
			int privateWordNo, int numPublicSentimentWord) {
		int frequency = 0;
		if (tweetFeature.length() == 0)
			return frequency;
		// int sentimentWordNo = configInfo.getNumPublicSentimentWord()
		// + privateWordNo;
		int sentimentWordNo = numPublicSentimentWord + privateWordNo;
		int lastcomma = tweetFeature.lastIndexOf(',');
		String tweetFeatures = tweetFeature.substring(1, lastcomma);
		String tweetLabel = tweetFeature.substring(lastcomma + 1,
				tweetFeature.length());
		String[] featureArray = tweetFeatures.split(",");
		for (int featureNo = 0; featureNo < featureArray.length; featureNo++) {
			String[] featureAndValue = featureArray[featureNo].split(" ");
			if (featureAndValue[0].contains(Integer.toString(sentimentWordNo)))
				frequency++;
		}
		return frequency;
	}

	/**
	 * statistic non-public word frequency in tweet content
	 * 
	 * @param tweetContent
	 * @param privateSentimentWord
	 * @return
	 */
	public static int sentimentWordFrequencyStat(String tweetContent,
			String privateSentimentWord) {
		int frequency = 0;
		if (tweetContent.length() == 0)
			return frequency;
		String[] tweetWordArray = tweetContent.split(" ");
		for (int wordNo = 0; wordNo < tweetWordArray.length; wordNo++) {
			if (tweetWordArray[0].equals(privateSentimentWord))
				frequency++;
		}
		return frequency;
	}

	/**
	 * calculate non-public sentiment word weight based on non-public sentiment
	 * word set and classify result
	 * 
	 * @param testFileDir
	 * @param testFileName
	 * @param classifyResultPositive
	 *            tweetId whose classify result is positive and its confidence
	 * @param classifyResultNeutral
	 * @param classifyResultNegative
	 * @param privateWordNo2TweetIds
	 *            map to see which tweet contains non-pulbic word
	 * @return
	 */
	public static Map<Integer, Double[]> getPrivateWordsWeightsDistribute(
			String testFileDir, String testFileName,
			Map<Integer, Double> classifyResultPositive,
			Map<Integer, Double> classifyResultNeutral,
			Map<Integer, Double> classifyResultNegative,
			Map<Integer, List<Integer>> privateWordNo2TweetIds,
			String nonpublicWordFileDir, String nonpublicWordFileName,
			int numPublicSentimentWord) {

		Map<Integer, Double[]> privateWordWeightDistribute = new HashMap<Integer, Double[]>();
		// ReadContentFromFile privateWordReader = new ReadContentFromFile(
		// FileUtil.mergeFileDirAndName(
		// configInfo.getNonpublicwordFileDir(),
		// configInfo.getNonpublicwordFileName()));
		ReadContentFromFile privateWordReader = new ReadContentFromFile(
				FileUtil.mergeFileDirAndName(nonpublicWordFileDir,
						nonpublicWordFileName));

		Map<Integer, String> privateWordNo2Word = new HashMap<Integer, String>();

		for (int privateWordNo = 1; privateWordReader.readyToRead(); privateWordNo++) {
			String word = privateWordReader.readLineFromFile();
			privateWordNo2Word.put(privateWordNo, word);
		}
		privateWordReader.closeFileReader();

		// calculate non-public word weight distribute using test data
		Map<Integer, String> tweetId2FeatureVector = getFeatureVectors(
				testFileDir, testFileName);

		/**
		 * iterate every non-public word, and then calculate its weight
		 * distribute based on classify results which classify result is
		 * positive, neutral and negative
		 */
		for (Map.Entry<Integer, String> privateWordNo2WOrdEntry : privateWordNo2Word
				.entrySet()) {
			int privateWordNo = privateWordNo2WOrdEntry.getKey();
			String privateWord = privateWordNo2WOrdEntry.getValue();

			double positiveWeight = 0.0, negativeWeight = 0.0, neturalWeight = 0.0;

			for (Map.Entry<Integer, Double> classifyResultPositiveEntry : classifyResultPositive
					.entrySet()) {
				int tweetNo = classifyResultPositiveEntry.getKey();
				double tweetPositiveConfidence = classifyResultPositiveEntry
						.getValue();

				int frequency = sentimentWordFrequencyStat(
						tweetId2FeatureVector.get(tweetNo), privateWordNo,
						numPublicSentimentWord);
				if (frequency != 0) {
					positiveWeight += frequency * tweetPositiveConfidence;
					if (privateWordNo2TweetIds.containsKey(privateWordNo) == false) {
						List<Integer> tweetsnumberlist = new ArrayList<Integer>();
						tweetsnumberlist.add(tweetNo);
						privateWordNo2TweetIds.put(privateWordNo,
								tweetsnumberlist);
					} else
						privateWordNo2TweetIds.get(privateWordNo).add(tweetNo);
				}
			}
			for (Map.Entry<Integer, Double> classifyResultNeutralEntry : classifyResultNeutral
					.entrySet()) {
				int tweetNo = classifyResultNeutralEntry.getKey();
				double tweetNeturalConfidence = classifyResultNeutralEntry
						.getValue();

				int frequency = sentimentWordFrequencyStat(
						tweetId2FeatureVector.get(tweetNo), privateWordNo,
						numPublicSentimentWord);
				if (frequency != 0) {
					neturalWeight += frequency * tweetNeturalConfidence;
					if (privateWordNo2TweetIds.containsKey(privateWordNo) == false) {
						List<Integer> tweetsnumberlist = new ArrayList<Integer>();
						tweetsnumberlist.add(tweetNo);
						privateWordNo2TweetIds.put(privateWordNo,
								tweetsnumberlist);
					} else
						privateWordNo2TweetIds.get(privateWordNo).add(tweetNo);
				}
			}
			for (Map.Entry<Integer, Double> classifyResultNegativeEntry : classifyResultNegative
					.entrySet()) {
				int tweetNo = classifyResultNegativeEntry.getKey();
				double tweetNegativeConfidence = classifyResultNegativeEntry
						.getValue();
				int frequency = sentimentWordFrequencyStat(
						tweetId2FeatureVector.get(tweetNo), privateWordNo,
						numPublicSentimentWord);
				if (frequency != 0) {
					negativeWeight += frequency * tweetNegativeConfidence;
					if (privateWordNo2TweetIds.containsKey(privateWordNo) == false) {
						List<Integer> tweetsnumberlist = new ArrayList<Integer>();
						tweetsnumberlist.add(tweetNo);
						privateWordNo2TweetIds.put(privateWordNo,
								tweetsnumberlist);
					} else
						privateWordNo2TweetIds.get(privateWordNo).add(tweetNo);
				}
			}
			if (positiveWeight != 0 || neturalWeight != 0
					|| negativeWeight != 0) {
				double sumWeight = positiveWeight + neturalWeight
						+ negativeWeight;
				Double[] privatewordpmnweight = { positiveWeight / sumWeight,
						neturalWeight / sumWeight, negativeWeight / sumWeight };
				logger.info("non-public word " + privateWord
						+ " normalized weight is \t" + privatewordpmnweight[0]
						+ " " + privatewordpmnweight[1] + " "
						+ privatewordpmnweight[2]);
				privateWordWeightDistribute.put(privateWordNo,
						privatewordpmnweight);
			}
		}
		return privateWordWeightDistribute;
	}

	/**
	 * read tweets' content from file, and add tweets to Map map Entry is
	 * tweetid to tweetcontent
	 * 
	 * @param fileDir
	 * @param fileName
	 * @return HashMap
	 */
	public static Map<Integer, String> readTweetsContent2Map(String fileDir,
			String fileName) {
		String tweetfile = FileUtil.mergeFileDirAndName(fileDir, fileName);
		ReadContentFromFile contentReader = new ReadContentFromFile(tweetfile);
		Map<Integer, String> tweetId2contentMap = new HashMap<Integer, String>();
		int tweetNo = 1;
		String tweetcontent = null;
		while (contentReader.readyToRead()) {
			tweetcontent = contentReader.readLineFromFile();
			tweetId2contentMap.put(tweetNo, tweetcontent);
			tweetNo++;
		}
		contentReader.closeFileReader();
		return tweetId2contentMap;
	}

	/**
	 * update private word weight in feature file
	 * 
	 * @param fileDir
	 * @param fileName
	 * @param privateWord2WeightMap
	 */
	public static boolean updatePrivateWordWeight(String fileDir,
			String fileName, Map<Integer, Double> privateWord2WeightMap,
			int numPublicSentimentWord) {
		if (privateWord2WeightMap.size() == 0) {
			logger.info("no more private word weight to update");
			return true;
		}
		String featurefiledirfilename = FileUtil.mergeFileDirAndName(fileDir,
				fileName);
		logger.info("feature file to update:\t" + featurefiledirfilename);
		ReadContentFromFile fileReader = new ReadContentFromFile(
				featurefiledirfilename);

		// specification information in file
		List<String> specificationInfoList = new ArrayList<String>();
		// feature data
		List<String> dataList = new ArrayList<String>();
		String readContent = null;
		while (fileReader.readyToRead()) {
			readContent = fileReader.readLineFromFile();
			specificationInfoList.add(readContent);
			if (readContent.equals("@data"))
				break;
		}
		while (fileReader.readyToRead()) {
			readContent = fileReader.readLineFromFile();
			if (0 == readContent.length())
				continue;
			dataList.add(readContent);
		}
		fileReader.closeFileReader();

		for (Map.Entry<Integer, Double> entry : privateWord2WeightMap
				.entrySet()) {
			int privatewordnumber = entry.getKey();
			double privatewordweight = entry.getValue();
			for (int i = 0; i < dataList.size(); i++) {
				String updatedfeature = updateSentimentWordWeight(
						dataList.get(i), true, privatewordnumber,
						privatewordweight, numPublicSentimentWord);
				dataList.set(i, updatedfeature);
			}
		}

		AppendContent2File featurefilewriter = new AppendContent2File(
				featurefiledirfilename, false);
		for (int i = 0; i < specificationInfoList.size(); i++)
			featurefilewriter.appendContent2File(specificationInfoList.get(i));
		for (int i = 0; i < dataList.size(); i++)
			featurefilewriter.appendContent2File(dataList.get(i));
		featurefilewriter.closeFileWriter();
		return true;
	}

	/**
	 * update sentiment word weight
	 * 
	 * @param featureVector
	 *            such as {1477 -2.8391,6093 0,6094 0,6095 negative}
	 * @param privateSentimentWord
	 *            whether private sentiment or public sentiment word
	 * @param sentimentWordNo
	 *            public No or non-public word No in non-public word set
	 * @param sentimentWordNewWeight
	 * 
	 * @return
	 */
	public static String updateSentimentWordWeight(String featureVector,
			boolean privateSentimentWord, int sentimentWordNo,
			double sentimentWordNewWeight, int numPublicSentimentWord) {
		String retFeatureVector = "";
		if (featureVector.length() == 0) {
			logger.error("feature vector is empty");
			return retFeatureVector;
		}
		if (privateSentimentWord == true)
			sentimentWordNo += numPublicSentimentWord;
		// if (privateSentimentWord == true)
		// sentimentWordNo += configInfo.getNumPublicSentimentWord();
		if (featureVector.contains(Integer.toString(sentimentWordNo)) == false)
			return featureVector;
		int lastcomma = featureVector.lastIndexOf(',');
		String tweetFeatures = featureVector.substring(1, lastcomma);
		String tweetLabel = featureVector.substring(lastcomma + 1,
				featureVector.length());
		String[] tweetFeatureArray = tweetFeatures.split(",");
		for (int featureNo = 0; featureNo < tweetFeatureArray.length; featureNo++) {
			String[] featureAndValue = tweetFeatureArray[featureNo].split(" ");
			if (featureAndValue[0].equals(Integer.toString(sentimentWordNo)))
				tweetFeatureArray[featureNo] = Integer
						.toString(sentimentWordNo)
						+ " "
						+ Double.toString(sentimentWordNewWeight);
		}
		for (int featureNo = 0; featureNo < tweetFeatureArray.length; featureNo++) {
			retFeatureVector += tweetFeatureArray[featureNo];
			retFeatureVector += ",";
		}
		retFeatureVector = '{' + retFeatureVector + tweetLabel;
		return retFeatureVector;
	}

	/**
	 * select more confident private word by weight
	 * 
	 * @param privateWordsWeights
	 * @param alreadySelectedPrivatedWordWeights
	 * @return
	 */
	public static Map<Integer, Double> selectPrivateSentimentWord(
			Map<Integer, Double> privateWordsWeights,
			Map<Integer, Double> alreadySelectedPrivatedWordWeights,
			int numTopKPrivateWord) {
		int numSeletedPrivateWord = 0;
		Map.Entry[] DesSortedpositiveprivateword = MapSortUtil
				.sortDescendByValue(privateWordsWeights);
		for (int i = 0; i < DesSortedpositiveprivateword.length; i++) {
			if (numSeletedPrivateWord >= numTopKPrivateWord)
				break;
			int privateWordNo = (Integer) DesSortedpositiveprivateword[i]
					.getKey();
			double privateWordWeight = (Double) DesSortedpositiveprivateword[i]
					.getValue();
			if (alreadySelectedPrivatedWordWeights.containsKey(privateWordNo) == false) {
				numSeletedPrivateWord++;
			}
			alreadySelectedPrivatedWordWeights.put(privateWordNo,
					privateWordWeight);
		}
		return alreadySelectedPrivatedWordWeights;
	}
}
