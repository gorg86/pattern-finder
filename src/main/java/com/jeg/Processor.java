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

	/**
	 * Lee el contenido de cada URL.
	 */
	public abstract void getContent();

	/**
	 * Extrae la información según el patrón.
	 * 
	 * @param pattern
	 */
	public abstract void extractInfo(String pattern);

	/**
	 * Guarda el resultado de cada URL.
	 */
	public abstract void saveInfo();

	public String getResource() {
		return resource;
	}

}
