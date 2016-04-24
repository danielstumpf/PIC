package simulator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class Main {

	private static Composite left;
	private static Composite centerUp;
	private static Composite right;
	static Composite centerDown;
	private static Composite menuComp;
	protected static ArrayList<String> arrayLinesReadIn;

	static Button nextStepObnClick;
	static Button stopBtn;
	static Button runOnCLick;
	static Button reset;
	static boolean running;
	protected static RunThread threadRunProgram;

	public static void main(String[] args) {
		// Programmeinstieg, Erstellen des Fensters
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("PIC-Simulator");
		shell.setMaximized(true);
		shell.setLayout(new FormLayout());
		shell.setLayoutData(new FormData(100, 100));

		// Listener für Größenveränderung des Fensters.
		// Wird Fenstergröße verändert, wird layoutTables() aufgerufen
		shell.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				layoutTables();
			}
		});
		// Erstellen des Menüs, Fenster(Shell) wird übergeben
		createMenu(shell);
		// Composites erstellen (Aufteilung im Fenster)
		createComposites(shell);
		// Tabellenn erstellen
		createTables();

		// Register mit Ausgangsdaten befüllen
		CreateRegisters.initializeOnStartOrReset();
		Table specRegAttachTable = CreateRegisters.createAllStates(centerDown);
		// Buttons erstellen
		createButtons(specRegAttachTable);

		Composite portAComposite = PortA.createPortAReg(left);
		PortB.createPortBReg(left, portAComposite);
		Stack.createStack(left);

		//
		// CreateCodeTable.createTable(leftDown,linesReadIn);
		// CreateRegister.createTheRegister(leftUp);
		// CreateStateRegister.initializeOnSTartOrReset();
		// CreateStateRegister.createAllStates(centerUp);
		// PortA.createPortAReg(rightUp);
		// PortB.createPortBReg(rightUp);
		// CreateStateRegister.createQuarzFreqGroup(rightUp);
		// CreateStateRegister.createRuntimeGroup(rightUp);
		// Timer0.visuallTimer(rightUp);

		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event e) {
				// threadRunProgram.stop();
			}
		});

		shell.open();
		layoutTables();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	protected static void layoutTables() {
		Rectangle boundsLeft = left.getBounds();
		Rectangle boundsCenterUp = centerUp.getBounds();
		Rectangle boundsRight = right.getBounds();
		FillCodeTable.layoutTable(boundsCenterUp);
		FillRegister.layoutTable(boundsRight);
		PortA.layoutTable(boundsLeft);
		PortB.layoutTable(boundsLeft);
		Stack.layoutTable(boundsLeft);
	}

	private static void createTables() {
		FillRegister.createTable(right);
		FillCodeTable.createTable(centerUp);
	}

	

	public static void createButtons(Table attachTable) {
		Composite buttonComposite = new Composite(centerDown, SWT.NONE);
		buttonComposite.setLayout(new FormLayout());
		FormData buttonCompData = new FormData();
		buttonCompData.bottom = new FormAttachment(100, -5);
		buttonCompData.left = new FormAttachment(0, 5);
		buttonCompData.right = new FormAttachment(20, -3);
		buttonCompData.top = new FormAttachment(attachTable, 5);
		buttonComposite.setLayoutData(buttonCompData);

		reset = new Button(buttonComposite, SWT.PUSH);
		reset.setText("Reset");
		FormData resButtonData = new FormData();
		resButtonData.top = new FormAttachment(0, 3);
		resButtonData.left = new FormAttachment(0, 3);
		reset.setLayoutData(resButtonData);
		reset.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FillRegister.setRegOnReset();
				CreateRegisters.setOrClearOnReset();
				FillCodeTable.codeTable.select(FillCodeTable.startValue);
				FillCodeTable.codeTable.showSelection();
				Worker.clearallVariablesOnReset();
			}
		});

		nextStepObnClick = new Button(buttonComposite, SWT.PUSH);
		nextStepObnClick.setText("Step");
		FormData nxtStepBtnData = new FormData();
		nxtStepBtnData.top = new FormAttachment(reset, 3);
		nxtStepBtnData.left = new FormAttachment(0, 3);
		nextStepObnClick.setLayoutData(nxtStepBtnData);
		nextStepObnClick.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Worker.workWithWorker(arrayLinesReadIn);
				CreateRegisters.setLaufzeit(Worker.cyclesInMicroSeconds * CreateRegisters.currentQuarzDouble);
			}
		});

		stopBtn = new Button(buttonComposite, SWT.PUSH);
		stopBtn.setText("Pause");
		FormData stopBtnData = new FormData();
		stopBtnData.top = new FormAttachment(nextStepObnClick, 3);
		stopBtnData.left = new FormAttachment(0, 3);
		stopBtn.setLayoutData(stopBtnData);
		stopBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO stop Btn
				running = false;
			}
		});

		runOnCLick = new Button(buttonComposite, SWT.PUSH);
		runOnCLick.setText("Run");
		FormData runBtnData = new FormData();
		runBtnData.top = new FormAttachment(stopBtn, 3);
		runBtnData.left = new FormAttachment(0, 3);
		runOnCLick.setLayoutData(runBtnData);
		runOnCLick.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (running) {
					running = false;
				} else {
					running = true;
				}
				 threadRunProgram = new RunThread();
				 threadRunProgram.start();
			}
		});
		setButtons(false);
	}

	static void setButtons(boolean flag){
		nextStepObnClick.setEnabled(flag);
		stopBtn.setEnabled(flag);
		runOnCLick.setEnabled(flag);
		reset.setEnabled(flag);
	};
	
	private static void createComposites(Shell parent) {
		// Menü oben Composite
		menuComp = new Composite(parent, SWT.BORDER);
		menuComp.setLayout(new FormLayout());
		FormData menuCompData = new FormData();
		menuCompData.top = new FormAttachment(0, 5);
		menuCompData.left = new FormAttachment(0, 5);
		menuCompData.right = new FormAttachment(100, -5);
		menuCompData.bottom = new FormAttachment(5, 0);
		menuComp.setLayoutData(menuCompData);

		// Linkes Drittel Composite
		left = new Composite(parent, SWT.BORDER);
		// left.setBackground(new Color(Display.getCurrent(), new RGB(100, 100,
		// 100)));
		left.setLayout(new FormLayout());

		FormData leftCompformData = new FormData();
		leftCompformData.top = new FormAttachment(menuComp, 3);
		leftCompformData.left = new FormAttachment(0, 5);
		leftCompformData.right = new FormAttachment(20, -5);
		leftCompformData.bottom = new FormAttachment(100, -5);
		left.setLayoutData(leftCompformData);

		// Composite rechtes Drittel
		right = new Composite(parent, SWT.BORDER);
		right.setLayout(new FormLayout());
		FormData rightCompFormData = new FormData();
		rightCompFormData.top = new FormAttachment(menuComp, 3);
		rightCompFormData.bottom = new FormAttachment(100, -5);
		rightCompFormData.left = new FormAttachment(80, 5);
		rightCompFormData.right = new FormAttachment(100, -5);
		right.setLayoutData(rightCompFormData);
		// right.setBackground(new Color(Display.getCurrent(), new RGB(0, 0,
		// 200)));

		// Composite Mitte Oben
		centerUp = new Composite(parent, SWT.BORDER);
		centerUp.setLayout(new FormLayout());
		FormData centerUpCompformData = new FormData();
		centerUpCompformData.top = new FormAttachment(menuComp, 3);
		centerUpCompformData.left = new FormAttachment(left);
		centerUpCompformData.right = new FormAttachment(right);
		centerUpCompformData.bottom = new FormAttachment(65, -3);
		centerUp.setLayoutData(centerUpCompformData);

		// Composite Mitte Unten
		centerDown = new Composite(parent, SWT.BORDER);
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

	private static void createMenu(Shell parent) {
		// Datei-Menüpunkt erstellen
		Menu menu = CreateItem.getMenu(parent, SWT.BAR | SWT.LEFT_TO_RIGHT);
		parent.setMenuBar(menu);

		MenuItem fileItemMenu = CreateItem.getMenuItem(menu, SWT.CASCADE, "Datei");
		Menu fileMenu = CreateItem.getMenu(parent, SWT.DROP_DOWN);
		fileItemMenu.setMenu(fileMenu);
		MenuItem openItem = CreateItem.getMenuItem(fileMenu, SWT.PUSH, "Öffnen");
		openItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Dateiinhalt in Array einlesen
				arrayLinesReadIn = InputReader.getInput(parent);
				FillCodeTable.insertLines(arrayLinesReadIn);
				setButtons(true);
			};
		});

		// Optionen Menüpunkt erstellen
		MenuItem menuOptionsItem = CreateItem.getMenuItem(menu, SWT.CASCADE, "Optionen");
		Menu optionsMenu = CreateItem.getMenu(parent, SWT.DROP_DOWN);
		menuOptionsItem.setMenu(optionsMenu);
		MenuItem dataItem = CreateItem.getMenuItem(optionsMenu, SWT.PUSH, "Datenblatt öffnen..");

		dataItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Datenblatt anzeigen
				Path path = Paths.get("resources", "datasheet_16f84.pdf");
				Program.launch(path.toString());
			}
		});
	}
}
