package pepband3;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.channels.*;
import java.text.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

import com.thoughtworks.xstream.*;

import pepband3.data.*;
import pepband3.gui.*;

public class IO {
	
	private static final File DATA_FILE = new File("Pep Band Data.xml");
	private static final File TEMP_FILE = new File("Pep Band Data.bak");
	private static final File PROPERTIES_FILE = new File("Pep Band Settings.xml");
	
	private static final XStream XSTREAM = new XStream();
	
	private static final String ICON_FORMAT = ".png";
	private static final Integer[] ICON_SIZES = {16,32,48};
	private static final String CURSOR_FORMAT = ".gif";
	private static final Integer[] CURSOR_SIZES = {16,32};
	private static final String[] CURSORS_ARRAY = {"handgrab","handgrabbed"};
	private static final String[] FONTS_ARRAY = {"Eurostile.TTF","Eurostile Bold.TTF","Exotic 350 Bold BT.TTF","Lucida Sans Demi Bold.ttf"};
	private static final String[] IMAGES_ARRAY = {};
	private static final String[] DESKTOP_ICONS_ARRAY = {"cornell","hot truck","texas"};
	private static final String[] HEADER_ICONS_ARRAY = {"allies","soviets","gdi","nod","quake","halflife","halflife2","wraith","wolfenstein",
														"member",
														"locations","paint","worldbox",
														"events","jump","neo",
														"seasons","snowflake","leaf",
														"drum","note","notes",
														"memberadd","memberedit",
														"about","minisplash"};
	private static final String[] INSTRUMENT_ICONS_ARRAY = {"cclef","coda","eighth","fclef","gclef","neutral","pedalup","segno","stick","tab"};
	private static final String[] LOCATION_ICONS_ARRAY = {"away","home","playoff","car","plane"};
	private static final String[] EVENT_ICONS_ARRAY = {	"adjustment","ball","baseball","basketball","boxing","fieldball","football","hockey","lacrosse",
														"music","question","soccer","tennis","time","volleyball"};
	private static final String[] WINDOW_ICONS_ARRAY = {"waldo16","waldo32","waldo48","waldo128","waldo256"};
	private static final String[] ICONS_ARRAY = {"acrobat","add","appearance","band","cascade","close","copy","cut","data","edit","editing","email","empty","events",
												"filter","history","info","member","memberinfo","minimize","new","nonlocevent","open","openevent","options","paste","pointer","print",
												"program","remove","restore","search","seasons","statistics","tilehorizontal","tilevertical","useradd"};
	private static boolean vocal;
	
	static {
		XSTREAM.alias("PepBandData", DataManager.class);
		XSTREAM.alias("Season", Season.class);
		XSTREAM.alias("PepBandEvent", PepBandEvent.class);
		XSTREAM.alias("AbstractBand", Band.class);
		XSTREAM.alias("Member", Member.class);
		XSTREAM.alias("Instrument", Instrument.class);
		XSTREAM.alias("Location", Location.class);
		XSTREAM.alias("EventType", EventType.class);
		XSTREAM.alias("AbstractPepBandData", PepBandData.class);
		
		vocal = true;
	}
			
