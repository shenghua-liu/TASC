package com.ict.sentimentclassify.classifyresult;

import java.util.Map;

/**
 * @author lifuxin
 * 
 */
public class ClassifyResultEvaluation {

	private double numClassifyRight;
	private int numTestData;

	/**
	 * numFalsePositiveNeutral means classify result is positive and human label
	 * is neutral
	 */
	public double numTruePositive, numFalsePositiveNeutral,
			numFalsePositiveNegative, numFalseNeutralPositive, numTrueNeutral,
			numFalseNeutralNegative, numFalseNegativePositive,
			numFalseNegativeNeutral, numTrueNegative;
	private Map<Integer, ClassifyResultBasicInfo> mapTestData2ClassifyResult;

	public void setMapTestData2ClassifyResult(
			Map<Integer, ClassifyResultBasicInfo> mapTestData2ClassifyResult) {
		this.mapTestData2ClassifyResult = mapTestData2ClassifyResult;
	}

	public Map<Integer, ClassifyResultBasicInfo> getMapTestData2ClassifyResult() {
		return mapTestData2ClassifyResult;
	}

	ClassifyResultEvaluation(double numTruePositive,
			double numFalsePositiveNeutral, double numFalsePositiveNegative,
			double numFalseNeutralPositive, double numTrueNeutral,
			double numFalseNeutralNegative, double numFalseNegativePositive,
			double numFalseNegativeNeutral, double numTrueNegative) {
		this.numTruePositive = numTruePositive;
		this.numFalsePositiveNeutral = numFalsePositiveNeutral;
		this.numFalsePositiveNegative = numFalsePositiveNegative;

		this.numFalseNeutralPositive = numFalseNeutralPositive;
		this.numTrueNeutral = numTrueNeutral;
		this.numFalseNeutralNegative = numFalseNeutralNegative;

		this.numFalseNegativePositive = numFalseNegativePositive;
		this.numFalseNegativeNeutral = numFalseNegativeNeutral;
		this.numTrueNegative = numTrueNegative;

		mapTestData2ClassifyResult = null;
	}

	public ClassifyResultEvaluation() {
		this.numClassifyRight = 0;
		this.numTestData = 0;

		this.numTruePositive = 0.0;
		this.numFalsePositiveNeutral = 0.0;
		this.numFalsePositiveNegative = 0.0;

		this.numFalseNeutralPositive = 0.0;
		this.numTrueNeutral = 0.0;
		this.numFalseNeutralNegative = 0.0;

		this.numFalseNegativePositive = 0.0;
		this.numFalseNegativeNeutral = 0.0;
		this.numTrueNegative = 0.0;

		mapTestData2ClassifyResult = null;
	}

	public void setNumTestData(int numTestData) {
		this.numTestData = numTestData;
	}

	public int getNumTestData() {
		return this.numTestData;
	}

	public void addClassifyRight() {
		this.numClassifyRight++;
	}

	public double getNumClassifyRight() {
		return this.numClassifyRight;
	}

	public double getAccuracy() {
		return this.numClassifyRight / this.numTestData;
	}

}
