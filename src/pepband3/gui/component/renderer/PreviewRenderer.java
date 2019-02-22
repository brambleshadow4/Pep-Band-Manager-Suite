package pepband3.gui.component.renderer;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import pepband3.data.*;
import pepband3.gui.*;
import pepband3.gui.extra.*;

public class PreviewRenderer extends DefaultTableCellRenderer {
	
	public static final Font HEADER_FONT = new Font("Sans-Serif", Font.BOLD, 14);
	public static final Font HEADER_LITE_FONT = new Font("Sans-Serif", Font.BOLD, 10);
	public static final Font INSTRUMENT_FONT = new Font("Sans-Serif", Font.BOLD, 12);
	public static final Font STD_FONT = new Font("Sans-Serif", Font.PLAIN, 10);
	
	private DataField dataField;
	private Color foregroundColor;
	
	public PreviewRenderer() {
		this(DataField.NAME);
	}
	
	public PreviewRenderer(DataField paramDataField) {
		setDataField(paramDataField);
		setForegroundColor(new Color(Tools.getInteger("Preview Text Color", Color.BLACK.getRGB())));
	}
	
	public Color getForegroundColor() {
		return foregroundColor;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel component = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		component.setIcon(null);
		component.setForeground(foregroundColor);
		if (!isSelected) {
			component.setOpaque(false);
		} else {
			component.setOpaque(true);
		}
		if (table.getName().equals("HEADER_TABLE")) {
			if (row == 0) {
				component.setFont(HEADER_FONT);
			} else {
				component.setFont(HEADER_LITE_FONT);
			}
		} else if (table.getName().equals("MEMBER_TABLE")) {
			if (value instanceof Instrument || value instanceof HeaderIdentifier) {
				component.setFont(INSTRUMENT_FONT);
			} else {
				component.setFont(STD_FONT);
				if (value instanceof Member) {
					Member member = (Member)value;
					switch (dataField) {
						case NAME : component.setText(member.getName()); break;
						case FULL_NAME : component.setText(member.getFullName()); break;
						case LAST_NAME : component.setText(member.getLastName()); break;
						case FIRST_NAME : component.setText(member.getFirstName()); break;
						case NICK_NAME : component.setText(member.getNickName()); break;
						case NAME_FIRST_LAST : component.setText(member.getNameFirstLast()); break;
						default : component.setText(member.getName());
					}
				}
			}
		} else {
			
		}
		return component;
	}
	
	public void setDataField(DataField value) {
		if (value != null) {
			if (value == DataField.NAME || value == DataField.FULL_NAME || value == DataField.NICK_NAME || value == DataField.LAST_NAME || value == DataField.FIRST_NAME || value == DataField.NAME_FIRST_LAST) {
				dataField = value;
			} else {
				throw new IllegalArgumentException("PREVIEW RENDERER DATA FIELD MUST BE A NAME TYPE");
			}
		} else {
			throw new NullPointerException("PREVIEW RENDERER DATA FIELD CANNOT BE NULL");
		}
	}
	
	public void setForegroundColor(Color value) {
		if (value != null) {
			foregroundColor = value;
		} else {
			throw new NullPointerException("PREVIEW RENDERER COLOR CANNOT BE NULL");
		}
	}
	
	public static class HeaderIdentifier {
		
		private String value;
		
		public HeaderIdentifier(String value) {
			this.value = value;
		}
		
		public String toString() {
			return value;
		}
	}
}