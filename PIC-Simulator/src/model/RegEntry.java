package model;

public class RegEntry {
	int[] values;
	
	public RegEntry(int[] values) {
		super();
		this.values = values;
	}

	public int[] getValues() {
		return values;
	}
	
	public int getValue(int index){
		return values[index];
	}

	public void setValues(int[] values) {
		this.values = values;
	}
	
	public void setValue(int index, int value){
		values[index] = value;
	}

}
