package simulator;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Stack {

	private static final int COLUMN_MINIMUM_WIDTH = 400;
	private static Table stackTable;

	public static void createStack(Composite parent) {

		FormData stackTableFormData = new FormData();
		stackTableFormData.bottom = new FormAttachment(100, -5);
		stackTableFormData.left = new FormAttachment(0, 5);
		stackTableFormData.right = new FormAttachment(100, -5);

		stackTable = new Table(parent, SWT.NONE);
		stackTable.setLayoutData(stackTableFormData);
		stackTable.setLinesVisible(true);
		stackTable.setHeaderVisible(false);
		TableColumn col = new TableColumn(stackTable, SWT.NONE);
		col.setWidth(80);
		for (int i = 0; i < 8; i++) {
			TableItem itemStack = new TableItem(stackTable, SWT.NONE);
			itemStack.setText("");
		}
	}

	public static void setStackInput(ArrayList<String> stacksaver) {
		int a = stacksaver.size();
		stackTable.clearAll();

		for (int i = 0; i < stacksaver.size(); i++) {
			if (0 == a) {
				break;
			} else {
				a = a - 1;
				stackTable.getItem(i).setText(0,
						Integer.toHexString(Integer.parseInt(stacksaver.get(a).substring(2, 4), 16) + 1));
			}
		}
	}

	public static void layoutTable(Rectangle bounds) {
		TableColumn[] columns = stackTable.getColumns();
		int width = bounds.width - 20;
		if (COLUMN_MINIMUM_WIDTH > width) {
			width = COLUMN_MINIMUM_WIDTH;
		}
		for (TableColumn tableColumn : columns) {
			tableColumn.setWidth(width);
		}
	}

}
