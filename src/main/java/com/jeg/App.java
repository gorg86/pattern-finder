package com.jeg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * @author Jorge Gamba
 *
 */
public class App {

	private final static Logger LOGGER = Logger.getLogger(App.class.getName());

	private final static String FILENAME = "c:\\exercise\\urls.txt";
	private final static int THREADS = 5;

	public static void main(String[] args) {
		long startTime = System.nanoTime();
		if (args.length == 0) {
			LOGGER.warning("A proper name must be  passed as an argument");
			System.exit(1);
		}
		String properName = args[0];
		if (args.length > 1) {
			boolean validUrl = validateUrl(args[1]);
			if (validUrl) {
				writeUrl(args[1]);
			}
		}
		App app = new App();
		final List<String> urls = app.readFile();
		app.process(urls, properName);
		long endTime = System.nanoTime();
		long timeElapsed = (endTime - startTime) / 1000000;
		System.out.println("All tasks completed in " + timeElapsed + " milliseconds.");
	}

	/**
	 * Realiza el procesamiento de las URLs (Lee el contenido de cada URL, extrae la
	 * información según el patrón, y guarda el resultado de cada URL)
	 * 
	 * @param urls
	 * @param properName
	 */
	private void process(List<String> urls, String properName) {
		final ExecutorService pool = Executors.newFixedThreadPool(THREADS);
		final ExecutorCompletionService<Processor> completionService = new ExecutorCompletionService<>(pool);
		for (final String site : urls) {
			completionService.submit(new Callable<Processor>() {
				@Override
				public Processor call() throws Exception {
					Processor url = new Url(site);
					url.getContent();
					url.extractInfo(properName);
					url.saveInfo();
					return url;
				}
			});
		}

		for (String url : urls) {
			try {
				final Future<Processor> future = completionService.take();
				final Processor processor = future.get();
				System.out.println("Task with url " + processor.resource + " has finished");
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		pool.shutdown();
	}

	private List<String> readFile() {
		File file = new File(FILENAME);
		List<String> urls = null;
		try (Scanner scanner = new Scanner(file)) {
			urls = new ArrayList<>();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				urls.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urls;
	}

	private static void writeUrl(String newUrl) {
		Path p = Paths.get(FILENAME);
		try (BufferedWriter writer = Files.newBufferedWriter(p, StandardOpenOption.APPEND)) {
			writer.write("\n" + newUrl);
		} catch (IOException ioe) {
			LOGGER.warning("IOException: " + ioe);
		}
	}

	private static boolean validateUrl(String recurso) {
		boolean validURL = false;
		try {
			URL url = new URL(recurso);
			validURL = true;
		} catch (MalformedURLException e) {
			LOGGER.warning("Malformed URL has occurred: " + e);
		}
		return validURL;
	}
}
