package dhbw.sysnprog.pic.main;

import dhbw.sysnprog.pic.controller.PicSimController;
import dhbw.sysnprog.pic.model.PicSimModel;
import dhbw.sysnprog.pic.view.PicSimView;

public class Main {
	static PicSimController controller;
	static PicSimModel model;
	static PicSimView view;

	/**
	 * Einstieg in das Programm. Instanzen von Model, view und controller werden
	 * erstellt und die view wird geöffnet
	 */
	public static void main(String[] args) {
		model = new PicSimModel();
		view = new PicSimView();
		controller = new PicSimController(view, model);

		view.open();
	}
}
