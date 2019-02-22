package pepband3.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.model.*;

public class EditMembersDialog extends JDialog {
	
	private static final String VAR_KEY = "EditMembersDialog.Varying";
	private static final Object VAL_VAR = new Object();
	private static final Object VAL_SAME = new Object();
	
	private static final String VAR = "<Varies>";
	
	private GradientPanel northPanel;
	private JCheckBox firstCheck, lastCheck, nickCheck, netIDCheck, instrumentCheck, classYearCheck, middleCheck, sexCheck;
	private JTextField firstField, lastField, nickField, netIDField, middleField;
	private JComboBox instrumentBox, sexBox;
	private JSpinner classYearSpinner;
	private JPanel buttonPanel;
	private JButton applyButton, cancelButton;
	
	private Action applyAction, cancelAction;
	
	private ArrayList<Member> members;
	
	public EditMembersDialog() {
		super(Tools.getProgramRoot(),"Edit Members",true);
		
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
		final EditMembersDialog editMembersDialog = this;
		applyAction = new AbstractAction("Apply Changes") {
			public void actionPerformed(ActionEvent e) {
				if (dataIsValid()) {
					if (firstCheck.isSelected()) {
						for (Member member : members) {
							member.setFirstName(firstField.getText());
						}
					}
					if (middleCheck.isSelected()) {
						for (Member member : members) {
							member.setMiddleName(middleField.getText());
						}
					}
					if (lastCheck.isSelected()) {
						for (Member member : members) {
							member.setLastName(lastField.getText());
						}
					}
					if (nickCheck.isSelected()) {
						for (Member member : members) {
							member.setNickName(nickField.getText());
						}
					}
					if (netIDCheck.isSelected()) {
						for (Member member : members) {
							member.setNetID(netIDField.getText());
						}
					}
					if (classYearCheck.isSelected()) {
						for (Member member : members) {
							member.setClassYear((Integer)classYearSpinner.getValue());
						}
					}
					if (instrumentCheck.isSelected()) {
						for (Member member : members) {
							member.setInstrument((Instrument)instrumentBox.getSelectedItem());
						}
					}
					if (sexCheck.isSelected()) {
						for (Member member : members) {
							member.setSex((Member.Sex)sexBox.getSelectedItem());
						}
					}
					setVisible(false);
				}
			}
		};
		cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		
		applyAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		applyAction.putValue(Action.LONG_DESCRIPTION,"Apply the selected changes to the members");
		applyAction.putValue(Action.SHORT_DESCRIPTION,"Apply changes");
		
		cancelAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		cancelAction.putValue(Action.LONG_DESCRIPTION,"Cancel");
		cancelAction.putValue(Action.SHORT_DESCRIPTION,"Cancel");
	}
	
