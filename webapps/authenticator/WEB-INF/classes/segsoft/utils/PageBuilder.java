package segsoft.utils;

/**
 * 
 * PageBuilder class
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PageBuilder {
	public static String getPage(String file) {
		StringBuilder pageBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String currLine;
			while ((currLine = br.readLine()) != null) {
				pageBuilder.append(currLine).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pageBuilder.toString();
	}
}
