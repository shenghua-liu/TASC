package com.ict.sentimentclassify.classifier;

import java.io.File;

import org.apache.log4j.Logger;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

import com.ict.sentimentclassify.classifyresult.ClassifyResultEvaluation;

public abstract class Classifier {
	static Logger logger = Logger.getLogger(Classifier.class.getName());

	protected String classifierName;
	protected String trainFileName = "";
	protected File trainFile;
	protected Instances trainInstances = null;
	protected ArffLoader arffLoader;
	protected int numTrainData = 0;

	public Classifier(String classifierName, String trainFileName) {

		try {
			this.classifierName = classifierName;
			this.trainFileName = trainFileName;
			this.trainFile = new File(trainFileName);
			this.arffLoader = new ArffLoader();
			this.arffLoader.setFile(trainFile);
			this.trainInstances = arffLoader.getDataSet();
			this.trainInstances
					.setClassIndex(trainInstances.numAttributes() - 1);
			this.numTrainData = trainInstances.numInstances();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exception when initializate " + this.classifierName);
		}
	}

	public String getClassifiername() {
		return this.classifierName;
	}

	public String getTrainFileName() {
		return this.trainFileName;
	}

	public int getNumTrainData() {
		return this.numTrainData;
	}

	abstract public ClassifyResultEvaluation classfy(String testFileName);

}