	public static void doFileCopy(File origin, File destination) {
		String exceptionString = null;
		try {
			FileInputStream inputStream = new FileInputStream(origin);
			FileOutputStream outputStream = new FileOutputStream(destination);
			FileLock outputLock = outputStream.getChannel().tryLock();
			if (outputLock == null) {
				throw new IOException("IO failed to perform file copy because the output file was locked");
			}
			int bytesRead = 0;
			byte[] buffer = new byte[(int)Math.pow(2, 20)];
			while (bytesRead >= 0) {
				bytesRead = inputStream.read(buffer, 0, buffer.length);
				if (bytesRead >= 0) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
			outputStream.flush();
			try {
				outputStream.getFD().sync();
			} catch (Exception exc) {
				exceptionString = "IO issue: stream writing to copy file failed to sync after flushing";
				exc.printStackTrace();
			}
			outputLock.release();
			inputStream.close();
			outputStream.close();
		} catch (Exception exc) {
			exceptionString = "IO failed to perform a file copy from " + origin.getName() + " to " + destination.getName();
			exc.printStackTrace();
		}
		if (vocal && exceptionString != null) {
			JOptionPane.showMessageDialog(null, exceptionString, "IO Exception", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static File getBackUpBackUpFile(String title, String extension) {
		String fileName = title + " Pep Band Backup " + DateFormat.getTimeInstance(DateFormat.LONG).format(Calendar.getInstance().getTime()) + "." + extension.toLowerCase();
		fileName = fileName.replace(':',' ').replace('/',' ').replace('\\',' ');
		return new File(TEMP_FILE.getParentFile(), fileName);
	}
	
	public static File getBackUpDataFile(String title, String extension) {
		String fileName = title + " Pep Band Data " + DateFormat.getTimeInstance(DateFormat.LONG).format(Calendar.getInstance().getTime()) + "." + extension.toLowerCase();
		fileName = fileName.replace(':',' ').replace('/',' ').replace('\\',' ');
		return new File(DATA_FILE.getParentFile(), fileName);
	}
	
	public static boolean isVocal() {
		return vocal;
	}
	
	public static DataManager loadData() {
		return loadData(DATA_FILE);
	}
	
	public static DataManager loadData(File loadFile) {
		DataManager loadedManager = null;
		String exceptionString = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(loadFile);
			try {
				Object loadedObject = XSTREAM.fromXML(fileInputStream);
				try {
					loadedManager = (DataManager)loadedObject;
				} catch (ClassCastException exc) {
					fileInputStream.close();
					doFileCopy(DATA_FILE, getBackUpDataFile("Corrupted","xml"));
					doFileCopy(TEMP_FILE, getBackUpBackUpFile("Saved","xml"));
					exceptionString = "IO failed to load database: loaded data was not a Pep Band Manager database";
					exc.printStackTrace();
				} catch (Exception exc) {
					fileInputStream.close();
					doFileCopy(DATA_FILE, getBackUpDataFile("Corrupted","xml"));
					doFileCopy(TEMP_FILE, getBackUpBackUpFile("Saved","xml"));
					exceptionString = "IO failed to load database: unexpected exception";
					exc.printStackTrace();
				}
			} catch (XStreamException exc) {
				fileInputStream.close();
				doFileCopy(DATA_FILE, getBackUpDataFile("Corrupted","xml"));
				doFileCopy(TEMP_FILE, getBackUpBackUpFile("Saved","xml"));
				exceptionString = "IO failed to load database: XStream XML load failed (data file is most likely corrupted or malformatted)";
				exc.printStackTrace();
			} catch (Exception exc) {
				fileInputStream.close();
				doFileCopy(DATA_FILE, getBackUpDataFile("Corrupted","xml"));
				doFileCopy(TEMP_FILE, getBackUpBackUpFile("Saved","xml"));
				exceptionString = "IO failed to load database: unexpected exception";
				exc.printStackTrace();
			}
			fileInputStream.close();
		} catch (FileNotFoundException exc) {
			exceptionString = "IO failed to load database: could not find file";
			exc.printStackTrace();
		} catch (SecurityException exc) {
			exceptionString = "IO failed to load database: security would not allow access to file";
			exc.printStackTrace();
		} catch (Exception exc) {
			exceptionString = "IO failed to load database: unexpected exception";
			exc.printStackTrace();
		}
		if (vocal && exceptionString != null) {
			JOptionPane.showMessageDialog(null, exceptionString, "IO Exception", JOptionPane.ERROR_MESSAGE);
		}
		return loadedManager;
	}
	
	public static void loadCursors() {
		GUIManager.getGUIManager().getCursorMap().put("hand", new Cursor(Cursor.HAND_CURSOR));
		for (int index = 0; index < CURSORS_ARRAY.length; index++) {
			try {
				ImageIcon imageIcon = new ImageIcon(IO.class.getResource("cursors/" + CURSORS_ARRAY[index] + CURSOR_SIZES[0] + CURSOR_FORMAT));
				Dimension dimension = Toolkit.getDefaultToolkit().getBestCursorSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
				if (dimension.getWidth() == imageIcon.getIconWidth() && dimension.getHeight() == imageIcon.getIconHeight()) {
					Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(imageIcon.getImage(), new Point(imageIcon.getIconWidth() / 2, imageIcon.getIconHeight() / 2), CURSORS_ARRAY[index]);
					GUIManager.getGUIManager().getCursorMap().put(CURSORS_ARRAY[index], cursor);
				} else {
					imageIcon = new ImageIcon(IO.class.getResource("cursors/" + CURSORS_ARRAY[index] + CURSOR_SIZES[1] + CURSOR_FORMAT));
					dimension = Toolkit.getDefaultToolkit().getBestCursorSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
					Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(imageIcon.getImage(), new Point(imageIcon.getIconWidth() / 2, imageIcon.getIconHeight() / 2), CURSORS_ARRAY[index]);
					GUIManager.getGUIManager().getCursorMap().put(CURSORS_ARRAY[index], cursor);
				}
			} catch(IndexOutOfBoundsException exc) {
				System.err.println("IO failed to load cursor " + CURSORS_ARRAY[index]);
				exc.printStackTrace();
			} catch(NullPointerException exc) {
				System.err.println("IO failed to load cursor " + CURSORS_ARRAY[index]);
				exc.printStackTrace();
			} catch (Exception exc) {
				System.err.println("IO failed to load cursor " + CURSORS_ARRAY[index]);
				exc.printStackTrace();
			}
		}
	}
	
	public static void loadFonts() {
		for (int index = 0; index < FONTS_ARRAY.length; index++) {
			try {
				Font font = Font.createFont(Font.TRUETYPE_FONT,IO.class.getResourceAsStream("fonts/" + FONTS_ARRAY[index]));
				GUIManager.getGUIManager().getFontMap().put(FONTS_ARRAY[index],font);
			} catch(FontFormatException exc) {
				System.err.println("IO failed to load font " + FONTS_ARRAY[index]);
				exc.printStackTrace();
			} catch(IOException exc) {
				System.err.println("IO failed to load font " + FONTS_ARRAY[index]);
				exc.printStackTrace();
			} catch (Exception exc) {
				System.err.println("IO failed to load font " + FONTS_ARRAY[index]);
				exc.printStackTrace();
			}
		}
	}
	
	public static void loadDesktopIcons() {
		for (int index = 0; index < DESKTOP_ICONS_ARRAY.length; index++) {
			try {
				ImageIcon icon = new ImageIcon(IO.class.getResource("icons/desktop/" + DESKTOP_ICONS_ARRAY[index] + ICON_FORMAT));
				GUIManager.getGUIManager().getDesktopIconMap().put(DESKTOP_ICONS_ARRAY[index],icon);
			} catch(NullPointerException exc) {
				System.err.println("IO failed to load desktop icon " + DESKTOP_ICONS_ARRAY[index] + ICON_FORMAT);
				exc.printStackTrace();
			} catch (Exception exc) {
				System.err.println("IO failed to load desktop icon " + DESKTOP_ICONS_ARRAY[index] + ICON_FORMAT);
				exc.printStackTrace();
			}
		}
	}
	
	public static void loadHeaderIcons() {
		for (int index = 0; index < HEADER_ICONS_ARRAY.length; index++) {
			try {
				ImageIcon icon = new ImageIcon(IO.class.getResource("icons/header/" + HEADER_ICONS_ARRAY[index] + ICON_FORMAT));
				GUIManager.getGUIManager().getHeaderIconMap().put(HEADER_ICONS_ARRAY[index],icon);
			} catch(NullPointerException exc) {
				System.err.println("IO failed to load header icon " + HEADER_ICONS_ARRAY[index] + ICON_FORMAT);
				exc.printStackTrace();
			} catch (Exception exc) {
				System.err.println("IO failed to load header icon " + HEADER_ICONS_ARRAY[index] + ICON_FORMAT);
				exc.printStackTrace();
			}
		}
	}
	
	public static void loadInstrumentIcons() {
		for (int index = 0; index < INSTRUMENT_ICONS_ARRAY.length; index++) {
			for (Integer size : ICON_SIZES) {
				try {
					ImageIcon icon = new ImageIcon(IO.class.getResource("icons/instrument/" + INSTRUMENT_ICONS_ARRAY[index] + size + ICON_FORMAT));
					GUIManager.getGUIManager().getInstrumentIconMap().put(INSTRUMENT_ICONS_ARRAY[index] + size,icon);
				} catch(NullPointerException exc) {
					System.err.println("IO failed to load instrument icon " + INSTRUMENT_ICONS_ARRAY[index] + size + ICON_FORMAT);
					exc.printStackTrace();
				} catch (Exception exc) {
					System.err.println("IO failed to load instrument icon " + INSTRUMENT_ICONS_ARRAY[index] + size + ICON_FORMAT);
					exc.printStackTrace();
				}
			}
		}
	}
	
	public static void loadIcons() {
		for (int index = 0; index < ICONS_ARRAY.length; index++) {
			for (Integer size : ICON_SIZES) {
				try {
					ImageIcon icon = new ImageIcon(IO.class.getResource("icons/" + ICONS_ARRAY[index] + size + ICON_FORMAT));
					GUIManager.getGUIManager().getIconMap().put(ICONS_ARRAY[index] + size,icon);
				} catch(NullPointerException exc) {
					System.err.println("IO failed to load icon " + ICONS_ARRAY[index] + size + ICON_FORMAT);
					exc.printStackTrace();
				} catch (Exception exc) {
					System.err.println("IO failed to load icon " + ICONS_ARRAY[index] + size + ICON_FORMAT);
					exc.printStackTrace();
				}
			}
		}
	}
	
	
	
	public static void loadLocationIcons() {
		for (int index = 0; index < LOCATION_ICONS_ARRAY.length; index++) {
			for (Integer size : ICON_SIZES) {
				try {
					ImageIcon icon = new ImageIcon(IO.class.getResource("icons/location/" + LOCATION_ICONS_ARRAY[index] + size + ICON_FORMAT));
					GUIManager.getGUIManager().getLocationIconMap().put(LOCATION_ICONS_ARRAY[index] + size,icon);
				} catch(NullPointerException exc) {
					System.err.println("IO failed to load location icon " + LOCATION_ICONS_ARRAY[index] + size + ICON_FORMAT);
					exc.printStackTrace();
				} catch (Exception exc) {
					System.err.println("IO failed to load location icon " + LOCATION_ICONS_ARRAY[index] + size + ICON_FORMAT);
					exc.printStackTrace();
				}
			}
		}
	}
	
	public static void loadEventIcons() {
		for (int index = 0; index < EVENT_ICONS_ARRAY.length; index++) {
			for (Integer size : ICON_SIZES) {
				try {
					ImageIcon icon = new ImageIcon(IO.class.getResource("icons/event/" + EVENT_ICONS_ARRAY[index] + size + ICON_FORMAT));
					GUIManager.getGUIManager().getEventIconMap().put(EVENT_ICONS_ARRAY[index] + size,icon);
				} catch(NullPointerException exc) {
					System.err.println("IO failed to load event icon " + EVENT_ICONS_ARRAY[index] + size + ICON_FORMAT);
					exc.printStackTrace();
				} catch (Exception exc) {
					System.err.println("IO failed to load event icon " + EVENT_ICONS_ARRAY[index] + size + ICON_FORMAT);
					exc.printStackTrace();
				}
			}
		}
	}
	
	public static void loadWindowIcons() {
		for (int index = 0; index < WINDOW_ICONS_ARRAY.length; index++) {
			try {
				ImageIcon icon = new ImageIcon(IO.class.getResource("icons/window/" + WINDOW_ICONS_ARRAY[index] + ICON_FORMAT));
				GUIManager.getGUIManager().getWindowIconMap().put(WINDOW_ICONS_ARRAY[index],icon);
			} catch(NullPointerException exc) {
				System.err.println("IO failed to load window icon " + WINDOW_ICONS_ARRAY[index] + ICON_FORMAT);
				exc.printStackTrace();
			} catch (Exception exc) {
				System.err.println("IO failed to load window icon " + WINDOW_ICONS_ARRAY[index] + ICON_FORMAT);
				exc.printStackTrace();
			}
		}
	}
	
	public static void loadImages() {
		for (int index = 0; index < IMAGES_ARRAY.length; index++) {
			try {
				BufferedImage image = ImageIO.read(IO.class.getResource("artwork/" + IMAGES_ARRAY[index]));
				GUIManager.getGUIManager().getImageMap().put(IMAGES_ARRAY[index],image);
			} catch(IllegalArgumentException exc) {
				System.err.println("IO failed to load image " + IMAGES_ARRAY[index]);
				exc.printStackTrace();
			} catch(IOException exc) {
				System.err.println("IO failed to load image " + IMAGES_ARRAY[index]);
				exc.printStackTrace();
			} catch (Exception exc) {
				System.err.println("IO failed to load image " + IMAGES_ARRAY[index]);
				exc.printStackTrace();
			}
		}
	}
	
	public static void loadProperties() {
		String exceptionString = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(PROPERTIES_FILE);
			try {
				GUIManager.getGUIManager().getProperties().loadFromXML(fileInputStream);
			} catch (InvalidPropertiesFormatException exc) {
				exceptionString = "IO failed to load properties: properties file has been corrupted";
				exc.printStackTrace();
			} catch (IOException exc) {
				exceptionString = "IO failed to load properties: input error while loading";
				exc.printStackTrace();
			} catch (Exception exc) {
				exceptionString = "IO failed to load properties: unexpected exception";
				exc.printStackTrace();
			}
			fileInputStream.close();
		} catch (FileNotFoundException exc) {
			exceptionString = "IO failed to load properties: could not find file";
			exc.printStackTrace();
		} catch (SecurityException exc) {
			exceptionString = "IO failed to load properties: security would not allow access to file";
			exc.printStackTrace();
		} catch (Exception exc) {
			exceptionString = "IO failed to load properties: unexpected exception";
			exc.printStackTrace();
		}
		if (vocal && exceptionString != null) {
			JOptionPane.showMessageDialog(null, exceptionString, "IO Exception", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static synchronized String saveData(DataManager dataManager, boolean commit) {
		String exceptionString = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(TEMP_FILE);
			FileLock fileLock = fileOutputStream.getChannel().tryLock();
			if (fileLock == null) {
				throw new IOException("IO failed to save database because the temp data file was locked");
			}
			try {
				XSTREAM.toXML(dataManager, fileOutputStream);
			} catch (XStreamException exc) {
				exceptionString = "IO failed to save database: XStream XML save failed (please call up Eric and yell at him)";
				exc.printStackTrace();
			} catch (Exception exc) {
				exceptionString = "IO failed to save database: unexpected exception";
				exc.printStackTrace();
			}
			fileOutputStream.flush();
			try {
				fileOutputStream.getFD().sync();
			} catch (Exception exc) {
				exceptionString = "IO issue: stream writing to file failed to sync after flushing";
				exc.printStackTrace();
			}
			fileLock.release();
			fileOutputStream.close();
			if (commit) {
				DataManager testManager = loadData(TEMP_FILE);
				if (testManager != null) {
					doFileCopy(TEMP_FILE, DATA_FILE);
				} else {
					exceptionString = "IO failed to determinately save database: IO aborted final save to main data file after a data load from the temporary file failed";
				}
			}
		} catch (FileNotFoundException exc) {
			exceptionString = "IO failed to save database: file could not be created, or exists and could not be written to";
			exc.printStackTrace();
		} catch (SecurityException exc) {
			exceptionString = "IO failed to save database: security would not allow access to file";
			exc.printStackTrace();
		} catch (Exception exc) {
			exceptionString = "IO failed to save database: unexpected exception";
			exc.printStackTrace();
		}
		if (vocal && exceptionString != null) {
			JOptionPane.showMessageDialog(null, exceptionString, "IO Exception", JOptionPane.ERROR_MESSAGE);
		}
		return exceptionString;
	}
	
	public static synchronized String saveProperties() {
		String exceptionString = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(PROPERTIES_FILE);
			try {
				DateFormat format = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL);
				GUIManager.getGUIManager().getProperties().storeToXML(fileOutputStream,format.format(new Date(System.currentTimeMillis())));
			} catch (IOException exc) {
				exceptionString = "IO failed to save properties: output error while saving";
				exc.printStackTrace();
			} catch (Exception exc) {
				exceptionString = "IO failed to save properties: unexpected exception";
				exc.printStackTrace();
			}
			fileOutputStream.close();
		} catch (FileNotFoundException exc) {
			exceptionString = "IO failed to save properties: file could not be created, or exists and could not be written to";
			exc.printStackTrace();
		} catch (SecurityException exc) {
			exceptionString = "IO failed to save properties: security would not allow access to file";
			exc.printStackTrace();
		} catch (Exception exc) {
			exceptionString = "IO failed to save properties: unexpected exception";
			exc.printStackTrace();
		}
		if (vocal && exceptionString != null) {
			JOptionPane.showMessageDialog(null, exceptionString, "IO Exception", JOptionPane.ERROR_MESSAGE);
		}
		return exceptionString;
	}
	
	public static void setVocal(boolean value) {
		vocal = value;
	}
}