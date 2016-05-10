package dhbw.sysnprog.pic.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author Daniel
 *
 *         Model des PIC-Simulator. F�hrt s�mtliche Funktionen des Simulators
 *         durch, unabh�ngig von der Benutzeroberfl�che
 *
 */
public class PicSimModel {

	/**
	 * Inhalt des W-Registers
	 */
	public int registerW = 0b0;
	/**
	 * Sprungadresse
	 */
	public int sprung;
	/**
	 * Programmz�hler
	 */
	private int PC = 0;
	/**
	 * Laufzeit
	 */
	private long runningTime = 0;
	/**
	 * Startzeit
	 */
	private long startTime = 0;
	/**
	 * Stack
	 */
	public Deque<Integer> STACK = new ArrayDeque<Integer>();
	/**
	 * Speicherinhalt
	 */
	public int[] registerArray = new int[256];
	/**
	 * Befehlsliste
	 */
	public List<Integer> codeList = new ArrayList<Integer>();
	private int takt;
	private String programFilePath;
	private int steps;

	private int portA;
	private int portB;
	/**
	 * true = counter-Modus false = timer-Modus
	 */
	private int prescaler;
	private boolean mode;

	/**
	 * Standardkonstruktor
	 */
	public PicSimModel() {
	}

	/**
	 * @return Integer Wert der durchgef�hrten Schritte
	 */
	public int getSteps() {
		return steps;
	}

	/**
	 * Erh�ht die Anzahl der durchgef�hrten Schritte um 1
	 */
	public void setSteps() {
		this.steps = steps + 1;
	}

	/**
	 * @return Long Wert der Startzeit
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Setzt die Startzeit auf den �bergebenen Wert
	 * 
	 * @param startTime
	 *            Long-Wert Startzeit
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * Gibt die aktuelle Laufzeit als Long-Wert zur�ck
	 * 
	 * @return Long Wert der Laufzeit
	 */
	public long getRunningTime() {
		return runningTime;
	}

	/**
	 * Setzt die Laufzeit auf den �bergebenen Wert
	 * 
	 * @param runningTime
	 *            Long Wert der zu setzenden Laufzeit
	 */
	public void setRunningTime(long runningTime) {
		this.runningTime = runningTime;
	}

	/**
	 * L�schen des Stacks
	 */
	public void cleanStack() {
		STACK.clear();
	}

	/**
	 * L�schen des W-Registers
	 */
	public void cleanWReg() {
		registerW = 0;
	}

	/**
	 * @return Integer Wert von PortA
	 */
	public int getPortA() {
		return portA;
	}

	/**
	 * Setzt den Wert des PortA
	 * 
	 * @param portA
	 *            zu setzender Integer Wert des PortA
	 */
	public void setPortA(int portA) {
		this.portA = portA;
	}

	/**
	 * @return Integer Wert von PortB
	 */
	public int getPortB() {
		return portB;
	}

	/**
	 * Setzt den Wert des PortB
	 * 
	 * @param portB
	 *            zu setzender Integer Wert des PortB
	 */
	public void setPortB(int portB) {
		this.portB = portB;
	}

	/**
	 * @return String Wert des Pfads der Programmdatei
	 */
	public String getProgramFilePath() {
		return programFilePath;
	}

	/**
	 * Setzt den Pfad der Programmdatei
	 * 
	 * @param path
	 *            String des neuen Dateipfades der Programmdatei
	 */
	public void setProgramFilePath(String path) {
		this.programFilePath = path;
	}

	/**
	 * Setzt den Programmz�hler auf den neuen Wert
	 * 
	 * @param counter
	 *            Integer Wert des neuen Programmz�hlers
	 */
	public void setProgramCounter(int counter) {
		PC = counter;
	}

	/**
	 * Gibt den Wert des Programmz�hlers zur�ck
	 * 
	 * @return Programmz�hler als Integer-Wert
	 */
	public int getProgrammCounter() {
		return PC;
	}

	/**
	 * Setzt den neuen Takt
	 * 
	 * @param takt
	 *            zu setzender Takt Integer
	 */
	public void setTakt(int takt) {
		this.takt = takt;
	}

	/**
	 * @return Integer-Wert des Taktes
	 */
	public int getTakt() {
		return takt;
	}

	/**
	 * @return Integer-Wert des Status-Registers
	 */
	public int getStatus() {
		return getRegisterEntry(3);
	}

