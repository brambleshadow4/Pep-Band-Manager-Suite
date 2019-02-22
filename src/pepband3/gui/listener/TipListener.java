package pepband3.gui.listener;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import pepband3.gui.*;

public class TipListener extends MouseMotionAdapter {
	
	private AppWindow appWindow;
	
	public TipListener(AppWindow value) {
		appWindow = value;
	}
	
	public void applyToChildren(Component component) {
		if (component instanceof JList || component instanceof JTable || component instanceof JComboBox) {
			applyToComponent(component);
		} else if (component instanceof JMenu) {
			Component[] menuComponents = ((JMenu)component).getMenuComponents();
			for(Component menuComponent: menuComponents) {
				applyToChildren(menuComponent);
			}
			applyToComponent(component);
		} else if (component instanceof Container) {
			Component[] subComponents = ((Container)component).getComponents();
			for(Component subComponent: subComponents) {
				applyToChildren(subComponent);
			}
			applyToComponent(component);
		} else {
			applyToComponent(component);
		}
	}
	
	private void applyToComponent(Component component) {
		component.removeMouseMotionListener(this);
		component.addMouseMotionListener(this);
	}
	
	public void mouseMoved(MouseEvent e) {
		String text;
		Icon icon;
		if (e.getSource() instanceof JMenu) {
			JMenu source = (JMenu)e.getSource();
			text = source.getText() + " Menu";
			icon = null;
		} else if (e.getSource() instanceof AbstractButton) {
			AbstractButton source = (AbstractButton)e.getSource();
			if (source.getAction() != null) {
				String actionLongDescription = (String)source.getAction().getValue(Action.LONG_DESCRIPTION);
				String actionShortDescription = (String)source.getAction().getValue(Action.SHORT_DESCRIPTION);
				if (actionLongDescription != null && !actionLongDescription.trim().isEmpty()) {
					text = actionLongDescription;
				} else if (actionShortDescription != null && !actionShortDescription.trim().isEmpty()) {
					text = actionShortDescription;
				} else if (source.getToolTipText() != null && !source.getToolTipText().trim().isEmpty()) {
					text = source.getToolTipText();
				} else {
					text = source.getText();
				}
				icon = (Icon)source.getAction().getValue(Action.SMALL_ICON);
			} else if (source.getToolTipText() != null && !source.getToolTipText().trim().isEmpty()) {
				text = source.getToolTipText();
				icon = null;
			} else {
				text = source.getText();
				icon = null;
			}
		} else if (e.getSource() instanceof JInternalFrame) {
			text = null;
			icon = null;
		} else if (e.getSource() instanceof JComponent) {
			JComponent source = (JComponent)e.getSource();
			if (source.getToolTipText() != null && !source.getToolTipText().isEmpty()) {
				text = source.getToolTipText();
				icon = null;
				
			} else if (source.getClientProperty("tip") != null && !((String)source.getClientProperty("tip")).isEmpty()) {
				text = (String)source.getClientProperty("tip");
				icon = null;
				
			} else {
				text = null;
				icon = null;
			}
		} else {
			text = null;
			icon = null;
		}
		appWindow.setTip(text,icon);
	}
}