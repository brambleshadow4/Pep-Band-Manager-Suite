/**
 * Copyright (c) 2009, Eric M. Heumann
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *    * Neither the name of the Cornell University Big Red Band nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package pepband3.names;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * A utility for determining the sex of someone with a specific first name.
 * 
 * @author Eric M. Heumann
 * @version April 4, 2010
 *
 */
public class CensusNames {
	
	//#############################################################################################//
	//########################## C L A S S   V A R I A B L E S
	//#############################################################################################//

	/*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*/
	/* SEX VALUES
	/*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*/
	
	/**
	 * Constant for when a name is male.
	 */
	public static final Object MALE = "CensusNames.MALE";
	
	/**
	 * Constant for when a name is female.
	 */
	public static final Object FEMALE = "CensusNames.FEMALE";
	
	/**
	 * Constant for when a name has equal chance of being male or female.
	 */
	public static final Object UNCLEAR = "CensusNames.UNCLEAR";
	
	/**
	 * Constant for when a name is not recognized.
	 */
	public static final Object UNKNOWN = "CensusNames.UNKNOWN";

	/*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*/
	/* FILES
	/*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*/
	
	/**
	 * The resource file containing a list of female names in order of popularity.
	 */
	private static final String femaleNamesFile = "female.txt";

	/**
	 * The resource file containing a list of male names in order of popularity.
	 */
	private static final String maleNamesFile = "male.txt";

	/**
	 * The resource file containing a list of surnames in order of popularity.
	 */
	private static final String surnamesFile = "surnames.txt";

	/*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*/
	/* LISTS & MAPS
	/*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*/
	
	/**
	 * A mapping of female first names to their popularity, as loaded from the resource file.
	 */
	private static final Map<String, Integer> femaleNamesMap = new TreeMap<String, Integer>();

	/**
	 * A mapping of male first names to their popularity, as loaded from the resource file.
	 */
	private static final Map<String, Integer> maleNamesMap = new TreeMap<String, Integer>();

	/**
	 * A list of common male names in order of popularity.
	 */
	private static final List<String> maleNames = new ArrayList<String>(1219);

	/**
	 * A list of common female names in order of popularity.
	 */
	private static final List<String> femaleNames = new ArrayList<String>(4280);
	
	/**
	 * A list of common surnames in order of popularity.
	 */
	private static final List<String> surnames = new ArrayList<String>(18840);

	/*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*/
	/* STATE
	/*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*/
	
	/**
	 * Whether or not the first name maps have been loaded.
	 */
	private static boolean mapsLoaded = false;
	
	/**
	 * Whether or not the male first names have been loaded.
	 */
	private static boolean maleLoaded = false;

	/**
	 * Whether or not the female first names have been loaded.
	 */
	private static boolean femaleLoaded = false;
	
	/**
	 * Whether or not the surnames have been loaded.
	 */
	private static boolean surLoaded = false;

	/*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*/
	/* RANDOM
	/*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*//*/
	
	/**
	 * A Random generator.
	 */
	private static final Random random = new Random();
	
	//#############################################################################################//
	//########################## P O P U L A T E
	//#############################################################################################//
	
	/**
	 * Populates the first names in the male and female name maps, using the resource files.
	 */
	private static void populateMaps() {
		try {
			final BufferedReader femaleInput = new BufferedReader(
					new InputStreamReader(
							CensusNames.class.getResourceAsStream(femaleNamesFile)));
			input(femaleInput, femaleNamesMap, 0 , 3);
			final BufferedReader maleInput = new BufferedReader(
					new InputStreamReader(
							CensusNames.class.getResourceAsStream(maleNamesFile)));
			input(maleInput, maleNamesMap, 0 , 3);
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			mapsLoaded = true;
		}
	}
	
	/**
	 * Populates the surnames using the surname resource file.
	 */
	private static void populateMaleNames() {
		try {
			final BufferedReader input = new BufferedReader(
					new InputStreamReader(
							CensusNames.class.getResourceAsStream(maleNamesFile)));
			input(input, maleNames, 0);
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			maleLoaded = true;
		}
	}
	
	/**
	 * Populates the surnames using the surname resource file.
	 */
	private static void populateFemaleNames() {
		try {
			final BufferedReader input = new BufferedReader(
					new InputStreamReader(
							CensusNames.class.getResourceAsStream(femaleNamesFile)));
			input(input, femaleNames, 0);
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			femaleLoaded = true;
		}
	}
	
	/**
	 * Populates the surnames using the surname resource file.
	 */
	private static void populateSurnames() {
		try {
			final BufferedReader input = new BufferedReader(
					new InputStreamReader(
							CensusNames.class.getResourceAsStream(surnamesFile)));
			input(input, surnames, 0);
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			surLoaded = true;
		}
	}
	
