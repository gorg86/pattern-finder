package com.jeg;

import java.util.List;

/**
 * @author Jorge Gamba
 *
 */
public abstract class Processor {
	protected String resource;
	protected String content;
	protected String errorMessage;
	protected List<String> references;

	public abstract void getContent();

	public abstract void extractInfo(String pattern);

	public abstract void saveInfo();

	public String getResource() {
		return resource;
	}

}
