package pepband3.gui.component.preview;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import pepband3.gui.*;
import pepband3.gui.extra.*;

public abstract class BandPreviewTable extends PreviewTable {
	
	protected static final String[] NAME_TYPES = {"Name (F L)","Name (L, F)","Full Name","Last Name", "First Name", "Nickname"};
	protected static final String[] OUTPUT_TYPES = {"Points","Roster","Event"};
	protected static final String[] SORT_MODES = {"Last Name","First Name","Instrument","Class","Net ID"};
	
	protected DefaultComboBoxModel nameTypeModel;
	protected JComboBox nameTypeBox;
	protected DefaultComboBoxModel outputTypeModel;
	protected JComboBox outputTypeBox;
	protected DefaultComboBoxModel sortModeModel;
	protected JComboBox sortModeBox;
	protected SpinnerNumberModel columnCountModel;
	protected JSpinner columnCountSpinner;
	protected JSpinner.NumberEditor columnCountEditor;
	
	public BandPreviewTable() {
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
		nameTypeModel = new DefaultComboBoxModel(NAME_TYPES);
		outputTypeModel = new DefaultComboBoxModel(OUTPUT_TYPES);
		sortModeModel = new DefaultComboBoxModel(SORT_MODES);
		columnCountModel = new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1));
		
		nameTypeBox = new JComboBox(nameTypeModel);
		outputTypeBox = new JComboBox(outputTypeModel);
		sortModeBox = new JComboBox(sortModeModel);
		columnCountSpinner = new JSpinner(columnCountModel);
		columnCountEditor = new JSpinner.NumberEditor(columnCountSpinner, "#,####' Columns'");
		columnCountSpinner.setEditor(columnCountEditor);
	}
	
	private void a3Listeners() {
		nameTypeBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() == nameTypeBox && e.getStateChange() == ItemEvent.SELECTED) {
					String selectedItem = (String)nameTypeBox.getSelectedItem();
					if (selectedItem.equals(NAME_TYPES[0])) {
						memberRenderer.setDataField(DataField.NAME_FIRST_LAST);
					} else if (selectedItem.equals(NAME_TYPES[1])) {
						memberRenderer.setDataField(DataField.NAME);
					} else if (selectedItem.equals(NAME_TYPES[2])) {
						memberRenderer.setDataField(DataField.FULL_NAME);
					} else if (selectedItem.equals(NAME_TYPES[3])) {
						memberRenderer.setDataField(DataField.LAST_NAME);
					} else if (selectedItem.equals(NAME_TYPES[4])) {
						memberRenderer.setDataField(DataField.FIRST_NAME);
					} else if (selectedItem.equals(NAME_TYPES[5])) {
						memberRenderer.setDataField(DataField.NICK_NAME);
					} else {
						memberRenderer.setDataField(DataField.NAME);
					}
					memberTable.repaint();
				}
			}
		});
	}
	
	private void a4Layouts() {
		optionsPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1; c.gridheight = 1;
		c.weightx = 0; c.weighty = 0;
		c.insets = new Insets(GUIManager.INS,2 * GUIManager.INS,GUIManager.INS,2 * GUIManager.INS);
		
		c.gridx = 1; c.gridy = 0;
		optionsPanel.add(new JLabel("Output Type:"), c);
		c.gridx = 1; c.gridy = 1;
		optionsPanel.add(outputTypeBox, c);
		c.gridx = 2; c.gridy = 0;
		optionsPanel.add(new JLabel("Name Type:"), c);
		c.gridx = 2; c.gridy = 1;
		optionsPanel.add(nameTypeBox, c);
		c.gridx = 4; c.gridy = 0;
		optionsPanel.add(new JLabel("Sort Mode:"), c);
		c.gridx = 4; c.gridy = 1;
		optionsPanel.add(sortModeBox, c);
		c.gridx = 5; c.gridy = 0;
		optionsPanel.add(new JLabel("Columns in Layout:"), c);
		c.gridx = 5; c.gridy = 1;
		optionsPanel.add(columnCountSpinner, c);
		c.gridheight = 2;
		c.weightx = 1; c.weighty = 0;
		c.gridx = 6; c.gridy = 0;
		optionsPanel.add(Box.createHorizontalGlue(), c);
	}
	
	private void a5Initialize() {
		nameTypeBox.setToolTipText("Select which name to display for members");
		columnCountSpinner.setToolTipText("The number of columns determines how to layout the members in the preview");
		sortModeBox.setToolTipText("Select which sort method to use");
		outputTypeBox.setToolTipText("Select which output to preview");
	}
}