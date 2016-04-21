package simulator;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FillCodeTable {

	private static Table codeTable;
	static ArrayList<Button> getButtoList = new ArrayList<>();
	static Button[] testGetBto = new Button[200];
	static int startValue;

	public static void createTable(Composite parent) {
		codeTable = new Table(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION );
		FormData codeData = new FormData();
		codeData.top = new FormAttachment(0, 3);
		codeData.bottom = new FormAttachment(100, -3);
		codeData.left = new FormAttachment(0, 3);
		codeData.right = new FormAttachment(100, -3);
		codeTable.setLayoutData(codeData);
		codeTable.setHeaderVisible(false);
		TableColumn checkColumn = new TableColumn(codeTable, SWT.NONE);
		checkColumn.setText("x");
		checkColumn.setWidth(30);
		TableColumn textColumn = new TableColumn(codeTable, SWT.SINGLE);
		textColumn.setWidth(1000);
	}

	public static void insertInCodeTable(ArrayList<String> arrayLinesReadIn) {
		for(int i = 0; i < arrayLinesReadIn.size(); i++) {
			TableItem item = new TableItem(codeTable, SWT.NONE);
			TableEditor editor = new TableEditor(codeTable);
			Button checkButton = new Button(codeTable, SWT.CHECK);
			editor.grabHorizontal = true;
			
			editor.setEditor(checkButton, item, 0);


			getButtoList.add(checkButton);
			String operandRegisterComment = arrayLinesReadIn.get(i);

			if(arrayLinesReadIn.get(i).isEmpty()) {

			} else {
				item.setFont(new org.eclipse.swt.graphics.Font(codeTable.getDisplay(), "Courier", 6,SWT.NONE));
				item.setText(1,operandRegisterComment);
				if(arrayLinesReadIn.get(i).startsWith("0000")) {
					startValue = i;
					codeTable.setSelection(i);
					codeTable.showSelection();
				}
			}
		}	
	}

	public static void layoutTable(Rectangle bounds) {
		// TODO Auto-generated method stub
		
	}
	
}
