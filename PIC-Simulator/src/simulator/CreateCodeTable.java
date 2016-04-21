package simulator;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CreateCodeTable {
	static Table testTable;
	static ArrayList<Button> getButtoList = new ArrayList<>();
	static Button[] testGetBto = new Button[200];
	static int startValue;

	/**
	 * erstelle den Table für die Code anzeige
	 * @param linksUnten
	 */
	public static void createTable(Composite linksUnten, ArrayList<String> linesReadIn) {
		testTable = new Table(linksUnten, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION );
		testTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		testTable.setHeaderVisible(false);
		TableColumn checkColumn = new TableColumn(testTable, SWT.NONE);
		checkColumn.setText("x");
		checkColumn.setWidth(20);
		TableColumn textColumn = new TableColumn(testTable, SWT.SINGLE);
		textColumn.setWidth(1000);
	}

	public static void inertLinesInCodeTable(ArrayList<String> arrayLinesReadIn) {
		for(int i = 0; i < arrayLinesReadIn.size(); i++) {
			TableItem item = new TableItem(testTable, SWT.NONE);
			TableEditor editor = new TableEditor(testTable);
			Button checkButton = new Button(testTable, SWT.CHECK);
			editor.grabHorizontal = true;
			
			editor.setEditor(checkButton, item, 0);


			getButtoList.add(checkButton);
			String operandRegisterComment = arrayLinesReadIn.get(i);

			if(arrayLinesReadIn.get(i).isEmpty()) {

			} else {
				item.setFont(new org.eclipse.swt.graphics.Font( testTable.getDisplay(), "Courier", 6,SWT.NONE));
				item.setText(1,operandRegisterComment);
				if(arrayLinesReadIn.get(i).startsWith("0000")) {
					startValue = i;
					testTable.setSelection(i);
					testTable.showSelection();
				}
			}
		}	
	}

	public static void breakpointCheck(int i) {
			if(getButtoList.get(i).getSelection() == true) {
				Main.running = false;
			}
	
	}

	public static void nextStepOnClick(String indexOFSelection, ArrayList<String> linesReadIn) {
		for(int i = 0; i < linesReadIn.size(); i++) {
			if(linesReadIn.get(i).substring(0, 4).contains(indexOFSelection)) {
				testTable.setSelection(i);
				breakpointCheck(i);
				break;
			}
		}

	}

}
