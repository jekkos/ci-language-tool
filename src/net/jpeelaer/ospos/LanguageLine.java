package net.jpeelaer.ospos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import com.google.common.collect.ComparisonChain;

class LanguageLine implements Comparable<LanguageLine> {
	
	/**
	 * 
	 */
	private final CsvLanguageTool csvLanguageTool;

	private String label, fileName;
	
	private Set<Translation> translations = new HashSet<Translation>();

	public LanguageLine(CsvLanguageTool csvLanguageTool, Matcher matcher, String fileName) {
		this.csvLanguageTool = csvLanguageTool;
		this.label =matcher.group(1) + "_" +  matcher.group(2) ;
		this.fileName = fileName.split("\\.")[0];
	}
	
	public void addTranslation(String language, String text) {
		translations.add(new Translation(this.csvLanguageTool, label, language, text));
	}
	
	public Set<Translation> getTranslations() {
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
		return this.csvLanguageTool;
	}

	String[] toCsvLine(List<String> languages) {
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
		return "$lang['" + label + "'] = " + translations.iterator().next() + ";";
	}
}