package pepband3.gui;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

import pepband3.gui.component.*;
import pepband3.gui.component.popup.*;

public class Tools {
	
	public static void addRootComponent(Component value) {
		GUIManager.getGUIManager().getRootComponentList().add(value);
	}
	
	public static void applyScrollPopup(JScrollBar value) {
		ScrollPopup popup = new ScrollPopup(value);
		value.setComponentPopupMenu(popup);
		Tools.addRootComponent(popup);
	}
	
	public static void applyScrollPopup(Container value) {
		Component[] children = value.getComponents();
		for (Component child : children) {
			if (child instanceof Container) {
				applyScrollPopup((Container)child);
			} else if (child instanceof JScrollBar) {
				applyScrollPopup((JScrollBar)child);
			}
		}
		if (value instanceof JScrollBar) {
			applyScrollPopup((JScrollBar)value);
		}
	}
	
	public static void applyTextPopup(JTextComponent value) {
		TextPopup popup = new TextPopup(value);
		value.setComponentPopupMenu(popup);
		Tools.addRootComponent(popup);
	}
	
	public static void applyTextPopup(Container value) {
		Component[] children = value.getComponents();
		for (Component child : children) {
			if (child instanceof Container) {
				applyTextPopup((Container)child);
			} else if (child instanceof JTextComponent) {
				applyTextPopup((JTextComponent)child);
			}
		}
		if (value instanceof JTextComponent) {
			applyTextPopup((JTextComponent)value);
		}
	}
	
	public static String determineOriginalToolbarLocation() {
		String value = getProperty("Toolbar Location");
		if (value != null && value.equals(BorderLayout.NORTH) || value.equals(BorderLayout.WEST) || value.equals(BorderLayout.SOUTH) || value.equals(BorderLayout.EAST)) {
			return value;
		} else {
			setProperty("Toolbar Location",BorderLayout.NORTH);
			return BorderLayout.NORTH;
		}
	}
	
	public static int determineOriginalToolbarOrientation() {
		String location = determineOriginalToolbarLocation();
		if (location.equals(BorderLayout.WEST) || location.equals(BorderLayout.EAST)) {
			return SwingConstants.VERTICAL;
		} else {
			return SwingConstants.HORIZONTAL;
		}
	}
	
	public static GraphicsConfiguration determineOriginalScreen() {
		String deviceID = getProperty("Preferred Screen Device");
		if (deviceID == null) {
			GraphicsConfiguration defaultConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			setProperty("Preferred Screen Device",defaultConfig.getDevice().getIDstring());
			return defaultConfig;
		} else {
			for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
				if (device.getIDstring().equals(deviceID)) {
					return device.getDefaultConfiguration();
				}
			}
			GraphicsConfiguration defaultConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
			setProperty("Preferred Screen Device",defaultConfig.getDevice().getIDstring());
			return defaultConfig;
		}
	}
	
	public static Boolean getBoolean(String key, boolean defaultValue) {
		try {
			return Boolean.parseBoolean(getProperty(key));
		} catch (Exception exc) {
			setProperty(key,Boolean.toString(defaultValue));
			System.err.println("Tools failed to read " + key + " property. Using " + Boolean.toString(defaultValue));
			exc.printStackTrace();
			return defaultValue;
		}
	}
	
	public static Cursor getCursor(String key) {
		return GUIManager.getGUIManager().getCursorMap().get(key);
	}
	
	public static ImageIcon getDesktopIcon(String key) {
		return GUIManager.getGUIManager().getDesktopIconMap().get(key);
	}
	
	public static AppWindowDesktop getDesktopPane() {
		return getProgramRoot().getDesktopPane();
	}
	
	public static Double getDouble(String key, double defaultValue) {
		try {
			return Double.parseDouble(getProperty(key));
		} catch (Exception exc) {
			setProperty(key,Double.toString(defaultValue));
			System.err.println("Tools failed to read " + key + " property. Using " + Double.toString(defaultValue));
			exc.printStackTrace();
			return defaultValue;
		}
	}
	
	public static Font getFont(String key) {
		return GUIManager.getGUIManager().getFontMap().get(key);
	}
	
	public static ImageIcon getEventIcon(String key) {
		return GUIManager.getGUIManager().getEventIconMap().get(key);
	}
	
	public static TreeMap<String,ImageIcon> getEventIcons() {
		return GUIManager.getGUIManager().getEventIconMap();
	}
	
	public static ImageIcon getInstrumentIcon(String key) {
		return GUIManager.getGUIManager().getInstrumentIconMap().get(key);
	}
	
	public static TreeMap<String,ImageIcon> getInstrumentIcons() {
		return GUIManager.getGUIManager().getInstrumentIconMap();
	}
	
	public static ImageIcon getHeaderIcon(String key) {
		return GUIManager.getGUIManager().getHeaderIconMap().get(key);
	}
	
	public static Integer getInteger(String key, int defaultValue) {
		try {
			return Integer.parseInt(getProperty(key));
		} catch (Exception exc) {
			setProperty(key,Integer.toString(defaultValue));
			System.err.println("Tools failed to read " + key + " property. Using " + Integer.toString(defaultValue));
			exc.printStackTrace();
			return defaultValue;
		}
	}
	
	public static ImageIcon getIcon(String key) {
		return GUIManager.getGUIManager().getIconMap().get(key);
	}
	
	public static BufferedImage getImage(String key) {
		return GUIManager.getGUIManager().getImageMap().get(key);
	}
	
	public static ImageIcon getLocationIcon(String key) {
		return GUIManager.getGUIManager().getLocationIconMap().get(key);
	}
	
	public static TreeMap<String,ImageIcon> getLocationIcons() {
		return GUIManager.getGUIManager().getLocationIconMap();
	}
	
	public static AppWindow getProgramRoot() {
		return GUIManager.getGUIManager().getProgramRoot();
	}
	
	public static String getProperty(String key) {
		return GUIManager.getGUIManager().getProperties().getProperty(key);
	}
	
	public static LinkedList<Component> getRootComponents() {
		return GUIManager.getGUIManager().getRootComponentList();
	}
	
	public static ImageIcon getWindowIcon(String key) {
		return GUIManager.getGUIManager().getWindowIconMap().get(key);
	}
	
	public static LinkedList<Image> getWindowIcons() {
		LinkedList<Image> windowIconList = new LinkedList<Image>();
		TreeMap<String,ImageIcon> windowIconMap = GUIManager.getGUIManager().getWindowIconMap();
		for (String value : windowIconMap.keySet()) {
			windowIconList.add(windowIconMap.get(value).getImage());
		}
		return windowIconList;
	}
	
	public static boolean setLookAndFeel(String className) {
		try {
			UIManager.setLookAndFeel(className);
			UIManager.put("PopupMenu.consumeEventOnClose",false);
			for (Component component : getRootComponents()) {
				SwingUtilities.updateComponentTreeUI(component);
			}
			if (getProgramRoot() != null) {
				applyTextPopup(getProgramRoot().getFileChooser());
				applyScrollPopup(getProgramRoot().getFileChooser());
				getProgramRoot().reapplyTipListener();
			}
			setProperty("Look And Feel",className);
			return true;
		} catch (Exception exc) {
			System.err.println("Tools failed to set the Look And Feel");
			exc.printStackTrace();
			return false;
		}
	}
	
	public static void setProgramRoot(AppWindow value) {
		GUIManager.getGUIManager().setProgramRoot(value);
	}
	
	public static void setProperty(String key, String value) {
		GUIManager.getGUIManager().getProperties().setProperty(key,value);
	}
}