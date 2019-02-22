package pepband3.gui.component.tab;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.*;
import pepband3.gui.dialog.*;
import pepband3.gui.extra.*;

public class EditingTab extends BandTableTab {
	
	private static final AddMembersDialog ADD_MEMBERS_DIALOG = new AddMembersDialog();
	private static final  EditMembersDialog EDIT_MEMBERS_DIALOG = new EditMembersDialog();
	
	static {
		Tools.addRootComponent(ADD_MEMBERS_DIALOG);
		Tools.addRootComponent(EDIT_MEMBERS_DIALOG);
	}
	
	private JButton addButton, openButton, editButton, removeButton;
	private JComponent glue;
	
	private RunnableAction addAction, openAction, editAction, removeAction;
	
	private BandTable bandTable;
	
	public EditingTab(BandTable paramBandTable) {
		if (paramBandTable != null) {
			bandTable = paramBandTable;
		} else {
			throw new NullPointerException("ADD MEMBERS DIALOG BAND IS NULL");
		}
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		addAction = new RunnableAction("Add Members",null) {
			public void act() {
				ADD_MEMBERS_DIALOG.display(bandTable.getBand());
			}
		};
		openAction = new RunnableAction("Open Members",null) {
			public void act() {
				Tools.getProgramRoot().getActionMap().get("Open Member").actionPerformed(new ActionEvent(getRecentSource(),ActionEvent.ACTION_PERFORMED,""));
			}
		};
		editAction = new RunnableAction("Edit Members",null) {
			public void act() {
				Tools.getProgramRoot().getActionMap().get("Edit Member").actionPerformed(new ActionEvent(getRecentSource(),ActionEvent.ACTION_PERFORMED,""));
			}
		};
		removeAction = new RunnableAction("Remove Members",null) {
			public void act() {
				if (bandTable.getBand() instanceof Season) {
					removeFromSeason();
				}
			}
			
			private void removeFromSeason() {
				int[] selectedRows = bandTable.getTable().getSelectedRows();
				if (selectedRows.length == 1) {
					String[] message = new String[4];
					message[0] = "Are you sure you want to remove the selected member?";
					message[1] = " \t ";
					message[2] = "This member will be removed from all events in this season.";
					message[3] = "If the member isn't present in any other seasons, the member will be perminantly lossed";
					String[] options = {"Remove Member","Nevermind"};
					int value = JOptionPane.showOptionDialog(Tools.getProgramRoot(),message,"Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[1]);
					if(value == JOptionPane.YES_OPTION) {
						doRemove(selectedRows);
					}
				} else if (selectedRows.length > 1) {
					String[] message = new String[4];
					message[0] = "Are you sure you want to remove the selected members?";
					message[1] = " \t ";
					message[2] = "These members will be removed from all events in this season.";
					message[3] = "If the members aren't present in any other seasons, they will be perminantly lossed";
					String[] options = {"Remove Members","Nevermind"};
					int value = JOptionPane.showOptionDialog(Tools.getProgramRoot(),message,"Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[1]);
					if(value == JOptionPane.YES_OPTION) {
						doRemove(selectedRows);
					}
				}
			}
			
			private void doRemove(int[] selectedRows) {
				ArrayList<Member> membersToRemove = bandTable.getSelectedMembers();
				Season season = (Season)bandTable.getBand();
				for (Member member : membersToRemove) {
					season.removeMember(member);
					for (PepBandEvent event : season.getEvents()) {
						event.removeMember(member);
					}
					if (member.getSeasons().isEmpty()) {
						MemberWindow window = Tools.getDesktopPane().getMemberWindow(member);
						if (window != null) {
							window.doDefaultCloseAction();
						}
					}
				}
				membersToRemove.clear();
			}
		};
		
		addAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		addAction.putValue(Action.SMALL_ICON,Tools.getIcon("useradd16"));
		addAction.putValue(Action.LONG_DESCRIPTION,"Add new members to the band");
		addAction.putValue(Action.SHORT_DESCRIPTION,"Add new members");
		
		openAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_O);
		openAction.putValue(Action.SMALL_ICON,Tools.getIcon("member16"));
		openAction.putValue(Action.LONG_DESCRIPTION,"Open selected members in new member windows");
		openAction.putValue(Action.SHORT_DESCRIPTION,"Open selected members");
		
		editAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
		editAction.putValue(Action.SMALL_ICON,Tools.getIcon("edit16"));
		editAction.putValue(Action.LONG_DESCRIPTION,"Edit the selected members");
		editAction.putValue(Action.SHORT_DESCRIPTION,"Edit selected members");
		
		removeAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_R);
		removeAction.putValue(Action.LONG_DESCRIPTION,"Remove the selected members");
		removeAction.putValue(Action.SHORT_DESCRIPTION,"Remove selected members");
	}
	
	private void a2Components() {
		setBorder(OptionsDialog.createTitledBorder("Edit Roster"));
		
		addButton = new JButton(addAction);
		openButton = new JButton(openAction);
		editButton = new JButton(editAction);
		removeButton = new JButton(removeAction);
		glue = (JComponent)Box.createVerticalGlue();
	}
	
	private void a3Listeners() {
		bandTable.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int[] rows = bandTable.getTable().getSelectedRows();
					if (rows.length <= 0) {
						openAction.putValue(Action.NAME,"Open Member");
						editAction.putValue(Action.NAME,"Edit Member");
						removeAction.putValue(Action.NAME,"Remove Member");
						setActionsEnabled(false);
					} else if (rows.length == 1) {
						openAction.putValue(Action.NAME,"Open Member");
						editAction.putValue(Action.NAME,"Edit Member");
						removeAction.putValue(Action.NAME,"Remove Member");
						setActionsEnabled(true);
					} else {
						openAction.putValue(Action.NAME,"Open Members");
						editAction.putValue(Action.NAME,"Edit Members");
						removeAction.putValue(Action.NAME,"Remove Members");
						setActionsEnabled(true);
					}
				}
			}
		});
	}
	
	private void a4Layouts() {
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 0;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		int yindex = 0;
		
		c.gridx = 0; c.gridy = yindex; yindex++;
		add(addButton,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		add(openButton,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		add(editButton,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		add(removeButton,c);
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = yindex; yindex++;
		add(glue,c);
	}
	
	private void a5Initialize() {
		setActionsEnabled(false);
	}
	
	public static AddMembersDialog getAddDialog() {
		return ADD_MEMBERS_DIALOG;
	}
	
	public static EditMembersDialog getEditDialog() {
		return EDIT_MEMBERS_DIALOG;
	}
	
	public Integer getIndex() {
		return Tools.getBoolean("Editing Tab Priority", false) ? new Integer(1) : new Integer(2);
	}
	
	public String getTabIconName() {
		return "editing";
	}
	
	public String getTabName() {
		return "Editing";
	}
	
	public String getToolTipText() {
		return "Editing Tab";
	}
	
	private void setActionsEnabled(boolean value) {
		openAction.setEnabled(value);
		editAction.setEnabled(value);
		removeAction.setEnabled(value);
	}
}