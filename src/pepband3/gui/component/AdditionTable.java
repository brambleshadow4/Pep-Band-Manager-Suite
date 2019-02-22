package pepband3.gui.component;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public class AdditionTable extends JPanel {
	
	private JPanel searchPanel;
	private SearchField searchField;
	private JScrollPane scrollPane;
	private JTable table;
	
	private JPopupMenu popupMenu;
		private JMenuItem addItem;
		private JMenuItem openItem;;
		private JMenuItem copyItem;
		private JMenuItem selectAllItem;
	
	private ViewManager viewManager;
	
	private Band targetBand;
	private BandTableModel model;
	
	private Action addAction, openAction, copyAction, selectAllAction;
	
	public AdditionTable(Band paramInitRosterBand, Band paramTargetBand, DataField paramInitNameField) {
		targetBand = paramTargetBand;
		
		a1Actions();
		a2Components(paramInitRosterBand, paramInitNameField);
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		addAction = new AbstractAction("Add") {
			public void actionPerformed(ActionEvent e) {
				addSelectedMembers();
            	searchField.getTextField().requestFocusInWindow();
			}
		};
		openAction = new AbstractAction("Open") {
			public void actionPerformed(ActionEvent e) {
				for (Member member : getSelectedMembers()) {
					Tools.getDesktopPane().addMemberWindow(member).display();
				}
			}
		};
		copyAction = new AbstractAction("Copy") {
			public void actionPerformed(ActionEvent e) {
				TransferHandler.getCopyAction().actionPerformed(new ActionEvent(table,ActionEvent.ACTION_PERFORMED,e.getActionCommand()));
			}
		};
		selectAllAction = new AbstractAction("Select All") {
			public void actionPerformed(ActionEvent e) {
				table.selectAll();
			}
		};
		
		addAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_M);
		addAction.putValue(Action.SMALL_ICON,Tools.getIcon("add16"));
		addAction.putValue(Action.LONG_DESCRIPTION,"Add the selected members to the band");
		addAction.putValue(Action.SHORT_DESCRIPTION,"Add selected members");
		
		openAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_O);
		openAction.putValue(Action.SMALL_ICON,Tools.getIcon("member16"));
		openAction.putValue(Action.LONG_DESCRIPTION,"Open the selected members in individual member windows");
		openAction.putValue(Action.SHORT_DESCRIPTION,"Open members");
		
		copyAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		copyAction.putValue(Action.SMALL_ICON,Tools.getIcon("copy16"));
		copyAction.putValue(Action.LONG_DESCRIPTION,"Copy the selected members onto clipboard");
		copyAction.putValue(Action.SHORT_DESCRIPTION,"Copy members");
		
		selectAllAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		selectAllAction.putValue(Action.LONG_DESCRIPTION,"Select all members currently showing in the list");
		selectAllAction.putValue(Action.SHORT_DESCRIPTION,"Select all members");
	}
	
	private void a2Components(Band paramInitRosterBand, DataField paramInitNameField) {
		popupMenu = new JPopupMenu("Roster List Popup");
		addItem = new JMenuItem(addAction);
		openItem = new JMenuItem(openAction);
		copyItem = new JMenuItem(copyAction);
		selectAllItem = new JMenuItem(selectAllAction);
		Tools.addRootComponent(popupMenu);
		
		model = new BandTableModel(paramInitRosterBand, 1);
		table = new JTable(model);
		table.setTransferHandler(new DataTransferHandler());
		table.setDragEnabled(true);
		table.setFillsViewportHeight(true);
		table.setTableHeader(null);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0,0));
		table.setComponentPopupMenu(popupMenu);
		scrollPane = new JScrollPane(table);
		scrollPane.setViewportBorder(BorderFactory.createLoweredBevelBorder());
		scrollPane.setBorder(OptionsDialog.createTitledBorder("Season Roster List"));
		
		viewManager = new ViewManager(table,ViewType.LIST,paramInitNameField);
		
		searchPanel = new JPanel();
		searchPanel.setBorder(OptionsDialog.createTitledBorder("Search for Members"));
		searchField = new SearchField(viewManager, this);
		
		Tools.applyScrollPopup(scrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(scrollPane.getVerticalScrollBar());
	}
	
	private void a3Listeners() {
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
             		int row = table.rowAtPoint(e.getPoint());
             		if (row >= 0) {
             			table.setRowSelectionInterval(row, row);
             			addSelectedMembers();
             		}
				} else if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) {
					int row = table.rowAtPoint(e.getPoint());
					if (row >= 0 && table.getSelectedRows().length <= 1) {
						table.setRowSelectionInterval(row, row);
					}
				}
			}
		});
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (((ListSelectionModel)e.getSource()).isSelectionEmpty()) {
						setActionsEnabled(false);
					} else {
						setActionsEnabled(true);
					}
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (e.getComponent() == table) {
					if (key == KeyEvent.VK_ENTER) {
						addSelectedMembers();
						e.consume();
					}
				}
			}
		});
	}
	
	private void a4Layouts() {
		setLayout(new GridBagLayout());
		searchPanel.setLayout(new BorderLayout());
		
		searchPanel.add(searchField,BorderLayout.CENTER);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 0;
		c.gridwidth = 1; c.gridheight = 1;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		c.gridx = 0; c.gridy = 0;
		add(searchPanel,c);
		c.gridx = 0; c.gridy = 1;
		add(new JSeparator(SwingConstants.HORIZONTAL));
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 2;
		add(scrollPane,c);
		
		popupMenu.add(addItem);
		popupMenu.addSeparator();
		popupMenu.add(openItem);
		popupMenu.addSeparator();
		popupMenu.add(copyItem);
		popupMenu.add(selectAllItem);
	}
	
	private void a5Initialize() {
		table.putClientProperty("tip","To add a member from the roster list, select one and double click, press enter, or right click and choose the add button in the popup menu");
		setActionsEnabled(false);
	}
	
	public void addSelectedMembers() {
		for (int row : table.getSelectedRows()) {
			if (targetBand != null) {
				targetBand.addMember((Member)table.getValueAt(row, 0));
			}
		}
	}
	
	public BandTableModel getBandTableModel() {
		return model;
	}
	
	public ArrayList<Member> getSelectedMembers() {
		ArrayList<Member> selectedMembers = new ArrayList<Member>();
		for (int index = 0; index < table.getSelectedRows().length; index++) {
			selectedMembers.add(model.getBand().getMembers().get(table.convertRowIndexToModel(table.getSelectedRows()[index])));
		}
		return selectedMembers;
	}
	
	public JTable getTable() {
		return table;
	}
	
	public ViewManager getViewManager() {
		return viewManager;
	}
	
	private void setActionsEnabled(boolean value) {
		addAction.setEnabled(value);
		openAction.setEnabled(value);
		copyAction.setEnabled(value);
	}
	
	public void setTargetBand(Band value) {
		targetBand = value;
	}
}