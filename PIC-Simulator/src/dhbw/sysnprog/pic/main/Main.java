package dhbw.sysnprog.pic.main;

import dhbw.sysnprog.pic.controller.PicSimController;
import dhbw.sysnprog.pic.model.PicSimModel;
import dhbw.sysnprog.pic.serial.PicSimSerialController;
import dhbw.sysnprog.pic.view.PicSimView;

public class Main {
	static PicSimController controller;
	static PicSimModel model;
	static PicSimView view;

	/**
	 * Diese Klasse wird nur dazu benutzt alle nötigen Komponenten zu
	 * Initialisieren und die erste View anzuzeigen
	 */
	public static void main(String[] args) {
		model = new PicSimModel();
		view = new PicSimView();
		controller = new PicSimController(view, model);
	
		view.open();
	
	}
	
	
}
