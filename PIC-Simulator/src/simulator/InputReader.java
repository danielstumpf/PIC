package simulator;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

public class InputReader {

	private ArrayList<String> getInput(Composite parent, ArrayList<String> arrayLinesReadIn) {
		FileDialog fd = new FileDialog(parent.getShell());
		String filename = fd.open();
		System.out.println(filename);
		return arrayLinesReadIn;
	}
}
