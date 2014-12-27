package com.ict.sentimentclassify.classify;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import weka.core.Instances;
import ClassifyStatistic.ClassifyStatic;

import com.ict.sentimentclassify.classifier.SMOClassifier;
import com.ict.sentimentclassify.classifyresult.ClassifyResultEvaluation;
import com.ict.sentimentclassify.config.ConfigInfo;
import com.ict.sentimentclassify.config.ConfigReader;
import com.ict.sentimentclassify.feature.FeatureUtil;
import com.ict.sentimentclassify.util.AppendContent2File;
import com.ict.sentimentclassify.util.FileUtil;

public class AdaptivecotrainClassify {

	public static Logger logger = Logger
			.getLogger(AdaptivecotrainClassify.class.getName());
	private String configXmlFileName;
	private ConfigReader configReader;
	private ConfigInfo configInfo;

	private Map<Integer, List<Integer>> mapPrivatewordNo2TweetsNo = new HashMap<Integer, List<Integer>>();

	private String textfeatureFileName = null;
	private String nontextfeatureFileName = null;

	public AdaptivecotrainClassify(String configXmlFileName) {
		this.configXmlFileName = configXmlFileName;
		this.configReader = new ConfigReader(configXmlFileName);
		this.configInfo = this.configReader.ReadConfig();

		this.textfeatureFileName = "textfeature.arff";
		this.nontextfeatureFileName = "nontextfeature.arff";
	}

	public boolean init() {
		FileUtil.createDir(this.configInfo.getClassifyStatResultDir());
		return true;
	}

