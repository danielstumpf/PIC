package simulator;

import java.math.BigInteger;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class CreateStateRegister {
	private static final int REG_COLUMN_WIDTH = 80;
	static Label valueOfPclLabel;
	static Label valueOfPcLabel;
	static Label valueOfW;
	static String valueOfWHex;
	static Label valueOfFSR;
	static Label laufzeitVal;
	static Label quarzFreqVal;
	static Button setBackRuntime;
	static String currentQuarzMicroSeconds = "1.000";
	static double currentQuarzDouble = 1.000;
	static int psaVal;
	static boolean checkOneOrZero;
	static boolean raActiv = false;

	static HashMap<String, String> quarzFinder = new HashMap<>();
	static String[] array = new String[27];
	static String optionBits[] = new String[] { "1", "1", "1", "1", "1", "1", "1", "1" };
	static String intconBits[] = new String[] { "0", "0", "0", "0", "0", "0", "0", "0" };

	static TableItem statusRegItem;
	static TableItem optionsRegItem;
	static TableItem intconRegItem;
	static Label statusRegHexValue;
	static Label optionRegHexValue;
	static Label intconRegHexValue;

	public static void createAllStates(Composite tabSpecComposite) {

		Label specRegLabel = new Label(tabSpecComposite, SWT.NONE);
		FormData specRegLabelData = new FormData();
		specRegLabelData.top = new FormAttachment(0, 3);
		specRegLabelData.left = new FormAttachment(0, 3);
		specRegLabel.setLayoutData(specRegLabelData);
		specRegLabel.setText("Spezialregister");

		Table specRegTable = new Table(tabSpecComposite, SWT.BORDER);
		specRegTable.setHeaderVisible(true);
		specRegTable.setHeaderVisible(false);
		FormData specTableData = new FormData();
		specTableData.top = new FormAttachment(specRegLabel, 5);
		specTableData.left = new FormAttachment(0, 3);
		specTableData.right = new FormAttachment(18, -3);
		specTableData.bottom = new FormAttachment(50, -3);
		specRegTable.setLayoutData(specTableData);

		TableColumn specNameColumn = new TableColumn(specRegTable, SWT.NONE);
		specNameColumn.setResizable(false);
		specNameColumn.setWidth(130);
		specNameColumn.setText("Bezeichnung");

		TableColumn specValueColumn = new TableColumn(specRegTable, SWT.NONE);
		specValueColumn.setResizable(false);
		specValueColumn.setWidth(150);
		specValueColumn.setText("Wert");

		TableItem itemRegWName = new TableItem(specRegTable, SWT.NONE);
		itemRegWName.setText(0, "W");
		itemRegWName.setText(1, "00" + "(" + "00" + "h)");
		TableItem itemRegFSRName = new TableItem(specRegTable, SWT.NONE);
		itemRegFSRName.setText(0, "FSR");
		itemRegFSRName.setText(1, "00" + "h");
		TableItem itemRegPCLName = new TableItem(specRegTable, SWT.NONE);
		itemRegPCLName.setText(0, "PCL");
		itemRegPCLName.setText(1, "00" + "h");
		TableItem itemRegPCName = new TableItem(specRegTable, SWT.NONE);
		itemRegPCName.setText(0, "PC");
		itemRegPCName.setText(1, "0000");

		// TODO *Status Register (03h, 83h) LABELS
		Label statusRegLabel = new Label(tabSpecComposite, SWT.NONE);
		FormData statusRegLabelData = new FormData();
		statusRegLabelData.top = new FormAttachment(0, 3);
		statusRegLabelData.left = new FormAttachment(specRegTable, 5);
		statusRegLabel.setLayoutData(statusRegLabelData);
		statusRegLabel.setText("Status-Register");

		Table statusRegTable = new Table(tabSpecComposite, SWT.BORDER);
		statusRegTable.setHeaderVisible(true);
		statusRegTable.setLinesVisible(true);

		FormData statusRegData = new FormData();
		statusRegData.top = new FormAttachment(statusRegLabel, 5);
		statusRegData.left = new FormAttachment(specRegTable, 5);
		statusRegData.bottom = new FormAttachment(30);
		statusRegTable.setLayoutData(statusRegData);

		TableColumn statusIRPColumn = new TableColumn(statusRegTable, SWT.NONE);
		statusIRPColumn.setResizable(false);
		statusIRPColumn.setWidth(REG_COLUMN_WIDTH);
		statusIRPColumn.setText("IRP");

		TableColumn statusRP1Column = new TableColumn(statusRegTable, SWT.NONE);
		statusRP1Column.setResizable(false);
		statusRP1Column.setWidth(REG_COLUMN_WIDTH);
		statusRP1Column.setText("RP1");

		TableColumn statusRP0Column = new TableColumn(statusRegTable, SWT.NONE);
		statusRP0Column.setResizable(false);
		statusRP0Column.setWidth(REG_COLUMN_WIDTH);
		statusRP0Column.setText("RP0");

		TableColumn statusT0Column = new TableColumn(statusRegTable, SWT.NONE);
		statusT0Column.setResizable(false);
		statusT0Column.setWidth(REG_COLUMN_WIDTH);
		statusT0Column.setText("T0");

		TableColumn statusPDColumn = new TableColumn(statusRegTable, SWT.NONE);
		statusPDColumn.setResizable(false);
		statusPDColumn.setWidth(REG_COLUMN_WIDTH);
		statusPDColumn.setText("PD");

		TableColumn statusZColumn = new TableColumn(statusRegTable, SWT.NONE);
		statusZColumn.setResizable(false);
		statusZColumn.setWidth(REG_COLUMN_WIDTH);
		statusZColumn.setText("Z");

		TableColumn statusDCColumn = new TableColumn(statusRegTable, SWT.NONE);
		statusDCColumn.setResizable(false);
		statusDCColumn.setWidth(REG_COLUMN_WIDTH);
		statusDCColumn.setText("DC");

		TableColumn statusCColumn = new TableColumn(statusRegTable, SWT.NONE);
		statusCColumn.setResizable(false);
		statusCColumn.setWidth(REG_COLUMN_WIDTH);
		statusCColumn.setText("C");

		statusRegItem = new TableItem(statusRegTable, SWT.NONE);
		for (int i = 0; i < 8; i++) {
			statusRegItem.setText(i, "0");
		}

		statusRegTable.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				TableItem item = statusRegTable.getItems()[0];
				if (item != null) {
					String[] builder = new String[8];
					for (int col = 0; col < statusRegTable.getColumnCount(); col++) {
						builder[col] = item.getText(col);
						Rectangle rect = item.getBounds(col);
						if (rect.contains(pt)) {
							statusRegItem.setText(col, checkOneOrZero(statusRegItem.getText(col)));
						}
					}
					calculateStatus();
				}
			}
		});

		Label optionRegLabel = new Label(tabSpecComposite, SWT.NONE);
		FormData optionRegLabelData = new FormData();
		optionRegLabelData.top = new FormAttachment(statusRegTable, 5);
		optionRegLabelData.left = new FormAttachment(specRegTable, 5);
		optionRegLabel.setLayoutData(optionRegLabelData);
		optionRegLabel.setText("Options-Register");

		Table optionsRegTable = new Table(tabSpecComposite, SWT.BORDER);
		optionsRegTable.setHeaderVisible(true);
		optionsRegTable.setLinesVisible(true);

		FormData optionsRegData = new FormData();
		optionsRegData.top = new FormAttachment(optionRegLabel, 5);
		optionsRegData.left = new FormAttachment(specRegTable, 5);
		optionsRegData.bottom = new FormAttachment(60);
		optionsRegTable.setLayoutData(optionsRegData);

		TableColumn statusRBPColumn = new TableColumn(optionsRegTable, SWT.NONE);
		statusRBPColumn.setResizable(false);
		statusRBPColumn.setWidth(REG_COLUMN_WIDTH);
		statusRBPColumn.setText("RBP");

		TableColumn optionsINTEDGColumn = new TableColumn(optionsRegTable, SWT.NONE);
		optionsINTEDGColumn.setResizable(false);
		optionsINTEDGColumn.setWidth(REG_COLUMN_WIDTH);
		optionsINTEDGColumn.setText("INTEDG");

		TableColumn optionsT0CSColumn = new TableColumn(optionsRegTable, SWT.NONE);
		optionsT0CSColumn.setResizable(false);
		optionsT0CSColumn.setWidth(REG_COLUMN_WIDTH);
		optionsT0CSColumn.setText("T0CS");

		TableColumn optionsT0SEColumn = new TableColumn(optionsRegTable, SWT.NONE);
		optionsT0SEColumn.setResizable(false);
		optionsT0SEColumn.setWidth(REG_COLUMN_WIDTH);
		optionsT0SEColumn.setText("T0SE");

		TableColumn optionsPSAColumn = new TableColumn(optionsRegTable, SWT.NONE);
		optionsPSAColumn.setResizable(false);
		optionsPSAColumn.setWidth(REG_COLUMN_WIDTH);
		optionsPSAColumn.setText("PSA");

		TableColumn optionsPS2Column = new TableColumn(optionsRegTable, SWT.NONE);
		optionsPS2Column.setResizable(false);
		optionsPS2Column.setWidth(REG_COLUMN_WIDTH);
		optionsPS2Column.setText("PS2");

		TableColumn optionsPS1Column = new TableColumn(optionsRegTable, SWT.NONE);
		optionsPS1Column.setResizable(false);
		optionsPS1Column.setWidth(REG_COLUMN_WIDTH);
		optionsPS1Column.setText("PS1");

		TableColumn optionsPS0Column = new TableColumn(optionsRegTable, SWT.NONE);
		optionsPS0Column.setResizable(false);
		optionsPS0Column.setWidth(REG_COLUMN_WIDTH);
		optionsPS0Column.setText("PS0");

		optionsRegItem = new TableItem(optionsRegTable, SWT.NONE);
		for (int i = 0; i < 8; i++) {
			optionsRegItem.setText(i, "1");
		}

		optionsRegTable.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				TableItem item = optionsRegTable.getItems()[0];
				if (item != null) {
					String[] builder = new String[8];
					for (int col = 0; col < optionsRegTable.getColumnCount(); col++) {
						builder[col] = item.getText(col);
						Rectangle rect = item.getBounds(col);
						if (rect.contains(pt)) {
							optionsRegItem.setText(col, checkOneOrZero(optionsRegItem.getText(col)));
						}
					}
					calculateOption();
				}
			}
		});

		Label intconRegLabel = new Label(tabSpecComposite, SWT.NONE);
		FormData intconRegLabelData = new FormData();
		intconRegLabelData.top = new FormAttachment(optionsRegTable, 5);
		intconRegLabelData.left = new FormAttachment(specRegTable, 5);
		intconRegLabel.setLayoutData(intconRegLabelData);
		intconRegLabel.setText("Intcon-Register");

		Table intconRegTable = new Table(tabSpecComposite, SWT.FULL_SELECTION);
		intconRegTable.setHeaderVisible(true);
		intconRegTable.setLinesVisible(true);

		FormData intconRegData = new FormData();
		intconRegData.top = new FormAttachment(intconRegLabel, 5);
		intconRegData.left = new FormAttachment(specRegTable, 5);
		intconRegData.bottom = new FormAttachment(90);
		intconRegTable.setLayoutData(intconRegData);

		TableColumn intconGIEColumn = new TableColumn(intconRegTable, SWT.NONE);
		intconGIEColumn.setResizable(false);
		intconGIEColumn.setWidth(REG_COLUMN_WIDTH);
		intconGIEColumn.setText("GIE");

		TableColumn intconEEIEColumn = new TableColumn(intconRegTable, SWT.NONE);
		intconEEIEColumn.setResizable(false);
		intconEEIEColumn.setWidth(REG_COLUMN_WIDTH);
		intconEEIEColumn.setText("EEIE");

		TableColumn intconT0IEColumn = new TableColumn(intconRegTable, SWT.NONE);
		intconT0IEColumn.setResizable(false);
		intconT0IEColumn.setWidth(REG_COLUMN_WIDTH);
		intconT0IEColumn.setText("T0IE");

		TableColumn intconINTEColumn = new TableColumn(intconRegTable, SWT.NONE);
		intconINTEColumn.setResizable(false);
		intconINTEColumn.setWidth(REG_COLUMN_WIDTH);
		intconINTEColumn.setText("INTE");

		TableColumn intconRBIEColumn = new TableColumn(intconRegTable, SWT.NONE);
		intconRBIEColumn.setResizable(false);
		intconRBIEColumn.setWidth(REG_COLUMN_WIDTH);
		intconRBIEColumn.setText("RBIE");

		TableColumn intconT0IFColumn = new TableColumn(intconRegTable, SWT.NONE);
		intconT0IFColumn.setResizable(false);
		intconT0IFColumn.setWidth(REG_COLUMN_WIDTH);
		intconT0IFColumn.setText("T0IF");

		TableColumn intconINTFColumn = new TableColumn(intconRegTable, SWT.NONE);
		intconINTFColumn.setResizable(false);
		intconINTFColumn.setWidth(REG_COLUMN_WIDTH);
		intconINTFColumn.setText("INTF");

		TableColumn intconRBIFColumn = new TableColumn(intconRegTable, SWT.NONE);
		intconRBIFColumn.setResizable(false);
		intconRBIFColumn.setWidth(REG_COLUMN_WIDTH);
		intconRBIFColumn.setText("RBIF");

		intconRegItem = new TableItem(intconRegTable, SWT.NONE);
		for (int i = 0; i < 8; i++) {
			intconRegItem.setText(i, "0");
		}

		intconRegTable.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				TableItem item = intconRegTable.getItem(pt);
				if (item != null) {
					String[] builder = new String[8];
					for (int col = 0; col < intconRegTable.getColumnCount(); col++) {
						builder[col] = item.getText(col);
						Rectangle rect = item.getBounds(col);
						if (rect.contains(pt)) {
							intconRegItem.setText(col, checkOneOrZero(intconRegItem.getText(col)));
						}
					}
					calculateIntcon();
				}
			}
		});
		statusRegHexValue = new Label(tabSpecComposite, SWT.NONE);
		FormData statusLabelData = new FormData();
		statusLabelData.top = new FormAttachment(0, 3);
		statusLabelData.left = new FormAttachment(statusRegLabel, 10);
		statusLabelData.width = 35;
		statusRegHexValue.setLayoutData(statusLabelData);
		statusRegHexValue.setText("FF");

		optionRegHexValue = new Label(tabSpecComposite, SWT.NONE);
		FormData optionLabelData = new FormData();
		optionLabelData.bottom = new FormAttachment(optionsRegTable, -3);
		optionLabelData.left = new FormAttachment(optionRegLabel, 10);
		optionLabelData.width = 35;
		optionRegHexValue.setLayoutData(optionLabelData);
		optionRegHexValue.setText("18");

		intconRegHexValue = new Label(tabSpecComposite, SWT.NONE);
		FormData intconLabelData = new FormData();
		intconLabelData.bottom = new FormAttachment(intconRegTable, -3);
		intconLabelData.left = new FormAttachment(intconRegLabel, 10);
		intconLabelData.width = 35;
		intconRegHexValue.setLayoutData(intconLabelData);
		intconRegHexValue.setText("00");

		calculateStatus();
	}

	public static String calculateIntcon() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			builder.append(intconRegItem.getText(i));
		}

		String newArray = String.format("%02X", Integer.parseInt(builder.toString(), 2));

		System.out.println(newArray);
		if (newArray.length() == 1) {
			newArray = "0" + newArray;
		}

		FillRegister.table.getItem(1).setText(4, newArray);
		intconRegHexValue.setText(newArray);
		return newArray;
	}

	public static String calculateOption() {
		String[] string = new String[8];
		for (int j = 0; j < 8; j++) {
			string[j] = optionsRegItem.getText(j);
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			builder.append(string[i]);
		}

		String newArray = String.format("%02X", Integer.parseInt(builder.toString(), 2));

		if (newArray.length() == 1) {
			newArray = "0" + newArray;
		}
		FillRegister.table.getItem(16).setText(2, newArray);
		optionRegHexValue.setText(newArray);
		return newArray;
	}

	public static void createStOpIntLabels(String nameOfLabel, Composite givenComp) {
		Label label = new Label(givenComp, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		label.setText(nameOfLabel);
	}

	public static void optionToBinary(String opt) {
		String binaryOption = BigInteger.valueOf(Integer.parseInt(opt, 16)).toString(2);
		while (binaryOption.length() < 8) {
			binaryOption = "0" + binaryOption;
		}
		for (int i = 0; i < optionBits.length; i++) {
			optionBits[i] = binaryOption.charAt(i) + "";
		}
		// hier die einzelnen bits setzen
		for (int i = 0; i < 8; i++) {
			optionsRegItem.setText(i, optionBits[i]);
		}
		// valOption.setText(calculateOption());
		optionRegHexValue.setText(calculateOption());

	}

	public static void intconToBinary(String intc) {
		String binaryIntcon = BigInteger.valueOf(Integer.parseInt(intc, 16)).toString(2);
		while (binaryIntcon.length() < 8) {
			binaryIntcon = "0" + binaryIntcon;
		}
		for (int i = 0; i < optionBits.length; i++) {
			intconBits[i] = binaryIntcon.charAt(i) + "";
		}
		// hier die einzelnen bits setzen
		for (int i = 0; i < 8; i++) {
			intconRegItem.setText(i, intconBits[i]);
		}
		// valIntcon.setText(calculateIntcon());
		intconRegHexValue.setText(calculateIntcon());
	}

	public static void statusToBinary(String stat) {
		String binaryStatus = BigInteger.valueOf(Integer.parseInt(stat, 16)).toString(2);
		while (binaryStatus.length() < 8) {
			binaryStatus = "0" + binaryStatus;
		}
		for (int i = 0; i < 8; i++) {
			statusRegItem.setText(i, binaryStatus.charAt(i) + "");
		}
		//
		// irp.setText(binaryStatus.charAt(0) + "");
		// rp1.setText(binaryStatus.charAt(1) + "");
		// rp0.setText(binaryStatus.charAt(2) + "");
		// t0.setText(binaryStatus.charAt(3) + "");
		// pd.setText(binaryStatus.charAt(4) + "");
		// z.setText(binaryStatus.charAt(5) + "");
		// dc.setText(binaryStatus.charAt(6) + "");
		// c.setText(binaryStatus.charAt(7) + "");

	}

	public static String checkOneOrZero(String valUToCHeck) {
		int valUToInt = Integer.parseInt(valUToCHeck);
		if (valUToInt == 1) {
			valUToInt = 0;
			valUToCHeck = "0";
		} else if (valUToInt == 0) {
			valUToInt = 1;
			valUToCHeck = "1";
		}
		return valUToCHeck;
	}

	public static String calculateStatus() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			builder.append(statusRegItem.getText(i));
		}
		String status = String.format("%02X", Integer.parseInt(builder.toString(), 2));

		FillRegister.table.getItem(0).setText(4, status);
		FillRegister.table.getItem(16).setText(4, status);
		statusRegHexValue.setText(status);
		return status;
	}

	public static void switchStatusSet(int bits) {
		statusRegItem.setText(bits, "1");
	}

	public static void switchStatusClear(int bits) {
		statusRegItem.setText(bits, "0");
	}

	public static void initializeOnSTartOrReset() {
		FillRegister.table.getItem(0).setText(4, "18");
		FillRegister.table.getItem(0).setText(6, "80");
		FillRegister.table.getItem(0).setText(7, "80");
		FillRegister.table.getItem(0).setText(8, "80");
		FillRegister.table.getItem(16).setText(2, "FF");
		FillRegister.table.getItem(16).setText(6, "FF");
		FillRegister.table.getItem(16).setText(7, "FF");
	}

	public static void setStateValuesPclWFSR(String[] registerAllValuesParent) {
		valueOfFSR.setText(registerAllValuesParent[4].toUpperCase() + "h");
		if (Worker.w == 0) {
			valueOfW.setText("00" + "(00h)");
		} else {
			valueOfW.setText(Worker.w + "(" + Worker.wHex.toUpperCase() + "h)");
			valueOfW.update();
		}
		valueOfPclLabel.setText(registerAllValuesParent[2].toUpperCase() + "h");
		valueOfPcLabel.setText("00" + registerAllValuesParent[2].toUpperCase());
	}

	public static void setLaufzeit(double neValue) {
		String testss = Math.round(
				(Double.parseDouble(laufzeitVal.getText().substring(0, laufzeitVal.getText().length() - 2)) + neValue)
						* 100)
				/ 100.0 + "µs";
		laufzeitVal.setText(testss);
	}

	public static void setOrClearOnReset() {
		optionToBinary("FF");
		if (Worker.instructionsString.equals("0000")
				|| Integer.parseInt(valueOfPcLabel.getText().substring(2, 4).toUpperCase(), 16) <= 0) {

		} else {
			setStateValuesPclWFSR(Worker.registerInputArray);
		}
	}

	public static void createRuntimeGroup(Composite rightUp) {
		Group group = new Group(rightUp, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		group.setText("Laufzeit");

		laufzeitVal = new Label(group, SWT.NONE);
		laufzeitVal.setText("0.00" + "µs");
		laufzeitVal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		setBackRuntime = new Button(group, SWT.PUSH);
		setBackRuntime.setText("Zurücksetzen");
		setBackRuntime.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		setBackRuntime.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				laufzeitVal.setText("0.00" + "µs");
				laufzeitVal.update();
			}
		});
	}

	public static void createQuarzFreqGroup(Composite rightUp) {
		Group group = new Group(rightUp, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
		group.setText("Quarzfrequenz");

		createQUarzFrequenz(group);

		// Combo combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		// combo.setLayout(new GridLayout());
		// combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		quarzFreqVal = new Label(group, SWT.NONE);
		quarzFreqVal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		quarzFreqVal.setText("1.000µs");
		quarzSaver();
		setComboInput();
		// combo.setItems(arry);
		// combo.select(8);
		// combo.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		// //eventuell auch über Formel (1/freq * 4) // thema frei wählbar
		// currentQuarzMicroSeconds =
		// quarzFinder.get(combo.getText().substring(0,
		// combo.getText().length()-4));
		// quarzFreqVal.setText(currentQuarzMicroSeconds);
		// currentQuarzDouble = Double.parseDouble(currentQuarzMicroSeconds);
		// }
		// });
	}

	public static void quarzSaver() {
		quarzFinder.put("32.76800", "122.070"); // kHz - µs
		// quarzFinder.put("500.0000, 122); // not valid
		quarzFinder.put("1.000000", "4.000"); // MHz - µs
		quarzFinder.put("2.000000", "2.000");
		quarzFinder.put("2.457600", "1.628");
		quarzFinder.put("3.000000", "1.333");
		quarzFinder.put("3.276800", "1.221");
		quarzFinder.put("3.680000", "1.087");
		quarzFinder.put("3.686411", "1.085");
		quarzFinder.put("4.000000", "1.000");
		quarzFinder.put("4.096000", "0.977");
		quarzFinder.put("4.194304", "0.954");
		quarzFinder.put("4.433619", "0.902");
		quarzFinder.put("4.915200", "0.814");
		quarzFinder.put("5.000000", "0.800");
		quarzFinder.put("6.000000", "0.667");
		quarzFinder.put("6.144000", "0.651");
		quarzFinder.put("6.250000", "0.640");
		quarzFinder.put("6.553600", "0.610");
		quarzFinder.put("8.000000", "0.500");
		quarzFinder.put("10.000000", "0.400");
		quarzFinder.put("12.000000", "0.333");
		quarzFinder.put("16.000000", "0.250");
		quarzFinder.put("20.000000", "0.200");
		quarzFinder.put("24.000000", "0.167");
		quarzFinder.put("32.000000", "0.125");
		quarzFinder.put("40.000000", "0.100");
		quarzFinder.put("80.000000", "50.000"); // MHz - ns

	}

	public static void setComboInput() {
		array[0] = "32.76800" + " kHz";
		array[1] = "1.000000" + " MHz";
		array[2] = "2.000000" + " MHz";
		array[3] = "2.457600" + " MHz";
		array[4] = "3.000000" + " MHz";
		array[5] = "3.276800" + " MHz";
		array[6] = "3.680000" + " MHz";
		array[7] = "3.686411" + " MHz";
		array[8] = "4.000000" + " MHz";
		array[9] = "4.096000" + " MHz";
		array[10] = "4.194304" + " MHz";
		array[11] = "4.433619" + " MHz";
		array[12] = "4.915200" + " MHz";
		array[13] = "5.000000" + " MHz";
		array[14] = "6.000000" + " MHz";
		array[15] = "6.144000" + " MHz";
		array[16] = "6.250000" + " MHz";
		array[17] = "6.553600" + " MHz";
		array[18] = "8.000000" + " MHz";
		array[19] = "10.000000" + " MHz";
		array[20] = "12.000000" + " MHz";
		array[21] = "16.000000" + " MHz";
		array[22] = "20.000000" + " MHz";
		array[23] = "24.000000" + " MHz";
		array[24] = "32.000000" + " MHz";
		array[25] = "40.000000" + " MHz";
		array[26] = "80.000000" + " MHz";
		quarzSaver();
	}

	public static void createQUarzFrequenz(Group group) {
		Text text = new Text(group, SWT.NONE);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		text.setMessage("4,000000");
		text.addListener(SWT.FocusOut | SWT.Traverse, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (text.getText().isEmpty()) {

				} else {
					if (text.getText().contains(",")) {
						text.setText(text.getText().replace(',', '.'));
					}
					currentQuarzMicroSeconds = text.getText();
					double tempDouble = Math.round((1 / Double.parseDouble(text.getText()) * 4) * 1000) / 1000.0;
					quarzFreqVal.setText(tempDouble + "µs");
					currentQuarzDouble = tempDouble;
				}
			}
		});

	}
}