	/**
	 * Setzt den �bergebenen Wert im Status-Register
	 * 
	 * @param value
	 *            zu setzender Speicherwert
	 */
	public void setStatus(int value) {
		registerArray[3] = value;
	}

	/**
	 * @return den Speicherinhalt des Option-Registers
	 */
	public int getOption() {
		return registerArray[0x81];
	}
	
	/**
	 * Setzt den �bergebenen Wert im Options-Register
	 * 
	 * @param value
	 *            zu setzender Speicherwert
	 */
	public void setOption(int value) {
		registerArray[0x81] = value;
	}
	
	/**
	 * Setzt den �bergebenen Wert im Intcon-Register
	 * 
	 * @param value
	 *            zu setzender Speicherwert
	 */
	public void setIntcon(int value) {
		registerArray[0xb] = value;
	}
	
	/**
	 * @return den Speicherinhalt des Intcon-Registers
	 */
	public int getIntcon() {
		return registerArray[0xb];
	}
	
	/**
	 * Setzt den Speicherinhalt an der Stelle "index" auf den Wert "value"
	 * 
	 * @param index
	 *            Integer-Wert des Index im Register
	 * @param value
	 *            Integer Wert des zu setzenden Speicherwertes
	 */
	public void setRegisterEntry(int index, int value) {
		if (checkBitSet(5, 3)) {
			// Wenn das Bit f�r Bankumschaltung gesetzt ist
			if (index == 0) {
				value = value & 0b11111111;
				registerArray[registerArray[4] + 128] = value;

			} else {
				value = value & 0b11111111;
				registerArray[index + 128] = value;
			}
		} else {
			if (index == 0) {
				value = value & 0b11111111;
				registerArray[registerArray[4]] = value;

			} else {
				value = value & 0b11111111;
				registerArray[index] = value;
			}
		}
	}

	/**
	 * Setzt ein Bit an bestimmter Speicherstelle "index" auf den Wert "value"
	 * 
	 * @param index
	 *            Integer-Wert des Index im Register
	 * @param value
	 *            Integer Wert des zu setzenden Speicherwertes
	 */
	public void setRegisterEntryOneBit(int index, int value) {
		if (index == 0) {
			value = value & 0b11111111;
			registerArray[registerArray[4]] = value;

		} else {
			value = value & 0b11111111;
			registerArray[index] = value;
		}
	}

	/**
	 * Gibt den Speicherinhalt an bestimmter Stelle "index" zur�ck
	 * 
	 * @param index
	 *            Integer Wert Index des Registers
	 * @return Integer Wert des Speicherinhalts
	 */
	public int getRegisterEntry(int index) {
		if (index == 0) {
			return registerArray[registerArray[4]];
		}
		return registerArray[index];
	}

	/**
	 * Setzt das gesamte Model auf Ausgangswerte zur�ck
	 */
	public void resetModel() {
		int m;
		for (m = 0; m < 256; m++) {
			registerArray[m] = 0;
		}
		registerW = 0;
		STACK.clear();
		sprung = 0;
		PC = 0;
		codeList.clear();
		programFilePath = "";
		takt = 4000;
		steps = 0;
		runningTime = 0;
	}

	/**
	 * 
	 * Setzt Werte in die Code-Liste. In dieser Liste sind die Befehle, die
	 * abgearbeitet werden.
	 * 
	 * @param code
	 *            Integer-Wert zum Einf�gen in die Code-Liste
	 */
	public void setCode(int code) {
		codeList.add(code);
	}

	/**
	 * Extrahiert aus einer Programmzeile den Befehlscode und speichert ihn in
	 * der CodeList ab.
	 * 
	 * @param codeLine
	 *            String einzelne Zeile des Programms
	 * 
	 */
	public void checkCode(String codeLine) {
		String codeLineTmp = codeLine;
		StringBuffer buffer = new StringBuffer(codeLineTmp);
		int i;
		for (i = 0; i < 5; i++) {
			buffer.deleteCharAt(0);
		}
		StringBuilder builder = new StringBuilder();
		for (i = 0; i < 4; i++) {
			builder.append(buffer.charAt(i));
		}
		int code = Integer.parseInt(builder.toString(), 16);
		setCode(code);
	}

