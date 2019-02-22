package pepband3.gui.dialog;

import java.awt.*;
import javax.swing.*;
import pepband3.gui.*;

public class ProgressDialog extends JDialog {
	
	private JDialog progressDialog;
		private JLabel iconLabel;
		private JLabel textLabel;
	
	public ProgressDialog() {
		super(Tools.getProgramRoot(), "Please Wait", false);
        setUndecorated(true);
        setResizable(false);
        setAlwaysOnTop(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		a2Components();
		a4Layouts();
	}
	
	private void a2Components() {
		JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		setContentPane(contentPane);
		
		iconLabel = new JLabel();
		iconLabel.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
		textLabel = new JLabel();
		textLabel.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout());
		
		add(iconLabel, BorderLayout.WEST);
		add(textLabel, BorderLayout.EAST);
	}
	
	public void display(ImageIcon icon, String text) {
		iconLabel.setIcon(icon);
		textLabel.setText(text);
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}
}