package pepband3.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import pepband3.data.DataManager;
import pepband3.data.Member;
import pepband3.data.Season;
import pepband3.gui.component.BandTable;
import pepband3.gui.component.SearchField;
import pepband3.gui.dialog.EventTypesDialog;
import pepband3.gui.dialog.InstrumentsDialog;
import pepband3.gui.dialog.LocationsDialog;
import pepband3.gui.dialog.PreviewDialog;
import pepband3.gui.dialog.SeasonsDialog;
import pepband3.gui.extra.IconDisplayer;
import pepband3.gui.extra.RunnableAction;
import pepband3.names.CensusNames;


public class OptionsDialog extends JDialog implements IconDisplayer {
	
	private static final Integer[] iconSizes = {new Integer(16),new Integer(32),new Integer(48)};
	
	private AppWindow appWindow;
	
	private JTabbedPane tabbedPane;
	
	private JPanel appearancePanel;
		private JPanel themePanel;
			private JLabel themeLabel;
			private JButton themeButton;
		private JPanel windowPanel;
			private JCheckBox watermarkBox, southPanelBox;
			private JPanel screenFractionPanel;
				private JLabel screenFractionLabel;
				private JSpinner screenFractionSpinner;
		private JPanel iconPanel;
			private JPanel toolBarIconPanel;
				private JLabel toolbarIconSizeLabel;
				private JComboBox toolbarIconSizeBox;
			private JCheckBox toolbarShowIconTextBox;
			private JPanel bandTableIconPanel;
				private JLabel bandTableIconSizeLabel;
				private JComboBox bandTableIconSizeBox;
			private JCheckBox bandTableShowIconTextBox;
			private JPanel miscIconPanel;
				private JLabel miscIconSizeLabel;
				private JComboBox miscIconSizeBox;
			private JCheckBox miscShowIconTextBox;	
		private JPanel toolbarPanel;
			private JCheckBox showToolbarBox, lockToolbarBox;
	private JPanel dataPanel;
		private JPanel seasonPanel;
			private JButton seasonButton;
		private JPanel instrumentPanel;
			private JButton instrumentButton;
		private JPanel eventTypePanel;
			private JButton eventTypeButton;
		private JPanel locationPanel;
			private JButton locationButton;
		private JPanel otherDataPanel;
			private JButton sexAssignButton;
	private JPanel programPanel;
		private JPanel generalPanel;
			private JPanel booleanPanel;
				private JCheckBox searchOptionsBox;
				private JCheckBox editingTabPriorityBox;
			private JPanel couplePanel;
				private JLabel exportDirectoryLabel;
				private JTextField exportDirectoryField;
				private JButton directoryButton;
	
	private SeasonsDialog seasonsDialog;
	private InstrumentsDialog instrumentsDialog;
	private EventTypesDialog eventTypesDialog;
	private LocationsDialog locationsDialog;
	
	private RunnableAction themeAction, seasonAction, instrumentAction, eventTypeAction, locationAction, sexAssignAction, directoryAction;
	
