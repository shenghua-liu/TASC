package com.ict.sentimentclassify.application;

import org.apache.log4j.Logger;

import com.ict.sentimentclassify.classify.AdaptivecotrainClassify;

public class AdaptivecotrainMain {
	public static Logger logger = Logger.getLogger(AdaptivecotrainMain.class
			.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String configxml = "./config.xml";
		AdaptivecotrainClassify AdaptivecotrainClassify = new AdaptivecotrainClassify(
				configxml);
		if (false == AdaptivecotrainClassify.init()) {
			logger.error("Adaptivecotrain initialize error");
			return;
		}
		logger.info("Adaptivecotrain algorithm start");
		if (false == AdaptivecotrainClassify.start()) {
			logger.error("Adaptivecotrain algorithm error");
			return;
		}
		logger.info("Adaptivecotrain algorithm stop");
	}

}
