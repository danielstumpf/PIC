package dhbw.sysnprog.pic.view;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

/**
 * @author Daniel
 *
 */
/**
 * @author Daniel
 *
 */
/**
 * @author Daniel
 *
 */
public class PicSimView {
	/**
	 * Anzahl der Spalten in der Tabelle
	 */
	private static final int COLUMNS = 9;
	/**
	 * Anzahl der Reihen in der Tabelle
	 */
	private static final int ROWS = 32;
	/**
	 * Reguläre Spaltenbreite, abhängig von der Bildschirmauflösung
	 */
	private static final int REG_COLUMN_WIDTH = 90;
	/**
	 * minimale Quarzfrequenz
	 */
	private static final int FREQ_MINIMUM = 10;
	/**
	 * maximale Quarzfrequenz
	 */
	private static final int FREQ_MAXIMUM = 10000;

	/**
	 * Setzt dem übergebenen TableItem alle Werte auf "00"
	 *
	 * @param item
	 */
	public static void setItemInput(TableItem item) {
		for (int i = 1; i < COLUMNS; i++) {
			item.setText(i, "00");
		}
	}

	/**
	 * Display, auf dem die Shell geöffnet wird
	 */
	private final Display display;

	/**
	 * Fenster, in dem der Simulator dargestellt wird
	 */
	private final Shell shell;

	/**
	 * MenüItem um eine Datei zu öffnen
	 */
	private MenuItem openFileItem;
	/**
	 * MenüItem um das Hilfedokument zu öffnen
	 */
	private MenuItem dataSheetItem;
	/**
	 * Composite im linken Bereich des Simulators. Hier werden Stack, RA und RB
	 * angezeigt.
	 */
	private Composite left;
	/**
	 * Composite im rechten Bereich des Simulators. Hier wird dere gesamte
	 * Speicher abgebildet.
	 */
	private Composite right;
	/**
	 * Composite im mittleren oberen Bereich des Simulators. Hier wird das
	 * auszuführende Programm dargestellt.
	 */
	private Composite centerUp;
	/**
	 * Composite im unteren mittleren Bereich des Simulators. Hier werden die
	 * verschiedenen Register visualisiert.
	 */
	private Composite centerDown;
	/**
	 * Button zum Zurücksetzen des Programmablaufs.
	 */
	private Button btnReset;
	/**
	 * Button um das Programm schrittweise weiterlaufen zu lassen.
	 */
	private Button btnWeiter;
	/**
	 * Button, um das Programm zu unterbrechen
	 */
	private Button btnStop;
	/**
	 * Button, um das Programm zu starten
	 */
	private Button btnRun;
	/**
	 * Breakpoint-Liste. Hier werden alle gesetzten Breakpoints gespeichert.
	 */
	public java.util.List<Integer> breakpointList = new ArrayList<Integer>();
	/**
	 * Label zur Anzeige des Wertes im Statusregister
	 */
	private Label statusRegHexValue;
	/**
	 * Label zur Anzeige des Wertes im Optionsregister
	 */
	private Label optionRegHexValue;
	/**
	 * Label zur Anzeige des Wertes im Intconregister
	 */
	private Label intconRegHexValue;
	/**
	 * Label zur Anzeige der Laufzeit
	 */
	private Label laufzeitVal;
	/**
	 * Label zur Anzeige der durchgeführten Schritte
	 */
	private Label stepsVal;
	/**
	 * Label zur Anzeige der eingestellten Quarzfrequenz
	 */
	private Label quarzFreqVal;
	/**
	 * Tabelle, in der der Programminhalt angezeigt wird.
	 */
	private Table codeTable;
	/**
	 * Tabelle, in der die Inhalte des Spezialregisters angezeigt werden.
	 */
	private Table specRegTable;
	/**
	 * Tabelle, in der die Inhalte des Port A dargestellt werden.
	 */
	private Table tablePortA;
	/**
	 * Tabelle, in der die Inhalte des Port B dargestellt werden.
	 */
	private Table tablePortB;
	/**
	 * Tabelle, in der die Inhalte des Stacks dargestellt werden.
	 */
	private Table stackTable;
	/**
	 * Tabelle, in der die Inhalte des Speichers dargestellt werden.
	 */
	private Table tableMem;
	private Table statusRegTable;
	private Table optionsRegTable;
	private Table intconRegTable;
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
	private Button btnLatchRA;
	private Button btnLatchRB;

	/**
	 * Konstruktor der View. Aufruf bei Programmstart
	 */
	public PicSimView() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("PIC16F84A-Simulator");
		shell.setMaximized(true);
		shell.setLayout(new FormLayout());
		shell.setLayoutData(new FormData(100, 100));

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