	/*
	 * Tats�chliche Pic-Befehle Hier findet die Auswahl der Befehle nach
	 * Bitfolge statt
	 */
	/**
	 * Auswertung des Befehls und anschlie�ender Befehlsaufruf
	 * 
	 * @param code
	 *            Befehlscode als Integer
	 * @throws InterruptedException
	 * 
	 */
	public void doAction(int code) throws InterruptedException {
		int codeTmp = code;

		int hex16 = codeTmp & 0b1111111111111111;
		switch (hex16) {
		case 100:
			do_clrwdt();
			break;
		case 9:
			do_retfie();
			break;
		case 8:
			do_return();
			break;
		case 99:
			do_sleep();
			break;
		}

		int hex10 = codeTmp & 0b1111110000000000;
		int _hex10 = codeTmp & 0b0000001111111111;
		switch (hex10) {
		case 4096:
			do_bcf(_hex10);
			break;
		case 5120:
			do_bsf(_hex10);
			break;
		case 6144:
			do_btfsc(_hex10);
			break;
		case 7168:
			do_btfss(_hex10);
			break;
		case 12288:
			do_movlw(_hex10);
			break;
		case 13312:
			do_retlw(_hex10);
			break;
		}

		int hex9 = codeTmp & 0b1111111110000000;
		int _hex9 = codeTmp & 0b0000000001111111;
		switch (hex9) {
		case 384:
			do_clrf(_hex9);
			break;
		case 256:
			do_clrw(_hex9);
			break;
		case 128:
			do_movwf(_hex9);
			break;
		case 0:
			do_nop();
			break;
		}

		int hex8 = codeTmp & 0b1111111100000000;
		int _hex8 = codeTmp & 0b0000000011111111;
		switch (hex8) {
		case 1792:
			do_addwf(_hex8);
			break;
		case 1280:
			do_andwf(_hex8);
			break;
		case 2304:
			do_comf(_hex8);
			break;
		case 768:
			do_decf(_hex8);
			break;
		case 2816:
			do_decfsz(_hex8);
			break;
		case 2560:
			do_incf(_hex8);
			break;
		case 3840:
			do_incfsz(_hex8);
			break;
		case 1024:
			do_iorwf(_hex8);
			break;
		case 2048:
			do_movf(_hex8);
			break;
		case 3328:
			do_rlf(_hex8);
			break;
		case 3072:
			do_rrf(_hex8);
			break;
		case 512:
			do_subwf(_hex8);
			break;
		case 3584:
			do_swapf(_hex8);
			break;
		case 1536:
			do_xorwf(_hex8);
			break;
		case 14592:
			do_andlw(_hex8);
			break;
		case 14336:
			do_iorlw(_hex8);
			break;
		case 14848:
			do_xorlw(_hex8);
			break;

		}

		int hex7 = codeTmp & 0b1111111000000000;
		int _hex7 = codeTmp & 0b0000000111111111;
		switch (hex7) {
		case 15872:
			do_addlw(_hex7);
			break;
		case 15360:
			do_sublw(_hex7);
			break;
		}

		int hex5 = codeTmp & 0b1111100000000000;
		int _hex5 = codeTmp & 0b0000011111111111;
		switch (hex5) {
		case 8192:
			do_call(_hex5);
			break;
		case 10240:
			do_goto(_hex5);
			break;
		}

	}

	/**
	 * The content of the w-register are added to the eight bit literal 'hex4'
	 * and the result is placed in the w-register
	 * 
	 * @param lit_K
	 * 
	 */
	private void do_addlw(int lit_K) {
		int lit_K_temp = lit_K & 0b011111111;
		int value = lit_K_temp + registerW;
		if (value > 255) {
			set_C(true);
		} else {
			set_C(false);
		}
		if ((((lit_K_temp & 0b1000) + (registerW & 0b1000)) == 16)) {
			set_DC(true);
		} else {
			set_DC(false);
		}
		if (value == 0) {
			set_Z(true);
		} else {
			set_Z(false);
		}
		registerW = value;
	}

	/**
	 * add the contents of the W-register with register 'f' if 'd' is 0 the
	 * result is stored in the W-Register. If 'd' is 1, the result is stored
	 * back in the register 'f'
	 * 
	 * @param d_f
	 * 
	 */
	private void do_addwf(int d_f) {
		int d = d_f & 0b10000000;
		int adress = d_f & 0b01111111;
		int value = getRegisterEntry(adress);
		int result = registerW + value;
		if (d == 0) {
			registerW = result;
		} else {
			setRegisterEntry(adress, result);
		}
		if (result > 255) {
			set_C(true);
		}
		if (result == 0) {
			set_Z(true);
		}
	}

