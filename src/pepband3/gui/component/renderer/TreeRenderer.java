package pepband3.gui.component.renderer;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.text.*;

import pepband3.data.*;
import pepband3.gui.*;

public class TreeRenderer extends DefaultTreeCellRenderer {
	
	private Font bigFont, littleFont;
	private DateFormat dateFormat;
	
	public TreeRenderer() {
		bigFont = new Font("Sans-serif",Font.BOLD,14);
		littleFont = new Font("Sans-serif",Font.PLAIN,12);
		dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)  {
		JLabel component = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value == null) {
			component.setText("<Null Value>");
			component.setIcon(null);
			component.setIconTextGap(4);
			component.setFont(littleFont);
		} else if (value instanceof Season) {
			component.setText("Events of " + ((Season)value).getStartingYear() + " - " + (((Season)value).getStartingYear() + 1) + " Season");
			component.setIcon(null);
			component.setIconTextGap(4);
			component.setFont(bigFont);
		} else if (value.equals(PepBandEvent.class)) {
			component.setText("Events");
			component.setIcon(null);
			component.setIconTextGap(4);
			component.setFont(bigFont);
		} else if (value instanceof EventType) {
			component.setText(((EventType)value).getName());
			component.setIcon(Tools.getEventIcon(((EventType)value).getIconName() + "16"));
			component.setIconTextGap(8);
			component.setFont(bigFont);
		} else if (value instanceof PepBandEvent) {
			PepBandEvent data = (PepBandEvent)value;
			component.setText(dateFormat.format(data.getDate()) + " - " + data.getName());
			if (data.getEventType().getHasLocation()) {
				component.setIcon(Tools.getLocationIcon(data.getLocation().getIconName() + "16"));
			} else {
				component.setIcon(Tools.getIcon("nonlocevent16"));
			}
			component.setIconTextGap(8);
			component.setFont(littleFont);
		} else {
			component.setText("CLASS OF VALUE UNRECOGNIZED");
			component.setIcon(null);
			component.setIconTextGap(4);
			component.setFont(littleFont);
		}
		return component;
	}
}