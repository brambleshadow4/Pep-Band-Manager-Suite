package pepband3.gui.model;

import javax.swing.*;
import pepband3.gui.component.*;

public class BandTableSelectionModel extends DefaultListSelectionModel {
	
	private BandTable bandTable;
	
	public BandTableSelectionModel(BandTable table) {
		bandTable = table;
	}
	
	public BandTable getBandTable() {
		return bandTable;
	}
}