	public OptionsDialog(AppWindow owner) {
		super(owner,"Options",false);
		
		appWindow = owner;
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
		themeAction = new RunnableAction("Select Theme","Selecting and implementing a new theme",true) {
			public void act() {
				final UIManager.LookAndFeelInfo[] installedList = UIManager.getInstalledLookAndFeels();
				if (installedList != null && installedList.length > 0) {
					String[] names = new String[installedList.length];
					for (int index = 0; index < installedList.length; index++) {
						names[index] = installedList[index].getName();
					}
					String currentName = getLookAndFeelName(UIManager.getLookAndFeel());
					String result = (String)JOptionPane.showInputDialog(themeButton, "Available Look And Feels found on this system:", "Select New Theme", JOptionPane.QUESTION_MESSAGE, null, names, currentName);
					if (result != null) {
						for (int index = 0; index < installedList.length; index++) {
							if (installedList[index].getName().equals(result)) {
								try {
									final int installIndex = index;
									SwingUtilities.invokeAndWait(new Runnable() {
										public void run() {
											if (Tools.setLookAndFeel(installedList[installIndex].getClassName())) {
												themeLabel.setText("Current Theme: " + installedList[installIndex].getName());
											} else {
												JOptionPane.showMessageDialog(themeButton, "The program could not change the theme to " + installedList[installIndex].getName(), "Exception", JOptionPane.ERROR_MESSAGE);
											}
										}
									});
								} catch (Exception exc) {
									JOptionPane.showMessageDialog(themeButton, "The program could not change the theme to " + installedList[index].getName(), "Exception", JOptionPane.ERROR_MESSAGE);
									System.err.println("Exception while trying to change LAF");
									exc.printStackTrace();
								}
								break;
							}
						}
					}
				}
			}
		};
		seasonAction = new RunnableAction("Manage Seasons",null) {
			public void act() {
				seasonsDialog.display();
			}
		};
		instrumentAction = new RunnableAction("Manage Instruments",null) {
			public void act() {
				instrumentsDialog.display();
			}
		};
		eventTypeAction = new RunnableAction("Manage Event Types",null) {
			public void act() {
				eventTypesDialog.display();
			}
		};
		locationAction = new RunnableAction("Manage Locations",null) {
			public void act() {
				locationsDialog.display();
			}
		};
		sexAssignAction = new RunnableAction("Assign Sex to Members",null) {
			public void act() {
				if (!DataManager.getDataManager().getSeasons().isEmpty()) {
					final Season season = (Season) JOptionPane.showInputDialog(
							OptionsDialog.this,
							"<html>Select the season in which you would like to auto-assign sex to all members.<br>Note that this will overwrite the current sex values of all members in the season for which a sex is determined.<br><br>Select a season and click <i>OK</i> to proceed.<br>&nbsp</html>",
							"Auto Assign Member Sex",
							JOptionPane.PLAIN_MESSAGE,
							null,
							DataManager.getDataManager().getSeasons().toArray(),
							DataManager.getDataManager().getSeasons().get(0));
					if (season != null) {
						int male = 0;
						int female = 0;
						int unrecognized = 0;
						int unclear = 0;
						for (final Member member : season.getMembers()) {
							final Object result = CensusNames.getSex(member.getFirstName());
							if (result == CensusNames.MALE) {
								member.setSex(Member.Sex.MALE);
								male++;
							} else if (result == CensusNames.FEMALE) {
								member.setSex(Member.Sex.FEMALE);
								female++;
							} else if (result == CensusNames.UNKNOWN){
								unrecognized++;
							} else if (result == CensusNames.UNCLEAR){
								unclear++;
							}
						}
						JOptionPane.showMessageDialog(
								OptionsDialog.this,
								"<html>Auto assigning of sex to members in " + season.getName() + " completed successfully!<br>The utility made the following assignments:<br>&nbsp<br>Male: <b>" + male + "</b><br>Female: <b>" + female + "</b><br>Unrecognized: <b>" + unrecognized + "</b><br>Unclear: <b>" + unclear + "</b></html>",
								"Auto Assign Complete",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		};
		directoryAction = new RunnableAction("...",null) {
			public void act() {
				JFileChooser fileChooser = appWindow.getFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				for (FileFilter fileFilter : fileChooser.getChoosableFileFilters()) {
					fileChooser.removeChoosableFileFilter(fileFilter);
				}
				fileChooser.setApproveButtonText("Select");
				fileChooser.setApproveButtonMnemonic(KeyEvent.VK_S);
				fileChooser.setApproveButtonToolTipText("Select directory as default export directory");
				fileChooser.setDialogTitle("Choose Default Export Directory");
				fileChooser.setSelectedFile(new File(Tools.getProperty("Default Export Directory"), ""));
				if (fileChooser.showDialog(appWindow, null) == JFileChooser.APPROVE_OPTION) {
					exportDirectoryField.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		};
		
		themeAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_S);
		themeAction.putValue(Action.LONG_DESCRIPTION,"Select a new theme for the program from a list of installed Java Look And Feels");
		themeAction.putValue(Action.SHORT_DESCRIPTION,"Select new theme");
		
		seasonAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_S);
		seasonAction.putValue(Action.LONG_DESCRIPTION,"Change the current season or add/remove seasons to/from the program's database");
		seasonAction.putValue(Action.SHORT_DESCRIPTION,"Manage seasons");
		
		instrumentAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_I);
		instrumentAction.putValue(Action.LONG_DESCRIPTION,"Change the names and/or ordering of instruments, or add/merge instruments");
		instrumentAction.putValue(Action.SHORT_DESCRIPTION,"Manage instruments");
		
		eventTypeAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_P);
		eventTypeAction.putValue(Action.LONG_DESCRIPTION,"Change the names, icons, and/or default point values of event types, or add/merge event types");
		eventTypeAction.putValue(Action.SHORT_DESCRIPTION,"Manage event types");
		
		locationAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_L);
		locationAction.putValue(Action.LONG_DESCRIPTION,"Change the names and/or ordering of locations, or add/merge locations");
		locationAction.putValue(Action.SHORT_DESCRIPTION,"Manage locations");
		
		sexAssignAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		sexAssignAction.putValue(Action.LONG_DESCRIPTION,"Automatically determine and assign the sex of members in a season using US Census data");
		sexAssignAction.putValue(Action.SHORT_DESCRIPTION,"Auto assign sex to members");
		
		directoryAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
		directoryAction.putValue(Action.LONG_DESCRIPTION,"Choose a directory...");
		directoryAction.putValue(Action.SHORT_DESCRIPTION,"Choose a directory...");
	}
	
	private void a2Components() {
		seasonsDialog = new SeasonsDialog(this);
		Tools.addRootComponent(seasonsDialog);
		instrumentsDialog = new InstrumentsDialog(this);
		Tools.addRootComponent(instrumentsDialog);
		eventTypesDialog = new EventTypesDialog(this);
		Tools.addRootComponent(eventTypesDialog);
		locationsDialog = new LocationsDialog(this);
		Tools.addRootComponent(locationsDialog);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.WRAP_TAB_LAYOUT);
		tabbedPane.setMinimumSize(new Dimension(450,600));
		tabbedPane.setPreferredSize(new Dimension(450,600));
		
		appearancePanel = new JPanel();
		themePanel = new JPanel();
		themePanel.setBorder(createTitledBorder("Theme"));
		themeLabel = new JLabel("Current Theme: " + getLookAndFeelName(UIManager.getLookAndFeel()));
		themeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		themeButton = new JButton(themeAction);
		windowPanel = new JPanel();
		windowPanel.setBorder(createTitledBorder("Application Window"));
		watermarkBox = new JCheckBox("Display watermark",Tools.getBoolean("Display Watermark",true));
		screenFractionPanel = new JPanel();
		screenFractionLabel = new JLabel("Size at start:");
		screenFractionLabel.setHorizontalAlignment(SwingConstants.LEFT);
		screenFractionSpinner = new JSpinner(new SpinnerNumberModel(Tools.getDouble("Screen Fraction",0.9), new Double(0.1), new Double(1.0), new Double(0.05)));
		southPanelBox = new JCheckBox("Show south panel",Tools.getBoolean("Show South Panel",true));
		
		iconPanel = new JPanel();
		iconPanel.setBorder(createTitledBorder("Icons"));
		toolBarIconPanel = new JPanel();
		toolbarIconSizeLabel = new JLabel("Toolbar icon size: ");
		toolbarIconSizeBox = new JComboBox(iconSizes);
		toolbarIconSizeBox.setSelectedItem(Tools.getInteger("Toolbar Icon Size",32));
		toolbarShowIconTextBox = new JCheckBox("Show text with Toolbar icons",Tools.getBoolean("Show Toolbar Icon Text",false));
		bandTableIconPanel = new JPanel();
		bandTableIconSizeLabel = new JLabel("Table icon size: ");
		bandTableIconSizeBox = new JComboBox(iconSizes);
		bandTableIconSizeBox.setSelectedItem(Tools.getInteger("BandTable Icon Size",32));
		bandTableShowIconTextBox = new JCheckBox("Show text with table icons",Tools.getBoolean("Show BandTable Icon Text",false));
		miscIconPanel = new JPanel();
		miscIconSizeLabel = new JLabel("Window icon size: ");
		miscIconSizeBox = new JComboBox(iconSizes);
		miscIconSizeBox.setSelectedItem(Tools.getInteger("Misc Icon Size",16));
		miscShowIconTextBox = new JCheckBox("Show text with window icons",Tools.getBoolean("Show Misc Icon Text",true));
		
		toolbarPanel = new JPanel();
		toolbarPanel.setBorder(createTitledBorder("Toolbar"));
		showToolbarBox = new JCheckBox("Show Toolbar",Tools.getBoolean("Show Toolbar",true));
		lockToolbarBox = new JCheckBox("Lock Toolbar",Tools.getBoolean("Lock Toolbar",true));
		
		dataPanel = new JPanel();
		seasonPanel = new JPanel();
		seasonPanel.setBorder(createTitledBorder("Seasons"));
		seasonButton = new JButton(seasonAction);
		instrumentPanel = new JPanel();
		instrumentPanel.setBorder(createTitledBorder("Instruments"));
		instrumentButton = new JButton(instrumentAction);
		eventTypePanel = new JPanel();
		eventTypePanel.setBorder(createTitledBorder("Event Types"));
		eventTypeButton = new JButton(eventTypeAction);
		locationPanel = new JPanel();
		locationPanel.setBorder(createTitledBorder("Locations"));
		locationButton = new JButton(locationAction);
		otherDataPanel = new JPanel();
		otherDataPanel.setBorder(createTitledBorder("Other"));
		sexAssignButton = new JButton(sexAssignAction);
		
		programPanel = new JPanel();
		
		generalPanel = new JPanel();
		generalPanel.setBorder(createTitledBorder("General Options"));
		booleanPanel = new JPanel();
		searchOptionsBox = new JCheckBox("Use simple search fields", Tools.getBoolean("Simple Search Fields", false));
		editingTabPriorityBox = new JCheckBox("Editing tab priorirty", Tools.getBoolean("Editing Tab Priority", false));
		couplePanel = new JPanel();
		exportDirectoryLabel = new JLabel("Default export directory:");
		exportDirectoryField = new JTextField(Tools.getProperty("Default Export Directory"));
		directoryButton = new JButton(directoryAction);
	}
	
