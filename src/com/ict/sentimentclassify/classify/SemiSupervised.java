package com.ict.sentimentclassify.classify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ict.sentimentclassify.util.AppendContent2File;
import com.ict.sentimentclassify.util.FileUtil;
import com.ict.sentimentclassify.util.MapSortUtil;
import com.ict.sentimentclassify.util.ReadContentFromFile;

public class SemiSupervised {
	public static Logger logger = Logger.getLogger(SemiSupervised.class
			.getName());

	/**
	 * select more confident test tweets from test data set to train data set
	 * based on classify result
	 * 
	 * @param classifyResultPositive
	 *            classify result is positive and its min confidence value from
	 *            two classifier
	 * @param classifyResultNeutral
	 * @param classifyResultNegative
	 * @param alreadyAddedTweetIds
	 *            already added tweets which can't be selected again
	 * 
	 * @return selected tweets contains more confident positive\neutral\negative
	 *         tweets
	 */
	public static List<Integer> selectTestTweets2Train(
			Map<Integer, Double> classifyResultPositive,
			Map<Integer, Double> classifyResultNeutral,
			Map<Integer, Double> classifyResultNegative,
			Set<Integer> alreadyAddedTweetIds, int numTopKTestData2TrainSet,
			double classifyResultThreshold) {

		List<Integer> tweetIdsFromTest2Train = new ArrayList<Integer>();

		// sort three map by classify result confident value
		Map.Entry[] classifyResultPositiveEntries = MapSortUtil
				.sortDescendByValue(classifyResultPositive);
		Map.Entry[] classifyResultNeturalEntries = MapSortUtil
				.sortDescendByValue(classifyResultNeutral);
		Map.Entry[] classifyResultNegativeEntries = MapSortUtil
				.sortDescendByValue(classifyResultNegative);

		// int numTopKTestData2TrainSet =
		// configInfo.getNumTopKTestData2TrainSet();
		// double classifyResultThreshold = configInfo
		// .getClassifyResultThreshold();
		int numSelectedTweets = 1;
		for (int i = 0; numSelectedTweets <= numTopKTestData2TrainSet
				&& i < classifyResultPositiveEntries.length; i++) {
			int tweetNo = (Integer) classifyResultPositiveEntries[i].getKey();
			double confidenceValue = (Double) classifyResultPositiveEntries[i]
					.getValue();
			if (confidenceValue >= classifyResultThreshold
					&& false == alreadyAddedTweetIds.contains(tweetNo)) {
				tweetIdsFromTest2Train.add(tweetNo);
				numSelectedTweets++;
			} else if (confidenceValue < classifyResultThreshold)
				break;
		}// end for
		numSelectedTweets = 1;
		for (int i = 0; numSelectedTweets <= numTopKTestData2TrainSet
				&& i < classifyResultNeturalEntries.length; i++) {
			int tweetnumber = (Integer) classifyResultNeturalEntries[i]
					.getKey();
			double confidenceValue = (Double) classifyResultNeturalEntries[i]
					.getValue();
			if (confidenceValue >= classifyResultThreshold
					&& false == alreadyAddedTweetIds.contains(tweetnumber)) {
				tweetIdsFromTest2Train.add(tweetnumber);
				numSelectedTweets++;

			}
		}
		numSelectedTweets = 1;
		for (int i = 0; numSelectedTweets <= numTopKTestData2TrainSet
				&& i < classifyResultNegativeEntries.length; i++) {
			int tweetnumber = (Integer) classifyResultNegativeEntries[i]
					.getKey();
			double confidenceValue = (Double) classifyResultNegativeEntries[i]
					.getValue();
			if (confidenceValue >= classifyResultThreshold
					&& false == alreadyAddedTweetIds.contains(tweetnumber)) {
				tweetIdsFromTest2Train.add(tweetnumber);
				numSelectedTweets++;
			}
		}

		return tweetIdsFromTest2Train;
	}

	/**
	 * according to the tweetNo in list, transfer data from test file to train
	 * file, and the others moved to text file used for next iteration
	 * 
	 * @param testFileDir
	 * @param testFileName
	 * @param nextTestFileName
	 * @param trainFileDir
	 * @param trainFileName
	 * @param tweetIdsFromTest2Train
	 *            tweets need to be transferred from test to train
	 * @return
	 */
	public static boolean transferTweetsFromTest2Train(String testFileDir,
			String testFileName, String nextTestFileName, String trainFileDir,
			String trainFileName, List<Integer> tweetIdsFromTest2Train) {

		ReadContentFromFile testFileReader = new ReadContentFromFile(
				FileUtil.mergeFileDirAndName(testFileDir, testFileName));
		AppendContent2File nextTestFileWriter = new AppendContent2File(
				FileUtil.mergeFileDirAndName(testFileDir, nextTestFileName));
		AppendContent2File trainFileWriter = new AppendContent2File(
				FileUtil.mergeFileDirAndName(trainFileDir, trainFileName));

		// delete duplicate ones
		HashSet hashset = new HashSet(tweetIdsFromTest2Train);
		tweetIdsFromTest2Train.clear();
		tweetIdsFromTest2Train.addAll(hashset);
		// sort by tweet id
		Collections.sort(tweetIdsFromTest2Train);
		logger.info("augmenting tweet ids");
		for (int i = 0; i < tweetIdsFromTest2Train.size(); i++) {
			logger.info(tweetIdsFromTest2Train.get(i));
		}
		String readContent = null;
		while (true == testFileReader.readyToRead()) {
			readContent = testFileReader.readLineFromFile();
			nextTestFileWriter.appendContent2File(readContent);
			if (true == readContent.equals("@data")) {
				break;
			}
		}

		int curTestTweetNo = 1;
		int curListTweetNo = 1;// number of already read tweet in list
		while (true == testFileReader.readyToRead()) {
			readContent = testFileReader.readLineFromFile();
			// all the tweets in list have been read
			if (curListTweetNo > tweetIdsFromTest2Train.size()) {
				nextTestFileWriter.appendContent2File(readContent);
				curTestTweetNo++;
				break;
			} else {
				if (curTestTweetNo == tweetIdsFromTest2Train
						.get(curListTweetNo - 1)) {
					trainFileWriter.appendContent2File(readContent);
					curListTweetNo++;
				} else {
					nextTestFileWriter.appendContent2File(readContent);
				}
				curTestTweetNo++;
			}
		}
		testFileReader.closeFileReader();
		nextTestFileWriter.closeFileWriter();
		trainFileWriter.closeFileWriter();
		return true;
	}
}
