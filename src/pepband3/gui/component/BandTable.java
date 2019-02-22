package pepband3.gui.component;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.tab.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public class BandTable extends JPanel implements IconDisplayer {
	
	private JPopupMenu popupMenu;
		private JMenuItem openItem;
		private JMenuItem editItem;
		private JMenuItem cutItem;
		private JMenuItem copyItem;
		private JMenuItem pasteItem;
		private JMenuItem removeItem;
		private JMenuItem selectAllItem;
	
	private JSplitPane splitPane;
	private JScrollPane tableScrollPane;
		private JTable table;
	private JTabbedPane tabbedPane;
	
	private TreeSet<BandTableTab> armedTabs;
	private TreeSet<BandTableTab> tabSet;
	
	private ViewManager viewManager;
	
	public BandTable(Band paramBand, ViewType paramViewType) {
		super();
		
		a2Components(paramBand);
		a3Listeners();
		a4Layouts();
		a5Initialize(paramViewType);
	}
	
	private void a2Components(Band paramBand) {
		popupMenu = new JPopupMenu("Band Table Popup");
		openItem = new JMenuItem(Tools.getProgramRoot().getActionMap().get("Open Member"));
		editItem = new JMenuItem(Tools.getProgramRoot().getActionMap().get("Edit Member"));
		cutItem = new JMenuItem(Tools.getProgramRoot().getActionMap().get("Cut"));
		copyItem = new JMenuItem(Tools.getProgramRoot().getActionMap().get("Copy"));
		pasteItem = new JMenuItem(Tools.getProgramRoot().getActionMap().get("Paste"));
		removeItem = new JMenuItem(Tools.getProgramRoot().getActionMap().get("Remove"));
		selectAllItem = new JMenuItem(Tools.getProgramRoot().getActionMap().get("Select All"));
		Tools.addRootComponent(popupMenu);
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPane.setResizeWeight(1);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(splitPane.getWidth() - 200);
		
		BandTableModel model = new BandTableModel(paramBand, 3);
		table = new JTable(model);
		table.setTransferHandler(new DataTransferHandler());
		table.setFillsViewportHeight(true);
		table.setDragEnabled(true);
		table.setComponentPopupMenu(popupMenu);
		table.setSelectionModel(new BandTableSelectionModel(this));
		table.setDropMode(DropMode.ON);
		
		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setMinimumSize(new Dimension(200,100));
		tableScrollPane.setPreferredSize(new Dimension(200,100));
		
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM,JTabbedPane.WRAP_TAB_LAYOUT);
		tabbedPane.setMinimumSize(new Dimension(200,100));
		tabbedPane.setPreferredSize(new Dimension(200,100));
		
		Tools.applyScrollPopup(tableScrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(tableScrollPane.getVerticalScrollBar());
	}
	
	private void a3Listeners() {
		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
             		int row = table.rowAtPoint(e.getPoint());
             		if (row >= 0) {
             			table.setRowSelectionInterval(row, row);
             			Tools.getProgramRoot().getActionMap().get("Open Member").actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
             		}
				} else if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) {
					int row = table.rowAtPoint(e.getPoint());
					if (row >= 0 && table.getSelectedRows().length <= 1) {
						table.setRowSelectionInterval(row, row);
					}
				}
			}
		});
		table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					int selectedRows = table.getSelectedRowCount();
					if(selectedRows > 0) {
						Tools.getProgramRoot().getActionMap().get("Open Member").actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
						e.consume();
					}
				}
			}
		});
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout());
		
		splitPane.setLeftComponent(tableScrollPane);
		splitPane.setRightComponent(tabbedPane);
		
		add(splitPane,BorderLayout.CENTER);
		
		popupMenu.add(openItem);
		popupMenu.add(editItem);
		popupMenu.addSeparator();
		popupMenu.add(cutItem);
		popupMenu.add(copyItem);
		popupMenu.add(pasteItem);
		popupMenu.add(removeItem);
		popupMenu.add(selectAllItem);
	}
	
	private void a5Initialize(ViewType paramViewType) {
		armedTabs = new TreeSet<BandTableTab>();
		tabSet = new TreeSet<BandTableTab>();
		setShowHorizontalGridLines(Tools.getBoolean("Show BandTable Horizontal Grid Lines",true));
		setShowVerticalGridLines(Tools.getBoolean("Show BandTable Vertical Grid Lines",true));
		
		viewManager = new ViewManager(table,paramViewType,Enum.valueOf(DataField.class,Tools.getProperty("Name Data Field")));
	}
	
	public void armEditingTab() {
		EditingTab editingTab = new EditingTab(this);
		armedTabs.add(editingTab);
	}
	
	public void armAdditionTab(Band seasonBand) {
		AdditionTab additionTab = new AdditionTab(seasonBand, getBand(), viewManager.getNameField());
		armedTabs.add(additionTab);
	}
	
	public void armFilterTab() {
		FilterTab filterTab = new FilterTab(getViewManager(),getBand());
		armedTabs.add(filterTab);
	}
	
	public void armOverviewTab() {
		OverviewTab overviewTab = new OverviewTab(getBand());
		armedTabs.add(overviewTab);
	}
	
	public Band getBand() {
		return ((BandTableModel)table.getModel()).getBand();
	}
	
	public BandTableModel getBandTableModel() {
		return (BandTableModel)table.getModel();
	}
	
	public ArrayList<Member> getSelectedMembers() {
		ArrayList<Member> selectedMembers = new ArrayList<Member>();
		for (int index = 0; index < table.getSelectedRows().length; index++) {
			selectedMembers.add(getBand().getMembers().get(table.convertRowIndexToModel(table.getSelectedRows()[index])));
		}
		return selectedMembers;
	}
	
	public JTable getTable() {
		return table;
	}
	
	public TreeSet<BandTableTab> getTabSet() {
		return tabSet;
	}
	
	public AdditionTab getAdditionTab() {
		for (BandTableTab tab : getTabSet()) {
			if (tab instanceof AdditionTab) {
				return (AdditionTab)tab;
			}
		}
		return null;
	}
	
	public EditingTab getEditingTab() {
		for (BandTableTab tab : getTabSet()) {
			if (tab instanceof EditingTab) {
				return (EditingTab)tab;
			}
		}
		return null;
	}
	
	public ViewManager getViewManager() {
		return viewManager;
	}
	
	public int highlight(DataField fieldValue, Comparable value) {
		if (fieldValue == null || value == null) {
			return 0;
		}
		ListSelectionModel selectionModel = table.getSelectionModel();
		selectionModel.clearSelection();
		int selectCount = 0;
		for (Member member : getBand().getMembers()) {
			int index = -1;
			if (fieldValue == DataField.INSTRUMENT) {
				if (member.getInstrument().equals(value)) {
					index = table.convertRowIndexToView(getBand().indexOfMember(member));
				}
			} else if (fieldValue == DataField.CLASS_YEAR) {
				if (member.getClassYear().equals(value)) {
					index = table.convertRowIndexToView(getBand().indexOfMember(member));
				}
			} else if (fieldValue == DataField.POINTS) {
				if (value instanceof StatisticsWindow.PointRange) {
					StatisticsWindow.PointRange range = (StatisticsWindow.PointRange)value;
					if (member.getPoints(getBand()) >= range.getStartValue() && member.getPoints(getBand()) <= range.getEndValue()) {
						index = table.convertRowIndexToView(getBand().indexOfMember(member));
					}
				} else {
					if (member.getPoints(getBand()).equals(value)) {
						index = table.convertRowIndexToView(getBand().indexOfMember(member));
					}
				}
			}
			if (index >= 0) {
				selectionModel.addSelectionInterval(index,index);
				selectCount++;
			}
		}
		return selectCount;
	}
	
	public void installArmedTabs() {
		for (BandTableTab tab : armedTabs) {
			tabbedPane.addTab(tab.getTabName(),null,tab,tab.getToolTipText());
			tabSet.add(tab);
		}
		armedTabs.clear();
		setIconSize(Tools.getInteger("BandTable Icon Size",32));
		setShowIconText(Tools.getBoolean("Show BandTable Icon Text",false));
	}
	
	public void reinstallTabs() {
		for (BandTableTab tab : tabSet) {
			armedTabs.add(tab);
		}
		tabSet.clear();
		tabbedPane.removeAll();
		installArmedTabs();
	}
	
	public void setIconSize(Integer value) {
		for (int index = 0; index < tabbedPane.getTabCount(); index++) {
			Component component = tabbedPane.getComponentAt(index);
			if (component instanceof BandTableTab) {
				BandTableTab tab = (BandTableTab)component;
				tabbedPane.setIconAt(tabbedPane.indexOfComponent(tab),Tools.getIcon(tab.getTabIconName() + value + ""));
			}
		}
	}
	
	public void setShowHorizontalGridLines(boolean value) { 
		table.setShowHorizontalLines(value);
		table.setRowMargin(value ? 1 : 0);
	}
	
	public void setShowIconText(boolean value) {
		for (int index = 0; index < tabbedPane.getTabCount(); index++) {
			Component component = tabbedPane.getComponentAt(index);
			if (component instanceof BandTableTab) {
				BandTableTab tab = (BandTableTab)component;
				tabbedPane.setTitleAt(tabbedPane.indexOfComponent(tab),value ? tab.getTabName() : null);
			}
		}
	}
	
	public void setShowVerticalGridLines(boolean value) {
		table.setShowVerticalLines(value);
		table.getColumnModel().setColumnMargin(value ? 1 : 0);
	}
	
	public void uninstall() {
		for (BandTableTab tab : tabSet) {
			if (tab instanceof FilterTab) {
				((FilterTab)tab).uninstall();
			}else if (tab instanceof OverviewTab) {
				((OverviewTab)tab).uninstall();
			}
		}
		tabSet.clear();
		tabbedPane.removeAll();
		((BandTableModel)table.getModel()).uninstall();
	}
}