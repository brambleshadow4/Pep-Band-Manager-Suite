package pepband3.gui.component.preview;

import java.awt.event.*;
import java.text.*;
import java.util.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public class RosterPreviewTable extends BandPreviewTable {
	
	private Season season;
	private Object previousSort;
	
	public RosterPreviewTable() {
		super();
		
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a2Components() {
		getHeaderTable().setSelectionModel(new AntiSelectionModel());
		outputTypeModel.removeElement(OUTPUT_TYPES[2]);
		outputTypeBox.setSelectedItem(OUTPUT_TYPES[0]);
		sortModeModel.setSelectedItem(SORT_MODES[2]);
		sortModeBox.setEnabled(false);
		nameTypeBox.setEnabled(true);
		columnCountModel.setValue(2);
		columnCountSpinner.setEnabled(false);
	}
	
	private void a3Listeners() {
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() == outputTypeBox && e.getStateChange() == ItemEvent.SELECTED) {
					if (OUTPUT_TYPES[0].equals(outputTypeBox.getSelectedItem())) {
						previousSort = sortModeModel.getSelectedItem();
						sortModeModel.setSelectedItem(SORT_MODES[2]);
						sortModeBox.setEnabled(false);
						nameTypeBox.setEnabled(true);
						columnCountSpinner.setValue(2);
					} else if (OUTPUT_TYPES[1].equals(outputTypeBox.getSelectedItem())) {
						sortModeModel.setSelectedItem(previousSort != null ? previousSort : SORT_MODES[0]);
						sortModeBox.setEnabled(true);
						nameTypeBox.setEnabled(false);
						columnCountSpinner.setValue(5);
					}
					Tools.getProgramRoot().getFileChooser().setSelectedFile(null);
					setTables();
				} else if (e.getSource() == sortModeBox && e.getStateChange() == ItemEvent.SELECTED) {
					setTables();
				}
			}
		};
		outputTypeBox.addItemListener(itemListener);
		sortModeBox.addItemListener(itemListener);
	}
	
	private void a4Layouts() {
	}
	
	private void a5Initialize() {
		
	}
	
	public Season getSeason() {
		return season;
	}
	
	public boolean isPointsPreview() {
		return OUTPUT_TYPES[0].equals(outputTypeBox.getSelectedItem());
	}
	
	public boolean isRosterPreview() {
		return OUTPUT_TYPES[1].equals(outputTypeBox.getSelectedItem());
	}
	
	public void setSeason(Season value) {
		if (value != null) {
			season = value;
			setTables();
		}
	}
	
	protected void setTables() {
		String selectedItem = (String)outputTypeBox.getSelectedItem();
		if (selectedItem.equals(OUTPUT_TYPES[0])) {
			setTablesForPoints();
		} else if (selectedItem.equals(OUTPUT_TYPES[1])) {
			setTablesForRoster();
		}
	}
	
	private void setTablesForPoints() {
		UneditableTableModel headerModel = new UneditableTableModel();
		Vector<Object> modelData = new Vector<Object>();
		String dateString = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(Calendar.getInstance().getTime());
		modelData.add("Pep Band Points");
		modelData.add("Reported " + dateString);
		modelData.add("");
		headerModel.addColumn("Header", modelData);
		
		UneditableTableModel dataModel = new UneditableTableModel();
		Vector<Object> dataColumn = new Vector<Object>();
		Vector<Object> pointColumn = new Vector<Object>();
		
		ArrayList<Instrument> instruments = season.getInstruments();
		TreeMap<Instrument, ArrayList<Member>> memberMap = new TreeMap<Instrument, ArrayList<Member>>();
		for (int index = 0; index < instruments.size(); index ++) {
			ArrayList<Member> membersOfInstrument = season.getMembersOfInstrument(instruments.get(index));
			Collections.sort(membersOfInstrument, new MemberComparator(DataField.POINTS, season));
			memberMap.put(instruments.get(index), membersOfInstrument);
		}
		
		for (int index = 0; index < instruments.size(); index ++) {
			dataColumn.add(instruments.get(index));
			pointColumn.add("");
			
			for (Member member : memberMap.get(instruments.get(index))) {
				
				if(member.getPoints(season) != 0) {
					dataColumn.add(member);
					pointColumn.add(member.getPoints(season));
				}
				
			}
			if (index != instruments.size() - 1) {
				dataColumn.add("");
				pointColumn.add("");
			}
		}
		
		dataModel.addColumn("Data", dataColumn);
		dataModel.addColumn("Points", pointColumn);
		
		headerTable.setModel(headerModel);
		memberTable.setModel(dataModel);
		
		for (int row = 0; row < headerTable.getRowCount(); row++) {
			if (row == 0) {
				headerTable.setRowHeight(row, 16);
			} else {
				headerTable.setRowHeight(row, 14);
			}
		}
		
		for (int row = 0; row < memberTable.getRowCount(); row++) {
			if (memberTable.getValueAt(row, 0) instanceof Instrument) {
				memberTable.setRowHeight(row, 16);
			} else {
				memberTable.setRowHeight(row, 14);
			}
		}
		
		for (int column = 0; column < headerTable.getColumnCount(); column++) {
			headerTable.getColumnModel().getColumn(column).setCellRenderer(headerRenderer);
		}
		
		for (int column = 0; column < memberTable.getColumnCount(); column++) {
			memberTable.getColumnModel().getColumn(column).setCellRenderer(memberRenderer);
		}
	}
	
	private void setTablesForRoster() {
		UneditableTableModel headerModel = new UneditableTableModel();
		Vector<Object> modelData = new Vector<Object>();
		String dateString = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(Calendar.getInstance().getTime());
		modelData.add("Pep Band Roster for " + season.getName());
		modelData.add("Reported " + dateString);
		modelData.add("");
		headerModel.addColumn("Header", modelData);
		
		UneditableTableModel dataModel = new UneditableTableModel();
		Vector<Object> instrumentColumn = new Vector<Object>();
		Vector<Object> lastColumn = new Vector<Object>();
		Vector<Object> firstColumn = new Vector<Object>();
		Vector<Object> classColumn = new Vector<Object>();
		Vector<Object> netIDColumn = new Vector<Object>();
		
		lastColumn.add(new PreviewRenderer.HeaderIdentifier("Last Name"));
		firstColumn.add(new PreviewRenderer.HeaderIdentifier("First Name"));
		instrumentColumn.add(new PreviewRenderer.HeaderIdentifier("Instrument"));
		classColumn.add(new PreviewRenderer.HeaderIdentifier("Class"));
		netIDColumn.add(new PreviewRenderer.HeaderIdentifier("Net ID"));
		
		lastColumn.add("");
		firstColumn.add("");
		instrumentColumn.add("");
		classColumn.add("");
		netIDColumn.add("");
		
		ArrayList<Member> members = new ArrayList<Member>(season.getMembers());
		MemberComparator memberComparator = null;
		if (SORT_MODES[0].equals(sortModeBox.getSelectedItem())) {
			memberComparator = new MemberComparator(DataField.LAST_NAME, season);
		} else if (SORT_MODES[1].equals(sortModeBox.getSelectedItem())) {
			memberComparator = new MemberComparator(DataField.FIRST_NAME, season);
		} else if (SORT_MODES[2].equals(sortModeBox.getSelectedItem())) {
			memberComparator = new MemberComparator(DataField.INSTRUMENT, season);
		} else if (SORT_MODES[3].equals(sortModeBox.getSelectedItem())) {
			memberComparator = new MemberComparator(DataField.CLASS_YEAR, season);
		} else if (SORT_MODES[4].equals(sortModeBox.getSelectedItem())) {
			memberComparator = new MemberComparator(DataField.NET_ID, season);
		} else {
			memberComparator = new MemberComparator(DataField.NAME, season);
		}
		Collections.sort(members, memberComparator);
		
		for (int index = 0; index < members.size(); index ++) {
			lastColumn.add(members.get(index).getLastName());
			firstColumn.add(members.get(index).getFirstName());
			instrumentColumn.add(members.get(index).getInstrument().getName());
			classColumn.add(members.get(index).getClassYear().toString());
			netIDColumn.add(members.get(index).getNetID());
		}
		
		if (SORT_MODES[0].equals(sortModeBox.getSelectedItem())) {
			dataModel.addColumn("Last Name", lastColumn);
			dataModel.addColumn("First Name", firstColumn);
			dataModel.addColumn("Instrument", instrumentColumn);
			dataModel.addColumn("Class", classColumn);
			dataModel.addColumn("Net ID", netIDColumn);
		} else if (SORT_MODES[1].equals(sortModeBox.getSelectedItem())) {
			dataModel.addColumn("First Name", firstColumn);
			dataModel.addColumn("Last Name", lastColumn);
			dataModel.addColumn("Instrument", instrumentColumn);
			dataModel.addColumn("Class", classColumn);
			dataModel.addColumn("Net ID", netIDColumn);
		} else if (SORT_MODES[2].equals(sortModeBox.getSelectedItem())) {
			dataModel.addColumn("Instrument", instrumentColumn);
			dataModel.addColumn("Last Name", lastColumn);
			dataModel.addColumn("First Name", firstColumn);
			dataModel.addColumn("Class", classColumn);
			dataModel.addColumn("Net ID", netIDColumn);
		} else if (SORT_MODES[3].equals(sortModeBox.getSelectedItem())) {
			dataModel.addColumn("Class", classColumn);
			dataModel.addColumn("Instrument", instrumentColumn);
			dataModel.addColumn("Last Name", lastColumn);
			dataModel.addColumn("First Name", firstColumn);
			dataModel.addColumn("Net ID", netIDColumn);
		} else if (SORT_MODES[4].equals(sortModeBox.getSelectedItem())) {
			dataModel.addColumn("Net ID", netIDColumn);
			dataModel.addColumn("First Name", firstColumn);
			dataModel.addColumn("Last Name", lastColumn);
			dataModel.addColumn("Instrument", instrumentColumn);
			dataModel.addColumn("Class", classColumn);
		} else {
			dataModel.addColumn("Last Name", lastColumn);
			dataModel.addColumn("First Name", firstColumn);
			dataModel.addColumn("Instrument", instrumentColumn);
			dataModel.addColumn("Class", classColumn);
			dataModel.addColumn("Net ID", netIDColumn);
		}
		
		headerTable.setModel(headerModel);
		memberTable.setModel(dataModel);
		
		for (int row = 0; row < headerTable.getRowCount(); row++) {
			if (row == 0) {
				headerTable.setRowHeight(row, 16);
			} else {
				headerTable.setRowHeight(row, 14);
			}
		}
		
		for (int row = 0; row < memberTable.getRowCount(); row++) {
			if (row == 0) {
				memberTable.setRowHeight(row, 16);
			} else {
				memberTable.setRowHeight(row, 14);
			}
		}
		
		for (int column = 0; column < headerTable.getColumnCount(); column++) {
			headerTable.getColumnModel().getColumn(column).setCellRenderer(headerRenderer);
		}
		
		for (int column = 0; column < memberTable.getColumnCount(); column++) {
			memberTable.getColumnModel().getColumn(column).setCellRenderer(memberRenderer);
		}
	}
}