package com.ict.sentimentclassify.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class MapSortUtil {
	/**
	 * sort map ascending by value
	 * 
	 * @param sortMap
	 * @return sorted entry from sortedMap
	 */
	public static Map.Entry[] sortAscendByValue(Map sortMap) {
		Set sortEntrySet = sortMap.entrySet();
		Map.Entry[] sortedEntryArray = (Map.Entry[]) sortEntrySet
				.toArray(new Map.Entry[sortEntrySet.size()]);
		Arrays.sort(sortedEntryArray, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Double key1 = Double.valueOf(((Map.Entry) arg0).getValue()
						.toString());
				Double key2 = Double.valueOf(((Map.Entry) arg1).getValue()
						.toString());
				return key1.compareTo(key2);
			}
		});

		return sortedEntryArray;
	}

	/**
	 * sort map descending by value
	 * 
	 * @param sortMap
	 * @return
	 */
	public static Map.Entry[] sortDescendByValue(Map sortMap) {
		Set sortEntrySet = sortMap.entrySet();
		Map.Entry[] sortedEntryArray = (Map.Entry[]) sortEntrySet
				.toArray(new Map.Entry[sortEntrySet.size()]);
		Arrays.sort(sortedEntryArray, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Double key1 = Double.valueOf(((Map.Entry) arg0).getValue()
						.toString());
				Double key2 = Double.valueOf(((Map.Entry) arg1).getValue()
						.toString());
				return key2.compareTo(key1);
			}
		});

		return sortedEntryArray;
	}

	/**
	 * sort map ascending by key
	 * 
	 * @param
	 * @return
	 */
	public static Map.Entry[] getSortedHashtableByKey(Map sortMap) {

		Set sortEntrySet = sortMap.entrySet();

		Map.Entry[] sortedEntryArray = (Map.Entry[]) sortEntrySet
				.toArray(new Map.Entry[sortEntrySet.size()]);

		Arrays.sort(sortedEntryArray, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Object key1 = ((Map.Entry) arg0).getKey();
				Object key2 = ((Map.Entry) arg1).getKey();
				return ((Comparable) key1).compareTo(key2);
			}

		});

		return sortedEntryArray;
	}
}