	/**
	 * Reads from one of the input files (the reader) and populates the provided map. 
	 * 
	 * @param reader the reader of an input file
	 * @param map the map to populate
	 * @param nameIndex the column index of names
	 * @param rankIndex the column index of rank
	 * @throws IOException thrown if something goes wrong
	 */
	private static void input(
			final BufferedReader reader,
			final Map<String, Integer> map,
			final int nameIndex,
			final int rankIndex) throws IOException {
		final List<String> splitList = new LinkedList<String>();
		while (reader.ready()) {
			final String line = reader.readLine();
			final String[] splitLine = line.split(" ");
			for (final String splitToken : splitLine)
				if (!splitToken.isEmpty())
					splitList.add(splitToken);
			if (splitList.size() >= 4) {
				final String name = splitList.get(nameIndex);
				final Integer rank = Integer.parseInt(splitList.get(rankIndex));
				map.put(name.toLowerCase(), rank);
			}
			splitList.clear();
		}
	}
	
	/**
	 * Reads from one of the input files (the reader) and populates the provided map. 
	 * 
	 * @param reader the reader of an input file
	 * @param list the list to populate
	 * @param nameIndex the column index of names
	 * @throws IOException thrown if something goes wrong
	 */
	private static void input(
			final BufferedReader reader,
			final List<String> list,
			final int nameIndex) throws IOException {
		final List<String> splitList = new LinkedList<String>();
		while (reader.ready()) {
			final String line = reader.readLine();
			final String[] splitLine = line.split(" ");
			for (final String splitToken : splitLine)
				if (!splitToken.isEmpty())
					splitList.add(splitToken);
			if (splitList.size() >= 4) {
				final String name = splitList.get(nameIndex);
				list.add(name.toLowerCase());
			}
			splitList.clear();
		}
	}
	
	//#############################################################################################//
	//########################## S E X
	//#############################################################################################//
	
	/**
	 * Gets the probably sex of the provided name based on US Census data.
	 * 
	 * @param name the name in question
	 * @return the probable sex of a person with the provided name
	 */
	public static Object getSex(final String name) {
		if (!mapsLoaded) {
			populateMaps();
		}
		final String querry = name.trim().toLowerCase();
		final Integer maleRank = maleNamesMap.get(querry);
		final Integer femaleRank = femaleNamesMap.get(querry);
		if (maleRank == null && femaleRank == null) {
			return UNKNOWN;
		} else if (maleRank == null) {
			return FEMALE;
		} else if (femaleRank == null) {
			return MALE;
		} else {
			if (maleRank < femaleRank) {
				return MALE;
			} else if (femaleRank < maleRank) {
				return FEMALE;
			} else {
				return UNCLEAR;
			}
		}
	}
	
	/**
	 * Gets a confidence factor. 100 indicates total assurance. 0 indicates totally unclear.
	 * 
	 * @param name the name in question
	 * @return the confidence in the result of the getSex method
	 */
	public static int getSexConfidence(final String name) {
		if (!mapsLoaded) {
			populateMaps();
		}
		final Integer maleRank = maleNamesMap.get(name.toLowerCase());
		final Integer femaleRank = femaleNamesMap.get(name.toLowerCase());
		if (maleRank == null && femaleRank == null) {
			return 0;
		} else if (maleRank == null) {
			return 100;
		} else if (femaleRank == null) {
			return 100;
		} else {
			final int total = Math.max(maleNamesMap.size(), femaleNamesMap.size());
			final int diff = Math.abs(maleRank - femaleRank);
			return (int) Math.round(100.0 * diff / total);
		}
	}
	
	//#############################################################################################//
	//########################## R A N D O M
	//#############################################################################################//
	
	/**
	 * Gets a random male first name.
	 * Not uniform random, but biased towards more common.
	 * 
	 * @return a random male first name
	 */
	public static String getRandomMale() {
		if (!maleLoaded) {
			populateMaleNames();
		}
		final String name = maleNames.get(getIndex(maleNames.size()));
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * Gets a random female first name.
	 * 
	 * @return a random female first name
	 */
	public static String getRandomFemale() {
		if (!femaleLoaded) {
			populateFemaleNames();
		}
		final String name =  femaleNames.get(getIndex(femaleNames.size()));
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * Gets a random first name (50/50 shot of male or female).
	 * 
	 * @return a random first name
	 */
	public static String getRandomFirst() {
		final int seed = random.nextInt(2);
		if (seed == 0) {
			return getRandomMale();
		} else {
			return getRandomFemale();
		}
	}
	
	/**
	 * Gets a random surname.
	 * 
	 * @return a random surname
	 */
	public static String getRandomSurname() {
		if (!surLoaded) {
			populateSurnames();
		}
		final String name =  surnames.get(getIndex(surnames.size()));
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	/**
	 * Gets an index for an a Collection of the provided size.
	 * 
	 * @param size the size of the Collection
	 * @return a random index, biased normally towards the 0 index
	 */
	protected static int getIndex(final int size) {
		final double seed = Math.abs(random.nextGaussian());
		return (int) Math.min(size - 1, seed * size / 3.5);
	}

}
