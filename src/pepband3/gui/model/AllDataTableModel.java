package pepband3.gui.model;

import java.util.*;
import javax.swing.table.*;

import pepband3.data.*;
import pepband3.gui.extra.*;

public class AllDataTableModel extends AbstractTableModel {
	
	private static final int COL_COUNT = 3;
	
	private ArrayList<PepBandData> dataList;
	
	public AllDataTableModel(ArrayList<PepBandData> dataList) {
		this.dataList = dataList;
	}
	
	public Class<?> getColumnClass(int column) {
		if (column >= 0 && column < getColumnCount()) {
			return PepBandData.class;
		} else {
			return Object.class;
		}
	}
	
	public int getColumnCount() {
		return COL_COUNT;
	}
	
	public String getColumnName(int column) {
		switch (column) {
			case 0: return "ID";
			case 1: return "Type";
			case 2: return "Title";
			default: return null;
		}
	}
	
	public Integer getColumnWidth(int column) {
		switch (column) {
			case 0: return 50;
			case 1: return 125;
			case 2: return 275;
			default: return null;
		}
	}
	
	public DataField getDataField(int column) {
		switch (column) {
			case 0: return DataField.ID;
			case 1: return DataField.DATA_TYPE;
			case 2: return DataField.FULL_NAME;
			default: return null;
		}
	}
	
	public int getRowCount() {
		return dataList.size();
	}
	
	public Object getValueAt(int row, int column) {
		if (column >= 0 && column < getColumnCount()) {
			if (row >= 0 && row < getRowCount()) {
				return dataList.get(row);
			} else {
				throw new IndexOutOfBoundsException("INVALID ROW FOR ALL DATA TABLE MODEL: " + row + " OUT OF " + getRowCount());
			}
		} else {
			throw new IndexOutOfBoundsException("INVALID COLUMN FOR ALL DATA TABLE MODEL: " + column + " OUT OF " + getColumnCount());
		}
	}
	
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	public void setValueAt(Object value, int row, int column) {
		throw new IllegalStateException("ALL DATA TABLE MODELS ARE NOT EDITABLE DIRECTLY VIA JTABLE");
	}
}