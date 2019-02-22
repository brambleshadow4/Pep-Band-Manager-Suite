package pepband3.gui.component;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.gui.*;

public class AppWindowDesktop extends JDesktopPane {
	
	private static final int windowOffset = 30;
	
	private LoadMessageWindow loadMessageWindow;
	
	private Color customBackground;
	private AppWindow appWindow;
	private int windowXLocation;
	private int windowYLocation;
	
	public AppWindowDesktop(AppWindow paramAppWindow) {
		super();
		setOpaque(true);
		customBackground = Color.WHITE;
		appWindow = paramAppWindow;
		loadMessageWindow = null;
		windowXLocation = 0;
		windowYLocation = 0;
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					addSeasonWindow(DataManager.getDataManager().getCurrentSeason()).display();
				}
			}
		});
	}
	
	public EventWindow addEventWindow(PepBandEvent event) {
		EventWindow window = getEventWindow(event);
		if (window == null) {
			window = new EventWindow(event);
			add(window);
			appWindow.reapplyTipListener();
			appWindow.addListenersToWindow(window);
			setWindowLocation(window);
		}
		return window;
	}
	
	public LoadMessageWindow addLoadMessageWindow() {
		if (loadMessageWindow == null) {
			loadMessageWindow = new LoadMessageWindow();
			add(loadMessageWindow);
			appWindow.reapplyTipListener();
			return loadMessageWindow;
		} else {
			for (JInternalFrame frame : getAllFrames()) {
				if (frame.equals(loadMessageWindow)) {
					return loadMessageWindow;
				}
			}
			add(loadMessageWindow);
			return loadMessageWindow;
		}
	}
	
	public MemberWindow addMemberWindow(Member member) {
		MemberWindow window = getMemberWindow(member);
		if (window == null) {
			window = new MemberWindow(member);
			add(window);
			appWindow.reapplyTipListener();
			appWindow.addListenersToWindow(window);
			setWindowLocation(window);
		}
		return window;
	}
	
	public SeasonWindow addSeasonWindow(Season season) {
		SeasonWindow window = getSeasonWindow(season);
		if (window == null) {
			window = new SeasonWindow(season);
			add(window);
			appWindow.reapplyTipListener();
			appWindow.addListenersToWindow(window);
			setWindowLocation(window);
		}
		return window;
	}
	
	public StatisticsWindow addStatisticsWindow(Band band) {
		StatisticsWindow window = getStatisticsWindow(band);
		if (window == null) {
			window = new StatisticsWindow(band);
			add(window);
			appWindow.reapplyTipListener();
			appWindow.addListenersToWindow(window);
			setWindowLocation(window);
		}
		return window;
	}
	
	public void cascade() {
		JInternalFrame[] frames = getAllFrames();
		windowXLocation = 0;
		windowYLocation = 0;
		for(int index = frames.length - 1; index >= 0; index--) {
			JInternalFrame frame = frames[index];
			if(frame.isMaximum()) {
				try{
					frame.setMaximum(false);
				} catch (Exception exc) {
					System.err.println("Could not un-maximize window");
					exc.printStackTrace();
				}
			}
			frame.pack();
			setWindowLocation(frame);
		}
	}
	
	public boolean containsEventWindow(PepBandEvent event) {
		JInternalFrame[] frames = getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame instanceof EventWindow ) {
				EventWindow window = (EventWindow)frame;
				if (window.getEvent().equals(event)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean containsMemberWindow(Member member) {
		JInternalFrame[] frames = getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame instanceof MemberWindow ) {
				MemberWindow window = (MemberWindow)frame;
				if (window.getMember().equals(member)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean containsSeasonWindow(Season season) {
		JInternalFrame[] frames = getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame instanceof SeasonWindow ) {
				SeasonWindow window = (SeasonWindow)frame;
				if (window.getSeason().equals(season)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean containsStatisticsWindow(Band band) {
		JInternalFrame[] frames = getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame instanceof StatisticsWindow ) {
				StatisticsWindow window = (StatisticsWindow)frame;
				if (window.getBand().equals(band)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Color getCustomBackground() {
		return customBackground;
	}
	
	public EventWindow getEventWindow(PepBandEvent event) {
		JInternalFrame[] frames = getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame instanceof EventWindow ) {
				EventWindow window = (EventWindow)frame;
				if (window.getEvent().equals(event)) {
					return window;
				}
			}
		}
		return null;
	}
	
	public MemberWindow getMemberWindow(Member member) {
		JInternalFrame[] frames = getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame instanceof MemberWindow ) {
				MemberWindow window = (MemberWindow)frame;
				if (window.getMember().equals(member)) {
					return window;
				}
			}
		}
		return null;
	}
	
	public SeasonWindow getSeasonWindow(Season season) {
		JInternalFrame[] frames = getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame instanceof SeasonWindow ) {
				SeasonWindow window = (SeasonWindow)frame;
				if (window.getSeason().equals(season)) {
					return window;
				}
			}
		}
		return null;
	}
	
	public StatisticsWindow getStatisticsWindow(Band band) {
		JInternalFrame[] frames = getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame instanceof StatisticsWindow ) {
				StatisticsWindow window = (StatisticsWindow)frame;
				if (window.getBand().equals(band)) {
					return window;
				}
			}
		}
		return null;
	}
	
	public void minimizeAll() {
		JInternalFrame[] frames = getAllFrames();
		for(int index = 0; index < frames.length; index++) {
			if (frames[index] instanceof InternalWindow) {
				((InternalWindow)frames[index]).minimize();
			}
		}
	}
	
	protected void paintComponent(Graphics g) {
		if (Tools.getBoolean("Display Watermark",true)) {
			g.setColor(getCustomBackground());
			g.fillRect(0,0,this.getWidth(),this.getHeight());
			Icon icon = null;
			if(Calendar.getInstance().get(Calendar.DATE) == 2 && Calendar.getInstance().get(Calendar.MONTH) == Calendar.MARCH) {
				icon = Tools.getDesktopIcon("texas");
			} else if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
				icon = Tools.getDesktopIcon("hot truck");
			} else {
				icon = Tools.getDesktopIcon("cornell");
			}
			icon.paintIcon(this,g,this.getWidth()/2 - icon.getIconWidth()/2,this.getHeight()/2 - icon.getIconHeight()/2);
		} else {
			super.paintComponent(g);
		}
	}
	
	public void removeWindow(InternalWindow window) {
		remove(window);
		appWindow.removeListenersFromWindow(window);
		JInternalFrame[] frames = getAllFrames();
		if (getSelectedFrame() == null && frames.length > 0) {
			for (JInternalFrame frame : frames) {
				if (frame instanceof InternalWindow && !frame.isIcon()) {
					((InternalWindow) frame).display();
					break;
				}
			}
		}
	}
	
	public void repaintAllMemberWindows() {
		JInternalFrame[] frames = getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame instanceof MemberWindow ) {
				frame.repaint();
			}
		}
	}
	
	public void repaintAllStatisticsWindows() {
		JInternalFrame[] frames = getAllFrames();
		for (JInternalFrame frame : frames) {
			if (frame instanceof StatisticsWindow ) {
				frame.repaint();
			}
		}
	}
	
	public void restoreAll() {
		JInternalFrame[] frames = getAllFrames();
		for(int index = frames.length - 1; index >= 0; index--) {
			if (frames[index] instanceof InternalWindow) {
				((InternalWindow)frames[index]).display();
			}
		}
	}
	
	public void setCustomBackground(Color value) {
		if (value != null) {
			customBackground = value;
		} else {
			throw new NullPointerException("DO NOT SET APPWINDOW DESKTOP CUSTOM BACKGROUND COLOR TO NULL");
		}
	}
	
	private void setWindowLocation(JInternalFrame frame) {
		if (frame.getWidth() <= 0 || frame.getHeight() <= 0) {
			frame.pack();
		}
		if(windowOffset * windowXLocation + frame.getWidth() > getWidth()) {
			windowXLocation = 0;
		}
		if(windowOffset * windowYLocation + frame.getHeight() > getHeight()) {
			windowYLocation = 0;
		}
		frame.setLocation(windowOffset * windowXLocation, windowOffset * windowYLocation);
		windowXLocation++;
		windowYLocation++;
	}
	
	public void tileHorizontally() {
		JInternalFrame[] frames = getAllFrames();
		int numberOfFrames = frames.length;
		int numColumns = (int) Math.sqrt(numberOfFrames);
		int numRows = (int) ((1.0 * numberOfFrames) / (1.0 * numColumns));
		int numColumnsWithExtra = numberOfFrames % numColumns;
		
		int frameWidth = getWidth() / numColumns;
		int frameHeight = getHeight() / numRows;
		int frameShortHeight = getHeight() / (numRows + 1);
		
		int currentFrameNo = 0;
		JInternalFrame frame = null;
		for (int a = 0; a < numColumns - numColumnsWithExtra; a++) {
			for (int b = 0; b < numRows; b++) {
				frame = frames[currentFrameNo];
				if (frame.isMaximum()) {
					try {
						frame.setMaximum(false);
					} catch (Exception exc) {
						System.err.println("Could not un-maximize window");
						exc.printStackTrace();
					}
				}
				frame.setSize(new Dimension(frameWidth, frameHeight));
				frame.setLocation(a * frameWidth, b * frameHeight);
				currentFrameNo++;
			}
		}
		for (int a = numColumns - numColumnsWithExtra; a < numColumns; a++) {
			for (int b = 0; b < numRows + 1; b++) {
				frame = frames[currentFrameNo];
				if (frame.isMaximum()) {
					try {
						frame.setMaximum(false);
					} catch (Exception exc) {
						System.err.println("Could not un-maximize window");
						exc.printStackTrace();
					}
				}
				frame.setSize(new Dimension(frameWidth, frameShortHeight));
				frame.setLocation(a * frameWidth, b * frameShortHeight);
				currentFrameNo++;
			}
		}
	}
	
	public void tileVertically() {
		JInternalFrame[] frames = getAllFrames();
		int numberOfFrames = frames.length;
		int numRows = (int) Math.sqrt(numberOfFrames);
		int numColumns = (int) ((1.0 * numberOfFrames) / (1.0 * numRows));
		int numRowsWithExtra = numberOfFrames % numRows;
		
		int frameWidth = getWidth() / numColumns;
		int frameShortWidth = getWidth() / (numColumns + 1);
		int frameHeight = getHeight() / (numRows);
		
		int currentFrameNo = 0;
		JInternalFrame frame = null;
		for (int a = 0; a < numRows - numRowsWithExtra; a++) {
			for (int b = 0; b < numColumns; b++) {
				frame = frames[currentFrameNo];
				if (frame.isMaximum()) {
					try {
						frame.setMaximum(false);
					} catch (Exception exc) {
						System.err.println("Could not un-maximize window");
						exc.printStackTrace();
					}
				}
				frame.setSize(new Dimension(frameWidth, frameHeight));
				frame.setLocation(b * frameWidth, a * frameHeight);
				currentFrameNo++;
			}
		}
		for (int a = numRows - numRowsWithExtra; a < numRows; a++) {
			for (int b = 0; b < numColumns + 1; b++) {
				frame = frames[currentFrameNo];
				if (frame.isMaximum()) {
					try {
						frame.setMaximum(false);
					} catch (Exception exc) {
						System.err.println("Could not un-maximize window");
						exc.printStackTrace();
					}
				}
				frame.setSize(new Dimension(frameShortWidth, frameHeight));
				frame.setLocation(b * frameShortWidth, a * frameHeight);
				currentFrameNo++;
			}
		}
	}
}