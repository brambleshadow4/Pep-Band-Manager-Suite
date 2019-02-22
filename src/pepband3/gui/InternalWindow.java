package pepband3.gui;

import javax.swing.*;
import javax.swing.event.*;

public abstract class InternalWindow extends JInternalFrame {
	
	public InternalWindow(String title, boolean a, boolean b, boolean c, boolean d) {
		super(title, a, b, c, d);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
				dispose();
			}
			
			public void internalFrameClosed(InternalFrameEvent e) {
				closingOperations();
			}
		});
	}
	
	protected void closingOperations() {
		Tools.getDesktopPane().removeWindow(this);
	}
	
	public void display() {
		if (!isVisible()) {
			pack();
			setVisible(true);
		}
		try {
			if (isIcon()) {
				setIcon(false);
			}
			if (!isSelected()) {
				setSelected(true);
			}
		} catch (Exception exc) {
			System.err.println("Internal Window could not fully display iteself");
			exc.printStackTrace();
		}
	}
	
	public void display(boolean select) {
		if (!isVisible()) {
			pack();
			setVisible(true);
		}
		if (select) {
			try {
				if (isIcon()) {
					setIcon(false);
				}
				if (!isSelected()) {
					setSelected(true);
				}
			} catch (Exception exc) {
				System.err.println("Internal Window could not fully display iteself");
				exc.printStackTrace();
			}
		}
	}
	
	public void minimize() {
		if (!isVisible()) {
			pack();
			setVisible(true);
		}
		try {
			setSelected(true);
			setIcon(true);
		} catch (Exception exc) {
			System.err.println("Internal Window could not be minimized");
			exc.printStackTrace();
		}
	}
}