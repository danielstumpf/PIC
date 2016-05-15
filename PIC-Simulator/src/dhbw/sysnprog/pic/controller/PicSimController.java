package dhbw.sysnprog.pic.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.swt.widgets.Display;

import dhbw.sysnprog.pic.model.PicSimModel;
import dhbw.sysnprog.pic.view.PicSimView;

/**
 * @author Daniel
 *
 */

public class PicSimController {
	private final PicSimView view;
	private final PicSimModel model;
	private boolean running;

	/**
	 * Konstruktor des Controllers. Aufruf bei Programmstart.
	 *
	 * @param view
	 * @param model
	 */
	public PicSimController(PicSimView view, PicSimModel model) {
		this.view = view;
		this.model = model;
		running = false;
		model.resetModel();
		valueOnPowerUp();
		new PicSimListener(this, view, model);
		ReloadGUI();
	}

	/**
	 * Ändert das entsprechende Bit im Intcon-Register
	 *
	 * @param column
	 *            Spalte, in welcher das Bit geändert werden soll
	 */
	protected void changeBitIntconReg(int column) {
		switch (column) {
		case 8: {
			final int temp = view.getValueIntconReg() & 0b00000001;
			if (temp == 0) {
				model.setIntcon(model.getIntcon() + 1);
			} else {
				model.setIntcon(model.getIntcon() - 1);
			}
			break;
		}
		case 7: {
			final int temp = view.getValueIntconReg() & 0b00000010;
			if (temp == 0) {
				model.setIntcon(model.getIntcon() + 2);
			} else {
				model.setIntcon(model.getIntcon() - 2);
			}
			break;
		}
		case 6: {
			final int temp = view.getValueIntconReg() & 0b00000100;
			if (temp == 0) {
				model.setIntcon(model.getIntcon() + 4);
			} else {
				model.setIntcon(model.getIntcon() - 4);
			}
			break;
		}
		case 5: {
			final int temp = view.getValueIntconReg() & 0b00001000;
			if (temp == 0) {
				model.setIntcon(model.getIntcon() + 8);
			} else {
				model.setIntcon(model.getIntcon() - 8);
			}
			break;
		}
		case 4: {
			final int temp = view.getValueIntconReg() & 0b00010000;
			if (temp == 0) {
				model.setIntcon(model.getIntcon() + 16);
			} else {
				model.setIntcon(model.getIntcon() - 16);
			}
			break;
		}
		case 3: {
			final int temp = view.getValueIntconReg() & 0b00100000;
			if (temp == 0) {
				model.setIntcon(model.getIntcon() + 32);
			} else {
				model.setIntcon(model.getIntcon() - 32);
			}
			break;
		}
		case 2: {
			final int temp = view.getValueIntconReg() & 0b01000000;
			if (temp == 0) {
				model.setIntcon(model.getIntcon() + 64);
			} else {
				model.setIntcon(model.getIntcon() - 64);
			}
			break;
		}
		case 1: {
			final int temp = view.getValueIntconReg() & 0b10000000;
			if (temp == 0) {
				model.setIntcon(model.getIntcon() + 128);
			} else {
				model.setIntcon(model.getIntcon() - 128);
			}
			break;
		}
		default: {
			break;
		}
		}
		ReloadGUI();
	}

	/**
	 * Ändert das entsprechende Bit im Options-Register
	 *
	 * @param column
	 *            Spalte, in welcher das Bit geändert werden soll
	 */
	protected void changeBitOptionReg(int column) {
		switch (column) {
		case 8: {
			final int temp = view.getValueOptionReg() & 0b00000001;
			if (temp == 0) {
				model.setOption(model.getOption() + 1);
			} else {
				model.setOption(model.getOption() - 1);
			}
			break;
		}
		case 7: {
			final int temp = view.getValueOptionReg() & 0b00000010;
			if (temp == 0) {
				model.setOption(model.getOption() + 2);
			} else {
				model.setOption(model.getOption() - 2);
			}
			break;
		}
		case 6: {
			final int temp = view.getValueOptionReg() & 0b00000100;
			if (temp == 0) {
				model.setOption(model.getOption() + 4);
			} else {
				model.setOption(model.getOption() - 4);
			}
			break;
		}
		case 5: {
			final int temp = view.getValueOptionReg() & 0b00001000;
			if (temp == 0) {
				model.setOption(model.getOption() + 8);
			} else {
				model.setOption(model.getOption() - 8);
			}
			break;
		}
		case 4: {
			final int temp = view.getValueOptionReg() & 0b00010000;
			if (temp == 0) {
				model.setOption(model.getOption() + 16);
			} else {
				model.setOption(model.getOption() - 16);
			}
			break;
		}
		case 3: {
			final int temp = view.getValueOptionReg() & 0b00100000;
			if (temp == 0) {
				model.setOption(model.getOption() + 32);
			} else {
				model.setOption(model.getOption() - 32);
			}
			break;
		}
		case 2: {
			final int temp = view.getValueOptionReg() & 0b01000000;
			if (temp == 0) {
				model.setOption(model.getOption() + 64);
			} else {
				model.setOption(model.getOption() - 64);
			}
			break;
		}
		case 1: {
			final int temp = view.getValueOptionReg() & 0b10000000;
			if (temp == 0) {
				model.setOption(model.getOption() + 128);
			} else {
				model.setOption(model.getOption() - 128);
			}
			break;
		}
		default: {
			break;
		}
		}
		ReloadGUI();
	}

