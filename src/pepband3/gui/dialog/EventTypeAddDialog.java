package pepband3.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.*;

public class EventTypeAddDialog extends JDialog {
	
	private GradientPanel northPanel;
	private JLabel nameLabel;
	private JTextField nameField;
	private JCheckBox hasLocationBox;
	private JLabel iconLabel;
	private JButton iconButton;
	private JPanel buttonPanel;
	private JButton okButton, cancelButton;
	
	private IconChooser iconChooser;
	
	private Action iconAction, okAction, cancelAction;
	
	public EventTypeAddDialog(JDialog owner) {
		super(owner,"Add Event Type",true);
		
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
		final EventTypeAddDialog eventTypeAddDialog = this;
		iconAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				iconChooser.display();
				String iconName = iconChooser.getChoice();
				if (iconName == null) {
					iconButton.setIcon(Tools.getIcon("empty32"));
				} else {
					iconButton.setIcon(Tools.getEventIcon(iconName + "32"));
				}
			}
		};
		okAction = new AbstractAction("Add Event Type") {
			public void actionPerformed(ActionEvent e) {
				DataManager dataManager = DataManager.getDataManager();
				if (nameField.getText().trim().isEmpty()) {
					JOptionPane.showMessageDialog(eventTypeAddDialog, "The name field is empty. Please enter a name for this event type.", "Say what?", JOptionPane.ERROR_MESSAGE);
					nameField.requestFocusInWindow();
				} else if (iconChooser.getChoice() == null) {
					JOptionPane.showMessageDialog(eventTypeAddDialog, "You did not select an icon for this event type.", "Check yo'self!", JOptionPane.ERROR_MESSAGE);
					iconButton.requestFocusInWindow();
				} else {
					EventType newEventType = new EventType(dataManager.getDataID(),nameField.getText(),iconChooser.getChoice(),hasLocationBox.isSelected());
					dataManager.addEventType(newEventType);
					setVisible(false);
				}
			}
		};
		cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		
		iconAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_I);
		iconAction.putValue(Action.LONG_DESCRIPTION,"Choose an icon for this event type");
		iconAction.putValue(Action.SHORT_DESCRIPTION,"Choose an icon");
		
		okAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		okAction.putValue(Action.LONG_DESCRIPTION,"Add this event type to the program's database with the entered properties");
		okAction.putValue(Action.SHORT_DESCRIPTION,"Add new event type");
		
		cancelAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		cancelAction.putValue(Action.LONG_DESCRIPTION,"Cancel");
		cancelAction.putValue(Action.SHORT_DESCRIPTION,"Cancel");
	}
	
	private void a2Components() {
		northPanel = new GradientPanel("Add a New Event Type",Tools.getHeaderIcon("jump"));
		
		iconChooser = new IconChooser(this,Tools.getEventIcons());
		Tools.addRootComponent(iconChooser);
		
		nameLabel = new JLabel("EventType Name: ");
		nameField = new JTextField();
		hasLocationBox = new JCheckBox("Has Location");
		iconLabel = new JLabel("Choose Icon: ");
		iconButton = new JButton(iconAction);
		iconButton.setIcon(Tools.getIcon("empty32"));
		buttonPanel = new JPanel();
		okButton = new JButton(okAction);
		cancelButton = new JButton(cancelAction);
		
		getRootPane().setDefaultButton(okButton);
		
		Tools.applyTextPopup(nameField);
	}
	
	private void a3Listeners() {
		
	}
	
	private void a4Layouts() {
		setLayout(new GridBagLayout());
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT,4,0));
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(0,0,0,0);
		
		c.gridwidth = 2; c.gridheight = 1;
		c.gridx = 0; c.gridy = 0;
		add(northPanel,c);
		
		c.insets = new Insets(2 * GUIManager.INS,2 * GUIManager.INS,2 * GUIManager.INS,2 * GUIManager.INS);
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 0;
		c.gridx = 0; c.gridy = 1;
		add(nameLabel,c);
		c.insets = new Insets(2 * GUIManager.INS,2 * GUIManager.INS,2 * GUIManager.INS,6 * GUIManager.INS);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0; c.gridy = 2;
		add(nameField,c);
		c.insets = new Insets(2 * GUIManager.INS,2 * GUIManager.INS,2 * GUIManager.INS,2 * GUIManager.INS);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0; c.gridy = 3;
		add(hasLocationBox,c);
		
		c.weightx = 0; c.weighty = 0;
		c.gridx = 1; c.gridy = 1;
		add(iconLabel,c);
		c.gridwidth = 1; c.gridheight = 2;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1; c.gridy = 2;
		add(iconButton,c);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 0;
		c.gridwidth = 2; c.gridheight = 1;
		c.gridx = 0; c.gridy = 4;
		add(new JSeparator(SwingConstants.HORIZONTAL),c);
		
		c.gridx = 0; c.gridy = 5;
		add(buttonPanel,c);
	}
	
	private void a5Initialize() {
		
	}
	
	public void display() {
		nameField.setText("");
		iconButton.setIcon(Tools.getIcon("empty32"));
		iconChooser.setChoice(null);
		hasLocationBox.setSelected(true);
		pack();
		setLocationRelativeTo(getOwner());
		nameField.requestFocusInWindow();
		setVisible(true);
	}
}