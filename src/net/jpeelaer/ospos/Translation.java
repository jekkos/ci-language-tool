package net.jpeelaer.ospos;

class Translation {
	/**
	 * 
	 */
	private final CsvLanguageTool csvLanguageTool;
	private String language, text, label;

	public Translation(CsvLanguageTool csvLanguageTool, java.lang.String label, java.lang.String language, java.lang.String text) {
		this.csvLanguageTool = csvLanguageTool;
		this.label = label;
		this.language = language;
		this.text = text;
	}

	public String getLanguage() {
		return language;
	}

	public String getText() {
		return text;
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getOuterType().hashCode();
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
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
		Translation other = (Translation) obj;
		if (!getOuterType().equals(other.getOuterType()))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return text ;
	}

	private CsvLanguageTool getOuterType() {
		return this.csvLanguageTool;
	}

}