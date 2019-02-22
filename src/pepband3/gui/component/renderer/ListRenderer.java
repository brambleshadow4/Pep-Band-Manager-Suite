package pepband3.gui.component.renderer;

import java.awt.*;
import javax.swing.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.extra.*;

public class ListRenderer extends DefaultListCellRenderer {
	
	private DataField dataField;
	private Band band;
	
	public ListRenderer() {
		this(DataField.FULL_NAME, null);
	}
	
	public ListRenderer(Band paramBand) {
		this(DataField.FULL_NAME, paramBand);
	}
	
	public ListRenderer(DataField paramDataField, Band paramBand) {
		setDataField(paramDataField);
		band = paramBand;
		initializeComponent();
	}
	
	public Band getBand() {
		return band;
	}
	
	public DataField getDataField() {
		return dataField;
	}
	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)  {
		JLabel component = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		component.setIcon(null);
		if (value == null) {
			component.setText("<Null Value>");
		} else if (value instanceof Instrument) {
			Instrument data = (Instrument)value;
			component.setText(data.getName());
			component.setIcon(Tools.getInstrumentIcon(data.getIconName() + "16"));
		} else if (value instanceof Location) {
			Location data = (Location)value;
			component.setText(data.getName());
			component.setIcon(Tools.getLocationIcon(data.getIconName() + "16"));
		} else if (value instanceof EventType) {
			EventType data = (EventType)value;
			component.setText(data.getName());
			component.setIcon(Tools.getEventIcon(data.getIconName() + "16"));
		} else if (value instanceof String) {
			String data = (String)value;
			component.setText(data);
		} else if (value instanceof Integer) {
			Integer data = (Integer)value;
			component.setText(data.toString());
		} else if (value instanceof Season) {
			Season data = (Season)value;
			component.setText(data.getName());
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
		} else if (value instanceof Member.Sex) {
			Member.Sex sex = (Member.Sex) value;
			component.setText(sex.getName());
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
			throw new NullPointerException("DATA FIELD FOR LIST RENDERER CANNOT BE NULL");
		}
	}
}