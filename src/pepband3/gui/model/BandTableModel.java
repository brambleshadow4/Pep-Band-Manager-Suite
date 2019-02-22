package pepband3.gui.model;

import java.util.Collections;
import javax.swing.table.*;

import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.extra.*;

public class BandTableModel extends AbstractTableModel {
	
	private DataListener dataListener;
	private int columnCount;
	private Band band;
	
	public BandTableModel(Band paramBand, int paramColumnCount) {
		initialize();
		setBand(paramBand);
		setColumnCount(paramColumnCount);
	}
	
	public Band getBand() {
		return band;
	}
	
	public Class<?> getColumnClass(int column) {
		if (column >= 0 && column < getColumnCount()) {
			return Member.class;
		} else {
			return Object.class;
		}
	}
	
	public int getColumnCount() {
		return columnCount;
	}
	
	public String getColumnName(int column) {
		return "Column " + column;
	}
	
	public int getRowCount() {
		return band.size();
	}
	
	public Object getValueAt(int row, int column) {
		if (column >= 0 && column < getColumnCount()) {
			if (row >= 0 && row < getRowCount()) {
				return band.getMembers().get(row);
			} else {
				throw new IndexOutOfBoundsException("INVALID ROW FOR BAND TABLE MODEL: " + row + " OUT OF " + getRowCount());
			}
		} else {
			throw new IndexOutOfBoundsException("INVALID COLUMN FOR BAND TABLE MODEL: " + column + " OUT OF " + getColumnCount());
		}
	}
	
	private void initialize() {
		dataListener = new DataListener() {
			public void eventOccured(SourceEvent sourceEvent) {
				if (sourceEvent.isField()) {
					SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					if (fieldEvent.getOwner() instanceof Member && band.getMembers().contains((Member)fieldEvent.getOwner())) {
						int indexOfOwner = band.indexOfMember((Member) fieldEvent.getOwner());
						if (getRowCount() > 0) {
							fireTableRowsUpdated(indexOfOwner, indexOfOwner);
						}
					} else if (fieldEvent.getField() == DataField.NAME && fieldEvent.getOwner() instanceof Instrument) {
						if (getRowCount() > 0) {
							fireTableRowsUpdated(0, getRowCount() - 1);
						}
					} else if (fieldEvent.getField() == DataField.POINT_VALUE && fieldEvent.getOwner() instanceof PepBandEvent && !Collections.disjoint(((PepBandEvent) fieldEvent.getOwner()).getMembers(), band.getMembers())) {
						if (getRowCount() > 0) {
							fireTableRowsUpdated(0, getRowCount() - 1);
						}
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (listEvent.getOwner().equals(DataManager.getDataManager()) && listEvent.getElement() instanceof Instrument && listEvent.getType() == SourceEvent.List.ORDER) {
						if (getRowCount() > 0) {
							fireTableRowsUpdated(0, getRowCount() - 1);
						}
					} else if (listEvent.getOwner().equals(band) && listEvent.getElement() instanceof Member && listEvent.getType() == SourceEvent.List.ADD) {
						if (listEvent.isMultiIndex()) {
							fireTableRowsInserted(listEvent.getMinIndex(), listEvent.getMaxIndex());
						} else if (listEvent.isSingleIndex()) {
							fireTableRowsInserted(listEvent.getIndex(), listEvent.getIndex());
						}
					} else if (listEvent.getOwner().equals(band) && listEvent.getElement() instanceof Member && listEvent.getType() == SourceEvent.List.REMOVE) {
						if (listEvent.isMultiIndex()) {
							fireTableRowsDeleted(listEvent.getMinIndex(), listEvent.getMaxIndex());
						} else if (listEvent.isSingleIndex()) {
							fireTableRowsDeleted(listEvent.getIndex(), listEvent.getIndex());
						}
					} else if (listEvent.getElement() instanceof PepBandEvent && listEvent.getType() == SourceEvent.List.REMOVE) {
						if (getRowCount() > 0) {
							fireTableRowsUpdated(0, getRowCount() - 1);
						}
					}
				}
			}
		};
		DataManager.getDataManager().addDataListener(dataListener);
	}
	
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	public void setBand(Band value) {
		if (value != null) {
			band = value;
			fireTableDataChanged();
		} else {
			throw new NullPointerException("DO NOT SET BAND TABLE MODEL BAND TO NULL");
		}
	}
	
	public void setColumnCount(int value) {
		if (value >= 0) {
			int oldValue = columnCount;
			columnCount = value;
			fireTableStructureChanged();
		} else {
			throw new IllegalArgumentException("NUMBER IN COLUMNS IN BAND TABLE CANNOT BE " + value);
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		throw new IllegalStateException("BAND TABLE MODELS ARE NOT EDITABLE DIRECTLY VIA JTABLE");
	}
	
	public void uninstall() {
		DataManager.getDataManager().removeDataListener(dataListener);
	}
}