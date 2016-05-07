package dhbw.sysnprog.pic.view;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class PicSimView {
	private static final int COLUMNS = 9;
	private static final int ROWS = 32;
	private static final int REG_COLUMN_WIDTH = 90;
	private static final int FREQ_MINIMUM = 500;
	private static final int FREQ_MAXIMUM = 10000;
	private Display display;
	private Shell shell;

	public Shell getShell() {
		return shell;
	}

	private MenuItem openFileItem;
	private MenuItem dataSheetItem;
	private Composite left;
	private Composite right;
	private Composite centerUp;
	private Composite centerDown;
	private Button btnReset;
	private Button btnWeiter;
	private Button btnStop;
	private Button btnRun;

	public java.util.List<Integer> breakpointList = new ArrayList<Integer>();
	private Label statusRegHexValue;
	private Label optionRegHexValue;
	private Label intconRegHexValue;
	private Label laufzeitVal;
	private Label stepsVal;
	private Label quarzFreqVal;
	private Table codeTable;
	private Table specRegTable;
	private Table tablePortA;
	private Table tablePortB;
	private Table stackTable;
	private Table tableMem;
	private TableItem itemRegW;
	private TableItem itemRegFSR;
	private TableItem itemRegPCL;
	private TableItem itemRegPC;
	private TableItem statusRegItem;
	private TableItem optionsRegItem;
	private TableItem intconRegItem;
	private TableItem portATrisItem;
	private TableItem portAPinItem;
	private TableItem portBTrisItem;
	private TableItem portBPinItem;
	public java.util.List<String> inputList = new ArrayList<String>();
	private Slider slider;
	private TableEditor editor;
	private List<Button> checkButtonList;

	public PicSimView() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("PIC16F84A-Simulator");
		shell.setMaximized(true);
		shell.setLayout(new FormLayout());
		shell.setLayoutData(new FormData(100, 100));

		// TODO Resize Listener

		// Menü erstellen
		createMenu();

		// Composites erstellen
		createComposites();

		// Speicherabbild erstellen
		createMemory();

		// CodeTable erstellen
		createCodeTable();

		// Buttons erstellen
		createButtons();

		// Register erstellen
		createRegisters();

		// Port A
		createPorts();

		// Quarz Frequenz Group erstellen
		createTiming();

		createStack();

	}

	private void createStack() {
		FormData stackTableFormData = new FormData();
		stackTableFormData.bottom = new FormAttachment(100, -5);
		stackTableFormData.left = new FormAttachment(0, 5);
		stackTableFormData.right = new FormAttachment(100, -5);

		stackTable = new Table(left, SWT.NONE);
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

	private void createPorts() {
		Composite tableComp = new Composite(left, SWT.BORDER);
		FormLayout formLayout = new FormLayout();
		tableComp.setLayout(formLayout);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 3);
		formData.left = new FormAttachment(0, 3);
		formData.right = new FormAttachment(100, -3);
		tableComp.setLayoutData(formData);

		setTablePortA(new Table(tableComp, SWT.NONE));
		FormData tableData = new FormData();
		tableData.top = new FormAttachment(0);
		tableData.left = new FormAttachment(0);
		tableData.right = new FormAttachment(100);
		getTablePortA().setLayoutData(tableData);
		portATrisItem = new TableItem(getTablePortA(), SWT.NONE);
		portAPinItem = new TableItem(getTablePortA(), SWT.NONE);
		portAPinItem.setText(0, "Pin");
		portATrisItem.setText(0, "Tris");
		getTablePortA().setLinesVisible(true);
		getTablePortA().setHeaderVisible(true);

		for (int i = 8; i >= 0; i--) {
			TableColumn tableColumn = new TableColumn(getTablePortA(), SWT.NONE);
			tableColumn.setWidth(50);
			tableColumn.setResizable(false);

			if (i == 8) {
				tableColumn.setText("RA");
				tableColumn.setAlignment(SWT.LEFT);
				tableColumn.setWidth(90);
			}
			if (i != 8) {
				tableColumn.setText((i) + "");
			}
		}

		for (int i = 1; i < 9; i++) {
			portAPinItem.setText(i, "1");
			portATrisItem.setText(i, "i");
		}

		final TableEditor editor = new TableEditor(getTablePortA());
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Port B

		Composite tableCompB = new Composite(left, SWT.BORDER);
		FormLayout formLayoutB = new FormLayout();
		tableCompB.setLayout(formLayoutB);
		FormData formDataB = new FormData();
		formDataB.top = new FormAttachment(tableComp, 3);
		formDataB.left = new FormAttachment(0, 3);
		formDataB.right = new FormAttachment(100, -3);
		tableCompB.setLayoutData(formDataB);

		setTablePortB(new Table(tableCompB, SWT.NONE));
		FormData tableDataB = new FormData();
		tableDataB.top = new FormAttachment(0);
		tableDataB.left = new FormAttachment(0);
		tableDataB.right = new FormAttachment(100);
		getTablePortB().setLayoutData(tableDataB);
		portBTrisItem = new TableItem(getTablePortB(), SWT.NONE);
		portBPinItem = new TableItem(getTablePortB(), SWT.NONE);
		portBPinItem.setText(0, "Pin");
		portBTrisItem.setText(0, "Tris");
		getTablePortB().setLinesVisible(true);
		getTablePortB().setHeaderVisible(true);

		for (int i = 8; i >= 0; i--) {
			TableColumn tableColumn = new TableColumn(getTablePortB(), SWT.NONE);
			tableColumn.setWidth(50);
			tableColumn.setResizable(false);
			if (i == 8) {
				tableColumn.setText("RB");
				tableColumn.setAlignment(SWT.LEFT);
				tableColumn.setWidth(90);
			}
			if (i != 8) {
				tableColumn.setText((i) + "");
			}
		}

		for (int i = 1; i < 9; i++) {
			portBPinItem.setText(i, "1");
			portBTrisItem.setText(i, "i");
		}

		final TableEditor editorB = new TableEditor(getTablePortA());
		editorB.horizontalAlignment = SWT.LEFT;
		editorB.grabHorizontal = true;
	}

	private void createTiming() {
		Group group = new Group(centerDown, SWT.NONE);
		group.setLayout(new FormLayout());
		FormData groupFormData = new FormData();
		groupFormData.top = new FormAttachment(0, 5);
		groupFormData.height = 100;
		groupFormData.width = 250;
		groupFormData.right = new FormAttachment(100, -5);

		group.setLayoutData(groupFormData);
		group.setText("Quarzfrequenz");

		setQuarzFreqVal(new Label(group, SWT.NONE));
		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(30, 3);
		labelFormData.right = new FormAttachment(100, -3);
		labelFormData.top = new FormAttachment(0, 3);
		getQuarzFreqVal().setLayoutData(labelFormData);
		getQuarzFreqVal().setText("4000");

		setSlider(new Slider(group, SWT.HORIZONTAL));
		getSlider().setMaximum(FREQ_MAXIMUM);
		getSlider().setMinimum(FREQ_MINIMUM);
		getSlider().setSelection(4000);
		FormData data = new FormData();
		data.bottom = new FormAttachment(100, -10);
		data.left = new FormAttachment(0, 3);
		data.right = new FormAttachment(100, -3);
		getSlider().setLayoutData(data);

		Group groupRuntime = new Group(centerDown, SWT.NONE);
		groupRuntime.setLayout(new FormLayout());
		FormData groupData = new FormData();
		groupData.top = new FormAttachment(group, 5);
		groupData.width = 250;
		groupData.right = new FormAttachment(100, -5);
		groupRuntime.setLayoutData(groupData);
		groupRuntime.setText("Laufzeit");

		Button setBackRuntime = new Button(groupRuntime, SWT.PUSH);
		setBackRuntime.setText("Zurücksetzen");
		FormData buttonData = new FormData();
		buttonData.bottom = new FormAttachment(100, -3);
		buttonData.left = new FormAttachment(0, 3);
		setBackRuntime.setLayoutData(buttonData);

		// TODO setBackRuntime Listener

		laufzeitVal = new Label(groupRuntime, SWT.NONE);
		FormData labelFormData1 = new FormData();
		labelFormData1.left = new FormAttachment(0, 3);
		labelFormData1.right = new FormAttachment(100, -3);
		labelFormData1.bottom = new FormAttachment(setBackRuntime, -3);
		laufzeitVal.setLayoutData(labelFormData1);
		laufzeitVal.setText("0 ms");

		Group groupSteps = new Group(centerDown, SWT.NONE);
		groupSteps.setLayout(new FormLayout());
		FormData groupStepsData = new FormData();
		groupStepsData.top = new FormAttachment(groupRuntime, 5);
		groupStepsData.width = 250;
		groupStepsData.right = new FormAttachment(100, -5);
		groupSteps.setLayoutData(groupStepsData);
		groupSteps.setText("Schritte");

		stepsVal = new Label(groupSteps, SWT.NONE);
		FormData labelFormData2 = new FormData();
		labelFormData2.left = new FormAttachment(0, 3);
		labelFormData2.right = new FormAttachment(100, -3);
		labelFormData2.bottom = new FormAttachment(100, -3);
		stepsVal.setLayoutData(labelFormData2);
		stepsVal.setText("0");
	}

	private void createRegisters() {
		// Spezialregister, Beschriftung und Tabelle erstellen
		Label specRegLabel = new Label(centerDown, SWT.NONE);
		FormData specRegLabelData = new FormData();
		specRegLabelData.top = new FormAttachment(0, 3);
		specRegLabelData.left = new FormAttachment(0, 3);
		specRegLabel.setLayoutData(specRegLabelData);
		specRegLabel.setText("Spezialregister");

		setSpecRegTable(new Table(centerDown, SWT.BORDER));
		getSpecRegTable().setHeaderVisible(true);
		getSpecRegTable().setHeaderVisible(false);
		FormData specTableData = new FormData();
		specTableData.top = new FormAttachment(specRegLabel, 5);
		specTableData.left = new FormAttachment(0, 3);
		specTableData.right = new FormAttachment(18, -3);
		specTableData.bottom = new FormAttachment(50, -3);
		getSpecRegTable().setLayoutData(specTableData);

		TableColumn specNameColumn = new TableColumn(getSpecRegTable(), SWT.NONE);
		specNameColumn.setResizable(false);
		specNameColumn.setWidth(130);
		specNameColumn.setText("Bezeichnung");

		TableColumn specValueColumn = new TableColumn(getSpecRegTable(), SWT.NONE);
		specValueColumn.setResizable(false);
		specValueColumn.setWidth(150);
		specValueColumn.setText("Wert");

		itemRegW = new TableItem(getSpecRegTable(), SWT.NONE);
		itemRegW.setText(0, "W");
		itemRegW.setText(1, "00");
		itemRegFSR = new TableItem(getSpecRegTable(), SWT.NONE);
		itemRegFSR.setText(0, "FSR");
		itemRegFSR.setText(1, "00");
		itemRegPCL = new TableItem(getSpecRegTable(), SWT.NONE);
		itemRegPCL.setText(0, "PCL");
		itemRegPCL.setText(1, "00");
		itemRegPC = new TableItem(getSpecRegTable(), SWT.NONE);
		itemRegPC.setText(0, "PC");
		itemRegPC.setText(1, "0000");

		// Status-Register, Beschriftung und Tabelle erstellen
		Label statusRegLabel = new Label(centerDown, SWT.NONE);
		FormData statusRegLabelData = new FormData();
		statusRegLabelData.top = new FormAttachment(0, 3);
		statusRegLabelData.left = new FormAttachment(getSpecRegTable(), 5);
		statusRegLabel.setLayoutData(statusRegLabelData);
		statusRegLabel.setText("Status-Register");

		Table statusRegTable = new Table(centerDown, SWT.BORDER);
		statusRegTable.setHeaderVisible(true);
		statusRegTable.setLinesVisible(true);

		FormData statusRegData = new FormData();
		statusRegData.top = new FormAttachment(statusRegLabel, 5);
		statusRegData.left = new FormAttachment(getSpecRegTable(), 5);
		statusRegData.bottom = new FormAttachment(30);
		statusRegTable.setLayoutData(statusRegData);

		// Tabellenspalte für "IRP-Bit"
		TableColumn statusIRPColumn = new TableColumn(statusRegTable, SWT.NONE);
		statusIRPColumn.setResizable(false);
		statusIRPColumn.setWidth(REG_COLUMN_WIDTH);
		statusIRPColumn.setText("IRP");

		// Tabellenspalte für "RP1-Bit"
		TableColumn statusRP1Column = new TableColumn(statusRegTable, SWT.NONE);
		statusRP1Column.setResizable(false);
		statusRP1Column.setWidth(REG_COLUMN_WIDTH);
		statusRP1Column.setText("RP1");

		// Tabellenspalte für "RP0-Bit"
		TableColumn statusRP0Column = new TableColumn(statusRegTable, SWT.NONE);
		statusRP0Column.setResizable(false);
		statusRP0Column.setWidth(REG_COLUMN_WIDTH);
		statusRP0Column.setText("RP0");

		// Tabellenspalte für "P0-Bit"
		TableColumn statusT0Column = new TableColumn(statusRegTable, SWT.NONE);
		statusT0Column.setResizable(false);
		statusT0Column.setWidth(REG_COLUMN_WIDTH);
		statusT0Column.setText("T0");

		// Tabellenspalte für "PD-Bit"
		TableColumn statusPDColumn = new TableColumn(statusRegTable, SWT.NONE);
		statusPDColumn.setResizable(false);
		statusPDColumn.setWidth(REG_COLUMN_WIDTH);
		statusPDColumn.setText("PD");

		// Tabellenspalte für "Z-Bit"
		TableColumn statusZColumn = new TableColumn(statusRegTable, SWT.NONE);
		statusZColumn.setResizable(false);
		statusZColumn.setWidth(REG_COLUMN_WIDTH);
		statusZColumn.setText("Z");

		// Tabellenspalte für "DC-Bit"
		TableColumn statusDCColumn = new TableColumn(statusRegTable, SWT.NONE);
		statusDCColumn.setResizable(false);
		statusDCColumn.setWidth(REG_COLUMN_WIDTH);
		statusDCColumn.setText("DC");

		// Tabellenspalte für "C-Bit"
		TableColumn statusCColumn = new TableColumn(statusRegTable, SWT.NONE);
		statusCColumn.setResizable(false);
		statusCColumn.setWidth(REG_COLUMN_WIDTH);
		statusCColumn.setText("C");

		statusRegItem = new TableItem(statusRegTable, SWT.NONE);

		for (int i = 0; i < 8; i++) {
			statusRegItem.setText(i, "0");
		}
		statusRegItem.setText(3, "1");
		statusRegItem.setText(4, "1");

		// TODO StatusRegTable Listener

		Label optionRegLabel = new Label(centerDown, SWT.NONE);
		FormData optionRegLabelData = new FormData();
		optionRegLabelData.top = new FormAttachment(statusRegTable, 5);
		optionRegLabelData.left = new FormAttachment(getSpecRegTable(), 5);
		optionRegLabel.setLayoutData(optionRegLabelData);
		optionRegLabel.setText("Options-Register");

		Table optionsRegTable = new Table(centerDown, SWT.BORDER);
		optionsRegTable.setHeaderVisible(true);
		optionsRegTable.setLinesVisible(true);

		FormData optionsRegData = new FormData();
		optionsRegData.top = new FormAttachment(optionRegLabel, 5);
		optionsRegData.left = new FormAttachment(getSpecRegTable(), 5);
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

		// TODO optionsRegTable Listener

		Label intconRegLabel = new Label(centerDown, SWT.NONE);
		FormData intconRegLabelData = new FormData();
		intconRegLabelData.top = new FormAttachment(optionsRegTable, 5);
		intconRegLabelData.left = new FormAttachment(getSpecRegTable(), 5);
		intconRegLabel.setLayoutData(intconRegLabelData);
		intconRegLabel.setText("Intcon-Register");

		Table intconRegTable = new Table(centerDown, SWT.FULL_SELECTION);
		intconRegTable.setHeaderVisible(true);
		intconRegTable.setLinesVisible(true);

		FormData intconRegData = new FormData();
		intconRegData.top = new FormAttachment(intconRegLabel, 5);
		intconRegData.left = new FormAttachment(getSpecRegTable(), 5);
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

		statusRegHexValue = new Label(centerDown, SWT.NONE);
		FormData statusLabelData = new FormData();
		statusLabelData.top = new FormAttachment(0, 3);
		statusLabelData.left = new FormAttachment(statusRegLabel, 10);
		statusLabelData.width = 35;
		statusRegHexValue.setLayoutData(statusLabelData);

		optionRegHexValue = new Label(centerDown, SWT.NONE);
		FormData optionLabelData = new FormData();
		optionLabelData.bottom = new FormAttachment(optionsRegTable, -3);
		optionLabelData.left = new FormAttachment(optionRegLabel, 10);
		optionLabelData.width = 35;
		optionRegHexValue.setLayoutData(optionLabelData);

		intconRegHexValue = new Label(centerDown, SWT.NONE);
		FormData intconLabelData = new FormData();
		intconLabelData.bottom = new FormAttachment(intconRegTable, -3);
		intconLabelData.left = new FormAttachment(intconRegLabel, 10);
		intconLabelData.width = 35;
		intconRegHexValue.setLayoutData(intconLabelData);
	}

	private void createButtons() {
		Composite buttonComposite = new Composite(centerDown, SWT.NONE);
		buttonComposite.setLayout(new FormLayout());
		FormData buttonCompData = new FormData();
		buttonCompData.bottom = new FormAttachment(100, -5);
		buttonCompData.left = new FormAttachment(0, 5);
		buttonCompData.right = new FormAttachment(15, -3);
		buttonCompData.top = new FormAttachment(60, 5);
		buttonComposite.setLayoutData(buttonCompData);

		btnReset = new Button(buttonComposite, SWT.PUSH);
		btnReset.setText("Reset");
		FormData resButtonData = new FormData();
		resButtonData.top = new FormAttachment(0, 3);
		resButtonData.left = new FormAttachment(0, 3);
		resButtonData.width = 100;
		btnReset.setLayoutData(resButtonData);

		btnWeiter = new Button(buttonComposite, SWT.PUSH);
		btnWeiter.setText("Step");
		FormData nxtStepBtnData = new FormData();
		nxtStepBtnData.top = new FormAttachment(btnReset, 3);
		nxtStepBtnData.left = new FormAttachment(0, 3);
		nxtStepBtnData.width = 100;
		btnWeiter.setLayoutData(nxtStepBtnData);

		btnStop = new Button(buttonComposite, SWT.PUSH);
		btnStop.setText("Pause");
		FormData stopBtnData = new FormData();
		stopBtnData.top = new FormAttachment(btnWeiter, 3);
		stopBtnData.left = new FormAttachment(0, 3);
		stopBtnData.width = 100;
		btnStop.setLayoutData(stopBtnData);

		btnRun = new Button(buttonComposite, SWT.PUSH);
		btnRun.setText("Run");
		FormData runBtnData = new FormData();
		runBtnData.top = new FormAttachment(btnStop, 3);
		runBtnData.left = new FormAttachment(0, 3);
		runBtnData.width = 100;
		btnRun.setLayoutData(runBtnData);
	}

	private void createCodeTable() {
		codeTable = new Table(centerUp, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
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
		checkButtonList = new ArrayList<Button>();
	}

	private void createMemory() {
		setTableMem(new Table(right, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION));
		FormData tableFormData = new FormData();
		tableFormData.top = new FormAttachment(0, 3);
		tableFormData.left = new FormAttachment(0, 3);
		tableFormData.right = new FormAttachment(100, -3);
		tableFormData.bottom = new FormAttachment(100, -3);
		getTableMem().setLayoutData(tableFormData);
		getTableMem().setLinesVisible(true);
		getTableMem().setHeaderVisible(true);

		for (int i = 0; i < COLUMNS; i++) {
			TableColumn tableColumn = new TableColumn(getTableMem(), SWT.NONE);
			tableColumn.setWidth(70);
			tableColumn.setResizable(false);
			if (i != 0) {
				tableColumn.setText("0" + (i - 1));
			}
		}

		setEditor(new TableEditor(getTableMem()));
		getEditor().horizontalAlignment = SWT.LEFT;
		getEditor().grabHorizontal = true;

		// TODO Listener für Zellen

		int zeileDez = 0;
		TableItem item = null;
		for (int i = 0; i < ROWS; i++) {
			Device device = Display.getCurrent();
			Color grey = new Color(device, 160, 160, 160);
			item = new TableItem(getTableMem(), SWT.NONE);

			String zeileHexa = Integer.toHexString(zeileDez);
			if (zeileHexa.length() < 2) {
				zeileHexa = '0' + zeileHexa;
			}
			item.setBackground(0, grey);
			item.setText(0, String.valueOf(zeileHexa).toUpperCase());

			zeileDez = zeileDez + 8;
			// setItemInput(item);
		}
	}

	public static void setItemInput(TableItem item) {
		for (int i = 1; i < COLUMNS; i++) {
			item.setText(i, "00");
		}
	}

	private void createMenu() {
		// Datei-Menüpunkt erstellen
		Menu menu = CreateItem.getMenu(shell, SWT.BAR | SWT.LEFT_TO_RIGHT);
		shell.setMenuBar(menu);

		MenuItem fileItemMenu = CreateItem.getMenuItem(menu, SWT.CASCADE, "Datei");
		Menu fileMenu = CreateItem.getMenu(shell, SWT.DROP_DOWN);
		fileItemMenu.setMenu(fileMenu);
		openFileItem = CreateItem.getMenuItem(fileMenu, SWT.PUSH, "Öffnen");

		// Optionen Menüpunkt erstellen
		MenuItem menuOptionsItem = CreateItem.getMenuItem(menu, SWT.CASCADE, "Optionen");
		Menu optionsMenu = CreateItem.getMenu(shell, SWT.DROP_DOWN);
		menuOptionsItem.setMenu(optionsMenu);
		dataSheetItem = CreateItem.getMenuItem(optionsMenu, SWT.PUSH, "Datenblatt öffnen..");

		// TODO Listener
		dataSheetItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Datenblatt anzeigen
				Path path = Paths.get("resources", "datasheet_16f84.pdf");
				Program.launch(path.toString());
			}
		});
	}

	private void createComposites() {
		// Linkes Drittel Composite
		left = new Composite(shell, SWT.BORDER);
		// left.setBackground(new Color(Display.getCurrent(), new RGB(100, 100,
		// 100)));
		left.setLayout(new FormLayout());

		FormData leftCompformData = new FormData();
		leftCompformData.top = new FormAttachment(0, 3);
		leftCompformData.left = new FormAttachment(0, 5);
		leftCompformData.right = new FormAttachment(20, -5);
		leftCompformData.bottom = new FormAttachment(100, -5);
		left.setLayoutData(leftCompformData);

		// Composite rechtes Drittel
		right = new Composite(shell, SWT.BORDER);
		right.setLayout(new FormLayout());
		FormData rightCompFormData = new FormData();
		rightCompFormData.top = new FormAttachment(0, 3);
		rightCompFormData.bottom = new FormAttachment(100, -5);
		rightCompFormData.left = new FormAttachment(80, 5);
		rightCompFormData.right = new FormAttachment(100, -5);
		right.setLayoutData(rightCompFormData);
		// right.setBackground(new Color(Display.getCurrent(), new RGB(0, 0,
		// 200)));

		// Composite Mitte Oben
		centerUp = new Composite(shell, SWT.BORDER);
		centerUp.setLayout(new FormLayout());
		FormData centerUpCompformData = new FormData();
		centerUpCompformData.top = new FormAttachment(0, 3);
		centerUpCompformData.left = new FormAttachment(left);
		centerUpCompformData.right = new FormAttachment(right);
		centerUpCompformData.bottom = new FormAttachment(65, -3);
		centerUp.setLayoutData(centerUpCompformData);

		// Composite Mitte Unten
		centerDown = new Composite(shell, SWT.BORDER);
		centerDown.setLayout(new FormLayout());
		FormData centerDownCompFormData = new FormData();
		centerDownCompFormData.top = new FormAttachment(centerUp);
		centerDownCompFormData.left = new FormAttachment(left);
		centerDownCompFormData.right = new FormAttachment(right);
		centerDownCompFormData.bottom = new FormAttachment(100, -5);
		centerDown.setLayoutData(centerDownCompFormData);
		// centerDown.setBackground(new Color(Display.getCurrent(), new RGB(255,
		// 0, 0)));
	}

	public int getSelectedLineIndex() {
		return codeTable.getSelectionIndex();
	}

	public void selectCode(int index) {
		codeTable.setSelection(index);
	}

	public void setWvalue(String s) {
		itemRegW.setText(1, s);
	}

	public void setStatusValue(int s) {
		statusRegHexValue.setText(String.format("%02X", s));
		// C-Bit
		if ((s & 0b00000001) == 1) {
			statusRegItem.setText(7, "1");
		} else {
			statusRegItem.setText(7, "0");
		}
		// DC-Bit
		if ((s & 0b00000010) == 2) {
			statusRegItem.setText(6, "1");
		} else {
			statusRegItem.setText(6, "0");
		}
		// Z-Bit
		if ((s & 0b00000100) == 4) {
			statusRegItem.setText(5, "1");
		} else {
			statusRegItem.setText(5, "0");
		}
		// PD-Bit
		if ((s & 0b00001000) == 8) {
			statusRegItem.setText(4, "1");
		} else {
			statusRegItem.setText(4, "0");
		}
		// T0-Bit
		if ((s & 0b00010000) == 16) {
			statusRegItem.setText(3, "1");
		} else {
			statusRegItem.setText(3, "0");
		}
		// RP0-Bit
		if ((s & 0b00100000) == 32) {
			statusRegItem.setText(2, "1");
		} else {
			statusRegItem.setText(2, "0");
		}
		// RP1-Bit
		if ((s & 0b01000000) == 64) {
			statusRegItem.setText(1, "1");
		} else {
			statusRegItem.setText(1, "0");
		}
		// IRP-Bit
		if ((s & 0b10000000) == 128) {
			statusRegItem.setText(0, "1");
		} else {
			statusRegItem.setText(0, "0");
		}
	}

	public void setOptionValue(int s) {
		optionRegHexValue.setText(String.format("%02X", s));
		if ((s & 0b00000001) == 1) {
			optionsRegItem.setText(7, "1");
		} else {
			optionsRegItem.setText(7, "0");
		}
		if ((s & 0b00000010) == 2) {
			optionsRegItem.setText(6, "1");
		} else {
			optionsRegItem.setText(6, "0");
		}
		if ((s & 0b00000100) == 4) {
			optionsRegItem.setText(5, "1");
		} else {
			optionsRegItem.setText(5, "0");
		}
		if ((s & 0b00001000) == 8) {
			optionsRegItem.setText(4, "1");
		} else {
			optionsRegItem.setText(4, "0");
		}
		if ((s & 0b00010000) == 16) {
			optionsRegItem.setText(3, "1");
		} else {
			optionsRegItem.setText(3, "0");
		}
		if ((s & 0b00100000) == 32) {
			optionsRegItem.setText(2, "1");
		} else {
			optionsRegItem.setText(2, "0");
		}
		if ((s & 0b01000000) == 64) {
			optionsRegItem.setText(1, "1");
		} else {
			optionsRegItem.setText(1, "0");
		}
		if ((s & 0b10000000) == 128) {
			optionsRegItem.setText(0, "1");
		} else {
			optionsRegItem.setText(0, "0");
		}
	}

	public void setIntconValue(int s) {
		intconRegHexValue.setText(String.format("%02X", s));
		if ((s & 0b00000001) == 1) {
			intconRegItem.setText(7, "1");
		} else {
			intconRegItem.setText(7, "0");
		}
		if ((s & 0b00000010) == 2) {
			intconRegItem.setText(6, "1");
		} else {
			intconRegItem.setText(6, "0");
		}
		if ((s & 0b00000100) == 4) {
			intconRegItem.setText(5, "1");
		} else {
			intconRegItem.setText(5, "0");
		}
		if ((s & 0b00001000) == 8) {
			intconRegItem.setText(4, "1");
		} else {
			intconRegItem.setText(4, "0");
		}
		if ((s & 0b00010000) == 16) {
			intconRegItem.setText(3, "1");
		} else {
			intconRegItem.setText(3, "0");
		}
		if ((s & 0b00100000) == 32) {
			intconRegItem.setText(2, "1");
		} else {
			intconRegItem.setText(2, "0");
		}
		if ((s & 0b01000000) == 64) {
			intconRegItem.setText(1, "1");
		} else {
			intconRegItem.setText(1, "0");
		}
		if ((s & 0b10000000) == 128) {
			intconRegItem.setText(0, "1");
		} else {
			intconRegItem.setText(0, "0");
		}
	}

	public void setPortALabels(int t) {
		int a = t & 0b00000001;
		int b = t & 0b00000010;
		int c = t & 0b00000100;
		int d = t & 0b00001000;
		int e = t & 0b00010000;
		int f = t & 0b00100000;
		int g = t & 0b01000000;
		int h = t & 0b10000000;

		if (a > 0) {
			portAPinItem.setText(8, "1");
		} else {
			portAPinItem.setText(8, "0");
		}
		if (b > 0) {
			portAPinItem.setText(7, "1");
		} else {
			portAPinItem.setText(7, "0");
		}
		if (c > 0) {
			portAPinItem.setText(6, "1");
		} else {
			portAPinItem.setText(6, "0");
		}
		if (d > 0) {
			portAPinItem.setText(5, "1");
		} else {
			portAPinItem.setText(5, "0");
		}
		if (e > 0) {
			portAPinItem.setText(4, "1");
		} else {
			portAPinItem.setText(4, "0");
		}
		if (f > 0) {
			portAPinItem.setText(3, "1");
		} else {
			portAPinItem.setText(3, "0");
		}
		if (g > 0) {
			portAPinItem.setText(2, "1");
		} else {
			portAPinItem.setText(2, "0");
		}
		if (h > 0) {
			portAPinItem.setText(1, "1");
		} else {
			portAPinItem.setText(1, "0");
		}
	}

	public void setTrisALabels(int t) {
		int a = t & 0b00000001;
		int b = t & 0b00000010;
		int c = t & 0b00000100;
		int d = t & 0b00001000;
		int e = t & 0b00010000;
		int f = t & 0b00100000;
		int g = t & 0b01000000;
		int h = t & 0b10000000;

		if (a > 0) {
			portATrisItem.setText(8, "i");
		} else {
			portATrisItem.setText(8, "o");
		}
		if (b > 0) {
			portATrisItem.setText(7, "i");
		} else {
			portATrisItem.setText(7, "o");
		}
		if (c > 0) {
			portATrisItem.setText(6, "i");
		} else {
			portATrisItem.setText(6, "o");
		}
		if (d > 0) {
			portATrisItem.setText(5, "i");
		} else {
			portATrisItem.setText(5, "o");
		}
		if (e > 0) {
			portATrisItem.setText(4, "i");
		} else {
			portATrisItem.setText(4, "o");
		}
		if (f > 0) {
			portATrisItem.setText(3, "i");
		} else {
			portATrisItem.setText(3, "o");
		}
		if (g > 0) {
			portATrisItem.setText(2, "i");
		} else {
			portATrisItem.setText(2, "o");
		}
		if (h > 0) {
			portATrisItem.setText(1, "i");
		} else {
			portATrisItem.setText(1, "o");
		}
	}

	public void setPortBLabels(int t) {
		int a = t & 0b00000001;
		int b = t & 0b00000010;
		int c = t & 0b00000100;
		int d = t & 0b00001000;
		int e = t & 0b00010000;
		int f = t & 0b00100000;
		int g = t & 0b01000000;
		int h = t & 0b10000000;

		if (a > 0) {
			portBPinItem.setText(8, "1");
		} else {
			portBPinItem.setText(8, "0");
		}
		if (b > 0) {
			portBPinItem.setText(7, "1");
		} else {
			portBPinItem.setText(7, "0");
		}
		if (c > 0) {
			portBPinItem.setText(6, "1");
		} else {
			portBPinItem.setText(6, "0");
		}
		if (d > 0) {
			portBPinItem.setText(5, "1");
		} else {
			portBPinItem.setText(5, "0");
		}
		if (e > 0) {
			portBPinItem.setText(4, "1");
		} else {
			portBPinItem.setText(4, "0");
		}
		if (f > 0) {
			portBPinItem.setText(3, "1");
		} else {
			portBPinItem.setText(3, "0");
		}
		if (g > 0) {
			portBPinItem.setText(2, "1");
		} else {
			portBPinItem.setText(2, "0");
		}
		if (h > 0) {
			portBPinItem.setText(1, "1");
		} else {
			portBPinItem.setText(1, "0");
		}
	}

	public void setTrisBLabels(int t) {
		int a = t & 0b00000001;
		int b = t & 0b00000010;
		int c = t & 0b00000100;
		int d = t & 0b00001000;
		int e = t & 0b00010000;
		int f = t & 0b00100000;
		int g = t & 0b01000000;
		int h = t & 0b10000000;

		if (a > 0) {
			portBTrisItem.setText(8, "i");
		} else {
			portBTrisItem.setText(8, "o");
		}
		if (b > 0) {
			portBTrisItem.setText(7, "i");
		} else {
			portBTrisItem.setText(7, "o");
		}
		if (c > 0) {
			portBTrisItem.setText(6, "i");
		} else {
			portBTrisItem.setText(6, "o");
		}
		if (d > 0) {
			portBTrisItem.setText(5, "i");
		} else {
			portBTrisItem.setText(5, "o");
		}
		if (e > 0) {
			portBTrisItem.setText(4, "i");
		} else {
			portBTrisItem.setText(4, "o");
		}
		if (f > 0) {
			portBTrisItem.setText(3, "i");
		} else {
			portBTrisItem.setText(3, "o");
		}
		if (g > 0) {
			portBTrisItem.setText(2, "i");
		} else {
			portBTrisItem.setText(2, "o");
		}
		if (h > 0) {
			portBTrisItem.setText(1, "i");
		} else {
			portBTrisItem.setText(1, "o");
		}
	}

	public void open() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public void setLaufzeit(long l) {
		laufzeitVal.setText(String.valueOf(l) + " ms");
	}

	public void setSteps(int steps) {
		stepsVal.setText(String.valueOf(steps));
	}

	public void stackClear() {
		// TODO
		// listModelStack.removeAll();
	}

	public void stackAdd(Integer integer) {
		// TODO
		// listModelStack.add(String.valueOf(integer));
	}

	public void setTableEntry(int registerEntry, int row, int column) {
		getTableMem().getItem(row).setText(column, String.format("%02X", registerEntry));
	}

	public int getListModelSize() {
		return inputList.size();
	}

	public String getElementListModel(int i) {
		return inputList.get(i);
	}

	public void addElementListModel(String value) {
		inputList.add(value);
	}

	public void clear_ListModel() {
		codeTable.removeAll();
		inputList.clear();
	}

	public void remove_ElementListModel(int index) {
		inputList.remove(index);
	}

	public void setVisibilityButtons(boolean error, boolean next, boolean pause) {
		btnReset.setEnabled(next);
		btnRun.setEnabled(next);
		btnWeiter.setEnabled(next);
		btnStop.setEnabled(pause);
	}

	public void setErrorMsgs(String string) {
		// TODO Auto-generated method stub

	}

	public void setSerialDisconnected() {
		// TODO Auto-generated method stub

	}

	public String getTableEntry(int row, int column) {
		return getTableMem().getItem(row).getText(column);
	}

	public int getFrequency() {
		return getSlider().getSelection();
	}

	public void setFrequency(int i) {
		getSlider().setSelection(i);
		getQuarzFreqVal().setText(String.valueOf(i));
	}

	public int getValuePortA() {
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < 9; i++) {
			builder.append(portAPinItem.getText(i));
		}
		int binary = 0;
		binary = Integer.parseInt(builder.toString(), 2);
		System.out.println("binary: " + binary);
		return binary;
	}

	public int getValuePortB() {
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < 9; i++) {
			builder.append(portBPinItem.getText(i));
		}
		int binary = 0;
		binary = Integer.parseInt(builder.toString(), 2);
		System.out.println("binary: " + binary);
		return binary;
	}

	public String getFilePath() {
		FileDialog dialog = new FileDialog(shell);
		String[] extensions = { "lst" };
		dialog.setFilterExtensions(extensions);
		return dialog.open();
	}

	public void setOpenFileListener(SelectionAdapter l) {
		openFileItem.addSelectionListener(l);
	}

	public void setNextStepListener(SelectionAdapter l) {
		btnWeiter.addSelectionListener(l);
	}

	public void setStartProgramListener(SelectionAdapter l) {
		btnRun.addSelectionListener(l);
	}

	public void setPauseListener(SelectionAdapter l) {
		btnStop.addSelectionListener(l);
	}

	public void setChangePortABits(Listener l) {
		getTablePortA().addListener(SWT.MouseDoubleClick, l);
	}

	public void setChangePortBBits(Listener l) {
		getTablePortB().addListener(SWT.MouseDoubleClick, l);
	}

	public void setSliderFrequencyListener(Listener l) {
		slider.addListener(SWT.Selection, l);
	}

	public Table getTablePortA() {
		return tablePortA;
	}

	public void setTablePortA(Table tablePortA) {
		this.tablePortA = tablePortA;
	}

	public Table getTablePortB() {
		return tablePortB;
	}

	public void setTablePortB(Table tablePortB) {
		this.tablePortB = tablePortB;
	}

	public void setMemoryListener(Listener listener) {
		getTableMem().addListener(SWT.MouseDoubleClick, listener);
	}

	public void setCodeInput() {
		for (int i = 0; i < inputList.size(); i++) {
			final int index = i;
			TableItem item = new TableItem(codeTable, SWT.NONE);
			TableEditor editor = new TableEditor(codeTable);
			Button checkButton = new Button(codeTable, SWT.CHECK);
			editor.grabHorizontal = true;

			checkButtonList.add(i, checkButton);

			checkButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					if (breakpointList.contains(index)) {
						breakpointList.remove(breakpointList.indexOf(index));
					} else {
						breakpointList.add(index);
					}
				}
			});

			editor.setEditor(checkButton, item, 0);

			if (!inputList.get(i).isEmpty()) {
				item.setFont(new org.eclipse.swt.graphics.Font(codeTable.getDisplay(), "Courier", 6, SWT.NONE));
				item.setText(1, inputList.get(i));
				if (inputList.get(i).startsWith("0000")) {
					codeTable.setSelection(i);
					codeTable.showSelection();
				}
			}
		}
	}

	public Slider getSlider() {
		return slider;
	}

	public void setSlider(Slider slider) {
		this.slider = slider;
	}

	public Label getQuarzFreqVal() {
		return quarzFreqVal;
	}

	public void setQuarzFreqVal(Label quarzFreqVal) {
		this.quarzFreqVal = quarzFreqVal;
	}

	public Table getTableMem() {
		return tableMem;
	}

	public void setTableMem(Table tableMem) {
		this.tableMem = tableMem;
	}

	public TableEditor getEditor() {
		return editor;
	}

	public void setEditor(TableEditor editor) {
		this.editor = editor;
	}

	public Table getSpecRegTable() {
		return specRegTable;
	}

	public void setSpecRegTable(Table specRegTable) {
		this.specRegTable = specRegTable;
	}

	public void setSpecRegListener(Listener listener) {
		getSpecRegTable().addListener(SWT.MouseDoubleClick, listener);
	}

	public void setResetListener(SelectionAdapter listener) {
		btnReset.addSelectionListener(listener);
	}

	public void setBreakPointButtonListener(SelectionAdapter listener) {

	}
}
