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

public class LocationsDialog extends JDialog {
	
	private LocationsDialog locationsDialog;
	private DataListener dataListener;
	
	private GradientPanel northPanel;
	private JScrollPane scrollPane;
	private JList dataList;
	private JButton addButton;
	private JButton editButton;
	private JButton deleteButton;
	private JButton upButton;
	private JButton downButton;
	private LocationAddDialog locationAddDialog;
	private LocationEditDialog locationEditDialog;
	
	private Action addAction, editAction, deleteAction, upAction, downAction;
	
	public LocationsDialog(JDialog owner) {
		super(owner,"Locations",true);
		
		setIconImages(Tools.getWindowIcons());
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);
		setUndecorated(false);
		locationsDialog = this;
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		addAction = new LocationsDialogAction("Add Location");
		editAction = new LocationsDialogAction("Edit Location");
		deleteAction = new LocationsDialogAction("Merge Location");
		upAction = new LocationsDialogAction("Move Up");
		downAction = new LocationsDialogAction("Move Down");
		
		addAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		addAction.putValue(Action.LONG_DESCRIPTION,"Add a new location to the program's database");
		addAction.putValue(Action.SHORT_DESCRIPTION,"Add new location");
		
		editAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
		editAction.putValue(Action.LONG_DESCRIPTION,"Edit the selected location ");
		editAction.putValue(Action.SHORT_DESCRIPTION,"Edit selected location");
		
		deleteAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_M);
		deleteAction.putValue(Action.LONG_DESCRIPTION,"Merge the selected location with another location (all references to the selected location will be updated to the location it was merged into)");
		deleteAction.putValue(Action.SHORT_DESCRIPTION,"Merge selected location");
		
		upAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_U);
		upAction.putValue(Action.LONG_DESCRIPTION,"Move the selected location up in the list");
		upAction.putValue(Action.SHORT_DESCRIPTION,"Move location up");
		
		downAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
		downAction.putValue(Action.LONG_DESCRIPTION,"Move the selected location down in the list");
		downAction.putValue(Action.SHORT_DESCRIPTION,"Move location down");
	}
	
	private void a2Components() {
		locationAddDialog = new LocationAddDialog(this);
		Tools.addRootComponent(locationAddDialog);
		locationEditDialog = new LocationEditDialog(this);
		Tools.addRootComponent(locationEditDialog);
		
		northPanel = new GradientPanel("Locations Manager",Tools.getHeaderIcon("locations"));
		
		dataList = new JList(new DataListModel(Location.class));
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
					if (fieldEvent.getOwner() instanceof Location) {
						int indexOfOwner = DataManager.getDataManager().indexOfLocation((Location) fieldEvent.getOwner());
						if (model.getSize() > 0 && indexOfOwner > -1) {
							model.fireContentsChanged(fieldEvent.getOwner(), indexOfOwner, indexOfOwner);
						}
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (listEvent.getElement() instanceof Location && listEvent.getType() == SourceEvent.List.ADD) {
						if (listEvent.isMultiIndex()) {
							model.fireIntervalAdded(listEvent.getOwner(), listEvent.getMinIndex(), listEvent.getMaxIndex());
						} else if (listEvent.isSingleIndex()) {
							model.fireIntervalAdded(listEvent.getOwner(), listEvent.getIndex(), listEvent.getIndex());
						}
					} else if (listEvent.getElement() instanceof Location && listEvent.getType() == SourceEvent.List.REMOVE) {
						if (listEvent.isMultiIndex()) {
							model.fireIntervalRemoved(listEvent.getOwner(), listEvent.getMinIndex(), listEvent.getMaxIndex());
						} else if (listEvent.isSingleIndex()) {
							model.fireIntervalRemoved(listEvent.getOwner(), listEvent.getIndex(), listEvent.getIndex());
						}
					} else if (listEvent.getElement() instanceof Location && listEvent.getType() == SourceEvent.List.ORDER) {
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
		dataList.setSelectedValue(DataManager.getDataManager().getLocations().get(0),true);	
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
	
	private class LocationsDialogAction extends AbstractAction {
		public LocationsDialogAction() {
			super();
		}
		
		public LocationsDialogAction(String value) {
			super(value);
		}
		 
		public void actionPerformed(ActionEvent e) {
			if (this == addAction) {
				locationAddDialog.display();
			} else if (this == editAction) {
				Location selectedLocation = (Location)dataList.getSelectedValue();
				locationEditDialog.display(selectedLocation);
			} else if (this == deleteAction) {
				DataManager dataManager = DataManager.getDataManager();
				if (dataManager.getLocations().size() == 1) {
					JOptionPane.showMessageDialog(locationsDialog, "You cannot merge the last remaining location.", "No no...", JOptionPane.ERROR_MESSAGE);
					return;
				}
				Location selectedLocation = (Location)dataList.getSelectedValue();
				int total = 0;
				for (Season season : dataManager.getSeasons()) {
					for (PepBandEvent event : season.getEvents()) {
						if (event.getEventType().getHasLocation() && event.getLocation().equals(selectedLocation)) {
							total++;
						}
					}
				}
				if (total == 0) {
					dataManager.mergeLocation(selectedLocation,dataManager.getLocations().get(0));
				} else {
					String[] message = {total + " events are currently associated with " + selectedLocation.getName() + ".",
									"Are you sure you want to merge it with another location?"};
					int result = JOptionPane.showConfirmDialog(locationsDialog,message,"Are Ya Sure?",JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						ArrayList<Location> locations = new ArrayList<Location>(dataManager.getLocations());
						locations.remove(selectedLocation);
						Location merge = (Location)JOptionPane.showInputDialog(locationsDialog, "Select which location " + selectedLocation.getName() + " will be merged into:", "Select Merger", JOptionPane.QUESTION_MESSAGE, null, locations.toArray(), locations.get(0));
						if (merge != null) {
							dataManager.mergeLocation(selectedLocation,merge);
						}
					}
				}
			} else if (this == upAction) {
				Location selectedLocation = (Location)dataList.getSelectedValue();
				DataManager.getDataManager().changeIndex(selectedLocation,-1);
				dataList.setSelectedValue(selectedLocation,true);
			} else if (this == downAction) {
				Location selectedLocation = (Location)dataList.getSelectedValue();
				DataManager.getDataManager().changeIndex(selectedLocation,1);
				dataList.setSelectedValue(selectedLocation,true);
			}
		}
	}
}