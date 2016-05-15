package dhbw.sysnprog.pic.controller;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import dhbw.sysnprog.pic.model.PicSimModel;
import dhbw.sysnprog.pic.view.PicSimView;

public class PicSimListener {

	private final PicSimController controller;
	private final PicSimView view;
	private final PicSimModel model;

	/**
	 * Konstruktor zur Erzeugung der Listener-Klasse. Hier wird die
	 * addListener() Methode aufgerufen, um die Listener der GUI hinzuzufügen.
	 *
	 * @param controller
	 * @param view
	 * @param model
	 */
	public PicSimListener(PicSimController controller, PicSimView view, PicSimModel model) {

		this.controller = controller;
		this.view = view;
		this.model = model;

		addListener();
	}

	/**
	 * Extrahierte Methode zum Setzen der Listener für die GUI. Hier werden die
	 * verschiedenen Listener erzeugt und der View übergeben, damit eine
	 * Trennung zwischen View und Controller eingehalten wird.
	 */
	private void addListener() {
		view.setResetListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.resetModel();
				controller.valueOnPowerUp();
				controller.ReloadGUI();
			}
		});

		view.setNextStepListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.runOneFunction();
			}
		});
		view.setStartProgramListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.runAllFunctions();
			}
		});
		view.setPauseListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setRunning(false);
			}
		});
		view.setOpenFileListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final String[] extensions = { "*.lst" };
				final FileDialog dialog = new FileDialog(view.getShell());
				dialog.setFilterExtensions(extensions);
				final String filePath = dialog.open();
				controller.loadFile(filePath);
				controller.filterCode();
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
			@Override
			public void handleEvent(Event event) {
				final Rectangle clientArea = view.getSpecRegTable().getClientArea();
				final Point pt = new Point(event.x, event.y);
				int index = view.getTableMem().getTopIndex();
				while (index < view.getSpecRegTable().getItemCount()) {
					boolean visible = false;
					final TableItem item = view.getSpecRegTable().getItem(index);
					final int column = 1;
					final Rectangle rect = item.getBounds(column);
					if (rect.contains(pt)) {
						final Text text = new Text(view.getSpecRegTable(), SWT.NONE);
						final Listener listener = new Listener() {
							@Override
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
								controller.writeTableToRegister();
								controller.ReloadGUI();
							}
						};
						text.addListener(SWT.FocusOut, listener);
						text.addListener(SWT.Traverse, listener);
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
					if (!visible) {
						return;
					}
					index++;
				}
			}
		});

		view.setChangeStatusRegBits(new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Point pt = new Point(event.x, event.y);
				final TableItem item = view.getStatusRegTable().getItem(0);
				for (int i = 0; i < view.getStatusRegTable().getColumnCount(); i++) {
					final Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i + 1;
						controller.changeBitStatusReg(column);
					}
				}
			}
		});

		view.setChangeOptionRegBits(new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Point pt = new Point(event.x, event.y);
				final TableItem item = view.getOptionsRegTable().getItem(0);
				for (int i = 0; i < view.getOptionsRegTable().getColumnCount(); i++) {
					final Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i + 1;
						controller.changeBitOptionReg(column);
					}
				}
			}
		});

		view.setChangeIntconRegBits(new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Point pt = new Point(event.x, event.y);
				final TableItem item = view.getIntconRegTable().getItem(0);
				for (int i = 0; i < view.getIntconRegTable().getColumnCount(); i++) {
					final Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i + 1;
						controller.changeBitIntconReg(column);
					}
				}
			}
		});

		view.setChangePortABits(new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Point pt = new Point(event.x, event.y);
				final TableItem item = view.getTablePortA().getItem(1);
				for (int i = 1; i < view.getTablePortA().getColumnCount(); i++) {
					final Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i;
						controller.changeBitPortA(column);
					}
				}
			}
		});

		view.setChangePortBBits(new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Point pt = new Point(event.x, event.y);
				final TableItem item = view.getTablePortB().getItem(1);
				for (int i = 1; i < view.getTablePortB().getColumnCount(); i++) {
					final Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						final int column = i;
						controller.changeTheRegisterFromPortB(column);
					}
				}
			}
		});

		view.setMemoryListener(new Listener() {
			@Override
			public void handleEvent(Event event) {
				final Rectangle clientArea = view.getTableMem().getClientArea();
				final Point pt = new Point(event.x, event.y);
				int index = view.getTableMem().getTopIndex();
				while (index < view.getTableMem().getItemCount()) {
					boolean visible = false;
					final TableItem item = view.getTableMem().getItem(index);
					for (int i = 1; i < view.getTableMem().getColumnCount(); i++) {
						final Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final Text text = new Text(view.getTableMem(), SWT.NONE);
							final Listener textListener = new Listener() {
								@Override
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
									controller.writeTableToRegister();
									controller.ReloadGUI();
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
					if (!visible) {
						return;
					}
					index++;
				}
			}
		});
	}
}
