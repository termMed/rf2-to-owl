package com.termmed.owl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;


public class FileHelper {

	private static final Logger log = Logger.getLogger(FileHelper.class);
	private static XMLConfiguration xmlConfig;

	public static void findAllFiles(File releaseFolder, HashSet< String> hashSimpleRefsetList, String mustHave, String doesntMustHave) {
		String name="";
		if (hashSimpleRefsetList==null){
			hashSimpleRefsetList=new HashSet<String>();

		}
		for (File file:releaseFolder.listFiles()){
			if (file.isDirectory()){
				findAllFiles(file, hashSimpleRefsetList, mustHave, doesntMustHave);
			}else{
				if (!file.isHidden()){
					name=file.getName().toLowerCase();
					if ( mustHave!=null && !name.contains(mustHave.toLowerCase()) ){
						continue;
					}
					if ( doesntMustHave!=null && name.contains(doesntMustHave.toLowerCase()) ){
						continue;
					}
					if (name.endsWith(".txt")){ 
						hashSimpleRefsetList.add(file.getAbsolutePath());
					}
				}
			}
		}

	}
	public static XMLConfiguration getXmlConfig() throws ConfigurationException{
		if (xmlConfig==null){
			File patternFile=new File("config/validation-rules.xml");
			if (!patternFile.exists()){
				log.error("Pattern file 'config/validation-rules.xml' doesn't exist. Actual directory is:" +
						System.getProperty("user.dir"));
				}
			xmlConfig = new XMLConfiguration(patternFile);
		}
		return xmlConfig;
	}
	public static String getFileTypeByHeader(File inputFile) {
		String namePattern =null;
		try {
			Thread currThread = Thread.currentThread();
			if (currThread.isInterrupted()) {
				return null;
			}
			XMLConfiguration xmlConfig=getXmlConfig();
			List<String> namePatterns = new ArrayList<String>();
			Object prop = xmlConfig.getProperty("files.file.fileType");
			if (prop instanceof Collection) {
				namePatterns.addAll((Collection) prop);
			}
			boolean toCheck = false;
			String headerRule = null;
			FileInputStream fis = new FileInputStream(inputFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String header = br.readLine();
			if (header!=null){
				for (int i = 0; i < namePatterns.size(); i++) {
					if (currThread.isInterrupted()) {
						return null;
					}
					headerRule = xmlConfig.getString("files.file(" + i + ").headerRule.regex");
					namePattern = namePatterns.get(i);
						if( header.matches(headerRule)){

							if ((inputFile.getName().toLowerCase().contains("definition") 
								&& namePattern.equals("rf2-descriptions")) 
								|| (inputFile.getName().toLowerCase().contains("description") 
										&& namePattern.equals("rf2-textDefinition"))){
							continue;
						}
						toCheck = true;
						break;
					}
				}
			}
			if (!toCheck) {
				log.info("Header for null pattern:" + header);
				namePattern=null;
				
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println(  "FileAnalizer: " +    e.getMessage());
		} catch (UnsupportedEncodingException e) {
			System.out.println(  "FileAnalizer: " +    e.getMessage());
		} catch (IOException e) {
			System.out.println(  "FileAnalizer: " +    e.getMessage());
		} catch (ConfigurationException e) {
			System.out.println(  "FileAnalizer: " +    e.getMessage());
		}
		return namePattern;
	}


	public static String getFile(File pathFolder,String patternFile,String defaultFolder, String mustHave, String doesntMustHave) throws IOException, Exception{
		if (!pathFolder.exists()){
			pathFolder.mkdirs();
		}

		HashSet<String> files = getFilesFromFolder(pathFolder.getAbsolutePath(), mustHave,  doesntMustHave);
		String previousFile = getFileByHeader(files,patternFile);
		if (previousFile==null && defaultFolder!=null){

			File relFolder=new File(defaultFolder);
			if (!relFolder.exists()){
				relFolder.mkdirs();
			}
			files = getFilesFromFolder(relFolder.getAbsolutePath(), mustHave, doesntMustHave);
			previousFile = getFileByHeader(files,patternFile);
			return previousFile;
		}
		return previousFile;
	}

	private static String getFileByHeader(HashSet<String> files, String patternType) throws IOException, Exception {
		if (files!=null){
			for (String file:files){
				String pattern=getFileTypeByHeader(new File(file));
				if (pattern==null){
					log.info("null pattern for file:" + file);
					continue;
				}
				if (pattern.equals(patternType)){

					return file;
				}
			}
		}
		return null;
	}

	private static HashSet<String> getFilesFromFolder(String folder, String mustHave, String doesntMustHave) throws IOException, Exception {
		HashSet<String> result = new HashSet<String>();
		File dir=new File(folder);
		HashSet<String> files=new HashSet<String>();
		findAllFiles(dir, files, mustHave, doesntMustHave);
		result.addAll(files);
		return result;

	}



}

