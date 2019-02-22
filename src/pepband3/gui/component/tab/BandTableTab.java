package pepband3.gui.component.tab;

import javax.swing.*;

public abstract class BandTableTab extends JPanel implements Comparable<BandTableTab> {
	
	public int compareTo(BandTableTab other) {
		return getIndex().compareTo(other.getIndex());
	}
	
	public abstract Integer getIndex();
	
	public abstract String getTabIconName();
	
	public abstract String getTabName();
	
	public abstract String getToolTipText();
}