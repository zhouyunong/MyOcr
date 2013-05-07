package com.example.myocr.translate;

import java.io.Serializable;
import java.util.ArrayList;

public class TranslateResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private String translation;
	private String docType;
	private int errorCode = -1;
	private String queryString;
	private Basic basicExplain = new Basic();
	private ArrayList<WebTranslation> webTranslation = new ArrayList<WebTranslation>();

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public Basic getBasicExplain() {
		return basicExplain;
	}

	public void setBasicExplain(Basic basicExplain) {
		this.basicExplain = basicExplain;
	}

	public ArrayList<WebTranslation> getWebTranslation() {
		return webTranslation;
	}

	public void setWebTranslation(ArrayList<WebTranslation> webTranslation) {
		this.webTranslation = webTranslation;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	@Override
	public String toString() {
		return "TranslateResult [translation=" + translation + ", docType="
				+ docType + ", errorCode=" + errorCode + ", queryString="
				+ queryString + ", basicExplain=" + basicExplain
				+ ", webTranslation=" + webTranslation.toString() + "]";
	}
}
