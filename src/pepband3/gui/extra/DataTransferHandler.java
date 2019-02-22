package pepband3.gui.extra;

import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.*;
import pepband3.gui.model.*;

public class DataTransferHandler extends TransferHandler {
	
	private static Component recentImporter = null;
	
	public DataTransferHandler() {
		
	}
	
	public boolean canImport(TransferSupport support) {
		if (support.getComponent() instanceof InternalWindow) {
			((InternalWindow)support.getComponent()).display();
		} else {
			InternalWindow window = getInternalWindowAncestor(support.getComponent());
			if (window != null) {
				window.display();
			}
		}
		if (support.isDataFlavorSupported(IDTransferable.FLAVOR)) {
			Component component = support.getComponent();
			if (component instanceof JTable) {
				JTable table = (JTable)component;
				return canImportTable(table);
			} else if (component instanceof InternalTableWindow) {
				JTable table = ((InternalTableWindow)component).getBandTable().getTable();
				return canImportTable(table);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private boolean canImportTable(JTable table) {
		if (table.getModel() instanceof BandTableModel) {
			BandTableModel model = (BandTableModel)table.getModel();
			if (model.getBand() instanceof PepBandEvent) {
				return true;
			} else if (model.getBand() instanceof Season) {
				return false;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	protected Transferable createTransferable(JComponent component) {
		if (component instanceof JTable && ((JTable)component).getModel() instanceof BandTableModel) {
			JTable table = (JTable)component;
			int[] selectedRows = table.getSelectedRows();
			if (selectedRows.length > 0) {
				Integer[] ids = new Integer[selectedRows.length];
				for (int index = 0; index < selectedRows.length; index++) {
					Member member = ((BandTableModel)table.getModel()).getBand().getMembers().get(table.convertRowIndexToModel(selectedRows[index]));
					ids[index] = member.getID();
				}
				return new IDTransferable(ids);
			}
		}
		return new IDTransferable(new Integer(-1));
	}
	
	protected void exportDone(JComponent component, Transferable transferable, int action) {
		if (action == MOVE) {
			if (recentImporter instanceof JTable && component == recentImporter) {
				//System.err.println("Table to Table export/import is to itself");
			} else if (recentImporter instanceof InternalTableWindow && component == ((InternalTableWindow)recentImporter).getBandTable().getTable()) {
				//System.err.println("Table to InternalTableWindow export/import is to the exporting table's own window");
			} else {
				try {
					if (transferable.isDataFlavorSupported(IDTransferable.FLAVOR)) {
						Object object = transferable.getTransferData(IDTransferable.FLAVOR);
						if (object instanceof Integer[]) {
							Integer[] ids = (Integer[])object;
							for (int index = 0; index < ids.length; index++) {
								PepBandData data = DataManager.getDataManager().getData(ids[index]);
								if (data != null && data instanceof Member) {
									Member member = (Member)data;
									if (component instanceof JTable && ((JTable)component).getModel() instanceof BandTableModel) {
										JTable table = (JTable)component;
										((BandTableModel)table.getModel()).getBand().removeMember(member);
									}
								}
							}
						}
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}
	
	public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
		recentImporter = null;
		super.exportToClipboard(comp,clip,action);
	}
	
	private InternalWindow getInternalWindowAncestor(Component component) {
		while (component != null && !(component instanceof InternalWindow)) {
			component = component.getParent();
		}
		return (InternalWindow)component;
	}
	
	public int getSourceActions(JComponent component) {
		if (component instanceof JTable) {
			JTable table = (JTable)component;
			if (table.getModel() instanceof BandTableModel) {
				BandTableModel model = (BandTableModel)table.getModel();
				if (model.getBand() instanceof PepBandEvent) {
					return COPY_OR_MOVE;
				} else if (model.getBand() instanceof Season) {
					return COPY;
				} else {
					return NONE;
				}
			} else {
				return NONE;
			}
		} else {
			return NONE;
		}
	}
	
	public Icon getVisualRepresentation(Transferable value) {
		if (value instanceof IDTransferable) {
			return Tools.getIcon("band16");
		} else {
			return null;
		}
	}

	public boolean importData(TransferSupport support) {
		if (!canImport(support)) {
			return false;
		} else {
			try {
				Object object = support.getTransferable().getTransferData(IDTransferable.FLAVOR);
				if (object instanceof Integer[]) {
					Integer[] ids = (Integer[])object;
					for (int index = 0; index < ids.length; index++) {
						PepBandData data = DataManager.getDataManager().getData(ids[index]);
						if (data != null && data instanceof Member) {
							Member member = (Member)data;
							if (support.getComponent() instanceof JTable) {
								importDataToTable(support,(JTable)support.getComponent(),member);
							} else if (support.getComponent() instanceof InternalTableWindow) {
								importDataToTable(support,((InternalTableWindow)support.getComponent()).getBandTable().getTable(),member);
							}
						}
					}
				}
				recentImporter = support.getComponent();
				return true;
			} catch (Exception exc) {
				exc.printStackTrace();
				return false;
			}
		}
	}
	
	private void importDataToTable(TransferSupport support, JTable table, Member member) {
		if (table.getModel() instanceof BandTableModel) {
			((BandTableModel)table.getModel()).getBand().addMember(member);
		}
	}
}