package simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;


public class InputReader {

	static ArrayList<String> getInput(Shell shell) {
		// FileDialog öffnen, Input-Datei wählen. 
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		// Filter für src-Dateien setzen
		String[] filters = {"*.lst"};
		fd.setFilterExtensions(filters);
		ArrayList<String> arrayInput = new ArrayList<String>();
		BufferedReader bufferedReader;
		try {
		// FileDialog öffnen und Dateipfad einlesen
		Path filePath = Paths.get(fd.open());
		String singleLine = null;
		// Dateiinhalt zeilenweise in Array abspeichern
			bufferedReader = new BufferedReader(new FileReader(filePath.toString()));
			while ((singleLine = bufferedReader.readLine()) != null){
				arrayInput.add(singleLine);
			}
			bufferedReader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return arrayInput;
	}
}
