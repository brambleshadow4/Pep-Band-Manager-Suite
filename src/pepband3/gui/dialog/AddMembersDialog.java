package pepband3.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import pepband3.data.Band;
import pepband3.data.DataManager;
import pepband3.data.Instrument;
import pepband3.data.Member;
import pepband3.data.Member.Sex;
import pepband3.data.Season;
import pepband3.gui.GUIManager;
import pepband3.gui.OptionsDialog;
import pepband3.gui.Tools;
import pepband3.gui.component.AdditionTable;
import pepband3.gui.component.GradientPanel;
import pepband3.gui.component.renderer.ListRenderer;
import pepband3.gui.extra.DataField;
import pepband3.gui.model.DataComboBoxModel;

public class AddMembersDialog extends JDialog {
	
	private GradientPanel northPanel;
	private JTabbedPane tabbedPane;
	private JPanel newPanel;
		private JLabel firstLabel, lastLabel, nickLabel, netIDLabel, instrumentLabel, classYearLabel, middleLabel, sexLabel;
		private JTextField firstField, lastField, nickField, netIDField, middleField;
		private JComboBox instrumentBox, sexBox;
		private JSpinner classYearSpinner;
	private JPanel existingPanel;
		private JPanel seasonPanel;
		private JComboBox seasonBox;
		private AdditionTable additionTable;
	private JPanel buttonPanel;
	private JButton addButton, closeButton;
	
	private Action addAction, closeAction;
	
	private Band targetBand;
	
	public AddMembersDialog() {
		super(Tools.getProgramRoot(),"Add Members",true);
		
		setIconImages(Tools.getWindowIcons());
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);
		setUndecorated(false);
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		final AddMembersDialog addMembersDialog = this;
		addAction = new AbstractAction("Add Member") {
			public void actionPerformed(ActionEvent e) {
				if (firstField.getText().trim().isEmpty()) {
					String[] message = new String[2];
					message[0] = "Member's first name is empty";
					message[1] = "Please enter a first name";
					JOptionPane.showMessageDialog(addMembersDialog, message, "Please Revise", JOptionPane.ERROR_MESSAGE);
					firstField.requestFocusInWindow();
				} else if (lastField.getText().trim().isEmpty()) {
					String[] message = new String[2];
					message[0] = "Member's last name is empty";
					message[1] = "Please enter a last name";
					JOptionPane.showMessageDialog(addMembersDialog, message, "Please Revise", JOptionPane.ERROR_MESSAGE);
					lastField.requestFocusInWindow();
				} else if (netIDField.getText().trim().isEmpty()) {
					String[] message = new String[2];
					message[0] = "Member's NetID is empty";
					message[1] = "Please enter a NetID";
					JOptionPane.showMessageDialog(addMembersDialog, message, "Please Revise", JOptionPane.ERROR_MESSAGE);
					netIDField.requestFocusInWindow();
				} else if ((Integer)classYearSpinner.getValue() < 1865) {
					String[] message = new String[2];
					message[0] = "Member's class year predates the founding of Cornell";
					message[1] = "Please enter a valid class year";
					JOptionPane.showMessageDialog(addMembersDialog, message, "Please Revise", JOptionPane.ERROR_MESSAGE);
					classYearSpinner.requestFocusInWindow();
				} else if (targetBand == null) {
					System.err.println("Cannot add member because targetBand is null");
				} else {
					DataManager dataManager = DataManager.getDataManager();
					Member existingMember = dataManager.getMemberWithNetID(netIDField.getText().trim());
					if (existingMember == null) {
						Member newMember = new Member(dataManager.getDataID(),firstField.getText().trim(),lastField.getText().trim(),nickField.getText().trim(),(Integer)classYearSpinner.getValue(),(Instrument)instrumentBox.getSelectedItem(),netIDField.getText().trim(),middleField.getText().trim(),(Sex)sexBox.getSelectedItem());
						targetBand.addMember(newMember);
					} else {
						String[] message = new String[3];
						message[0] = "A " + existingMember.getInstrument().getName() + " player by the name of " + existingMember.getName() + " exists with the entered Net ID (" + existingMember.getNetID() + ").";
						message[1] = " \t ";
						message[2] = "Please note that having multiple instances of a member will cause point totals for that member to be invalid!";
						String[] options = {"Add Existing Member","Add New Member","Cancel"};
						int value = JOptionPane.showOptionDialog(addMembersDialog,message,"Possible Duplicate Found",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[0]);
						if(value == JOptionPane.YES_OPTION) {
							targetBand.addMember(existingMember);
						} else if (value == JOptionPane.NO_OPTION) {
							Member newMember = new Member(dataManager.getDataID(),firstField.getText().trim(),lastField.getText().trim(),nickField.getText().trim(),(Integer)classYearSpinner.getValue(),(Instrument)instrumentBox.getSelectedItem(),netIDField.getText().trim(),middleField.getText().trim(),(Sex)sexBox.getSelectedItem());
							targetBand.addMember(newMember);
						} else if (value == JOptionPane.CANCEL_OPTION) {
							
						}
					}
					firstField.requestFocusInWindow();
					firstField.selectAll();
				}
			}
		};
		closeAction = new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		
		addAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		addAction.putValue(Action.LONG_DESCRIPTION,"Add a new member with the entered data");
		addAction.putValue(Action.SHORT_DESCRIPTION,"Add a new member");
		
		closeAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		closeAction.putValue(Action.LONG_DESCRIPTION,"Close the add members dialog");
		closeAction.putValue(Action.SHORT_DESCRIPTION,"Close");
	}
	
	private void a2Components() {
		setPreferredSize(new Dimension(300,600));
		northPanel = new GradientPanel("Add Members",Tools.getHeaderIcon("memberadd"));
		
		newPanel = new JPanel();
		
		firstLabel = new JLabel("First Name: ");
		middleLabel = new JLabel("Middle Name: ");
		lastLabel = new JLabel("Last Name: ");
		nickLabel = new JLabel("Nickname: ");
		netIDLabel = new JLabel("NetID: ");
		instrumentLabel = new JLabel("Instrument: ");
		classYearLabel = new JLabel("Class Year: ");
		sexLabel = new JLabel("Sex: ");
		
		firstField = new JTextField();
		middleField = new JTextField();
		lastField = new JTextField();
		nickField = new JTextField();
		netIDField = new JTextField();
		
		instrumentBox = new JComboBox(new DataComboBoxModel(Instrument.class));
		instrumentBox.setRenderer(new ListRenderer());
		sexBox = new JComboBox(Member.Sex.values());
		sexBox.setRenderer(new ListRenderer());
		classYearSpinner = new JSpinner(new SpinnerNumberModel());
		
		existingPanel = new JPanel();
		
		seasonPanel = new JPanel();
		seasonPanel.setBorder(OptionsDialog.createTitledBorder("Select Season"));
		seasonBox = new JComboBox(new DataComboBoxModel(Season.class));
		seasonBox.setSelectedItem(DataManager.getDataManager().getCurrentSeason());
		seasonBox.setRenderer(new ListRenderer());
		additionTable = new AdditionTable(DataManager.getDataManager().getCurrentSeason(),null,DataField.FULL_NAME);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		
		buttonPanel = new JPanel();
		addButton = new JButton(addAction);
		closeButton = new JButton(closeAction);
		
		getRootPane().setDefaultButton(addButton);
		
		JSpinner.DefaultEditor classYearEditor = new JSpinner.NumberEditor(classYearSpinner,"#");
		classYearSpinner.setEditor(classYearEditor);
		Tools.applyTextPopup(classYearEditor.getTextField());
		Tools.applyTextPopup(firstField);
		Tools.applyTextPopup(middleField);
		Tools.applyTextPopup(lastField);
		Tools.applyTextPopup(nickField);
		Tools.applyTextPopup(netIDField);
	}
	
	private void a3Listeners() {
		seasonBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() == seasonBox && e.getStateChange() == ItemEvent.SELECTED) {
					Season season = (Season)seasonBox.getSelectedItem();
					additionTable.getBandTableModel().setBand(season);
				}
			}
		});
		FocusAdapter focusAdapter = new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				Component component = e.getComponent();
				if (component instanceof JTextField) {
					((JTextField)component).selectAll();
				} else if (component instanceof JComboBox) {
					((JComboBox)component).showPopup();
				}
			}
		};
		firstField.addFocusListener(focusAdapter);
		middleField.addFocusListener(focusAdapter);
		lastField.addFocusListener(focusAdapter);
		nickField.addFocusListener(focusAdapter);
		netIDField.addFocusListener(focusAdapter);
		instrumentBox.addFocusListener(focusAdapter);
		sexBox.addFocusListener(focusAdapter);
		((JSpinner.DefaultEditor)classYearSpinner.getEditor()).getTextField().addFocusListener(focusAdapter);
	}
	
	private void a4Layouts() {
		setLayout(new GridBagLayout());
		newPanel.setLayout(new GridBagLayout());
		seasonPanel.setLayout(new BorderLayout());
		existingPanel.setLayout(new BorderLayout());
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,4,4));
		
		buttonPanel.add(addButton);
		buttonPanel.add(closeButton);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		int yVal = 0;
		c.insets = new Insets(2 * GUIManager.INS,4 * GUIManager.INS,2 * GUIManager.INS,4 * GUIManager.INS);
		c.weightx = 0; c.weighty = 0;
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(firstLabel,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(middleLabel,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(lastLabel,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(nickLabel,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(netIDLabel,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(classYearLabel,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(instrumentLabel,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(sexLabel,c);
		
		yVal = 0;
		c.weightx = 1; c.weighty = 0;
		c.gridx = 1; c.gridy = yVal; yVal++;
		newPanel.add(firstField,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		newPanel.add(middleField,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		newPanel.add(lastField,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		newPanel.add(nickField,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		newPanel.add(netIDField,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		newPanel.add(classYearSpinner,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		newPanel.add(instrumentBox,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		newPanel.add(sexBox,c);
		
		c.gridwidth = 2; c.gridheight = 1;
		c.weightx = 0; c.weighty = 1;
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(Box.createVerticalGlue(),c);
		c.weightx = 0; c.weighty = 0;
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(new JSeparator(SwingConstants.HORIZONTAL),c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		newPanel.add(buttonPanel,c);
		
		seasonPanel.add(seasonBox,BorderLayout.CENTER);
		
		existingPanel.add(seasonPanel,BorderLayout.NORTH);
		existingPanel.add(additionTable,BorderLayout.CENTER);
		
		tabbedPane.addTab("New Member",null,newPanel,"Add a new member to the roster");
		tabbedPane.addTab("Existing Member",null,existingPanel,"Add a member to the current roster from another season");
		
		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 0;
		c.gridx = 0; c.gridy = 0;
		add(northPanel,c);
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 1;
		add(tabbedPane,c);
	}
	
	private void a5Initialize() {
			
	}
	
	public void display(Band value) {
		if (value != null) {
			targetBand = value;
			additionTable.setTargetBand(targetBand);
			pack();
			setLocationRelativeTo(getOwner());
			setVisible(true);
		}
	}
}