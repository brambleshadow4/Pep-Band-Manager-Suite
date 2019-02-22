package pepband3.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.*;
import pepband3.gui.component.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.model.*;

public class EventTypesDialog extends JDialog {
	
	private EventTypesDialog eventTypesDialog;
	private DataListener dataListener;
	
	private GradientPanel northPanel;
	private JScrollPane scrollPane;
	private JList dataList;
	private JButton addButton;
	private JButton editButton;
	private JButton deleteButton;
	private JButton upButton;
	private JButton downButton;
	private EventTypeAddDialog eventTypeAddDialog;
	private EventTypeEditDialog eventTypeEditDialog;
	
	private Action addAction, editAction, deleteAction, upAction, downAction;
	
	public EventTypesDialog(JDialog owner) {
		super(owner,"Event Types",true);
		
		setIconImages(Tools.getWindowIcons());
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);
		setUndecorated(false);
		eventTypesDialog = this;
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		addAction = new EventTypesDialogAction("Add Event Type");
		editAction = new EventTypesDialogAction("Edit Event Type");
		deleteAction = new EventTypesDialogAction("Merge Event Type");
		upAction = new EventTypesDialogAction("Move Up");
		downAction = new EventTypesDialogAction("Move Down");
		
		addAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		addAction.putValue(Action.LONG_DESCRIPTION,"Add a new event type to the program's database");
		addAction.putValue(Action.SHORT_DESCRIPTION,"Add new event type");
		
		editAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
		editAction.putValue(Action.LONG_DESCRIPTION,"Edit the selected event type ");
		editAction.putValue(Action.SHORT_DESCRIPTION,"Edit selected event type");
		
		deleteAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_M);
		deleteAction.putValue(Action.LONG_DESCRIPTION,"Merge the selected event type with another event type (all references to the selected event type will be updated to the event type it was merged into)");
		deleteAction.putValue(Action.SHORT_DESCRIPTION,"Merge selected event type");
		
		upAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_U);
		upAction.putValue(Action.LONG_DESCRIPTION,"Move the selected event type up in the list");
		upAction.putValue(Action.SHORT_DESCRIPTION,"Move event type up");
		
		downAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
		downAction.putValue(Action.LONG_DESCRIPTION,"Move the selected event type down in the list");
		downAction.putValue(Action.SHORT_DESCRIPTION,"Move event type down");
	}
	
	private void a2Components() {
		eventTypeAddDialog = new EventTypeAddDialog(this);
		Tools.addRootComponent(eventTypeAddDialog);
		eventTypeEditDialog = new EventTypeEditDialog(this);
		Tools.addRootComponent(eventTypeEditDialog);
		
		northPanel = new GradientPanel("Event Types Manager",Tools.getHeaderIcon("events"));
		
		dataList = new JList(new DataListModel(EventType.class));
		dataList.setCellRenderer(new ListRenderer());
		dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane = new JScrollPane(dataList);
		scrollPane.setPreferredSize(new Dimension(200,300));
		addButton = new JButton(addAction);
		editButton = new JButton(editAction);
		deleteButton = new JButton(deleteAction);
		upButton = new JButton(upAction);
		downButton = new JButton(downAction);
		
		getRootPane().setDefaultButton(addButton);
		
		Tools.applyScrollPopup(scrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(scrollPane.getVerticalScrollBar());
	}
	
	private void a3Listeners() {
		dataList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					int index = dataList.locationToIndex(e.getPoint());
				}
			}
		});
		dataList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = dataList.getSelectedIndex();
				if (index < 0) {
					setActionsEnabled(false);
				} else {
					setActionsEnabled(true);
				}
			}
		});
		dataListener = new DataListener() {
			public void eventOccured(SourceEvent sourceEvent) {
				DataListModel model = (DataListModel) dataList.getModel();
				if (sourceEvent.isField()) {
					SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					if (fieldEvent.getOwner() instanceof EventType) {
						int indexOfOwner = DataManager.getDataManager().indexOfEventType((EventType) fieldEvent.getOwner());
						if (model.getSize() > 0 && indexOfOwner > -1) {
							model.fireContentsChanged(fieldEvent.getOwner(), indexOfOwner, indexOfOwner);
						}
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (listEvent.getElement() instanceof EventType && listEvent.getType() == SourceEvent.List.ADD) {
						if (listEvent.isMultiIndex()) {
							model.fireIntervalAdded(listEvent.getOwner(), listEvent.getMinIndex(), listEvent.getMaxIndex());
						} else if (listEvent.isSingleIndex()) {
							model.fireIntervalAdded(listEvent.getOwner(), listEvent.getIndex(), listEvent.getIndex());
						}
					} else if (listEvent.getElement() instanceof EventType && listEvent.getType() == SourceEvent.List.REMOVE) {
						if (listEvent.isMultiIndex()) {
							model.fireIntervalRemoved(listEvent.getOwner(), listEvent.getMinIndex(), listEvent.getMaxIndex());
						} else if (listEvent.isSingleIndex()) {
							model.fireIntervalRemoved(listEvent.getOwner(), listEvent.getIndex(), listEvent.getIndex());
						}
					} else if (listEvent.getElement() instanceof EventType && listEvent.getType() == SourceEvent.List.ORDER) {
						if (listEvent.isMultiIndex()) {
							model.fireContentsChanged(listEvent.getOwner(), listEvent.getMinIndex(), listEvent.getMaxIndex());
						} else if (listEvent.isSingleIndex()) {
							model.fireContentsChanged(listEvent.getOwner(), listEvent.getIndex(), listEvent.getIndex());
						}
					}
				}
			}
		};
		DataManager.getDataManager().addDataListener(dataListener);
	}
	
	private void a4Layouts() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 2; c.gridheight = 1;
		c.weightx = 1; c.weighty = 0;
		c.gridx = 0; c.gridy = 0;
		add(northPanel,c);
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		c.gridwidth = 1; c.gridheight = 7;
		c.gridx = 0; c.gridy = 1;
		add(scrollPane,c);
		c.weightx = 0; c.weighty = 0;
		c.gridwidth = 1; c.gridheight = 1;
		c.gridx = 1; c.gridy = 1;
		add(addButton,c);
		c.gridx = 1; c.gridy = 2;
		add(editButton,c);
		c.gridx = 1; c.gridy = 3;
		add(deleteButton,c);
		c.gridx = 1; c.gridy = 4;
		add(new JSeparator(SwingConstants.HORIZONTAL),c);
		c.gridx = 1; c.gridy = 5;
		add(upButton,c);
		c.gridx = 1; c.gridy = 6;
		add(downButton,c);
		c.weightx = 0; c.weighty = 1;
		c.gridx = 1; c.gridy = 7;
		add(Box.createVerticalGlue(),c);
	}
	
	private void a5Initialize() {
		dataList.setSelectedValue(DataManager.getDataManager().getEventTypes().get(0),true);	
	}
	
	public void display() {
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}
	
	private void setActionsEnabled(boolean value) {
		editAction.setEnabled(value);
		deleteAction.setEnabled(value);
		upAction.setEnabled(value);
		downAction.setEnabled(value);
	}
	
	private class EventTypesDialogAction extends AbstractAction {
		public EventTypesDialogAction() {
			super();
		}
		
		public EventTypesDialogAction(String value) {
			super(value);
		}
		 
		public void actionPerformed(ActionEvent e) {
			if (this == addAction) {
				eventTypeAddDialog.display();
			} else if (this == editAction) {
				EventType selectedEventType = (EventType)dataList.getSelectedValue();
				eventTypeEditDialog.display(selectedEventType);
			} else if (this == deleteAction) {
				DataManager dataManager = DataManager.getDataManager();
				if (dataManager.getEventTypes().size() == 1) {
					JOptionPane.showMessageDialog(eventTypesDialog, "You cannot merge the last remaining event type.", "No no...", JOptionPane.ERROR_MESSAGE);
					return;
				}
				EventType selectedEventType = (EventType)dataList.getSelectedValue();
				int total = 0;
				for (Season season : dataManager.getSeasons()) {
					for (PepBandEvent event : season.getEvents()) {
						if (event.getEventType().equals(selectedEventType)) {
							total++;
						}
					}
				}
				if (total == 0) {
					dataManager.mergeEventType(selectedEventType,dataManager.getEventTypes().get(0));
				} else {
					String[] message = {total + " events are currently associated with " + selectedEventType.getName() + ".",
									"Are you sure you want to merge it with another event type?"};
					int result = JOptionPane.showConfirmDialog(eventTypesDialog,message,"Are Ya Sure?",JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						ArrayList<EventType> eventTypes = new ArrayList<EventType>(dataManager.getEventTypes());
						eventTypes.remove(selectedEventType);
						EventType merge = (EventType)JOptionPane.showInputDialog(eventTypesDialog, "Select which event type " + selectedEventType.getName() + " will be merged into:", "Select Merger", JOptionPane.QUESTION_MESSAGE, null, eventTypes.toArray(), eventTypes.get(0));
						if (merge != null) {
							dataManager.mergeEventType(selectedEventType,merge);
						}
					}
				}
			} else if (this == upAction) {
				EventType selectedEventType = (EventType)dataList.getSelectedValue();
				DataManager.getDataManager().changeIndex(selectedEventType,-1);
				dataList.setSelectedValue(selectedEventType,true);
			} else if (this == downAction) {
				EventType selectedEventType = (EventType)dataList.getSelectedValue();
				DataManager.getDataManager().changeIndex(selectedEventType,1);
				dataList.setSelectedValue(selectedEventType,true);
			}
		}
	}
}