package pepband3.gui.extra;

import java.awt.event.*;
import javax.swing.*;

import pepband3.gui.*;

public abstract class RunnableAction extends AbstractAction implements Runnable {
	
	private String actionPerformingString;
	private ActionEvent recentEvent;
	private boolean asynchronus;
	
	public RunnableAction(String paramName, String paramPerformingString) {
		super(paramName);
		actionPerformingString = paramPerformingString;
		recentEvent = null;
		asynchronus = false;
	}
	
	public RunnableAction(String paramName, String paramPerformingString, boolean paramAsync) {
		super(paramName);
		actionPerformingString = paramPerformingString;
		recentEvent = null;
		asynchronus = paramAsync;
	}
	
	public void actionPerformed(ActionEvent e) {
		recentEvent = e;
		if (asynchronus) {
			Tools.getProgramRoot().getExecutorService().submit(this);
		} else {
			act();
		}
	}
	
	public void run() {
		try {
			Tools.getProgramRoot().setLoading(actionPerformingString);
			act();
		} catch (Exception exc) {
			System.err.println("Exception occured during action running.");
			exc.printStackTrace();
		} finally {
			Tools.getProgramRoot().setLoading(null);
		}
	}
	
	public void act() {
		// Do stuff here //
	}
	
	public Object getRecentSource() {
		if (recentEvent != null) {
			return recentEvent.getSource();
		} else {
			return null;
		}
	}
}