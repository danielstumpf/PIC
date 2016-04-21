package simulator;

import java.math.BigInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class PortA {
	static Table table;
	static String[] aforBin = new String[]{"i","i","i","i","i","i","i","i"};
	static String[] aPinforBin = new String[]{"1","0","0","0","0","0","0","0"};

	public static Composite createPortAReg(Composite parent) {
		Composite tableComp = new Composite(parent, SWT.BORDER);
		FormLayout formLayout = new FormLayout();
		tableComp.setLayout(formLayout);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0,3);
		formData.left = new FormAttachment(0,3);
		formData.right = new FormAttachment(100,-3);
		tableComp.setLayoutData(formData);

		table = new Table(tableComp, SWT.NONE);
		FormData tableData = new FormData();
		tableData.top = new FormAttachment(0);
		tableData.left = new FormAttachment(0);
		tableData.right = new FormAttachment(100);
		table.setLayoutData(tableData);
		TableItem itemTris = new TableItem(table, SWT.NONE);
		TableItem itemPin = new TableItem(table, SWT.NONE);
		itemPin.setText(0,"Pin");
		itemTris.setText(0,"Tris");
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		for(int i = 8; i >= 0; i--) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setWidth(50);
			tableColumn.setResizable(false);
			if( i == 8) {
				tableColumn.setText("RA");
				tableColumn.setAlignment(SWT.LEFT);
				tableColumn.setWidth(90);
			}
			if(i != 8) {
				tableColumn.setText((i) +"");
			}		
		}

		// Listener for change of Pin "0" or "1"
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		table.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = 1;
				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(index);
					for (int i = 1; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							if(item.getText(i).equals("1")) {
								item.setText(i,"0");
								aPinforBin[i-1] = "0";
								if(i == 4){
									Timer0.ra4OneToZero = true;
								}
							}else {
								item.setText(i,"1");	
								aPinforBin[i-1] = "1";
								if(i == 4){
									Timer0.ra4ZeroToOne = true;
								}
							}
							calculatePinA();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
		trisInputForTable();
		pinInputForTable();
		return tableComp;
	}

	public static void calculatePinA() {
		String newArray =  Integer.toHexString(Integer.parseInt(aPinforBin[0]+aPinforBin[1]+aPinforBin[2]+aPinforBin[3]+aPinforBin[4]+aPinforBin[5]+aPinforBin[6]+aPinforBin[7],2)).toUpperCase();
		if(newArray.length() == 1) {
			newArray = "0"+newArray;
		}
		FillRegister.table.getItem(0).setText(6,newArray);
		Worker.registerInputArray[5] = newArray;
	}


	//
	public static void binaryOfPinA(String text) {
		String binaryAPin = BigInteger.valueOf(Integer.parseInt(text,16)).toString(2);
		while(binaryAPin.length() < 8) {
			binaryAPin = "0"+binaryAPin;
		}
		for(int i = 0; i < binaryAPin.length(); i++) {
			if(binaryAPin.charAt(i)=='1') {
				aPinforBin[i] = "1";
			} else {
				aPinforBin[i] = "0";
			}
		}
		pinInputForTable();
	}



	//Tris Register save to Binary String
	public static void binaryOfTrisA(String text) {
		String binaryATris = BigInteger.valueOf(Integer.parseInt(text,16)).toString(2);
		while(binaryATris.length() < 8) {
			binaryATris = "0"+binaryATris;
		}
		for(int i = 0; i < binaryATris.length(); i++) {
			if(binaryATris.charAt(i) == '1') {
				aforBin[i] = "i";
			} else {
				aforBin[i] = "o";
			}
		}
		trisInputForTable();
	}


	public static void pinInputForTable() {
		for(int i = 1; i <= 8; i++){
			table.getItem(1).setText(i, aPinforBin[i-1]);
		}
	}

	public static void trisInputForTable() {
		for(int i = 1; i <= 8 ; i++){
			table.getItem(0).setText(i, aforBin[i-1]);
		}
	}
	
	public static void layoutTable(Rectangle bounds) {
		TableColumn[] columns = table.getColumns();
		int width = ((bounds.width - (columns.length + 6)) / (columns.length+1));
		columns[0].setWidth(2*width);
		for (int i = 1; i < columns.length; i++) {
			columns[i].setWidth(width);
		}
	}
}