		// Stack erstellen
		createStack();
	}

	/**
	 * Fügt den übergebenen String zur Input-Liste hinzu
	 *
	 * @param value
	 */
	public void addElementToList(String value) {
		inputList.add(value);
	}

	/**
	 * Entfernt alle Elemente aus der Code-Tabelle und aus der Input-Liste
	 */
	public void clearList() {
		codeTable.removeAll();
		inputList.clear();
	}

	/**
	 * Erzeugt die Buttons zur Steuerung des Programms
	 */
	private void createButtons() {
		final Composite buttonComposite = new Composite(centerDown, SWT.NONE);
		buttonComposite.setLayout(new FormLayout());
		final FormData buttonCompData = new FormData();
		buttonCompData.bottom = new FormAttachment(100, -5);
		buttonCompData.left = new FormAttachment(0, 5);
		buttonCompData.right = new FormAttachment(15, -3);
		buttonCompData.top = new FormAttachment(60, 5);
		buttonComposite.setLayoutData(buttonCompData);

		btnReset = new Button(buttonComposite, SWT.PUSH);
		btnReset.setText("Reset");
		final FormData resButtonData = new FormData();
		resButtonData.top = new FormAttachment(0, 3);
		resButtonData.left = new FormAttachment(0, 3);
		resButtonData.width = 100;
		btnReset.setLayoutData(resButtonData);

		btnWeiter = new Button(buttonComposite, SWT.PUSH);
		btnWeiter.setText("Step");
		final FormData nxtStepBtnData = new FormData();
		nxtStepBtnData.top = new FormAttachment(btnReset, 3);
		nxtStepBtnData.left = new FormAttachment(0, 3);
		nxtStepBtnData.width = 100;
		btnWeiter.setLayoutData(nxtStepBtnData);

		btnStop = new Button(buttonComposite, SWT.PUSH);
		btnStop.setText("Pause");
		final FormData stopBtnData = new FormData();
		stopBtnData.top = new FormAttachment(btnWeiter, 3);
		stopBtnData.left = new FormAttachment(0, 3);
		stopBtnData.width = 100;
		btnStop.setLayoutData(stopBtnData);

		btnRun = new Button(buttonComposite, SWT.PUSH);
		btnRun.setText("Run");
		final FormData runBtnData = new FormData();
		runBtnData.top = new FormAttachment(btnStop, 3);
		runBtnData.left = new FormAttachment(0, 3);
		runBtnData.width = 100;
		btnRun.setLayoutData(runBtnData);
	}

	/**
	 * Erzeugung der Tabelle zum Anzeigen des Programminhalts
	 */
	private void createCodeTable() {
		codeTable = new Table(centerUp, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		final FormData codeData = new FormData();
		codeData.top = new FormAttachment(0, 3);
		codeData.bottom = new FormAttachment(100, -3);
		codeData.left = new FormAttachment(0, 3);
		codeData.right = new FormAttachment(100, -3);
		codeTable.setLayoutData(codeData);
		codeTable.setHeaderVisible(false);
		final TableColumn checkColumn = new TableColumn(codeTable, SWT.NONE);
		checkColumn.setText("x");
		checkColumn.setWidth(30);
		final TableColumn textColumn = new TableColumn(codeTable, SWT.SINGLE);
		textColumn.setWidth(1000);
		checkButtonList = new ArrayList<Button>();
	}

	/**
	 * Erzeugung der Composites zur Aufteilung der Programminhalte
	 */
	private void createComposites() {
		// Linkes Drittel Composite
		left = new Composite(shell, SWT.BORDER);
		// left.setBackground(new Color(Display.getCurrent(), new RGB(100, 100,
		// 100)));
		left.setLayout(new FormLayout());

		final FormData leftCompformData = new FormData();
		leftCompformData.top = new FormAttachment(0, 3);
		leftCompformData.left = new FormAttachment(0, 5);
		leftCompformData.right = new FormAttachment(20, -5);
		leftCompformData.bottom = new FormAttachment(100, -5);
		left.setLayoutData(leftCompformData);

		// Composite rechtes Drittel
		right = new Composite(shell, SWT.BORDER);
		right.setLayout(new FormLayout());
		final FormData rightCompFormData = new FormData();
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
		final FormData centerUpCompformData = new FormData();
		centerUpCompformData.top = new FormAttachment(0, 3);
		centerUpCompformData.left = new FormAttachment(left);
		centerUpCompformData.right = new FormAttachment(right);
		centerUpCompformData.bottom = new FormAttachment(65, -3);
		centerUp.setLayoutData(centerUpCompformData);

		// Composite Mitte Unten
		centerDown = new Composite(shell, SWT.BORDER);
		centerDown.setLayout(new FormLayout());
		final FormData centerDownCompFormData = new FormData();
		centerDownCompFormData.top = new FormAttachment(centerUp);
		centerDownCompFormData.left = new FormAttachment(left);
		centerDownCompFormData.right = new FormAttachment(right);
		centerDownCompFormData.bottom = new FormAttachment(100, -5);
		centerDown.setLayoutData(centerDownCompFormData);
		// centerDown.setBackground(new Color(Display.getCurrent(), new RGB(255,
		// 0, 0)));
	}

	/**
	 * Erzeugung der Speichertabelle zum Abbilden des gesamten Speichers in der
	 * Tabelle.
	 */
	private void createMemory() {
		setTableMem(new Table(right, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION));
		final FormData tableFormData = new FormData();
		tableFormData.top = new FormAttachment(0, 3);
		tableFormData.left = new FormAttachment(0, 3);
		tableFormData.right = new FormAttachment(100, -3);
		tableFormData.bottom = new FormAttachment(100, -3);
		getTableMem().setLayoutData(tableFormData);
		getTableMem().setLinesVisible(true);
		getTableMem().setHeaderVisible(true);

		for (int i = 0; i < COLUMNS; i++) {
			final TableColumn tableColumn = new TableColumn(getTableMem(), SWT.NONE);
			tableColumn.setWidth(70);
			tableColumn.setResizable(false);
			if (i != 0) {
				tableColumn.setText("0" + (i - 1));
			}
		}

		setEditor(new TableEditor(getTableMem()));
		getEditor().horizontalAlignment = SWT.LEFT;
		getEditor().grabHorizontal = true;

		int zeileDez = 0;
		TableItem item = null;
		for (int i = 0; i < ROWS; i++) {
			final Device device = Display.getCurrent();
			final Color grey = new Color(device, 160, 160, 160);
			item = new TableItem(getTableMem(), SWT.NONE);

			String zeileHexa = Integer.toHexString(zeileDez);
			if (zeileHexa.length() < 2) {
				zeileHexa = '0' + zeileHexa;
			}
			item.setBackground(0, grey);
			item.setText(0, String.valueOf(zeileHexa).toUpperCase());

			zeileDez = zeileDez + 8;
		}
	}

	/**
	 * Erzeugung des Menüs zum Öffnen einer Datei und zum Aufrufen des
	 * Hilfedokuments
	 */
	private void createMenu() {
		// Datei-Menüpunkt erstellen
		final Menu menu = CreateItem.getMenu(shell, SWT.BAR | SWT.LEFT_TO_RIGHT);
		shell.setMenuBar(menu);

		final MenuItem fileItemMenu = CreateItem.getMenuItem(menu, SWT.CASCADE, "Datei");
		final Menu fileMenu = CreateItem.getMenu(shell, SWT.DROP_DOWN);
		fileItemMenu.setMenu(fileMenu);
		openFileItem = CreateItem.getMenuItem(fileMenu, SWT.PUSH, "Öffnen");

		// Optionen Menüpunkt erstellen
		final MenuItem menuOptionsItem = CreateItem.getMenuItem(menu, SWT.CASCADE, "Optionen");
		final Menu optionsMenu = CreateItem.getMenu(shell, SWT.DROP_DOWN);
		menuOptionsItem.setMenu(optionsMenu);
		dataSheetItem = CreateItem.getMenuItem(optionsMenu, SWT.PUSH, "Dokumentation öffnen..");

		dataSheetItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Path path = Paths.get("resources", "documentation.pdf");
				Program.launch(path.toString());
			}
		});
	}

	/**
	 * Erzeugung devon PortA und PortB
	 */
	private void createPorts() {
		final Composite tableComp = new Composite(left, SWT.BORDER);
		final FormLayout formLayout = new FormLayout();
		tableComp.setLayout(formLayout);
		final FormData formData = new FormData();
		formData.top = new FormAttachment(0, 3);
		formData.left = new FormAttachment(0, 3);
		formData.right = new FormAttachment(100, -3);
		tableComp.setLayoutData(formData);

		final Label latchLabelRA = new Label(tableComp, SWT.NONE);
		latchLabelRA.setText("Latch RA");
		final FormData latchLabelRAData = new FormData();
		latchLabelRAData.top = new FormAttachment(0, 3);
		latchLabelRAData.left = new FormAttachment(0, 3);
		latchLabelRAData.width = 150;
		latchLabelRA.setLayoutData(latchLabelRAData);

		btnLatchRA = new Button(tableComp, SWT.CHECK);
		final FormData btnLatchRAData = new FormData();
		btnLatchRAData.top = new FormAttachment(0, 3);
		btnLatchRAData.left = new FormAttachment(latchLabelRA, 3);
		btnLatchRA.setLayoutData(btnLatchRAData);

		setTablePortA(new Table(tableComp, SWT.NONE));
		final FormData tableData = new FormData();
		tableData.top = new FormAttachment(latchLabelRA, 5);
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
			final TableColumn tableColumn = new TableColumn(getTablePortA(), SWT.NONE);
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

		final Label latchLabelRB = new Label(tableComp, SWT.NONE);
		latchLabelRB.setText("Latch RB");
		final FormData latchLabelRBData = new FormData();
		latchLabelRBData.top = new FormAttachment(getTablePortA(), 3);
		latchLabelRBData.left = new FormAttachment(0, 3);
		latchLabelRBData.width = 150;
		latchLabelRB.setLayoutData(latchLabelRBData);

		btnLatchRB = new Button(tableComp, SWT.CHECK);
		final FormData btnLatchRBData = new FormData();
		btnLatchRBData.top = new FormAttachment(getTablePortA(), 3);
		btnLatchRBData.left = new FormAttachment(latchLabelRB, 3);
		btnLatchRB.setLayoutData(btnLatchRBData);

		setTablePortB(new Table(tableComp, SWT.NONE));
		final FormData tableDataB = new FormData();
		tableDataB.top = new FormAttachment(latchLabelRB, 5);
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
			final TableColumn tableColumn = new TableColumn(getTablePortB(), SWT.NONE);
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

	/**
	 * Erzeugung Registerabbildungen
	 */
	private void createRegisters() {
		// Spezialregister, Beschriftung und Tabelle erstellen
		final Label specRegLabel = new Label(centerDown, SWT.NONE);
		final FormData specRegLabelData = new FormData();
		specRegLabelData.top = new FormAttachment(0, 3);
		specRegLabelData.left = new FormAttachment(0, 3);
		specRegLabel.setLayoutData(specRegLabelData);
		specRegLabel.setText("Spezialregister");

		setSpecRegTable(new Table(centerDown, SWT.BORDER));
		getSpecRegTable().setHeaderVisible(true);
		getSpecRegTable().setHeaderVisible(false);
		final FormData specTableData = new FormData();
		specTableData.top = new FormAttachment(specRegLabel, 5);
		specTableData.left = new FormAttachment(0, 3);
		specTableData.right = new FormAttachment(18, -3);
		specTableData.bottom = new FormAttachment(50, -3);
		getSpecRegTable().setLayoutData(specTableData);

		final TableColumn specNameColumn = new TableColumn(getSpecRegTable(), SWT.NONE);
		specNameColumn.setResizable(false);
		specNameColumn.setWidth(130);
		specNameColumn.setText("Bezeichnung");

		final TableColumn specValueColumn = new TableColumn(getSpecRegTable(), SWT.NONE);
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
		final Label statusRegLabel = new Label(centerDown, SWT.NONE);
		final FormData statusRegLabelData = new FormData();
		statusRegLabelData.top = new FormAttachment(0, 3);
		statusRegLabelData.left = new FormAttachment(getSpecRegTable(), 5);
		statusRegLabel.setLayoutData(statusRegLabelData);
		statusRegLabel.setText("Status-Register");

		setStatusRegTable(new Table(centerDown, SWT.BORDER));
		getStatusRegTable().setHeaderVisible(true);
		getStatusRegTable().setLinesVisible(true);

		final FormData statusRegData = new FormData();
		statusRegData.top = new FormAttachment(statusRegLabel, 5);
		statusRegData.left = new FormAttachment(getSpecRegTable(), 5);
		statusRegData.bottom = new FormAttachment(30);
		getStatusRegTable().setLayoutData(statusRegData);

		// Tabellenspalte für "IRP-Bit"
		final TableColumn statusIRPColumn = new TableColumn(getStatusRegTable(), SWT.NONE);
		statusIRPColumn.setResizable(false);
		statusIRPColumn.setWidth(REG_COLUMN_WIDTH);
		statusIRPColumn.setText("IRP");

		// Tabellenspalte für "RP1-Bit"
		final TableColumn statusRP1Column = new TableColumn(getStatusRegTable(), SWT.NONE);
		statusRP1Column.setResizable(false);
		statusRP1Column.setWidth(REG_COLUMN_WIDTH);
		statusRP1Column.setText("RP1");

		// Tabellenspalte für "RP0-Bit"
		final TableColumn statusRP0Column = new TableColumn(getStatusRegTable(), SWT.NONE);
		statusRP0Column.setResizable(false);
		statusRP0Column.setWidth(REG_COLUMN_WIDTH);
		statusRP0Column.setText("RP0");

		// Tabellenspalte für "P0-Bit"
		final TableColumn statusT0Column = new TableColumn(getStatusRegTable(), SWT.NONE);
		statusT0Column.setResizable(false);
		statusT0Column.setWidth(REG_COLUMN_WIDTH);
		statusT0Column.setText("T0");

		// Tabellenspalte für "PD-Bit"
		final TableColumn statusPDColumn = new TableColumn(getStatusRegTable(), SWT.NONE);
		statusPDColumn.setResizable(false);
		statusPDColumn.setWidth(REG_COLUMN_WIDTH);
		statusPDColumn.setText("PD");

		// Tabellenspalte für "Z-Bit"
		final TableColumn statusZColumn = new TableColumn(getStatusRegTable(), SWT.NONE);
		statusZColumn.setResizable(false);
		statusZColumn.setWidth(REG_COLUMN_WIDTH);
		statusZColumn.setText("Z");

		// Tabellenspalte für "DC-Bit"
		final TableColumn statusDCColumn = new TableColumn(getStatusRegTable(), SWT.NONE);
		statusDCColumn.setResizable(false);
		statusDCColumn.setWidth(REG_COLUMN_WIDTH);
		statusDCColumn.setText("DC");

		// Tabellenspalte für "C-Bit"
		final TableColumn statusCColumn = new TableColumn(getStatusRegTable(), SWT.NONE);
		statusCColumn.setResizable(false);
		statusCColumn.setWidth(REG_COLUMN_WIDTH);
		statusCColumn.setText("C");

		statusRegItem = new TableItem(getStatusRegTable(), SWT.NONE);

		for (int i = 0; i < 8; i++) {
			statusRegItem.setText(i, "0");
		}
		statusRegItem.setText(3, "1");
		statusRegItem.setText(4, "1");

		final Label optionRegLabel = new Label(centerDown, SWT.NONE);
		final FormData optionRegLabelData = new FormData();
		optionRegLabelData.top = new FormAttachment(getStatusRegTable(), 5);
		optionRegLabelData.left = new FormAttachment(getSpecRegTable(), 5);
		optionRegLabel.setLayoutData(optionRegLabelData);
		optionRegLabel.setText("Options-Register");

		setOptionsRegTable(new Table(centerDown, SWT.BORDER));
		getOptionsRegTable().setHeaderVisible(true);
		getOptionsRegTable().setLinesVisible(true);

		final FormData optionsRegData = new FormData();
		optionsRegData.top = new FormAttachment(optionRegLabel, 5);
		optionsRegData.left = new FormAttachment(getSpecRegTable(), 5);
		optionsRegData.bottom = new FormAttachment(60);
		getOptionsRegTable().setLayoutData(optionsRegData);

		final TableColumn statusRBPColumn = new TableColumn(getOptionsRegTable(), SWT.NONE);
		statusRBPColumn.setResizable(false);
		statusRBPColumn.setWidth(REG_COLUMN_WIDTH);
		statusRBPColumn.setText("RBP");

		final TableColumn optionsINTEDGColumn = new TableColumn(getOptionsRegTable(), SWT.NONE);
		optionsINTEDGColumn.setResizable(false);
		optionsINTEDGColumn.setWidth(REG_COLUMN_WIDTH);
		optionsINTEDGColumn.setText("INTEDG");

		final TableColumn optionsT0CSColumn = new TableColumn(getOptionsRegTable(), SWT.NONE);
		optionsT0CSColumn.setResizable(false);
		optionsT0CSColumn.setWidth(REG_COLUMN_WIDTH);
		optionsT0CSColumn.setText("T0CS");

		final TableColumn optionsT0SEColumn = new TableColumn(getOptionsRegTable(), SWT.NONE);
		optionsT0SEColumn.setResizable(false);
		optionsT0SEColumn.setWidth(REG_COLUMN_WIDTH);
		optionsT0SEColumn.setText("T0SE");

		final TableColumn optionsPSAColumn = new TableColumn(getOptionsRegTable(), SWT.NONE);
		optionsPSAColumn.setResizable(false);
		optionsPSAColumn.setWidth(REG_COLUMN_WIDTH);
		optionsPSAColumn.setText("PSA");

		final TableColumn optionsPS2Column = new TableColumn(getOptionsRegTable(), SWT.NONE);
		optionsPS2Column.setResizable(false);
		optionsPS2Column.setWidth(REG_COLUMN_WIDTH);
		optionsPS2Column.setText("PS2");

		final TableColumn optionsPS1Column = new TableColumn(getOptionsRegTable(), SWT.NONE);
		optionsPS1Column.setResizable(false);
		optionsPS1Column.setWidth(REG_COLUMN_WIDTH);
		optionsPS1Column.setText("PS1");

		final TableColumn optionsPS0Column = new TableColumn(getOptionsRegTable(), SWT.NONE);
		optionsPS0Column.setResizable(false);
		optionsPS0Column.setWidth(REG_COLUMN_WIDTH);
		optionsPS0Column.setText("PS0");

		optionsRegItem = new TableItem(getOptionsRegTable(), SWT.NONE);

		for (int i = 0; i < 8; i++) {
			optionsRegItem.setText(i, "1");
		}

		final Label intconRegLabel = new Label(centerDown, SWT.NONE);
		final FormData intconRegLabelData = new FormData();
		intconRegLabelData.top = new FormAttachment(getOptionsRegTable(), 5);
		intconRegLabelData.left = new FormAttachment(getSpecRegTable(), 5);
		intconRegLabel.setLayoutData(intconRegLabelData);
		intconRegLabel.setText("Intcon-Register");

		setIntconRegTable(new Table(centerDown, SWT.FULL_SELECTION));
		getIntconRegTable().setHeaderVisible(true);
		getIntconRegTable().setLinesVisible(true);

		final FormData intconRegData = new FormData();
		intconRegData.top = new FormAttachment(intconRegLabel, 5);
		intconRegData.left = new FormAttachment(getSpecRegTable(), 5);
		intconRegData.bottom = new FormAttachment(90);
		getIntconRegTable().setLayoutData(intconRegData);

		final TableColumn intconGIEColumn = new TableColumn(getIntconRegTable(), SWT.NONE);
		intconGIEColumn.setResizable(false);
		intconGIEColumn.setWidth(REG_COLUMN_WIDTH);
		intconGIEColumn.setText("GIE");

		final TableColumn intconEEIEColumn = new TableColumn(getIntconRegTable(), SWT.NONE);
		intconEEIEColumn.setResizable(false);
		intconEEIEColumn.setWidth(REG_COLUMN_WIDTH);
		intconEEIEColumn.setText("EEIE");

		final TableColumn intconT0IEColumn = new TableColumn(getIntconRegTable(), SWT.NONE);
		intconT0IEColumn.setResizable(false);
		intconT0IEColumn.setWidth(REG_COLUMN_WIDTH);
		intconT0IEColumn.setText("T0IE");

		final TableColumn intconINTEColumn = new TableColumn(getIntconRegTable(), SWT.NONE);
		intconINTEColumn.setResizable(false);
		intconINTEColumn.setWidth(REG_COLUMN_WIDTH);
		intconINTEColumn.setText("INTE");

		final TableColumn intconRBIEColumn = new TableColumn(getIntconRegTable(), SWT.NONE);
		intconRBIEColumn.setResizable(false);
		intconRBIEColumn.setWidth(REG_COLUMN_WIDTH);
		intconRBIEColumn.setText("RBIE");

		final TableColumn intconT0IFColumn = new TableColumn(getIntconRegTable(), SWT.NONE);
		intconT0IFColumn.setResizable(false);
		intconT0IFColumn.setWidth(REG_COLUMN_WIDTH);
		intconT0IFColumn.setText("T0IF");

		final TableColumn intconINTFColumn = new TableColumn(getIntconRegTable(), SWT.NONE);
		intconINTFColumn.setResizable(false);
		intconINTFColumn.setWidth(REG_COLUMN_WIDTH);
		intconINTFColumn.setText("INTF");

		final TableColumn intconRBIFColumn = new TableColumn(getIntconRegTable(), SWT.NONE);
		intconRBIFColumn.setResizable(false);
		intconRBIFColumn.setWidth(REG_COLUMN_WIDTH);
		intconRBIFColumn.setText("RBIF");

		intconRegItem = new TableItem(getIntconRegTable(), SWT.NONE);

		for (int i = 0; i < 8; i++) {
			intconRegItem.setText(i, "0");
		}

		statusRegHexValue = new Label(centerDown, SWT.NONE);
		final FormData statusLabelData = new FormData();
		statusLabelData.top = new FormAttachment(0, 3);
		statusLabelData.left = new FormAttachment(statusRegLabel, 10);
		statusLabelData.width = 50;
		statusRegHexValue.setLayoutData(statusLabelData);

		optionRegHexValue = new Label(centerDown, SWT.NONE);
		final FormData optionLabelData = new FormData();
		optionLabelData.bottom = new FormAttachment(getOptionsRegTable(), -3);
		optionLabelData.left = new FormAttachment(optionRegLabel, 10);
		optionLabelData.width = 35;
		optionRegHexValue.setLayoutData(optionLabelData);

		intconRegHexValue = new Label(centerDown, SWT.NONE);
		final FormData intconLabelData = new FormData();
		intconLabelData.bottom = new FormAttachment(getIntconRegTable(), -3);
		intconLabelData.left = new FormAttachment(intconRegLabel, 10);
		intconLabelData.width = 35;
		intconRegHexValue.setLayoutData(intconLabelData);
	}

	/**
	 * Erzeugung des Stackabbilds
	 */
	private void createStack() {
		final FormData stackTableFormData = new FormData();
		stackTableFormData.bottom = new FormAttachment(100, -5);
		stackTableFormData.left = new FormAttachment(0, 5);
		stackTableFormData.right = new FormAttachment(100, -5);

		stackTable = new Table(left, SWT.NONE);
		stackTable.setLayoutData(stackTableFormData);
		stackTable.setLinesVisible(true);
		stackTable.setHeaderVisible(false);
		final TableColumn col = new TableColumn(stackTable, SWT.NONE);
		col.setWidth(80);
		for (int i = 0; i < 8; i++) {
			final TableItem itemStack = new TableItem(stackTable, SWT.NONE);
			itemStack.setText("");
		}

		final Label labelStack = new Label(left, SWT.NONE);
		final FormData labelData = new FormData();
		labelData.bottom = new FormAttachment(stackTable, -5);
		labelData.left = new FormAttachment(0, 5);
		labelData.width = 100;
		labelStack.setLayoutData(labelData);
		labelStack.setText("Stack");
	}

	/**
	 * Erzeugung der Darstellung der Zeitfunktionen
	 */
	private void createTiming() {
		final Group group = new Group(centerDown, SWT.NONE);
		group.setLayout(new FormLayout());
		final FormData groupFormData = new FormData();
		groupFormData.top = new FormAttachment(0, 5);
		groupFormData.height = 100;
		groupFormData.width = 250;
		groupFormData.right = new FormAttachment(100, -5);

		group.setLayoutData(groupFormData);
		group.setText("Quarzfrequenz");

		setQuarzFreqVal(new Label(group, SWT.NONE));
		final FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(30, 3);
		labelFormData.right = new FormAttachment(100, -3);
		labelFormData.top = new FormAttachment(0, 3);
		getQuarzFreqVal().setLayoutData(labelFormData);
		getQuarzFreqVal().setText("4000");

		setSlider(new Slider(group, SWT.HORIZONTAL));
		getSlider().setMaximum(FREQ_MAXIMUM);
		getSlider().setMinimum(FREQ_MINIMUM);
		getSlider().setSelection(4000);
		final FormData data = new FormData();
		data.bottom = new FormAttachment(100, -10);
		data.left = new FormAttachment(0, 3);
		data.right = new FormAttachment(100, -3);
		getSlider().setLayoutData(data);

		final Group groupRuntime = new Group(centerDown, SWT.NONE);
		groupRuntime.setLayout(new FormLayout());
		final FormData groupData = new FormData();
		groupData.top = new FormAttachment(group, 5);
		groupData.width = 250;
		groupData.right = new FormAttachment(100, -5);
		groupRuntime.setLayoutData(groupData);
		groupRuntime.setText("Laufzeit");

		final Button setBackRuntime = new Button(groupRuntime, SWT.PUSH);
		setBackRuntime.setText("Zurücksetzen");
		final FormData buttonData = new FormData();
		buttonData.bottom = new FormAttachment(100, -3);
		buttonData.left = new FormAttachment(0, 3);
		setBackRuntime.setLayoutData(buttonData);

		laufzeitVal = new Label(groupRuntime, SWT.NONE);
		final FormData labelFormData1 = new FormData();
		labelFormData1.left = new FormAttachment(0, 3);
		labelFormData1.right = new FormAttachment(100, -3);
		labelFormData1.bottom = new FormAttachment(setBackRuntime, -3);
		laufzeitVal.setLayoutData(labelFormData1);
		laufzeitVal.setText("0 ms");

		final Group groupSteps = new Group(centerDown, SWT.NONE);
		groupSteps.setLayout(new FormLayout());
		final FormData groupStepsData = new FormData();
		groupStepsData.top = new FormAttachment(groupRuntime, 5);
		groupStepsData.width = 250;
		groupStepsData.right = new FormAttachment(100, -5);
		groupSteps.setLayoutData(groupStepsData);
		groupSteps.setText("Schritte");

		stepsVal = new Label(groupSteps, SWT.NONE);
		final FormData labelFormData2 = new FormData();
		labelFormData2.left = new FormAttachment(0, 3);
		labelFormData2.right = new FormAttachment(100, -3);
		labelFormData2.bottom = new FormAttachment(100, -3);
		stepsVal.setLayoutData(labelFormData2);
		stepsVal.setText("0");
	}

	/**
	 *
	 * Getter für den TabellenEditor
	 *
	 * @return
	 */
	public TableEditor getEditor() {
		return editor;
	}

	/**
	 * getter für den Wert in der Input-List an der Stelle i
	 *
	 * @param i
	 * @return
	 */
	public String getElementList(int i) {
		return inputList.get(i);
	}

	/**
	 * Getter für den Dateipfad der auszuführenden Datei
	 *
	 * @return
	 */
	public String getFilePath() {
		final FileDialog dialog = new FileDialog(shell);
		final String[] extensions = { "lst" };
		dialog.setFilterExtensions(extensions);
		return dialog.open();
	}

	/**
	 * Getter für die eingestellte Quarzfrequenz des Sliders
	 *
	 * @return
	 */
	public int getFrequency() {
		return getSlider().getSelection();
	}

	public Table getIntconRegTable() {
		return intconRegTable;
	}

	public boolean getLatchRA() {
		return btnLatchRA.getSelection();
	}

	public boolean getLatchRB() {
		return btnLatchRB.getSelection();
	}

	public int getListModelSize() {
		return inputList.size();
	}

	public Table getOptionsRegTable() {
		return optionsRegTable;
	}

	public Label getQuarzFreqVal() {
		return quarzFreqVal;
	}

	public int getSelectedLineIndex() {
		return codeTable.getSelectionIndex();
	}

	public Shell getShell() {
		return shell;
	}

	public Slider getSlider() {
		return slider;
	}

	public Table getSpecRegTable() {
		return specRegTable;
	}

	public Table getStatusRegTable() {
		return statusRegTable;
	}

	public String getTableEntry(int row, int column) {
		return getTableMem().getItem(row).getText(column);
	}

	public Table getTableMem() {
		return tableMem;
	}

	public Table getTablePortA() {
		return tablePortA;
	}

	public Table getTablePortB() {
		return tablePortB;
	}

	public int getValueIntconReg() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			builder.append(intconRegItem.getText(i));
		}
		int binary = 0;
		binary = Integer.parseInt(builder.toString(), 2);
		return binary;
	}

	public int getValueOptionReg() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			builder.append(optionsRegItem.getText(i));
		}
		int binary = 0;
		binary = Integer.parseInt(builder.toString(), 2);
		return binary;
	}

	public int getValuePortA() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 1; i < 9; i++) {
			builder.append(portAPinItem.getText(i));
		}
		int binary = 0;
		binary = Integer.parseInt(builder.toString(), 2);
		return binary;
	}

	public int getValuePortB() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 1; i < 9; i++) {
			builder.append(portBPinItem.getText(i));
		}
		int binary = 0;
		binary = Integer.parseInt(builder.toString(), 2);
		System.out.println("binary: " + binary);
		return binary;
	}

	public int getValueStatusReg() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			builder.append(statusRegItem.getText(i));
		}
		int binary = 0;
		binary = Integer.parseInt(builder.toString(), 2);
		return binary;
	}

	public void open() {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	public void remove_ElementListModel(int index) {
		inputList.remove(index);
	}

	public void selectCode(int index) {
		codeTable.setSelection(index);
	}

	public void setChangeIntconRegBits(Listener l) {
		getIntconRegTable().addListener(SWT.MouseDoubleClick, l);
	}

	public void setChangeOptionRegBits(Listener l) {
		getOptionsRegTable().addListener(SWT.MouseDoubleClick, l);
	}

	public void setChangePortABits(Listener l) {
		getTablePortA().addListener(SWT.MouseDoubleClick, l);
	}

	public void setChangePortBBits(Listener l) {
		getTablePortB().addListener(SWT.MouseDoubleClick, l);
	}

	public void setChangeStatusRegBits(Listener l) {
		getStatusRegTable().addListener(SWT.MouseDoubleClick, l);
	}

	public void setCodeInput() {
		for (int i = 0; i < inputList.size(); i++) {
			final int index = i;
			final TableItem item = new TableItem(codeTable, SWT.NONE);
			final TableEditor editor = new TableEditor(codeTable);
			final Button checkButton = new Button(codeTable, SWT.CHECK);
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

	public void setEditor(TableEditor editor) {
		this.editor = editor;
	}

	public void setErrorMsgs(String string) {

	}

	public void setFrequency(int i) {
		getSlider().setSelection(i);
		getQuarzFreqVal().setText(String.valueOf(i));
	}

	public void setFsrValue(String fsr) {
		itemRegFSR.setText(1, fsr);
	}

	public void setIntconRegTable(Table intconRegTable) {
		this.intconRegTable = intconRegTable;
	}

	public void setIntconValue(int s) {
		intconRegHexValue.setText(String.format("%02X", s));
		if (1 == (s & 1)) {
			intconRegItem.setText(7, "1");
		} else {
			intconRegItem.setText(7, "0");
		}
		if (2 == (s & 2)) {
			intconRegItem.setText(6, "1");
		} else {
			intconRegItem.setText(6, "0");
		}
		if (4 == (s & 4)) {
			intconRegItem.setText(5, "1");
		} else {
			intconRegItem.setText(5, "0");
		}
		if (8 == (s & 8)) {
			intconRegItem.setText(4, "1");
		} else {
			intconRegItem.setText(4, "0");
		}
		if (16 == (s & 16)) {
			intconRegItem.setText(3, "1");
		} else {
			intconRegItem.setText(3, "0");
		}
		if (32 == (s & 32)) {
			intconRegItem.setText(2, "1");
		} else {
			intconRegItem.setText(2, "0");
		}
		if (64 == (s & 64)) {
			intconRegItem.setText(1, "1");
		} else {
			intconRegItem.setText(1, "0");
		}
		if (128 == (s & 128)) {
			intconRegItem.setText(0, "1");
		} else {
			intconRegItem.setText(0, "0");
		}
	}

	public void setLaufzeit(long l) {
		laufzeitVal.setText(String.valueOf(l) + " ms");
	}

	public void setMemoryListener(Listener listener) {
		getTableMem().addListener(SWT.MouseDoubleClick, listener);
	}

	public void setNextStepListener(SelectionAdapter l) {
		btnWeiter.addSelectionListener(l);
	}

	public void setOpenFileListener(SelectionAdapter l) {
		openFileItem.addSelectionListener(l);
	}

	public void setOptionsRegTable(Table optionsRegTable) {
		this.optionsRegTable = optionsRegTable;
	}

	public void setOptionValue(int s) {
		optionRegHexValue.setText(String.format("%02X", s));
		if (1 == (s & 1)) {
			optionsRegItem.setText(7, "1");
		} else {
			optionsRegItem.setText(7, "0");
		}
		if (2 == (s & 2)) {
			optionsRegItem.setText(6, "1");
		} else {
			optionsRegItem.setText(6, "0");
		}
		if (4 == (s & 4)) {
			optionsRegItem.setText(5, "1");
		} else {
			optionsRegItem.setText(5, "0");
		}
		if (8 == (s & 8)) {
			optionsRegItem.setText(4, "1");
		} else {
			optionsRegItem.setText(4, "0");
		}
		if (16 == (s & 16)) {
			optionsRegItem.setText(3, "1");
		} else {
			optionsRegItem.setText(3, "0");
		}
		if (32 == (s & 32)) {
			optionsRegItem.setText(2, "1");
		} else {
			optionsRegItem.setText(2, "0");
		}
		if (64 == (s & 64)) {
			optionsRegItem.setText(1, "1");
		} else {
			optionsRegItem.setText(1, "0");
		}
		if (128 == (s & 128)) {
			optionsRegItem.setText(0, "1");
		} else {
			optionsRegItem.setText(0, "0");
		}
	}

	public void setPauseListener(SelectionAdapter l) {
		btnStop.addSelectionListener(l);
	}

	public void setPclValue(String pcl) {
		itemRegPCL.setText(1, pcl);
	}

	public void setPcValue(String programmCounter) {
		itemRegPC.setText(1, programmCounter);
	}

	public void setPortALabels(int t) {
		if (1 == (t & 1)) {
			portAPinItem.setText(8, "1");
		} else {
			portAPinItem.setText(8, "0");
		}
		if (2 == (t & 2)) {
			portAPinItem.setText(7, "1");
		} else {
			portAPinItem.setText(7, "0");
		}
		if (4 == (t & 4)) {
			portAPinItem.setText(6, "1");
		} else {
			portAPinItem.setText(6, "0");
		}
		if (4 == (t & 4)) {
			portAPinItem.setText(5, "1");
		} else {
			portAPinItem.setText(5, "0");
		}
		if (8 == (t & 8)) {
			portAPinItem.setText(4, "1");
		} else {
			portAPinItem.setText(4, "0");
		}
		if (16 == (t & 16)) {
			portAPinItem.setText(3, "1");
		} else {
			portAPinItem.setText(3, "0");
		}
		if (32 == (t & 32)) {
			portAPinItem.setText(2, "1");
		} else {
			portAPinItem.setText(2, "0");
		}
		if (128 == (t & 128)) {
			portAPinItem.setText(1, "1");
		} else {
			portAPinItem.setText(1, "0");
		}
	}

	public void setPortBLabels(int t) {
		if (1 == (t & 1)) {
			portBPinItem.setText(8, "1");
		} else {
			portBPinItem.setText(8, "0");
		}
		if (2 == (t & 2)) {
			portBPinItem.setText(7, "1");
		} else {
			portBPinItem.setText(7, "0");
		}
		if (4 == (t & 4)) {
			portBPinItem.setText(6, "1");
		} else {
			portBPinItem.setText(6, "0");
		}
		if (8 == (t & 8)) {
			portBPinItem.setText(5, "1");
		} else {
			portBPinItem.setText(5, "0");
		}
		if (16 == (t & 16)) {
			portBPinItem.setText(4, "1");
		} else {
			portBPinItem.setText(4, "0");
		}
		if (32 == (t & 32)) {
			portBPinItem.setText(3, "1");
		} else {
			portBPinItem.setText(3, "0");
		}
		if (64 == (t & 64)) {
			portBPinItem.setText(2, "1");
		} else {
			portBPinItem.setText(2, "0");
		}
		if (128 == (t & 128)) {
			portBPinItem.setText(1, "1");
		} else {
			portBPinItem.setText(1, "0");
		}
	}

	public void setQuarzFreqVal(Label quarzFreqVal) {
		this.quarzFreqVal = quarzFreqVal;
	}

	public void setResetListener(SelectionAdapter listener) {
		btnReset.addSelectionListener(listener);
	}

	public void setSlider(Slider slider) {
		this.slider = slider;
	}

	public void setSliderFrequencyListener(Listener l) {
		slider.addListener(SWT.Selection, l);
	}

	public void setSpecRegListener(Listener listener) {
		getSpecRegTable().addListener(SWT.MouseDoubleClick, listener);
	}

	public void setSpecRegTable(Table specRegTable) {
		this.specRegTable = specRegTable;
	}

	public void setStartProgramListener(SelectionAdapter l) {
		btnRun.addSelectionListener(l);
	}

	public void setStatusRegTable(Table statusRegTable) {
		this.statusRegTable = statusRegTable;
	}

	public void setStatusValue(int s) {
		statusRegHexValue.setText(String.format("%02X", s));
		// C-Bit
		if (1 == (s & 1)) {
			statusRegItem.setText(7, "1");
		} else {
			statusRegItem.setText(7, "0");
		}
		// DC-Bit
		if (2 == (s & 2)) {
			statusRegItem.setText(6, "1");
		} else {
			statusRegItem.setText(6, "0");
		}
		// Z-Bit
		if (4 == (s & 4)) {
			statusRegItem.setText(5, "1");
		} else {
			statusRegItem.setText(5, "0");
		}
		// PD-Bit
		if (8 == (s & 8)) {
			statusRegItem.setText(4, "1");
		} else {
			statusRegItem.setText(4, "0");
		}
		// T0-Bit
		if (16 == (s & 16)) {
			statusRegItem.setText(3, "1");
		} else {
			statusRegItem.setText(3, "0");
		}
		// RP0-Bit
		if (32 == (s & 32)) {
			statusRegItem.setText(2, "1");
		} else {
			statusRegItem.setText(2, "0");
		}
		// RP1-Bit
		if (64 == (s & 64)) {
			statusRegItem.setText(1, "1");
		} else {
			statusRegItem.setText(1, "0");
		}
		// IRP-Bit
		if (128 == (s & 128)) {
			statusRegItem.setText(0, "1");
		} else {
			statusRegItem.setText(0, "0");
		}
	}

	public void setSteps(int steps) {
		stepsVal.setText(String.valueOf(steps));
	}

	public void setTableEntry(int registerEntry, int row, int column) {
		getTableMem().getItem(row).setText(column, String.format("%02X", registerEntry));
	}

	public void setTableMem(Table tableMem) {
		this.tableMem = tableMem;
	}

	public void setTablePortA(Table tablePortA) {
		this.tablePortA = tablePortA;
	}

	public void setTablePortB(Table tablePortB) {
		this.tablePortB = tablePortB;
	}

	public void setTrisALabels(int t) {
		if (1 == (t & 1)) {
			portATrisItem.setText(8, "i");
		} else {
			portATrisItem.setText(8, "o");
		}
		if (2 == (t & 2)) {
			portATrisItem.setText(7, "i");
		} else {
			portATrisItem.setText(7, "o");
		}
		if (4 == (t & 4)) {
			portATrisItem.setText(6, "i");
		} else {
			portATrisItem.setText(6, "o");
		}
		if (8 == (t & 8)) {
			portATrisItem.setText(5, "i");
		} else {
			portATrisItem.setText(5, "o");
		}
		if (16 == (t & 16)) {
			portATrisItem.setText(4, "i");
		} else {
			portATrisItem.setText(4, "o");
		}
		if (32 == (t & 32)) {
			portATrisItem.setText(3, "i");
		} else {
			portATrisItem.setText(3, "o");
		}
		if (64 == (t & 64)) {
			portATrisItem.setText(2, "i");
		} else {
			portATrisItem.setText(2, "o");
		}
		if (128 == (t & 128)) {
			portATrisItem.setText(1, "i");
		} else {
			portATrisItem.setText(1, "o");
		}
	}

	public void setTrisBLabels(int t) {
		if (1 == (t & 1)) {
			portBTrisItem.setText(8, "i");
		} else {
			portBTrisItem.setText(8, "o");
		}
		if (2 == (t & 2)) {
			portBTrisItem.setText(7, "i");
		} else {
			portBTrisItem.setText(7, "o");
		}
		if (4 == (t & 4)) {
			portBTrisItem.setText(6, "i");
		} else {
			portBTrisItem.setText(6, "o");
		}
		if (8 == (t & 8)) {
			portBTrisItem.setText(5, "i");
		} else {
			portBTrisItem.setText(5, "o");
		}
		if (16 == (t & 16)) {
			portBTrisItem.setText(4, "i");
		} else {
			portBTrisItem.setText(4, "o");
		}
		if (32 == (t & 32)) {
			portBTrisItem.setText(3, "i");
		} else {
			portBTrisItem.setText(3, "o");
		}
		if (64 == (t & 64)) {
			portBTrisItem.setText(2, "i");
		} else {
			portBTrisItem.setText(2, "o");
		}
		if (128 == (t & 128)) {
			portBTrisItem.setText(1, "i");
		} else {
			portBTrisItem.setText(1, "o");
		}
	}

	public void setVisibilityButtons(boolean error, boolean next, boolean pause) {
		btnReset.setEnabled(next);
		btnRun.setEnabled(next);
		btnWeiter.setEnabled(next);
		btnStop.setEnabled(pause);
	}

	public void setWvalue(String s) {
		itemRegW.setText(1, s);
	}

	public void stackAdd(Integer[] integer) {
		final TableItem[] items = stackTable.getItems();
		for (int i = 0; i < integer.length; i++) {
			items[i].setText(String.valueOf(integer[i]));
		}
	}

	public void stackClear() {
		final TableItem[] items = stackTable.getItems();
		for (final TableItem tableItem : items) {
			tableItem.setText("");
		}
	}
}
