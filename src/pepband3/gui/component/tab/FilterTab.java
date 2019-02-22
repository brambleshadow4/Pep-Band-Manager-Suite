package pepband3.gui.component.tab;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public class FilterTab extends BandTableTab {
	public static final String ALL_STRING = "All";
	
	private static final String[] compares = {ALL_STRING,"At Least","At Most","Equals"};
	private static final String[] letters = {ALL_STRING,"A","B","C","D","E","F","G","H","I","J",
												"K","L","M","N","O","P","Q","R","S","T",
												"U","V","W","X","Y","Z"};
	
	private JButton resetButton;
	private JScrollPane scrollPane;
	private ScrollVerticalPanel innerPanel;
		private JLabel searchLabel;
		private SearchField searchField;
		private JPanel namePanel;
		private JLabel nickLabel;
		private JComboBox nickComboBox;
		private JLabel firstLabel;
		private JComboBox firstComboBox;
		private JLabel lastLabel;
		private JComboBox lastComboBox;
		private JLabel classYearLabel;
		private JComboBox classYearComboBox;
		private JLabel instrumentLabel;
		private JComboBox instrumentComboBox;
		private JLabel currentPointsLabel;
		private JComboBox currentPointsComboBox;
		private JSpinner currentPointsSpinner;
		private JComponent glue;
	
	private FilterComboBoxModel classYearModel;
	private FilterComboBoxModel instrumentModel;
	
	private Action resetAction;
	
	private ViewManager viewManager;
	
	public FilterTab(ViewManager paramViewManager, Band paramBand) {
		if (paramViewManager != null) {
			viewManager = paramViewManager;
		} else {
			throw new NullPointerException("FILTER TAB VIEW MANAGER IS NULL");
		}
		
		a1Actions();
		a2Components(paramBand);
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		resetAction = new AbstractAction("Reset Filters") {
			public void actionPerformed(ActionEvent e) {
				firstComboBox.setSelectedItem(ALL_STRING);
				lastComboBox.setSelectedItem(ALL_STRING);
				nickComboBox.setSelectedItem(ALL_STRING);
				classYearComboBox.setSelectedItem(ALL_STRING);
				instrumentComboBox.setSelectedItem(ALL_STRING);
				currentPointsComboBox.setSelectedItem(ALL_STRING);
				currentPointsSpinner.setValue(new Integer(0));
				searchField.getTextField().setText("");
			}
		};
		
		resetAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_R);
		resetAction.putValue(Action.LONG_DESCRIPTION,"Reset all filters to a state that will not filter any members");
		resetAction.putValue(Action.SHORT_DESCRIPTION,"Reset all filters");
	}
	
	private void a2Components(Band paramBand) {
		classYearModel = new FilterComboBoxModel(DataField.CLASS_YEAR, paramBand);
		instrumentModel = new FilterComboBoxModel(DataField.INSTRUMENT, paramBand);
		
		resetButton = new JButton(resetAction);
		
		innerPanel = new ScrollVerticalPanel();
		scrollPane = new JScrollPane(innerPanel);
		scrollPane.setBorder(OptionsDialog.createTitledBorder("Filters"));
		searchLabel = new JLabel("Search");
		searchField = new SearchField(viewManager, null);
		namePanel = new JPanel();
		nickLabel = new JLabel("Nick");
		nickComboBox = new JComboBox(letters);
		firstLabel = new JLabel("First");
		firstComboBox = new JComboBox(letters);
		lastLabel = new JLabel("Last");
		lastComboBox = new JComboBox(letters);
		classYearLabel = new JLabel("Class Year");
		classYearComboBox = new JComboBox(classYearModel);
		classYearComboBox.setRenderer(new ListRenderer());
		instrumentLabel = new JLabel("Instrument");
		instrumentComboBox = new JComboBox(instrumentModel);
		instrumentComboBox.setRenderer(new ListRenderer());
		currentPointsLabel = new JLabel("Current Points");
		currentPointsComboBox = new JComboBox(compares);
		currentPointsSpinner = new JSpinner(new SpinnerNumberModel());
		glue = (JComponent)Box.createVerticalGlue();
		
		Tools.applyTextPopup(((JSpinner.DefaultEditor)currentPointsSpinner.getEditor()).getTextField());
		Tools.applyScrollPopup(scrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(scrollPane.getVerticalScrollBar());
	}
	
	private void a3Listeners() {
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getSource();
				if (source == firstComboBox && e.getStateChange() == ItemEvent.SELECTED) {
					if (firstComboBox.getSelectedItem().equals(ALL_STRING)) {
						viewManager.getFilter().setFirstName(null);
					} else {
						viewManager.getFilter().setFirstName((String)firstComboBox.getSelectedItem());
					}
					viewManager.sort();
				} else if (source == lastComboBox && e.getStateChange() == ItemEvent.SELECTED) {
					if (lastComboBox.getSelectedItem().equals(ALL_STRING)) {
						viewManager.getFilter().setLastName(null);
					} else {
						viewManager.getFilter().setLastName((String)lastComboBox.getSelectedItem());
					}
					viewManager.sort();
				} else if (source == nickComboBox && e.getStateChange() == ItemEvent.SELECTED) {
					if (nickComboBox.getSelectedItem().equals(ALL_STRING)) {
						viewManager.getFilter().setNickName(null);
					} else {
						viewManager.getFilter().setNickName((String)nickComboBox.getSelectedItem());
					}
					viewManager.sort();
				} else if (source == classYearComboBox && e.getStateChange() == ItemEvent.SELECTED) {
					if (classYearComboBox.getSelectedItem().equals(ALL_STRING)) {
						viewManager.getFilter().setClassYear(null);
					} else {
						viewManager.getFilter().setClassYear((Integer)classYearComboBox.getSelectedItem());
					}
					viewManager.sort();
				} else if (source == instrumentComboBox && e.getStateChange() == ItemEvent.SELECTED) {
					if (instrumentComboBox.getSelectedItem().equals(ALL_STRING)) {
						viewManager.getFilter().setInstrument(null);
					} else {
						viewManager.getFilter().setInstrument((Instrument)instrumentComboBox.getSelectedItem());
					}
					viewManager.sort();
				} else if (source == currentPointsComboBox && e.getStateChange() == ItemEvent.SELECTED) {
					applyCurrentPointsFilter();
				}
			}
		};
		currentPointsSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				applyCurrentPointsFilter();
			}
		});
		
		firstComboBox.addItemListener(itemListener);
		lastComboBox.addItemListener(itemListener);
		nickComboBox.addItemListener(itemListener);
		classYearComboBox.addItemListener(itemListener);
		instrumentComboBox.addItemListener(itemListener);
		currentPointsComboBox.addItemListener(itemListener);
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout());
		innerPanel.setLayout(new GridBagLayout());
		namePanel.setLayout(new GridLayout(2,3,GUIManager.INS,GUIManager.INS));
		
		namePanel.add(firstLabel);
		namePanel.add(nickLabel);
		namePanel.add(lastLabel);
		namePanel.add(firstComboBox);
		namePanel.add(nickComboBox);
		namePanel.add(lastComboBox);
		
		GridBagConstraints c = new GridBagConstraints();
		
		int yindex = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		c.gridwidth = 2; c.gridheight = 1;
		c.weightx = 0; c.weighty = 0;
		c.gridx = 0; c.gridy = yindex; yindex++;
		innerPanel.add(searchLabel,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		innerPanel.add(searchField,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		innerPanel.add(instrumentLabel,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		innerPanel.add(instrumentComboBox,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		innerPanel.add(classYearLabel,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		innerPanel.add(classYearComboBox,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		innerPanel.add(namePanel,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		innerPanel.add(currentPointsLabel,c);
		c.gridwidth = 1;
		c.gridx = 0; c.gridy = yindex;
		innerPanel.add(currentPointsComboBox,c);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.gridx = 1; c.gridy = yindex; yindex++;		
		innerPanel.add(currentPointsSpinner,c);
		
		c.gridwidth = 2;
		c.weightx = 0; c.weighty = 1;
		c.gridx = 0; c.gridy = yindex; yindex++;
		innerPanel.add(glue,c);
		
		add(resetButton,BorderLayout.NORTH);
		add(scrollPane,BorderLayout.CENTER);
	}
	
	private void a5Initialize() {
		instrumentComboBox.putClientProperty("tip","Filter by a member's instrument");
		classYearComboBox.putClientProperty("tip","Filter by a member's class year");
		lastComboBox.putClientProperty("tip","Filter by a member's last name");
		firstComboBox.putClientProperty("tip","Filter by a member's first name");
		nickComboBox.putClientProperty("tip","Filter by a member's nickname");
		currentPointsComboBox.putClientProperty("tip","Filter by the current points a member has");
		((JSpinner.DefaultEditor)currentPointsSpinner.getEditor()).getTextField().putClientProperty("tip","Filter by the current points a member has");
	}
	
	private void applyCurrentPointsFilter() {
		if (currentPointsComboBox.getSelectedItem().equals(compares[0])) {
			viewManager.getFilter().setMaxPoints(null);
			viewManager.getFilter().setMinPoints(null);
		} else if (currentPointsComboBox.getSelectedItem().equals(compares[1])) {
			viewManager.getFilter().setMaxPoints(null);
			viewManager.getFilter().setMinPoints((Integer)currentPointsSpinner.getValue());
		} else if (currentPointsComboBox.getSelectedItem().equals(compares[2])) {
			viewManager.getFilter().setMaxPoints((Integer)currentPointsSpinner.getValue());
			viewManager.getFilter().setMinPoints(null);
		} else if (currentPointsComboBox.getSelectedItem().equals(compares[3])) {
			viewManager.getFilter().setMaxPoints((Integer)currentPointsSpinner.getValue());
			viewManager.getFilter().setMinPoints((Integer)currentPointsSpinner.getValue());
		}
		viewManager.sort();
	}
	
	public Integer getIndex() {
		return Tools.getBoolean("Editing Tab Priority", false) ? new Integer(2) : new Integer(1);
	}
	
	public String getTabIconName() {
		return "filter";
	}
	
	public String getTabName() {
		return "Filters";
	}
	
	public String getToolTipText() {
		return "Filter Tab";
	}
	
	public void uninstall() {
		classYearModel.uninstall();
		instrumentModel.uninstall();
	}
}