	/**
	 * Ändert das entsprechende Bit von PortA
	 *
	 * @param column
	 *            Spalte, in der das Bit geändert werden soll
	 */
	void changeBitPortA(int column) {
		switch (column) {
		case 8: {
			final int temp = view.getValuePortA() & 0b00000001;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 1);
			} else {
				model.setPortA(model.getPortA() - 1);
			}
			break;
		}
		case 7: {
			final int temp = view.getValuePortA() & 0b00000010;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 2);
			} else {
				model.setPortA(model.getPortA() - 2);
			}
			break;
		}
		case 6: {
			final int temp = view.getValuePortA() & 0b00000100;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 4);
			} else {
				model.setPortA(model.getPortA() - 4);
			}
			break;
		}
		case 5: {
			final int temp = view.getValuePortA() & 0b00001000;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 8);
			} else {
				model.setPortA(model.getPortA() - 8);
			}
			break;
		}
		case 4: {
			final int temp = view.getValuePortA() & 0b00010000;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 16);
			} else {
				model.setPortA(model.getPortA() - 16);
			}
			break;
		}
		case 3: {
			final int temp = view.getValuePortA() & 0b00100000;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 32);
			} else {
				model.setPortA(model.getPortA() - 32);
			}
			break;
		}
		case 2: {
			final int temp = view.getValuePortA() & 0b01000000;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 64);
			} else {
				model.setPortA(model.getPortA() - 64);
			}
			break;
		}
		case 1: {
			final int temp = view.getValuePortA() & 0b10000000;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 128);
			} else {
				model.setPortA(model.getPortA() - 128);
			}
			break;
		}
		default: {
			break;
		}
		}
		model.registerArray[5] = model.getPortA();
		ReloadGUI();
	}

	/**
	 * Ändert das entsprechende Bit im Status-Register
	 *
	 * @param column
	 *            Spalte, in welcher das Bit geändert werden soll
	 */
	protected void changeBitStatusReg(int column) {
		switch (column) {
		case 8: {
			final int temp = view.getValueStatusReg() & 0b00000001;
			if (temp == 0) {
				model.setStatus(model.getStatus() + 1);
			} else {
				model.setStatus(model.getStatus() - 1);
			}
			break;
		}
		case 7: {
			final int temp = view.getValueStatusReg() & 0b00000010;
			if (temp == 0) {
				model.setStatus(model.getStatus() + 2);
			} else {
				model.setStatus(model.getStatus() - 2);
			}
			break;
		}
		case 6: {
			final int temp = view.getValueStatusReg() & 0b00000100;
			if (temp == 0) {
				model.setStatus(model.getStatus() + 4);
			} else {
				model.setStatus(model.getStatus() - 4);
			}
			break;
		}
		case 5: {
			final int temp = view.getValueStatusReg() & 0b00001000;
			if (temp == 0) {
				model.setStatus(model.getStatus() + 8);
			} else {
				model.setStatus(model.getStatus() - 8);
			}
			break;
		}
		case 4: {
			final int temp = view.getValueStatusReg() & 0b00010000;
			if (temp == 0) {
				model.setStatus(model.getStatus() + 16);
			} else {
				model.setStatus(model.getStatus() - 16);
			}
			break;
		}
		case 3: {
			final int temp = view.getValueStatusReg() & 0b00100000;
			if (temp == 0) {
				model.setStatus(model.getStatus() + 32);
			} else {
				model.setStatus(model.getStatus() - 32);
			}
			break;
		}
		case 2: {
			final int temp = view.getValueStatusReg() & 0b01000000;
			if (temp == 0) {
				model.setStatus(model.getStatus() + 64);
			} else {
				model.setStatus(model.getStatus() - 64);
			}
			break;
		}
		case 1: {
			final int temp = view.getValueStatusReg() & 0b10000000;
			if (temp == 0) {
				model.setStatus(model.getStatus() + 128);
			} else {
				model.setStatus(model.getStatus() - 128);
			}
			break;
		}
		default: {
			break;
		}
		}
		ReloadGUI();
	}

	/**
	 * Ändert das entsprechende Bit von PortB
	 *
	 * @param column
	 *            Spalte, in der das Bit geändert werden soll
	 */
	void changeTheRegisterFromPortB(int column) {
		switch (column) {
		case 8: {
			final int temp = view.getValuePortB() & 0b00000001;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 1);
			} else {
				model.setPortB(model.getPortB() - 1);
			}

			model.setBit(4, 0xb);

			model.doInterrupt(1);
			break;
		}
		case 7: {
			final int temp = view.getValuePortB() & 0b00000010;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 2);
			} else {
				model.setPortB(model.getPortB() - 2);
			}
			break;
		}
		case 6: {
			final int temp = view.getValuePortB() & 0b00000100;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 4);
			} else {
				model.setPortB(model.getPortB() - 4);
			}
			break;
		}
		case 5: {
			final int temp = view.getValuePortB() & 0b00001000;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 8);
			} else {
				model.setPortB(model.getPortB() - 8);
			}
			break;
		}
		case 4: {
			final int temp = view.getValuePortB() & 0b00010000;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 16);
			} else {
				model.setPortB(model.getPortB() - 16);
			}
			model.setBit(7, 0xb);
			model.setBit(3, 0xb);
			model.setBit(0, 0xb);
			model.doInterrupt(3);
			break;
		}
		case 3: {
			final int temp = view.getValuePortB() & 0b00100000;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 32);
			} else {
				model.setPortB(model.getPortB() - 32);
			}
			model.setBit(7, 0xb);
			model.setBit(3, 0xb);
			model.setBit(0, 0xb);
			model.doInterrupt(3);
			break;
		}
		case 2: {
			final int temp = view.getValuePortB() & 0b01000000;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 64);
			} else {
				model.setPortB(model.getPortB() - 64);
			}
			model.setBit(7, 0xb);
			model.setBit(3, 0xb);
			model.setBit(0, 0xb);
			model.doInterrupt(3);
			break;
		}
		case 1: {
			final int temp = view.getValuePortB() & 0b10000000;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 128);
			} else {
				model.setPortB(model.getPortB() - 128);
			}
			model.setBit(7, 0xb);
			model.setBit(3, 0xb);
			model.setBit(0, 0xb);
			model.doInterrupt(3);
			break;
		}
		default: {
			break;
		}
		}
		model.registerArray[6] = model.getPortB();
		ReloadGUI();
	}

	/**
	 * true = Counter-Mode false = Timer-Mode
	 */
	private void chooseMode() {
		if (model.checkBitSet(5, 0x81)) {
			model.setMode(true);
		} else {
			model.setMode(false);
		}
	}

	private void counterMode() {
		// TODO Auto-generated method stub

	}

	/**
	 * Erhöht die Anzahl der durchgeführten Schritte um 1
	 */
	public void countSteps() {
		model.setSteps();
	}

	/**
	 * Geht durch die kompletten Inhalte und entfernt unrelevante Codezeilen
	 * sodass später nur Zeilen mit ausführbarem Befehlscode enthalten sind
	 */
	public void filterCode() {
		for (int i = 0; i < view.getListModelSize(); i++) {
			final String temp = view.getElementList(i);
			if (temp.startsWith("     ")) {
				view.remove_ElementListModel(i);
				i--;
			}
		}
	}

	/**
	 * @return die aktuelle Frequenz, mit der das Programm läuft
	 */
	public int getFrequency() {
		return model.getTakt();
	}

	/**
	 * @return boolean gibt an, ob Programm läuft oder pausiert ist
	 */
	public boolean getRunning() {
		return running;
	}

	/**
	 * Lädt die Datei und fügt sie in die Inputlist ein
	 *
	 * @param filePath
	 *            Pfad zur Datei, welche das Programm enthält
	 */
	void loadFile(String filePath) {
		running = false;
		view.clearList();
		model.resetModel();
		valueOnPowerUp();

		ReloadGUI();

		BufferedReader in;
		String zeile = null;
		model.setProgramFilePath(filePath);

		try {
			in = new BufferedReader(new FileReader(model.getProgramFilePath()));
			try {
				while ((zeile = in.readLine()) != null) {
					try {
						view.inputList.add(zeile);
					} catch (final Exception e1) {
						System.out.println(e1);
					}
				}

			} catch (final IOException e) {
				e.printStackTrace();
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		view.setVisibilityButtons(false, true, true);
		if (view.getListModelSize() == 0) {
			view.setErrorMsgs("Kein Programm enthalten.");
		}
	}

	/**
	 * Inhalte auf der Benutzeroberfläche werden aktualisiert
	 */
	public void ReloadGUI() {
		// aktuell ausgeführten Code markieren
		view.selectCode(model.getProgrammCounter());

		// W-Register auf der Benutzeroberfläche setzen
		view.setWvalue(String.format("%02X", model.registerW & 0xFF));
		final int registerEntry = model.getRegisterEntry(4);

		view.setFsrValue(String.format("%02X", registerEntry & 0xFF));

		// Speichertabelle wird mit Werten aus dem Register-Array gesetzt
		ReloadTable();

		// Status, Intcon, Option setzen
		view.setStatusValue(model.getStatus());
		view.setIntconValue(model.getIntcon());
		view.setOptionValue(model.getOption());

		// Ports werden mit den Werten aus dem Model gesetzt
		view.setPortALabels(model.getRegisterEntry(5));
		view.setPortBLabels(model.getRegisterEntry(6));
		view.setTrisALabels(model.getRegisterEntry(0x85));
		view.setTrisBLabels(model.getRegisterEntry(0x86));

		// Laufzeit wird aktualisiert
		view.setLaufzeit(model.getRunningTime());

		// Programmschritte werden aktualisiert
		view.setSteps(model.getSteps());
		view.setPclValue(String.format("%02X", model.getProgrammCounter() & 0xFF));
		view.setPcValue(String.format("%04X", model.getProgrammCounter()));

		view.stackClear();

		// Stack wird aktualisiert
		if (!model.StackPC.isEmpty()) {
			final Integer[] temp = new Integer[model.getStackSize()];
			model.StackPC.toArray(temp);
			view.stackClear();
			final int size = model.getStackSize();
			for (int i = 0; i < size; i++) {
				view.stackAdd(temp);
			}
		}
	}

	/**
	 * Aktualisierung der Speichertabelle mit Werten aus dem Register-Array
	 */
	public void ReloadTable() {
		int index = 0, row = 0, column = 1;
		while (index < 256) {
			while (column < 9) {
				view.setTableEntry(model.getRegisterEntry(index), row, column);
				column++;
				index++;
			}
			column = 1;
			row++;
		}
	}

	/**
	 * Ausführen aller Funktionen, d.h. Erzeugung eines neuen Threads und
	 * Einstieg in das Programm.
	 */
	public void runAllFunctions() {
		if (view.getListModelSize() > 0) {

			model.setStartTime(System.currentTimeMillis());
			start();

			setRunning(true);

			view.setVisibilityButtons(false, true, true);
			final Thread t1 = new Thread(new PicSimControllerThread(this));

			t1.start();
		} else {
			view.setErrorMsgs("Kein Programm geöffnet.");
			setRunning(false);
		}

	}

	/**
	 * Ausführen einer Funktion
	 */
	public void runOneFunction() {
		if (view.getListModelSize() > 0) {

			model.setStartTime(System.currentTimeMillis());
			start();

			setRunning(true);

			view.setVisibilityButtons(false, true, true);
			final Thread t1 = new Thread(new PicSimControllerOneThread(this));

			t1.start();
			setRunning(false);
		} else {
			view.setErrorMsgs("Kein Programm geöffnet.");
			setRunning(false);
		}
	}

	/**
	 * @param s
	 *            setzt das Programm auf laufend oder nicht laufend
	 */
	public void setRunning(boolean s) {
		running = s;
	}

	/**
	 * Setzt die aktuelle Laufzeit. Dabei wird die Differenz der aktuellen Zeit
	 * zur Startzeit gesetzt.
	 */
	public void setTime() {
		model.setRunningTime((System.currentTimeMillis()) - (model.getStartTime()));
	}

	/**
	 * Geht durch die kompletten Inhalte und speichert den Befehlscode in der
	 * Codeliste ab
	 */
	private void start() {
		for (int i = 0; i < view.getListModelSize(); i++) {
			model.checkCode(view.getElementList(i));
		}
	}

	/**
	 * Startet das geladene Programm. Vor Ausführung des nächsten Befehls wird
	 * auf Latch-Funktion geprüft und ggf. die Werte von RA und RB auf 0
	 * gesetzt. Vor Ausführung wird außerdem der Thread um die Dauer des taktes
	 * pausiert.
	 *
	 * @param takt
	 * @throws InterruptedException
	 */
	public void start_programm(int takt) throws InterruptedException {
		if (!view.getLatchRA()) {
			model.resetRA();
		}
		if (!view.getLatchRB()) {
			model.resetRB();
		}
		if (model.getProgrammCounter() == model.codeList.size()) {
			Thread.sleep(takt);
			model.setProgramCounter(0);
			startFunction();
		} else {
			Thread.sleep(takt);
			startFunction();
		}
		if (model.getMode()) {
			counterMode();

		} else {
			timerMode();
		}
		chooseMode();
	}

	/**
	 * Startet die aktuelle Funktion
	 *
	 * @throws InterruptedException
	 */
	public void startFunction() throws InterruptedException {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < view.breakpointList.size(); i++) {
					if (view.getSelectedLineIndex() == (view.breakpointList.get(i) - 1)) {
						running = false;
					}
				}

				try {
					model.doAction(model.codeList.get(model.getProgrammCounter()));
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				model.setProgramCounter(model.getProgrammCounter() + 1);
				ReloadGUI();
			}
		});
	}

	/**
	 * Wenn Timer-Mode gesetzt ist, wird der Prescaler ausgewertet und
	 * entsprechend des Wertes hochgezählt oder der Zähler erhöht. Je nachdem
	 * wie der Prescaler gesetzt ist, wird bei entsprechendem Wert ein Interrupt
	 * gesetzt.
	 */
	public void timerMode() {
		if (!model.checkBitSet(3, 0x81)) {
			final int prescaler = model.registerArray[0x81] & 0b00000111;

			switch (prescaler) {
			case 0:
				if (model.getPrescaler() == 1) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.doInterrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;

			case 1:

				if (model.getPrescaler() == 2) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.doInterrupt(2);

					}
					model.setPrescaler(1);

				} else {
					model.incrPrescaler();

				}
				break;
			case 2:
				if (model.getPrescaler() == 4) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.doInterrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			case 3:
				if (model.getPrescaler() == 8) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.doInterrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			case 4:
				if (model.getPrescaler() == 16) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.doInterrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			case 5:
				if (model.getPrescaler() == 32) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.doInterrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			case 6:
				if (model.getPrescaler() == 64) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.doInterrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			case 7:
				if (model.getPrescaler() == 128) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);

					if (model.registerArray[1] == 0) {
						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.doInterrupt(2);
					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			default: {
				break;
			}
			}
		}
	}

	/**
	 * Vorbelegung der Registereinträge beim Start des Programms
	 */
	void valueOnPowerUp() {
		model.setRegisterEntry(0x3, 24);
		model.setRegisterEntry(0x81, 255);
		model.setRegisterEntry(0x83, 24);
		model.setRegisterEntry(0x85, 31);
		model.setRegisterEntry(0x86, 255);
	}

	/**
	 * Schreibt die Werte aus der Speichertabelle in das Registerarray im Model
	 */
	public void writeTableToRegister() {
		if (running == false) {
			for (int i = 0; i <= 31; i++) {
				for (int j = 0; j <= 7; j++) {
					final int adress = ((i * 8) + j);
					final int value = Integer.parseInt(view.getTableEntry(i, j + 1), 16);
					if (model.getRegisterEntry(adress) != value) {
						model.registerArray[adress] = value;
						System.out.println("Adresse: " + adress + " Aktualisierter Wert: " + value);
					}
				}
			}
		} else {
			view.setErrorMsgs("Direkte Registeränderung nicht während der Laufzeit möglich.");
		}
	}

	/**
	 * Setzt bestimmten Wert im Speicher
	 *
	 * @param adress
	 *            Integerwert derSpeicheradresse
	 * @param value
	 *            zu setzender Wert im Speicher
	 */
	public void writeToRegister(int adress, int value) {
		model.setRegisterEntry(adress, value);
		ReloadGUI();
	}
}
