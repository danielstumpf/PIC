package dhbw.sysnprog.pic.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import dhbw.sysnprog.pic.model.PicSimModel;
import dhbw.sysnprog.pic.serial.PicSimSerialController;
import dhbw.sysnprog.pic.view.PicSimView;
import simulator.CreateRegisters;
import simulator.PortA;
import simulator.PortB;
import simulator.Worker;

public class PicSimController {
	private PicSimView view;
	private PicSimModel model;
	private boolean running;

	private PicSimSerialController serial;
	private boolean serialConnected = false;

	public PicSimController(PicSimView view, PicSimModel model) {
		this.view = view;
		this.model = model;
		this.running = false;
		model.reset_model();
		addListener();
		valueOnPowerUp();
		//// searchForComPorts();
		ReloadGUI();
	}

	private void valueOnPowerUp() {
		/* Vorbelegung einiger Werte */
		model.setRegisterEntry(0x3, 24);
		model.setRegisterEntry(0x81, 255);
		model.setRegisterEntry(0x83, 24);
		model.setRegisterEntry(0x85, 31);
		model.setRegisterEntry(0x86, 255);
	}

	// private void searchForComPorts() {
	// @SuppressWarnings("rawtypes")
	// Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
	// if (portIdentifiers != null) {
	// while (portIdentifiers.hasMoreElements()) {
	// CommPortIdentifier pid = (CommPortIdentifier) portIdentifiers
	// .nextElement();
	// if (pid.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	// view.initializeComMenu(pid.getName());
	//
	// System.out.println(pid.getName());
	// }
	// }
	// } else {
	// view.set_ErrorMsgs("Keine COM-Ports gefunden.");
	// }
	//
	// }

