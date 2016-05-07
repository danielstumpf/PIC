package dhbw.sysnprog.pic.controller;

public class PicSimControllerThread implements Runnable {
	private PicSimController controller;

	public PicSimControllerThread(PicSimController controller) {
		this.controller = controller;

	}

	@Override
	public void run() {

		try {
			
			controller.set_running(true);
			while (controller.get_running()) {
				controller.start_programm(controller.get_Frequency() / 10);
			
				controller.setTime();
				controller.countSteps();
			}
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}
}
