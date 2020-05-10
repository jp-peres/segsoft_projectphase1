package segsoft.unit;

/**
 * 
 * PageLoaderTest class
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

import static org.junit.Assert.assertNotEquals;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.junit.Test;

public class PageLoaderTest {
	@Test
	public void homePage() {
		StringBuilder pageBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader("default.txt"))) {
			String currLine;
			while ((currLine = br.readLine()) != null) {
				pageBuilder.append(currLine).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertNotEquals(null, pageBuilder.toString());
	}
}
