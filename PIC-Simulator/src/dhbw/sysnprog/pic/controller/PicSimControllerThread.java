package dhbw.sysnprog.pic.controller;

public class PicSimControllerThread implements Runnable {
	private PicSimController controller;

	public PicSimControllerThread(PicSimController controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		try {
			controller.setRunning(true);
			while (controller.getRunning()) {
				controller.countSteps();
				controller.start_programm(controller.getFrequency() / 10);
				controller.setTime();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}