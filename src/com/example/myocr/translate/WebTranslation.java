package com.example.myocr.translate;

import java.util.ArrayList;

public class WebTranslation {
	private String key;
	private ArrayList<String> values;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ArrayList<String> getValues() {
		return values;
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	public WebTranslation(String key, ArrayList<String> values) {
		super();
		this.key = key;
		this.values = values;
	}
	public WebTranslation() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		for (String item : values) {
			builder.append(item+"; ");
		}
		return builder.toString() ;
	}
	
	
}