	/**
	 * The contents of W-Register are AND'ed with the eight bit literal 'k'. The
	 * result is placed in the W-Register.
	 * 
	 * @param lit_K
	 * 
	 */
	private void do_andlw(int lit_K) {
		int value = registerW & lit_K;
		if (value == 0) {
			set_Z(true);
		}
		registerW = value;
	}

	/**
	 * 
	 * AND the W-Register with register 'f'. If 'd' is 0, the result is stored
	 * in the W-Register. If 'd' is 1 the result is stored back in the register
	 * 'f'.
	 * 
	 * @param d_f
	 */
	private void do_andwf(int d_f) {
		int d = d_f & 0b10000000;
		int adress = d_f & 0b01111111;
		int value = getRegisterEntry(adress);
		int result = registerW & value;
		if (d == 0) {
			registerW = result;
		} else {
			setRegisterEntry(adress, result);
		}
		if (result == 0) {
			set_Z(true);
		}
	}

	/**
	 * Bit 'b' in the register 'f' is cleared
	 * 
	 * @param b_f
	 * 
	 */
	private void do_bcf(int b_f) {
		int bit = (b_f & 0b1110000000) / 128;
		int adress = b_f & 0b0001111111;
		clear_Bit(bit, adress);
	}

	/**
	 * Bit 'b' in the register 'f' is set
	 * 
	 * @param b_f
	 * 
	 */
	private void do_bsf(int b_f) {
		int bit = (b_f & 0b1110000000) / 128;
		int adress = b_f & 0b0001111111;
		setBit(bit, adress);
	}

	/**
	 * 
	 * If bit 'b' in register 'f' is '1' the the next instruction is executed.
	 * If bit 'b' in register 'f' is '0' then the next unstruction is discarded
	 * and a NOP is executed instead, making this a 2Tcy instruction
	 * 
	 * @param b_f
	 */
	private void do_btfsc(int b_f) {
		int adress = b_f & 0b0001111111;
		int bit = (b_f & 0b1110000000) / 128;
		if (!checkBitSet(bit, adress)) {
			setProgramCounter(getProgrammCounter() + 1);
		}
	}

	/**
	 * If bit 'b' in register 'f' is '0' the the next instruction is executed.
	 * If bit 'b' is '1' then the next unstruction is discarded and a NOP is
	 * executed instead, making this a 2Tcy instruction
	 * 
	 * @param b_f
	 * 
	 */
	private void do_btfss(int b_f) {
		int adress = b_f & 0b0001111111;
		int bit = (b_f & 0b1110000000) / 128;
		if (checkBitSet(bit, adress)) {
			setProgramCounter(getProgrammCounter() + 1);
		}
	}

	/**
	 * Call Subroutine. First, return address (PC+1) is pushed onto the STACK.
	 * The eleven bit immediate address is loaded into OC bits <10:0>. The upper
	 * bits of the PC are loaded from PCLATH. CALL is a two cycle instruction.
	 * 
	 * @param lit_K
	 * 
	 */
	private void do_call(int lit_K) {
		STACK.add(getProgrammCounter());
		setProgramCounter(lit_K - 1);
	}

	/**
	 * The contents of register 'f' are cleared and the Z bit is set.
	 * 
	 * @param f
	 * 
	 */
	private void do_clrf(int f) {
		setRegisterEntry(f, 0);
		set_Z(true);
	}

	/**
	 * W register is cleared. Zero bit (Z) is set.
	 * 
	 * @param value
	 * 
	 */
	private void do_clrw(int value) {
		registerW = 0b0;
		set_Z(true);
	}

	/**
	 * CLRWDT Instruction resets the Watchdog-Timer. It also resets the
	 * prescaler of th WDT. Status bits TO and PD are set.
	 */
	private void do_clrwdt() {
		// TODO Clear WatchDog Timer
	}

	/**
	 * The contents of register 'f' are complemented. If 'd' is 0 the result is
	 * stored back in W. If 'd' is 1 the result is stored back in register 'f'.
	 * 
	 * @param f_d
	 * 
	 */
	private void do_comf(int f_d) {

		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int temp = getRegisterEntry(adress);
		int value = ~temp;
		if (d == 0) {
			registerW = value & 0xFF;
		} else {
			setRegisterEntry(adress, registerW);
		}
		if ((value & 0xFF) == 0) {
			set_Z(true);
		}
	}

