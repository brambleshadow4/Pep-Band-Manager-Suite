package pepband3.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

import pepband3.data.*;

public class LoadMessageWindow extends InternalWindow {
	
	private static final String[] STYLES = {"Regular","Bold","Critical","Good"};
	
	private JScrollPane scrollPane;
	private JTextPane textPane;
		private StyledDocument document;	
	
	public LoadMessageWindow() {
		super("Maintenance Results", true, true, true, true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setFrameIcon(Tools.getIcon("info16"));
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		
	}
	
	private void a2Components() {
		setPreferredSize(new Dimension(600,550));
		setMinimumSize(new Dimension(600,550));
		textPane = new JTextPane();
		textPane.setOpaque(true);
		textPane.setEditable(false);
		scrollPane = new JScrollPane(textPane);
		
		Tools.applyTextPopup(textPane);
		Tools.applyScrollPopup(scrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(scrollPane.getVerticalScrollBar());
	}
	
	private void a3Listeners() {
		
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout());
		
		add(scrollPane, BorderLayout.CENTER);
	}
	
	private void a5Initialize() {
		initializeStyles();
		setPrimaryMessage();
		setSecondaryMessage();
		textPane.setCaretPosition(0);
		scrollPane.getViewport().setViewPosition(new Point(0,0));
	}
	
	public void display() {
		super.display();
		setLocation((Tools.getDesktopPane().getWidth() - getWidth()) / 2, (Tools.getDesktopPane().getHeight() - getHeight()) / 2);
	}
	
	private void initializeStyles() {
		StyleContext styleContext = new StyleContext();
		Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		
		Style regular = styleContext.addStyle(STYLES[0], defaultStyle);
		StyleConstants.setFontFamily(regular, "SansSerif");
		StyleConstants.setBold(regular, false);
		StyleConstants.setItalic(regular, false);
		StyleConstants.setFontSize(regular, 14);
		
		Style newStyle = styleContext.addStyle(STYLES[1], regular);
		StyleConstants.setFontSize(newStyle, 14);
		StyleConstants.setBold(newStyle, true);
		
		newStyle = styleContext.addStyle(STYLES[2], regular);
		StyleConstants.setFontSize(newStyle, 14);
		StyleConstants.setBold(newStyle, true);
		StyleConstants.setForeground(newStyle, Color.RED);
		
		newStyle = styleContext.addStyle(STYLES[3], regular);
		StyleConstants.setFontSize(newStyle, 14);
		StyleConstants.setBold(newStyle, true);
		StyleConstants.setForeground(newStyle, new Color(0,125,0));
		
		document = new DefaultStyledDocument(styleContext);
		textPane.setStyledDocument(document);
	}
	
	private void setPrimaryMessage() {
		int results = DataManager.getDataManager().getMaintenanceResults();
		if (results == DataManager.GOOD) {
			try {
				document.remove(0,document.getLength());
				String text = "ALL PRIMARY DATA MAINTENANCE CHECKS PASSED. DATA IS GOOD." + "\n\n";
				document.insertString(document.getLength(), text, document.getStyle(STYLES[3]));
			} catch (Exception exc) {
				exc.printStackTrace();
				textPane.setText("setMessage Exception");
			}
		} else {
			ArrayList<Integer> messages = new ArrayList<Integer>();
			for (int power = 12; power >= 0; power--) {
				if (results - (int) Math.pow(2, power) >= 0) {
					results -= (int) Math.pow(2, power);
					messages.add((int) Math.pow(2, power));
				}
			}
			try {
				document.remove(0,document.getLength());
				String text = "THE FOLLOWING PRIMARY MAINTENANCE WAS PERFORMED ON YOUR DATABASE:" + "\n\n";
				document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
				
				if (messages.contains(DataManager.SEASONS_INIT)) {
					text = "Seasons Initialized" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[2]));
					text = "No seasons were found in the database. This could be due to a first time running of the program, or because the Pep Band Data.xml file on your computer was corrupted, relocated, renamed, or deleted. If a corrupted version was found, an attempt was made to back it up under the name Corrupted Pep Band Data. If an error message appeared indicating that this attempt failed, make a backup of the data file NOW before exiting the program! Please send the corrupt file to Eric Heumann (emh39) or someone who understands the architecture of the file to attempt a recovery of the data. Since at least one season must exist, a single new season has been created using a starting year of this year and a starting date of today." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
				if (messages.contains(DataManager.CURRENT_SEASON_SET)) {
					text = "Current Season Reset" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
					text = "If a flag indicating the seasons were initialized is also present, this indicates that the current season had been set to a detached season and is now set to the newly created season. If not, this indicates that seasons existed, but none of them bore the title of current season. The current season was set to the season with the latest starting year." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
				if (messages.contains(DataManager.IDS_INIT)) {
					text = "ID Tracker Initialized" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
					text = "The data manager assigns an ID tag to each piece of data. When a new piece of data, such as an event, member, or instrument, is created, the data manager uses a stored integer to assign a new ID number, and then increments it by one. This next available ID value was found to be -1, indicating it had never been used. It has now been set to zero, the lowest allowed ID." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
				if (messages.contains(DataManager.IDS_RESET)) {
					text = "Data ID Values Reset" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
					text = "If a flag indicating that the ID tracker was initialized is also present, this indicates that the ID tracker was not initialized, but data existed with ID values. If not, this indicates that a piece of data existed with an ID value larger than the ID tracker's next available ID. To prevent two pieces of data from having the same ID, the tracker was reset, all data was reassigned a new ID value, and the next new piece of data is assured to be assigned an ID value larger than any existing ID." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
				if (messages.contains(DataManager.DEFAULT_INSTRUMENT_INSTALL)) {
					text = "Default Instruments were installed" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
					text = "No instruments were found in the database, so a fresh install of the default instruments was made." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
				if (messages.contains(DataManager.DEFAULT_EVENT_TYPE_INSTALL)) {
					text = "Default Event Types were Installed" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
					text = "No event types were found in the database, so a fresh install of the default event types was made." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
				if (messages.contains(DataManager.DEFAULT_LOCATION_INSTALL)) {
					text = "Default Locations were Installed" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
					text = "No locations were found in the database, so a fresh install of the default locations was made." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
				if (messages.contains(DataManager.DETACHED_INSTRUMENT_ATTACHED)) {
					text = "Detached Instruments were Reattached" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
					text = "Members were found with instruments that were not present in the database's list of instruments. The instruments have been added to the database." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
				if (messages.contains(DataManager.DETACHED_EVENT_TYPE_ATTACHED)) {
					text = "Detached Event Types were Reattached" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
					text = "Events were found with event types that were not present in the database's list of event types. The event types have been added to the database." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
				if (messages.contains(DataManager.DETACHED_LOCATION_ATTACHED)) {
					text = "Detached Locations were Reattached" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
					text = "Events were found with locations that were not present in the database's list of locations. The locations have been added to the database." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
				if (messages.contains(DataManager.DETACHED_MEMBERS_ATTACHED)) {
					text = "Detached Members were Reattached" + "\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
					text = "Members were found in events who were not present in the season roster of the season that contained the event. The members were added to the appropriate rosters." + "\n\n";
					document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
				}
			} catch (Exception exc) {
				exc.printStackTrace();
				textPane.setText("setMessage Exception");
			}
		}
	}
	
	private void setSecondaryMessage() {
		try {
			String text = "\n\n" + "SECONDARY MAINTENANCE REPORTS:" + "\n\n";
			document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
			
			TreeMap<String, LinkedList<Member>> duplicateMap = DataManager.getDataManager().getDuplicateNetIDs();
			
			if (!duplicateMap.isEmpty()) {
				text = "Duplicate Net IDs Found" + "\n";
				document.insertString(document.getLength(), text, document.getStyle(STYLES[1]));
				StringBuilder builder = new StringBuilder();
				builder.append("\n");
				for (String netID : duplicateMap.keySet()) {
					builder.append("Net ID: " + netID + ". Members: ");
					LinkedList<Member> members = duplicateMap.get(netID);
					for (int index = 0; index < members.size(); index++) {
						builder.append(members.get(index).getName());
						if (index != members.size() - 1) {
							builder.append(", ");
						} else {
							builder.append("\n");
						}
					}
				}
				builder.append("\n");
				builder.append("The list above shows the multiple members corresponding to each duplicate net ID. Please correct any incorrect net ID information. If a member has multiple instances (i.e. is listed as two or more separate members), please take the following action to ensure proper point totalling: open the different instances in member windows and identify which one has the most seasons and events associated with it. Keep this one. For all other instances, write down the seasons and events it's associated with, delete the member, and then go back through those seasons and events and add the one instance of the member that was kept.");
				builder.append("\n\n");
				document.insertString(document.getLength(), builder.toString(), document.getStyle(STYLES[0]));
			} else {
				text = "No Duplicate Net IDs Found" + "\n";
				document.insertString(document.getLength(), text, document.getStyle(STYLES[3]));
				text = "All members have unique Net IDs" + "\n\n";
				document.insertString(document.getLength(), text, document.getStyle(STYLES[0]));
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}