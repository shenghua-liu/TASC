package com.ict.sentimentclassify.config;

import java.io.File;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ConfigReader {
	static Logger logger = Logger.getLogger(ConfigReader.class.getName());

	private String xmlFilePath;

	public ConfigReader(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
	}

	public ConfigInfo ReadConfig() {
		ConfigInfo retConfigInfo = null;
		try {
			File xmlFile = new File(xmlFilePath);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xmlFile);
			retConfigInfo = new ConfigInfo();
			logger.info("now parsing xml file...");

			Element numberlayerElem = (Element) doc
					.selectSingleNode("//numLayer");
			if (null == numberlayerElem) {
				logger.error("no //numberlayer node found in xml");
				return null;
			}
			retConfigInfo.setNumLayer(Integer.parseInt(numberlayerElem
					.getText()));

			Element tweetsContentFileElem = (Element) doc
					.selectSingleNode("//tweetsContentFile");
			if (null == tweetsContentFileElem) {
				logger.error("no //tweetsContentFile node found in xml");
				return null;
			}
			retConfigInfo.setTweetsContentFile(tweetsContentFileElem.getText());

			Element trainfiledirElem = (Element) doc
					.selectSingleNode("//trainFileDir");
			if (null == trainfiledirElem) {
				logger.error("no //trainFileDir node found in xml");
				return null;
			}
			retConfigInfo.setTrainFileDir(trainfiledirElem.getText());

			Element trainFileNameElem = (Element) doc
					.selectSingleNode("//trainFileName");
			if (null == trainFileNameElem) {
				logger.error("no //trainFileName node found in xml");
				return null;
			}
			retConfigInfo.setTrainFileName(trainFileNameElem.getText());

			Element testFileDirElem = (Element) doc
					.selectSingleNode("//testFileDir");
			if (null == testFileDirElem) {
				logger.error("no //testFileDir node found in xml");
				return null;
			}
			retConfigInfo.setTestFileDir(testFileDirElem.getText());

			Element testFileNameElem = (Element) doc
					.selectSingleNode("//testFileName");
			if (null == testFileNameElem) {
				logger.error("no //testFileName node found in xml");
				return null;
			}
			retConfigInfo.setTestFileName(testFileNameElem.getText());

			Element classifyStatResultDirElem = (Element) doc
					.selectSingleNode("//classifyStatResultDir");
			if (null == classifyStatResultDirElem) {
				logger.error("no //classifyStatResultDir node found in xml");
				return null;
			}
			retConfigInfo.setClassifyStatResultDir(classifyStatResultDirElem
					.getText());

			Element nonpublicwordFileDirElem = (Element) doc
					.selectSingleNode("//nonpublicwordFileDir");
			if (null == nonpublicwordFileDirElem) {
				logger.error("no //nonpublicwordFileDir node found in xml");
				return null;
			}
			retConfigInfo.setNonpublicwordFileDir(nonpublicwordFileDirElem
					.getText());

			Element nonpublicwordFileNameElem = (Element) doc
					.selectSingleNode("//nonpublicwordFileName");
			if (null == nonpublicwordFileNameElem) {
				logger.error("no //nonpublicwordFileName node found in xml");
				return null;
			}
			retConfigInfo.setNonpublicwordFileName(nonpublicwordFileNameElem
					.getText());

			Element emocValuePositionElem = (Element) doc
					.selectSingleNode("//emocValuePosition");
			if (null == emocValuePositionElem) {
				logger.error("no //emocValuePosition node found in xml");
				return null;
			}
			retConfigInfo.setEmocValuePosition(Integer
					.parseInt(emocValuePositionElem.getText()));

			Element numNegativeWordsPositionElem = (Element) doc
					.selectSingleNode("//numNegativeWordsPosition");
			if (null == numNegativeWordsPositionElem) {
				logger.error("no //numNegativeWordsPosition node found in xml");
				return null;
			}
			retConfigInfo.setNumNegativeWordsPosition(Integer
					.parseInt(numNegativeWordsPositionElem.getText()));

			Element numPublicSentimentWordElem = (Element) doc
					.selectSingleNode("//numPublicSentimentWord");
			if (null == numPublicSentimentWordElem) {
				logger.error("no //numPublicSentimentWord node found in xml");
				return null;
			}
			retConfigInfo.setNumPublicSentimentWord(Integer
					.parseInt(numPublicSentimentWordElem.getText()));

			Element labelPositionElem = (Element) doc
					.selectSingleNode("//labelPosition");
			if (null == labelPositionElem) {
				logger.error("no //labelPosition node found in xml");
				return null;
			}
			retConfigInfo.setLabelPosition(Integer.parseInt(labelPositionElem
					.getText()));

			Element numPrivateSentimentWordElem = (Element) doc
					.selectSingleNode("//numPrivateSentimentWord");
			if (null == numPrivateSentimentWordElem) {
				logger.error("no //numPrivateSentimentWord node found in xml");
				return null;
			}
			retConfigInfo.setNumPrivateSentimentWord(Integer
					.parseInt(numPrivateSentimentWordElem.getText()));

			Element numPeriodElem = (Element) doc
					.selectSingleNode("//numPeriod");
			if (null == numPeriodElem) {
				logger.error("no //numPeriod node found in xml");
				return null;
			}
			retConfigInfo
					.setNumPeriod(Integer.parseInt(numPeriodElem.getText()));

			Element classifyResultThresholdElem = (Element) doc
					.selectSingleNode("//classifyResultThreshold");
			if (null == classifyResultThresholdElem) {
				logger.error("no //classifyResultThreshold node found in xml");
				return null;
			}
			retConfigInfo.setClassifyResultThreshold(Double
					.parseDouble(classifyResultThresholdElem.getText()));

			Element numTopKTestData2TrainSetElem = (Element) doc
					.selectSingleNode("//numTopKTestData2TrainSet");
			if (null == numTopKTestData2TrainSetElem) {
				logger.error("no //numTopKTestData2TrainSet node found in xml");
				return null;
			}
			retConfigInfo.setNumTopKTestData2TrainSet(Integer
					.parseInt(numTopKTestData2TrainSetElem.getText()));

			Element maxIterationElem = (Element) doc
					.selectSingleNode("//maxIteration");
			if (null == maxIterationElem) {
				logger.error("no //maxIteration node found in xml");
				return null;
			}
			retConfigInfo.setMaxIteration(Integer.parseInt(maxIterationElem
					.getText()));

			Element numTopKPrivateWordElem = (Element) doc
					.selectSingleNode("//numTopKPrivateWord");
			if (null == numTopKPrivateWordElem) {
				logger.error("no //numTopKPrivateWord node found in xml");
				return null;
			}
			retConfigInfo.setNumTopKPrivateWord(Integer
					.parseInt(numTopKPrivateWordElem.getText()));

			Element numNontextFeatureElem = (Element) doc
					.selectSingleNode("//numNontextFeature");
			if (null == numNontextFeatureElem) {
				logger.error("no //numNontextFeature node found in xml");
				return null;
			}
			retConfigInfo.setNumNontextFeature(Integer
					.parseInt(numNontextFeatureElem.getText()));

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception when parsing XML configuration File");
			e.printStackTrace();
			return null;
		}

		logger.info("parsing XML configuration file success");
		return retConfigInfo;
	}
}