	/**
	 * algorithm interface
	 */
	public boolean start() {
		Map<Integer, Double> mapPrivateWord2Weight = new HashMap<Integer, Double>();
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMddHHmm");
		String resultFileName = FileUtil.mergeFileDirAndName(
				configInfo.getClassifyStatResultDir(),
				dataFormat.format(new Date()));

		AppendContent2File resultWriter = new AppendContent2File(resultFileName);
		logger.info("parameter numTopKTestData2TrainSet:\t"
				+ configInfo.getNumTopKTestData2TrainSet()
				+ "\tparameter classifyResultThreshold:\t"
				+ configInfo.getClassifyResultThreshold()
				+ "\tparameter maxIteration:\t" + configInfo.getMaxIteration()
				+ "\tparameter numTopKPrivateWord:\t"
				+ configInfo.getNumTopKPrivateWord());
		resultWriter.appendContent2File("parameter numTopKTestData2TrainSet:\t"
				+ configInfo.getNumTopKTestData2TrainSet());
		resultWriter.appendContent2File("parameter classifyResultThreshold:\t"
				+ configInfo.getClassifyResultThreshold());
		resultWriter.appendContent2File("parameter maxIteration:\t"
				+ configInfo.getMaxIteration());
		resultWriter.appendContent2File("parameter numTopKPrivateWord:\t"
				+ configInfo.getNumTopKPrivateWord());
		resultWriter
				.appendContent2File("Iteration Result:\tNumTestData:\taccuracy\tprecision\trecall");

		final int MaxIteration = configInfo.getMaxIteration();
		for (int dayNo = 1; dayNo <= configInfo.getNumPeriod(); dayNo++) {
			logger.info("dayNo " + dayNo
					+ ":\ttest dataset, select testData to TrainDataSet");
			logger.info("train classifier using train data");
			String curPeriodTrainFileName = configInfo.getTrainFileName()
					+ ".arff";

			HashMap<Integer, Double> classifyResultPositive = new HashMap<Integer, Double>();
			HashMap<Integer, Double> classifyResultNetural = new HashMap<Integer, Double>();
			HashMap<Integer, Double> classifyResultNegative = new HashMap<Integer, Double>();

			int curIteration = 0;
			String testFileDir = null;
			String testFileName = null;
			String nextTestFileName = null;

			Set<Integer> tweetidadded = new HashSet<Integer>();
			tweetidadded.clear();

			for (curIteration = 0; curIteration < MaxIteration; curIteration++) {
				classifyResultPositive.clear();
				classifyResultNetural.clear();
				classifyResultNegative.clear();

				logger.info("Iteration " + (curIteration + 1));
				Instances testInstances, testFeatureInstances, nontextFeatureInstances = null;
				logger.info("train data set:\t" + curPeriodTrainFileName);

				logger.info("update private words weight in train file:\t");
				FeatureUtil.updatePrivateWordWeight(
						configInfo.getTrainFileDir(), curPeriodTrainFileName,
						mapPrivateWord2Weight,
						configInfo.getNumPublicSentimentWord());

				logger.info("split train data set feature into text and non-text feature, and then train classifier respectively");
				CotrainingFrame.splitFeatureIntoTextAndNontextFeatures(
						configInfo.getTrainFileDir(), curPeriodTrainFileName,
						this.textfeatureFileName, this.nontextfeatureFileName,
						configInfo.getNumPublicSentimentWord(),
						configInfo.getNumPrivateSentimentWord(),
						configInfo.getNumNontextFeature());

				testFileDir = configInfo.getTestFileDir() + "/day"
						+ Integer.toString(dayNo);
				testFileName = configInfo.getTestFileName()
						+ Integer.toString(curIteration);
				testFileName += ".arff";

				nextTestFileName = configInfo.getTestFileName();
				nextTestFileName += Integer.toString(curIteration + 1);
				nextTestFileName += ".arff";

				logger.info("test data set:\t" + testFileName);
				logger.info("update private word weight in test data set:\t");
				FeatureUtil.updatePrivateWordWeight(testFileDir, testFileName,
						mapPrivateWord2Weight,
						configInfo.getNumPublicSentimentWord());

				logger.info("update initial train data set\t");
				FeatureUtil.updatePrivateWordWeight(testFileDir,
						"testfile0.arff", mapPrivateWord2Weight,
						configInfo.getNumPublicSentimentWord());

				SMOClassifier combineClassifier = new SMOClassifier(
						"COMBINE(TEXT+NONTEXT) CLASSIFIER",
						FileUtil.mergeFileDirAndName(
								configInfo.getTrainFileDir(),
								curPeriodTrainFileName));

				ClassifyResultEvaluation combineClassifyResultEvaluation = combineClassifier
						.classfy(FileUtil.mergeFileDirAndName(testFileDir,
								"testfile0.arff"));
				if (null == combineClassifyResultEvaluation) {
					logger.error("classifier "
							+ combineClassifier.getClassifiername()
							+ " classify test data set testfile0.arff");
					return false;
				}
				logger.info("combine classifier classify result [numTestData accuracy tPositive fPositiveNeutral "
						+ "fPositiveNegative fNeutralPositive tneutral fNeutralNegative fNegativePositve "
						+ "fNegativeNeutral tNegative]");
				logger.info(combineClassifyResultEvaluation.getNumTestData()
						+ "\t"
						+ combineClassifyResultEvaluation.getAccuracy()
						+ "\t"
						+ combineClassifyResultEvaluation.numTruePositive
						+ "\t"
						+ combineClassifyResultEvaluation.numFalsePositiveNeutral
						+ "\t"
						+ combineClassifyResultEvaluation.numFalsePositiveNegative
						+ "\t"
						+ combineClassifyResultEvaluation.numFalseNeutralPositive
						+ "\t"
						+ combineClassifyResultEvaluation.numTrueNeutral
						+ "\t"
						+ combineClassifyResultEvaluation.numFalseNeutralNegative
						+ "\t"
						+ combineClassifyResultEvaluation.numFalseNegativePositive
						+ "\t"
						+ combineClassifyResultEvaluation.numFalseNegativeNeutral
						+ "\t"
						+ combineClassifyResultEvaluation.numTrueNegative);
				writeClassifyResultEvaluation(dayNo, curIteration,
						combineClassifier, combineClassifyResultEvaluation,
						resultWriter);

				// train classifier based on text feature
				SMOClassifier textClassifer = new SMOClassifier(
						"Text classifier", FileUtil.mergeFileDirAndName(
								configInfo.getTrainFileDir(),
								this.textfeatureFileName));
				ClassifyResultEvaluation textClassifyResultEvaluation = textClassifer
						.classfy(FileUtil.mergeFileDirAndName(testFileDir,
								"testfile0.arff"));
				if (null == textClassifyResultEvaluation) {
					logger.error("classifier "
							+ textClassifer.getClassifiername()
							+ " classify test data set testfile0.arff");
					return false;
				}
				logger.info("text classifier classify result [numTestData accuracy tPositive fPositiveNeutral "
						+ "fPositiveNegative fNeutralPositive tneutral fNeutralNegative fNegativePositve "
						+ "fNegativeNeutral tNegative]");
				logger.info(textClassifyResultEvaluation.getNumTestData()
						+ "\t" + textClassifyResultEvaluation.getAccuracy()
						+ "\t" + textClassifyResultEvaluation.numTruePositive
						+ "\t"
						+ textClassifyResultEvaluation.numFalsePositiveNeutral
						+ "\t"
						+ textClassifyResultEvaluation.numFalsePositiveNegative
						+ "\t"
						+ textClassifyResultEvaluation.numFalseNeutralPositive
						+ "\t" + textClassifyResultEvaluation.numTrueNeutral
						+ "\t"
						+ textClassifyResultEvaluation.numFalseNeutralNegative
						+ "\t"
						+ textClassifyResultEvaluation.numFalseNegativePositive
						+ "\t"
						+ textClassifyResultEvaluation.numFalseNegativeNeutral
						+ "\t" + textClassifyResultEvaluation.numTrueNegative);

				// train classifier based on non-text feature
				SMOClassifier nontextClassifer = new SMOClassifier(
						"NONTEXT CLASSIFER", FileUtil.mergeFileDirAndName(
								configInfo.getTrainFileDir(),
								this.nontextfeatureFileName));
				ClassifyResultEvaluation nontextClassifyResultEvaluation = nontextClassifer
						.classfy(FileUtil.mergeFileDirAndName(testFileDir,
								"testfile0.arff"));
				if (null == nontextClassifyResultEvaluation) {
					logger.error("classifier "
							+ nontextClassifer.getClassifiername()
							+ " classify test data set testfile0.arff");
					return false;
				}
				logger.info("non-text classifier classify result [numTestData accuracy tPositive fPositiveNeutral "
						+ "fPositiveNegative fNeutralPositive tneutral fNeutralNegative fNegativePositve "
						+ "fNegativeNeutral tNegative]");
				logger.info(nontextClassifyResultEvaluation.getNumTestData()
						+ "\t"
						+ nontextClassifyResultEvaluation.getAccuracy()
						+ "\t"
						+ nontextClassifyResultEvaluation.numTruePositive
						+ "\t"
						+ nontextClassifyResultEvaluation.numFalsePositiveNeutral
						+ "\t"
						+ nontextClassifyResultEvaluation.numFalsePositiveNegative
						+ "\t"
						+ nontextClassifyResultEvaluation.numFalseNeutralPositive
						+ "\t"
						+ nontextClassifyResultEvaluation.numTrueNeutral
						+ "\t"
						+ nontextClassifyResultEvaluation.numFalseNeutralNegative
						+ "\t"
						+ nontextClassifyResultEvaluation.numFalseNegativePositive
						+ "\t"
						+ nontextClassifyResultEvaluation.numFalseNegativeNeutral
						+ "\t"
						+ nontextClassifyResultEvaluation.numTrueNegative);

				this.writeClassifyResultEvaluation(dayNo, curIteration,
						textClassifer, textClassifyResultEvaluation,
						resultWriter);
				this.writeClassifyResultEvaluation(dayNo, curIteration,
						nontextClassifer, nontextClassifyResultEvaluation,
						resultWriter);

				List<Map<Integer, Double>> classifyResultList = CotrainingFrame
						.getClassifyResult(textClassifyResultEvaluation,
								nontextClassifyResultEvaluation,
								classifyResultPositive, classifyResultNetural,
								classifyResultNegative);
				classifyResultPositive = (HashMap<Integer, Double>) classifyResultList
						.get(0);
				classifyResultNetural = (HashMap<Integer, Double>) classifyResultList
						.get(1);
				classifyResultNegative = (HashMap<Integer, Double>) classifyResultList
						.get(2);

				// take first k tweets which classify result confidence larger
				// than threshold respective from three class
				List<Integer> tweetIdsFromTest2Train = SemiSupervised
						.selectTestTweets2Train(classifyResultPositive,
								classifyResultNetural, classifyResultNegative,
								tweetidadded,
								configInfo.getNumTopKTestData2TrainSet(),
								configInfo.getClassifyResultThreshold());
				if (tweetIdsFromTest2Train.size() == 0) {
					logger.info("no more test data whose classify result confidence is enough larger than threshold");
					break;// step out current period
				}
				for (int tweetId : tweetIdsFromTest2Train) {
					tweetidadded.add(tweetId);
				}

				// transfer selected test data to train file
				if (false == SemiSupervised.transferTweetsFromTest2Train(
						testFileDir, "testfile0.arff", nextTestFileName,
						configInfo.getTrainFileDir(), curPeriodTrainFileName,
						tweetIdsFromTest2Train)) {
					logger.error("selected test data transferred to train file");
					return false;
				}

			}// end for iteration

			/**
			 * select non-public sentiment word Map each non-public sentiment
			 * word to weight distribute in positive, neutral and negative, and
			 * then decide non-public sentiment word weight
			 */
			Map<Integer, List<Integer>> privateWord2WeightDistribute = new HashMap<Integer, List<Integer>>();

			Map<Integer, Double[]> privateWord2WeightsDistribute = FeatureUtil
					.getPrivateWordsWeightsDistribute(testFileDir,
							"testfile0.arff", classifyResultPositive,
							classifyResultNetural, classifyResultNegative,
							privateWord2WeightDistribute,
							configInfo.getNonpublicwordFileDir(),
							configInfo.getNonpublicwordFileName(),
							configInfo.getNumPublicSentimentWord());

			Map<Integer, Double> positivePrivateWord2Weight = new HashMap<Integer, Double>();
			Map<Integer, Double> neutralPrivateWord2Weight = new HashMap<Integer, Double>();
			Map<Integer, Double> negativePrivateWord2Weight = new HashMap<Integer, Double>();

			for (Map.Entry<Integer, Double[]> entry : privateWord2WeightsDistribute
					.entrySet()) {
				int privateWordNo = entry.getKey();
				Double[] privateWordWeightDist = entry.getValue();
				if (privateWordWeightDist[0] >= privateWordWeightDist[1]
						&& privateWordWeightDist[0] >= privateWordWeightDist[2])
					positivePrivateWord2Weight.put(privateWordNo,
							privateWordWeightDist[0]);
				else if (privateWordWeightDist[1] >= privateWordWeightDist[0]
						&& privateWordWeightDist[1] >= privateWordWeightDist[2])
					neutralPrivateWord2Weight.put(privateWordNo,
							privateWordWeightDist[1]);
				else if (privateWordWeightDist[2] >= privateWordWeightDist[0]
						&& privateWordWeightDist[2] >= privateWordWeightDist[1])
					negativePrivateWord2Weight.put(privateWordNo,
							privateWordWeightDist[2]);
			}
			mapPrivateWord2Weight = FeatureUtil.selectPrivateSentimentWord(
					positivePrivateWord2Weight, mapPrivateWord2Weight,
					configInfo.getNumTopKPrivateWord());
			mapPrivateWord2Weight = FeatureUtil.selectPrivateSentimentWord(
					neutralPrivateWord2Weight, mapPrivateWord2Weight,
					configInfo.getNumTopKPrivateWord());
			mapPrivateWord2Weight = FeatureUtil.selectPrivateSentimentWord(
					negativePrivateWord2Weight, mapPrivateWord2Weight,
					configInfo.getNumTopKPrivateWord());

			// update selected private sentiment word
			if (false == FeatureUtil.updatePrivateWordWeight(
					configInfo.getTrainFileDir(), curPeriodTrainFileName,
					mapPrivateWord2Weight,
					configInfo.getNumPublicSentimentWord())) {
				logger.error("update train file private sentiment word weight");
				return false;
			}
			if (false == FeatureUtil.updatePrivateWordWeight(testFileDir,
					"testfile0.arff", mapPrivateWord2Weight,
					configInfo.getNumPublicSentimentWord())) {
				logger.error("update test file private sentiment word weight");
				return false;
			}
			String curPeriodTrainFile = FileUtil.mergeFileDirAndName(
					configInfo.getTrainFileDir(), curPeriodTrainFileName);
			String curPeriodTestFile = FileUtil.mergeFileDirAndName(
					testFileDir, "testfile0.arff");

			SMOClassifier curPeriodClassifier = new SMOClassifier(
					"FINAL(TEXT+NONTEXT) CLASSIFIER", curPeriodTrainFile);
			ClassifyResultEvaluation curPeriodClassifyResultEvaluation = curPeriodClassifier
					.classfy(curPeriodTestFile);
			if (null == curPeriodClassifyResultEvaluation) {
				logger.error("current " + dayNo + " period classifier "
						+ curPeriodClassifier.getClassifiername()
						+ "classify test file");
				return false;
			}
			logger.info("current period classifier classify result [numTestData accuracy tPositive fPositiveNeutral "
					+ "fPositiveNegative fNeutralPositive tneutral fNeutralNegative fNegativePositve "
					+ "fNegativeNeutral tNegative]");
			logger.info(curPeriodClassifyResultEvaluation.getNumTestData()
					+ "\t"
					+ curPeriodClassifyResultEvaluation.getAccuracy()
					+ "\t"
					+ curPeriodClassifyResultEvaluation.numTruePositive
					+ "\t"
					+ curPeriodClassifyResultEvaluation.numFalsePositiveNeutral
					+ "\t"
					+ curPeriodClassifyResultEvaluation.numFalsePositiveNegative
					+ "\t"
					+ curPeriodClassifyResultEvaluation.numFalseNeutralPositive
					+ "\t"
					+ curPeriodClassifyResultEvaluation.numTrueNeutral
					+ "\t"
					+ curPeriodClassifyResultEvaluation.numFalseNeutralNegative
					+ "\t"
					+ curPeriodClassifyResultEvaluation.numFalseNegativePositive
					+ "\t"
					+ curPeriodClassifyResultEvaluation.numFalseNegativeNeutral
					+ "\t" + curPeriodClassifyResultEvaluation.numTrueNegative);

			writeClassifyResultEvaluation(dayNo, curIteration,
					curPeriodClassifier, curPeriodClassifyResultEvaluation,
					resultWriter);
			resultWriter.appendContent2File(" ");

			// statistic current period classify result used for visualization
			try {
				String visualizationStatFileName = "ClassifyResultVisualizationStat"
						+ Integer.toString(dayNo);
				String visualizationStatFile = FileUtil.mergeFileDirAndName(
						configInfo.getClassifyStatResultDir(),
						visualizationStatFileName);
				ClassifyStatic classifystat = new ClassifyStatic(
						configInfo.getNumLayer(), curPeriodTrainFile,
						curPeriodTestFile, visualizationStatFile);
				classifystat.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("exception when statistic classify result used for visualization");
			}
		}
		resultWriter.closeFileWriter();
		logger.info("algorithm stop");
		return true;
	}

