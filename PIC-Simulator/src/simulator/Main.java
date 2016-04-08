package simulator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class Main {

	private static Composite left;
	private static Composite centerUp;
	private static Composite right;
	private static Composite centerDown;
	private static Composite menuComp;
	protected static boolean comportSelected;
	
	static Button nextStepObnClick;
	static Button stopBtn;
	static Button runOnCLick;
	static Button reset;
	static boolean running;

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("PIC-Simulator");
		shell.setMaximized(true);
		shell.setLayout(new FormLayout());
		shell.setLayoutData(new FormData(100, 100));

		createMenu(shell);
		createComposites(shell);
//		createButtons(false);
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
				// Worker.comport.closeComConnection();
				// threadRunProgram.stop();
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	
	public static void createButtons(boolean flagForEnable) {
		Composite buttonComposite = new Composite(right, SWT.NONE);
		buttonComposite.setLayout(new GridLayout());
		buttonComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true));

		Button comport = new Button(right, SWT.CHECK);
		comport.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		comport.setText("Activate COM 3");
		comport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				

				if(comportSelected == false){
					comportSelected = true;
				} else {
					comportSelected = false;
				}
			}
		});

		reset = new Button(buttonComposite, SWT.PUSH);
		reset.setText("Reset");
		reset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		reset.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				CreateRegister.setRegOnReset();
//				CreateStateRegister.setOrClearOnReset();
//				CreateCodeTable.testTable.select(CreateCodeTable.startValue);
//				CreateCodeTable.testTable.showSelection();
//				Worker.clearallVariablesOnReset();				
			}
		});
		reset.setEnabled(flagForEnable);

		nextStepObnClick = new Button(buttonComposite, SWT.PUSH);
		nextStepObnClick.setText("Step");
		nextStepObnClick.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nextStepObnClick.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				Worker.workWithWorker(linesReadIn);
//				CreateStateRegister.setLaufzeit(Worker.cyclesInMicroSeconds*CreateStateRegister.currentQuarzDouble);
			}
		});
		nextStepObnClick.setEnabled(flagForEnable);


		stopBtn = new Button(buttonComposite, SWT.PUSH);
		stopBtn.setText("Pause");
		stopBtn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		stopBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO stop Btn
				running = false;
			}
		});
		stopBtn.setEnabled(flagForEnable);


		runOnCLick = new Button(buttonComposite, SWT.PUSH);
		runOnCLick.setText("Run");
		runOnCLick.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		runOnCLick.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(running) {
					running = false;
				} else {
					running = true;
				}
//				threadRunProgram = new ThreadTest();
//				threadRunProgram.start();
			}
		});
		runOnCLick.setEnabled(flagForEnable);
	}

	private static void createComposites(Shell parent) {
		// Menü oben Composite
		menuComp = new Composite(parent, SWT.BORDER);
		menuComp.setLayout(new FormLayout());
		FormData menuCompData = new FormData();
		menuCompData.top = new FormAttachment(0, 5);
		menuCompData.left = new FormAttachment(0, 5);
		menuCompData.right = new FormAttachment(100, -5);
		menuCompData.bottom = new FormAttachment(4, 0);
		menuComp.setLayoutData(menuCompData);
		
		//Linkes Drittel Composite
		left = new Composite(parent, SWT.BORDER);
//		left.setBackground(new Color(Display.getCurrent(), new RGB(100, 100, 100)));
		left.setLayout(new FormLayout());
		
		FormData leftCompformData = new FormData();
		leftCompformData.top = new FormAttachment(menuComp, 3); 
		leftCompformData.left = new FormAttachment(0, 5);
		leftCompformData.right = new FormAttachment(30, -5);
		leftCompformData.bottom = new FormAttachment(100, -5);
		left.setLayoutData(leftCompformData);

		// Composite rechtes Drittel
		right = new Composite(parent, SWT.BORDER);
		right.setLayout(new FormLayout());
		FormData rightCompFormData = new FormData();
		rightCompFormData.top = new FormAttachment(menuComp, 3);
		rightCompFormData.bottom = new FormAttachment(100, -5);
		rightCompFormData.left = new FormAttachment(70, 5);
		rightCompFormData.right = new FormAttachment(100, -5);
		right.setLayoutData(rightCompFormData);
//		right.setBackground(new Color(Display.getCurrent(), new RGB(0, 0, 200)));
		
		// Composite Mitte Oben
		centerUp = new Composite(parent, SWT.BORDER);
		centerUp.setLayout(new FormLayout());
		FormData centerUpCompformData = new FormData();
		centerUpCompformData.top = new FormAttachment(menuComp, 3);
		centerUpCompformData.left = new FormAttachment(left);
		centerUpCompformData.right = new FormAttachment(right);
		centerUpCompformData.bottom = new FormAttachment(80, -5);
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
//		centerDown.setBackground(new Color(Display.getCurrent(), new RGB(255, 0, 0)));
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
				// TODO FileDialog Datei öffnen
			};
		});

		// Optionen Manüpunkt erstellen
		MenuItem menuOptionsItem = CreateItem.getMenuItem(menu, SWT.CASCADE, "Optionen");
		Menu optionsMenu = CreateItem.getMenu(parent, SWT.DROP_DOWN);
		menuOptionsItem.setMenu(optionsMenu);
		MenuItem dataItem = CreateItem.getMenuItem(optionsMenu, SWT.PUSH, "Datenblatt öffnen..");

		dataItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO PIC Datei einfügen
				Program.launch(".\\PIC16F84A.pdf");
			}
		});
	}
}
