package pepband3.data.extra;

import java.awt.datatransfer.*;

import pepband3.data.*;

public class IDTransferable implements Transferable {
	
	public static final DataFlavor FLAVOR = new DataFlavor(Integer.class,"Pep Band Data ID Array");
	
	private Integer[] values;
	
	public IDTransferable(Integer paramID) {
		if (paramID != null) {
			values = new Integer[1];
			values[0] = paramID;
		} else {
			throw new NullPointerException("ID FOR IDTRANSFERABLE IS NULL");
		}
	}
	
	public IDTransferable(Integer[] paramIDs) {
		if (paramIDs != null) {
			values = paramIDs;
		} else {
			throw new NullPointerException("IDS FOR IDTRANSFERABLE IS NULL");
		}
	}
	
	public Object getTransferData(DataFlavor flavor) {
		if (flavor.equals(FLAVOR)) {
			return values;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			StringBuilder builder = new StringBuilder();
			for (Integer value : values) {
				if (value >= 0) {
					builder.append(DataManager.getDataManager().getData(value).toString());
					builder.append(System.getProperty("line.separator"));
				}
			}
			return builder.toString();
		} else {
			return null;
		}
	}
	
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors = {FLAVOR, DataFlavor.stringFlavor};
		return flavors;
	}
	
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor.equals(FLAVOR)) {
			return true;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			return true;
		} else {
			return false;
		}
	}
}