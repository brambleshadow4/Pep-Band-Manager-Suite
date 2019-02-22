package pepband3.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.component.*;
import pepband3.gui.component.chart.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.component.tab.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public class MemberWindow extends InternalWindow implements IconDisplayer, ClipboardOwner {
	
	private static final String[] STYLES = {"Regular","Bold"};
	
	private GradientPanel northPanel;
	private JTabbedPane tabbedPane;
	private JPanel infoPanel;
		private JPanel leftPanel;
			private JPanel propertiesPanel;
				private JScrollPane propertiesScrollPane;
				private JTextPane propertiesPane;
				private StyledDocument propertiesDocument;
			private JPanel linksPanel;
				private JButton editButton, emailButton, copyButton;
		private JPanel centerPanel;
			private JPanel eventTypePanel;
				private JLabel eventTypesLabel;
				private PieChart eventTypesChart;
			private JPanel locationsPanel;
				private JLabel locationsLabel;
				private PieChart locationsChart;
	private JPanel historyPanel;
		private JPanel seasonPanel;
			private JLabel seasonLabel;
			private JComboBox seasonBox;
			private JButton seasonButton;
			private HistoryChart historyChart;
	
	private RunnableAction seasonAction, editAction, emailAction, copyAction;
	
	private DataListener dataListener;
	private MemberSeasonsComboBoxModel comboBoxModel;
	private Member member;
	
	public MemberWindow(Member paramMember) {
		super("Member - " + paramMember.getFullName(), true, true, true, true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setFrameIcon(Tools.getIcon("member16"));
		
		member = paramMember;
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		final MemberWindow thisWindow = this;
		seasonAction = new RunnableAction("Open Season","Opening selected season") {
			public void act() {
				Season season = (Season)seasonBox.getSelectedItem();
				if (season != null) {
					Tools.getDesktopPane().addSeasonWindow(season).display();
				}
			}
		};
		editAction = new RunnableAction("Edit Member",null) {
			public void act() {
				ArrayList<Member> singularList = new ArrayList<Member>();
				singularList.add(member);
				EditingTab.getEditDialog().display(singularList);
			}
		};
		emailAction = new RunnableAction("Email Member","Opening OS email client and preparing email to member",true) {
			public void act() {
				try {
					String addressString = member.getFullName() + "<" + member.getNetID() + "@cornell.edu" + ">";
					URI mailTo = new URI("mailto:" + URITools.replaceEscapeCharacters(addressString) + "?subject=Pep%20Band");
					Desktop.getDesktop().mail(mailTo);
				} catch (Exception exc) {
					System.err.println("Could not open email to member");
					exc.printStackTrace();
				}
			}
		};
		copyAction = new RunnableAction("Copy Member","Copying member to clipboard") {
			public void act() {
				try {
					Integer[] idArray = {member.getID()};
					IDTransferable transferable = new IDTransferable(idArray);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable,thisWindow);
				} catch (Exception exc) {
					System.err.println("Could not copy member to clipboard");
					exc.printStackTrace();
				}
			}
		};
		
		seasonAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_O);
		seasonAction.putValue(Action.SMALL_ICON,Tools.getIcon("open16"));
		seasonAction.putValue(Action.LONG_DESCRIPTION,"Open the currently displayed season");
		seasonAction.putValue(Action.SHORT_DESCRIPTION,"Open season");
		
		editAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
		editAction.putValue(Action.SMALL_ICON,Tools.getIcon("edit16"));
		editAction.putValue(Action.LONG_DESCRIPTION,"Edit this member");
		editAction.putValue(Action.SHORT_DESCRIPTION,"Edit member");
		
		emailAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_M);
		emailAction.putValue(Action.SMALL_ICON,Tools.getIcon("email16"));
		emailAction.putValue(Action.LONG_DESCRIPTION,"Email this member");
		emailAction.putValue(Action.SHORT_DESCRIPTION,"Email member");
		
		copyAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		copyAction.putValue(Action.SMALL_ICON,Tools.getIcon("copy16"));
		copyAction.putValue(Action.LONG_DESCRIPTION,"Copy this member to the clipboard");
		copyAction.putValue(Action.SHORT_DESCRIPTION,"Copy member");
		
		if (!Desktop.isDesktopSupported()) {
			emailAction.setEnabled(false);
		}
	}
	
	private void a2Components() {
		setMinimumSize(new Dimension(450,275));
		setPreferredSize(new Dimension(750,500));
		setTransferHandler(new DataTransferHandler());
		
		northPanel = new GradientPanel(member.getFullName(),Tools.getHeaderIcon("member"));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		
		infoPanel = new JPanel();
		leftPanel = new JPanel();
		propertiesPanel = new JPanel();
		propertiesPanel.setBorder(OptionsDialog.createTitledBorder("Properties"));
		propertiesPanel.setPreferredSize(new Dimension(225, 300));
		propertiesPane = new JTextPane();
		propertiesPane.setEditable(false);
		propertiesPane.setOpaque(true);
		propertiesScrollPane = new JScrollPane(propertiesPane);
		linksPanel = new JPanel();
		editButton = new JButton(editAction);
		emailButton = new JButton(emailAction);
		copyButton = new JButton(copyAction);
		centerPanel = new JPanel();
		centerPanel.setBorder(OptionsDialog.createTitledBorder("Lifetime Statistics"));
		eventTypePanel = new JPanel();
		eventTypesLabel = new JLabel("Event Types");
		eventTypesLabel.setFont(new Font("Sans-serif",Font.BOLD,16));
		eventTypesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		eventTypesChart = new PieChart();
		locationsPanel = new JPanel();
		locationsLabel = new JLabel("Locations");
		locationsLabel.setFont(new Font("Sans-serif",Font.BOLD,16));
		locationsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		locationsChart = new PieChart();
		
		historyPanel = new JPanel();
		seasonPanel = new JPanel();
		seasonLabel = new JLabel("Select Season: ");
		comboBoxModel = new MemberSeasonsComboBoxModel(member); 
		seasonBox = new JComboBox(comboBoxModel);
		seasonBox.setRenderer(new ListRenderer());
		seasonButton = new JButton(seasonAction);
		historyChart = new HistoryChart(member);
		
		Tools.applyTextPopup(propertiesPane);
		Tools.applyScrollPopup(propertiesScrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(propertiesScrollPane.getVerticalScrollBar());
	}
	
	private void a3Listeners() {
		dataListener = new DataListener() {
			public void eventOccured(SourceEvent sourceEvent) {
				if (sourceEvent.isField()) {
					SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					if (fieldEvent.getField() == DataField.CURRENT_SEASON && fieldEvent.getOwner() == DataManager.getDataManager()) {
						updateProperties();
					} else if (fieldEvent.getOwner().equals(member)) {
						if (fieldEvent.getField() == DataField.FIRST_NAME || fieldEvent.getField() == DataField.LAST_NAME || fieldEvent.getField() == DataField.NICK_NAME) {
							setTitle("Member - " + member.getFullName());
							northPanel.setTitle(member.getFullName());
						}
						updateProperties();
					} else if (fieldEvent.getField() == DataField.POINT_VALUE && ((PepBandEvent) fieldEvent.getOwner()).getMembers().contains(member)) {
						updateProperties();
					} else if (fieldEvent.getField() == DataField.EVENT_TYPE && ((PepBandEvent) fieldEvent.getOwner()).getMembers().contains(member)) {
						updateEventTypeChart();
					} else if (fieldEvent.getField() == DataField.LOCATION && ((PepBandEvent) fieldEvent.getOwner()).getMembers().contains(member)) {
						updateLocationChart();
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (listEvent.getOwner() instanceof PepBandEvent && listEvent.containsElement(member) && (listEvent.getType() == SourceEvent.List.ADD || listEvent.getType() == SourceEvent.List.REMOVE)) {
						updateCharts();
					} else if (listEvent.getElement() instanceof PepBandEvent && listEvent.getType() == SourceEvent.List.REMOVE) {
						updateProperties();
						updateCharts();
					}
				}
			}
		};
		DataManager.getDataManager().addDataListener(dataListener);
		seasonBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() == seasonBox && e.getStateChange() == ItemEvent.SELECTED) {
					historyChart.setSeason((Season)seasonBox.getSelectedItem());
				}
			}
		});
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout(0,0));
		infoPanel.setLayout(new BorderLayout(0,0));
		leftPanel.setLayout(new BorderLayout(0,0));
		centerPanel.setLayout(new GridLayout(1,2,0,0));
		propertiesPanel.setLayout(new BorderLayout(0,0));
		linksPanel.setLayout(new GridBagLayout());
		eventTypePanel.setLayout(new BorderLayout(0,0));
		locationsPanel.setLayout(new BorderLayout(0,0));
		seasonPanel.setLayout(new GridBagLayout());
		historyPanel.setLayout(new BorderLayout());
		
		propertiesPanel.add(propertiesScrollPane,BorderLayout.CENTER);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 0;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		c.gridx = 0; c.gridy = 0;
		linksPanel.add(editButton,c);
		c.gridx = 0; c.gridy = 1;
		linksPanel.add(emailButton,c);
		c.gridx = 0; c.gridy = 2;
		linksPanel.add(copyButton,c);
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 3;
		linksPanel.add(Box.createVerticalGlue(),c);
		
		leftPanel.add(propertiesPanel,BorderLayout.CENTER);
		leftPanel.add(linksPanel,BorderLayout.SOUTH);
		
		eventTypePanel.add(eventTypesLabel,BorderLayout.NORTH);
		eventTypePanel.add(eventTypesChart,BorderLayout.CENTER);
		
		locationsPanel.add(locationsLabel,BorderLayout.NORTH);
		locationsPanel.add(locationsChart,BorderLayout.CENTER);
		
		centerPanel.add(eventTypePanel);
		centerPanel.add(locationsPanel);
		
		infoPanel.add(leftPanel,BorderLayout.WEST);
		infoPanel.add(centerPanel,BorderLayout.CENTER);
		
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 0.5; c.weighty = 1;
		c.gridx = 0; c.gridy = 0;
		seasonPanel.add(Box.createHorizontalGlue(),c);
		c.weightx = 0; c.weighty = 1;
		c.gridx = 1; c.gridy = 0;
		seasonPanel.add(seasonLabel,c);
		c.gridx = 2; c.gridy = 0;
		seasonPanel.add(seasonBox,c);
		c.gridx = 3; c.gridy = 0;
		seasonPanel.add(seasonButton,c);
		c.weightx = 0.5; c.weighty = 1;
		c.gridx = 4; c.gridy = 0;
		seasonPanel.add(Box.createHorizontalGlue(),c);
		
		historyPanel.add(seasonPanel,BorderLayout.NORTH);
		historyPanel.add(historyChart,BorderLayout.CENTER);
		
		tabbedPane.addTab(" Information ",Tools.getIcon("memberinfo32"),infoPanel,"Display information about the member");
		tabbedPane.addTab(" History ",Tools.getIcon("history32"),historyPanel,"Display the member's event and point history");
		
		add(northPanel,BorderLayout.NORTH);
		add(tabbedPane,BorderLayout.CENTER);
	}
	
	private void a5Initialize() {
		initializeStyles();
		updateCharts();
		updateProperties();
		historyChart.setSeason((Season)seasonBox.getSelectedItem());
		
		propertiesPane.putClientProperty("tip","An overview of the member's properties");
		seasonBox.putClientProperty("tip","Select which season to display in the history chart");
		
		setIconSize(Tools.getInteger("Misc Icon Size",32));
		setShowIconText(Tools.getBoolean("Show Misc Icon Text",true));
	}
	
	protected void closingOperations() {
		eventTypesChart.uninstall();
		locationsChart.uninstall();
		historyChart.uninstall();
		comboBoxModel.uninstall();
		DataManager.getDataManager().removeDataListener(dataListener);
		super.closingOperations();
	}
	
	public Season getHistorySeason() {
		return (Season)seasonBox.getSelectedItem();
	}
	
	public Member getMember() {
		return member;
	}
	
	public boolean isViewingHistory() {
		return tabbedPane.getSelectedComponent() == historyPanel;
	}
	
	public boolean isViewingInformation() {
		return tabbedPane.getSelectedComponent() == infoPanel;
	}
	
	private void initializeStyles() {
		TabStop[] stops = {new TabStop(100),new TabStop(200),new TabStop(300),new TabStop(400)};
		TabSet set = new TabSet(stops);
		
		StyleContext styleContext = new StyleContext();
		Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		
		Style regular = styleContext.addStyle(STYLES[0], defaultStyle);
		StyleConstants.setFontFamily(regular, "SansSerif");
		StyleConstants.setBold(regular, false);
		StyleConstants.setItalic(regular, false);
		StyleConstants.setTabSet(regular, set);
		StyleConstants.setFontSize(regular, 12);
		
		Style newStyle = styleContext.addStyle(STYLES[1], regular);
		StyleConstants.setFontSize(newStyle, 14);
		StyleConstants.setBold(newStyle, true);
		
		propertiesDocument = new DefaultStyledDocument(styleContext);
		propertiesPane.setStyledDocument(propertiesDocument);
		propertiesPane.setParagraphAttributes(regular, false);
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		
	}
	
	public void setShowIconText(boolean value) {
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(infoPanel),value ? " Information " : null);
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(historyPanel),value ? " History " : null);
	}
	
	public void setIconSize(Integer value) {
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(infoPanel),Tools.getIcon("memberinfo" + value +""));
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(historyPanel),Tools.getIcon("history" + value +""));
	}
	
	private void updateCharts() {
		TreeMap<EventType,Integer> eventTypeMap = new TreeMap<EventType,Integer>();
		TreeMap<Location,Integer> locationMap = new TreeMap<Location,Integer>();
		for(Season season : member.getSeasons()) {
			for (PepBandEvent event : member.getEvents(season)) {
				if (event.getEventType().getHasLocation()) {
					if (eventTypeMap.containsKey(event.getEventType())) {
						eventTypeMap.put(event.getEventType(),new Integer(eventTypeMap.get(event.getEventType()) + 1));
					} else {
						eventTypeMap.put(event.getEventType(),new Integer(1));
					}
					if (locationMap.containsKey(event.getLocation())) {
						locationMap.put(event.getLocation(),new Integer(locationMap.get(event.getLocation()) + 1));
					} else {
						locationMap.put(event.getLocation(),new Integer(1));
					}
				}
			}
		}
		eventTypesChart.setData(DataField.EVENT_TYPE,eventTypeMap);
		locationsChart.setData(DataField.LOCATION,locationMap);
	}
	
	private void updateEventTypeChart() {
		TreeMap<EventType,Integer> eventTypeMap = new TreeMap<EventType,Integer>();
		for(Season season : member.getSeasons()) {
			for (PepBandEvent event : member.getEvents(season)) {
				if (event.getEventType().getHasLocation()) {
					if (eventTypeMap.containsKey(event.getEventType())) {
						eventTypeMap.put(event.getEventType(),new Integer(eventTypeMap.get(event.getEventType()) + 1));
					} else {
						eventTypeMap.put(event.getEventType(),new Integer(1));
					}
				}
			}
		}
		eventTypesChart.setData(DataField.EVENT_TYPE,eventTypeMap);
	}
	
	private void updateLocationChart() {
		TreeMap<Location,Integer> locationMap = new TreeMap<Location,Integer>();
		for(Season season : member.getSeasons()) {
			for (PepBandEvent event : member.getEvents(season)) {
				if (event.getEventType().getHasLocation()) {
					if (locationMap.containsKey(event.getLocation())) {
						locationMap.put(event.getLocation(),new Integer(locationMap.get(event.getLocation()) + 1));
					} else {
						locationMap.put(event.getLocation(),new Integer(1));
					}
				}
			}
		}
		locationsChart.setData(DataField.LOCATION,locationMap);
	}
	
	private void updateProperties() {
		try {
			propertiesDocument.remove(0,propertiesDocument.getLength());
			
			propertiesDocument.insertString(propertiesDocument.getLength(), "First Name: ", propertiesDocument.getStyle(STYLES[0]));
			propertiesDocument.insertString(propertiesDocument.getLength(), "\t" + member.getFirstName() + "\n", propertiesDocument.getStyle(STYLES[1]));
			
			propertiesDocument.insertString(propertiesDocument.getLength(), "Middle Name: ", propertiesDocument.getStyle(STYLES[0]));
			propertiesDocument.insertString(propertiesDocument.getLength(), "\t" + member.getMiddleName() + "\n", propertiesDocument.getStyle(STYLES[1]));
			
			propertiesDocument.insertString(propertiesDocument.getLength(), "Last Name: ", propertiesDocument.getStyle(STYLES[0]));
			propertiesDocument.insertString(propertiesDocument.getLength(), "\t" + member.getLastName() + "\n", propertiesDocument.getStyle(STYLES[1]));
			
			propertiesDocument.insertString(propertiesDocument.getLength(), "Nickname: ", propertiesDocument.getStyle(STYLES[0]));
			propertiesDocument.insertString(propertiesDocument.getLength(), "\t" + member.getNickName() + "\n", propertiesDocument.getStyle(STYLES[1]));
			
			propertiesDocument.insertString(propertiesDocument.getLength(), "Net ID: ", propertiesDocument.getStyle(STYLES[0]));
			propertiesDocument.insertString(propertiesDocument.getLength(), "\t" + member.getNetID() + "\n", propertiesDocument.getStyle(STYLES[1]));
			
			propertiesDocument.insertString(propertiesDocument.getLength(), "Class Year: ", propertiesDocument.getStyle(STYLES[0]));
			propertiesDocument.insertString(propertiesDocument.getLength(), "\t" + member.getClassYear().toString() + "\n", propertiesDocument.getStyle(STYLES[1]));
			
			propertiesDocument.insertString(propertiesDocument.getLength(), "Instrument: ", propertiesDocument.getStyle(STYLES[0]));
			propertiesDocument.insertString(propertiesDocument.getLength(), "\t" + member.getInstrument().getName() + "\n", propertiesDocument.getStyle(STYLES[1]));
			
			propertiesDocument.insertString(propertiesDocument.getLength(), "Sex: ", propertiesDocument.getStyle(STYLES[0]));
			propertiesDocument.insertString(propertiesDocument.getLength(), "\t" + member.getSex().getName() + "\n", propertiesDocument.getStyle(STYLES[1]));
			
			propertiesDocument.insertString(propertiesDocument.getLength(), "Current Points: ", propertiesDocument.getStyle(STYLES[0]));
			propertiesDocument.insertString(propertiesDocument.getLength(), "\t" + member.getPoints(DataManager.getDataManager().getCurrentSeason()).toString() + "\n", propertiesDocument.getStyle(STYLES[1]));
			
			propertiesDocument.insertString(propertiesDocument.getLength(), "Lifetime Points: ", propertiesDocument.getStyle(STYLES[0]));
			propertiesDocument.insertString(propertiesDocument.getLength(), "\t" + member.getLifeTimePoints().toString(), propertiesDocument.getStyle(STYLES[1]));
		} catch (Exception exc) {
			System.err.println("Member Window could not update member properties text");
			exc.printStackTrace();
		}
	}
	
	public void viewInfo() {
		tabbedPane.setSelectedComponent(infoPanel);
	}
	
	public void viewHistory() {
		tabbedPane.setSelectedComponent(historyPanel);
	}
}