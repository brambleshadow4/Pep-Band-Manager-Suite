package pepband3.gui.component;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import pepband3.gui.*;
import pepband3.gui.extra.*;

public class SearchField extends JPanel {
	
	private JPopupMenu optionsMenu;
		private JCheckBoxMenuItem firstNameItem;
		private JCheckBoxMenuItem lastNameItem;
		private JCheckBoxMenuItem nickNameItem;
		private JCheckBoxMenuItem netIDItem;
		private JCheckBoxMenuItem classYearItem;
		private JCheckBoxMenuItem instrumentItem;
		
	private JTextField textField;
	private JButton optionsButton;
	
	private Action optionsAction;
	
	private ViewManager viewManager;
	private AdditionTable additionTable;
	
	public SearchField(ViewManager paramViewManager, AdditionTable paramTable) {
		if (paramViewManager != null) {
			viewManager = paramViewManager;
		} else {
			throw new NullPointerException("SEARCH FIELD VIEW MANAGER IS NULL");
		}
		additionTable = paramTable;
		
		a1Actions();
		a2Components();
		a3Listeners();
		a4Layouts();
		a5Initialize();
	}
	
	private void a1Actions() {
		optionsAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				optionsMenu.show(optionsButton, 0, optionsButton.getHeight() - 1);
			}
		};
		
		optionsAction.putValue(Action.SMALL_ICON,Tools.getIcon("search16"));
		optionsAction.putValue(Action.LONG_DESCRIPTION,"Select fields to include in search");
		optionsAction.putValue(Action.SHORT_DESCRIPTION,"Select search fields");
	}
	
	private void a2Components() {
		optionsMenu = new JPopupMenu("Search Field Options");
		firstNameItem = new JCheckBoxMenuItem("First Name");
		lastNameItem = new JCheckBoxMenuItem("Last Name");
		nickNameItem = new JCheckBoxMenuItem("Nickname");
		netIDItem = new JCheckBoxMenuItem("Net ID");
		classYearItem = new JCheckBoxMenuItem("Class Year");
		instrumentItem = new JCheckBoxMenuItem("Instrument");
		
		textField = new JTextField();
		textField.setEditable(true);
		optionsButton = new JButton(optionsAction);
		
		Tools.applyTextPopup(textField);
		Tools.addRootComponent(optionsMenu);
	}
	
	private void a3Listeners() {
		if (additionTable != null) {
			textField.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
					JTable table = additionTable.getTable();
					if (e.getComponent() == textField) {
						if (key == KeyEvent.VK_UP) {
							int index = table.getSelectedRow();
							if (table.getRowCount() == 0) {
								table.clearSelection();
							} else if (index > 0) {
								table.setRowSelectionInterval(index - 1, index - 1);
								table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), 0, true));
							} else {
								table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
								table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), 0, true));
							}
							e.consume();
						} else if (key == KeyEvent.VK_DOWN) {
							int index = table.getSelectedRow();
							if (table.getRowCount() == 0) {
								table.clearSelection();
							} else if (index < table.getRowCount() - 1) {
								table.setRowSelectionInterval(index + 1, index + 1);
								table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), 0, true));
							} else {
								table.setRowSelectionInterval(0, 0);
								table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), 0, true));
							}
							e.consume();
						} else if (key == KeyEvent.VK_ENTER) {
							additionTable.addSelectedMembers();
							textField.setText("");
							viewManager.getFilter().setComboFitler(null);
							viewManager.sort();
							e.consume();
						}
					}
				}
			});
		}
		textField.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				textField.selectAll();
			}
		});
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				applyFilter(textField.getText());
			}
			
			public void insertUpdate(DocumentEvent e) {
				applyFilter(textField.getText());
			}
			
			public void removeUpdate(DocumentEvent e) {
				applyFilter(textField.getText());
			}
		});
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getSource();
				if (source == firstNameItem) {
					if (firstNameItem.isSelected()) {
						viewManager.getFilter().getOptions().add(DataField.FIRST_NAME);
					} else {
						viewManager.getFilter().getOptions().remove(DataField.FIRST_NAME);
					}
					viewManager.sort();
				} else if (source == lastNameItem) {
					if (lastNameItem.isSelected()) {
						viewManager.getFilter().getOptions().add(DataField.LAST_NAME);
					} else {
						viewManager.getFilter().getOptions().remove(DataField.LAST_NAME);
					}
					viewManager.sort();
				} else if (source == nickNameItem) {
					if (nickNameItem.isSelected()) {
						viewManager.getFilter().getOptions().add(DataField.NICK_NAME);
					} else {
						viewManager.getFilter().getOptions().remove(DataField.NICK_NAME);
					}
					viewManager.sort();
				} else if (source == netIDItem) {
					if (netIDItem.isSelected()) {
						viewManager.getFilter().getOptions().add(DataField.NET_ID);
					} else {
						viewManager.getFilter().getOptions().remove(DataField.NET_ID);
					}
					viewManager.sort();
				} else if (source == classYearItem) {
					if (classYearItem.isSelected()) {
						viewManager.getFilter().getOptions().add(DataField.CLASS_YEAR);
					} else {
						viewManager.getFilter().getOptions().remove(DataField.CLASS_YEAR);
					}
					viewManager.sort();
				} else if (source == instrumentItem) {
					if (instrumentItem.isSelected()) {
						viewManager.getFilter().getOptions().add(DataField.INSTRUMENT);
					} else {
						viewManager.getFilter().getOptions().remove(DataField.INSTRUMENT);
					}
					viewManager.sort();
				}
			}
		};
		firstNameItem.addItemListener(itemListener);
		lastNameItem.addItemListener(itemListener);
		nickNameItem.addItemListener(itemListener);
		netIDItem.addItemListener(itemListener);
		classYearItem.addItemListener(itemListener);
		instrumentItem.addItemListener(itemListener);
	}
	
	private void a4Layouts() {
		setLayout(new GridBagLayout());
		
		optionsMenu.add(firstNameItem);
		optionsMenu.add(lastNameItem);
		optionsMenu.add(nickNameItem);
		optionsMenu.add(netIDItem);
		optionsMenu.add(classYearItem);
		optionsMenu.add(instrumentItem);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 1; c.weighty = 1;
		
		c.gridx = 0; c.gridy = 0;
		add(textField, c);
		c.weightx = 0; c.weighty = 1;
		c.gridx = 1; c.gridy = 0;
		add(optionsButton, c);
	}
	
	private void a5Initialize() {
		textField.putClientProperty("tip","Search for a member");
		
		firstNameItem.setSelected(true);
		lastNameItem.setSelected(true);
		nickNameItem.setSelected(true);
		netIDItem.setSelected(true);
		classYearItem.setSelected(true);
		instrumentItem.setSelected(true);
		
		setOptionsVisible(!Tools.getBoolean("Simple Search Fields", false));
	}
	
	private void applyFilter(String value) {
		viewManager.getFilter().setComboFitler(value);
		viewManager.sort();
		if (additionTable != null) {
			if (additionTable.getTable().getRowCount() > 0) {
				additionTable.getTable().setRowSelectionInterval(0, 0);
			} else {
				additionTable.getTable().clearSelection();
			}
		}
	}
	
	public boolean isCoupledWithAdditionTable() {
		return additionTable != null;
	}
	
	public JTextField getTextField() {
		return textField;
	}
	
	public void setOptionsVisible(boolean value) {
		optionsButton.setVisible(value);
		firstNameItem.setSelected(true);
		lastNameItem.setSelected(true);
		nickNameItem.setSelected(true);
		netIDItem.setSelected(value);
		classYearItem.setSelected(value);
		instrumentItem.setSelected(value);
	}
}