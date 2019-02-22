package pepband3.gui.component.tab;

import java.awt.*;
import pepband3.data.*;
import pepband3.gui.component.*;
import pepband3.gui.extra.*;

public class AdditionTab extends BandTableTab {
	
	private AdditionTable additionTable;
	
	public AdditionTab(Band paramRosterBand, Band paramTargetBand, DataField paramNameField) {
		a1Actions();
		a2Components(paramRosterBand,paramTargetBand,paramNameField);
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		
	}
	
	private void a2Components(Band paramRosterBand, Band paramTargetBand, DataField paramNameField) {
		additionTable = new AdditionTable(paramRosterBand,paramTargetBand,paramNameField);
	}
	
	private void a3Listeners() {
		
	}
	
	private void a4Layouts() {
		setLayout(new BorderLayout());
		
		add(additionTable,BorderLayout.CENTER);
	}
	
	private void a5Initialize() { 
		
	}
	
	public AdditionTable getAdditionTable() {
		return additionTable;
	}
	
	public Integer getIndex() {
		return new Integer(0);
	}
	
	public String getTabIconName() {
		return "add";
	}
	
	public String getTabName() {
		return "Addition";
	}
	
	public String getToolTipText() {
		return "Addition Tab";
	}
	
	public ViewManager getViewManager() {
		return getAdditionTable().getViewManager();
	}
}