	private void a2Components() {
		northPanel = new GradientPanel("Edit Selected Members",Tools.getHeaderIcon("memberedit"));
		
		firstCheck = new JCheckBox("First Name: ");
		middleCheck = new JCheckBox("Middle Name: ");
		lastCheck = new JCheckBox("Last Name: ");
		nickCheck = new JCheckBox("Nickname: ");
		netIDCheck = new JCheckBox("NetID: ");
		instrumentCheck = new JCheckBox("Instrument: ");
		sexCheck = new JCheckBox("Sex: ");
		classYearCheck = new JCheckBox("Class Year: ");
		
		int value = 8;
		firstCheck.setIconTextGap(value);
		middleCheck.setIconTextGap(value);
		lastCheck.setIconTextGap(value);
		nickCheck.setIconTextGap(value);
		netIDCheck.setIconTextGap(value);
		classYearCheck.setIconTextGap(value);
		instrumentCheck.setIconTextGap(value);
		sexCheck.setIconTextGap(value);
		
		firstCheck.setToolTipText("Change first name");
		middleCheck.setToolTipText("Change middle name");
		lastCheck.setToolTipText("Change last name");
		nickCheck.setToolTipText("Change nickname");
		netIDCheck.setToolTipText("Change netID");
		classYearCheck.setToolTipText("Change class year");
		instrumentCheck.setToolTipText("Change instrument");
		sexCheck.setToolTipText("Change sex");
		
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
		
		buttonPanel = new JPanel();
		applyButton = new JButton(applyAction);
		cancelButton = new JButton(cancelAction);
		
		getRootPane().setDefaultButton(applyButton);
		
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
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getSource();
				if (source == firstCheck) {
					firstField.setEnabled(firstCheck.isSelected());
					if (firstField.getClientProperty(VAR_KEY).equals(VAL_VAR)) {
						firstField.setText(firstCheck.isSelected() ? "" : VAR);
					}
					if (firstCheck.isSelected()) {
						firstField.requestFocusInWindow();
					}
				} else if (source == middleCheck) {
					middleField.setEnabled(middleCheck.isSelected());
					if (middleField.getClientProperty(VAR_KEY).equals(VAL_VAR)) {
						middleField.setText(middleCheck.isSelected() ? "" : VAR);
					}
					if (middleCheck.isSelected()) {
						middleField.requestFocusInWindow();
					}
				} else if (source == lastCheck) {
					lastField.setEnabled(lastCheck.isSelected());
					if (lastField.getClientProperty(VAR_KEY).equals(VAL_VAR)) {
						lastField.setText(lastCheck.isSelected() ? "" : VAR);
					}
					if (lastCheck.isSelected()) {
						lastField.requestFocusInWindow();
					}
				} else if (source == nickCheck) {
					nickField.setEnabled(nickCheck.isSelected());
					if (nickField.getClientProperty(VAR_KEY).equals(VAL_VAR)) {
						nickField.setText(nickCheck.isSelected() ? "" : VAR);
					}
					if (nickCheck.isSelected()) {
						nickField.requestFocusInWindow();
					}
				} else if (source == netIDCheck) {
					netIDField.setEnabled(netIDCheck.isSelected());
					if (netIDField.getClientProperty(VAR_KEY).equals(VAL_VAR)) {
						netIDField.setText(netIDCheck.isSelected() ? "" : VAR);
					}
					if (netIDCheck.isSelected()) {
						netIDField.requestFocusInWindow();
					}
				} else if (source == instrumentCheck) {
					instrumentBox.setEnabled(instrumentCheck.isSelected());
					if (instrumentCheck.isSelected()) {
						instrumentBox.requestFocusInWindow();
					}
				} else if (source == sexCheck) {
					sexBox.setEnabled(sexCheck.isSelected());
					if (sexCheck.isSelected()) {
						sexBox.requestFocusInWindow();
					}
				} else if (source == classYearCheck) {
					classYearSpinner.setEnabled(classYearCheck.isSelected());
					if (classYearCheck.isSelected()) {
						classYearSpinner.requestFocusInWindow();
					}
				}
			}
		};
		firstCheck.addItemListener(itemListener);
		middleCheck.addItemListener(itemListener);
		lastCheck.addItemListener(itemListener);
		nickCheck.addItemListener(itemListener);
		netIDCheck.addItemListener(itemListener);
		classYearCheck.addItemListener(itemListener);
		instrumentCheck.addItemListener(itemListener);
		sexCheck.addItemListener(itemListener);
	}
	
	private void a4Layouts() {
		setLayout(new GridBagLayout());
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,4,4));
		
		buttonPanel.add(applyButton);
		buttonPanel.add(cancelButton);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 2; c.gridheight = 1;
		c.weightx = 0; c.weighty = 0;
		c.gridx = 0; c.gridy = 0;
		add(northPanel,c);
		
		int yVal = 1;
		c.insets = new Insets(2 * GUIManager.INS,4 * GUIManager.INS,2 * GUIManager.INS,4 * GUIManager.INS);
		c.gridwidth = 1; c.gridheight = 1;
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(firstCheck,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(middleCheck,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(lastCheck,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(nickCheck,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(netIDCheck,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(classYearCheck,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(instrumentCheck,c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(sexCheck,c);
		
		yVal = 1;
		c.weightx = 1; c.weighty = 0;
		c.gridx = 1; c.gridy = yVal; yVal++;
		add(firstField,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		add(middleField,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		add(lastField,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		add(nickField,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		add(netIDField,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		add(classYearSpinner,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		add(instrumentBox,c);
		c.gridx = 1; c.gridy = yVal; yVal++;
		add(sexBox,c);
		
		c.gridwidth = 2; c.gridheight = 1;
		c.weightx = 0; c.weighty = 1;
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(Box.createVerticalGlue(),c);
		c.weightx = 0; c.weighty = 0;
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(new JSeparator(SwingConstants.HORIZONTAL),c);
		c.gridx = 0; c.gridy = yVal; yVal++;
		add(buttonPanel,c);
	}
	
	private void a5Initialize() {
		members = new ArrayList<Member>();
		setAllEnabled(false);
	}
	
	private boolean dataIsValid() {
		if (firstCheck.isSelected()) {
			if (firstField.getText().isEmpty()) {
				String[] message = new String[2];
				message[0] = "First name is empty";
				message[1] = "Please enter a first name";
				JOptionPane.showMessageDialog(this, message, "Please Revise", JOptionPane.ERROR_MESSAGE);
				firstField.requestFocusInWindow();
				firstField.selectAll();
				return false;
			}
		}
		if (lastCheck.isSelected()) {
			if (lastField.getText().isEmpty()) {
				String[] message = new String[2];
				message[0] = "Last name is empty";
				message[1] = "Please enter a last name";
				JOptionPane.showMessageDialog(this, message, "Please Revise", JOptionPane.ERROR_MESSAGE);
				lastField.requestFocusInWindow();
				lastField.selectAll();
				return false;
			}
		}
		if (netIDCheck.isSelected()) {
			if (netIDField.getText().isEmpty()) {
				String[] message = new String[2];
				message[0] = "NetID is empty";
				message[1] = "Please enter a NetID";
				JOptionPane.showMessageDialog(this, message, "Please Revise", JOptionPane.ERROR_MESSAGE);
				netIDField.requestFocusInWindow();
				netIDField.selectAll();
				return false;
			}
		}
		if (classYearCheck.isSelected()) {
			if ((Integer)classYearSpinner.getValue() < 1865) {
				String[] message = new String[2];
				message[0] = "Class year predates the founding of Cornell";
				message[1] = "Please enter a valid class year";
				JOptionPane.showMessageDialog(this, message, "Please Revise", JOptionPane.ERROR_MESSAGE);
				classYearSpinner.requestFocusInWindow();
				return false;
			}
		}
		return true;
	}
	
	public void display(ArrayList<Member> value) {
		if (value != null && !value.isEmpty()) {
			prepare(value);
			pack();
			setLocationRelativeTo(getOwner());
			setVisible(true);
		}
	}
	
	private void prepare(ArrayList<Member> value) {
		members.clear();
		members.addAll(value);
		
		boolean same = true;
		Object property = value.get(0).getFirstName();
		for (Member member : value) {
			if (!member.getFirstName().equals(property)) {
				same = false;
			}
		}
		if (same) {
			firstField.putClientProperty(VAR_KEY, VAL_SAME);
			firstCheck.setSelected(true);
			firstField.setText((String)property);
		} else {
			firstField.putClientProperty(VAR_KEY, VAL_VAR);
			firstCheck.setSelected(false);
			firstField.setText(VAR);
		}
		same = true;
		property = value.get(0).getMiddleName();
		for (Member member : value) {
			if (!member.getMiddleName().equals(property)) {
				same = false;
			}
		}
		if (same) {
			middleField.putClientProperty(VAR_KEY, VAL_SAME);
			middleCheck.setSelected(true);
			middleField.setText((String)property);
		} else {
			middleField.putClientProperty(VAR_KEY, VAL_VAR);
			middleCheck.setSelected(false);
			middleField.setText(VAR);
		}
		same = true;
		property = value.get(0).getLastName();
		for (Member member : value) {
			if (!member.getLastName().equals(property)) {
				same = false;
			}
		}
		if (same) {
			lastField.putClientProperty(VAR_KEY, VAL_SAME);
			lastCheck.setSelected(true);
			lastField.setText((String)property);
		} else {
			lastField.putClientProperty(VAR_KEY, VAL_VAR);
			lastCheck.setSelected(false);
			lastField.setText(VAR);
		}
		same = true;
		property = value.get(0).getNickName();
		for (Member member : value) {
			if (!member.getNickName().equals(property)) {
				same = false;
			}
		}
		if (same) {
			nickField.putClientProperty(VAR_KEY, VAL_SAME);
			nickCheck.setSelected(true);
			nickField.setText((String)property);
		} else {
			nickField.putClientProperty(VAR_KEY, VAL_VAR);
			nickCheck.setSelected(false);
			nickField.setText(VAR);
		}
		same = true;
		property = value.get(0).getNetID();
		for (Member member : value) {
			if (!member.getNetID().equals(property)) {
				same = false;
			}
		}
		if (same) {
			netIDField.putClientProperty(VAR_KEY, VAL_SAME);
			netIDCheck.setSelected(true);
			netIDField.setText((String)property);
		} else {
			netIDField.putClientProperty(VAR_KEY, VAL_VAR);
			netIDCheck.setSelected(false);
			netIDField.setText(VAR);
		}
		same = true;
		property = value.get(0).getInstrument();
		for (Member member : value) {
			if (!member.getInstrument().equals(property)) {
				same = false;
			}
		}
		if (same) {
			instrumentBox.putClientProperty(VAR_KEY, VAL_SAME);
			instrumentCheck.setSelected(true);
			instrumentBox.setSelectedItem(value.get(0).getInstrument());
		} else {
			instrumentBox.putClientProperty(VAR_KEY, VAL_VAR);
			instrumentCheck.setSelected(false);
			instrumentBox.setSelectedItem(value.get(0).getInstrument());
		}
		same = true;
		property = value.get(0).getSex();
		for (Member member : value) {
			if (!member.getSex().equals(property)) {
				same = false;
			}
		}
		if (same) {
			sexBox.putClientProperty(VAR_KEY, VAL_SAME);
			sexCheck.setSelected(true);
			sexBox.setSelectedItem(value.get(0).getSex());
		} else {
			sexBox.putClientProperty(VAR_KEY, VAL_VAR);
			sexCheck.setSelected(false);
			sexBox.setSelectedItem(value.get(0).getSex());
		}
		same = true;
		property = value.get(0).getClassYear();
		for (Member member : value) {
			if (!member.getClassYear().equals(property)) {
				same = false;
			}
		}
		if (same) {
			classYearSpinner.putClientProperty(VAR_KEY, VAL_SAME);
			classYearCheck.setSelected(true);
			classYearSpinner.setValue(value.get(0).getClassYear());
		} else {
			classYearSpinner.putClientProperty(VAR_KEY, VAL_VAR);
			classYearCheck.setSelected(false);
			classYearSpinner.setValue(value.get(0).getClassYear());
		}
		
		if (value.size() == 1) {
			northPanel.setTitle("Edit Selected Member");
			setTitle("Edit Member");
		} else {
			northPanel.setTitle("Edit Selected Members");
			setTitle("Edit Members");
		}
	}
	
	private void setAllEnabled(boolean value) {
		firstCheck.setSelected(value);
		middleCheck.setSelected(value);
		lastCheck.setSelected(value);
		nickCheck.setSelected(value);
		netIDCheck.setSelected(value);
		classYearCheck.setSelected(value);
		instrumentCheck.setSelected(value);
		sexCheck.setSelected(value);
		
		firstField.setEnabled(value);
		middleField.setEnabled(value);
		lastField.setEnabled(value);
		nickField.setEnabled(value);
		netIDField.setEnabled(value);
		instrumentBox.setEnabled(value);
		sexBox.setEnabled(value);
		classYearSpinner.setEnabled(value);
	}
}