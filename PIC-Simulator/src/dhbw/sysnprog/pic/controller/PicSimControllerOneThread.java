package dhbw.sysnprog.pic.controller;

public class PicSimControllerOneThread implements Runnable {
	private final PicSimController controller;

	/**
	 * Konstruktor zur Erzeugung eines Threads zum Ausführen eines Befehls
	 *
	 * @param controller
	 */
	public PicSimControllerOneThread(PicSimController controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		try {
			controller.countSteps();
			controller.start_programm(controller.getFrequency() / 10);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}