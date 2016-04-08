package simulator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class CreateItems {

	public static MenuItem getMenuPoint(Menu parent, final String menuName){
		MenuItem menuitem =  new MenuItem(parent, SWT.PUSH);
		menuitem.setText(menuName);
		return menuitem;
	}
}
