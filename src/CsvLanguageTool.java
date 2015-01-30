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

import com.google.common.collect.ComparisonChain;



public class CsvLanguageTool {

	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			String languagePath = args[0];
			String translationFolder = languagePath 
					+ File.separator + ".." 
					+ File.separator + ".." 
					+ File.separator + "translations";
			new CsvLanguageTool().createLanguageLines(languagePath).toCsv(languagePath, translationFolder);
		}
	}
	
	private Map<LanguageLine, LanguageLine> lineMap = new TreeMap<LanguageLine, LanguageLine>();
	private List<String> languages = new ArrayList<String>();
	private Pattern pattern = Pattern.compile("\\$lang\\['(.*?)_(.*?)'\\]\\s*=\\s*['\\\"](.*)['\\\"];");

	private CsvLanguageTool createLanguageLines(String languagePath) throws IOException {
		File path = new File(languagePath);
		for (File langDir : path.listFiles()) {
			String language = langDir.getName();
			if (langDir.isDirectory()) {
				languages.add(langDir.getName());
				for (File file : langDir.listFiles()) {
					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new FileReader(file));
						String line = "";
						while((line=reader.readLine()) != null) {
							Matcher matcher = pattern.matcher(line);
							if (matcher.matches()) {
								LanguageLine languageLine = new LanguageLine(matcher, file.getName());
								if (!lineMap.containsKey(languageLine)) {
									lineMap.put(languageLine, languageLine);
								} else {
									languageLine = lineMap.get(languageLine);
								}
								languageLine.addTranslation(language, matcher.group(3));
								System.out.println(languageLine);
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
	
	private class Translation {
		private String language, text;

		public Translation(java.lang.String language, java.lang.String text) {
			super();
			this.language = language;
			this.text = text;
		}

		public String getLanguage() {
			return language;
		}

		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return text ;
		}

	}
	
	private class LanguageLine implements Comparable<LanguageLine> {
		
		private String label, fileName;
		
		private List<Translation> translations = new ArrayList<Translation>();

		public LanguageLine(Matcher matcher, String fileName) {
			this.label =matcher.group(1) + "_" +  matcher.group(2) ;
			this.fileName = fileName.split("\\.")[0];
		}
		
		public void addTranslation(String language, String text) {
			translations.add(new Translation(language, text));
		}
		
		public List<Translation> getTranslations() {
			return translations;
		}

		public String getLabel() {
			return label;
		}

		public String getFileName() {
			return fileName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((fileName == null) ? 0 : fileName.hashCode());
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LanguageLine other = (LanguageLine) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (fileName == null) {
				if (other.fileName != null)
					return false;
			} else if (!fileName.equals(other.fileName))
				return false;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			return true;
		}

		private CsvLanguageTool getOuterType() {
			return CsvLanguageTool.this;
		}

		private String[] toCsvLine(List<String> languages) {
			List<String> csvLines = new ArrayList<String>();
			csvLines.add(getLabel());
			for(String language : languages) {
				boolean textFound = false;
				for (Translation translation : getTranslations()) {
					if (translation.getLanguage().equals(language)) {
						csvLines.add(translation.getText());
						textFound = true;
					}
				}
				if (!textFound) {
					csvLines.add("");
				}
			}
			return csvLines.toArray(new String[csvLines.size()]);
		}
		
		public int compareTo(LanguageLine line) {
			return ComparisonChain.start()
				.compare(getFileName(), line.getFileName())
				.compare(getLabel(), line.getLabel()).result();
		}
		
		public String toString() {
			return "$lang['" + label + "'] = " + translations.get(translations.size() - 1) + ";";
		}
	}

}
