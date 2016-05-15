package dhbw.sysnprog.pic.controller;

public class PicSimControllerThread implements Runnable {
	private final PicSimController controller;

	/**
	 * Konstruktor für den Thread zum Ausführen des Programms.
	 *
	 * @param controller
	 */
	public PicSimControllerThread(PicSimController controller) {
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			controller.setRunning(true);
			while (controller.getRunning()) {
				controller.countSteps();
				controller.start_programm(controller.getFrequency() / 10);
				controller.setTime();
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}