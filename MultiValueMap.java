import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * This class uses nesting of an ArrayList inside of a HashMap inside of another
 * HashMap. Similar to the MultiValueMap from Apache, it allows for a "many-to-one"
 * nature of values:keys. However, because it is nested /twice/, this allows for the 
 * "many-to-one" value:key specification to percolate down. In other words, one word 
 * can have many locations, and one location can have many positions.
 * 
 * @author Colin Lindsay
 */

 public class MultiValueMap<K, V, E> { /* Key, Value, Element | Generic types for easy re-use of code*/
	 private HashMap<K, HashMap<V, ArrayList<E>>> map;
	 
	 /**
	  * Default constructor
	  */
	 public MultiValueMap() {
		 map = new HashMap<K, HashMap<V, ArrayList<E>>>();
	 }
	 
	 /**
	  * Due to the nested nature of the MultiValueMap class, this requires three
	  * parameters to be passed: an initial Key "word", a secondary Key "location"
	  * and a final Value "position". Calling put() will associate a position of a
	  * word inside of a file at "location", which will then associate the location
	  * with the specified word. If no (file) location exists for the word, or if
	  * no positions exists in that location, it will be added to the MultiValueMap.
	  * @param word
	  * @param location
	  */
	 public void put(K word, V location, E position) {
		 HashMap<V, ArrayList<E>> locationMap = map.get(word);
		 
		 if (locationMap == null) {
			 locationMap = new HashMap<V, ArrayList<E>>();
			 map.put(word, locationMap);
		 } 
		 
		 ArrayList<E> positionList = map.get(word).get(location);
		 if (positionList == null) {
			 positionList = new ArrayList<E>();
			 locationMap.put(location, positionList);
		 }
		 locationMap.put(location, positionList);
		 positionList.add(position);	 
	 }
	 
	 /**
	  * Prints the MultiValueMap to a specified destination. The ordering is K, followed by
	  * a listing of each V in K followed by a listing of all E in V. OR:
	  * 
	  * V
	  * "K", E, E, ...
	  * 
	  * Throws an IOException with error dialogue if there is access/permission problems
	  * @param map
	  * @param writeDestination
	  */
	 public void writeMap(MultiValueMap<K, V, E> map, String writeDestination) {
		 try {
				BufferedWriter br = new BufferedWriter(new FileWriter(writeDestination));
				for (K k : map.keyset()) {
					br.write((k + "\n"));
					for (V v : map.getLocations(k))
						/* Clean the brackets that ArrayList places around first and last elements */
						br.write(("\"" + v + "\", " + map.getPositions(k, v).toString().replaceAll("[\\[\\]]", "")) + "\n");
					br.write("\n");
				}
				br.close();
			} catch (IOException e) { /* If we cannot create or write the file, let the user know */
				System.out.println("Unable to create or write to file \".../invertedindex.txt\". Please check that user " +
									"permissions allow for file write access");
			}
	 }
	 
	 /**
	  * Returns the locations of the given Key "word" in the form of a string Set.
	  * Returns null if it does not exist.
	  * @param key
	  * @param index
	  * @return key
	  */
	public Set<V> getLocations(K word) {
		if (!map.containsKey(word))
			 return null;
		else return map.get(word).keySet();
	 }
	
	/**
	 * Returns the positions of the Key "word" in the location supplied in the
	 * form of an ArrayList. This ArrayList will contain all the positions
	 * in of the word in a particular file location passed to it. Returns null if it
	 * does not exist.
	 * @param word
	 * @param location
	 * @return
	 */
	public ArrayList<E> getPositions(Object word, Object location) {
		if (!map.containsKey(word) || !map.get(word).containsKey(location))
			return null;
		else return (ArrayList<E>) map.get(word).get(location);
	}
	 
	 /**
	  * Return a set of all Keys "word" in the MultiValueMap
	  * @return
	  */
	 public Set<K> keyset() {
		return map.keySet();
	 }
	 
	 /**
	  * Returns all results in the MultiValueMap which starts with
	  * the supplied query.
	  * @param query
	  * @return
	  */
	 public ArrayList<Object> partialSearch(String query) {
		 ArrayList<Object> results = new ArrayList<Object>();
		 for (Object o : map.keySet()) {
			 if (o.toString().matches("(?i)^"+query+".*"))
				 results.add(o);
		 }
		 return results;
	 }
	 
	 /**
	  * Returns true if specific Key "word" exists in the map, 
	  * otherwise return false.
	  * @param key
	  * @return
	  */
	 public boolean containsKey(K word) {
		 return map.containsKey(word);
	 }
 }