package pepband3.gui.component.preview;

import java.text.*;
import java.util.*;
import pepband3.data.*;
import pepband3.gui.component.renderer.*;
import pepband3.gui.model.*;

public class HistoryPreviewTable extends PreviewTable {
	
	private static final int DATE_COL_WIDTH = 75;
	private static final int POINT_COL_WIDTH = 50;
	
	private Member member;
	private Season season;
	
	public HistoryPreviewTable() {
		super();
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		
	}
	
	private void a2Components() {
		getHeaderTable().setSelectionModel(new AntiSelectionModel());
	}
	
	private void a3Listeners() {
		
	}
	
	private void a4Layouts() {
	}
	
	private void a5Initialize() {
		
	}
	
	public Member getMember() {
		return member;
	}
	
	public Season getSeason() {
		return season;
	}
	
	public void setMemberAndSeason(Member paramMember, Season paramSeason) {
		if (paramMember != null && paramSeason != null) {
			member = paramMember;
			season = paramSeason;
			setTables();
		}
	}
	
	protected void setTables() {
		UneditableTableModel headerModel = new UneditableTableModel();
		Vector<Object> modelData = new Vector<Object>();
		String dateString = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(Calendar.getInstance().getTime());
		modelData.add(member.getFullName() + " Member History for " + season.getName());
		modelData.add("Reported " + dateString);
		modelData.add("");
		headerModel.addColumn("Header", modelData);
		
		UneditableTableModel dataModel = new UneditableTableModel();
		Vector<Object> dateColumn = new Vector<Object>();
		Vector<Object> eventTypeColumn = new Vector<Object>();
		Vector<Object> eventNameColumn = new Vector<Object>();
		Vector<Object> pointValueColumn = new Vector<Object>();
		Vector<Object> cumulativePointColumn = new Vector<Object>();
		
		dateColumn.add(new PreviewRenderer.HeaderIdentifier("Date"));
		eventTypeColumn.add(new PreviewRenderer.HeaderIdentifier("Event Type"));
		eventNameColumn.add(new PreviewRenderer.HeaderIdentifier("Name"));
		pointValueColumn.add(new PreviewRenderer.HeaderIdentifier("Value"));
		cumulativePointColumn.add(new PreviewRenderer.HeaderIdentifier("Points"));
		
		dateColumn.add("");
		eventTypeColumn.add("");
		eventNameColumn.add("");
		pointValueColumn.add("");
		cumulativePointColumn.add("");
		
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		for (PepBandEvent event : member.getEvents(season)) {
			dateColumn.add(dateFormat.format(event.getDate()));
			eventTypeColumn.add(event.getEventType().getName());
			eventNameColumn.add(event.getName());
			pointValueColumn.add(event.getPointValue().toString());
			cumulativePointColumn.add(Integer.toString(member.getPointsBefore(season, event.getDate()) + event.getPointValue()));
		}
		
		dataModel.addColumn("Date", dateColumn);
		dataModel.addColumn("Type", eventTypeColumn);
		dataModel.addColumn("Name", eventNameColumn);
		dataModel.addColumn("Value", pointValueColumn);
		dataModel.addColumn("Points", cumulativePointColumn);
		
		headerTable.setModel(headerModel);
		memberTable.setModel(dataModel);
		
		for (int row = 0; row < headerTable.getRowCount(); row++) {
			if (row == 0) {
				headerTable.setRowHeight(row, 16);
			} else {
				headerTable.setRowHeight(row, 14);
			}
		}
		
		for (int row = 0; row < memberTable.getRowCount(); row++) {
			if (row == 0) {
				memberTable.setRowHeight(row, 16);
			} else {
				memberTable.setRowHeight(row, 14);
			}
		}
		
		for (int column = 0; column < headerTable.getColumnCount(); column++) {
			headerTable.getColumnModel().getColumn(column).setCellRenderer(headerRenderer);
		}
		
		for (int column = 0; column < memberTable.getColumnCount(); column++) {
			memberTable.getColumnModel().getColumn(column).setCellRenderer(memberRenderer);
		}
		
		memberTable.getColumnModel().getColumn(0).setPreferredWidth(DATE_COL_WIDTH);
		memberTable.getColumnModel().getColumn(0).setMaxWidth(DATE_COL_WIDTH);
		memberTable.getColumnModel().getColumn(0).setResizable(false);
		
		memberTable.getColumnModel().getColumn(3).setPreferredWidth(POINT_COL_WIDTH);
		memberTable.getColumnModel().getColumn(3).setMaxWidth(POINT_COL_WIDTH);
		memberTable.getColumnModel().getColumn(3).setResizable(false);
		
		memberTable.getColumnModel().getColumn(4).setPreferredWidth(POINT_COL_WIDTH);
		memberTable.getColumnModel().getColumn(4).setMaxWidth(POINT_COL_WIDTH);
		memberTable.getColumnModel().getColumn(4).setResizable(false);
	}
}