package pepband3.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import pepband3.data.*;
import pepband3.gui.component.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.extra.*;
import pepband3.gui.model.*;

public class SeasonWindow extends InternalTableWindow implements IconDisplayer {
	
	private JPopupMenu popupMenu;
		private JMenuItem openItem;
		private JMenuItem cloneItem;
		private JMenuItem deleteItem;
		private JMenuItem expandAllItem;
		private JMenuItem collapseAllItem;
	
	private GradientPanel northPanel;
	private JTabbedPane tabbedPane;
	private JPanel eventsPanel;
	private JScrollPane scrollPane;
	private JTree eventTree;
	private JPanel editingPanel;
		private JButton newButton;
		private JButton openButton;
		private JButton cloneButton;
		private JButton deleteButton;
		private JSeparator separator;
		private JButton expandAllButton;
		private JButton collapseAllButton;
		private JComponent glue;
	
	private RunnableAction newAction, openAction, cloneAction, deleteAction, expandAllAction, collapseAllAction;
	
	private Season season;
	
	public SeasonWindow(Season paramSeason) {
		super("Season of " + paramSeason.getStartingYear() + " - " + (paramSeason.getStartingYear() + 1), true, true, true, true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setFrameIcon(Tools.getIcon("seasons16"));
		
		season = paramSeason;
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		newAction = new RunnableAction("New Event","Creating a new event window") {
			public void act() {
				Tools.getProgramRoot().getActionMap().get("New Event").act();
			}
		};
		openAction = new RunnableAction("Open Event","Opening the selected events") {
			public void act() {
				TreePath[] selectedPaths = eventTree.getSelectionPaths();
				if (selectedPaths != null) {
					for (TreePath selectedPath : selectedPaths) {
						if (selectedPath.getLastPathComponent() instanceof PepBandEvent) {
							Tools.getDesktopPane().addEventWindow((PepBandEvent)selectedPath.getLastPathComponent()).display();
						}
					}
				}
			}
		};
		cloneAction = new RunnableAction("Clone Event","Cloning selected event") {
			public void act() {
				TreePath[] selectedPaths = eventTree.getSelectionPaths();
				if (selectedPaths != null) {
					for (TreePath selectedPath : selectedPaths) {
						if (selectedPath.getLastPathComponent() instanceof PepBandEvent) {
							PepBandEvent clone = ((PepBandEvent)selectedPath.getLastPathComponent()).cloneEvent();
							season.addEvent(clone);
						}
					}
				}
			}
		};
		deleteAction = new RunnableAction("Delete Event","Deleting the selected events") {
			public void act() {
				ArrayList<PepBandEvent> eventsToDelete = new ArrayList<PepBandEvent>();
				TreePath[] selectedPaths = eventTree.getSelectionPaths();
				if (selectedPaths != null) {
					for (TreePath selectedPath : selectedPaths) {
						if (selectedPath.getLastPathComponent() instanceof PepBandEvent) {
							eventsToDelete.add((PepBandEvent)selectedPath.getLastPathComponent());
						}
					}
				}
				if (!eventsToDelete.isEmpty()) {
					String[] message = new String[2];
					String[] options = {"Delete Event","Nevermind"};
					if (eventsToDelete.size() == 1) {
						message[0] = "Are you sure you want to delete " + eventsToDelete.get(0).getName() + "?";
						message[1] = "This event will be perminantly removed from the season, and any open windows pertaining to it will be closed";
					} else {
						message[0] = "Are you sure you want to delete these " + eventsToDelete.size() + " events?";
						message[1] = "They will be perminantly removed from the season, and any open windows pertaining to them will be closed";
						options[0] = "Delete Events";
					}
					int value = JOptionPane.showOptionDialog(Tools.getProgramRoot(),message,"Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE,null,options,options[1]);
					if(value == JOptionPane.YES_OPTION) {
						for (PepBandEvent event : eventsToDelete) {
							deleteEvent(event);
						}
					}
				}
			}
			
			private void deleteEvent(PepBandEvent event) {
				season.removeEvent(event);
				EventWindow window = Tools.getDesktopPane().getEventWindow(event);
				StatisticsWindow windowStat = Tools.getDesktopPane().getStatisticsWindow(event);
				if (window != null) {
					window.doDefaultCloseAction();
				}
				if (windowStat != null) {
					windowStat.doDefaultCloseAction();
				}
			}
		};
		expandAllAction = new RunnableAction("Expand All","Expanding all event types") {
			public void act() {
				for (EventType eventType : DataManager.getDataManager().getEventTypes()) {
					Object[] path = {season, eventType};
					TreePath treePath = new TreePath(path);
					eventTree.expandPath(treePath);
				}
			}
		};
		collapseAllAction = new RunnableAction("Collapse All","Collapsing all event types") {
			public void act() {
				for (EventType eventType : DataManager.getDataManager().getEventTypes()) {
					Object[] path = {season, eventType};
					TreePath treePath = new TreePath(path);
					eventTree.collapsePath(treePath);
				}
			}
		};
		
		newAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_DOWN_MASK));
		newAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_N);
		newAction.putValue(Action.SMALL_ICON,Tools.getIcon("new16"));
		newAction.putValue(Action.LONG_DESCRIPTION,"Create a new event");
		newAction.putValue(Action.SHORT_DESCRIPTION,"Create event");
		
		openAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_O);
		openAction.putValue(Action.SMALL_ICON,Tools.getIcon("openevent16"));
		openAction.putValue(Action.LONG_DESCRIPTION,"Open selected event in an event window");
		openAction.putValue(Action.SHORT_DESCRIPTION,"Open selected event");
		
		cloneAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_L);
		cloneAction.putValue(Action.LONG_DESCRIPTION,"Make a single duplicate of each selected event and add the duplicates to the season");
		cloneAction.putValue(Action.SHORT_DESCRIPTION,"Clone selected events");
		
		deleteAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
		deleteAction.putValue(Action.LONG_DESCRIPTION,"Perminantly delete the selected event");
		deleteAction.putValue(Action.SHORT_DESCRIPTION,"Delete selected event");
		
		expandAllAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
		expandAllAction.putValue(Action.LONG_DESCRIPTION,"Expand all event types to show every event in the season");
		expandAllAction.putValue(Action.SHORT_DESCRIPTION,"Expand all event types");
		
		collapseAllAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		collapseAllAction.putValue(Action.LONG_DESCRIPTION,"Collapse all event types to hide every event in the season");
		collapseAllAction.putValue(Action.SHORT_DESCRIPTION,"Collapse all event types");
		
		openAction.setEnabled(false);
		deleteAction.setEnabled(false);
		cloneAction.setEnabled(false);
	}
	
	private void a2Components() {
		setMinimumSize(new Dimension(450,275));
		setPreferredSize(new Dimension(750,600));
		setTransferHandler(new DataTransferHandler());
		
		popupMenu = new JPopupMenu("Event Tree Popup");
		openItem = new JMenuItem(openAction);
		cloneItem = new JMenuItem(cloneAction);
		deleteItem = new JMenuItem(deleteAction);
		expandAllItem = new JMenuItem(expandAllAction);
		collapseAllItem = new JMenuItem(collapseAllAction);
		
		northPanel = new GradientPanel(season.getStartingYear() + " - " + (season.getStartingYear() + 1) + " Season", getNorthPanelIcon());
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		
		eventsPanel = new JPanel();
		
		eventTree = new JTree(new EventsTreeModel(season));
		eventTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		eventTree.setCellRenderer(new TreeRenderer());
		eventTree.setRootVisible(true);
		eventTree.setComponentPopupMenu(popupMenu);
		scrollPane = new JScrollPane(eventTree);
		
		editingPanel = new JPanel();
		editingPanel.setBorder(OptionsDialog.createTitledBorder("Edit Events"));
		editingPanel.setMinimumSize(new Dimension(200,100));
		editingPanel.setPreferredSize(new Dimension(200,100));
		newButton = new JButton(newAction);
		openButton = new JButton(openAction);
		cloneButton = new JButton(cloneAction);
		deleteButton = new JButton(deleteAction);
		separator = new JSeparator(SwingConstants.HORIZONTAL);
		expandAllButton = new JButton(expandAllAction);
		collapseAllButton = new JButton(collapseAllAction);
		glue = (JComponent)Box.createVerticalGlue();
		
		bandTable = new BandTable(season, ViewType.ROSTER);
		bandTable.armEditingTab();
		bandTable.armFilterTab();
		bandTable.armOverviewTab();
		bandTable.installArmedTabs();
		
		Tools.applyScrollPopup(scrollPane.getHorizontalScrollBar());
		Tools.applyScrollPopup(scrollPane.getVerticalScrollBar());
	}
	
	private void a3Listeners() {
		final SeasonWindow seasonWindow = this;
		eventTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					int selectedRows = eventTree.getSelectionCount();
					if(selectedRows > 0) {
						openAction.actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
					}
				} else if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3) {
					int row = eventTree.getRowForLocation(e.getX(), e.getY());
					if (row >= 0 && row < eventTree.getRowCount() && eventTree.getSelectionCount() <= 1) {
						eventTree.setSelectionRow(row);
					}
				}
			}
		});
		eventTree.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					int selectedRows = eventTree.getSelectionCount();
					if(selectedRows > 0) {
						openAction.actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
						e.consume();
					}
				}
			}
		});
		eventTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				Integer selectedEvents = 0;
				TreePath[] selectedPaths = eventTree.getSelectionPaths();
				if (selectedPaths != null) {
					for (TreePath selectedPath : selectedPaths) {
						if (selectedPath.getLastPathComponent() instanceof PepBandEvent) {
							selectedEvents++;
						}
					}
				}
				if (selectedEvents > 1) {
					openAction.setEnabled(true);
					deleteAction.setEnabled(true);
					cloneAction.setEnabled(true);
					openAction.putValue(Action.NAME,"Open Events");
					deleteAction.putValue(Action.NAME,"Delete Events");
					cloneAction.putValue(Action.NAME,"Clone Events");
				} else if (selectedEvents == 1) {
					openAction.setEnabled(true);
					deleteAction.setEnabled(true);
					cloneAction.setEnabled(true);
					openAction.putValue(Action.NAME,"Open Event");
					deleteAction.putValue(Action.NAME,"Delete Event");
					cloneAction.putValue(Action.NAME,"Clone Event");
				} else {
					openAction.setEnabled(false);
					deleteAction.setEnabled(false);
					cloneAction.setEnabled(false);
					openAction.putValue(Action.NAME,"Open Event");
					deleteAction.putValue(Action.NAME,"Delete Event");
					cloneAction.putValue(Action.NAME,"Clone Event");
				}
			}
		});
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout());
		eventsPanel.setLayout(new BorderLayout());
		editingPanel.setLayout(new GridBagLayout());
		
		popupMenu.add(openItem);
		popupMenu.add(cloneItem);
		popupMenu.add(deleteItem);
		popupMenu.addSeparator();
		popupMenu.add(expandAllItem);
		popupMenu.add(collapseAllItem);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 0;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		int yindex = 0;
		
		c.gridx = 0; c.gridy = yindex; yindex++;
		editingPanel.add(newButton,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		editingPanel.add(openButton,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		editingPanel.add(cloneButton,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		editingPanel.add(deleteButton,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		editingPanel.add(separator,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		editingPanel.add(expandAllButton,c);
		c.gridx = 0; c.gridy = yindex; yindex++;
		editingPanel.add(collapseAllButton,c);
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = yindex; yindex++;
		editingPanel.add(glue,c);
		
		eventsPanel.add(scrollPane,BorderLayout.CENTER);
		eventsPanel.add(editingPanel,BorderLayout.EAST);
		
		tabbedPane.addTab(" Events ",Tools.getIcon("events32"),eventsPanel,"Display the events for this season");
		tabbedPane.addTab(" Band Roster ",Tools.getIcon("band32"),bandTable,"Display the band roster for this season");
		
		add(northPanel,BorderLayout.NORTH);
		add(tabbedPane,BorderLayout.CENTER);
	}
	
	private void a5Initialize() {
		setIconSize(Tools.getInteger("Misc Icon Size",32));
		setShowIconText(Tools.getBoolean("Show Misc Icon Text",true));
	}
	
	protected void closingOperations() {
		((EventsTreeModel) eventTree.getModel()).uninstall();
		super.closingOperations();
		System.gc();
	}
	
	private ImageIcon getNorthPanelIcon() {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			return Tools.getHeaderIcon("quake");
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
			return Tools.getHeaderIcon("allies");
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
			return Tools.getHeaderIcon("soviets");
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
			return Tools.getHeaderIcon("gdi");
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
			return Tools.getHeaderIcon("nod");
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && calendar.get(Calendar.WEEK_OF_MONTH) % 2 == 1) {
			return Tools.getHeaderIcon("wolfenstein");
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && calendar.get(Calendar.WEEK_OF_MONTH) % 2 == 0) {
			return Tools.getHeaderIcon("wraith");
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) == 5) {
			return Tools.getHeaderIcon("halflife2");
		} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			return Tools.getHeaderIcon("halflife");
		} else {
			return null;
		}
	}
	
	public Season getSeason() {
		return season;
	}
	
	public int highlight(DataField fieldValue, Comparable value) {
		viewBandTable();
		return super.highlight(fieldValue,value);
	}
	
	public void highlight(PepBandEvent value) {
		viewEvents();
		Object[] path = {value.getSeason(),value.getEventType(),value};
		TreePath treePath = new TreePath(path);
		eventTree.setSelectionPath(treePath);
		eventTree.scrollPathToVisible(treePath);
	}
	
	public boolean isViewingBandTable() {
		return tabbedPane.getSelectedComponent() == bandTable;
	}
	
	public boolean isViewingEvents() {
		return tabbedPane.getSelectedComponent() == eventsPanel;
	}
	
	public void setShowIconText(boolean value) {
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(eventsPanel),value ? " Events " : null);
		tabbedPane.setTitleAt(tabbedPane.indexOfComponent(bandTable),value ? " Band Roster " : null);
	}
	
	public void setIconSize(Integer value) {
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(eventsPanel),Tools.getIcon("events" + value +""));
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(bandTable),Tools.getIcon("band" + value +""));
	}
	
	public void viewBandTable() {
		tabbedPane.setSelectedComponent(bandTable);
	}
	
	public void viewEvents() {
		tabbedPane.setSelectedComponent(eventsPanel);
	}
}