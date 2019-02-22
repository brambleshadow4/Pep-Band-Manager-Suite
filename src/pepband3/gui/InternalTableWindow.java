package pepband3.gui;

import pepband3.gui.extra.*;
import pepband3.gui.component.*;

public abstract class InternalTableWindow extends InternalWindow {
	
	protected BandTable bandTable;
	
	public InternalTableWindow(String title, boolean a, boolean b, boolean c, boolean d) {
		super(title, a, b, c, d);
	}
	
	protected void closingOperations() {
		bandTable.uninstall();
		super.closingOperations();
	}
	
	public BandTable getBandTable() {
		return bandTable;
	}
	
	public int highlight(DataField fieldValue, Comparable value) {
		return bandTable.highlight(fieldValue, value);
	}
}