package com.ict.sentimentclassify.classifier;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;

import weka.classifiers.functions.SMO;
import weka.core.Instances;

import com.ict.sentimentclassify.classifyresult.ClassifyResultBasicInfo;
import com.ict.sentimentclassify.classifyresult.ClassifyResultEvaluation;

public class SMOClassifier extends Classifier {
	static Logger logger = Logger.getLogger(SMOClassifier.class.getName());

	private SMO smoClassifier;

	public SMOClassifier(String classifierName, String trainFileName) {
		super(classifierName, trainFileName);
		try {
			this.smoClassifier = new SMO();
			this.smoClassifier.buildClassifier(this.trainInstances);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exception when initialize " + this.classifierName);
		}
	}

	@Override
	public ClassifyResultEvaluation classfy(String testFileName) {
		// TODO Auto-generated method stub
		logger.info("classify test data :\t" + testFileName);
		ClassifyResultEvaluation classifyResultEvaluation = new ClassifyResultEvaluation();
		HashMap<Integer, ClassifyResultBasicInfo> mapTestData2ClassifyResult = new HashMap<Integer, ClassifyResultBasicInfo>();

		Instances testInstances = null;
		File testFile = new File(testFileName);

		try {
			this.arffLoader.setFile(testFile);
			testInstances = this.arffLoader.getDataSet();
			testInstances.setClassIndex(testInstances.numAttributes() - 1);
			classifyResultEvaluation.setNumTestData(testInstances
					.numInstances());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exception when load testFile\t" + testFileName);
			return null;
		}

		logger.info(this.classifierName + " classify testData\t" + testFileName);
		double humanLabel, classifyResultLabel = 0.0f;
		double[] classifyResultDistribute = null;
		for (int i = 0; i < classifyResultEvaluation.getNumTestData(); i++) {
			humanLabel = testInstances.instance(i).classValue();
			try {
				classifyResultLabel = this.smoClassifier
						.classifyInstance(testInstances.instance(i));
				classifyResultDistribute = this.smoClassifier
						.distributionForInstance(testInstances.instance(i));
				mapTestData2ClassifyResult.put(i, new ClassifyResultBasicInfo(
						classifyResultDistribute, classifyResultLabel,
						humanLabel));
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("exception when " + this.classifierName
						+ " classify No" + i + " data from testData "
						+ testFileName);
				return null;
			}

			if (classifyResultLabel == humanLabel)
				classifyResultEvaluation.addClassifyRight();

			if (classifyResultLabel == 0) {
				if (humanLabel == 0)
					classifyResultEvaluation.numTruePositive += 1;
				else if (humanLabel == 1)
					classifyResultEvaluation.numFalsePositiveNeutral += 1;
				else if (humanLabel == 2)
					classifyResultEvaluation.numFalsePositiveNegative += 1;
			} else if (classifyResultLabel == 1) {
				if (humanLabel == 0)
					classifyResultEvaluation.numFalseNeutralPositive += 1;
				else if (humanLabel == 1)
					classifyResultEvaluation.numTrueNeutral += 1;
				else if (humanLabel == 2)
					classifyResultEvaluation.numFalseNeutralNegative += 1;
			} else if (classifyResultLabel == 2) {
				if (humanLabel == 0)
					classifyResultEvaluation.numFalseNegativePositive += 1;
				else if (humanLabel == 1)
					classifyResultEvaluation.numFalseNegativeNeutral += 1;
				else if (humanLabel == 2)
					classifyResultEvaluation.numTrueNegative += 1;
			}
		}
		classifyResultEvaluation
				.setMapTestData2ClassifyResult(mapTestData2ClassifyResult);
		logger.info(this.classifierName
				+ " classify result:\t num of classify right is \t"
				+ classifyResultEvaluation.getNumClassifyRight()
				+ "\t, and accuracy is \t"
				+ classifyResultEvaluation.getAccuracy());
		return classifyResultEvaluation;
	}
}