package pepband3.gui.component.preview;

import java.text.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public class EventPreviewTable extends BandPreviewTable {
	
	private int columnCount;
	private PepBandEvent event;
	
	public EventPreviewTable() {
		super();
		
		columnCount = Tools.getInteger("Preview Column Count",3);
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		
	}
	
	private void a2Components() {
		columnCountModel.setValue(columnCount);
		outputTypeModel.removeElement(OUTPUT_TYPES[0]);
		outputTypeModel.removeElement(OUTPUT_TYPES[1]);
		outputTypeBox.setSelectedItem(OUTPUT_TYPES[2]);
		outputTypeBox.setEnabled(false);
		sortModeBox.setSelectedItem(SORT_MODES[2]);
		sortModeBox.setEnabled(false);
	}
	
	private void a3Listeners() {
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() == columnCountSpinner) {
					columnCount = (Integer) columnCountSpinner.getValue();
					Tools.setProperty("Preview Column Count", Integer.toString(columnCount));
					setTables();
				}
			}
		};
		columnCountSpinner.addChangeListener(changeListener);
	}
	
	private void a4Layouts() {
	}
	
	private void a5Initialize() {
		
	}
	
	private void addBlankRow(TreeMap<Integer, Vector<Object>> columnMap, int quantity) {
		for (int index = 0; index < columnCount; index++) {
			for (int blank = 0; blank < quantity; blank++) {
				columnMap.get(index).add("");
			}
		}
	}
	
	private void fillBlanks(TreeMap<Integer, Vector<Object>> columnMap) {
		int maxSize = 0;
		for (int index = 0; index < columnCount; index++) {
			maxSize = Math.max(maxSize, columnMap.get(index).size());
		}
		for (int index = 0; index < columnCount; index++) {
			while (columnMap.get(index).size() < maxSize) {
				columnMap.get(index).add("");
			}
		}
	}
	
	public PepBandEvent getEvent() {
		return event;
	}
	
	public void setEvent(PepBandEvent value) {
		if (value != null) {
			event = value;
			setTables();
		}
	}
	
	protected void setTables() {
		DefaultTableModel headerModel = new DefaultTableModel();
		Vector<Object> modelData = new Vector<Object>();
		modelData.add("Event: " + event.getName());
		modelData.add("Date: " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(event.getDate()));
		String pointsString = " Points";
		if (event.getPointValue() == 1) {
			pointsString = " Point";
		}
		if (event.getEventType().getHasLocation()) {
			modelData.add("Info: " + event.getEventType() + ", " + event.getLocation() + ", " + event.getPointValue() + pointsString);
		} else {
			modelData.add("Info: " + event.getEventType() + ", " + event.getPointValue() + pointsString);
		}
		modelData.add("");
		headerModel.addColumn("Header", modelData);
		
		UneditableTableModel dataModel = new UneditableTableModel();
		TreeMap<Integer, Vector<Object>> columnMap = new TreeMap<Integer, Vector<Object>>();
		for (int index = 0; index < columnCount; index++) {
			columnMap.put(index, new Vector<Object>());
		}
		
		ArrayList<Instrument> instruments = event.getInstruments();
		TreeMap<Instrument, ArrayList<Member>> memberMap = new TreeMap<Instrument, ArrayList<Member>>();
		for (int index = 0; index < instruments.size(); index ++) {
			ArrayList<Member> membersOfInstrument = event.getMembersOfInstrument(instruments.get(index));
			Collections.sort(membersOfInstrument, new MemberComparator(DataField.POINTS, event));
			memberMap.put(instruments.get(index), membersOfInstrument);
		}
		
		int row = 0;
		int column = 0;
		
		for (int index = 0; index < instruments.size(); index ++) {
			if (column >= columnCount) {
				row++;
				column = 0;
				fillBlanks(columnMap);
				addBlankRow(columnMap, 1);
			}
			
			columnMap.get(column).add(instruments.get(index));
			for (Member member : memberMap.get(instruments.get(index))) {
				columnMap.get(column).add(member);
			}
			
			column++;
		}
		fillBlanks(columnMap);
		
		for (int index = 0; index < columnCount; index++) {
			dataModel.addColumn("Column " + (index + 1), columnMap.get(index));
		}
		
		headerTable.setModel(headerModel);
		memberTable.setModel(dataModel);
		
		for (row = 0; row < headerTable.getRowCount(); row++) {
			if (row == 0) {
				headerTable.setRowHeight(row, 16);
			} else {
				headerTable.setRowHeight(row, 14);
			}
		}
		
		for (row = 0; row < memberTable.getRowCount(); row++) {
			memberTable.setRowHeight(row, 14);
			for (column = 0; column < memberTable.getColumnCount(); column++) {
				if (memberTable.getValueAt(row, column) instanceof Instrument) {
					memberTable.setRowHeight(row, 16);
					break;
				}
			}
		}
		
		for (column = 0; column < headerTable.getColumnCount(); column++) {
			headerTable.getColumnModel().getColumn(column).setCellRenderer(headerRenderer);
		}
		
		for (column = 0; column < memberTable.getColumnCount(); column++) {
			memberTable.getColumnModel().getColumn(column).setCellRenderer(memberRenderer);
		}
	}
}