package model;

import java.util.ArrayList;

public enum RegisterModelProvider {
	INSTANCE;

	private static final int COLUMNS = 8;
	private static final int ROWS = 32;

	private RegisterModelProvider() {

		Register.setName("data");
		// Image here some fancy database access to read the persons and to
		// put them into the model

		ArrayList<RegEntry> data = new ArrayList<RegEntry>();
		int[] values = { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
		for (int i = 0; i < ROWS; i++) {
			data.add(new RegEntry(values));
		}
		Register.setData(data);
	}

	public ArrayList<RegEntry> getData() {
		ArrayList<RegEntry> data = Register.getData();
		return data;
	}
}
