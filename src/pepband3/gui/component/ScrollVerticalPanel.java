package pepband3.gui.component;

import java.awt.*;
import javax.swing.*;

public class ScrollVerticalPanel extends JPanel implements Scrollable {
	
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 20;
	}
	
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}
	
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}
}