	private void addListener() {
		// TODO
		view.setResetListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				model.reset_model();
				valueOnPowerUp();
				ReloadGUI();
			}
		});

		view.setNextStepListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				run_one_function();
			}
		});
		view.setStartProgramListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				run_all_functions();
			}
		});
		view.setPauseListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					if (serialConnected) {
						serial.close();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				serialConnected = false;
				view.setSerialDisconnected();
				set_running(false);
			}
		});
		// view.setSpeichernRegisterListener(new SpeichernRegisterListener());
		view.setOpenFileListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String[] extensions = { "*.lst" };
				FileDialog dialog = new FileDialog(view.getShell());
				dialog.setFilterExtensions(extensions);
				String filePath = dialog.open();
				loadFile(filePath);
				filterCode();
				view.setCodeInput();
			}
		});

		view.setSliderFrequencyListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				view.getQuarzFreqVal().setText(String.valueOf(view.getSlider().getSelection()));
				model.setTakt(view.getFrequency());
			}
		});

		view.setSpecRegListener(new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = view.getSpecRegTable().getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = view.getTableMem().getTopIndex();
				while (index < view.getSpecRegTable().getItemCount()) {
					boolean visible = false;
					final TableItem item = view.getSpecRegTable().getItem(index);
					final int column = 1;
					Rectangle rect = item.getBounds(column);
					if (rect.contains(pt)) {
						final Text text = new Text(view.getSpecRegTable(), SWT.NONE);
						Listener textListener = new Listener() {
							public void handleEvent(final Event e) {
								switch (e.type) {
								case SWT.FocusOut:
									item.setText(column, text.getText());
									text.dispose();
									break;
								case SWT.Traverse:
									switch (e.detail) {
									case SWT.TRAVERSE_RETURN:
										item.setText(column, text.getText());
										if (item.getText(column).length() == 1) {
											item.setText(column, "0" + item.getText(column));
										}
									case SWT.TRAVERSE_ESCAPE:
										text.dispose();
										e.doit = false;
									}
								}
								writeTableToRegister();
								ReloadGUI();
							}
						};
						text.addListener(SWT.FocusOut, textListener);
						text.addListener(SWT.Traverse, textListener);
						view.getEditor().setEditor(text, item, column);
						text.setText(item.getText(column));
						text.selectAll();
						text.setFocus();
						text.setTextLimit(2);
						return;
					}
					if (!visible && rect.intersects(clientArea)) {
						visible = true;
					}
					if (!visible)
						return;
					index++;
				}
			}
		});

		view.setChangePortABits(new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				final TableItem item = view.getTablePortA().getItem(1);
				for (int i = 1; i < view.getTablePortA().getColumnCount(); i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i;
						changeBitPortA(column);
					}
				}
			}
		});

		view.setChangePortBBits(new Listener() {
			public void handleEvent(Event event) {
				Point pt = new Point(event.x, event.y);
				final TableItem item = view.getTablePortB().getItem(1);
				for (int i = 1; i < view.getTablePortB().getColumnCount(); i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i;
						changeTheRegisterFromPortB(column);
					}
				}
			}
		});

		view.setMemoryListener(new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = view.getTableMem().getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = view.getTableMem().getTopIndex();
				while (index < view.getTableMem().getItemCount()) {
					boolean visible = false;
					final TableItem item = view.getTableMem().getItem(index);
					for (int i = 1; i < view.getTableMem().getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final Text text = new Text(view.getTableMem(), SWT.NONE);
							Listener textListener = new Listener() {
								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										item.setText(column, text.getText());
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
											// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
										}
									}
									writeTableToRegister();
									ReloadGUI();
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							view.getEditor().setEditor(text, item, i);
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

		// TODO view.setComPortChange(new ComPortChange());
	}

	public void reloadSerialViaThread() {
		Thread t1 = new Thread(new SerialThread(this));
		t1.run();
	}

	public void reloadSerial() {
		if (serialConnected) {
			try {
				serial.sendRS232();
				ArrayList<Integer> readSerial = new ArrayList<Integer>();
				readSerial = serial.read();
				if (readSerial.size() == 2) {
					model.setRegisterEntryOneBit(5, readSerial.get(0) - 32);
					model.setRegisterEntryOneBit(6, readSerial.get(1));
					System.out.println("empfangen portA:" + readSerial.get(0) + "portB" + readSerial.get(1));
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		try {
			// System.out.println(serial.read());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean get_running() {
		return running;
	}

	public void set_running(boolean s) {

		running = s;
	}

	public int get_Frequency() {
		return model.getTakt();
	}

	/* ARRAY FÜR DAS REGISTER */

	public void start_programm(int takt) throws InterruptedException {
		/*
		 * Überprüfung ob Ende des Programms erreicht wird kann am Ende gelöscht
		 * werden !!
		 */

		if (model.getProgrammCounter() == model.codeList.size()) {
			Thread.sleep(takt);
			model.setProgramCounter(0);
			start_function();
		} else {
			Thread.sleep(takt);
			start_function();
		}
		// Timermode, Countermode Ã¼berprÃ¼fen und setzen
		chooseMode();

	}

	public void start_function() throws InterruptedException {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				for (int i = 0; i < view.breakpointList.size(); i++) {
					if (view.getSelectedLineIndex() == view.breakpointList.get(i) - 1) {
						running = false;
					}
				}

				try {
					model.doAction(model.codeList.get(model.getProgrammCounter()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				model.setProgramCounter(model.getProgrammCounter() + 1);
				if (model.getMode()) {
					countermode();

				} else {
					timermode();
				}
				ReloadGUI();
			}
		});
	}

	public void ReloadGUI() {
		/* Erweiterungen aktualisieren */
		ReloadElements();
		/* aktuell ausgeführten Code markieren */
		view.selectCode(model.getProgrammCounter());
		view.setWvalue(String.format("%02X", model.registerW & 0xFF));

		/* Aktualisieren der Tabelle mit den Werten aus Register_Array */
		ReloadTable();

		/* Status setzen */
		view.setStatusValue(model.getStatus());
		view.setIntconValue(model.getIntcon());
		view.setOptionValue(model.getOption());
		/* Ports aktualisieren */
		view.setPortALabels(model.getRegisterEntry(5));
		view.setPortBLabels(model.getRegisterEntry(6));
		view.setTrisALabels(model.getRegisterEntry(0x85));
		view.setTrisBLabels(model.getRegisterEntry(0x86));

		/* Wuerfel setzen */
		// view.setWuerfel(false, false, false, false, false, false, false,
		// false,
		// false);

		/* Laufzeit aktualisieren */
		view.setLaufzeit(model.getRunningTime());

		/* Programmschritte aktualisieren */
		view.setSteps(model.getSteps());

		/* Serielle Schnittstelle */
		// TODO
		// reloadSerialViaThread();
		// if (serialConnected) {
		// view.panel_portstatus.setBackground(Color.green);
		// } else {
		// view.panel_portstatus.setBackground(Color.decode("#f0f0f0"));
		// }
		/*
		 * int m; for(m=0; m < view.breakpoint_list.size(); m++){
		 * 
		 * view.addBreakpoints(view.breakpoint_list.get(m));
		 * 
		 * }
		 */

		/* stack aktualisieren */
		if (!model.STACK.isEmpty()) {
			Integer[] temp = new Integer[model.STACK.size()];
			model.STACK.toArray(temp);
			view.stackClear();
			for (int i = 0; i < model.STACK.size(); i++) {
				view.stackAdd(temp[i]);
			}
		}
	}

	public void ReloadElements() {
		// TODO Elemente aktualisieren LEDs etc
	}

	public void ReloadTable() {
		int m1 = 0, row = 0, column = 1;
		while (m1 < 256) {
			while (column < 9) {
				/* Tabelle bekommt Werte aus Array zugewiesen */
				view.setTableEntry(model.getRegisterEntry(m1), row, column);
				column++;
				m1++;
			}
			column = 1;
			row++;
		}
	}

	public void run_one_function() {

		Thread t1 = new Thread(new PicSimControllerThread_Once(this));
		t1.start();
		// TODO serielle verbindung bei einem schritt
	}

	private void start() {
		int i;
		for (i = 0; i < view.getListModelSize(); i++) {
			model.checkCode(view.getElementListModel(i));
		}
	}

	public void filterCode() {
		/* Programmcode nach relevanten Zeilen filtern */
		int i;
		for (i = 0; i < view.getListModelSize(); i++) {
			String temp = view.getElementListModel(i);
			if (temp.startsWith("     ")) {
				view.remove_ElementListModel(i);
				i--;
			}
		}
	}

	public void run_all_functions() {

		if (view.getListModelSize() > 0) {

			model.setStartTime(System.currentTimeMillis());
			start();

			set_running(true);

			// TODO COMPORT
			// if (view.portComCheck()) {
			// serial = new PicSimSerialController(model);
			// if (serial.open(model.getDefaultSerialPort())) {
			// serialConnected = true;
			// view.setSerialConnected();
			// } else {
			// serialConnected = false;
			// view.setSerialDisconnected();
			// }
			// }

			view.setVisibilityButtons(false, true, true);
			Thread t1 = new Thread(new PicSimControllerThread(this));

			t1.start();

		} else {
			view.setErrorMsgs("Kein Programm geöffnet.");
			set_running(false);
		}

	}

	public void chooseMode() {
		if (model.checkBitSet(5, 0x81)) {/* CounterMode */

			model.setMode(true);
		} else {/* TimerMode */

			model.setMode(false);
		}
	}

	public void timermode() {
		if (model.checkBitSet(3, 0x81)) {
			model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);

		} else {

			int prescaler = model.registerArray[0x81] & 0b00000111;
			switch (prescaler) {

			case 0:
				if (model.getPrescaler() == 2) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.do_interrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;

			case 1:

				if (model.getPrescaler() == 4) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.do_interrupt(2);

					}
					model.setPrescaler(1);

				} else {
					model.incrPrescaler();

				}
				break;
			case 2:
				if (model.getPrescaler() == 8) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.do_interrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			case 3:
				if (model.getPrescaler() == 16) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.do_interrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			case 4:
				if (model.getPrescaler() == 32) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.do_interrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			case 5:
				if (model.getPrescaler() == 64) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.do_interrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			case 6:
				if (model.getPrescaler() == 128) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);
					if (model.registerArray[1] == 0) {

						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.do_interrupt(2);

					}
					model.setPrescaler(1);
				} else {
					model.incrPrescaler();
				}
				break;
			case 7:
				if (model.getPrescaler() == 256) {
					model.setRegisterEntryOneBit(1, model.getRegisterEntry(1) + 1);

					if (model.registerArray[1] == 0) {
						model.setBit(7, 0xb);
						model.setBit(5, 0xb);
						model.setBit(2, 0xb);
						model.do_interrupt(2);
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

	public void countermode() {

	}

	// TODO
	// class SpeichernRegisterListener implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	//
	// int i, m;
	// for (i = 0; i <= 31; i++) {
	// for (m = 1; m <= 8; m++) {
	// int adress = i * 8 + m;
	// String entry = view.getTableEntry(i, m);
	// int value;
	// try {
	// value = Integer.parseInt(entry);
	// model.setRegisterEntry(adress, value);
	// } catch (NumberFormatException e1) {
	// e1.printStackTrace();
	// }
	// }
	// }
	// }
	// }

	// TODO
	// class SliderChangeListener implements ChangeListener {
	// // TODO Slider
	// @Override
	// public void stateChanged(ChangeEvent e) {
	// model.set_takt(view.getFrequency());
	// view.setFrequency(model.get_takt());
	// }
	//
	// }

	private void loadFile(String filePath) {
		running = false;
		view.clear_ListModel();
		model.reset_model();
		valueOnPowerUp();
		ReloadGUI();

		/* Datei einlesen mit Buffered Reader */
		BufferedReader in;
		String zeile = null;
		model.setPath_of_programfile(filePath);

		try {
			in = new BufferedReader(new FileReader(model.getPath_of_programfile()));
			try {
				while ((zeile = in.readLine()) != null) {
					try {
						/*
						 * Zeile für Zeile wird eingefügt
						 */
						view.inputList.add(zeile);
					} catch (Exception e1) {
						System.out.println(e1);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		view.setVisibilityButtons(false, true, true);
		if (view.getListModelSize() == 0) {
			view.setErrorMsgs("Kein Programm enthalten.");
		}
	}

	// TODO
	// class RegisterLadenListener implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// register_load();
	// }
	// }

	// TODO
	// @SuppressWarnings("resource")
	// public void register_load() {
	// /* Auswï¿½ï¿½hlen der Datei */
	// JFileChooser chooser = new JFileChooser();
	// /* Was wurde angeklickt -> rueckgabewert */
	// int rueckgabeWert = chooser.showOpenDialog(null);
	// if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
	// try {
	// /* Datei einlesen mit Buffered Reader */
	// BufferedReader in;
	// String zeile = null;
	// model.setPath_of_registerfile(chooser.getSelectedFile().getAbsolutePath());
	// in = new BufferedReader(new FileReader(model.getPath_of_registerfile()));
	// zeile = in.readLine();
	// String[] splitResult = zeile.split(";");
	//
	// int m, s = 0;
	// for (m = 0; m < 256; m++) {
	// {
	// /* Array wird mit Werten aus Dokument gefï¿½ï¿½llt */
	// model.setRegisterEntry(m, Integer.parseInt(splitResult[s]));
	// s++;
	// }
	// }
	//
	// ReloadGUI();
	//
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }
	// }
	// }

	// TODO
	// class OpenWuerfelListener implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// // TODO Auto-generated method stub
	//
	// }
	// }

	class ChangeTableEntryListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			if (e.keyCode == SWT.KEYPAD_CR) {
				writeTableToRegister();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
		}
	}

	// TODO
	// class ComPortChange implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent arg0) {
	// // TODO Comport
	// // model.setDefaultSerialPort(view.selectedComPort());
	// }
	// }

	public void writeToRegister(int adress, int value) {
		model.setRegisterEntry(adress, value);
		ReloadGUI();
	}

	private void changeBitPortA(int column) {
		switch (column) {
		case 8: {
			int temp = view.getValuePortA() & 0b00000001;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 1);
			} else {
				model.setPortA(model.getPortA() - 1);
			}
			break;
		}
		case 7: {
			int temp = view.getValuePortA() & 0b00000010;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 2);
			} else {
				model.setPortA(model.getPortA() - 2);
			}
			break;
		}
		case 6: {
			int temp = view.getValuePortA() & 0b00000100;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 4);
			} else {
				model.setPortA(model.getPortA() - 4);
			}
			break;
		}
		case 5: {
			int temp = view.getValuePortA() & 0b00001000;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 8);
			} else {
				model.setPortA(model.getPortA() - 8);
			}
			break;
		}
		case 4: {
			int temp = view.getValuePortA() & 0b00010000;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 16);
			} else {
				model.setPortA(model.getPortA() - 16);
			}
			break;
		}
		case 3: {
			int temp = view.getValuePortA() & 0b00100000;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 32);
			} else {
				model.setPortA(model.getPortA() - 32);
			}
			break;
		}
		case 2: {
			int temp = view.getValuePortA() & 0b01000000;
			if (temp == 0) {
				model.setPortA(model.getPortA() + 64);
			} else {
				model.setPortA(model.getPortA() - 64);
			}
			break;
		}
		case 1: {
			int temp = view.getValuePortA() & 0b10000000;
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

	private void changeTheRegisterFromPortB(int column) {
		switch (column) {
		case 8: {
			int temp = view.getValuePortB() & 0b00000001;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 1);
			} else {
				model.setPortB(model.getPortB() - 1);
			}

			model.setBit(4, 0xb);

			model.do_interrupt(1);
			break;
		}
		case 7: {
			int temp = view.getValuePortB() & 0b00000010;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 2);
			} else {
				model.setPortB(model.getPortB() - 2);
			}
			break;
		}
		case 6: {
			int temp = view.getValuePortB() & 0b00000100;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 4);
			} else {
				model.setPortB(model.getPortB() - 4);
			}
			break;
		}
		case 5: {
			int temp = view.getValuePortB() & 0b00001000;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 8);
			} else {
				model.setPortB(model.getPortB() - 8);
			}
			break;
		}
		case 4: {
			int temp = view.getValuePortB() & 0b00010000;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 16);
			} else {
				model.setPortB(model.getPortB() - 16);
			}
			model.setBit(7, 0xb);
			model.setBit(3, 0xb);
			model.setBit(0, 0xb);
			model.do_interrupt(3);
			break;
		}
		case 3: {
			int temp = view.getValuePortB() & 0b00100000;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 32);
			} else {
				model.setPortB(model.getPortB() - 32);
			}
			model.setBit(7, 0xb);
			model.setBit(3, 0xb);
			model.setBit(0, 0xb);
			model.do_interrupt(3);
			break;
		}
		case 2: {
			int temp = view.getValuePortB() & 0b01000000;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 64);
			} else {
				model.setPortB(model.getPortB() - 64);
			}
			model.setBit(7, 0xb);
			model.setBit(3, 0xb);
			model.setBit(0, 0xb);
			model.do_interrupt(3);
			break;
		}
		case 1: {
			int temp = view.getValuePortB() & 0b10000000;
			if (temp == 0) {
				model.setPortB(model.getPortB() + 128);
			} else {
				model.setPortB(model.getPortB() - 128);
			}
			model.setBit(7, 0xb);
			model.setBit(3, 0xb);
			model.setBit(0, 0xb);
			model.do_interrupt(3);
			break;
		}
		default: {
			break;
		}
		}
		model.registerArray[6] = model.getPortB();
		ReloadGUI();
	}

	public void setTime() {
		model.setRunningTime((System.currentTimeMillis()) - (model.getStartTime()));
	}

	public void countSteps() {
		model.setSteps();
	}

	public void writeTableToRegister() {
		if (running == false) {
			int i, m;
			for (i = 0; i <= 31; i++) {
				for (m = 0; m <= 7; m++) {
					int adress = (i * 8 + m);
					int value = Integer.parseInt(view.getTableEntry(i, m + 1), 16);
					if (model.getRegisterEntry(adress) != value) {
						model.registerArray[adress] = value;
						System.out.println("adresse: " + adress + " neuer wert: " + value);
					}
				}
			}
		} else {
			view.setErrorMsgs("Direkte Registeränderung nicht während der Laufzeit möglich.");
		}
	}
}
