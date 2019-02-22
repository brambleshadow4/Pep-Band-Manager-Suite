package pepband3.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import pepband3.gui.*;

public class IconChooser extends JDialog {
	
	private static final String ICON_SIZE = "48";
	
	private String choice;
	
	private Action buttonAction;
	
	public IconChooser(JDialog owner, TreeMap<String,ImageIcon> iconMap) {
		super(owner,"Choose Icon",true);
		
		setIconImages(Tools.getWindowIcons());
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);
		setUndecorated(false);
		
		a1Actions();
		a2ComponentsAndLayout(iconMap);
	}
	
	private void a1Actions() {
		buttonAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				choice = ((JButton)e.getSource()).getText();
				setVisible(false);
			}
		};
		
		buttonAction.putValue(Action.LONG_DESCRIPTION,"Choose this icon");
		buttonAction.putValue(Action.SHORT_DESCRIPTION,"Choose this icon");
	}
	
	private void a2ComponentsAndLayout(TreeMap<String,ImageIcon> iconMap) {
		setLayout(new GridLayout(4,4,4,4));
		for (String key : iconMap.keySet()) {
			if (key.endsWith(ICON_SIZE)) {
				String name = key.substring(0,key.length() - ICON_SIZE.length());
				JButton button = new JButton(buttonAction);
				button.setIcon(iconMap.get(key));
				button.setText(name);
				button.setHorizontalTextPosition(SwingConstants.CENTER);
				button.setVerticalTextPosition(SwingConstants.BOTTOM);
				add(button);
			}
		}
	}
	
	public void display() {
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}
	
	public String getChoice() {
		return choice;
	}
	
	public void setChoice(String value) {
		choice = value;
	}
}