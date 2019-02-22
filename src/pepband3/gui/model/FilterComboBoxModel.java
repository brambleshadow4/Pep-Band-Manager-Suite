package pepband3.gui.model;

import javax.swing.*;
import pepband3.data.*;
import pepband3.data.extra.*;
import pepband3.gui.component.tab.*;
import pepband3.gui.extra.*;

public class FilterComboBoxModel extends AbstractListModel implements ComboBoxModel {
	
	private DataListener dataListener;
	private DataField dataField;
	private Band band;
	private Season season;
	private Object selectedItem;
	
	public FilterComboBoxModel(DataField paramDataField, Band paramBand) {
		initialize(paramDataField, paramBand);
		setSelectedItem(FilterTab.ALL_STRING);
	}
	
	public FilterComboBoxModel(DataField paramDataField, Season paramSeason) {
		initialize(paramDataField, paramSeason);
		setSelectedItem(FilterTab.ALL_STRING);
	}
	
	private void initialize(DataField paramDataField, Band paramBand) {
		if (paramDataField != null) {
			band = paramBand;
			dataField = paramDataField;
			dataListener = new DataListener() {
				public void eventOccured(SourceEvent sourceEvent) {
					if (sourceEvent.isField()) {
						SourceEvent.Field fieldEvent = (SourceEvent.Field)sourceEvent;
						if (fieldEvent.getField() == dataField) {
							fireContentsChanged(band, 0, 0);
						} else if (fieldEvent.getOwner() instanceof EventType) {
							fireContentsChanged(band, 0, 0);
						} else if (fieldEvent.getOwner() instanceof Location) {
							fireContentsChanged(band, 0, 0);
						} else if (fieldEvent.getOwner() instanceof Instrument) {
							fireContentsChanged(band, 0, 0);
						}
					} else if (sourceEvent.isList()) {
						SourceEvent.List listEvent = (SourceEvent.List)sourceEvent;
						if (listEvent.getOwner().equals(band) && listEvent.getElement() instanceof Member && (listEvent.getType() == SourceEvent.List.ADD || listEvent.getType() == SourceEvent.List.REMOVE)) {
							fireContentsChanged(band, 0, 0);
						}
					}
				}
			};
			DataManager.getDataManager().addDataListener(dataListener);
		} else {
			throw new NullPointerException("CANNOT INITIALIZE DATA FIELD TO NULL IN FILTER COMBO BOX MODEL");
		}
	}
	
	
	
	private void initialize(DataField paramDataField, Season paramSeason) {
		if (paramDataField != null) {
			season = paramSeason;
			dataField = paramDataField;
		} else {
			throw new NullPointerException("CANNOT INITIALIZE DATA FIELD TO NULL IN FILTER COMBO BOX MODEL");
		}
	}
	
	public Object getElementAt(int index) {
		if (index == 0) {
			return FilterTab.ALL_STRING;
		} else if (band != null) {
			if (dataField == DataField.CLASS_YEAR) {
				return band.getClassYears().get(index - 1);
			} else if (dataField == DataField.INSTRUMENT) {
				return band.getInstruments().get(index - 1);
			} else {
				throw new IllegalStateException(dataField + " IS NOT VALID DATA FIELD FOR A BAND-ARMED FILTER COMBO BOX MODEL");
			}
		} else if (season != null) {
			if (dataField == DataField.EVENT_TYPE) {
				return season.getEventTypes().get(index - 1);
			} else if (dataField == DataField.LOCATION) {
				return season.getLocations().get(index - 1);
			} else {
				throw new IllegalStateException(dataField + " IS NOT VALID DATA FIELD FOR A SEASON-ARMED FILTER COMBO BOX MODEL");
			}
		} else {
			throw new IndexOutOfBoundsException(index + " IS NOT VALID INDEX FOR FILTER COMBO BOX MODEL");
		}
	}
	
	public Object getSelectedItem() {
		return selectedItem;
	}
	
	public int getSize() {
		if (band != null) {
			if (dataField == DataField.CLASS_YEAR) {
				return band.getClassYears().size() + 1;
			} else if (dataField == DataField.INSTRUMENT) {
				return band.getInstruments().size() + 1;
			} else {
				return 1;
			}
		} else if (season != null) {
			if (dataField == DataField.EVENT_TYPE) {
				return season.getEventTypes().size() + 1;
			} else if (dataField == DataField.LOCATION) {
				return season.getLocations().size() + 1;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}
	
	public void setSeason(Season value) {
		season = value;
		setSelectedItem(FilterTab.ALL_STRING);
	}
	
	public void setSelectedItem(Object anItem) {
		selectedItem = anItem;
		fireContentsChanged(this, 0, 0);
	}
	
	public void uninstall() {
		DataManager.getDataManager().removeDataListener(dataListener);
	}
}