package pepband3.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.component.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public class EventWindow extends InternalTableWindow {
	
	private JPanel northPanel;
		private JPanel topPanel;
			private JLabel nameLabel;
			private JTextField nameField;
		private JPanel bottomPanel;
			private JSpinner dateSpinner;
			private JComboBox eventTypeBox;
			private JComboBox locationBox;
			private JSpinner pointsSpinner;
	private JLabel southLabel;
	
	private DataListener dataListener;
	
	private PepBandEvent event;
	
	public EventWindow(PepBandEvent paramEvent) {
		super("Event - " + paramEvent.getName(), true, true, true, true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setFrameIcon(Tools.getEventIcon(paramEvent.getEventType().getIconName() + "16"));
		
		event = paramEvent;
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		
	}
	
	private void a2Components() {
		setMinimumSize(new Dimension(450,275));
		setPreferredSize(new Dimension(600,550));
		setTransferHandler(new DataTransferHandler());
		
		northPanel = new JPanel();
		northPanel.setBorder(new CompoundBorder(new EmptyBorder(2 * GUIManager.INS,2 * GUIManager.INS,2 * GUIManager.INS,2 * GUIManager.INS),OptionsDialog.createTitledBorder("")));
		topPanel = new JPanel();
		nameLabel = new JLabel("Name or Description: ");
		nameField = new JTextField(event.getName());
		nameField.setEditable(true);
		bottomPanel = new JPanel();
		dateSpinner = new JSpinner(new SpinnerDateModel(event.getDate(),event.getSeason().getStartingDate(),null,Calendar.DAY_OF_MONTH));
		dateSpinner.setPreferredSize(new Dimension(135,20));
		dateSpinner.setMinimumSize(new Dimension(135,20));
		JSpinner.DateEditor dateSpinnerEditor = new JSpinner.DateEditor(dateSpinner,"M/d/yyyy h:mm a");
		dateSpinner.setEditor(dateSpinnerEditor);
		eventTypeBox = new JComboBox(new DataComboBoxModel(EventType.class));
		eventTypeBox.setRenderer(new ListRenderer());
		eventTypeBox.setSelectedItem(event.getEventType());
		locationBox = new JComboBox(new DataComboBoxModel(Location.class));
		locationBox.setRenderer(new ListRenderer());
		locationBox.setSelectedItem(event.getLocation());
		locationBox.setVisible(event.getEventType().getHasLocation());
		pointsSpinner = new JSpinner(new SpinnerNumberModel(new Integer(event.getPointValue()),null,null,new Integer(1)));
		pointsSpinner.setPreferredSize(new Dimension(60,20));
		pointsSpinner.setMinimumSize(new Dimension(60,20));
		
		bandTable = new BandTable(event, ViewType.EVENT);
		bandTable.armAdditionTab(event.getSeason());
		bandTable.armOverviewTab();
		bandTable.armFilterTab();
		bandTable.installArmedTabs();
		
		southLabel = new JLabel("Attendance: " + event.size() + " Members");
		
		Tools.applyTextPopup(nameField);
		Tools.applyTextPopup(((JSpinner.DefaultEditor)dateSpinner.getEditor()).getTextField());
		Tools.applyTextPopup(((JSpinner.DefaultEditor)pointsSpinner.getEditor()).getTextField());
	}
	
	private void a3Listeners() {
		dataListener = new DataListener() {
			public void eventOccured(SourceEvent sourceEvent) {
				if (sourceEvent.isField()) {
					SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					if (fieldEvent.getField() == DataField.EVENT_TYPE && fieldEvent.getOwner().equals(event)) {
						locationBox.setVisible(event.getEventType().getHasLocation());
						bottomPanel.revalidate();
						setFrameIcon(Tools.getEventIcon(event.getEventType().getIconName() + "16"));
					} else if (fieldEvent.getField() == DataField.HAS_LOCATION && fieldEvent.getOwner().equals(event.getEventType())) {
						locationBox.setVisible(event.getEventType().getHasLocation());
						bottomPanel.revalidate();
					} else if (fieldEvent.getField() == DataField.ICON_NAME && fieldEvent.getOwner().equals(event.getEventType())) {
						setFrameIcon(Tools.getEventIcon(event.getEventType().getIconName() + "16"));
					} else if (fieldEvent.getField() == DataField.STARTING_DATE && fieldEvent.getOwner().equals(event.getSeason())) {
						((SpinnerDateModel) dateSpinner.getModel()).setStart(((Season) fieldEvent.getOwner()).getStartingDate());
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (listEvent.getOwner().equals(event) && (listEvent.getType() == SourceEvent.List.ADD || listEvent.getType() == SourceEvent.List.REMOVE)) {
						southLabel.setText("Event Attendance: " + event.size() + " Members");
					} else if (listEvent.getType() == SourceEvent.List.REMOVE && listEvent.getElement() instanceof EventType) {
						eventTypeBox.setSelectedItem(event.getEventType());
					} else if (listEvent.getType() == SourceEvent.List.REMOVE && listEvent.getElement() instanceof Location) {
						locationBox.setSelectedItem(event.getLocation());
					}
				}
			}
		};
		DataManager.getDataManager().addDataListener(dataListener);
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setEventName();
			}
			
			public void insertUpdate(DocumentEvent e) {
				setEventName();
			}
			
			public void removeUpdate(DocumentEvent e) {
				setEventName();
			}
		});
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() == pointsSpinner) {
					event.setPointValue((Integer)pointsSpinner.getValue());
				} else if (e.getSource() == dateSpinner) {
					event.setDate((Date)dateSpinner.getValue());
				}
			}
		};
		pointsSpinner.addChangeListener(changeListener);
		dateSpinner.addChangeListener(changeListener);
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() == eventTypeBox && e.getStateChange() == ItemEvent.SELECTED) {
					event.setEventType((EventType)eventTypeBox.getSelectedItem());
				} else if (e.getSource() == locationBox && e.getStateChange() == ItemEvent.SELECTED) {
					event.setLocation((Location)locationBox.getSelectedItem());
				}
			}
		};
		eventTypeBox.addItemListener(itemListener);
		locationBox.addItemListener(itemListener);
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout());
		northPanel.setLayout(new GridLayout(2,1,2 * GUIManager.INS,2 * GUIManager.INS));
		topPanel.setLayout(new BorderLayout(2 * GUIManager.INS,2 * GUIManager.INS));
		bottomPanel.setLayout(new GridBagLayout());
		
		topPanel.add(nameLabel,BorderLayout.WEST);
		topPanel.add(nameField,BorderLayout.CENTER);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		c.weightx = 0.2; c.weighty = 1;
		c.gridx = 0; c.gridy = 0;
		bottomPanel.add(dateSpinner,c);
		c.weightx = 0.3; c.weighty = 1;
		c.gridx = 1; c.gridy = 0;
		bottomPanel.add(eventTypeBox,c);
		c.weightx = 0.3; c.weighty = 1;
		c.gridx = 2; c.gridy = 0;
		bottomPanel.add(locationBox,c);
		c.weightx = 0.2; c.weighty = 1;
		c.gridx = 3; c.gridy = 0;
		bottomPanel.add(pointsSpinner,c);
		
		northPanel.add(topPanel,BorderLayout.NORTH);
		northPanel.add(bottomPanel,BorderLayout.SOUTH);
		
		add(northPanel,BorderLayout.NORTH);
		add(bandTable,BorderLayout.CENTER);
		add(southLabel,BorderLayout.SOUTH);
	}
	
	private void a5Initialize() {
		nameField.putClientProperty("tip","Enter the name (in the case of a sporting event) or description (in the case of a point adjustment or rehearsal) of the event");
		((JSpinner.DefaultEditor)dateSpinner.getEditor()).getTextField().putClientProperty("tip","Enter or spin to the date of the event");
		eventTypeBox.putClientProperty("tip","Select a type for this event");
		locationBox.putClientProperty("tip","Select a location for this event");
		((JSpinner.DefaultEditor)pointsSpinner.getEditor()).getTextField().putClientProperty("tip","Enter or spin to the point value of this event");
		southLabel.putClientProperty("tip","This label displays the total number of members present in the event's band");
		nameField.requestFocusInWindow();
		nameField.selectAll();
	}
	
	protected void closingOperations() {
		DataManager.getDataManager().removeDataListener(dataListener);
		((DataComboBoxModel) eventTypeBox.getModel()).uninstall();
		((DataComboBoxModel) locationBox.getModel()).uninstall();
		super.closingOperations();
	}
	
	public PepBandEvent getEvent() {
		return event;
	}
	
	public void setEventName() {
		event.setName(nameField.getText());
		setTitle("Event - " + nameField.getText());
	}
}