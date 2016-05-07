package dhbw.sysnprog.pic.controller;

public class SerialThread implements Runnable {

	private PicSimController controller;
	
	public SerialThread(PicSimController controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		
		controller.reloadSerial();
		
	}

}
