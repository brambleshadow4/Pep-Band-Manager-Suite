package pepband3.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.model.*;

public class DataDialog extends JDialog {
	
	private GradientPanel northPanel;
	private JScrollPane scrollPane;
		private JTable table;
	private JPanel buttonPanel;
	private JButton closeButton;
	
	private Action closeAction;
	
	public DataDialog() {
		super(Tools.getProgramRoot(),"All Data",true);
		
		setIconImages(Tools.getWindowIcons());
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);
		setUndecorated(false);
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		final DataDialog dataDialog = this;
		closeAction = new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		
		closeAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		closeAction.putValue(Action.LONG_DESCRIPTION,"Close the dialog");
		closeAction.putValue(Action.SHORT_DESCRIPTION,"Close");
	}
	
	private void a2Components() {
		setPreferredSize(new Dimension(450,600));
		northPanel = new GradientPanel("All Data", Tools.getIcon("data48"));
		
		table = new JTable();
		scrollPane = new JScrollPane(table);
		
		buttonPanel = new JPanel();
		closeButton = new JButton(closeAction);
		
		Tools.applyScrollPopup(scrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(scrollPane.getVerticalScrollBar());
	}
	
	private void a3Listeners() {
		
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout(0,0));
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, GUIManager.INS, GUIManager.INS));
		
		buttonPanel.add(closeButton);
		
		add(northPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void a5Initialize() {
			
	}
	
	public void display() {
		AllDataTableModel tableModel = new AllDataTableModel(DataManager.getDataManager().getAllData());
		table.setModel(tableModel);
		for (int index = 0; index < tableModel.getColumnCount(); index++) {
			table.getColumnModel().getColumn(index).setCellRenderer(new TableRenderer(tableModel.getDataField(index), null));
			table.getColumnModel().getColumn(index).setPreferredWidth(tableModel.getColumnWidth(index));
		}
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}
}