package pepband3.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.*;

public class SeasonEditDialog extends JDialog {
	
	private GradientPanel northPanel;
	private JLabel startingYearLabel;
	private JLabel yearLabel;
	private JLabel startingDateLabel;
	private JSpinner startingDateSpinner;
	private JButton okButton, cancelButton;
	
	private Action okAction, cancelAction;
	
	private Season season;
	
	public SeasonEditDialog(JDialog owner) {
		super(owner,"Edit Season",true);
		
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
		final SeasonEditDialog seasonEditDialog = this;
		okAction = new AbstractAction("Apply Changes") {
			public void actionPerformed(ActionEvent e) {
				Integer year = season.getStartingYear();
				Date date = (Date)startingDateSpinner.getValue();
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				Integer dateYear = calendar.get(Calendar.YEAR);
				
				if (!dateYear.equals(year)) {
					String[] message = new String[1];
					message[0] = DateFormat.getDateInstance(DateFormat.SHORT).format(date) + " does not fall within the year " + year + "!";
					JOptionPane.showMessageDialog(seasonEditDialog, message, "Oops!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				for (PepBandEvent event : season.getEvents()) {
					if (event.getDate().before(date)) {
						String[] message = new String[2];
						message[0] = "A " + event.getEventType().getName() + " event, " + event.getName() + ", has a date, " + DateFormat.getDateInstance(DateFormat.SHORT).format(event.getDate()) + ", which falls before the new starting date.";
						message[1] = "Please enter a starting date before the date of any event, or change the date of the event and come back to change the season's starting date afterwards.";
						JOptionPane.showMessageDialog(seasonEditDialog, message, "Oops!", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				season.setStartingDate(date);
				setVisible(false);
				season = null;
			}
		};
		cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				season = null;
			}
		};
		
		okAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		okAction.putValue(Action.LONG_DESCRIPTION,"Apply the entered starting date to the season being edited");
		okAction.putValue(Action.SHORT_DESCRIPTION,"Apply changes to season");
		
		cancelAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		cancelAction.putValue(Action.LONG_DESCRIPTION,"Cancel");
		cancelAction.putValue(Action.SHORT_DESCRIPTION,"Cancel");
	}
	
	private void a2Components() {
		northPanel = new GradientPanel("Edit Selected Season",Tools.getHeaderIcon("leaf"));
		
		startingYearLabel = new JLabel("Starting Year: ");
		yearLabel = new JLabel();
		yearLabel.setHorizontalAlignment(SwingConstants.CENTER);
		startingDateLabel = new JLabel("Starting Date: ");
		startingDateSpinner = new JSpinner(new SpinnerDateModel());
		okButton = new JButton(okAction);
		cancelButton = new JButton(cancelAction);
		
		getRootPane().setDefaultButton(okButton);
		
		JSpinner.DefaultEditor dateEditor = new JSpinner.DateEditor(startingDateSpinner,"M/d/yy");
		dateEditor.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
		startingDateSpinner.setEditor(dateEditor);
		Tools.applyTextPopup(dateEditor.getTextField());
	}
	
	private void a3Listeners() {
		
	}
	
	private void a4Layouts() {
		setLayout(new GridBagLayout());
		
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
		c.weightx = 0.5; c.weighty = 0;
		c.gridx = 0; c.gridy = 1;
		add(startingYearLabel,c);
		c.gridx = 1; c.gridy = 1;
		add(yearLabel,c);
		c.gridx = 0; c.gridy = 2;
		add(startingDateLabel,c);
		c.gridx = 1; c.gridy = 2;
		add(startingDateSpinner,c);
		c.gridwidth = 2; c.gridheight = 1;
		c.gridx = 0; c.gridy = 3;
		add(new JSeparator(SwingConstants.HORIZONTAL),c);
		c.gridwidth = 1; c.gridheight = 1;
		c.gridx = 0; c.gridy = 4;
		add(okButton,c);
		c.gridx = 1; c.gridy = 4;
		add(cancelButton,c);
	}
	
	private void a5Initialize() {
		season = null;
	}
	
	public void display(Season value) {
		if (value != null) {
			season = value;
			northPanel.setTitle("Edit " + season.getStartingYear() + " Season");
			yearLabel.setText(season.getStartingYear().toString());
			startingDateSpinner.setValue(season.getStartingDate());
			pack();
			setLocationRelativeTo(getOwner());
			setVisible(true);
		}
	}
}