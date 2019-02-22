package pepband3.gui.component.popup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import pepband3.gui.*;

public class ScrollPopup extends JPopupMenu {
	
	private JScrollBar scrollBar;
	
	private int xClick;
	private int yClick;
	
	private JMenuItem itemScrollHere;
	private JMenuItem itemMax;
	private JMenuItem itemMin;
	private JMenuItem itemPagePlus;
	private JMenuItem itemPageMinus;
	private JMenuItem itemScrollPlus;
	private JMenuItem itemScrollMinus;
	
	private Action scrollHereAction, maxAction, minAction, pagePlusAction, pageMinusAction, scrollPlusAction, scrollMinusAction;
	
	public ScrollPopup(JScrollBar paramScrollBar) {
		super("Scroll Popup");
		
		setScrollBar(paramScrollBar);
		createActions();
		createComponents();
	}
	
	public void show(Component invoker, int x, int y) {
		xClick = x;
		yClick = y;
		super.show(invoker, x, y);
	}
	
	private void createActions() {
		scrollHereAction = new ScrollPopupAction("Scroll Here");
		if (scrollBar.getOrientation() == SwingConstants.VERTICAL) {
			maxAction = new ScrollPopupAction("Bottom");
			minAction = new ScrollPopupAction("Top");
			pagePlusAction = new ScrollPopupAction("Page Down");
			pageMinusAction = new ScrollPopupAction("Page Up");
			scrollPlusAction = new ScrollPopupAction("Scroll Down");
			scrollMinusAction = new ScrollPopupAction("Scroll Up");
		} else {
			maxAction = new ScrollPopupAction("Right Edge");
			minAction = new ScrollPopupAction("Left Edge");
			pagePlusAction = new ScrollPopupAction("Page Right");
			pageMinusAction = new ScrollPopupAction("Page Left");
			scrollPlusAction = new ScrollPopupAction("Scroll Right");
			scrollMinusAction = new ScrollPopupAction("Scroll Left");
		}
		
		scrollHereAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_H);
		scrollHereAction.putValue(Action.SMALL_ICON,Tools.getIcon("pointer16"));
		scrollHereAction.putValue(Action.LONG_DESCRIPTION,"Scroll to the point clicked on the bar");
		scrollHereAction.putValue(Action.SHORT_DESCRIPTION,"Scroll Here");
		
		if (scrollBar.getOrientation() == SwingConstants.VERTICAL) {
			maxAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_B);
			maxAction.putValue(Action.LONG_DESCRIPTION,"Scroll to the bottom");
			maxAction.putValue(Action.SHORT_DESCRIPTION,"Bottom");
			
			minAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_T);
			minAction.putValue(Action.LONG_DESCRIPTION,"Scroll to the top");
			minAction.putValue(Action.SHORT_DESCRIPTION,"Top");
			
			pagePlusAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
			pagePlusAction.putValue(Action.LONG_DESCRIPTION,"Scroll one page down");
			pagePlusAction.putValue(Action.SHORT_DESCRIPTION,"Page Down");
			
			pageMinusAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_U);
			pageMinusAction.putValue(Action.LONG_DESCRIPTION,"Scroll one page up");
			pageMinusAction.putValue(Action.SHORT_DESCRIPTION,"Page Up");
			
			scrollPlusAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_W);
			scrollPlusAction.putValue(Action.LONG_DESCRIPTION,"Scroll downward");
			scrollPlusAction.putValue(Action.SHORT_DESCRIPTION,"Scroll Down");
			
			scrollMinusAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_P);
			scrollMinusAction.putValue(Action.LONG_DESCRIPTION,"Scroll upward");
			scrollMinusAction.putValue(Action.SHORT_DESCRIPTION,"Scroll Up");
		} else {
			maxAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_E);
			maxAction.putValue(Action.LONG_DESCRIPTION,"Scroll to the left edge");
			maxAction.putValue(Action.SHORT_DESCRIPTION,"Left Edge");
			
			minAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
			minAction.putValue(Action.LONG_DESCRIPTION,"Scroll to the right edge");
			minAction.putValue(Action.SHORT_DESCRIPTION,"Right Edge");
			
			pagePlusAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_R);
			pagePlusAction.putValue(Action.LONG_DESCRIPTION,"Scroll one page right");
			pagePlusAction.putValue(Action.SHORT_DESCRIPTION,"Page Right");
			
			pageMinusAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_L);
			pageMinusAction.putValue(Action.LONG_DESCRIPTION,"Scroll one page left");
			pageMinusAction.putValue(Action.SHORT_DESCRIPTION,"Page Left");
			
			scrollPlusAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_I);
			scrollPlusAction.putValue(Action.LONG_DESCRIPTION,"Scroll rightward");
			scrollPlusAction.putValue(Action.SHORT_DESCRIPTION,"Scroll Right");
			
			scrollMinusAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_F);
			scrollMinusAction.putValue(Action.LONG_DESCRIPTION,"Scroll leftward");
			scrollMinusAction.putValue(Action.SHORT_DESCRIPTION,"Scroll Left");
		}
	}
	
	private void createComponents() {
		itemScrollHere = new JMenuItem(scrollHereAction);
		itemMin = new JMenuItem(minAction);
		itemMax = new JMenuItem(maxAction);
		itemPageMinus = new JMenuItem(pageMinusAction);
		itemPagePlus = new JMenuItem(pagePlusAction);
		itemScrollMinus = new JMenuItem(scrollMinusAction);
		itemScrollPlus = new JMenuItem(scrollPlusAction);
		
		super.add(itemScrollHere);
		super.addSeparator();
		super.add(itemMin);
		super.add(itemMax);
		super.addSeparator();
		super.add(itemPageMinus);
		super.add(itemPagePlus);
		super.addSeparator();
		super.add(itemScrollMinus);
		super.add(itemScrollPlus);
	}
	
	public void setScrollBar(JScrollBar value) {
		if (value != null) {
			scrollBar = value;
		} else {
			throw new NullPointerException("SCROLL POPUP CANNOT HAVE A NULL SCROLL BAR");
		}
	}
	
	private class ScrollPopupAction extends AbstractAction {
		public ScrollPopupAction() {
			super();
		}
		
		public ScrollPopupAction(String value) {
			super(value);
		}
		
		public void actionPerformed(ActionEvent e) {
			if (this == scrollHereAction) {
				if (scrollBar.getOrientation() == SwingConstants.VERTICAL) {
					int arrowSize = scrollBar.getWidth();
					final double value = (1.0 * (yClick - arrowSize) / (scrollBar.getHeight() - 2 * arrowSize)) * scrollBar.getMaximum() - 0.5 * scrollBar.getVisibleAmount();
					scrollBar.setValue((int)value);
				} else {
					int arrowSize = scrollBar.getHeight();
					final double value = (1.0 * (xClick - arrowSize) / (scrollBar.getWidth() - 2 * arrowSize)) * scrollBar.getMaximum() - 0.5 * scrollBar.getVisibleAmount();
					scrollBar.setValue((int)value);
				}
			} else if (this == maxAction) {
				scrollBar.setValue(scrollBar.getMaximum());
			} else if (this == minAction) {
				scrollBar.setValue(scrollBar.getMinimum());
			} else if (this == pagePlusAction) {
				scrollBar.setValue(scrollBar.getValue() + scrollBar.getBlockIncrement(1));
			} else if (this == pageMinusAction) {
				scrollBar.setValue(scrollBar.getValue() - scrollBar.getBlockIncrement(-1));
			}  else if (this == scrollPlusAction) {
				scrollBar.setValue(scrollBar.getValue() + scrollBar.getUnitIncrement(1));
			}  else if (this == scrollMinusAction) {
				scrollBar.setValue(scrollBar.getValue() - scrollBar.getUnitIncrement(-1));
			} 
		}
	}
}