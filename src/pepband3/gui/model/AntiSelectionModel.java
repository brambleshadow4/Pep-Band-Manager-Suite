package pepband3.gui.model;

import javax.swing.*;
	
public class AntiSelectionModel extends DefaultListSelectionModel {
	
	public void addSelectionInterval(int index0, int index1) {
		clearSelection();
	}
	
	public void insertIndexInterval(int index, int length, boolean before) {
		clearSelection();
	}
	
	public void setAnchorSelectionIndex(int index) {
		clearSelection();
	}
	
	public void setLeadSelectionIndex(int index) {
		clearSelection();
	}
	
	public void setSelectionInterval(int index0, int index1) {
		clearSelection();
	}
}