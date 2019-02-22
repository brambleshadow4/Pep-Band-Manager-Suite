package pepband3.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.*;

public class SeasonAddDialog extends JDialog {
	
	private GradientPanel northPanel;
	private JLabel startingYearLabel;
	private JSpinner startingYearSpinner;
	private JLabel startingDateLabel;
	private JSpinner startingDateSpinner;
	private JButton okButton, cancelButton;
	
	private Action okAction, cancelAction;
	
	private Season createdSeason;
	
	public SeasonAddDialog(JDialog owner) {
		super(owner,"Add Season",true);
		
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
		final SeasonAddDialog seasonAddDialog = this;
		okAction = new AbstractAction("Add Season") {
			public void actionPerformed(ActionEvent e) {
				DataManager dataManager = DataManager.getDataManager();
				Integer year = (Integer)startingYearSpinner.getValue();
				Date date = (Date)startingDateSpinner.getValue();
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				Integer dateYear = calendar.get(Calendar.YEAR);
				
				if (!dateYear.equals(year)) {
					String[] message = new String[1];
					message[0] = DateFormat.getDateInstance(DateFormat.SHORT).format(date) + " does not fall within the year " + year + "!";
					JOptionPane.showMessageDialog(seasonAddDialog, message, "Oops!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				for (Season season : dataManager.getSeasons()) {
					if (season.getStartingYear().equals(year)) {
						String[] message = new String[3];
						message[0] = "A " + year + " - " + (year + 1) + " Season already exists!";
						message[1] = " \t ";
						message[2] = "You may overwrite the existing " + year + " season by deleting it and then adding a new " + year + " season.";
						JOptionPane.showMessageDialog(seasonAddDialog, message, "Uh oh!", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				Season newSeason = new Season(dataManager.getDataID(), new ArrayList<Member>(), year, date, new ArrayList<PepBandEvent>());
				dataManager.addSeason(newSeason);
				createdSeason = newSeason;
				setVisible(false);
			}
		};
		cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		
		okAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		okAction.putValue(Action.LONG_DESCRIPTION,"Add this season to the program's database with the entered properties");
		okAction.putValue(Action.SHORT_DESCRIPTION,"Add new season");
		
		cancelAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		cancelAction.putValue(Action.LONG_DESCRIPTION,"Cancel");
		cancelAction.putValue(Action.SHORT_DESCRIPTION,"Cancel");
	}
	
	private void a2Components() {
		northPanel = new GradientPanel("Add a New Season",Tools.getHeaderIcon("snowflake"));
		
		startingYearLabel = new JLabel("Starting Year: ");
		startingYearSpinner = new JSpinner(new SpinnerNumberModel());
		startingDateLabel = new JLabel("Starting Date: ");
		startingDateSpinner = new JSpinner(new SpinnerDateModel());
		okButton = new JButton(okAction);
		cancelButton = new JButton(cancelAction);
		
		getRootPane().setDefaultButton(okButton);
		
		JSpinner.DefaultEditor yearEditor = new JSpinner.NumberEditor(startingYearSpinner,"#");
		yearEditor.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
		startingYearSpinner.setEditor(yearEditor);
		Tools.applyTextPopup(yearEditor.getTextField());
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
		add(startingYearSpinner,c);
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
		startingYearSpinner.setValue(Calendar.getInstance().get(Calendar.YEAR));
		startingDateSpinner.setValue(Calendar.getInstance().getTime());
		createdSeason = null;
	}
	
	public void display() {
		pack();
		setLocationRelativeTo(getOwner());
		createdSeason = null;
		setVisible(true);
	}
	
	public Season display(boolean value) {
		pack();
		setLocationRelativeTo(getOwner());
		createdSeason = null;
		setVisible(true);
		return createdSeason;
	}
}