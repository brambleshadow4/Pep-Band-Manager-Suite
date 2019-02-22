package pepband3.gui.component;

import java.awt.*;
import javax.swing.*;

import pepband3.gui.*;

public class GradientPanel extends JPanel {
	
	private JLabel title;
	private JLabel icon;
	
	public GradientPanel(String paramText, Icon paramIcon) {
		title = new JLabel(paramText);
		title.setHorizontalAlignment(SwingConstants.LEFT);
		title.setFont(Tools.getFont("Lucida Sans Demi Bold.ttf").deriveFont(Font.PLAIN,20));
		icon = new JLabel(paramIcon);
		icon.setHorizontalAlignment(SwingConstants.RIGHT);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 0.5; c.weighty = 1;
		c.insets = new Insets(5 * GUIManager.INS,3 * GUIManager.INS,5 * GUIManager.INS,3 * GUIManager.INS);
		
		c.gridx = 0; c.gridy = 0;
		add(title,c);
		c.gridx = 1; c.gridy = 0;
		add(icon,c);
		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 2; c.gridheight = 1;
		c.weightx = 0.5; c.weighty = 0;
		c.gridx = 0; c.gridy = 1;
		add(new JSeparator(SwingConstants.HORIZONTAL),c);
	}
	
	protected void paintComponent(Graphics gfx) {
		Graphics2D g = (Graphics2D)gfx;
		setOpaque(false);
		
		GradientPaint paint = new GradientPaint(0,0,getBackground().darker(),0,getHeight(),getBackground());
		g.setPaint(paint);
		g.fillRect(0,0,getWidth(),getHeight());
		
		super.paintComponent(g);
	}
	
	public void setTitle(String value) {
		title.setText(value);
	}
}