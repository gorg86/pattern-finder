package com.jeg;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/**
 * @author Jorge Gamba
 *
 */
public class Url extends Processor {

	private final static Logger LOGGER = Logger.getLogger(Url.class.getName());
	
	private URL url;

	public Url(String resource) {
		super();
		this.resource = resource;
		try {
			this.url = new URL(resource);
		} catch (MalformedURLException e) {
			errorMessage = e.getMessage();
			LOGGER.warning("Malformed URL has occurred: " + e);			
		}
	}

	@Override
	public void obtenerContenido() {
		if (errorMessage == null) {
			try {
				content = IOUtils.toString(url, StandardCharsets.UTF_8);
				if (content == null || content.length() == 0) {
					resource = resource.replace("http", "https");
					url = new URL(resource);
					content = IOUtils.toString(url, StandardCharsets.UTF_8);
				}
			} catch (IOException e) {
				errorMessage = e.getMessage();				
				LOGGER.warning(resource + " " + e.getMessage());				
			}
		}
	}

	@Override
	public void extractInfo(String pattern) {
		if (errorMessage == null) {
			String contenidoEnMayuscula = content.toUpperCase();
			pattern = pattern.toUpperCase();
			int lastIndexFound = 0;

			references = new ArrayList<>();
			while (lastIndexFound != -1) {

				lastIndexFound = contenidoEnMayuscula.indexOf(pattern, lastIndexFound);
				if (lastIndexFound != -1) {
					int beginIndex = lastIndexFound;
					int endIndex = beginIndex + pattern.length();
					String foundReference = content.substring(beginIndex, endIndex);
					references.add(foundReference);
					lastIndexFound += pattern.length();
				}
			}
		}
	}

	@Override
	public void saveInfo() {
		
		String fileName = url!=null ? url.getHost(): "Error"+resource.hashCode();
		
		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("c://exercise//" + fileName + ".txt"), "utf-8"))) {
			if (errorMessage != null) {
				writer.write(errorMessage + "\n");
			} else {
				for (String reference : references) {
					writer.write(reference + "\n");
				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