	private void a3Listeners() {
		DocumentListener documentListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if (e.getDocument() == exportDirectoryField.getDocument()) {
					Tools.setProperty("Default Export Directory", exportDirectoryField.getText());
				}
			}
			
			public void insertUpdate(DocumentEvent e) {
				if (e.getDocument() == exportDirectoryField.getDocument()) {
					Tools.setProperty("Default Export Directory", exportDirectoryField.getText());
				}
			}
			
			public void removeUpdate(DocumentEvent e) {
				if (e.getDocument() == exportDirectoryField.getDocument()) {
					Tools.setProperty("Default Export Directory", exportDirectoryField.getText());
				}
			}
		};
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Object source = e.getSource();
				if (source == screenFractionSpinner) {
					Tools.setProperty("Screen Fraction",Double.toString((Double)screenFractionSpinner.getValue()));
				}
			}
		};
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getSource();
				if (source == showToolbarBox) {
					Tools.setProperty("Show Toolbar",Boolean.toString(showToolbarBox.isSelected()));
					appWindow.setShowToolbar(showToolbarBox.isSelected());
				} else if (source == lockToolbarBox) {
					Tools.setProperty("Lock Toolbar",Boolean.toString(lockToolbarBox.isSelected()));
					appWindow.setLockToolbar(lockToolbarBox.isSelected());
				} else if (source == toolbarIconSizeBox) {
					Tools.setProperty("Toolbar Icon Size",Integer.toString((Integer)toolbarIconSizeBox.getSelectedItem()));
					for (Component component : Tools.getRootComponents()) {
						updateIconSizes(component);
					}
				} else if (source == toolbarShowIconTextBox) {
					Tools.setProperty("Show Toolbar Icon Text",Boolean.toString(toolbarShowIconTextBox.isSelected()));
					for (Component component : Tools.getRootComponents()) {
						updateShowIconText(component);
					}
				} else if (source == bandTableIconSizeBox) {
					Tools.setProperty("BandTable Icon Size",Integer.toString((Integer)bandTableIconSizeBox.getSelectedItem()));
					for (Component component : Tools.getRootComponents()) {
						updateIconSizes(component);
					}
				} else if (source == bandTableShowIconTextBox) {
					Tools.setProperty("Show BandTable Icon Text",Boolean.toString(bandTableShowIconTextBox.isSelected()));
					for (Component component : Tools.getRootComponents()) {
						updateShowIconText(component);
					}
				} else if (source == miscIconSizeBox) {
					Tools.setProperty("Misc Icon Size",Integer.toString((Integer)miscIconSizeBox.getSelectedItem()));
					for (Component component : Tools.getRootComponents()) {
						updateIconSizes(component);
					}
				} else if (source == miscShowIconTextBox) {
					Tools.setProperty("Show Misc Icon Text",Boolean.toString(miscShowIconTextBox.isSelected()));
					for (Component component : Tools.getRootComponents()) {
						updateShowIconText(component);
					}
				} else if (source == watermarkBox) {
					Tools.setProperty("Display Watermark",Boolean.toString(watermarkBox.isSelected()));
					Tools.getDesktopPane().repaint();
				} else if (source == southPanelBox) {
					Tools.setProperty("Show South Panel",Boolean.toString(southPanelBox.isSelected()));
					appWindow.setShowSouthPanel(southPanelBox.isSelected());
				} else if (source == searchOptionsBox) {
					Tools.setProperty("Simple Search Fields", Boolean.toString(searchOptionsBox.isSelected()));
					for (Component component : Tools.getRootComponents()) {
						updateSearchFields(component);
					}
				} else if (source == editingTabPriorityBox) {
					Tools.setProperty("Editing Tab Priority", Boolean.toString(editingTabPriorityBox.isSelected()));
					for (Component component : Tools.getRootComponents()) {
						updateTabOrder(component);
					}
				}
			}
		};
		
		exportDirectoryField.getDocument().addDocumentListener(documentListener);
		
		screenFractionSpinner.addChangeListener(changeListener);
		
		southPanelBox.addItemListener(itemListener);
		watermarkBox.addItemListener(itemListener);
		toolbarIconSizeBox.addItemListener(itemListener);
		toolbarShowIconTextBox.addItemListener(itemListener);
		bandTableIconSizeBox.addItemListener(itemListener);
		bandTableShowIconTextBox.addItemListener(itemListener);
		miscIconSizeBox.addItemListener(itemListener);
		miscShowIconTextBox.addItemListener(itemListener);
		showToolbarBox.addItemListener(itemListener);
		lockToolbarBox.addItemListener(itemListener);
		searchOptionsBox.addItemListener(itemListener);
		editingTabPriorityBox.addItemListener(itemListener);
	}
	
	private void a4Layouts() {
		themePanel.setLayout(new FlowLayout(FlowLayout.LEFT,4,4));
		themePanel.add(themeLabel);
		themePanel.add(Box.createHorizontalStrut(8));
		themePanel.add(themeButton);
		
		windowPanel.setLayout(new GridLayout(2,2,4,4));
		windowPanel.add(watermarkBox);
		windowPanel.add(screenFractionPanel);
		windowPanel.add(southPanelBox);
		windowPanel.add(Box.createHorizontalGlue());
		
		screenFractionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		screenFractionPanel.add(screenFractionLabel);
		screenFractionPanel.add(screenFractionSpinner);
		
		iconPanel.setLayout(new GridLayout(3,2,4,4));
		iconPanel.add(toolBarIconPanel);
		iconPanel.add(toolbarShowIconTextBox);
		iconPanel.add(bandTableIconPanel);
		iconPanel.add(bandTableShowIconTextBox);
		iconPanel.add(miscIconPanel);
		iconPanel.add(miscShowIconTextBox);
		
		toolBarIconPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolBarIconPanel.add(toolbarIconSizeLabel);
		toolBarIconPanel.add(toolbarIconSizeBox);
		bandTableIconPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		bandTableIconPanel.add(bandTableIconSizeLabel);
		bandTableIconPanel.add(bandTableIconSizeBox);
		miscIconPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		miscIconPanel.add(miscIconSizeLabel);
		miscIconPanel.add(miscIconSizeBox);
		
		toolbarPanel.setLayout(new GridLayout(1,2,4,4));
		toolbarPanel.add(showToolbarBox);
		toolbarPanel.add(lockToolbarBox);
		
		appearancePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		c.weightx = 1; c.weighty = 0;
		c.gridx = 0; c.gridy = 0;
		appearancePanel.add(themePanel,c);
		c.gridx = 0; c.gridy = 1;
		appearancePanel.add(windowPanel,c);
		c.gridx = 0; c.gridy = 2;
		appearancePanel.add(iconPanel,c);
		c.gridx = 0; c.gridy = 3;
		appearancePanel.add(toolbarPanel,c);
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 4;
		appearancePanel.add(Box.createVerticalGlue(),c);
		
		seasonPanel.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0; c.gridy = 0;
		seasonPanel.add(seasonButton,c);
		
		instrumentPanel.setLayout(new GridBagLayout());
		c.gridx = 0; c.gridy = 0;
		instrumentPanel.add(instrumentButton,c);
		
		eventTypePanel.setLayout(new GridBagLayout());
		c.gridx = 0; c.gridy = 0;
		eventTypePanel.add(eventTypeButton,c);
		
		locationPanel.setLayout(new GridBagLayout());
		c.gridx = 0; c.gridy = 0;
		locationPanel.add(locationButton,c);
		
		otherDataPanel.setLayout(new GridBagLayout());
		c.gridx = 0; c.gridy = 0;
		otherDataPanel.add(sexAssignButton,c);
		
		dataPanel.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 0;
		c.gridx = 0; c.gridy = 0;
		dataPanel.add(seasonPanel,c);
		c.gridx = 0; c.gridy = 1;
		dataPanel.add(instrumentPanel,c);
		c.gridx = 0; c.gridy = 2;
		dataPanel.add(eventTypePanel,c);
		c.gridx = 0; c.gridy = 3;
		dataPanel.add(locationPanel,c);
		c.gridx = 0; c.gridy = 4;
		dataPanel.add(otherDataPanel,c);
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 5;
		dataPanel.add(Box.createVerticalGlue(),c);
		
		booleanPanel.setLayout(new GridLayout(0,2,GUIManager.INS,GUIManager.INS));
		booleanPanel.add(searchOptionsBox);
		booleanPanel.add(editingTabPriorityBox);
		
		couplePanel.setLayout(new GridBagLayout());
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0; c.weighty = 0;
		c.gridx = 0; c.gridy = 0;
		couplePanel.add(exportDirectoryLabel, c);
		c.weightx = 1; c.weighty = 0;
		c.gridx = 1; c.gridy = 0;
		couplePanel.add(exportDirectoryField, c);
		c.weightx = 0; c.weighty = 0;
		c.gridx = 2; c.gridy = 0;
		couplePanel.add(directoryButton, c);
		
		generalPanel.setLayout(new BorderLayout(0,0));
		generalPanel.add(booleanPanel, BorderLayout.NORTH);
		generalPanel.add(Box.createVerticalStrut(8), BorderLayout.CENTER);
		generalPanel.add(couplePanel, BorderLayout.SOUTH);
		
		programPanel.setLayout(new GridBagLayout());
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 0;
		c.gridx = 0; c.gridy = 0;
		programPanel.add(generalPanel, c);
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 1;
		programPanel.add(Box.createVerticalGlue(), c);
		
		setLayout(new GridBagLayout());
		
		c.insets = new Insets(0,0,0,0);
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 0;
		add(tabbedPane,c);
		
		tabbedPane.add("Appearance",appearancePanel);
		tabbedPane.addTab("Data",dataPanel);
		tabbedPane.addTab("Program",programPanel);
	}
	
	private void a5Initialize() {
		prepareToolTips();
		setIconSize(Tools.getInteger("Misc Icon Size",32));
		setShowIconText(Tools.getBoolean("Show Misc Icon Text",false));
	}
	
	public static Border createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(title);
	}
	
	public void display() {
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}
	
	public void displaySeasonsManager() {
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
		tabbedPane.setSelectedComponent(dataPanel);
		seasonsDialog.display();
	}
	
	private String getLookAndFeelName(LookAndFeel lookAndFeel) {
		UIManager.LookAndFeelInfo[] installedList = UIManager.getInstalledLookAndFeels();
		if (installedList != null && installedList.length > 0) {
			for (int index = 0; index < installedList.length; index++) {
				if (installedList[index].getClassName().equals(lookAndFeel.getClass().getName())) {
					return installedList[index].getName();
				}
			}
			return lookAndFeel.getName();
		} else {
			return lookAndFeel.getName();
		}
	}
	
	public void prepareToolTips() {
		tabbedPane.setToolTipTextAt(tabbedPane.indexOfComponent(appearancePanel),"Appearance");
		tabbedPane.setToolTipTextAt(tabbedPane.indexOfComponent(dataPanel),"Data");
		tabbedPane.setToolTipTextAt(tabbedPane.indexOfComponent(programPanel),"Program");
		
		watermarkBox.setToolTipText("The application window will display a white background with a Cornell watermark instead of the theme's default background");
		southPanelBox.setToolTipText("Show the thin panel at the bottom of the application window with rollover tips");
		screenFractionLabel.setToolTipText("Set the fraction of the screen that the application window will size itself to occupy when it loads");
		screenFractionSpinner.setToolTipText("Set the fraction of the screen that the application window will size itself to occupy when it loads");
		toolbarIconSizeBox.setToolTipText("Set the toolbar icon size");
		toolbarShowIconTextBox.setToolTipText("Show toolbar button text below icon");
		bandTableIconSizeBox.setToolTipText("Set the table tab icon size");
		bandTableShowIconTextBox.setToolTipText("Show table tab text next to icon");
		miscIconSizeBox.setToolTipText("Set icon size for misc. icons");
		miscShowIconTextBox.setToolTipText("Show a misc. icon's text with the icon");
		showToolbarBox.setToolTipText("Show the application's toolbar in addition to its menu");
		lockToolbarBox.setToolTipText("Do not allow the toolbar to be draggged to different cardinal sides of the application window");
		searchOptionsBox.setToolTipText("Use simple search field options instead of showing a button to select which fields to use");
		editingTabPriorityBox.setToolTipText("Editing tab has priority over the Filter tab in the roster of season windows");
		exportDirectoryField.setToolTipText("The default directory to export PDFs to");
	}
	
	public void setIconSize(Integer value) {
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(appearancePanel),Tools.getIcon("appearance"+ value +""));
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(dataPanel),Tools.getIcon("data"+ value +""));
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(programPanel),Tools.getIcon("program"+ value +""));
	}
	
	public void setShowIconText(boolean value) {
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(appearancePanel),value ? "Appearance" : null);
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(dataPanel),value ? "Data" : null);
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(programPanel),value ? "Program" : null);
	}
	
	private void updateIconSizes(Component component) {
		if (component instanceof Container) {
			Container container = (Container)component;
			Component[] children = container.getComponents();
			if (children != null) {
				for (Component child : children) {
					updateIconSizes(child);
				}
			}
		}
		if (component instanceof AppWindow || component instanceof PreviewDialog) {
			IconDisplayer iconDisplayer = (IconDisplayer)component;
			iconDisplayer.setIconSize((Integer)toolbarIconSizeBox.getSelectedItem());
		} else if (component instanceof BandTable) {
			BandTable iconDisplayer = (BandTable)component;
			iconDisplayer.setIconSize((Integer)bandTableIconSizeBox.getSelectedItem());
		} else if (component instanceof IconDisplayer) {
			IconDisplayer iconDisplayer = (IconDisplayer)component;
			iconDisplayer.setIconSize((Integer)miscIconSizeBox.getSelectedItem());
		}
	}
	
	private void updateSearchFields(Component component) {
		if (component instanceof Container) {
			Container container = (Container)component;
			Component[] children = container.getComponents();
			if (children != null) {
				for (Component child : children) {
					updateSearchFields(child);
				}
			}
		}
		if (component instanceof SearchField) {
			SearchField searchField = (SearchField) component;
			searchField.setOptionsVisible(!searchOptionsBox.isSelected());
		}
	}
	
	private void updateShowIconText(Component component) {
		if (component instanceof Container) {
			Container container = (Container)component;
			Component[] children = container.getComponents();
			if (children != null) {
				for (Component child : children) {
					updateShowIconText(child);
				}
			}
		}
		if (component instanceof AppWindow) {
			AppWindow iconDisplayer = (AppWindow)component;
			iconDisplayer.setShowIconText(toolbarShowIconTextBox.isSelected());
		} else if (component instanceof BandTable) {
			BandTable iconDisplayer = (BandTable)component;
			iconDisplayer.setShowIconText(bandTableShowIconTextBox.isSelected());
		} else if (component instanceof IconDisplayer) {
			IconDisplayer iconDisplayer = (IconDisplayer)component;
			iconDisplayer.setShowIconText(miscShowIconTextBox.isSelected());
		}
	}
	
	private void updateTabOrder(Component component) {
		if (component instanceof Container) {
			Container container = (Container)component;
			Component[] children = container.getComponents();
			if (children != null) {
				for (Component child : children) {
					updateTabOrder(child);
				}
			}
		}
		if (component instanceof BandTable) {
			BandTable bandTable = (BandTable) component;
			bandTable.reinstallTabs();
		}
	}
}