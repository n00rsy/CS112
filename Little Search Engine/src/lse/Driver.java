package lse;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class Driver {
	public static void main (String []args) {
		LittleSearchEngine lse = new LittleSearchEngine();

		try {

			

			lse.makeIndex("docs.txt", "noisewords.txt");



		} catch (Exception e) {
		System.out.println(e.getMessage());
		}
	
		System.out.println(lse.top5search("deep","world"));
	}
	

}
