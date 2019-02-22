package pepband3.gui.extra;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import pepband3.data.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.model.*;

public class ViewManager  {
	
	private static final int CAPACITY = 5;
	
	private ViewType viewType;
	private DataField nameField;
	private JTable table;
	private TableRenderer[] tableRenderers;
	private TableRowSorter<BandTableModel> rowSorter;
	private MemberComparator[] comparators;
	private BandTableFilter filter;
	
	public ViewManager(JTable paramTable, ViewType paramViewType, DataField paramNameType) {
		if (paramTable != null && paramViewType != null && paramNameType != null) {
			table = paramTable;
			viewType = paramViewType;
			nameField = paramNameType;
			initialize();
			addListeners();
			setViewType(paramViewType);
		} else {
			throw new NullPointerException("VIEW MANAGER CONSTRUCTOR HAS NULL");
		}
	}
	
	private void addListeners() {
		rowSorter.addRowSorterListener(new RowSorterListener() {
			public void sorterChanged(final RowSorterEvent e) {
				if (e.getType() == RowSorterEvent.Type.SORTED) {
					/* This is required due to Bug 6625663 which had not been resolved at the time this program was written */
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (e.getPreviousRowCount() == e.getSource().getViewRowCount()) {
								table.repaint();
							} else {
								table.revalidate();
							}
						}
					});
				}
			}
		});
	}
	
	private Band getBand() {
		return getBandTableModel().getBand();
	}
	
	private BandTableModel getBandTableModel() {
		if (table.getModel() instanceof BandTableModel) {
			return (BandTableModel)table.getModel();
		} else {
			throw new IllegalStateException("VIEW MANAGER IS MANAGING A TABLE WHOSE MODEL IS NOT A BANDTABLEMODEL");
		}
	}
	
	public BandTableFilter getFilter() {
		return filter;
	}
	
	private int[] getModelSelection() {
		int[] selectedView = table.getSelectedRows();
		int[] selectedModel = new int[selectedView.length];
		for (int index = 0; index < selectedView.length; index++) {
			selectedModel[index] = table.convertRowIndexToModel(selectedView[index]);
		}
		return selectedModel;
	}
	
	public DataField getNameField() {
		return nameField;
	}
	
	public ViewType getViewType() {
		return viewType;
	}
	
	private void initialize() {
		tableRenderers = new TableRenderer[CAPACITY];
		comparators = new MemberComparator[CAPACITY];
		rowSorter = new TableRowSorter<BandTableModel>(getBandTableModel());
		filter = new BandTableFilter(getBand());
		
		for (int index = 0; index < CAPACITY; index++) {
			tableRenderers[index] = new TableRenderer(nameField, getBand());
			comparators[index] = new MemberComparator(nameField, getBand());
		}
		
		rowSorter.setRowFilter(filter);
		rowSorter.setMaxSortKeys(1);
		rowSorter.setSortsOnUpdates(true);
		rowSorter.toggleSortOrder(0);
		
		table.setRowSorter(rowSorter);
	}
	
	private void refresh() {
		table.repaint();
		if (table.getTableHeader() != null) {
			table.getTableHeader().repaint();
		}
		sort();
	}
	
	public void setNameField(DataField value) {
		nameField = value;
		int index = 0;
		switch (viewType) {
			case EVENT : index = 1; break;
			case ROSTER : index = 0; break;
			case LIST : index = 0;
		}
		comparators[index].setSortMode(nameField);
		tableRenderers[index].setDataField(nameField);
		table.getColumnModel().getColumn(index).setHeaderValue(nameField);
		refresh();
	}
	
	private void setRowSelection(int[] selectedModel) {
		ListSelectionModel selectionModel = table.getSelectionModel();
		selectionModel.clearSelection();
		for (int index : selectedModel) {
			int row = table.convertRowIndexToView(index);
			if (row >= 0) {
				selectionModel.addSelectionInterval(row,row);
			}
		}
	}
	
	private void setSortKeys(List<? extends RowSorter.SortKey> value) {
		if (value == null || value.isEmpty() || value.get(0).getColumn() >= getBandTableModel().getColumnCount()) {
			rowSorter.toggleSortOrder(0);
		} else {
			rowSorter.setSortKeys(value);
		}
	}
	
	public void setViewType(ViewType value) {
		viewType = value;
		int[] selectedModel = getModelSelection();
		List<? extends RowSorter.SortKey> sortKeys = rowSorter.getSortKeys();
		if (viewType == ViewType.EVENT) {
			int columnCount = 3;
			getBandTableModel().setColumnCount(columnCount);
			
			DataField[] dataFields = new DataField[columnCount];
			dataFields[0] = DataField.INSTRUMENT;
			dataFields[1] = nameField;
			dataFields[2] = DataField.POINTS;
			
			for (int index = 0; index < columnCount; index++) {
				comparators[index].setSortMode(dataFields[index]);
				rowSorter.setComparator(index,comparators[index]);
				tableRenderers[index].setDataField(dataFields[index]);
				table.getColumnModel().getColumn(index).setCellRenderer(tableRenderers[index]);
				table.getColumnModel().getColumn(index).setHeaderValue(dataFields[index]);
			}
		} else if (viewType == ViewType.ROSTER) {
			int columnCount = 5;
			getBandTableModel().setColumnCount(columnCount);
			
			DataField[] dataFields = new DataField[columnCount];
			dataFields[0] = nameField;
			dataFields[1] = DataField.CLASS_YEAR;
			dataFields[2] = DataField.INSTRUMENT;
			dataFields[3] = DataField.NET_ID;
			dataFields[4] = DataField.POINTS;
			
			for (int index = 0; index < columnCount; index++) {
				comparators[index].setSortMode(dataFields[index]);
				rowSorter.setComparator(index,comparators[index]);
				tableRenderers[index].setDataField(dataFields[index]);
				table.getColumnModel().getColumn(index).setCellRenderer(tableRenderers[index]);
				table.getColumnModel().getColumn(index).setHeaderValue(dataFields[index]);
			}
		} else if (viewType == ViewType.LIST) {
			int columnCount = 1;
			getBandTableModel().setColumnCount(columnCount);
			
			DataField[] dataFields = new DataField[columnCount];
			dataFields[0] = nameField;
			
			for (int index = 0; index < columnCount; index++) {
				comparators[index].setSortMode(dataFields[index]);
				rowSorter.setComparator(index,comparators[index]);
				tableRenderers[index].setDataField(dataFields[index]);
				table.getColumnModel().getColumn(index).setCellRenderer(tableRenderers[index]);
				table.getColumnModel().getColumn(index).setHeaderValue(dataFields[index]);
			}	
		}
		setSortKeys(sortKeys);
		refresh();
		setRowSelection(selectedModel);
	}
	
	public void sort() {
		rowSorter.sort();
	}
}