package pepband3.gui.model;

import javax.swing.table.*;
	
public class UneditableTableModel extends DefaultTableModel {
	
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}