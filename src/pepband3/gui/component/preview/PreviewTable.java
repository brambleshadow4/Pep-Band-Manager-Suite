package pepband3.gui.component.preview;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import pepband3.*;
import pepband3.gui.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public abstract class PreviewTable extends JPanel {
	
	private JPanel northPanel;
		private JButton colorButton;
		protected JPanel optionsPanel;
		
	private JPanel previewPanel;
		private JScrollPane scrollPane;
		private JPanel tablePanel;
			protected JTable headerTable;
			protected JTable memberTable;
			protected PreviewRenderer headerRenderer;
			protected PreviewRenderer memberRenderer;
	
	private Action colorAction;
	
	public PreviewTable() {
		super();
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		colorAction = new RunnableAction("Color...","Choosing text color") {
			public void act() {
				Color choice = JColorChooser.showDialog(previewPanel, "Choose Text Color", memberRenderer.getForegroundColor());
				if (choice != null) {
					headerRenderer.setForegroundColor(choice);
					memberRenderer.setForegroundColor(choice);
					Tools.setProperty("Preview Text Color",Integer.toString(choice.getRGB()));
				}
				headerTable.repaint();
				memberTable.repaint();
			}
		};
		
		colorAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		colorAction.putValue(Action.LONG_DESCRIPTION,"Choose the foreground color of the text for exporting or printing");
		colorAction.putValue(Action.SHORT_DESCRIPTION,"Choose foreground color of text");
	}
	
	private void a2Components() {
		northPanel = new JPanel();
		optionsPanel = new JPanel();
		
		colorButton = new JButton(colorAction);
		
		headerRenderer = new PreviewRenderer(DataField.NAME_FIRST_LAST);
		memberRenderer = new PreviewRenderer(DataField.NAME_FIRST_LAST);
		
		previewPanel = new JPanel();
		tablePanel = new JPanel();
		scrollPane = new JScrollPane(tablePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		
		headerTable = new JTable();
		headerTable.setName("HEADER_TABLE");
		headerTable.setShowGrid(false);
		headerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		headerTable.setTableHeader(null);
		
		memberTable = new JTable();
		memberTable.setName("MEMBER_TABLE");
		memberTable.setShowGrid(false);
		memberTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		memberTable.setSelectionModel(new AntiSelectionModel());
		memberTable.setTableHeader(null);
		
		Tools.applyScrollPopup(scrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(scrollPane.getVerticalScrollBar());
	}
	
	private void a3Listeners() {
		
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout());
		northPanel.setLayout(new GridBagLayout());
		previewPanel.setLayout(new GridBagLayout());
		tablePanel.setLayout(new BorderLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 0; c.weighty = 0;
		c.insets = new Insets(GUIManager.INS, GUIManager.INS, GUIManager.INS, GUIManager.INS);
		
		c.gridx = 0; c.gridy = 0;
		northPanel.add(new JLabel("Text Color:"), c);
		c.gridx = 0; c.gridy = 1;
		northPanel.add(colorButton, c);
		c.gridwidth = 1; c.gridheight = 2;
		c.weightx = 1; c.weighty = 0;
		c.gridx = 1; c.gridy = 0;
		northPanel.add(optionsPanel, c);
		
		tablePanel.add(headerTable, BorderLayout.NORTH);
		tablePanel.add(memberTable, BorderLayout.CENTER);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 0; c.weighty = 0;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		c.gridwidth = 3;
		c.weightx = 0.5; c.weighty = 0.5;
		c.gridx = 0; c.gridy = 0;
		previewPanel.add(Box.createVerticalGlue(), c);
		c.gridwidth = 1;
		c.weightx = 0.5; c.weighty = 0;
		c.gridx = 0; c.gridy = 1;
		previewPanel.add(Box.createHorizontalGlue(), c);
		c.weightx = 0; c.weighty = 0;
		c.gridx = 1; c.gridy = 1;
		previewPanel.add(scrollPane, c);
		c.weightx = 0.5; c.weighty = 0;
		c.gridx = 2; c.gridy = 1;
		previewPanel.add(Box.createHorizontalGlue(), c);
		c.gridwidth = 3;
		c.weightx = 0.5; c.weighty = 0.5;
		c.gridx = 0; c.gridy = 2;
		previewPanel.add(Box.createVerticalGlue(), c);
		
		add(northPanel, BorderLayout.NORTH);
		add(previewPanel, BorderLayout.CENTER);
	}
	
	private void a5Initialize() {
		headerTable.putClientProperty("tip","Preview is \"What you see is what you get\", however it does not show margins. To change paper size or margins, click the page setup button.");
		memberTable.putClientProperty("tip","Preview is \"What you see is what you get\", however it does not show margins. To change paper size or margins, click the page setup button.");
		
		setPreviewPageSize();
	}
	
	public JTable getHeaderTable() {
		return headerTable;
	}
	
	public JTable getMemberTable() {
		return memberTable;
	}
	
	public void setPreviewPageSize() {
		scrollPane.setPreferredSize(new Dimension((int) IE.getPageFormat().getImageableWidth(),(int) IE.getPageFormat().getImageableHeight()));
		scrollPane.setMinimumSize(new Dimension((int) IE.getPageFormat().getImageableWidth(),(int) IE.getPageFormat().getImageableHeight()));
		scrollPane.setMaximumSize(new Dimension((int) IE.getPageFormat().getImageableWidth(),(int) IE.getPageFormat().getImageableHeight()));
	}
	
	protected abstract void setTables();
}