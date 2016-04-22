package simulator;

import org.eclipse.swt.widgets.Display;

public class ThreadTest extends Thread{

	public void run() {

		Runnable r = new Runnable(){
			public void run(){
				Worker.workWithWorker(Main.arrayLinesReadIn);
				CreateRegisters.setLaufzeit(Worker.cyclesInMicroSeconds*CreateRegisters.currentQuarzDouble);
			}
		};
		while (Main.running) {
			if (!Main.centerDown.isDisposed()) {
				Display.getDefault().asyncExec(r);
			} else {
				break;
			}

			try {
				sleep((long) (Worker.cyclesInMicroSeconds*25));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

