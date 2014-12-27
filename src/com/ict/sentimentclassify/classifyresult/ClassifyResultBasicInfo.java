package com.ict.sentimentclassify.classifyresult;

/**
 * classified result information Class
 * 
 * @author lifuxin
 * 
 */
public class ClassifyResultBasicInfo {
	public double[] classifyResultDistribute;
	public double classifyResultLabel; // label classified by classifier
	public double humanLabel;// labeled by human

	public ClassifyResultBasicInfo(double[] classifyResultDistribute,
			double classifyResultLabel, double humanLabel) {
		this.classifyResultDistribute = classifyResultDistribute;
		this.classifyResultLabel = classifyResultLabel;
		this.humanLabel = humanLabel;
	}

	public void setClassifyResultLabel(double classifyResultLabel) {
		this.classifyResultLabel = classifyResultLabel;
	}

	public double getClassifyResultLabel() {
		return this.classifyResultLabel;
	}

	public void setClassifyResultDistribute(double[] classifyResultDistribute) {
		this.classifyResultDistribute = classifyResultDistribute;
	}

	public double[] getClassifyResultDistribute() {
		return this.classifyResultDistribute;
	}

	public double getHumanLabel() {
		return this.humanLabel;
	}

	public void setHumanLabel(double humanLabel) {
		this.humanLabel = humanLabel;
	}
}
