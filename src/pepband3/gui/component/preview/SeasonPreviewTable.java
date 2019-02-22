package pepband3.gui.component.preview;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.component.tab.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public class SeasonPreviewTable extends PreviewTable {
	
	private static final String[] SORT_MODES = {"Date","Event Type"};
	private static final int DATE_COL_WIDTH = 75;
	
	private DefaultComboBoxModel sortModeModel;
	private JComboBox sortModeBox;
	private FilterComboBoxModel eventTypeFilterModel;
	private JComboBox eventTypeFilterBox;
	private FilterComboBoxModel locationFilterModel;
	private JComboBox locationFilterBox;
	private SpinnerDateModel startDateModel;
	private JSpinner startDateSpinner;
	private SpinnerDateModel endDateModel;
	private JSpinner endDateSpinner;
	
	private Season season;
	
	public SeasonPreviewTable() {
		super();
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		
	}
	
	private void a2Components() {
		getHeaderTable().setSelectionModel(new AntiSelectionModel());
		
		sortModeModel = new DefaultComboBoxModel(SORT_MODES);
		sortModeBox = new JComboBox(sortModeModel);
		
		eventTypeFilterModel = new FilterComboBoxModel(DataField.EVENT_TYPE, season);
		eventTypeFilterBox = new JComboBox(eventTypeFilterModel);
		eventTypeFilterBox.setRenderer(new ListRenderer());
		
		locationFilterModel = new FilterComboBoxModel(DataField.LOCATION, season);
		locationFilterBox = new JComboBox(locationFilterModel);
		locationFilterBox.setRenderer(new ListRenderer());
		
		startDateModel = new SpinnerDateModel();
		startDateSpinner = new JSpinner(startDateModel);
		
		endDateModel = new SpinnerDateModel();
		endDateSpinner = new JSpinner(endDateModel);
	}
	
	private void a3Listeners() {
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() == sortModeBox && e.getStateChange() == ItemEvent.SELECTED) {
					setTables();
				} else if (e.getSource() == eventTypeFilterBox && e.getStateChange() == ItemEvent.SELECTED) {
					setTables();
				} else if (e.getSource() == locationFilterBox && e.getStateChange() == ItemEvent.SELECTED) {
					setTables();
				}
			}
		};
		sortModeBox.addItemListener(itemListener);
		eventTypeFilterBox.addItemListener(itemListener);
		locationFilterBox.addItemListener(itemListener);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() == startDateSpinner) {
					setTables();
				} else if (e.getSource() == endDateSpinner) {
					setTables();
				}
			}
		};
		startDateSpinner.addChangeListener(changeListener);
		endDateSpinner.addChangeListener(changeListener);
	}
	
	private void a4Layouts() {
		optionsPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 0; c.weighty = 0;
		c.insets = new Insets(GUIManager.INS, GUIManager.INS, GUIManager.INS, GUIManager.INS);
		
		c.gridx = 0; c.gridy = 0;
		optionsPanel.add(new JLabel("Sort Mode:"), c);
		c.gridx = 0; c.gridy = 1;
		optionsPanel.add(sortModeBox, c);
		c.gridx = 1; c.gridy = 0;
		optionsPanel.add(new JLabel("Filter Event Type:"), c);
		c.gridx = 1; c.gridy = 1;
		optionsPanel.add(eventTypeFilterBox, c);
		c.gridx = 2; c.gridy = 0;
		optionsPanel.add(new JLabel("Filter Location:"), c);
		c.gridx = 2; c.gridy = 1;
		optionsPanel.add(locationFilterBox, c);
		c.gridx = 3; c.gridy = 0;
		optionsPanel.add(new JLabel("Start Date:"), c);
		c.gridx = 3; c.gridy = 1;
		optionsPanel.add(startDateSpinner, c);
		c.gridx = 4; c.gridy = 0;
		optionsPanel.add(new JLabel("End Date:"), c);
		c.gridx = 4; c.gridy = 1;
		optionsPanel.add(endDateSpinner, c);
		c.gridheight = 2;
		c.weightx = 1; c.weighty = 0;
		c.gridx = 5; c.gridy = 0;
		optionsPanel.add(Box.createHorizontalGlue(), c);
	}
	
	private void a5Initialize() {
		
	}
	
	public Season getSeason() {
		return season;
	}
	
	public void setSeason(Season value) {
		if (value != null) {
			season = value;
			setInitialFilterValues();
			setTables();
		}
	}
	
	private void setInitialFilterValues() {
		sortModeModel.setSelectedItem(SORT_MODES[0]);
		eventTypeFilterModel.setSeason(season);
		locationFilterModel.setSeason(season);
		Date start = season.getStartingDate();
		Date end = season.getEvents().isEmpty() ? season.getStartingDate() : season.getEvents().get(season.getEvents().size() - 1).getDate();
		startDateModel.setValue(start);
		startDateModel.setStart(start);
		startDateModel.setEnd(end);
		endDateModel.setValue(end);
		endDateModel.setStart(start);
		endDateModel.setEnd(end);
	}
	
	protected void setTables() {
		UneditableTableModel headerModel = new UneditableTableModel();
		Vector<Object> modelData = new Vector<Object>();
		String dateString = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(Calendar.getInstance().getTime());
		modelData.add("Pep Band Events for " + season.getName());
		modelData.add("Reported " + dateString);
		modelData.add("");
		headerModel.addColumn("Header", modelData);
		
		UneditableTableModel dataModel = new UneditableTableModel();
		Vector<Object> dateColumn = new Vector<Object>();
		Vector<Object> eventTypeColumn = new Vector<Object>();
		Vector<Object> locationColumn = new Vector<Object>();
		Vector<Object> nameColumn = new Vector<Object>();
		Vector<Object> pointValueColumn = new Vector<Object>();
		
		dateColumn.add(new PreviewRenderer.HeaderIdentifier("Date"));
		eventTypeColumn.add(new PreviewRenderer.HeaderIdentifier("Event Type"));
		locationColumn.add(new PreviewRenderer.HeaderIdentifier("Location"));
		nameColumn.add(new PreviewRenderer.HeaderIdentifier("Name"));
		pointValueColumn.add(new PreviewRenderer.HeaderIdentifier("Point Value"));
		
		dateColumn.add("");
		eventTypeColumn.add("");
		locationColumn.add("");
		nameColumn.add("");
		pointValueColumn.add("");
		
		ArrayList<PepBandEvent> events = new ArrayList<PepBandEvent>();
		for (PepBandEvent event : season.getEvents()) {
			boolean startDateCheck = event.getDate().equals(startDateModel.getDate()) || event.getDate().after(startDateModel.getDate());
			boolean endDateCheck = event.getDate().equals(endDateModel.getDate()) || event.getDate().before(endDateModel.getDate());
			boolean eventTypeCheck = true;
			boolean locationCheck = true;
			if (!eventTypeFilterModel.getSelectedItem().equals(FilterTab.ALL_STRING)) {
				eventTypeCheck = event.getEventType().equals(eventTypeFilterModel.getSelectedItem());
			}
			if (!locationFilterModel.getSelectedItem().equals(FilterTab.ALL_STRING)) {
				if (event.getEventType().getHasLocation()) {
					locationCheck = event.getLocation().equals(locationFilterModel.getSelectedItem());
				} else {
					locationCheck = false;
				}
			}
			if (startDateCheck && endDateCheck && eventTypeCheck && locationCheck) {
				events.add(event);
			}
		}
		
		EventComparator eventComparator = null;
		if (SORT_MODES[0].equals(sortModeBox.getSelectedItem())) {
			eventComparator = new EventComparator(DataField.DATE);
		} else if (SORT_MODES[1].equals(sortModeBox.getSelectedItem())) {
			eventComparator = new EventComparator(DataField.EVENT_TYPE);
		} else {
			eventComparator = new EventComparator(DataField.DATE);
		}
		Collections.sort(events, eventComparator);
		
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		
		EventType eventType = null;
		for (int index = 0; index < events.size(); index ++) {
			if (SORT_MODES[1].equals(sortModeModel.getSelectedItem())) {
				if (eventType == null) {
					eventType = events.get(index).getEventType();
				} else if (!eventType.equals(events.get(index).getEventType())) {
					eventType = events.get(index).getEventType();
					dateColumn.add("");
					eventTypeColumn.add("");
					locationColumn.add("");
					nameColumn.add("");
					pointValueColumn.add("");
				}
			}
			dateColumn.add(dateFormat.format(events.get(index).getDate()));
			if (events.get(index).getEventType().getHasLocation()) {
				eventTypeColumn.add(events.get(index).getEventType().getName() + " - " + events.get(index).getLocation().getName());
				locationColumn.add(events.get(index).getLocation().getName());
			} else {
				eventTypeColumn.add(events.get(index).getEventType().getName());
				locationColumn.add("");
			}
			nameColumn.add(events.get(index).getName());
			pointValueColumn.add(events.get(index).getPointValue().toString());
		}
		
		if (SORT_MODES[0].equals(sortModeBox.getSelectedItem())) {
			dataModel.addColumn("Date", dateColumn);
			dataModel.addColumn("Event Type", eventTypeColumn);
			dataModel.addColumn("Name", nameColumn);
		} else if (SORT_MODES[1].equals(sortModeBox.getSelectedItem())) {
			dataModel.addColumn("Event Type", eventTypeColumn);
			dataModel.addColumn("Date", dateColumn);
			dataModel.addColumn("Name", nameColumn);
		} else {
			dataModel.addColumn("Date", dateColumn);
			dataModel.addColumn("Event Type", eventTypeColumn);
			dataModel.addColumn("Name", nameColumn);
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
		
		if (SORT_MODES[0].equals(sortModeBox.getSelectedItem())) {
			memberTable.getColumnModel().getColumn(0).setPreferredWidth(DATE_COL_WIDTH);
			memberTable.getColumnModel().getColumn(0).setMaxWidth(DATE_COL_WIDTH);
			memberTable.getColumnModel().getColumn(0).setResizable(false);
		} else if (SORT_MODES[1].equals(sortModeBox.getSelectedItem())) {
			memberTable.getColumnModel().getColumn(1).setPreferredWidth(DATE_COL_WIDTH);
			memberTable.getColumnModel().getColumn(1).setMaxWidth(DATE_COL_WIDTH);
			memberTable.getColumnModel().getColumn(1).setResizable(false);
		}
	}
}