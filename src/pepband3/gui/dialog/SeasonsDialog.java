package pepband3.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.*;
import pepband3.gui.component.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public class SeasonsDialog extends JDialog {
	
	private SeasonsDialog seasonsDialog;
	private DataListener dataListener;
	
	private GradientPanel northPanel;
	private JScrollPane scrollPane;
	private JList seasonList;
	private JPanel currentSeasonPanel;
	private JLabel seasonLabel;
	private JButton setButton;
	private JButton addButton;
	private JButton editButton;
	private JButton deleteButton;
	private SeasonAddDialog seasonAddDialog;
	private SeasonEditDialog seasonEditDialog;
	
	private Action setAction, addAction, editAction, deleteAction;
	
	public SeasonsDialog(JDialog owner) {
		super(owner,"Seasons",true);
		
		setIconImages(Tools.getWindowIcons());
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);
		setUndecorated(false);
		seasonsDialog = this;
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		setAction = new SeasonsDialogAction("Set Current Season");
		addAction = new SeasonsDialogAction("Add Season");
		editAction = new SeasonsDialogAction("Edit Season");
		deleteAction = new SeasonsDialogAction("Delete Season");
		
		setAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_S);
		setAction.putValue(Action.LONG_DESCRIPTION,"Set the selected season to be the current season");
		setAction.putValue(Action.SHORT_DESCRIPTION,"Set current season to selected");
		
		addAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		addAction.putValue(Action.LONG_DESCRIPTION,"Add a new season to the program's database");
		addAction.putValue(Action.SHORT_DESCRIPTION,"Start new season");
		
		editAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
		editAction.putValue(Action.LONG_DESCRIPTION,"Edit the selected season ");
		editAction.putValue(Action.SHORT_DESCRIPTION,"Edit selected season");
		
		deleteAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
		deleteAction.putValue(Action.LONG_DESCRIPTION,"Perminantly delete the selected season");
		deleteAction.putValue(Action.SHORT_DESCRIPTION,"Delete selected season");
	}
	
	private void a2Components() {
		seasonAddDialog = new SeasonAddDialog(this);
		Tools.addRootComponent(seasonAddDialog);
		seasonEditDialog = new SeasonEditDialog(this);
		Tools.addRootComponent(seasonEditDialog);
		
		northPanel = new GradientPanel("Seasons Manager",Tools.getHeaderIcon("seasons"));
		
		seasonList = new JList(new DataListModel(Season.class));
		seasonList.setCellRenderer(new ListRenderer());
		seasonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane = new JScrollPane(seasonList);
		scrollPane.setPreferredSize(new Dimension(200,300));
		currentSeasonPanel = new JPanel();
		currentSeasonPanel.setBorder(OptionsDialog.createTitledBorder("Current Season"));
		Integer startingYear = DataManager.getDataManager().getCurrentSeason().getStartingYear();
		seasonLabel = new JLabel(startingYear + " - " + (startingYear + 1));
		seasonLabel.setHorizontalAlignment(SwingConstants.CENTER);
		setButton = new JButton(setAction);
		addButton = new JButton(addAction);
		editButton = new JButton(editAction);
		deleteButton = new JButton(deleteAction);
		
		getRootPane().setDefaultButton(setButton);
	}
	
	private void a3Listeners() {
		seasonList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					int index = seasonList.locationToIndex(e.getPoint());
					setAction.actionPerformed(new ActionEvent(e.getSource(),1207,"set",e.getModifiers()));
				}
			}
		});
		seasonList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = seasonList.getSelectedIndex();
				if (index < 0) {
					setActionsEnabled(false);
				} else {
					setActionsEnabled(true);
				}
			}
		});
		dataListener = new DataListener() {
			public void eventOccured(SourceEvent sourceEvent) {
				DataListModel model = (DataListModel)seasonList.getModel();
				if (sourceEvent.isField()) {
					SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					if (fieldEvent.getOwner() instanceof DataManager && fieldEvent.getField() == DataField.CURRENT_SEASON) {
						Integer startingYear = DataManager.getDataManager().getCurrentSeason().getStartingYear();
						seasonLabel.setText(startingYear + " - " + (startingYear + 1));
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (listEvent.getElement() instanceof Season && listEvent.getType() == SourceEvent.List.ADD) {
						if (listEvent.isMultiIndex()) {
							model.fireIntervalAdded(listEvent.getOwner(), listEvent.getMinIndex(), listEvent.getMaxIndex());
						} else if (listEvent.isSingleIndex()) {
							model.fireIntervalAdded(listEvent.getOwner(), listEvent.getIndex(), listEvent.getIndex());
						}
					} else if (listEvent.getElement() instanceof Season && listEvent.getType() == SourceEvent.List.REMOVE) {
						if (listEvent.isMultiIndex()) {
							model.fireIntervalRemoved(listEvent.getOwner(), listEvent.getMinIndex(), listEvent.getMaxIndex());
						} else if (listEvent.isSingleIndex()) {
							model.fireIntervalRemoved(listEvent.getOwner(), listEvent.getIndex(), listEvent.getIndex());
						}
					}
				}
			}
		};
		DataManager.getDataManager().addDataListener(dataListener);
	}
	
	private void a4Layouts() {
		setLayout(new GridBagLayout());
		currentSeasonPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		c.insets = new Insets(3 * GUIManager.INS,3 * GUIManager.INS,3 * GUIManager.INS,3 * GUIManager.INS);
		c.gridx = 0; c.gridy = 0;
		currentSeasonPanel.add(seasonLabel,c);
		
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
		add(currentSeasonPanel,c);
		c.gridx = 1; c.gridy = 2;
		add(setButton,c);
		c.gridx = 1; c.gridy = 3;
		add(new JSeparator(SwingConstants.HORIZONTAL),c);
		c.gridx = 1; c.gridy = 4;
		add(addButton,c);
		c.gridx = 1; c.gridy = 5;
		add(editButton,c);
		c.gridx = 1; c.gridy = 6;
		add(deleteButton,c);
		c.weightx = 0; c.weighty = 1;
		c.gridx = 1; c.gridy = 7;
		add(Box.createVerticalGlue(),c);
	}
	
	private void a5Initialize() {
		seasonList.setSelectedValue(DataManager.getDataManager().getCurrentSeason(),true);	
	}
	
	public void display() {
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}
	
	private void setActionsEnabled(boolean value) {
		setAction.setEnabled(value);
		editAction.setEnabled(value);
		deleteAction.setEnabled(value);
	}
	
	private class SeasonsDialogAction extends AbstractAction {
		public SeasonsDialogAction() {
			super();
		}
		
		public SeasonsDialogAction(String value) {
			super(value);
		}
		 
		public void actionPerformed(ActionEvent e) {
			if (this == setAction) {
				Season selectedSeason = (Season)seasonList.getSelectedValue();
				if (selectedSeason != null) {
					DataManager.getDataManager().setCurrentSeason(selectedSeason);
				}
			} else if (this == addAction) {
				seasonAddDialog.display();
			} else if (this == editAction) {
				Season selectedSeason = (Season)seasonList.getSelectedValue();
				seasonEditDialog.display(selectedSeason);
			} else if (this == deleteAction) {
				Season selectedSeason = (Season)seasonList.getSelectedValue();
				if (DataManager.getDataManager().getCurrentSeason().equals(selectedSeason)) {
					String[] message = new String[4];
					message[0] = "You cannot delete the " + selectedSeason.getName() + " because it is the current season.";
					message[1] = "If you still really wish to delete it, set the current season to another season and try again.";
					JOptionPane.showMessageDialog(seasonAddDialog, message, "Ehh, Can't Do That", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String[] message = new String[4];
				message[0] = "Are you fully aware that all events and unique member data in the " + selectedSeason.getName() + " will be deleted?";
				message[1] = " \t ";
				message[2] = "This season contains " + selectedSeason.getEvents().size() + " events that will be perminantly lossed and no longer recognized in point tallying.";
				message[3] = "This season contains " + selectedSeason.size() + " members. If any of these members are only present in this season's roster, they will be perminantly lossed.";
				String[] options = {"YA RLY. Burn! Raze! Destroy!","Snap! This sounds like a bad idea... nevermind."};
				int value = JOptionPane.showOptionDialog(seasonsDialog,message,"O RLY?",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[1]);
				if(value == JOptionPane.YES_OPTION) {
					boolean success = DataManager.getDataManager().removeSeason(selectedSeason);
					if (!success) {
						String[] submessage = new String[2];
						submessage[0] = "The " + selectedSeason.getName() + " could not be deleted.";
						submessage[1] = "This is an abnormal error to recieve, indicating that the season was not present in the list of seasons and was most likely already deleted.";
						JOptionPane.showMessageDialog(seasonAddDialog, submessage, "NO WAI!", JOptionPane.ERROR_MESSAGE);
					} else {
						SeasonWindow window = Tools.getDesktopPane().getSeasonWindow(selectedSeason);
						if (window != null) {
							window.doDefaultCloseAction();
						}
						String submessage = "The " + selectedSeason.getName() + " was successfully deleted.";
						JOptionPane.showMessageDialog(seasonsDialog, submessage, "RIP", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}
	}
}