package pepband3.gui;

import java.awt.*;
import java.awt.datatransfer .*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import pepband3.*;
import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.component.*;
import pepband3.gui.component.tab.*;
import pepband3.gui.dialog.*;
import pepband3.gui.extra.*;
import pepband3.gui.listener.*;
import pepband3.gui.model.*;

public class AppWindow extends JFrame implements IconDisplayer {
	
	private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor(new ThreadFactory() {
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable,"APP_WINDOW_EXECUTOR_SERVICE");
			thread.setPriority(3);
			return thread;
		}
	});
	private final AppWindow APP_WINDOW = this;
	
	private JMenuBar menuBar;
		private JMenu fileMenu;
			private JMenuItem newItem;
			private JMenuItem openItem;
			private JMenuItem closeItem;
			private JMenuItem closeAllItem;
			private JMenuItem importItem;
			private JMenuItem exportItem;
			private JMenuItem pageSetupItem;
			private JMenuItem printItem;
			private JMenuItem exitItem;
		private JMenu editMenu;
			private JMenuItem cutItem;
			private JMenuItem copyItem;
			private JMenuItem pasteItem;
			private JMenuItem removeItem;
			private JMenuItem selectAllItem;
			private JMenuItem openMemberItem;
			private JMenuItem editMemberItem;
		private JMenu viewMenu;
			private JMenu nameMenu;
				private ButtonGroup nameGroup;
				private JRadioButtonMenuItem nameFirstLastItem;
				private JRadioButtonMenuItem nameItem;
				private JRadioButtonMenuItem fullNameItem;
				private JRadioButtonMenuItem nickNameItem;
			private JMenu viewTypeMenu;
				private ButtonGroup viewTypeGroup;
				private JRadioButtonMenuItem eventViewItem;
				private JRadioButtonMenuItem rosterViewItem;
			private JCheckBoxMenuItem horizontalGridItem;
			private JCheckBoxMenuItem verticalGridItem;
		private JMenu toolsMenu;
			private JMenuItem optionsItem;
		private JMenu windowMenu;
			private JMenuItem cascadeItem;
			private JMenuItem tileHorizontallyItem;
			private JMenuItem tileVerticallyItem;
			private JMenuItem minimizeAllItem;
			private JMenuItem restoreAllItem;
		private JMenu helpMenu;
			private JMenuItem maintenanceItem;
			private JMenuItem allDataItem;
			private JMenuItem resetIDsItem;
			private JMenuItem aboutItem;
	
	private JPanel centerPanel;
		private JToolBar toolBar;
			private JButton newButton;
			private JButton openButton;
			private JButton cutButton;
			private JButton copyButton;
			private JButton pasteButton;
			private JButton exportButton;
			private JButton printButton;
		private AppWindowDesktop desktopPane;
	private JPanel southPanel;
		private JLabel tipLabel;
		private JPanel loadPanel;
			private LoadIcon loadIcon;
			private JLabel loadLabel;
	
	private JFileChooser fileChooser;
		private FileNameExtensionFilter csvFilter;
		private FileNameExtensionFilter pdfFilter;
	
	private ProgressDialog progressDialog;
	private OptionsDialog optionsDialog;
	private PreviewDialog previewDialog;
	private AboutDialog aboutDialog;
	private DataDialog dataDialog;
	
	private HashMap<String,RunnableAction> actionMap;
	private RunnableAction newAction, openAction, closeAction, closeAllAction,
					importAction, exportAction, pageSetupAction, printAction, exitAction;
	private RunnableAction cutAction, copyAction, pasteAction, removeAction, selectAllAction, openMemberAction, editMemberAction;
	private RunnableAction nameFirstLastAction, nameAction, fullNameAction, nickNameAction, eventViewAction, rosterViewAction,
					horizontalGridAction, verticalGridAction;
	private RunnableAction optionsAction;
	private RunnableAction cascadeAction, tileHorizontallyAction, tileVerticallyAction, minimizeAllAction, restoreAllAction;
	private RunnableAction maintenanceAction, allDataAction, resetIDsAction, aboutAction;
	
	private TipListener tipListener;
	private InternalFrameAdapter internalFrameAdapter;
	private ListSelectionListener bandTableSelectionListener;
	
	public AppWindow() {
		super("Pep Band Manager Suite", Tools.determineOriginalScreen());
		
		Tools.setProgramRoot(this);
		Tools.addRootComponent(this);
		
		setIconImages(Tools.getWindowIcons());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(true);
		setUndecorated(false);
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		actionMap = new HashMap<String,RunnableAction>();
		actionMap.put("New Event",newAction = new RunnableAction("New","Creating a new event window",true) {
			public void act() {
				PepBandEvent newEvent = DataManager.getDataManager().getNewEvent();
				SeasonWindow seasonWindow = desktopPane.getSeasonWindow(DataManager.getDataManager().getSeasonForBand(newEvent));
				if (seasonWindow != null) {
					seasonWindow.highlight(newEvent);
				}
				desktopPane.addEventWindow(newEvent).display();
			}
		});
		actionMap.put("Open Current Season",openAction = new RunnableAction("Open","Opening a season window for the current season",true) {
			public void act() {
				SeasonWindow window = desktopPane.addSeasonWindow(DataManager.getDataManager().getCurrentSeason());
				window.display();
				window.viewEvents();
			}
		});
		actionMap.put("Close",closeAction = new RunnableAction("Close","Closing the selected window") {
			public void act() {
				if (desktopPane.getSelectedFrame() != null) {
					desktopPane.getSelectedFrame().doDefaultCloseAction();
				}
			}
		});
		actionMap.put("Close All",closeAllAction = new RunnableAction("Close All","Closing all open windows") {
			public void act() {
				for (JInternalFrame frame : desktopPane.getAllFrames()) {
					frame.doDefaultCloseAction();
				}
			}
		});
		actionMap.put("Import",importAction = new RunnableAction("Import","Preparing to import") {
			public void act() {
				
			}
		});
		actionMap.put("Export",exportAction = new RunnableAction("Export","Preparing to export") {
			public void act() {
				JInternalFrame frame = desktopPane.getSelectedFrame();
				PepBandData data1 = null;
				PepBandData data2 = null;
				PreviewDialog.PreviewMode previewMode = null;
				if (frame instanceof EventWindow) {
					data1 = ((EventWindow) frame).getEvent();
					data2 = null;
					previewMode = PreviewDialog.PreviewMode.EXPORT_EVENT;
				} else if (frame instanceof SeasonWindow) {
					if (((SeasonWindow)frame).isViewingBandTable()) {
						data1 = ((SeasonWindow)frame).getSeason();
						data2 = null;
						previewMode = PreviewDialog.PreviewMode.EXPORT_SEASON_ROSTER;
					} else if (((SeasonWindow)frame).isViewingEvents()) {
						data1 = ((SeasonWindow)frame).getSeason();
						data2 = null;
						previewMode = PreviewDialog.PreviewMode.EXPORT_SEASON_EVENTS;
					}
				} else if (frame instanceof MemberWindow) {
					if (((MemberWindow)frame).isViewingHistory()) {
						data1 = ((MemberWindow)frame).getMember();
						data2 = ((MemberWindow)frame).getHistorySeason();
						previewMode = PreviewDialog.PreviewMode.EXPORT_MEMBER_HISTORY;
					} else if (((MemberWindow)frame).isViewingInformation()) {
						
					}
				}
				previewDialog.display(data1, data2, previewMode);
			}
		});
		actionMap.put("Page Setup",pageSetupAction = new RunnableAction("Page Setup","Displaying page setup dialog",true) {
			public void act() {
				IE.setPageFormat(PrinterJob.getPrinterJob().pageDialog(IE.getPageFormat()));
			}
		});
		actionMap.put("Print",printAction = new RunnableAction("Print","Displaying print dialog") {
			public void act() {
				JInternalFrame frame = desktopPane.getSelectedFrame();
				PepBandData data1 = null;
				PepBandData data2 = null;
				PreviewDialog.PreviewMode previewMode = null;
				if (frame instanceof EventWindow) {
					data1 = ((EventWindow) frame).getEvent();
					data2 = null;
					previewMode = PreviewDialog.PreviewMode.PRINT_EVENT;
				} else if (frame instanceof SeasonWindow) {
					if (((SeasonWindow)frame).isViewingBandTable()) {
						data1 = ((SeasonWindow)frame).getSeason();
						data2 = null;
						previewMode = PreviewDialog.PreviewMode.PRINT_SEASON_ROSTER;
					} else if (((SeasonWindow)frame).isViewingEvents()) {
						data1 = ((SeasonWindow)frame).getSeason();
						data2 = null;
						previewMode = PreviewDialog.PreviewMode.PRINT_SEASON_EVENTS;
					}
				} else if (frame instanceof MemberWindow) {
					if (((MemberWindow)frame).isViewingHistory()) {
						data1 = ((MemberWindow)frame).getMember();
						data2 = ((MemberWindow)frame).getHistorySeason();
						previewMode = PreviewDialog.PreviewMode.PRINT_MEMBER_HISTORY;
					} else if (((MemberWindow)frame).isViewingInformation()) {
						
					}
				}
				previewDialog.display(data1, data2, previewMode);
			}
		});
		actionMap.put("Exit",exitAction = new RunnableAction("Exit",null) {
			public void act() {
				Tools.setProperty("Preferred Screen Device", getGraphicsConfiguration().getDevice().getIDstring());
				Tools.setProperty("Extended State", Integer.toString(getExtendedState() & ~Frame.ICONIFIED));
				disposeAllWindowsAndDialogs();
			}
		});
		
		actionMap.put("Cut",cutAction = new RunnableAction("Cut","Cutting the selected members") {
			public void act() {
				JInternalFrame frame = desktopPane.getSelectedFrame();
				if (frame instanceof InternalTableWindow) {
					JTable table = ((InternalTableWindow)frame).getBandTable().getTable();
					TransferHandler.getCutAction().actionPerformed(new ActionEvent(table,ActionEvent.ACTION_PERFORMED,""));
				}
			}
		});
		actionMap.put("Copy",copyAction = new RunnableAction("Copy","Copying the selected members") {
			public void act() {
				JInternalFrame frame = desktopPane.getSelectedFrame();
				if (frame instanceof InternalTableWindow) {
					JTable table = ((InternalTableWindow)frame).getBandTable().getTable();
					TransferHandler.getCopyAction().actionPerformed(new ActionEvent(table,ActionEvent.ACTION_PERFORMED,""));
				}
			}
		});
		actionMap.put("Paste",pasteAction = new RunnableAction("Paste","Pasting members from the clipboard") {
			public void act() {
				JInternalFrame frame = desktopPane.getSelectedFrame();
				if (frame instanceof InternalTableWindow) {
					JTable table = ((InternalTableWindow)frame).getBandTable().getTable();
					TransferHandler.getPasteAction().actionPerformed(new ActionEvent(table,ActionEvent.ACTION_PERFORMED,""));
				}
			}
		});
		actionMap.put("Remove",removeAction = new RunnableAction("Remove","Removing the selected members") {
			public void act() {
				JInternalFrame frame = desktopPane.getSelectedFrame();
				if (frame instanceof InternalTableWindow && ((InternalTableWindow)frame).getBandTable().getBand() instanceof PepBandEvent) {
					ArrayList<Member> membersToRemove = ((InternalTableWindow)frame).getBandTable().getSelectedMembers();
					for (Member member : membersToRemove) {
						((InternalTableWindow)frame).getBandTable().getBand().removeMember(member);
					}
				}
			}
		});
		actionMap.put("Select All",selectAllAction = new RunnableAction("Select All","Selecting all members") {
			public void act() {
				JInternalFrame frame = desktopPane.getSelectedFrame();
				if (frame instanceof InternalTableWindow) {
					((InternalTableWindow)frame).getBandTable().getTable().selectAll();
				}
			}
		});
		actionMap.put("Open Member",openMemberAction = new RunnableAction("Open","Opening selected members in individual member windows") {
			public void act() {
				JInternalFrame frame = desktopPane.getSelectedFrame();
				if (frame instanceof InternalTableWindow) {
					ArrayList<Member> selectedMembers = ((InternalTableWindow)frame).getBandTable().getSelectedMembers();
					boolean openSelectedMembers = true;
					if (selectedMembers.size() > 10) {
						String[] message = new String[4];
						message[0] = "Do you really want to open more than 10 member windows at once?";
						message[1] = "This action could make your screen rather crowded.";
						String[] options = {"Yes, Open Windows","No, I'll Reconsider"};
						int value = JOptionPane.showOptionDialog(Tools.getProgramRoot(), message, "Tragedy of the Commons", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE ,null, options, options[1]);
						if(value == JOptionPane.NO_OPTION) {
							openSelectedMembers = false;
						}
					}
					if (openSelectedMembers) {
						for (Member member : selectedMembers) {
							Tools.getDesktopPane().addMemberWindow(member).display();
						}
					}
				}
			}
		});
		actionMap.put("Edit Member",editMemberAction = new RunnableAction("Edit",null) {
			public void act() {
				JInternalFrame frame = desktopPane.getSelectedFrame();
				if (frame instanceof InternalTableWindow) {
					BandTable bandTable = ((InternalTableWindow)frame).getBandTable();
					EditingTab.getEditDialog().display(bandTable.getSelectedMembers());
				}
			}
		});
		
		actionMap.put("FL Name View",nameFirstLastAction = new RunnableAction("Name (First, Last)","Setting view to show names") {
			public void act() {
				if (desktopPane.getSelectedFrame() instanceof InternalTableWindow) {
					BandTable bandTable = ((InternalTableWindow)desktopPane.getSelectedFrame()).getBandTable();
					bandTable.getViewManager().setNameField(DataField.NAME_FIRST_LAST);
					if (desktopPane.getSelectedFrame() instanceof EventWindow) {
						bandTable.getAdditionTab().getViewManager().setNameField(DataField.NAME_FIRST_LAST);
					}
					Tools.setProperty("Name Data Field",DataField.NAME_FIRST_LAST.name());
				}
			}
		});
		actionMap.put("Name View",nameAction = new RunnableAction("Name (Last, First)","Setting view to show names") {
			public void act() {
				if (desktopPane.getSelectedFrame() instanceof InternalTableWindow) {
					BandTable bandTable = ((InternalTableWindow)desktopPane.getSelectedFrame()).getBandTable();
					bandTable.getViewManager().setNameField(DataField.NAME);
					if (desktopPane.getSelectedFrame() instanceof EventWindow) {
						bandTable.getAdditionTab().getViewManager().setNameField(DataField.NAME);
					}
					Tools.setProperty("Name Data Field",DataField.NAME.name());
				}
			}
		});
		actionMap.put("Full Name View",fullNameAction = new RunnableAction("Full Name (First \"Nickname\" Last)","Setting view to show full names") {
			public void act() {
				if (desktopPane.getSelectedFrame() instanceof InternalTableWindow) {
					BandTable bandTable = ((InternalTableWindow)desktopPane.getSelectedFrame()).getBandTable();
					bandTable.getViewManager().setNameField(DataField.FULL_NAME);
					if (desktopPane.getSelectedFrame() instanceof EventWindow) {
						bandTable.getAdditionTab().getViewManager().setNameField(DataField.FULL_NAME);
					}
					Tools.setProperty("Name Data Field",DataField.FULL_NAME.name());
				}
			}
		});
		actionMap.put("Nickname View",nickNameAction = new RunnableAction("Nickname (Nickname)","Setting view to show nicknames") {
			public void act() {
				if (desktopPane.getSelectedFrame() instanceof InternalTableWindow) {
					BandTable bandTable = ((InternalTableWindow)desktopPane.getSelectedFrame()).getBandTable();
					bandTable.getViewManager().setNameField(DataField.NICK_NAME);
					if (desktopPane.getSelectedFrame() instanceof EventWindow) {
						bandTable.getAdditionTab().getViewManager().setNameField(DataField.NICK_NAME);
					}
					Tools.setProperty("Name Data Field",DataField.NICK_NAME.name());
				}
			}
		});
		actionMap.put("Event View",eventViewAction = new RunnableAction("Event View","Setting view to event view") {
			public void act() {
				if (desktopPane.getSelectedFrame() instanceof InternalTableWindow) {
					BandTable bandTable = ((InternalTableWindow)desktopPane.getSelectedFrame()).getBandTable();
					bandTable.getViewManager().setViewType(ViewType.EVENT);
				}
			}
		});
		actionMap.put("Roster View",rosterViewAction = new RunnableAction("Roster View","Setting view to roster view") {
			public void act() {
				if (desktopPane.getSelectedFrame() instanceof InternalTableWindow) {
					BandTable bandTable = ((InternalTableWindow)desktopPane.getSelectedFrame()).getBandTable();
					bandTable.getViewManager().setViewType(ViewType.ROSTER);
				}
			}
		});
		actionMap.put("Horizontal Grid",horizontalGridAction = new RunnableAction("Show Horizontal Gridlines","Showing horizontal gridlines") {
			public void act() {
				if (desktopPane.getSelectedFrame() instanceof InternalTableWindow) {
					BandTable bandTable = ((InternalTableWindow)desktopPane.getSelectedFrame()).getBandTable();
					bandTable.setShowHorizontalGridLines(horizontalGridItem.isSelected());
					Tools.setProperty("Show BandTable Horizontal Grid Lines",Boolean.toString(horizontalGridItem.isSelected()));
				}
			}
		});
		actionMap.put("Vertical Grid",verticalGridAction = new RunnableAction("Show Vertical Gridlines","Showing vertical gridlines") {
			public void act() {
				if (desktopPane.getSelectedFrame() instanceof InternalTableWindow) {
					BandTable bandTable = ((InternalTableWindow)desktopPane.getSelectedFrame()).getBandTable();
					bandTable.setShowVerticalGridLines(verticalGridItem.isSelected());
					Tools.setProperty("Show BandTable Vertical Grid Lines",Boolean.toString(verticalGridItem.isSelected()));
				}
			}
		});
		
		actionMap.put("Options",optionsAction = new RunnableAction("Options","Displaying options dialog") {
			public void act() {
				optionsDialog.display();
			}
		});
		
		actionMap.put("Cascade",cascadeAction = new RunnableAction("Cascade","Cascading all windows") {
			public void act() {
				desktopPane.cascade();
			}
		});
		actionMap.put("Tile Horizontally",tileHorizontallyAction = new RunnableAction("Tile Horizontally","Horizontally tiling all windows") {
			public void act() {
				desktopPane.tileHorizontally();
			}
		});
		actionMap.put("Tile Vertically",tileVerticallyAction = new RunnableAction("Tile Vertically","Vertically tiling all windows") {
			public void act() {
				desktopPane.tileVertically();
			}
		});
		actionMap.put("Minimize All",minimizeAllAction = new RunnableAction("Minimize All","Minimizing all windows") {
			public void act() {
				desktopPane.minimizeAll();
			}
		});
		actionMap.put("Restore All",restoreAllAction = new RunnableAction("Restore All","Restoring all windows") {
			public void act() {
				desktopPane.restoreAll();
			}
		});
		
		actionMap.put("Maintenance Window", maintenanceAction = new RunnableAction("Maintenance Window","Opening maintenance window") {
			public void act() {
				desktopPane.addLoadMessageWindow().display();
			}
		});
		actionMap.put("All Data", allDataAction = new RunnableAction("View All Data","Opening dialog to view all data") {
			public void act() {
				dataDialog.display();
			}
		});
		actionMap.put("Reset Data IDs", resetIDsAction = new RunnableAction("Reset Data IDs","Reseting data IDs") {
			public void act() {
				DataManager.getDataManager().resetIDs();
			}
		});
		actionMap.put("About Pep Band Manager Suite", aboutAction = new RunnableAction("About Pep Band Manager Suite","Opening about dialog") {
			public void act() {
				aboutDialog.display();
			}
		});
		
		newAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_DOWN_MASK));
		newAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_N);
		newAction.putValue(Action.SMALL_ICON,Tools.getIcon("new16"));
		newAction.putValue(Action.LONG_DESCRIPTION,"Create a new event");
		newAction.putValue(Action.SHORT_DESCRIPTION,"Create event");
		
		openAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_DOWN_MASK));
		openAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_O);
		openAction.putValue(Action.SMALL_ICON,Tools.getIcon("open16"));
		openAction.putValue(Action.LONG_DESCRIPTION,"Open the current season to access events and band roster");
		openAction.putValue(Action.SHORT_DESCRIPTION,"Open current season");
		
		closeAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK));
		closeAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		closeAction.putValue(Action.SMALL_ICON,Tools.getIcon("close16"));
		closeAction.putValue(Action.LONG_DESCRIPTION,"Close the selected window");
		closeAction.putValue(Action.SHORT_DESCRIPTION,"Close window");
		
		closeAllAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
		closeAllAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_L);
		closeAllAction.putValue(Action.LONG_DESCRIPTION,"Close all open windows");
		closeAllAction.putValue(Action.SHORT_DESCRIPTION,"Close all windows");
		
		importAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_I,InputEvent.CTRL_DOWN_MASK));
		importAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_I);
		importAction.putValue(Action.LONG_DESCRIPTION,"Importfrom an external CSV file");
		importAction.putValue(Action.SHORT_DESCRIPTION,"Import");
		
		exportAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_DOWN_MASK));
		exportAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
		exportAction.putValue(Action.SMALL_ICON,Tools.getIcon("acrobat16"));
		exportAction.putValue(Action.LONG_DESCRIPTION,"Export the current window to a PDF");
		exportAction.putValue(Action.SHORT_DESCRIPTION,"Export window");
		
		pageSetupAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_T);
		pageSetupAction.putValue(Action.LONG_DESCRIPTION,"Display the page setup dialog");
		pageSetupAction.putValue(Action.SHORT_DESCRIPTION,"Page Setup");
		
		printAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.CTRL_DOWN_MASK));
		printAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_P);
		printAction.putValue(Action.SMALL_ICON,Tools.getIcon("print16"));
		printAction.putValue(Action.LONG_DESCRIPTION,"Print the current window");
		printAction.putValue(Action.SHORT_DESCRIPTION,"Print window");
		
		exitAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_Q,InputEvent.CTRL_DOWN_MASK));
		exitAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_X);
		exitAction.putValue(Action.LONG_DESCRIPTION,"Exit the program");
		exitAction.putValue(Action.SHORT_DESCRIPTION,"Exit");
		
		cutAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_DOWN_MASK));
		cutAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_T);
		cutAction.putValue(Action.SMALL_ICON,Tools.getIcon("cut16"));
		cutAction.putValue(Action.LONG_DESCRIPTION,"Cut the selected member onto clipboard");
		cutAction.putValue(Action.SHORT_DESCRIPTION,"Cut member");
		
		copyAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_DOWN_MASK));
		copyAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		copyAction.putValue(Action.SMALL_ICON,Tools.getIcon("copy16"));
		copyAction.putValue(Action.LONG_DESCRIPTION,"Copy the selected member onto clipboard");
		copyAction.putValue(Action.SHORT_DESCRIPTION,"Copy member");
		
		pasteAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_DOWN_MASK));
		pasteAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_P);
		pasteAction.putValue(Action.SMALL_ICON,Tools.getIcon("paste16"));
		pasteAction.putValue(Action.LONG_DESCRIPTION,"Paste member from clipboard into current event");
		pasteAction.putValue(Action.SHORT_DESCRIPTION,"Paste member");
		
		removeAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
		removeAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
		removeAction.putValue(Action.SMALL_ICON,Tools.getIcon("remove16"));
		removeAction.putValue(Action.LONG_DESCRIPTION,"Remove the selected member");
		removeAction.putValue(Action.SHORT_DESCRIPTION,"Remove member");
		
		selectAllAction.putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_DOWN_MASK));
		selectAllAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		selectAllAction.putValue(Action.LONG_DESCRIPTION,"Select all members");
		selectAllAction.putValue(Action.SHORT_DESCRIPTION,"Select all members");
		
		openMemberAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_O);
		openMemberAction.putValue(Action.SMALL_ICON,Tools.getIcon("member16"));
		openMemberAction.putValue(Action.LONG_DESCRIPTION,"Open the selected member in a new member window");
		openMemberAction.putValue(Action.SHORT_DESCRIPTION,"Open selected member");
		
		editMemberAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
		editMemberAction.putValue(Action.SMALL_ICON,Tools.getIcon("edit16"));
		editMemberAction.putValue(Action.LONG_DESCRIPTION,"Edit the selected member");
		editMemberAction.putValue(Action.SHORT_DESCRIPTION,"Edit selected member");
		
		nameAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_N);
		nameAction.putValue(Action.LONG_DESCRIPTION,"Display member names in selected window");
		nameAction.putValue(Action.SHORT_DESCRIPTION,"Display names");
		
		fullNameAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_F);
		fullNameAction.putValue(Action.LONG_DESCRIPTION,"Display member full names in selected window");
		fullNameAction.putValue(Action.SHORT_DESCRIPTION,"Display full names");
		
		nickNameAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_K);
		nickNameAction.putValue(Action.LONG_DESCRIPTION,"Display member nicknames in selected window");
		nickNameAction.putValue(Action.SHORT_DESCRIPTION,"Display nicknames");
		
		eventViewAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
		eventViewAction.putValue(Action.SMALL_ICON,Tools.getIcon("events16"));
		eventViewAction.putValue(Action.LONG_DESCRIPTION,"Set the band table in selected window to a view showing info relevant for events");
		eventViewAction.putValue(Action.SHORT_DESCRIPTION,"Event view");
		
		rosterViewAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_R);
		rosterViewAction.putValue(Action.SMALL_ICON,Tools.getIcon("band16"));
		rosterViewAction.putValue(Action.LONG_DESCRIPTION,"Set the band table in selected window to a view showing info relevant for season band rosters");
		rosterViewAction.putValue(Action.SHORT_DESCRIPTION,"Band roseter view");
		
		horizontalGridAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_H);
		horizontalGridAction.putValue(Action.LONG_DESCRIPTION,"Toggle displaying horizontal gridlines on table in selected window");
		horizontalGridAction.putValue(Action.SHORT_DESCRIPTION,"Toggle horizontal gridlines");
		
		verticalGridAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_V);
		verticalGridAction.putValue(Action.LONG_DESCRIPTION,"Toggle displaying vertical gridlines on table in selected window");
		verticalGridAction.putValue(Action.SHORT_DESCRIPTION,"Toggle vertical gridlines");
		
		optionsAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_O);
		optionsAction.putValue(Action.SMALL_ICON,Tools.getIcon("options16"));
		optionsAction.putValue(Action.LONG_DESCRIPTION,"Display the options and preferences dialog");
		optionsAction.putValue(Action.SHORT_DESCRIPTION,"Change options");
		
		cascadeAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		cascadeAction.putValue(Action.SMALL_ICON,Tools.getIcon("cascade16"));
		cascadeAction.putValue(Action.LONG_DESCRIPTION,"Cascade all windows");
		cascadeAction.putValue(Action.SHORT_DESCRIPTION,"Cascade windows");
		
		tileHorizontallyAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_H);
		tileHorizontallyAction.putValue(Action.SMALL_ICON,Tools.getIcon("tilehorizontal16"));
		tileHorizontallyAction.putValue(Action.LONG_DESCRIPTION,"Tile all windows horizontally");
		tileHorizontallyAction.putValue(Action.SHORT_DESCRIPTION,"Tile horizontally");
		
		tileVerticallyAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_V);
		tileVerticallyAction.putValue(Action.SMALL_ICON,Tools.getIcon("tilevertical16"));
		tileVerticallyAction.putValue(Action.LONG_DESCRIPTION,"Tile all windows vertically");
		tileVerticallyAction.putValue(Action.SHORT_DESCRIPTION,"Tile vertically");
		
		minimizeAllAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_M);
		minimizeAllAction.putValue(Action.SMALL_ICON,Tools.getIcon("minimize16"));
		minimizeAllAction.putValue(Action.LONG_DESCRIPTION,"Minimize all windows");
		minimizeAllAction.putValue(Action.SHORT_DESCRIPTION,"Minimize all windows");
		
		restoreAllAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_R);
		restoreAllAction.putValue(Action.SMALL_ICON,Tools.getIcon("restore16"));
		restoreAllAction.putValue(Action.LONG_DESCRIPTION,"Restore all windows");
		restoreAllAction.putValue(Action.SHORT_DESCRIPTION,"Restore all windows");
		
		maintenanceAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_M);
		maintenanceAction.putValue(Action.LONG_DESCRIPTION,"Display maintenance window to view reports about the state of the database");
		maintenanceAction.putValue(Action.SHORT_DESCRIPTION,"Display maintenance window");
		
		allDataAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		allDataAction.putValue(Action.LONG_DESCRIPTION,"Open a dialog that shows all data currently held in the database");
		allDataAction.putValue(Action.SHORT_DESCRIPTION,"View all data");
		
		resetIDsAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_R);
		resetIDsAction.putValue(Action.LONG_DESCRIPTION,"Reset the ID numbers for all data in the database");
		resetIDsAction.putValue(Action.SHORT_DESCRIPTION,"Reset all database IDs");
		
		aboutAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		aboutAction.putValue(Action.SMALL_ICON,Tools.getIcon("info16"));
		aboutAction.putValue(Action.LONG_DESCRIPTION,"Display program information, version number, and legal info");
		aboutAction.putValue(Action.SHORT_DESCRIPTION,"About the program");
		
		closeAction.setEnabled(false);
		closeAllAction.setEnabled(false);
		exportAction.setEnabled(false);
		printAction.setEnabled(false);
		cutAction.setEnabled(false);
		copyAction.setEnabled(false);
		pasteAction.setEnabled(false);
		removeAction.setEnabled(false);
		selectAllAction.setEnabled(false);
		openMemberAction.setEnabled(false);
		editMemberAction.setEnabled(false);
		nameFirstLastAction.setEnabled(false);
		nameAction.setEnabled(false);
		fullNameAction.setEnabled(false);
		nickNameAction.setEnabled(false);
		eventViewAction.setEnabled(false);
		rosterViewAction.setEnabled(false);
		horizontalGridAction.setEnabled(false);
		verticalGridAction.setEnabled(false);
		cascadeAction.setEnabled(false);
		tileHorizontallyAction.setEnabled(false);
		tileVerticallyAction.setEnabled(false);
		minimizeAllAction.setEnabled(false);
		restoreAllAction.setEnabled(false);
	}
	
	private void a2Components() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		newItem = new JMenuItem(newAction);
		openItem = new JMenuItem(openAction);
		closeItem = new JMenuItem(closeAction);
		closeAllItem = new JMenuItem(closeAllAction);
		importItem = new JMenuItem(importAction);
		exportItem = new JMenuItem(exportAction);
		pageSetupItem = new JMenuItem(pageSetupAction);
		printItem = new JMenuItem(printAction);
		exitItem = new JMenuItem(exitAction);
		editMenu = new JMenu("Member");
		cutItem = new JMenuItem(cutAction);
		copyItem = new JMenuItem(copyAction);
		pasteItem = new JMenuItem(pasteAction);
		removeItem = new JMenuItem(removeAction);
		selectAllItem = new JMenuItem(selectAllAction);
		openMemberItem = new JMenuItem(openMemberAction);
		editMemberItem = new JMenuItem(editMemberAction);
		viewMenu = new JMenu("View");
		nameMenu = new JMenu("Name");
		nameFirstLastItem = new JRadioButtonMenuItem(nameFirstLastAction);
		nameItem = new JRadioButtonMenuItem(nameAction);
		fullNameItem = new JRadioButtonMenuItem(fullNameAction);
		nickNameItem = new JRadioButtonMenuItem(nickNameAction);
		viewTypeMenu = new JMenu("Table View");
		eventViewItem = new JRadioButtonMenuItem(eventViewAction);
		rosterViewItem = new JRadioButtonMenuItem(rosterViewAction);
		horizontalGridItem = new JCheckBoxMenuItem(horizontalGridAction);
		verticalGridItem = new JCheckBoxMenuItem(verticalGridAction);
		toolsMenu = new JMenu("Tools");
		optionsItem = new JMenuItem(optionsAction);
		windowMenu = new JMenu("Window");
		cascadeItem = new JMenuItem(cascadeAction);
		tileHorizontallyItem = new JMenuItem(tileHorizontallyAction);
		tileVerticallyItem = new JMenuItem(tileVerticallyAction);
		minimizeAllItem = new JMenuItem(minimizeAllAction);
		restoreAllItem = new JMenuItem(restoreAllAction);
		helpMenu = new JMenu("Help");
		maintenanceItem = new JMenuItem(maintenanceAction);
		allDataItem = new JMenuItem(allDataAction);
		resetIDsItem = new JMenuItem(resetIDsAction);
		aboutItem = new JMenuItem(aboutAction);
		
		toolBar = new JToolBar("Program Toolbar", Tools.determineOriginalToolbarOrientation());
		newButton = new JButton(newAction);
		newButton.setHorizontalTextPosition(SwingConstants.CENTER);
		newButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		openButton = new JButton(openAction);
		openButton.setHorizontalTextPosition(SwingConstants.CENTER);
		openButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		cutButton = new JButton(cutAction);
		cutButton.setHorizontalTextPosition(SwingConstants.CENTER);
		cutButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		copyButton = new JButton(copyAction);
		copyButton.setHorizontalTextPosition(SwingConstants.CENTER);
		copyButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		pasteButton = new JButton(pasteAction);
		pasteButton.setHorizontalTextPosition(SwingConstants.CENTER);
		pasteButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		exportButton = new JButton(exportAction);
		exportButton.setHorizontalTextPosition(SwingConstants.CENTER);
		exportButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		printButton = new JButton(printAction);
		printButton.setHorizontalTextPosition(SwingConstants.CENTER);
		printButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		
		centerPanel = new JPanel();
		desktopPane = new AppWindowDesktop(this);
		
		southPanel = new JPanel();
		tipLabel = new JLabel("Pep Band Manager Suite 2.0");
		tipLabel.setIconTextGap(8);
		tipLabel.setBorder(new EmptyBorder(4,4,4,4));
		loadPanel = new JPanel();
		loadIcon = new LoadIcon();
		loadIcon.setVisible(false);
		loadLabel = new JLabel("");
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(true); 
		fileChooser.setFileView(new FileView() {
			public Icon getIcon(File file) {
				return FileSystemView.getFileSystemView().getSystemIcon(file);
			}
		});
		pdfFilter = new FileNameExtensionFilter("Adobe PDF (.pdf)","pdf");
		csvFilter = new FileNameExtensionFilter("Comma Delimited File (.csv)","csv");
		Tools.addRootComponent(fileChooser);
		Tools.applyTextPopup(fileChooser);
		Tools.applyScrollPopup(fileChooser);
		
		progressDialog = new ProgressDialog();
		Tools.addRootComponent(progressDialog);
		
		optionsDialog = new OptionsDialog(this);
		Tools.addRootComponent(optionsDialog);
		
		previewDialog = new PreviewDialog(fileChooser, pdfFilter, csvFilter, progressDialog);
		Tools.addRootComponent(previewDialog);
		
		aboutDialog = new AboutDialog();
		Tools.addRootComponent(aboutDialog);
		
		dataDialog = new DataDialog();
		Tools.addRootComponent(dataDialog);
	}
	
	private void a3Listeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitItem.doClick();
			}
			
			public void windowClosed(WindowEvent e) {
				try {
					SERVICE.shutdown();
					SERVICE.awaitTermination(30,TimeUnit.SECONDS);
				} catch (Exception exc) {
					System.err.println("Exception while shuting down executor service.");
				} finally {
					System.exit(0);
				}
			}
		});
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				if (DataManager.getDataManager().getMaintenanceResults() == DataManager.GOOD) {
					openAction.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,""));
				} else {
					desktopPane.addLoadMessageWindow().display();
				}
			}
		});
		centerPanel.addContainerListener(new ContainerAdapter() {
			public void componentAdded(ContainerEvent e) {
				if (e.getChild() == toolBar) {
					BorderLayout layout = (BorderLayout)e.getContainer().getLayout();
					Object constraints = layout.getConstraints(toolBar);
					if (constraints != null && constraints instanceof String) {
						Tools.setProperty("Toolbar Location",(String)constraints);
					} else {
						System.err.println("AppWindow failed to record location of Toolbar");
					}
				}
			}
		});
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() == nameFirstLastItem && e.getStateChange() == ItemEvent.SELECTED) {
					nameFirstLastAction.actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
				} else if (e.getSource() == nameItem && e.getStateChange() == ItemEvent.SELECTED) {
					nameAction.actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
				} else if (e.getSource() == fullNameItem && e.getStateChange() == ItemEvent.SELECTED) {
					fullNameAction.actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
				} else if (e.getSource() == nickNameItem && e.getStateChange() == ItemEvent.SELECTED) {
					nickNameAction.actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
				} else if (e.getSource() == eventViewItem && e.getStateChange() == ItemEvent.SELECTED) {
					eventViewAction.actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
				} else if (e.getSource() == rosterViewItem && e.getStateChange() == ItemEvent.SELECTED) {
					rosterViewAction.actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
				} else if (e.getSource() == horizontalGridItem) {
					horizontalGridAction.actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
				} else if (e.getSource() == verticalGridItem) {
					verticalGridAction.actionPerformed(new ActionEvent(e.getSource(),ActionEvent.ACTION_PERFORMED,""));
				}
			}
		};
		nameFirstLastItem.addItemListener(itemListener);
		nameItem.addItemListener(itemListener);
		fullNameItem.addItemListener(itemListener);
		nickNameItem.addItemListener(itemListener);
		eventViewItem.addItemListener(itemListener);
		rosterViewItem.addItemListener(itemListener);
		horizontalGridItem.addItemListener(itemListener);
		verticalGridItem.addItemListener(itemListener);
		
		internalFrameAdapter = new InternalFrameAdapter() {
			public void internalFrameActivated(InternalFrameEvent e) {
				InternalWindow window = (InternalWindow)e.getInternalFrame();
				closeAction.setEnabled(true);
				exportAction.setEnabled(true);
				printAction.setEnabled(true);
				if (window instanceof InternalTableWindow) {
					InternalTableWindow tableWindow = (InternalTableWindow)window;
					nameFirstLastAction.setEnabled(true);
					nameAction.setEnabled(true);
					fullNameAction.setEnabled(true);
					nickNameAction.setEnabled(true);
					eventViewAction.setEnabled(true);
					rosterViewAction.setEnabled(true);
					horizontalGridAction.setEnabled(true);
					verticalGridAction.setEnabled(true);
					selectAllAction.setEnabled(true);
					if (Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(IDTransferable.FLAVOR) && window instanceof EventWindow) {
						pasteAction.setEnabled(true);
					} else {
						pasteAction.setEnabled(false);
					}
					if (tableWindow.getBandTable().getTable().getSelectedRows().length > 0) {
						copyAction.setEnabled(true);
						openMemberAction.setEnabled(true);
						editMemberAction.setEnabled(true);
						if (window instanceof EventWindow) {
							cutAction.setEnabled(true);
							removeAction.setEnabled(true);
						} else {
							cutAction.setEnabled(false);
							removeAction.setEnabled(false);
						}
					} else {
						cutAction.setEnabled(false);
						copyAction.setEnabled(false);
						removeAction.setEnabled(false);
						openMemberAction.setEnabled(false);
						editMemberAction.setEnabled(false);
					}
					switch(tableWindow.getBandTable().getViewManager().getViewType()) {
						case EVENT : eventViewItem.setSelected(true); break;
						case ROSTER : rosterViewItem.setSelected(true);
					}
					switch(tableWindow.getBandTable().getViewManager().getNameField()) {
						case NAME_FIRST_LAST: nameFirstLastItem.setSelected(true); break;
						case NAME : nameItem.setSelected(true); break;
						case FULL_NAME : fullNameItem.setSelected(true); break;
						case NICK_NAME : nickNameItem.setSelected(true);
					}
					horizontalGridItem.setSelected(tableWindow.getBandTable().getTable().getShowHorizontalLines());
					verticalGridItem.setSelected(tableWindow.getBandTable().getTable().getShowVerticalLines());
				} else {
					cutAction.setEnabled(false);
					copyAction.setEnabled(false);
					pasteAction.setEnabled(false);
					removeAction.setEnabled(false);
					selectAllAction.setEnabled(false);
					openMemberAction.setEnabled(false);
					editMemberAction.setEnabled(false);
					nameFirstLastAction.setEnabled(false);
					nameAction.setEnabled(false);
					fullNameAction.setEnabled(false);
					nickNameAction.setEnabled(false);
					eventViewAction.setEnabled(false);
					rosterViewAction.setEnabled(false);
					horizontalGridAction.setEnabled(false);
					verticalGridAction.setEnabled(false);
				}
			}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {
				InternalWindow window = (InternalWindow)e.getInternalFrame();
				closeAction.setEnabled(false);
				exportAction.setEnabled(false);
				printAction.setEnabled(false);
				cutAction.setEnabled(false);
				copyAction.setEnabled(false);
				pasteAction.setEnabled(false);
				removeAction.setEnabled(false);
				selectAllAction.setEnabled(false);
				openMemberAction.setEnabled(false);
				editMemberAction.setEnabled(false);
				nameFirstLastAction.setEnabled(false);
				nameAction.setEnabled(false);
				fullNameAction.setEnabled(false);
				nickNameAction.setEnabled(false);
				eventViewAction.setEnabled(false);
				rosterViewAction.setEnabled(false);
				horizontalGridAction.setEnabled(false);
				verticalGridAction.setEnabled(false);
			}
		};
		bandTableSelectionListener = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					BandTableSelectionModel model = (BandTableSelectionModel)e.getSource();
					if (desktopPane.getSelectedFrame() instanceof InternalTableWindow) {
						InternalTableWindow window = (InternalTableWindow)desktopPane.getSelectedFrame();
						if (window.getBandTable().equals(model.getBandTable()) && !model.isSelectionEmpty()) {
							copyAction.setEnabled(true);
							openMemberAction.setEnabled(true);
							editMemberAction.setEnabled(true);
							if (model.getBandTable().getBand() instanceof PepBandEvent) {
								cutAction.setEnabled(true);
								removeAction.setEnabled(true);
							} else {
								cutAction.setEnabled(false);
								removeAction.setEnabled(false);
							}
						} else {
							cutAction.setEnabled(false);
							copyAction.setEnabled(false);
							removeAction.setEnabled(false);
							openMemberAction.setEnabled(false);
							editMemberAction.setEnabled(false);
						}
					} else {
						cutAction.setEnabled(false);
						copyAction.setEnabled(false);
						removeAction.setEnabled(false);
						openMemberAction.setEnabled(false);
						editMemberAction.setEnabled(false);
					}
				}
			}
		};
		desktopPane.addContainerListener(new ContainerAdapter() {
			public void componentAdded(ContainerEvent e) {
				if (desktopPane.getComponentCount() > 0) {
					setEnabledActions(true);
				} else {
					setEnabledActions(false);
				}
			}
			
			public void componentRemoved(ContainerEvent e) {
				if (desktopPane.getComponentCount() > 0) {
					setEnabledActions(true);
				} else {
					setEnabledActions(false);
				}
			}
			
			private void setEnabledActions(boolean value) {
				closeAllAction.setEnabled(value);
				cascadeAction.setEnabled(value);
				tileHorizontallyAction.setEnabled(value);
				tileVerticallyAction.setEnabled(value);
				minimizeAllAction.setEnabled(value);
				restoreAllAction.setEnabled(value);
			}
		});
		Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() {
			public void flavorsChanged(FlavorEvent e) {
				if (desktopPane.getSelectedFrame() instanceof EventWindow && ((Clipboard)e.getSource()).isDataFlavorAvailable(IDTransferable.FLAVOR)) {
					pasteAction.setEnabled(true);
				} else {
					pasteAction.setEnabled(false);
				}
			}
		});
	}
	
	private void a4Layouts() {
		setJMenuBar(menuBar);
		
		menuBar.add(fileMenu);
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(closeItem);
		fileMenu.add(closeAllItem);
		fileMenu.addSeparator();
		//fileMenu.add(importItem);
		fileMenu.add(exportItem);
		fileMenu.addSeparator();
		fileMenu.add(pageSetupItem);
		fileMenu.add(printItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		
		menuBar.add(editMenu);
		editMenu.add(openMemberItem);
		editMenu.add(editMemberItem);
		editMenu.addSeparator();
		editMenu.add(cutItem);
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		editMenu.add(removeItem);
		editMenu.add(selectAllItem);
		
		
		menuBar.add(viewMenu);
		viewMenu.add(nameMenu);
		nameMenu.add(nameFirstLastItem);
		nameMenu.add(nameItem);
		nameMenu.add(fullNameItem);
		nameMenu.add(nickNameItem);
		viewMenu.add(viewTypeMenu);
		viewTypeMenu.add(eventViewItem);
		viewTypeMenu.add(rosterViewItem);
		viewMenu.addSeparator();
		viewMenu.add(horizontalGridItem);
		viewMenu.add(verticalGridItem);
		
		menuBar.add(toolsMenu);
		toolsMenu.add(optionsItem);
		
		menuBar.add(windowMenu);
		windowMenu.add(cascadeItem);
		windowMenu.add(tileHorizontallyItem);
		windowMenu.add(tileVerticallyItem);
		windowMenu.addSeparator();
		windowMenu.add(minimizeAllItem);
		windowMenu.add(restoreAllItem);
		
		menuBar.add(helpMenu);
		helpMenu.add(maintenanceItem);
		helpMenu.add(allDataItem);
		helpMenu.add(resetIDsItem);
		helpMenu.addSeparator();
		helpMenu.add(aboutItem);
		
		nameGroup = new ButtonGroup();
		nameGroup.add(nameFirstLastItem);
		nameGroup.add(nameItem);
		nameGroup.add(fullNameItem);
		nameGroup.add(nickNameItem);
		
		viewTypeGroup = new ButtonGroup();
		viewTypeGroup.add(eventViewItem);
		viewTypeGroup.add(rosterViewItem);
		
		toolBar.add(newButton);
		toolBar.add(openButton);
		toolBar.addSeparator();
		toolBar.add(cutButton);
		toolBar.add(copyButton);
		toolBar.add(pasteButton);
		toolBar.addSeparator();
		toolBar.add(exportButton);
		toolBar.add(printButton);
		
		setLayout(new BorderLayout(0,0));
		centerPanel.setLayout(new BorderLayout(0,0));
		southPanel.setLayout(new BorderLayout(0,0));
		loadPanel.setLayout(new BorderLayout(0,0));
		
		centerPanel.add(toolBar,Tools.determineOriginalToolbarLocation());
		centerPanel.add(desktopPane,BorderLayout.CENTER);
		
		loadPanel.add(loadIcon,BorderLayout.WEST);
		loadPanel.add(Box.createHorizontalStrut(8),BorderLayout.CENTER);
		loadPanel.add(loadLabel,BorderLayout.EAST);
		
		southPanel.add(new JSeparator(SwingConstants.HORIZONTAL),BorderLayout.NORTH);
		southPanel.add(tipLabel,BorderLayout.WEST);
		southPanel.add(loadPanel,BorderLayout.EAST);
		
		add(centerPanel,BorderLayout.CENTER);
		add(southPanel,BorderLayout.SOUTH);
	}
	
	private void a5Initialize() {
		setIconSize(Tools.getInteger("Toolbar Icon Size",32));
		setShowIconText(Tools.getBoolean("Show Toolbar Icon Text",false));
		setShowToolbar(Tools.getBoolean("Show Toolbar",true));
		setLockToolbar(Tools.getBoolean("Lock Toolbar",false));
		setShowSouthPanel(Tools.getBoolean("Show South Panel",true));
		tipListener = new TipListener(this);
		reapplyTipListener();
	}
	
	public void addListenersToWindow(InternalWindow window) {
		window.addInternalFrameListener(internalFrameAdapter);
		if (window instanceof InternalTableWindow) {
			((InternalTableWindow)window).getBandTable().getTable().getSelectionModel().addListSelectionListener(bandTableSelectionListener);
		}
	}
	
	public void display() {
		Double screenFraction = Tools.getDouble("Screen Fraction",0.9);
		setPreferredSize(new Dimension((int)(getGraphicsConfiguration().getBounds().getWidth() * screenFraction),(int)(getGraphicsConfiguration().getBounds().getHeight() * screenFraction)));
		pack();
		setLocation((int)(getGraphicsConfiguration().getBounds().getX() + getGraphicsConfiguration().getBounds().getWidth() / 2.0 - getSize().getWidth() / 2.0),(int)(getGraphicsConfiguration().getBounds().getY() + getGraphicsConfiguration().getBounds().getHeight() / 2.0 - getSize().getHeight() / 2.0));
		setExtendedState(Tools.getInteger("Extended State", Frame.NORMAL));
		setVisible(true);
	}
	
	public void disposeAllWindowsAndDialogs() {
		closeAllAction.act();
		for (Component component : Tools.getRootComponents()) {
			if (component instanceof Window && component != this) {
				((Window)component).dispose();
			}
		}
		dispose();
	}
	
	public HashMap<String,RunnableAction> getActionMap() {
		return actionMap;
	}
	
	public AppWindowDesktop getDesktopPane() {
		return desktopPane;
	}
	
	public ExecutorService getExecutorService() {
		return SERVICE;
	}
	
	public JFileChooser getFileChooser() {
		return fileChooser;
	}
	
	public OptionsDialog getOptionsDialog() {
		return optionsDialog;
	}
	
	public void reapplyTipListener() {
		for (Component component : Tools.getRootComponents()) {
			tipListener.applyToChildren(component);
		}
	}
	
	public void removeListenersFromWindow(InternalWindow window) {
		window.removeInternalFrameListener(internalFrameAdapter);
		if (window instanceof InternalTableWindow) {
			((InternalTableWindow)window).getBandTable().getTable().getSelectionModel().removeListSelectionListener(bandTableSelectionListener);
		}
	}
	
	public void setIconSize(Integer value) {
		newAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("new" + value));
		openAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("open" + value));
		closeAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("close" + value));
		importAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("import" + value));
		exportAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("acrobat" + value));
		printAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("print" + value));
		cutAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("cut" + value));
		copyAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("copy" + value));
		pasteAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("paste" + value ));
		removeAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("remove" + value));
		optionsAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("options" + value));
	}
	
	public void setLoading(String value) {
		if (value != null && !value.isEmpty()) {
			loadLabel.setText(value + "     ");
			loadIcon.setVisible(true);
		} else {
			loadLabel.setText("");
			loadIcon.setVisible(false);
		}
	}
	
	public void setLockToolbar(boolean value) {
		toolBar.setFloatable(!value);
	}
	
	public void setShowIconText(boolean value) {
		newButton.setHideActionText(!value);
		openButton.setHideActionText(!value);
		cutButton.setHideActionText(!value);
		copyButton.setHideActionText(!value);
		pasteButton.setHideActionText(!value);
		exportButton.setHideActionText(!value);
		printButton.setHideActionText(!value);
	}
	
	public void setShowSouthPanel(boolean value) {
		southPanel.setVisible(value);
	}
	
	public void setShowToolbar(boolean value) {
		toolBar.setVisible(value);
	}
	
	public void setTip(String text, Icon icon) {
		if (text != null && !text.isEmpty()) {
			tipLabel.setText(text);
		} else {
			tipLabel.setText("Pep Band Manager Suite");
		}
		if (icon != null && icon.getIconHeight() == 16) {
			tipLabel.setIcon(icon);
		} else {
			tipLabel.setIcon(Tools.getIcon("empty16"));
		}
	}
}