package pepband3.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import pepband3.gui.extra.*;

public class AboutDialog extends JDialog {
	
	private static final Color ABOUT_COLOR = new Color(181,24,24);
	private static final String[] STYLES = {"Regular","Bold","Red","Italic","Red Small","Hyperlink","MiniSplash"};
	
	private JPanel northPanel;
	private JLabel northLabel;
	private JTabbedPane tabbedPane;
		private JScrollPane infoScrollPane;
			private JTextPane infoTextPane;
			private StyledDocument infoDocument;
		private JScrollPane creditScrollPane;
			private JTextPane creditTextPane;
			private StyledDocument creditDocument;
		private JScrollPane historyScrollPane;
			private JTextPane historyTextPane;
			private StyledDocument historyDocument;
		
		private Style heumannStyle;
		private Style endrissStyle;
		private Style hugelStyle;
		private Style caulfieldStyle;
		private Style itextStyle;
		private Style xstreamStyle;
		private Style xpp3Style;
	
	public AboutDialog() {
		super(Tools.getProgramRoot(), "About Pep Band Manager Suite", true);
		
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
		
	}
	
	private void a2Components() {
		setPreferredSize(new Dimension(600, 425));
		
		northPanel = new JPanel();
		northPanel.setOpaque(true);
		northPanel.setBackground(ABOUT_COLOR);
		northLabel = new JLabel(Tools.getHeaderIcon("about"));
		northLabel.setHorizontalAlignment(SwingConstants.CENTER);
		northLabel.setOpaque(false);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		
		infoTextPane = new JTextPane();
		infoTextPane.setOpaque(true);
		infoTextPane.setEditable(false);
		infoScrollPane = new JScrollPane(infoTextPane);
		
		creditTextPane = new JTextPane();
		creditTextPane.setOpaque(true);
		creditTextPane.setEditable(false);
		creditScrollPane = new JScrollPane(creditTextPane);
		
		historyTextPane = new JTextPane();
		historyTextPane.setOpaque(true);
		historyTextPane.setEditable(false);
		historyScrollPane = new JScrollPane(historyTextPane);
		
		Tools.applyTextPopup(infoTextPane);
		Tools.applyScrollPopup(infoScrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(infoScrollPane.getVerticalScrollBar());
		
		Tools.applyTextPopup(creditTextPane);
		Tools.applyScrollPopup(creditScrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(creditScrollPane.getVerticalScrollBar());
		
		Tools.applyTextPopup(historyTextPane);
		Tools.applyScrollPopup(historyScrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(historyScrollPane.getVerticalScrollBar());
	}
	
	private void a3Listeners() {
		MouseAdapter mouseAdapter = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getComponent() == creditTextPane) {
					Object value = getValueAt(new Point(e.getX(), e.getY()));
					if (value != null) {
						if (value instanceof URI) {
							try {
								Desktop desktop = Desktop.getDesktop();
								desktop.mail((URI) value);
							} catch (Exception exc) {
								exc.printStackTrace();
							}
						} else if (value instanceof URL) {
							try {
								Desktop desktop = Desktop.getDesktop();
								desktop.browse(((URL) value).toURI());
							} catch (Exception exc) {
								exc.printStackTrace();
							}
						}
					}
				} else if (e.getComponent() == northLabel) {
					try {
						Desktop desktop = Desktop.getDesktop();
						desktop.browse(new URI("http://pepband.bigredbands.org/"));
					} catch (Exception exc) {
						exc.printStackTrace();
					}
				}
			}
			
			public void mouseExited(MouseEvent e) {
				if (e.getComponent() == creditTextPane) {
					creditTextPane.setCursor(Cursor.getDefaultCursor());
				} else if (e.getComponent() == northLabel) {
					northLabel.setCursor(Cursor.getDefaultCursor());
				}
			}
			
			public void mouseMoved(MouseEvent e) {
				if (e.getComponent() == creditTextPane) {
					Object value = getValueAt(new Point(e.getX(), e.getY()));
					if (value != null) {
						creditTextPane.setCursor(Tools.getCursor("hand"));
					} else {
						creditTextPane.setCursor(Cursor.getDefaultCursor());
					}
				} else if (e.getComponent() == northLabel) {
					northLabel.setCursor(Tools.getCursor("hand"));
				}
			}
			
			public Object getValueAt(Point clickPoint) {
				int position = creditTextPane.viewToModel(clickPoint);
				if (position >= 0 && position < creditDocument.getLength()) {
					Element element = creditDocument.getCharacterElement(position);
					Object value = element.getAttributes().getAttribute(HTML.Attribute.HREF);
					return value;
				} else {
					return null;
				}
			}
		};
		northLabel.addMouseMotionListener(mouseAdapter);
		northLabel.addMouseListener(mouseAdapter);
		creditTextPane.addMouseMotionListener(mouseAdapter);
		creditTextPane.addMouseListener(mouseAdapter);
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout(0,0));
		northPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(0,0,0,0);
		
