package simulator;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class Timer0 {

	static String[] opTest = new String[]{"0","0","0","0","0","0","0","1"};	//[0] und [1] not used
	static boolean raActiv;
	static int timer = 0;
	static boolean ra4OneToZero;
	static boolean ra4ZeroToOne;
	static int festerWert = 0;
	static int counterFesterWert = 0;
	static Label virtualPSA;


	public static void checkForTimer0(String valOfReg) {//01
		if(valOfReg.equals("FF")) {

		} else {
			workWithTimer0();

		}
	}

	public static void workWithTimer0() {
		if(Integer.parseInt(CreateStateRegister.optionBits[2]) == 1) { // wenn true, RA. wenn false, 1/4Pic Quarz
			raActiv = true; // was mit ra machen
		} else {
			double freq = Math.round(CreateStateRegister.currentQuarzDouble/4*1000)/1000.0; // 1/4 quarzfreq.
			System.err.println(freq);
		}

		if(Integer.parseInt(CreateStateRegister.optionBits[3]) == 1) { // timer immer bei wechsel von RA4 von 1 zu 0 erhöhen
			if(ra4OneToZero == true) {
				Worker.registerInputArray[1] = Integer.toHexString(Integer.parseInt(Worker.registerInputArray[1],16)+ 1).toUpperCase(); // timer0 holen
			} 
			ra4OneToZero = false;
		} else { // timer immer bei wechsel von RA4 von 0 zu 1 erhöhen
			if(ra4ZeroToOne== true) {
				Worker.registerInputArray[1] = Integer.toHexString(Integer.parseInt(Worker.registerInputArray[1],16)+ 1).toUpperCase(); // timer0 holen registerInput[1]...

			}
			ra4ZeroToOne = false;
		}

		//check PSA -> vorerst nur für timer0 (1 = ohne vorteiler, 0 = mit Vorteiler)
		if(Integer.parseInt(CreateStateRegister.optionBits[4]) == 1) {
			// vorteiler für wdt, vorerst uninteressant
		} else {
			// vorteiler für timer0
		}

		int psaValueOnlyForChangeDetect = timer0PS2PS1PS0(Integer.parseInt(CreateStateRegister.optionBits[5]), Integer.parseInt(CreateStateRegister.optionBits[6]), Integer.parseInt(CreateStateRegister.optionBits[7]));

		
		if(festerWert == psaValueOnlyForChangeDetect) { // wenn beide werte gleich
			if(counterFesterWert-Worker.cyclesInMicroSeconds >=0 ) {
			counterFesterWert = counterFesterWert - Worker.cyclesInMicroSeconds;
			} else {
				counterFesterWert =0;
			}
			
			
			if(counterFesterWert == 0) {
				counterFesterWert = psaValueOnlyForChangeDetect;

				if((Integer.parseInt(Worker.registerInputArray[1],16)+1) >255) {
					Worker.registerInputArray[1] = "00";
					FillRegister.generalValueSetterForRegister(1, "00");
					CreateStateRegister.intconBits[5] = "1";
					Worker.interruptCheck();
					CreateStateRegister.calculateIntcon();
					CreateStateRegister.intconToBinary(FillRegister.table.getItem(1).getText(4));
					Worker.registerInputArray[11] = FillRegister.table.getItem(1).getText(4);
					
				} else {
					Worker.registerInputArray[1] = Integer.toHexString(Integer.parseInt(Worker.registerInputArray[1],16)+ 1).toUpperCase();
				}
				FillRegister.generalValueSetterForRegister(1, Worker.registerInputArray[1]);
			}
		} else { // wenn werte unterschiedlich
			festerWert = psaValueOnlyForChangeDetect;
			counterFesterWert = festerWert;
			counterFesterWert+=2;
		}
//		virtualPSA.setText(counterFesterWert+"");

	}

	public static void visuallTimer(Composite rightup) {
//		Composite comp = new Composite(rightup, SWT.BORDER);
//		comp.setLayout(new GridLayout());
//		comp.setLayoutData(new GridData());
//		virtualPSA = new Label(comp, SWT.NONE);
//		virtualPSA.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		virtualPSA.setText("00");
	}




	public static int timer0PS2PS1PS0(int ps2, int ps1, int ps0) {
		//nur wenn vorteiler für timer0 verwendet (also false) // für wdt -> true (andere Teilverhältnisse!!!!)
		int psaValue = 0;
		String timeCheckFromOption = ps2 +""+ ps1 +""+ ps0;
		switch(timeCheckFromOption) {
		case "000": psaValue = 2; break;
		case "001": psaValue = 4; break;
		case "010": psaValue = 8; break;
		case "011": psaValue = 16; break;
		case "100": psaValue = 32; break;
		case "101": psaValue = 64; break;
		case "110": psaValue = 128; break;
		case "111": psaValue = 256; break;
		}
		return psaValue;
	}

}
