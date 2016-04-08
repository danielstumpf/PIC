package simulator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class Main {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("PIC-Simulator");
		shell.setMaximized(true);
		shell.setLayout(new GridLayout(3, false));
		shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createMenu(shell);
		// createComposites(shell);
		// createButtons(rightDown, false);
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

	private static void createMenu(Shell parent) {
		Menu menu = new Menu(parent, SWT.BAR | SWT.LEFT_TO_RIGHT);
		parent.setMenuBar(menu);
		MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText("Datei");
		menuItem.setMenu(createItemsForMenu(parent));
		// Help Aufruf der PDF
		MenuItem menuHelp = new MenuItem(menu, SWT.PUSH);
		menuHelp.setText("Help");
		menuHelp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Program.launch(".\\PIC16F84A.pdf");
			}
		});
	}

	public static Menu createItemsForMenu(Shell parent) {
		Menu fileMenu = new Menu(parent, SWT.DROP_DOWN);
		MenuItem openFile = CreateItems.getMenuPoint(fileMenu, "Öffnen");
		openFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				FileDialog fd = new FileDialog(parent);
				String filename = fd.open();
				System.out.println(filename);
				// OpenDocument.openDocument(parent, linesReadIn);
				// stopBtn.setEnabled(true);
				// nextStepObnClick.setEnabled(true);
				// runOnCLick.setEnabled(true);
				// reset.setEnabled(true);
			}
		});
		return fileMenu;
	}

}
