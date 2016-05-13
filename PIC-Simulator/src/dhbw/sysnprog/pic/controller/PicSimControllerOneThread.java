package dhbw.sysnprog.pic.controller;

public class PicSimControllerOneThread implements Runnable {
	private PicSimController controller;

	public PicSimControllerOneThread(PicSimController controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		try {
			controller.countSteps();
			controller.start_programm(controller.getFrequency() / 10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}