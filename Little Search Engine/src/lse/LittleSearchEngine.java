package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages
 * in which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the
	 * associated value is an array list of all occurrences of the keyword in
	 * documents. The array list is maintained in DESCENDING order of frequencies.
	 */
	HashMap<String, ArrayList<Occurrence>> keywordsIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
		noiseWords = new HashSet<String>(100, 2.0f);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword
	 * occurrences in the document. Uses the getKeyWord method to separate keywords
	 * from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an
	 *         Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/

		HashMap<String, Occurrence> hashmap = new HashMap<String, Occurrence>();
		if (docFile == null) {
			throw new FileNotFoundException();
		}
		// docFile = docFile.replaceAll("\\s+", " ");
		// String[] tokens = docFile.split(" ");
		Scanner sc = new Scanner(new File(docFile));

		while (sc.hasNext()) {
			String token = sc.next();
			//System.out.println(token);
			token = getKeyword(token.replaceAll("\\s+", " "));
			//System.out.println(token);
			if (token != null) {
				if(hashmap.get(token) != null) {
					hashmap.get(token).frequency++;
				} else {
					hashmap.put(token, new Occurrence(docFile,1));
				}
				//System.out.println(o);
			}
		}
		sc.close();
		return hashmap;
	}

	// following line is a placeholder to make the program compile
	// you should modify it as needed when you write your code
	

	

	/**
	 * Merges the keywords for a single document into the master keywordsIndex hash
	 * table. For each keyword, its Occurrence in the current document must be
	 * inserted in the correct place (according to descending order of frequency) in
	 * the same keyword's Occurrence list in the master hash table. This is done by
	 * calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String, Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for (Map.Entry<String, Occurrence> entry : kws.entrySet()) {
			String key = entry.getKey();
			Occurrence value = entry.getValue();

			ArrayList<Occurrence> master = keywordsIndex.get(key);
			if (master == null) {
				ArrayList<Occurrence> a = new ArrayList<Occurrence>();
				a.add(value);
				keywordsIndex.put(key, a);
			} else {
				master.add(value);
				//System.out.println(key+"  "+value+ "  "+master);
				insertLastOccurrence(master);
			}
		}
	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of
	 * any trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!' NO
	 * OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be
	 * stripped So "word!!" will become "word", and "word?!?!" will also become
	 * "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		//System.out.println("PENIS");
		/** COMPLETE THIS METHOD **/
		if (word == null) {
			return null;
		}
		
		int firstLetterPos=0;
		int lastLetterPos=word.length();
		for(int i =0;i<word.length();i++) {
			char c =word.charAt(i);
			if(Character.isLetter(c)) {
				firstLetterPos=i;
				break;
			}
		}
		
		for(int i =word.length()-1;i>=0;i--) {
			char c =word.charAt(i);
			if(Character.isLetter(c)) {
				lastLetterPos=i;
				break;
			}
		}
		
		char[] punctuations = { '.', ',', '?', ':', ';', '!' };
		
		if(!(lastLetterPos+1>word.length())) {
			lastLetterPos++;
		}
		String noPunc=word.substring(0,lastLetterPos);
		//System.out.println(noPunc);
		char[] cs=noPunc.toCharArray();
		for(char c :cs) {
			if(!(Character.isLetter(c))) {
				return null;
			}
		}
		
		int x=0;
		while (x<200) {
		
			for (char p : punctuations) {
				int pIndex = word.indexOf(p);
				if (pIndex >= firstLetterPos) {
					word = word.substring(0, pIndex) + word.substring(pIndex + 1);
				}
			}
			
			x++;
		}
		//System.out.println(word);
		//boolean isWord=true;
		for(int i =0;i<word.length();i++) {
			if(!Character.isLetter(word.charAt(i))) {
				return null;
			}
			
		}
		if ( !noiseWords.contains(word.toLowerCase())) {
			return word.toLowerCase();
		}
		return null;
	}

	private boolean containsPunctuation(String s) {
		char[] punctuations = { '.', ',', '?', ':', ';', '!' };
		for (char p : punctuations) {
			if (s.contains(p + "")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in
	 * the list, based on ordering occurrences on descending frequencies. The
	 * elements 0..n-2 in the list are already in the correct order. Insertion is
	 * done by first finding the correct spot using binary search, then inserting at
	 * that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary
	 *         search process, null if the size of the input list is 1. This
	 *         returned array list is only used to test your code - it is not used
	 *         elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		if (occs.size() <= 1) {
			return null;
		}
		
		Occurrence o = occs.get(occs.size() - 1);
		occs.remove(occs.size() - 1);
		//System.out.println(o+ "  "+occs);
		int oFreq = o.frequency;
		ArrayList<Integer> mids = new ArrayList<Integer>();

		int right = occs.size() - 1;
		int left = 0;
		int insertLocation = -1;
		int mid=0;
/*
		while (right >= left)
		{
			mid = ((right + left) / 2);
			int data = occs.get(mid).frequency;
			mids.add(mid);

			if (data == oFreq) {
				insertLocation=mid;
				break;
			}

			else if (data < oFreq)
			{
				right = mid - 1;
			}

			else if (data > oFreq)
			{
				left = mid + 1;
				if (right <= mid)
					mid = mid + 1;
			}
		}
		*/
		int loc = binarySearch(occs, oFreq, 0, right); 
		//System.out.println(loc+"\n");
		if (loc >= 0) {
			//System.out.println("WJKRKJ");
			occs.add( o);
			loc = -1;
		}

		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return mids;
	}

	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all
	 * keywords, each of which is associated with an array list of Occurrence
	 * objects, arranged in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile       Name of file that has a list of all the document file
	 *                       names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise
	 *                       word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input
	 *                               files on disk
	 */
	
	int binarySearch(ArrayList<Occurrence> occs, int item, int low, int high) 
	{ 
	    if (high <= low) 
	        return (item > occs.get(low).frequency)?  (low + 1): low; 
	  
	    int mid = (low + high)/2; 
	  
	    if(item == occs.get(mid).frequency){
	        return mid+1; 
	    }
	  
	    if(item > occs.get(mid).frequency) {
	        return binarySearch(occs, item, mid+1, high); 
	    }
	    return binarySearch(occs, item, low, mid-1); 
	} 
	
	public void makeIndex(String docsFile, String noiseWordsFile) throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String, Occurrence> kws = loadKeywordsFromDocument(docFile);
			System.out.println(kws);
			System.out.println(kws.size());
			mergeKeywords(kws);
			
			//System.out.println("");
			
		}
		//System.out.println(keywordsIndex);
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2
	 * occurs in that document. Result set is arranged in descending order of
	 * document frequencies.
	 * 
	 * Note that a matching document will only appear once in the result.
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. That is,
	 * if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same
	 * frequency f1, then doc1 will take precedence over doc2 in the result.
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all,
	 * result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in
	 *         descending order of frequencies. The result size is limited to 5
	 *         documents. If there are no matches, returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/

		// ArrayList <String> docs= new ArrayList <String>();
		ArrayList<Occurrence> occs1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> occs2 = keywordsIndex.get(kw2);
		//System.out.println(occs1);
		//System.out.println(occs2);
		ArrayList<String> merged = new ArrayList<String>();
		int i1 = 0;
		int i2 = 0;
		while (i1 < occs1.size() && i2 < occs2.size() && merged.size() <= 5) {

			// if(i1>=occs1.size()||i2>=occs2.size())break;

			Occurrence o1 = occs1.get(i1);
			Occurrence o2 = occs2.get(i2);

			if (o1.frequency > o2.frequency) {
				if (!merged.contains(o1.document))
					merged.add(o1.document);
				i1++;
				continue;
			}
			if (o1.frequency < o2.frequency) {
				if (!merged.contains(o2.document))
					merged.add(o2.document);
				i2++;
				continue;
			}
			if (o1.frequency == o2.frequency) {
				if (!merged.contains(o1.document))
					merged.add(o1.document);
				i1++;
				i2++;
				continue;
			}
		}

		while (merged.size() <= 5 && i1 < occs1.size()) {
			Occurrence o1 = occs1.get(i1);
			if (!merged.contains(o1.document))
				merged.add(o1.document);
			i1++;
		}

		while (merged.size() <= 5 && i2 < occs2.size()) {
			Occurrence o2 = occs2.get(i2);
			if (!merged.contains(o2.document))
				merged.add(o2.document);
			i2++;
		}
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		if (merged.size() > 0) {
			return merged;
		}
		return null;
	}
}
