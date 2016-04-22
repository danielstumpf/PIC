package simulator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class FillRegister {

	static TableViewer viewer;
	static Table table;
	private final static int COLUMNS = 9;
	private final static int ROWS = 32;
	private static final int COLUMN_MINIMUM_WIDTH = 50;

	static void createTable(Composite parent) {

		table = new Table(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		FormData tableFormData = new FormData();
		tableFormData.top = new FormAttachment(0, 3);
		tableFormData.left = new FormAttachment(0, 3);
		tableFormData.right = new FormAttachment(100, -3);
		tableFormData.bottom = new FormAttachment(100, -3);
		table.setLayoutData(tableFormData);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		for (int i = 0; i < COLUMNS; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.NONE);
			tableColumn.setResizable(false);
			if (i != 0) {
				tableColumn.setText("0" + (i - 1));
			}
		}

		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		table.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(index);

					for (int i = 1; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener() {
								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										item.setText(column, text.getText());
										if (item.getText(column).length() == 1) {
											item.setText(column, "0" + item.getText(column));
										}
										// set the input from the register in
										// the registerInputArray
										int itemToDecimal = Integer.parseInt(item.getText(0).toString(), 16);
										itemToDecimal += column - 1;
										System.err.println(itemToDecimal);
										Worker.registerInputArray[itemToDecimal] = item.getText(column).toUpperCase();

										if (itemToDecimal == 3) {
											CreateRegisters.statusRegHexValue.setText(item.getText());
											CreateRegisters.statusToBinary(text.getText());
										}
										if (itemToDecimal == 129) {
											CreateRegisters.optionRegHexValue.setText(text.getText());
											CreateRegisters.optionToBinary(text.getText());
										}
										if (itemToDecimal == 11) {
											CreateRegisters.intconRegHexValue.setText(text.getText());
											CreateRegisters.intconToBinary(text.getText());
										}
										if (itemToDecimal == 133) {
											PortA.binaryOfTrisA(text.getText());
										}
										if (itemToDecimal == 134) {
											PortB.binaryOfTrisB(text.getText());
										}
										if (itemToDecimal == 5) {
											PortA.binaryOfPinA(text.getText());
										}
										if (itemToDecimal == 6) {
											PortB.binaryOfPinB(text.getText());
										}

										text.dispose();
										break;
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											item.setText(column, text.getText());
											if (item.getText(column).length() == 1) {
												item.setText(column, "0" + item.getText(column));
											}
											// set the input from the register
											// in the registerInputArray
											itemToDecimal = Integer.parseInt(item.getText(0).toString(), 16);
											itemToDecimal += column - 1;
											Worker.registerInputArray[itemToDecimal] = item.getText(column)
													.toUpperCase();

											if (itemToDecimal == 3) {
												CreateRegisters.statusRegHexValue.setText(text.getText());
												CreateRegisters.statusToBinary(text.getText());
											}
											if (itemToDecimal == 129) {
												CreateRegisters.optionRegHexValue.setText(text.getText());
												CreateRegisters.optionToBinary(text.getText());
											}
											if (itemToDecimal == 11) {
												CreateRegisters.intconRegHexValue.setText(text.getText());
												CreateRegisters.intconToBinary(text.getText());
											}
											if (itemToDecimal == 133) {
												PortA.binaryOfTrisA(text.getText());
											}
											if (itemToDecimal == 134) {
												PortB.binaryOfTrisB(text.getText());
											}
											if (itemToDecimal == 5) {
												PortA.binaryOfPinA(text.getText());
											}
											if (itemToDecimal == 6) {
												PortB.binaryOfPinB(text.getText());
											}

											// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
										}
										break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, i);
							text.setText(item.getText(i));
							text.selectAll();
							text.setFocus();
							text.setTextLimit(2);

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

		// register 02h --> PCL ::: nehme table.getItemCount /columnCount (siehe
		// selectionAdapter)

		int zeileDez = 0;
		TableItem item = null;
		for (int i = 0; i < ROWS; i++) {
			Device device = Display.getCurrent();
			Color grey = new Color(device, 160, 160, 160);
			item = new TableItem(table, SWT.NONE);

			String zeileHexa = Integer.toHexString(zeileDez);
			if (zeileHexa.length() < 2) {
				zeileHexa = '0' + zeileHexa;
			}
			item.setBackground(0, grey);
			item.setText(0, String.valueOf(zeileHexa).toUpperCase());

			zeileDez = zeileDez + 8;
			setItemInput(table, item);
		}
	}

	public static void setItemInput(Table table, TableItem item) {
		for (int i = 1; i < COLUMNS; i++) {
			item.setText(i, "00");
		}
	}

	public static void layoutTable(Rectangle bounds) {
		TableColumn[] columns = table.getColumns();
		int width = ((bounds.width - (columns.length + 6)) / (columns.length+1));
		if (COLUMN_MINIMUM_WIDTH>width){
			width=COLUMN_MINIMUM_WIDTH;
		}
		for (TableColumn tableColumn : columns) {
			tableColumn.setWidth(width);
		}
	}
	
	public static void generalValueSetterForRegister(int address, String generalValue){
		int colX = address%8;
		int rowY = address/8;

		if(generalValue.length() == 1 ){
			generalValue = "0"+generalValue;
		} 
		table.getItem(rowY).setText(colX+1, generalValue.toUpperCase());
	}
	
	public static void setRegOnReset() {
		generalValueSetterForRegister(0, "00");
		generalValueSetterForRegister(2, "00");
		generalValueSetterForRegister(10, "00");
		generalValueSetterForRegister(11, "00");
		generalValueSetterForRegister(128, "00");
		generalValueSetterForRegister(129, "FF");
		generalValueSetterForRegister(130, "00");
		generalValueSetterForRegister(133, "1F");
		generalValueSetterForRegister(134, "FF");
	}
}
