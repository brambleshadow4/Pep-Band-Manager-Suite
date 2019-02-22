package pepband3.gui.component.tab;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.*;
import pepband3.gui.extra.*;

public class OverviewTab extends BandTableTab {
	
	private static final String[] STYLES = {"Regular","Italic"};
	
	private DataListener dataListener;
	
	private JPanel instrumentationPanel;
		private JTextPane instrumentationPane;
		private StyledDocument instrumentationDocument;
	private JPanel linksPanel;
		private JButton seasonButton;
		private JButton statisticsButton;
	
	private RunnableAction seasonAction, statisticsAction;
	
	private Band band;
	
	public OverviewTab(Band paramBand) {
		band = paramBand;
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		seasonAction = new RunnableAction("Season Window","Opening band's season window") {
			public void act() {
				SeasonWindow window = Tools.getDesktopPane().addSeasonWindow(DataManager.getDataManager().getSeasonForBand(band));
				window.display();
				if (band instanceof PepBandEvent) {
					window.highlight((PepBandEvent)band);
				}
			}
		};
		statisticsAction = new RunnableAction("Statistics Window","Opening band's statisitcs window") {
			public void act() {
				Tools.getDesktopPane().addStatisticsWindow(band).display();
			}
		};
		
		seasonAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_S);
		seasonAction.putValue(Action.SMALL_ICON,Tools.getIcon("open16"));
		seasonAction.putValue(Action.LONG_DESCRIPTION,"Open the season window for the season that contains this event");
		seasonAction.putValue(Action.SHORT_DESCRIPTION,"Open season window");
		
		statisticsAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_T);
		statisticsAction.putValue(Action.SMALL_ICON,Tools.getIcon("statistics16"));
		statisticsAction.putValue(Action.LONG_DESCRIPTION,"Open a statistics window displaying data pertaining to this table of members");
		statisticsAction.putValue(Action.SHORT_DESCRIPTION,"Open statistics window");
	}
	
	private void a2Components() {
		instrumentationPanel = new JPanel();
		instrumentationPanel.setBorder(OptionsDialog.createTitledBorder("Instrumentation"));
		instrumentationPane = new JTextPane();
		instrumentationPane.setEditable(false);
		instrumentationPane.setOpaque(true);
		linksPanel = new JPanel();
		linksPanel.setBorder(OptionsDialog.createTitledBorder("Links"));
		seasonButton = new JButton(seasonAction);
		statisticsButton = new JButton(statisticsAction);
		
		Tools.applyTextPopup(instrumentationPane);
		
		if (band instanceof Season) {
			seasonButton.setVisible(false);
		}
	}
	
	private void a3Listeners() {
		dataListener = new DataListener() {
			public void eventOccured(SourceEvent sourceEvent) {
				if (sourceEvent.isField()) {
					SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
					if (fieldEvent.getField() == DataField.INSTRUMENT && fieldEvent.getOwner() instanceof Member && band.getMembers().contains((Member) fieldEvent.getOwner())) {
						updateSummary();
					} else if (fieldEvent.getField() == DataField.NAME && fieldEvent.getOwner() instanceof Instrument) {
						updateSummary();
					}
				} else if (sourceEvent.isList()) {
					SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
					if (listEvent.getOwner().equals(band) && listEvent.getElement() instanceof Member && (listEvent.getType() == SourceEvent.List.ADD || listEvent.getType() == SourceEvent.List.REMOVE)) {
						updateSummary();
					} else if (listEvent.getOwner().equals(DataManager.getDataManager()) && listEvent.getElement() instanceof Instrument && listEvent.getType() == SourceEvent.List.ORDER) {
						updateSummary();
					}
				}
			}
		};
		DataManager.getDataManager().addDataListener(dataListener);
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout());
		instrumentationPanel.setLayout(new BorderLayout());
		linksPanel.setLayout(new GridBagLayout());
		
		instrumentationPanel.add(instrumentationPane,BorderLayout.CENTER);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 0;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		c.gridx = 0; c.gridy = 0;
		linksPanel.add(seasonButton,c);
		c.gridx = 0; c.gridy = 1;
		linksPanel.add(statisticsButton,c);
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 2;
		linksPanel.add(Box.createVerticalGlue(),c);
		
		add(instrumentationPanel,BorderLayout.NORTH);
		add(linksPanel,BorderLayout.CENTER);
	}
	
	private void a5Initialize() {
		initializeStyles();
		instrumentationPane.putClientProperty("tip","Overview of the band's instrumentation");
		updateSummary();
	}
	
	public Integer getIndex() {
		return new Integer(3);
	}
	
	public String getTabIconName() {
		return "info";
	}
	
	public String getTabName() {
		return "Overview";
	}
	
	public String getToolTipText() {
		return "Overview Tab";
	}
	
	private void initializeStyles() {
		StyleContext styleContext = new StyleContext();
		Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		
		Style regular = styleContext.addStyle(STYLES[0], defaultStyle);
		StyleConstants.setFontSize(regular, 12);
		StyleConstants.setFontFamily(regular, "SansSerif");
		StyleConstants.setBold(regular, true);
		
		Style newStyle = styleContext.addStyle(STYLES[1], regular);
		StyleConstants.setFontSize(newStyle, 12);
		StyleConstants.setItalic(newStyle, true);
		
		instrumentationDocument = new DefaultStyledDocument(styleContext);
		instrumentationPane.setStyledDocument(instrumentationDocument);
	}
	
	public void uninstall() {
		DataManager.getDataManager().removeDataListener(dataListener);
	}
	
	public void updateSummary() {
		try {
			TreeMap<Instrument,Integer> instruments = new TreeMap<Instrument,Integer>();
			Integer mostPoints = new Integer(0);
			Integer lowestPoints = new Integer(0);
			Integer totalPoints = new Integer(0);
			for (Member member : band.getMembers()) {
				totalPoints += member.getPoints(band);
				if (member.getPoints(band) > mostPoints) {
					mostPoints = member.getPoints(band);
				}
				if (lowestPoints == 0 || member.getPoints(band) < lowestPoints) {
					lowestPoints = member.getPoints(band);
				}
				if (!instruments.containsKey(member.getInstrument())) {
					instruments.put(member.getInstrument(),new Integer(1));
				} else {
					instruments.put(member.getInstrument(),instruments.get(member.getInstrument()) + 1);
				}
			}
			instrumentationDocument.remove(0,instrumentationDocument.getLength());
			for (Instrument instrument : instruments.keySet()) {
				instrumentationDocument.insertString(instrumentationDocument.getLength(), instruments.get(instrument).toString(), instrumentationDocument.getStyle(STYLES[0]));
				instrumentationDocument.insertString(instrumentationDocument.getLength(), " " + instrument.getName() + "\n", instrumentationDocument.getStyle(STYLES[0]));
			}
			if (instruments.keySet().isEmpty()) {
				instrumentationDocument.insertString(instrumentationDocument.getLength(), band.size().toString(), instrumentationDocument.getStyle(STYLES[0]));
			} else {
				instrumentationDocument.insertString(instrumentationDocument.getLength(), "\n" + band.size().toString(), instrumentationDocument.getStyle(STYLES[0]));
			}
			instrumentationDocument.insertString(instrumentationDocument.getLength(), " " + "Total", instrumentationDocument.getStyle(STYLES[0]));
		} catch (Exception exc) {
			System.err.println("Overview Tab could not update overview text");
			exc.printStackTrace();
		}
	}
}