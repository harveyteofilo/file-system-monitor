package com.hicx.solutions.service.processor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hicx.solutions.model.FileStats;

@Component
public class FileContentsProcessorImpl implements FileContentsProcessor {

	@Override
	public FileStats processContents(String fileName, List<String> fileContents) {
		List<String> words = new ArrayList<String>(0);
		List<Integer> dotsPerLine = new ArrayList<Integer>(0);
		Pattern dotPattern = Pattern.compile("[.]");
		// ADD ALL WORDS TO LIST
		fileContents.stream().forEach(str -> {
			// SPLIT WORDS PER LINE
			words.addAll(Arrays.asList(str.split("\\s+")));
			// COUNT DOTS PER LINE
			dotsPerLine.add(
				Math.toIntExact(dotPattern.matcher(str).results().count()));
		});
		// CREATE FILE STATS
		FileStats fileStats = new FileStats(fileName);
		fileStats.setTotalWords(words.size());
		fileStats.setTotalDots(
			Math.toIntExact(dotsPerLine.stream().mapToInt(Integer::intValue).sum()));
		fileStats.setCommonWords(getMostCommonWords(words));
		return fileStats;
	}
	
	private String[] getMostCommonWords(List<String> words) {
		Set<String> uniqueWords = new HashSet<String>(words);
		List<Entry<String, Integer>> wordInstanceMapping = new ArrayList<Entry<String, Integer>>(0);
		// COUNT INSTANCES OF ALL UNIQUE WORDS FROM LIST OF ALL WORDS
		uniqueWords.stream().forEach(uniqueWord -> {
			wordInstanceMapping.add(
				// MAP EACH UNIQUE WORD TO ITS NUMBER OF INSTANCES
				new AbstractMap.SimpleEntry<String, Integer>(
						uniqueWord, Collections.frequency(words, uniqueWord)));
		});
		// SORT MAPPING OF UNIQUE WORDS AND THEIR INSTANCES, IN DESCENDING ORDER
		Collections.sort(wordInstanceMapping, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		}.reversed());
		// GET MOST COMMONLY USED WORDS
		int topInstance = wordInstanceMapping.size() > 0 ? wordInstanceMapping.get(0).getValue() : 0;
		List<String> mostCommonWords = new ArrayList<String>(0);
		if (topInstance != 0) {
			// GET ALL WORDS THAT HAS SAME NUMBER OF INSTANCES AS THE TOP INSTANCE
			mostCommonWords = wordInstanceMapping.stream()
					.filter(wordInstanceMap -> wordInstanceMap.getValue() == topInstance)
						.map(filteredMap -> filteredMap.getKey())
							.collect(Collectors.toList());
		}
		return mostCommonWords.toArray(new String[mostCommonWords.size()]);
	}
}