	/**
	 * Decrement register 'f'. If 'd' is 0 the result is stored back in the W
	 * register. If 'd' is 1 the result is stored back in register 'f'.
	 * 
	 * @param f_d
	 * 
	 */
	private void do_decf(int f_d) {
		int value = (getRegisterEntry(f_d) - 1);
		setRegisterEntry(f_d, value);
	}

	/**
	 * The contents of register 'f' are decremented. If 'd' is 0 the result is
	 * placed in the W register. If 'd' is 1 the result is placed back in
	 * register 'f'. If the result is 1, the next instruction, is executed. If
	 * the result is 0, then a NOP is executed instead making it a 2Tcy
	 * instruction
	 * 
	 * @param f_d
	 * 
	 */
	private void do_decfsz(int f_d) {

		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int decr = getRegisterEntry(adress) - 1;

		if (d == 128) {
			setRegisterEntry(adress, decr);
		} else {

			registerW = decr;
		}
		if (decr == 0) {
			setProgramCounter(getProgrammCounter() + 1);
		}
	}

	/**
	 * GOTO is an unconditional branch. The eleven bit immediate value is loaded
	 * into PC bits <10:0>. The upper bits of PC are loaded from PCLATH<4:3>.
	 * GOTO is a two cycle instruction.
	 * 
	 * @param lit_K
	 * 
	 */
	private void do_goto(int lit_K) {
		sprung = getProgrammCounter();
		setProgramCounter(lit_K - 1);
	}

	/**
	 * The contents of register 'f' are incremented. If 'd' is 0 the result is
	 * placed in the W register. If 'd' is 1 the result is placed back in
	 * register 'f'.
	 * 
	 * @param f_d
	 * 
	 */
	private void do_incf(int f_d) {
		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int value = getRegisterEntry(adress);
		if (get_Z()) {
			set_Z(false);

		} else {
			if (value == 255) {
				set_Z(true);
			}
		}

		if (d == 0) {
			registerW = getRegisterEntry(adress) + 1;

		} else {
			setRegisterEntry(adress, (getRegisterEntry(adress) + 1));
		}
	}

	/**
	 * The contents of register 'f' are incremented. If 'd' is 0 the result is
	 * placed in the W register. If 'd' is 1 the result is placed back in
	 * register 'f'. If the result is 1, the next instruction is executed. If
	 * the result is 0, a NOP is executed instead making it a 2TCY instruction.
	 * 
	 * @param f_d
	 * 
	 */
	private void do_incfsz(int f_d) {
		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int decr = getRegisterEntry(adress) + 1;
		if (d == 128) {
			setRegisterEntry(adress, decr);
		} else {
			registerW = decr;
		}
		if (getRegisterEntry(adress) == 0) {
			setProgramCounter(getProgrammCounter() + 1);
		}
	}

	/**
	 * The contents of the W register is OR�ed with the eight bit literal 'k'.
	 * The result is placed in the W register.
	 * 
	 * @param lit_K
	 * 
	 */
	private void do_iorlw(int lit_K) {
		int value = registerW | lit_K;
		if (value == 0) {
			set_Z(true);
		}
		registerW = value;
	}

	/**
	 * Inclusive OR the W register with register 'f'. If 'd' is 0 the result is
	 * placed in the W register. If 'd' is 1 the result is placed back in
	 * register 'f'.
	 * 
	 * @param f_d
	 * 
	 */
	private void do_iorwf(int f_d) {
		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int value = getRegisterEntry(adress) | registerW;
		if (d == 0) {
			registerW = value;
		} else {
			setRegisterEntry(adress, value);
		}
		if (value == 0) {
			set_Z(true);
		}
	}

	/**
	 * The contents of register f is moved to a destination dependant upon the
	 * status of d. If d = 0, destination is W register. If d = 1, the
	 * destination is file register f itself. d = 1 is useful to test a file
	 * register since status flag Z is affected. Words:
	 * 
	 * @param f_d
	 * 
	 */
	private void do_movf(int f_d) {
		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int value = getRegisterEntry(adress);
		if (d == 0) {
			registerW = value;
		}
		if (value == 0) {
			set_Z(true);
		}
	}

	/**
	 * The eight bit literal 'k' is loaded into W register. The don�t cares will
	 * assemble as 0�s.
	 * 
	 * @param lit_K
	 * 
	 */
	private void do_movlw(int lit_K) {
		int value = lit_K & 0b0011111111;
		registerW = value;
	}

