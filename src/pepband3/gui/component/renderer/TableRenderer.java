package pepband3.gui.component.renderer;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import pepband3.data.*;
import pepband3.gui.extra.*;

public class TableRenderer extends DefaultTableCellRenderer {
	
	private DataField dataField;
	private Band band;
	
	public TableRenderer(Band paramBand) {
		this(DataField.FULL_NAME, paramBand);
	}
	
	public TableRenderer(DataField paramDataField, Band paramBand) {
		setDataField(paramDataField);
		band = paramBand;
		initializeComponent();
	}
	
	public DataField getDataField() {
		return dataField;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel component = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		component.setIcon(null);
		if (value == null) {
			component.setText("<Null Value>");
		} else if (dataField == DataField.ID) {
			if (value instanceof PepBandData) {
				component.setText(((PepBandData) value).getID().toString());
			} else {
				component.setText("CANNOT ID NON-PEP_BAND_DATA");
			}
		} else if (dataField == DataField.DATA_TYPE) {
			if (value instanceof Season) {
				component.setText("Season");
			} else if (value instanceof PepBandEvent) {
				component.setText("Event");
			} else if (value instanceof Member) {
				component.setText("Member");
			} else if (value instanceof Instrument) {
				component.setText("Instrument");
			} else if (value instanceof EventType) {
				component.setText("Event Type");
			} else if (value instanceof Location) {
				component.setText("Location");
			} else {
				component.setText("DATA TYPE OF VALUE NOT RECOGNIZED");
			}
		} else if (value instanceof Season) {
			Season data = (Season)value;
			component.setText(data.getName());
		} else if (value instanceof PepBandEvent) {
			PepBandEvent data = (PepBandEvent)value;
			component.setText(data.getName());
		} else if (value instanceof Instrument) {
			Instrument data = (Instrument)value;
			component.setText(data.getName());
		} else if (value instanceof Location) {
			Location data = (Location)value;
			component.setText(data.getName());
		} else if (value instanceof EventType) {
			EventType data = (EventType)value;
			component.setText(data.getName());
		} else if (value instanceof String) {
			String data = (String)value;
			component.setText(data);
		} else if (value instanceof Integer) {
			Integer data = (Integer)value;
			component.setText(data.toString());
		} else if (value instanceof Member) {
			Member member = (Member)value;
			if (dataField == DataField.INSTRUMENT) {
				component.setText(member.getInstrument().getName());
			} else if (dataField == DataField.NICK_NAME) {
				component.setText(member.getNickName());
			} else if (dataField == DataField.POINTS) {
				component.setText(member.getPoints(band).toString());
			} else if (dataField == DataField.NAME) {
				component.setText(member.getName());
			} else if (dataField == DataField.FULL_NAME) {
				component.setText(member.getFullName());
			} else if (dataField == DataField.CLASS_YEAR) {
				component.setText(member.getClassYear().toString());
			} else if (dataField == DataField.NET_ID) {
				component.setText(member.getNetID());
			} else if (dataField == DataField.NAME_FIRST_LAST) {
				component.setText(member.getNameFirstLast());
			} else {
				component.setText("MEMBER DATA FIELD UNRECOGNIZED");
			}
		} else {
			component.setText("CLASS OF VALUE UNRECOGNIZED");
		}
		return component;
	}
	
	private void initializeComponent() {
		setHorizontalAlignment(SwingConstants.LEFT);
	}
	
	public void setDataField(DataField value) {
		if (value != null) {
			dataField = value;
		} else {
			throw new NullPointerException("DATA FIELD FOR TABLE RENDERER CANNOT BE NULL");
		}
	}
}