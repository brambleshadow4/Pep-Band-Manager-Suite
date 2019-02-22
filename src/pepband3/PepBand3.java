package pepband3;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import pepband3.data.*;
import pepband3.gui.*;

/*
 * Copyright 2009 Eric Heumann
 * Pep Band Manager Suite 2.0.0 (Major Version . Data Version . GUI Version)
 * Uses XStream 1.3.1
 * Uses XPP3 Min 1.1.4c
 * Uses iText 2.1.4
 * 
 */

public class PepBand3 {
	
	private static BackgroundManager backgroundManager;
	
	public static void main(String[] args) {
		addTextToSplash("Loading");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				addTextToSplash("Loading Background Manager");
				backgroundManager = new BackgroundManager();
				
				addTextToSplash("Loading GUI Resources");
				GUIManager.getGUIManager().loadGUIResources();
				
				addTextToSplash("Loading Data Resources");
				DataManager.loadDataResources();
				
				addTextToSplash("Loading Application Window and Dialogs");
				AppWindow appWindow = new AppWindow();
				appWindow.display();
				
				addTextToSplash("Done Loading\n");
			}
		});
	}
	
	public static void addTextToSplash(String text) {
		System.out.println(text);
		if (SplashScreen.getSplashScreen() != null && text != null && !text.isEmpty()) {
			try {
				SplashScreen splash = SplashScreen.getSplashScreen();
				int splashWidth = (int) splash.getSize().getWidth();
				int splashHeigh = (int) splash.getSize().getHeight();
				
				Graphics2D g = (Graphics2D)splash.createGraphics();
				g.setComposite(AlphaComposite.Clear);
				g.fillRect(0, 0, splashWidth, splashHeigh);
				g.setPaintMode();
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setFont(new Font("SansSerif",Font.BOLD,12));
				g.setColor(Color.RED);
				
				Rectangle2D stringSize = g.getFontMetrics().getStringBounds(text,g);
				int stringWidth = (int)stringSize.getWidth();
				int stringHeight = (int)stringSize.getHeight();
				
				g.drawString(text, (splashWidth - stringWidth) / 2, 584 + stringHeight / 2);
				g.dispose();
				splash.update();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	}
}
