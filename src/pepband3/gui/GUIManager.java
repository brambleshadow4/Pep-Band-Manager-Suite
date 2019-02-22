package pepband3.gui;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import pepband3.*;

public class GUIManager {
	
	public static final int INS = 2;
	private static final GUIManager GUI_MANAGER = new GUIManager();
	private static final TreeMap<String,Cursor> CURSOR_MAP = new TreeMap<String,Cursor>();
	private static final TreeMap<String,Font> FONT_MAP = new TreeMap<String,Font>();
	private static final TreeMap<String,ImageIcon> ICON_MAP = new TreeMap<String,ImageIcon>();
	private static final TreeMap<String,ImageIcon> DESKTOP_ICON_MAP = new TreeMap<String,ImageIcon>();
	private static final TreeMap<String,ImageIcon> HEADER_ICON_MAP = new TreeMap<String,ImageIcon>();
	private static final TreeMap<String,ImageIcon> INSTRUMENT_ICON_MAP = new TreeMap<String,ImageIcon>();
	private static final TreeMap<String,ImageIcon> LOCATION_ICON_MAP = new TreeMap<String,ImageIcon>();
	private static final TreeMap<String,ImageIcon> EVENT_ICON_MAP = new TreeMap<String,ImageIcon>();
	private static final TreeMap<String,ImageIcon> WINDOW_ICON_MAP = new TreeMap<String,ImageIcon>();
	private static final TreeMap<String,BufferedImage> IMAGE_MAP = new TreeMap<String,BufferedImage>();
	private static final LinkedList<Component> ROOT_COMPONENT_LIST = new LinkedList<Component>();
	private static final Properties PROPERTIES = new Properties(initializeDefaultProperties());
	private static AppWindow programRoot;
	
	private GUIManager() {
		
	}
	
	public static GUIManager getGUIManager() {
		return GUI_MANAGER;
	}
	
	public TreeMap<String,Cursor> getCursorMap() {
		return CURSOR_MAP;
	}
	
	public TreeMap<String,Font> getFontMap() {
		return FONT_MAP;
	}
	
	public TreeMap<String,ImageIcon> getDesktopIconMap() {
		return DESKTOP_ICON_MAP;
	}
	
	public TreeMap<String,ImageIcon> getHeaderIconMap() {
		return HEADER_ICON_MAP;
	}
	
	public TreeMap<String,ImageIcon> getInstrumentIconMap() {
		return INSTRUMENT_ICON_MAP;
	}
	
	public TreeMap<String,ImageIcon> getIconMap() {
		return ICON_MAP;
	}
	
	public TreeMap<String,ImageIcon> getLocationIconMap() {
		return LOCATION_ICON_MAP;
	}
	
	public TreeMap<String,ImageIcon> getEventIconMap() {
		return EVENT_ICON_MAP;
	}
	
	public TreeMap<String,ImageIcon> getWindowIconMap() {
		return WINDOW_ICON_MAP;
	}
	
	public TreeMap<String,BufferedImage> getImageMap() {
		return IMAGE_MAP;
	}
	
	public AppWindow getProgramRoot() {
		return programRoot;
	}
	
	public Properties getProperties() {
		return PROPERTIES;
	}
	
	public LinkedList<Component> getRootComponentList() {
		return ROOT_COMPONENT_LIST;
	}
	
	private static Properties initializeDefaultProperties() {
		final Properties defaultProperties = new Properties();
		defaultProperties.setProperty("Toolbar Location","North");
		defaultProperties.setProperty("Look And Feel","com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		defaultProperties.setProperty("Display Watermark","true");
		defaultProperties.setProperty("Extended State","0");
		defaultProperties.setProperty("Screen Fraction","0.9");
		defaultProperties.setProperty("Show South Panel","true");
		
		defaultProperties.setProperty("Toolbar Icon Size","32");
		defaultProperties.setProperty("BandTable Icon Size","32");
		defaultProperties.setProperty("Misc Icon Size","32");
		
		defaultProperties.setProperty("Show Toolbar Icon Text","false");
		defaultProperties.setProperty("Show BandTable Icon Text","false");
		defaultProperties.setProperty("Show Misc Icon Text","true");
		
		defaultProperties.setProperty("Show Toolbar","true");
		defaultProperties.setProperty("Lock Toolbar","false");
		
		defaultProperties.setProperty("Show BandTable Horizontal Grid Lines","true");
		defaultProperties.setProperty("Show BandTable Vertical Grid Lines","true");
		defaultProperties.setProperty("Name Data Field","NAME");
		
		defaultProperties.setProperty("Preview Text Color",Integer.toString(Color.BLACK.getRGB()));
		defaultProperties.setProperty("Preview Column Count","3");
		
		defaultProperties.setProperty("Simple Search Fields","false");
		defaultProperties.setProperty("Editing Tab Priority","false");
		defaultProperties.setProperty("Default Export Directory", FileSystemView.getFileSystemView().getDefaultDirectory().getPath());
		
		return defaultProperties;
	}
	
	public void loadGUIResources() {
		loadResourcesFromIO();
		prepareUI();
	}
	
	private void loadResourcesFromIO() {
		IO.loadCursors();
		IO.loadFonts();
		IO.loadDesktopIcons();
		IO.loadHeaderIcons();
		IO.loadInstrumentIcons();
		IO.loadIcons();
		IO.loadLocationIcons();
		IO.loadEventIcons();
		IO.loadWindowIcons();
		IO.loadImages();
		IO.loadProperties();
	}
	
	private void prepareUI() {
		try {
			UIManager.setLookAndFeel(PROPERTIES.getProperty("Look And Feel"));
			UIManager.put("PopupMenu.consumeEventOnClose",false);
		} catch (Exception exc) {
			PROPERTIES.setProperty("Look And Feel",UIManager.getLookAndFeel().getClass().getName());
			System.err.println("Program failed to set Look And Feel " + PROPERTIES.getProperty("Look And Feel"));
			exc.printStackTrace();
		}
	}
	
	public void setProgramRoot(AppWindow value) {
		programRoot = value;
	}
}