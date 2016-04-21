package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class Register {
	private static ArrayList<RegEntry> data;
	private static String name;
	private static PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(Register.class);

	public Register() {
	}

	public Register(String name, ArrayList<RegEntry> data) {
		super();
		Register.name = name;
		Register.data = data;
	}

	public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public static void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		propertyChangeSupport.firePropertyChange("name", Register.name, Register.name = name);
	}

	public static ArrayList<RegEntry> getData() {
		return data;
	}

	public static void setData(ArrayList<RegEntry> data) {
		propertyChangeSupport.firePropertyChange("data", Register.data, Register.data = data);
	}
	
	public static void setValue(int index, int value){
		int index2 = index%8;
		RegEntry regEntry = data.get(index2);
		int index3 = index%32;
		regEntry.setValue(index3, value);
	}

	@Override
	public String toString() {
		return name;
	}
}