	/**
	 * Move data from W register to register 'f'.
	 * 
	 * @param var_F
	 * 
	 */
	private void do_movwf(int var_F) {
		setRegisterEntry(var_F, registerW);
	}

	/**
	 * No operation.
	 */
	private void do_nop() {
		// No Operation
	}

	/**
	 * Return from Interrupt. Stack is POPed and Top of Stack (TOS) is loaded in
	 * the PC. Interrupts are enabled by setting Global Interrupt Enable bit,
	 * GIE (INTCON<7>). This is a two cycle instruction.
	 */
	private void do_retfie() {
		int adress = STACK.pop();
		setProgramCounter(adress);
		// Global Interrupt setzen
		setBit(7, 0xb);
	}

	/**
	 * The W register is loaded with the eight bit literal 'k'. The program
	 * counter is loaded from the top of the stack (the return address). This is
	 * a two cycle instruction.
	 * 
	 * @param lit_K
	 * 
	 */
	private void do_retlw(int lit_K) {
		int temp = lit_K & 0b0011111111;
		registerW = temp;
		int adress = STACK.pop();
		setProgramCounter(adress);
	}

	/**
	 * 
	 * Return from Subroutine. The Stack is popped and th top of the stack is
	 * loaded into the program counter. This is a two cycle instruction.
	 * 
	 * @throws InterruptedException
	 */
	private void do_return() throws InterruptedException {
		int adress = STACK.pop();
		setProgramCounter(adress);
	}

	/**
	 * The contents of register 'f' are rotated one bit to the left through the
	 * Carry Flag. If 'd' is 0 the result is placed in the W register. If 'd' is
	 * 1 the result is stored back in register 'f'.
	 * 
	 * @param f_d
	 * 
	 */
	private void do_rlf(int f_d) {
		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int value = getRegisterEntry(adress);
		value = value << 1;
		if (get_C() == 1) {
			value += 1;
		}
		if ((value & 0b100000000) == 256) {
			set_C(true);
			value = value & 0b011111111;
		}
		if (d == 0) {
			registerW = value;
		} else {
			setRegisterEntry(adress, value);
		}
	}

	/**
	 * The contents of register 'f' are rotated one bit to the right through the
	 * Carry Flag. If 'd' is 0 the result is placed in the W register. If 'd' is
	 * 1 the result is placed back in register 'f'.
	 * 
	 * @param f_d
	 * 
	 */
	private void do_rrf(int f_d) {
		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int value = getRegisterEntry(adress);
		if (get_C() == 1) {
			value += 256;
		}
		if ((value & 0b000000001) == 1) {
			set_C(true);
			value = value & 0b111111110;
		}
		value = value >> 1;
		if (d == 0) {
			registerW = value;
		} else {
			setRegisterEntry(adress, value);
		}
	}

	/**
	 * The power-down status bit, PD is cleared. Time-out status bit, TO is
	 * set.Watchdog Timer and its prescaler are cleared. The processor is put
	 * into SLEEP mode with the oscillator stopped. See Section 14.8 for more
	 * details.
	 */
	private void do_sleep() {
		// TODO clear bit PD
		// TODO clear bit TO
		// TODO clear bit Watchdog
		// TODO processor is put to sleep
		// TODO oscillator stopped
	}

	/**
	 * The W register is subtracted (2�s complement method) from the eight bit
	 * literal 'k'. The result is placed in the W register.
	 * 
	 * @param lit_K
	 * 
	 */
	private void do_sublw(int lit_K) {
		int temp = lit_K & 0b011111111;

		int value = temp - registerW;
		if (value >= 0) {
			set_C(true);
		} else {
			set_C(false);
		}
		if ((((temp & 0b10000) - (registerW & 0b10000)) == 0) && ((value & 0b10000) == 0)) {
			set_DC(true);
		} else {
			set_DC(false);
		}
		if (value == 0) {
			set_Z(true);
		} else {
			set_Z(false);
		}
		registerW = value;
	}

	/**
	 * Subtract (2�s complement method) W register from register 'f'. If 'd' is
	 * 0 the result is stored in the W register. If 'd' is 1 the result is
	 * stored back in register 'f'.
	 * 
	 * @param f_d
	 * 
	 */
	private void do_subwf(int f_d) {
		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int value = getRegisterEntry(adress) - registerW;
		if (d == 0) {
			registerW = value;
		} else {
			setRegisterEntry(adress, value);
		}
		if (value > 255) {
			set_C(true);
		}
		if (value == 0) {
			set_Z(true);
		}
	}

