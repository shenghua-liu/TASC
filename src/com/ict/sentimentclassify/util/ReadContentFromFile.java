package com.ict.sentimentclassify.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * read content from file Utility Class
 * 
 * @author lifuxin
 * @email lifuxin1125@gmail.com
 */
public class ReadContentFromFile {
	static Logger logger = Logger
			.getLogger(ReadContentFromFile.class.getName());

	private String fileName = "";
	private FileInputStream fileInputStream;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;

	public ReadContentFromFile(String fileName) {
		try {
			this.fileName = fileName;
			this.fileInputStream = new FileInputStream(this.fileName);
			this.inputStreamReader = new InputStreamReader(this.fileInputStream);
			this.bufferedReader = new BufferedReader(this.inputStreamReader);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception when ReadContentFromFile initialize");
		}
	}

	public String getFileName() {
		return this.fileName;
	}

	public boolean readyToRead() {
		boolean ifReady = false;
		try {
			ifReady = this.bufferedReader.ready();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("exception when test file is ready to read");
		}
		return ifReady;
	}

	public String readLineFromFile() {
		String retstring = "";
		try {
			retstring = this.bufferedReader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exception when read " + this.fileName);
		}
		return retstring;
	}

	public boolean closeFileReader() {
		try {
			this.bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("exception when close" + this.fileName);
			return false;
		}
		return true;
	}
}
