package pepband3.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import pepband3.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.component.preview.*;
import pepband3.gui.extra.*;

public class PreviewDialog extends JDialog implements IconDisplayer {
	
	private JPanel centerPanel;
	private CardLayout cardLayout;
	private RosterPreviewTable rosterPreview;
	private EventPreviewTable eventPreview;
	private SeasonPreviewTable seasonPreview;
	private HistoryPreviewTable historyPreview;
	
	private JButton exportPDFButton;
	private JButton printButton;
	private JButton pageSetupButton;
	private JButton cancelButton;
	
	private ProgressDialog progressDialog;
	private JFileChooser fileChooser;
	private FileNameExtensionFilter pdfFilter;
	private FileNameExtensionFilter csvFilter;
	
	private Action exportPDFAction, printAction, pageSetupAction, cancelAction;
	
	private Member member;
	private Season season;
	private PepBandEvent event;
	private PreviewMode previewMode;
	
	public PreviewDialog(JFileChooser paramFileChooser, FileNameExtensionFilter paramPDFfilter, FileNameExtensionFilter paramCSVfilter, ProgressDialog paramProgressDialog) {
		super(Tools.getProgramRoot(),"Preview",true);
		
		setIconImages(Tools.getWindowIcons());
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(true);
		setUndecorated(false);
		setFileChooser(paramFileChooser);
		setFilters(paramPDFfilter, paramCSVfilter);
		setProgressDialog(paramProgressDialog);
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		final PreviewDialog thisDialog = this;
		exportPDFAction = new RunnableAction("Export to PDF","Exporting to PDF", true) {
			public void act() {
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.removeChoosableFileFilter(pdfFilter);
				fileChooser.removeChoosableFileFilter(csvFilter);
				fileChooser.addChoosableFileFilter(pdfFilter);
				fileChooser.setFileFilter(pdfFilter);
				fileChooser.setApproveButtonText("Export");
				fileChooser.setApproveButtonMnemonic(KeyEvent.VK_E);
				fileChooser.setApproveButtonToolTipText("Export to a PDF with the entered file name and extension");
				fileChooser.setDialogTitle("Choose PDF Name");
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							if (fileChooser.getSelectedFile() == null) {
								File defaultDirectory = new File(Tools.getProperty("Default Export Directory"));
								fileChooser.setCurrentDirectory(defaultDirectory);
								if (previewMode == PreviewMode.EXPORT_MEMBER_HISTORY && member != null) {
									fileChooser.setSelectedFile(new File(member.getNameFirstLast() + " History" + ".pdf"));
								} else if (previewMode == PreviewMode.EXPORT_EVENT && event != null) {
									fileChooser.setSelectedFile(new File(event.getName() + ".pdf"));
								} else if (previewMode == PreviewMode.EXPORT_SEASON_EVENTS && season != null) {
									fileChooser.setSelectedFile(new File(season.getName() + " Events" + ".pdf"));
								} else if (previewMode == PreviewMode.EXPORT_SEASON_ROSTER && season != null) {
									if(rosterPreview.isPointsPreview()) {
										fileChooser.setSelectedFile(new File("Points" + ".pdf"));
									} else if (rosterPreview.isRosterPreview()) {
										fileChooser.setSelectedFile(new File(season.getName() + " Roster" + ".pdf"));
									}
								}
							}
						}	
					});
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				int result = fileChooser.showDialog(thisDialog, null);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					boolean doExport = false;
					File exportFile = fileChooser.getSelectedFile();
					if (fileChooser.getFileFilter().equals(pdfFilter) && !exportFile.getPath().endsWith(".pdf") && !exportFile.getPath().endsWith(".PDF")) {
						exportFile = new File(exportFile.getParentFile(), exportFile.getName() + ".pdf");
					}
					if (exportFile.exists()) {
						result = JOptionPane.showConfirmDialog(thisDialog, "Overwrite Existing File?", "PDF Export", JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.YES_OPTION) {
							doExport = true;
						}
					} else {
						doExport = true;
					}
					if (doExport) {
						final File fileToExport = exportFile;
						setElementsEnabled(false);
						if (previewMode == PreviewMode.EXPORT_MEMBER_HISTORY) {
							progressDialog.display((ImageIcon) exportPDFAction.getValue(Action.LARGE_ICON_KEY), "Exporting Member History to PDF");
						} else if (previewMode == PreviewMode.EXPORT_EVENT) {
							progressDialog.display((ImageIcon) exportPDFAction.getValue(Action.LARGE_ICON_KEY), "Exporting Event to PDF");
						} else if (previewMode == PreviewMode.EXPORT_SEASON_EVENTS) {
							progressDialog.display((ImageIcon) exportPDFAction.getValue(Action.LARGE_ICON_KEY), "Exporting Season Events to PDF");
						} else if (previewMode == PreviewMode.EXPORT_SEASON_ROSTER) {
							if(rosterPreview.isPointsPreview()) {
								progressDialog.display((ImageIcon) exportPDFAction.getValue(Action.LARGE_ICON_KEY), "Exporting Points to PDF");
							} else if (rosterPreview.isRosterPreview()) {
								progressDialog.display((ImageIcon) exportPDFAction.getValue(Action.LARGE_ICON_KEY), "Exporting Season Roster to PDF");
							}
						}
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								fileChooser.setSelectedFile(fileToExport);
								String exceptionString = null;
								if (previewMode == PreviewMode.EXPORT_MEMBER_HISTORY) {
									exceptionString = IE.exportToPDF(historyPreview, fileToExport);
								} else if (previewMode == PreviewMode.EXPORT_EVENT) {
									exceptionString = IE.exportToPDF(eventPreview, fileToExport);
								} else if (previewMode == PreviewMode.EXPORT_SEASON_EVENTS) {
									exceptionString = IE.exportToPDF(seasonPreview, fileToExport);
								} else if (previewMode == PreviewMode.EXPORT_SEASON_ROSTER) {
									exceptionString = IE.exportToPDF(rosterPreview, fileToExport);
								}
								progressDialog.setVisible(false);
								if (exceptionString != null) {
									JOptionPane.showMessageDialog(thisDialog, exceptionString, "IE Exception", JOptionPane.ERROR_MESSAGE);
								}
								setElementsEnabled(true);
							}
						});
					}
				}
			}
		};
		printAction = new RunnableAction("Print","Printing", true) {
			public void act() {
				setElementsEnabled(false);
				PrinterJob aqcuiredJob = null;
				if (previewMode == PreviewMode.PRINT_MEMBER_HISTORY) {
					aqcuiredJob = IE.getPrinterJob(historyPreview);
				} else if (previewMode == PreviewMode.PRINT_EVENT) {
					aqcuiredJob = IE.getPrinterJob(eventPreview);
				} else if (previewMode == PreviewMode.PRINT_SEASON_EVENTS) {
					aqcuiredJob = IE.getPrinterJob(seasonPreview);
				} else if (previewMode == PreviewMode.PRINT_SEASON_ROSTER) {
					aqcuiredJob = IE.getPrinterJob(rosterPreview);
				}
				final PrinterJob printerJob = aqcuiredJob;
				if (printerJob != null && printerJob.printDialog()) {
					if (previewMode == PreviewMode.PRINT_MEMBER_HISTORY) {
						progressDialog.display((ImageIcon) printAction.getValue(Action.LARGE_ICON_KEY), "Printing Member History");
					} else if (previewMode == PreviewMode.PRINT_EVENT) {
						progressDialog.display((ImageIcon) printAction.getValue(Action.LARGE_ICON_KEY), "Printing Event");
					} else if (previewMode == PreviewMode.PRINT_SEASON_EVENTS) {
						progressDialog.display((ImageIcon) printAction.getValue(Action.LARGE_ICON_KEY), "Printing Season Events");
					} else if (previewMode == PreviewMode.PRINT_SEASON_ROSTER) {
						if(rosterPreview.isPointsPreview()) {
							progressDialog.display((ImageIcon) printAction.getValue(Action.LARGE_ICON_KEY), "Printing Points");
						} else if (rosterPreview.isRosterPreview()) {
							progressDialog.display((ImageIcon) printAction.getValue(Action.LARGE_ICON_KEY), "Printing Season Roster");
						}
					}
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {
								printerJob.print();
							} catch (PrinterException exc) {
								progressDialog.setVisible(false);
								exc.printStackTrace();
								String message = "A problem has occured while printing! Consult the log for details.";
								JOptionPane.showMessageDialog(thisDialog, message, "Printing Exception", JOptionPane.ERROR_MESSAGE);
							}
							progressDialog.setVisible(false);
							setElementsEnabled(true);
						}
					});
				} else {
					setElementsEnabled(true);
				}
			}
		};
		pageSetupAction = new RunnableAction("Page Setup","Displaying page setup dialog and resizing preview") {
			public void act() {
				setVisible(false);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Tools.getProgramRoot().setLoading("Displaying page setup dialog and resizing preview");
						Tools.getProgramRoot().getActionMap().get("Page Setup").act();
						Tools.getProgramRoot().setLoading(null);
						if (previewMode == PreviewMode.EXPORT_MEMBER_HISTORY) {
							display(member, season, previewMode);
						} else if (previewMode == PreviewMode.EXPORT_EVENT) {
							display(event, null, previewMode);
						} else if (previewMode == PreviewMode.EXPORT_SEASON_EVENTS) {
							display(season, null, previewMode);
						} else if (previewMode == PreviewMode.EXPORT_SEASON_ROSTER) {
							display(season, null, previewMode);
						} else if (previewMode == PreviewMode.PRINT_MEMBER_HISTORY) {
							display(member, season, previewMode);
						} else if (previewMode == PreviewMode.PRINT_EVENT) {
							display(event, null, previewMode);
						} else if (previewMode == PreviewMode.PRINT_SEASON_EVENTS) {
							display(season, null, previewMode);
						} else if (previewMode == PreviewMode.PRINT_SEASON_ROSTER) {
							display(season, null, previewMode);
						}
					}
				});
			}
		};
		cancelAction = new RunnableAction("Done","Closing the preview dialog") {
			public void act() {
				thisDialog.setVisible(false);
			}
		};
		
		exportPDFAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_P);
		exportPDFAction.putValue(Action.SMALL_ICON,Tools.getIcon("acrobat16"));
		exportPDFAction.putValue(Action.LONG_DESCRIPTION,"Export the displayed preview to a PDF");
		exportPDFAction.putValue(Action.SHORT_DESCRIPTION,"Export to PDF");
		
		printAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_P);
		printAction.putValue(Action.SMALL_ICON,Tools.getIcon("print16"));
		printAction.putValue(Action.LONG_DESCRIPTION,"Open an OS print dialog to print the data shown in the preview");
		printAction.putValue(Action.SHORT_DESCRIPTION,"Print the displayed preview");
		
		pageSetupAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_S);
		pageSetupAction.putValue(Action.LONG_DESCRIPTION,"Open the OS page setup dialog, afterwhich the preview will be reloaded with the new page setup");
		pageSetupAction.putValue(Action.SHORT_DESCRIPTION,"Change the page setup");
		
		cancelAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
		cancelAction.putValue(Action.LONG_DESCRIPTION,"Close the preview dialog");
		cancelAction.putValue(Action.SHORT_DESCRIPTION,"Close this dialog");
	}
	
	private void a2Components() {
		centerPanel = new JPanel();
		rosterPreview = new RosterPreviewTable();
		eventPreview = new EventPreviewTable();
		seasonPreview = new SeasonPreviewTable();
		historyPreview = new HistoryPreviewTable();
		
		exportPDFButton = new JButton(exportPDFAction);
		printButton = new JButton(printAction);
		pageSetupButton = new JButton(pageSetupAction);
		cancelButton = new JButton(cancelAction);
	}
	
	private void a3Listeners() {
		
	}
	
	private void a4Layouts() {
		cardLayout = new CardLayout();
		setLayout(new GridBagLayout());
		centerPanel.setLayout(cardLayout);
		
		centerPanel.add(seasonPreview, "Season");
		centerPanel.add(rosterPreview, "Roster");
		centerPanel.add(eventPreview, "Event");
		centerPanel.add(historyPreview, "History");
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(GUIManager.INS,GUIManager.INS,GUIManager.INS,GUIManager.INS);
		
		int xIndex = 0;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 0; c.weighty = 0;
		c.gridx = xIndex; c.gridy = 1; xIndex++;
		add(exportPDFButton, c);
		c.gridx = xIndex; c.gridy = 1; xIndex++;
		add(printButton, c);
		c.gridx = xIndex; c.gridy = 1; xIndex++;
		add(pageSetupButton, c);
		
		c.weightx = 1; c.weighty = 0;
		c.gridx = xIndex; c.gridy = 1; xIndex++;
		add(Box.createHorizontalGlue(), c);
		c.weightx = 0; c.weighty = 0;
		c.gridx = xIndex; c.gridy = 1; xIndex++;
		add(cancelButton, c);
		
		c.gridwidth = xIndex; c.gridheight = 1;
		c.weightx = 0; c.weighty = 1;
		c.gridx = 0; c.gridy = 0;
		add(centerPanel, c);
	}
	
	private void a5Initialize() {
		setIconSize(Tools.getInteger("Toolbar Icon Size",32));
	}
	
	public void display(PepBandData data1, PepBandData data2, PreviewMode mode) {
		if (data1 != null && mode != null) {
			String titleString = "Preview";
			if (data1 instanceof Member && data2 instanceof Season && (mode == PreviewMode.EXPORT_MEMBER_HISTORY || mode == PreviewMode.PRINT_MEMBER_HISTORY)) {
				if (mode != previewMode) {
					fileChooser.setSelectedFile(null);
				} else if (!data1.equals(member) || !data2.equals(season)) {
					fileChooser.setSelectedFile(null);
				}
				previewMode = mode;
				member = (Member)data1;
				season = (Season)data2;
				event = null;
				titleString = "Preview - " + member.getFullName() + " History";
				cardLayout.show(centerPanel, "History");
				historyPreview.setPreviewPageSize();
				historyPreview.setMemberAndSeason(member, season);
			} else if (data1 instanceof PepBandEvent && (mode == PreviewMode.EXPORT_EVENT || mode == PreviewMode.PRINT_EVENT)) {
				if (mode != previewMode) {
					fileChooser.setSelectedFile(null);
				} else if (!data1.equals(event)) {
					fileChooser.setSelectedFile(null);
				}
				previewMode = mode;
				member = null;
				season = null;
				event = (PepBandEvent)data1;
				titleString = "Preview - " + event.getName();
				cardLayout.show(centerPanel, "Event");
				eventPreview.setPreviewPageSize();
				eventPreview.setEvent(event);
			} else if (data1 instanceof Season && (mode == PreviewMode.EXPORT_SEASON_EVENTS || mode == PreviewMode.PRINT_SEASON_EVENTS)) {
				if (mode != previewMode) {
					fileChooser.setSelectedFile(null);
				} else if (!data1.equals(season)) {
					fileChooser.setSelectedFile(null);
				}
				previewMode = mode;
				member = null;
				season = (Season)data1;
				event = null;
				titleString = "Preview - " + season.getName() + " Events";
				cardLayout.show(centerPanel, "Season");
				seasonPreview.setPreviewPageSize();
				seasonPreview.setSeason(season);
			} else if (data1 instanceof Season && (mode == PreviewMode.EXPORT_SEASON_ROSTER || mode == PreviewMode.PRINT_SEASON_ROSTER)) {
				if (mode != previewMode) {
					fileChooser.setSelectedFile(null);
				} else if (!data1.equals(season)) {
					fileChooser.setSelectedFile(null);
				}
				previewMode = mode;
				member = null;
				season = (Season)data1;
				event = null;
				titleString = "Preview - " + season.getName() + " Points & Roster";
				cardLayout.show(centerPanel, "Roster");
				rosterPreview.setPreviewPageSize();
				rosterPreview.setSeason(season);
			} else {
				return;
			}
			
			setTitle(titleString);
			setElementsEnabled(true);
			setButtons();
			
			setMinimumSize(null);
			pack();
			setLocationRelativeTo(getOwner());
			setMinimumSize(getSize());
			setVisible(true);
		}
	}
	
	private void setButtons() {
		exportPDFButton.setVisible(previewMode.isExporting());
		printButton.setVisible(previewMode.isPrinting());
		pageSetupButton.setVisible(true);
	}
	
	private void setElementsEnabled(boolean value) {
		printAction.setEnabled(value);
		pageSetupAction.setEnabled(value);
		exportPDFAction.setEnabled(value);
		setCursor(value ? new Cursor(Cursor.DEFAULT_CURSOR) : new Cursor(Cursor.WAIT_CURSOR));
	}
	
	public void setFileChooser(JFileChooser value) {
		if (value != null) {
			fileChooser = value;
		} else {
			throw new NullPointerException("PREVIEW DIALOG CANNOT HAVE NULL FILE CHOOSER");
		}
	}
	
	public void setFilters(FileNameExtensionFilter paramPDFfilter, FileNameExtensionFilter paramCSVfilter) {
		if (paramPDFfilter != null && paramCSVfilter != null) {
			pdfFilter = paramPDFfilter;
			csvFilter = paramCSVfilter;
		} else {
			throw new NullPointerException("PREVIEW DIALOG CANNOT HAVE NULL FILTERS");
		}
	}
	
	public void setIconSize(Integer value) {
		exportPDFAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("acrobat" + value));
		printAction.putValue(Action.LARGE_ICON_KEY,Tools.getIcon("print" + value));
	}
	
	public void setProgressDialog(ProgressDialog value) {
		if (value != null) {
			progressDialog = value;
		} else {
			throw new NullPointerException("PREVIEW DIALOG CANNOT HAVE NULL PROGRESS DIALOG");
		}
	}
	
	public void setShowIconText(boolean value) {
		
	}
	
	public static enum PreviewMode {
		EXPORT_MEMBER_HISTORY,
		EXPORT_EVENT,
		EXPORT_SEASON_EVENTS,
		EXPORT_SEASON_ROSTER,
		
		PRINT_MEMBER_HISTORY,
		PRINT_EVENT,
		PRINT_SEASON_EVENTS,
		PRINT_SEASON_ROSTER;
		
		public boolean isPrinting() {
			return this == PRINT_MEMBER_HISTORY || this == PRINT_EVENT || this == PRINT_SEASON_EVENTS || this == PRINT_SEASON_ROSTER;
		}
		
		public boolean isExporting() {
			return this == EXPORT_MEMBER_HISTORY || this == EXPORT_EVENT || this == EXPORT_SEASON_EVENTS || this == EXPORT_SEASON_ROSTER;
		}
	}
}