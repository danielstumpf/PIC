package dhbw.sysnprog.pic.view;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class CreateItem {

	/**
	 * Erzeugung eines MenüItems
	 * 
	 * @param parent
	 * @param style
	 * @param menuName
	 * @return
	 */
	public static MenuItem getMenuItem(Menu parent, int style, final String menuName){
		MenuItem menuitem =  new MenuItem(parent, style);
		menuitem.setText(menuName);
		return menuitem;
	}

	/**
	 * Erzeugung eines Menüs
	 * 
	 * @param parent
	 * @param style
	 * @return
	 */
	static Menu getMenu(Shell parent, int style) {
		Menu menu = new Menu(parent, style);
		return menu;
	}
}