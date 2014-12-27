package com.ict.sentimentclassify.config;

/**
 * configuration information some parameters are from config.php which is used
 * for preprocessing
 * 
 * @author lifuxin
 * @Email: lifuxin1125@gmail.com
 * 
 */
public class ConfigInfo {

	/**
	 * data set
	 */
	private String trainFileDir;
	private String trainFileName;
	private String testFileDir;
	private String testFileName;
	private String classifyStatResultDir;
	private String nonpublicwordFileDir;
	private String nonpublicwordFileName;
	private String tweetsContentFile;

	/**
	 * feature and feature position
	 */
	private int emocValuePosition;
	private int numNegativeWordsPosition;
	private int labelPosition;
	private int numPublicSentimentWord;
	private int numPrivateSentimentWord;
	private int numNontextFeature;

	/**
	 * model parameters
	 */
	private int numPeriod;
	private double classifyResultThreshold;
	private int numTopKTestData2TrainSet;
	private int maxIteration;// 迭代次数
	private int numTopKPrivateWord;

	/**
	 * classify result visualization parameter
	 */
	private int numLayer;

	public String getTrainFileDir() {
		return trainFileDir;
	}

	public void setTrainFileDir(String trainFileDir) {
		this.trainFileDir = trainFileDir;
	}

	public String getTrainFileName() {
		return trainFileName;
	}

	public void setTrainFileName(String trainFileName) {
		this.trainFileName = trainFileName;
	}

	public String getTestFileDir() {
		return testFileDir;
	}

	public void setTestFileDir(String testFileDir) {
		this.testFileDir = testFileDir;
	}

	public String getTestFileName() {
		return testFileName;
	}

	public void setTestFileName(String testFileName) {
		this.testFileName = testFileName;
	}

	public String getClassifyStatResultDir() {
		return classifyStatResultDir;
	}

	public void setClassifyStatResultDir(String classifyStatResultDir) {
		this.classifyStatResultDir = classifyStatResultDir;
	}

	public String getNonpublicwordFileDir() {
		return nonpublicwordFileDir;
	}

	public void setNonpublicwordFileDir(String nonpublicwordFileDir) {
		this.nonpublicwordFileDir = nonpublicwordFileDir;
	}

	public String getNonpublicwordFileName() {
		return nonpublicwordFileName;
	}

	public void setNonpublicwordFileName(String nonpublicwordFileName) {
		this.nonpublicwordFileName = nonpublicwordFileName;
	}

	public String getTweetsContentFile() {
		return tweetsContentFile;
	}

	public void setTweetsContentFile(String tweetsContentFile) {
		this.tweetsContentFile = tweetsContentFile;
	}

	public int getEmocValuePosition() {
		return emocValuePosition;
	}

	public void setEmocValuePosition(int emocValuePosition) {
		this.emocValuePosition = emocValuePosition;
	}

	public int getNumNegativeWordsPosition() {
		return numNegativeWordsPosition;
	}

	public void setNumNegativeWordsPosition(int numNegativeWordsPosition) {
		this.numNegativeWordsPosition = numNegativeWordsPosition;
	}

	public int getLabelPosition() {
		return labelPosition;
	}

	public void setLabelPosition(int labelPosition) {
		this.labelPosition = labelPosition;
	}

	public int getNumPublicSentimentWord() {
		return numPublicSentimentWord;
	}

	public void setNumPublicSentimentWord(int numPublicSentimentWord) {
		this.numPublicSentimentWord = numPublicSentimentWord;
	}

	public int getNumPrivateSentimentWord() {
		return numPrivateSentimentWord;
	}

	public void setNumPrivateSentimentWord(int numPrivateSentimentWord) {
		this.numPrivateSentimentWord = numPrivateSentimentWord;
	}

	public int getNumNontextFeature() {
		return numNontextFeature;
	}

	public void setNumNontextFeature(int numNontextFeature) {
		this.numNontextFeature = numNontextFeature;
	}

	public int getNumPeriod() {
		return numPeriod;
	}

	public void setNumPeriod(int numPeriod) {
		this.numPeriod = numPeriod;
	}

	public double getClassifyResultThreshold() {
		return classifyResultThreshold;
	}

	public void setClassifyResultThreshold(double classifyResultThreshold) {
		this.classifyResultThreshold = classifyResultThreshold;
	}

	public int getNumTopKTestData2TrainSet() {
		return numTopKTestData2TrainSet;
	}

	public void setNumTopKTestData2TrainSet(int numTopKTestData2TrainSet) {
		this.numTopKTestData2TrainSet = numTopKTestData2TrainSet;
	}

	public int getMaxIteration() {
		return maxIteration;
	}

	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}

	public int getNumTopKPrivateWord() {
		return numTopKPrivateWord;
	}

	public void setNumTopKPrivateWord(int numTopKPrivateWord) {
		this.numTopKPrivateWord = numTopKPrivateWord;
	}

	public int getNumLayer() {
		return numLayer;
	}

	public void setNumLayer(int numLayer) {
		this.numLayer = numLayer;
	}

}