	/**
	 * The upper and lower nibbles of register 'f' are exchanged. If 'd' is 0
	 * the result is placed in W register. If 'd' is 1 the result is placed in
	 * register 'f'.
	 * 
	 * @param f_d
	 * 
	 */
	private void do_swapf(int f_d) {
		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int nibble1 = (getRegisterEntry(adress) & 0b00001111) * 16;
		int nibble2 = (getRegisterEntry(adress) & 0b11110000) / 16;
		int ergebnis = nibble1 + nibble2;
		if (d == 0) {
			registerW = ergebnis;
		} else {
			setRegisterEntry(adress, ergebnis);
		}
	}

	/**
	 * The contents of the W register are XOR�ed with the eight bit literal 'k'.
	 * The result is placed in the W register.
	 * 
	 * @param lit_K
	 * 
	 */
	private void do_xorlw(int lit_K) {
		int value = registerW ^ lit_K;
		if (value == 0) {
			set_Z(true);
		} else {
			set_Z(false);
		}
		registerW = value;
	}

	/**
	 * Exclusive OR the contents of the W register with register 'f'. If 'd' is
	 * 0 the result is stored in the W register. If 'd' is 1 the result is
	 * stored back in register 'f'.
	 * 
	 * @param f_d
	 * 
	 */
	private void do_xorwf(int f_d) {
		int d = f_d & 0b10000000;
		int adress = f_d & 0b01111111;
		int value = getRegisterEntry(adress);
		int result = registerW | value;
		if (d == 0) {
			registerW = result;
		} else {
			setRegisterEntry(adress, result);
		}

		if (result == 0) {
			set_Z(true);
		}
	}

	/**
	 * Interrupt wird gesetzt
	 * 
	 * @param ID
	 * 
	 */
	public void doInterrupt(int ID) {
		System.out.println("Interrupt");
		if (checkBitSet(7, 0xb)) {
			switch (ID) {
			// RB0-Interrupt
			case 1:
				if (checkBitSet(4, 0xb)) {
					STACK.add(getProgrammCounter());
					setProgramCounter(4);
					setBit(1, 0xb);
				}
				System.out.println("RB0-Interrupt");
				break;
			// Timer-Interrupt
			case 2:
				if (checkBitSet(5, 0xb)) {
					STACK.add(getProgrammCounter());
					setProgramCounter(4);
					setBit(2, 0xb);
				}
				break;
			// Port-B-Interrupt
			case 3:
				if (checkBitSet(3, 0xb)) {
					STACK.add(getProgrammCounter());
					setProgramCounter(4);
					setBit(0, 0xb);
				}
				System.out.println("RB4-7 Interrupt");
				break;
			// EEPROM-Interrupt
			case 4:
				if (checkBitSet(6, 0xb)) {
					STACK.add(getProgrammCounter());
					setProgramCounter(4);
				}
				break;
			}
		}
	}