		c.gridx = 0; c.gridy = 0;
		northPanel.add(northLabel, c);
		
		tabbedPane.addTab(" Program ", null, infoScrollPane, "Information about the program");
		tabbedPane.addTab(" Credits ", null, creditScrollPane, "Credits for contributors to the program");
		tabbedPane.addTab(" History ", null, historyScrollPane, "History of the program");
		
		add(northPanel, BorderLayout.NORTH);
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	private void a5Initialize() {
		initializeStyles();
		initializeText();
		infoTextPane.setCaretPosition(0);
		infoScrollPane.getViewport().setViewPosition(new Point(0, 0));
	}
	
	public void display() {
		pack();
		setLocationRelativeTo(getOwner());
		setMinimumSize(getSize());
		setVisible(true);
	}
	
	private void initializeStyles() {
		String familyName = Tools.getFont("Lucida Sans Demi Bold.ttf").getFamily();
		StyleContext styleContext = new StyleContext();
		Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		
		Style regular = styleContext.addStyle(STYLES[0], defaultStyle);
		StyleConstants.setBold(regular, false);
		StyleConstants.setItalic(regular, false);
		StyleConstants.setAlignment(regular, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontFamily(regular, familyName);
		StyleConstants.setFontSize(regular, 14);
		
		Style newStyle = styleContext.addStyle(STYLES[1], regular);
		StyleConstants.setFontFamily(newStyle, familyName);
		StyleConstants.setFontSize(newStyle, 14);
		StyleConstants.setBold(newStyle, true);
		
		newStyle = styleContext.addStyle(STYLES[2], regular);
		StyleConstants.setFontFamily(newStyle, familyName);
		StyleConstants.setFontSize(newStyle, 24);
		StyleConstants.setBold(newStyle, true);
		StyleConstants.setForeground(newStyle, ABOUT_COLOR);
		
		newStyle = styleContext.addStyle(STYLES[3], regular);
		StyleConstants.setFontFamily(newStyle, "Sans-Serif");
		StyleConstants.setFontSize(newStyle, 14);
		StyleConstants.setItalic(newStyle, true);
		StyleConstants.setBold(newStyle, false);
		
		newStyle = styleContext.addStyle(STYLES[4], regular);
		StyleConstants.setFontFamily(newStyle, familyName);
		StyleConstants.setFontSize(newStyle, 16);
		StyleConstants.setBold(newStyle, true);
		StyleConstants.setForeground(newStyle, ABOUT_COLOR);
		
		Style hyperlinkStyle = styleContext.addStyle(STYLES[5], regular);
		StyleConstants.setFontFamily(hyperlinkStyle, familyName);
		StyleConstants.setFontSize(hyperlinkStyle, 14);
		StyleConstants.setBold(hyperlinkStyle, true);
		StyleConstants.setUnderline(hyperlinkStyle, true);
		StyleConstants.setForeground(hyperlinkStyle, Color.BLUE);
		
		newStyle = styleContext.addStyle(STYLES[6], regular);
		StyleConstants.setIcon(newStyle, Tools.getHeaderIcon("minisplash"));
		
		heumannStyle = styleContext.addStyle("Heumann", hyperlinkStyle);
		endrissStyle = styleContext.addStyle("Endriss", hyperlinkStyle);
		hugelStyle = styleContext.addStyle("Hugel", hyperlinkStyle);
		caulfieldStyle = styleContext.addStyle("Caulfield", hyperlinkStyle);
		itextStyle = styleContext.addStyle("iText", hyperlinkStyle);
		xstreamStyle = styleContext.addStyle("XStream", hyperlinkStyle);
		xpp3Style = styleContext.addStyle("XPP3", hyperlinkStyle);
		
		try {
			heumannStyle.addAttribute(HTML.Attribute.HREF, new URI("mailto:" + URITools.replaceEscapeCharacters("Eric M. Heumann<emh39@cornell.edu>")));
			endrissStyle.addAttribute(HTML.Attribute.HREF, new URI("mailto:" + URITools.replaceEscapeCharacters("Jason Endriss<jre29@cornell.edu>")));
			hugelStyle.addAttribute(HTML.Attribute.HREF, new URI("mailto:" + URITools.replaceEscapeCharacters("Erik Hugel<elh38@cornell.edu>")));
			caulfieldStyle.addAttribute(HTML.Attribute.HREF, new URI("mailto:" + URITools.replaceEscapeCharacters("Matthew Caulfield<mfc28@cornell.edu>")));
			itextStyle.addAttribute(HTML.Attribute.HREF, new URL("http://www.lowagie.com/iText/"));
			xstreamStyle.addAttribute(HTML.Attribute.HREF, new URL("http://xstream.codehaus.org/index.html"));
			xpp3Style.addAttribute(HTML.Attribute.HREF, new URL("http://www.extreme.indiana.edu/xgws/xsoap/xpp/mxp1/index.html"));
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		infoDocument = new DefaultStyledDocument(styleContext);
		infoTextPane.setStyledDocument(infoDocument);
		infoTextPane.setParagraphAttributes(regular, false);
		
		creditDocument = new DefaultStyledDocument(styleContext);
		creditTextPane.setStyledDocument(creditDocument);
		creditTextPane.setParagraphAttributes(regular, false);
		
		historyDocument = new DefaultStyledDocument(styleContext);
		historyTextPane.setStyledDocument(historyDocument);
		historyTextPane.setParagraphAttributes(regular, false);
	}
	
	private void initializeText() {
		String text;
		try {
			// INFO //
			infoDocument.remove(0, infoDocument.getLength());
			
			text = "Pep Band Manager Suite" + "\n";
			infoDocument.insertString(infoDocument.getLength(), text, infoDocument.getStyle(STYLES[2]));
			
			text = "Version 2.0.4" + "\n\n";
			infoDocument.insertString(infoDocument.getLength(), text, infoDocument.getStyle(STYLES[4]));
			
			text = " ";
			infoDocument.insertString(infoDocument.getLength(), text, infoDocument.getStyle(STYLES[6]));
			
			//text = "\n\nCopyleft 2009" + "\n";
			text = "\n\nSpring 2009" + "\n";
			infoDocument.insertString(infoDocument.getLength(), text, infoDocument.getStyle(STYLES[1]));
			
			/* NOT AN ACTUAL LICENSE... DOESN'T MENTION XSTREAM, XPP3, OR iTEXT
			text = "This program is free, open source software. Anyone may use, modify, or redistribute this program without permission and may do so for any purpose. " +
				"Any program that is in any way a descendant of this program is also expected to offer the aforementioned rights. To obtain a copy of the source code, contact the creator.";
			infoDocument.insertString(infoDocument.getLength(), text, infoDocument.getStyle(STYLES[0]));
			*/
			
			// CREDITS //
			creditDocument.remove(0, creditDocument.getLength());
			
			text = "Creator: " + "\n";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[3]));
			
			text = " ";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = "Eric M. Heumann '08";
			creditDocument.insertString(creditDocument.getLength(), text, heumannStyle);
			
			text = ", CEE \n\n";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = "Consultants: " + "\n";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[3]));
			
