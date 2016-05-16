package dhbw.sysnprog.pic.controller;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.util.Enumeration;

import dhbw.sysnprog.pic.model.PicSimModel;

public class PicSimSerialController {

	private CommPortIdentifier portId;
	private SerialPort port;
	private final PicSimModel model;

	public PicSimSerialController(PicSimModel model) {
		this.model = model;
	}

	// Startpunkt für die Verbindung
	public boolean open(String comportUsed) {
		System.out.println("Open serial");
		try {
			Enumeration portID;
			String defaultPort;
			// Unterscheidung Betriebssysteme
			final String osname = System.getProperty("os.name", "").toLowerCase();
			if (osname.startsWith("windows")) {
				// windows
				defaultPort = "COM5";
			} else if (osname.startsWith("linux")) {
				// linux
				defaultPort = "/dev/ttyS0";
			} else if (osname.startsWith("mac")) {
				// mac
				defaultPort = "/dev/tty.usbserial-FTH92HF9";
			} else {
				System.out.println("Betriebssystem wird nicht unterstützt");

			}

			// Hier werden keine Ports gefunden.
			portID = CommPortIdentifier.getPortIdentifiers();
			while (portID.hasMoreElements()) {
				portId = (CommPortIdentifier) portID.nextElement();
				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					if (portId.getName().equals(comportUsed)) {
						System.out.println("Gefundener Port: " + comportUsed);
						System.out.println(" " + portId.getName());
						break;
					}
				}
			}

			port = (SerialPort) portId.open("PIC", 2000);

			port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			return true;
		}

		catch (final Exception e) {
			System.err.println("Port Open:Failed");
			return false;
		}
	}
}
