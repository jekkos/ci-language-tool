package net.jpeelaer.ospos;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVWriter;



public class CsvLanguageTool {
	

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			String languagePath = args[0];
			String translationFolder = languagePath 
					+ File.separator + ".." 
					+ File.separator + ".." 
					+ File.separator + "translations";
			new CsvLanguageTool().createLanguageLines(languagePath).toCsv(languagePath, translationFolder);
		} else {
			System.out.println("Usage: java -jar ci-language-tool-0.1-SNAPSHOT.jar <languagfilepath>");
		}
	}
	
	private Map<LanguageLine, LanguageLine> lineMap = new TreeMap<LanguageLine, LanguageLine>();
	private List<String> languages = new ArrayList<String>();
	private Pattern pattern = Pattern.compile("\\$lang\\[['\\\"](.*?)_(.*?)['\\\"]\\]\\s*=\\s*['\\\"](.*)['\\\"];.*");

	private CsvLanguageTool createLanguageLines(String languagePath) throws IOException {
		File path = new File(languagePath);
		int translationCount = 0;
		for (File langDir : path.listFiles()) {
			translationCount++;
			String language = langDir.getName();
			if (langDir.isDirectory()) {
				System.out.println("found language " + langDir.getName());
				languages.add(langDir.getName());
				for (File file : langDir.listFiles()) {
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(file));
						String line = "";
						while((line=reader.readLine()) != null) {
							Matcher matcher = pattern.matcher(line);
							if (matcher.matches()) {
								addLanguageLine(language, file, matcher, translationCount);
							}
						}
					} finally {
						reader.close();
						System.out.println("Done reading!!");
					}
				}
			}
		}
		return this;
	}

	private void addLanguageLine(String language, File file, Matcher matcher, int translationCount) {
		LanguageLine languageLine = new LanguageLine(this, matcher, file.getName());
		if (!lineMap.containsKey(languageLine)) {
			lineMap.put(languageLine, languageLine);
		} else {
			languageLine = lineMap.get(languageLine);
		}
		// last translation will be take unique place in translation set
		languageLine.addTranslation(language, matcher.group(3));
		System.out.println(languageLine);
	}
	
	private void toCsv(String languagePath, String translationFolder) throws IOException {
		String fileName = null;
		CSVWriter csvWriter = null;
		try {
			new File(translationFolder).mkdirs();
			for (Entry<LanguageLine, LanguageLine> labelEntry : lineMap.entrySet()) {
				String currentFilename = labelEntry.getValue().getFileName();
				if (!currentFilename.equals(fileName)) {
					if (csvWriter != null) {
						csvWriter.close();
					}
					FileWriter writer = new FileWriter(
						new File(translationFolder, currentFilename + ".csv"));
					csvWriter = new CSVWriter(writer);				
					fileName = currentFilename;
					writeCsvHeaders(languages, csvWriter);
				}
				writeCsvLines(labelEntry.getValue(), csvWriter);
			}
		} finally {
			csvWriter.close();
		}
	}
	
	private void writeCsvHeaders(List<String> languages, CSVWriter csvWriter) {
		LinkedList<String> headers = new LinkedList<String>(languages);
		headers.addFirst("label");
		String[] headerArray = new String[headers.size()];
		csvWriter.writeNext(headers.toArray(headerArray));
	}

	private void writeCsvLines(LanguageLine languageLine,
		CSVWriter csvWriter) throws IOException {
		// create a new file.. should then iterate over possible languages??
		String[] csvLines = languageLine.toCsvLine(languages);
		// write to end of csvOutput
		csvWriter.writeNext(csvLines);
	}

}