			text = " ";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = "Jason Endriss '08";
			creditDocument.insertString(creditDocument.getLength(), text, endrissStyle);
			
			text = ", Manager \n";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = " ";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = "Erik Hugel '09";
			creditDocument.insertString(creditDocument.getLength(), text, hugelStyle);
			
			text = ", Manager \n";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = " ";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = "Matthew Caulfield '08";
			creditDocument.insertString(creditDocument.getLength(), text, caulfieldStyle);
			
			text = ", ECE \n\n";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = "External Libraries: " + "\n";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[3]));
			
			text = " ";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = "iText 2.1.4";
			creditDocument.insertString(creditDocument.getLength(), text, itextStyle);
			
			text = ", Adobe PDF Exporting \n";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = " ";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = "XStream 1.3.1";
			creditDocument.insertString(creditDocument.getLength(), text, xstreamStyle);
			
			text = ", Data Marshalling \n";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = " ";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = "XPP3 Min 1.1.4c";
			creditDocument.insertString(creditDocument.getLength(), text, xpp3Style);
			
			text = ", XML Parsing \n\n";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			text = "Written in Java";
			creditDocument.insertString(creditDocument.getLength(), text, creditDocument.getStyle(STYLES[1]));
			
			// HISTORY //
			historyDocument.remove(0, historyDocument.getLength());
			
			text = "Version 1.2 - Spring 2007" + "\n";
			historyDocument.insertString(historyDocument.getLength(), text, historyDocument.getStyle(STYLES[1]));
			
			text = "Written for Manager Jason Endriss '08 during his first term." + "\n" + 
				"Served to expediate entering members into spreadsheets." + "\n\n";
			historyDocument.insertString(historyDocument.getLength(), text, historyDocument.getStyle(STYLES[0]));
			
			text = "Version 1.5 - Fall 2007" + "\n";
			historyDocument.insertString(historyDocument.getLength(), text, historyDocument.getStyle(STYLES[1]));
			
			text = "Added functionality and improved appearance of previous version." + "\n\n";
			historyDocument.insertString(historyDocument.getLength(), text, historyDocument.getStyle(STYLES[0]));
			
			text = "Version 2.0 - Winter 2008/2009" + "\n";
			historyDocument.insertString(historyDocument.getLength(), text, historyDocument.getStyle(STYLES[1]));
			
			text = "Written upon request of Manager Erik Hugel '09 for Manager Miranda Reid '10." + "\n" +
				"Improved appearance, added functionality, and switched to XML data storage." + "\n\n";
			historyDocument.insertString(historyDocument.getLength(), text, historyDocument.getStyle(STYLES[0]));
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}