	/**
	 * after classifier classify test data write classify result evaluation to
	 * statistic file
	 * 
	 * @param periodNo
	 * @param curIteration
	 * @param classifier
	 * @param classifyResultEvaluation
	 * @param resultWriter
	 * @return
	 */
	public boolean writeClassifyResultEvaluation(int periodNo,
			int curIteration, SMOClassifier classifier,
			ClassifyResultEvaluation classifyResultEvaluation,
			AppendContent2File resultWriter) {
		resultWriter.appendContent2File("period " + periodNo + "\t"
				+ "iteration " + curIteration + "\t" + "classifier "
				+ classifier.getClassifiername() + "\t"
				+ "number of train data " + classifier.getNumTrainData() + "\t"
				+ "number of test data "
				+ classifyResultEvaluation.getNumTestData());

		// calculate average precision
		double numClassifyPositive = (classifyResultEvaluation.numTruePositive
				+ classifyResultEvaluation.numFalsePositiveNeutral + classifyResultEvaluation.numFalsePositiveNegative);
		double numClassifyNeutral = (classifyResultEvaluation.numFalseNeutralPositive
				+ classifyResultEvaluation.numTrueNeutral + classifyResultEvaluation.numFalseNeutralNegative);
		double numClassifyNegative = (classifyResultEvaluation.numFalseNegativePositive
				+ classifyResultEvaluation.numFalseNegativeNeutral + classifyResultEvaluation.numTrueNegative);

		double avgPrecision = 0.0;
		int numLabelClass = 0;
		if (numClassifyPositive != 0) {
			avgPrecision += classifyResultEvaluation.numTruePositive
					/ numClassifyPositive;
			numLabelClass++;
		}
		if (numClassifyNeutral != 0) {
			avgPrecision += classifyResultEvaluation.numTrueNeutral
					/ numClassifyNeutral;
			numLabelClass++;
		}

		if (numClassifyNegative != 0) {
			avgPrecision += classifyResultEvaluation.numTrueNegative
					/ numClassifyNegative;
			numLabelClass++;
		}
		avgPrecision /= numLabelClass;

		// calculate average recall
		double numLabelPositive = (classifyResultEvaluation.numTruePositive
				+ classifyResultEvaluation.numFalseNeutralPositive + classifyResultEvaluation.numFalseNegativePositive);
		double numLabelNeutral = (classifyResultEvaluation.numFalsePositiveNeutral
				+ classifyResultEvaluation.numTrueNeutral + classifyResultEvaluation.numFalseNegativeNeutral);
		double numLabelNegative = (classifyResultEvaluation.numFalsePositiveNegative
				+ classifyResultEvaluation.numFalseNeutralNegative + classifyResultEvaluation.numTrueNegative);

		double avgRecall = 0.0;
		numLabelClass = 0;
		if (numLabelPositive != 0) {
			avgRecall += classifyResultEvaluation.numTruePositive
					/ numLabelPositive;
			numLabelClass++;
		}

		if (numLabelNeutral != 0) {
			avgRecall += classifyResultEvaluation.numTrueNeutral
					/ numLabelNeutral;
			numLabelClass++;
		}

		if (numLabelNegative != 0) {
			avgRecall += classifyResultEvaluation.numTrueNegative
					/ numLabelNegative;
			numLabelClass++;
		}

		avgRecall /= numLabelClass;

		resultWriter.appendContent2File("period " + periodNo + "\t"
				+ "iteration " + curIteration + "\t" + "classifier "
				+ classifier.getClassifiername() + "\t" + "classify result: "
				+ "num of test data "
				+ classifyResultEvaluation.getNumTestData() + "\taccuracy "
				+ classifyResultEvaluation.getAccuracy()
				+ "\taverage precision " + Double.toString(avgPrecision)
				+ "\taverage recall " + Double.toString(avgRecall));
		return true;
	}
}
