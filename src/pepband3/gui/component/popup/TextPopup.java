package pepband3.gui.component.popup;

import javax.swing.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import pepband3.gui.*;

public class TextPopup extends JPopupMenu {
	private JTextComponent textComponent;
	
	private JMenuItem menuItemCut;
	private JMenuItem menuItemCopy;
	private JMenuItem menuItemPaste;
	private JMenuItem menuItemDelete;
	private JMenuItem menuItemSelectAll;
	
	private Action cutAction, copyAction, pasteAction, deleteAction, selectAllAction;
	
	public TextPopup(JTextComponent paramTextComponent) {
		super("Text Popup");
		
		setTextComponent(paramTextComponent);
		createActions();
		createComponents();
	}
	
	public void show(Component invoker, int x, int y) {
		if (textComponent.getText() == null || textComponent.getText().isEmpty()){
			selectAllAction.setEnabled(false);
		} else {
			selectAllAction.setEnabled(true);
		}
		if (textComponent.isEditable()) {
			pasteAction.setEnabled(false);
			DataFlavor[] availableFlavors = Toolkit.getDefaultToolkit().getSystemClipboard().getAvailableDataFlavors();
			if (textComponent.getTransferHandler().canImport(textComponent,availableFlavors)) {
				pasteAction.setEnabled(true);
			}
			if (textComponent.getSelectedText() == null || textComponent.getSelectedText().isEmpty()) {
				cutAction.setEnabled(false);
				copyAction.setEnabled(false);
				deleteAction.setEnabled(false);
			} else {
				cutAction.setEnabled(true);
				copyAction.setEnabled(true);
				deleteAction.setEnabled(true);
			}
		} else {
			cutAction.setEnabled(false);
			pasteAction.setEnabled(false);
			deleteAction.setEnabled(false);
			if (textComponent.getSelectedText() == null || textComponent.getSelectedText().isEmpty()) {
				copyAction.setEnabled(false);
			} else {
				copyAction.setEnabled(true);
			}
		}
		textComponent.requestFocusInWindow();
		super.show(invoker, x, y);
	}
	
	private void createActions() {
		cutAction = new TextPopupAction("Cut");
		copyAction = new TextPopupAction("Copy");
		pasteAction = new TextPopupAction("Paste");
		deleteAction = new TextPopupAction("Delete");
		selectAllAction = new TextPopupAction("Select All");
		
		cutAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_T);
		cutAction.putValue(Action.SMALL_ICON,Tools.getIcon("cut16"));
		cutAction.putValue(Action.LONG_DESCRIPTION,"Cut selection and put on clipboard");
		cutAction.putValue(Action.SHORT_DESCRIPTION,"Cut");
		
		copyAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_C);
		copyAction.putValue(Action.SMALL_ICON,Tools.getIcon("copy16"));
		copyAction.putValue(Action.LONG_DESCRIPTION,"Copy selection to clipboard");
		copyAction.putValue(Action.SHORT_DESCRIPTION,"Copy");
		
		pasteAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_P);
		pasteAction.putValue(Action.SMALL_ICON,Tools.getIcon("paste16"));
		pasteAction.putValue(Action.LONG_DESCRIPTION,"Paste text from clipboard");
		pasteAction.putValue(Action.SHORT_DESCRIPTION,"Paste");
		
		deleteAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_D);
		deleteAction.putValue(Action.SMALL_ICON,Tools.getIcon("remove16"));
		deleteAction.putValue(Action.LONG_DESCRIPTION,"Delete selection");
		deleteAction.putValue(Action.SHORT_DESCRIPTION,"Delete");
		
		selectAllAction.putValue(Action.MNEMONIC_KEY,KeyEvent.VK_A);
		selectAllAction.putValue(Action.LONG_DESCRIPTION,"Select all text");
		selectAllAction.putValue(Action.SHORT_DESCRIPTION,"Select All");
	}
	
	private void createComponents() {
		menuItemCut = new JMenuItem(cutAction);
		menuItemCopy = new JMenuItem(copyAction);
		menuItemPaste = new JMenuItem(pasteAction);
		menuItemDelete = new JMenuItem(deleteAction);
		menuItemSelectAll = new JMenuItem(selectAllAction);
		
		super.add(menuItemCut);
		super.add(menuItemCopy);
		super.add(menuItemPaste);
		super.add(menuItemDelete);
		super.addSeparator();
		super.add(menuItemSelectAll);
	}
	
	public void setTextComponent(JTextComponent value) {
		if (value != null) {
			textComponent = value;
		} else {
			throw new NullPointerException("TEXT POPUP COMPONENT CANNOT BE NULL");
		}
	}
	
	private class TextPopupAction extends AbstractAction {
		public TextPopupAction() {
			super();
		}
		
		public TextPopupAction(String value) {
			super(value);
		}
		 
		public void actionPerformed(ActionEvent e) {
			if (this == cutAction) {
				textComponent.cut();
			} else if (this == copyAction) {
				textComponent.copy();
			} else if (this == pasteAction) {
				textComponent.paste();
			} else if (this == deleteAction) {
				textComponent.replaceSelection("");
			} else if (this == selectAllAction) {
				textComponent.selectAll();
			} 
		}
	}
}