	/**
	 * @return Wert des C-Bits
	 */
	public int get_C() {
		if (checkBitSet(1, 3)) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Setzt das C-Bit mit dem �bergebenen Wert
	 * 
	 * @param s
	 *            boolean, gibt an, ob C gesetzt werden soll oder nicht
	 * 
	 */
	public void set_C(boolean s) {
		if (s) {
			setBit(1, 3);
		} else {
			clear_Bit(1, 3);
		}
	}

	/**
	 * @return Wert des DC-Bits
	 */
	public int get_DC() {
		if (checkBitSet(2, 3)) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Setzt das DC-Bit mit dem �bergebenen Wert
	 * 
	 * @param s
	 *            boolean, gibt an, ob DC gesetzt werden soll oder nicht
	 * 
	 */
	public void set_DC(boolean s) {
		if (s) {
			setBit(2, 3);
		} else {
			clear_Bit(2, 3);
		}
	}

	/**
	 * @return Wert des Z-Bits
	 */
	public boolean get_Z() {
		if (checkBitSet(3, 3)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Setzt das Z-Bit mit dem �bergebenen Wert
	 * 
	 * @param s
	 *            boolean, gibt an, ob Z gesetzt werden soll oder nicht
	 * 
	 */
	public void set_Z(boolean s) {
		if (s) {
			setBit(3, 3);
		} else {
			clear_Bit(3, 3);
		}
	}

	/**
	 * Setzt den �bergebenen Wert in die berechnete Speicheradresse
	 * 
	 * @param xColumn
	 *            Speicheradresse Spalte
	 * @param yRow
	 *            Speicheradresse Zeile
	 * @param value
	 *            Zu setzender Wert
	 * 
	 */
	public void writeToRegister(int xColumn, int yRow, String value) {
		int adress = (yRow * 8) + xColumn + 1;
		setRegisterEntry(adress, (Integer.parseInt(value)));
	}

	/**
	 * Setzt ein einzelnes Bit an bestimmer Speicheradresse
	 * 
	 * @param position
	 *            Gibt das Bit an, welches gesetzt wird.
	 * @param adress
	 *            Gibt die Adresse an, an der das Bit gesetzt wird.
	 */
	public void setBit(int position, int adress) {
		switch (position) {
		case 1: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00000001));
			break;
		}
		case 2: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00000010));
			break;
		}
		case 3: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00000100));
			break;
		}
		case 4: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00001000));
			break;
		}
		case 5: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00010000));
			break;
		}
		case 6: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b00100000));
			break;
		}
		case 7: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b01000000));
			break;
		}
		case 8: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) | 0b10000000));
			break;
		}
		default: {
		}
		}
	}

	/**
	 * L�scht ein einzelnes Bit an bestimmer Speicheradresse
	 * 
	 * @param position
	 *            Gibt das Bit an, welches gel�scht wird.
	 * @param adress
	 *            Gibt die Adresse an, an der das Bit gel�scht wird.
	 */
	public void clear_Bit(int position, int adress) {
		switch (position) {
		case 1: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11111110));
			break;
		}
		case 2: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11111101));
			break;
		}
		case 3: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11111011));
			break;
		}
		case 4: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11110111));
			break;
		}
		case 5: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11101111));
			break;
		}
		case 6: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b11011111));
			break;
		}
		case 7: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b10111111));
			break;
		}
		case 8: {
			setRegisterEntryOneBit(adress, (getRegisterEntry(adress) & 0b01111111));
			break;
		}
		default: {
		}
		}
	}

	/**
	 * �berpr�ft ein einzelnes Bit an bestimmer Speicheradresse
	 * 
	 * @param position
	 *            Gibt das Bit an, welches �berpr�ft wird.
	 * @param adress
	 *            Gibt die Adresse an, an der sich das Bit befindet.
	 *
	 * @return boolean, true, wenn das Bit gesetzt ist. False, wenn nicht.
	 */
	public boolean checkBitSet(int position, int adress) {
		switch (position) {
		case 0: {
			int d = getRegisterEntry(adress) & 0b00000001;
			if (d == 1) {
				return true;
			} else {
				return false;
			}
		}
		case 1: {
			int d = getRegisterEntry(adress) & 0b00000010;
			if (d == 2) {
				return true;
			} else {
				return false;
			}
		}
		case 2: {
			int d = getRegisterEntry(adress) & 0b00000100;
			if (d == 4) {
				return true;
			} else {
				return false;
			}
		}
		case 3: {
			int d = getRegisterEntry(adress) & 0b00001000;
			if (d == 8) {
				return true;
			} else {
				return false;
			}
		}
		case 4: {
			int d = getRegisterEntry(adress) & 0b00010000;
			if (d == 16) {
				return true;
			} else {
				return false;
			}
		}
		case 5: {
			int d = getRegisterEntry(adress) & 0b00100000;
			if (d == 32) {
				return true;
			} else {
				return false;
			}
		}
		case 6: {
			int d = getRegisterEntry(adress) & 0b01000000;
			if (d == 64) {
				return true;
			} else {
				return false;
			}
		}
		case 7: {
			int d = getRegisterEntry(adress) & 0b10000000;
			if (d == 128) {
				return true;
			} else {
				return false;
			}
		}
		default:
			return false;
		}
	}
	

	public boolean getMode() {
		return mode;
	}

	public void setMode(boolean mode) {
		this.mode = mode;
	}

	/**
	 * @return den Vorteiler
	 */
	public int getPrescaler() {
		return prescaler;
	}

	/**
	 * Setzt den Vorteiler
	 * 
	 * @param prescaler
	 */
	public void setPrescaler(int prescaler) {
		this.prescaler = prescaler;
	}

	/**
	 * Erh�ht den Prescaler um 1
	 */
	public void incrPrescaler() {
		prescaler++;
	}
}
