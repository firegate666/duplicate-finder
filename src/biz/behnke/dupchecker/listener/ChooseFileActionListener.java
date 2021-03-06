package biz.behnke.dupchecker.listener;

import biz.behnke.dupchecker.DupChecker;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class ChooseFileActionListener implements ActionListener {

	protected DupChecker parent;
	protected JComboBox model;
	protected Component[] dependingComponents;
	protected boolean saveMode = false;

	public ChooseFileActionListener(DupChecker parent, JComboBox model, boolean saveMode, Component[] dependingComponents) {
		this.parent = parent;
		this.model = model;
		this.saveMode = saveMode;
		this.dependingComponents = dependingComponents;
	}

	private void setDependingComponentsEnabled(boolean newEnabled) {
		for (int i = 0; i < this.dependingComponents.length; i++) {
			this.dependingComponents[i].setEnabled(newEnabled);
		}
	}

	public ChooseFileActionListener(DupChecker parent, JComboBox model, boolean saveMode) {
		this(parent, model, saveMode, new Component[0]);
	}

	public ChooseFileActionListener(DupChecker parent, JComboBox model) {
		this(parent, model, false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser jfc = new JFileChooser();

		if (model.getSelectedIndex() != -1) {
			jfc.setSelectedFile(new File((String)model.getSelectedItem()));
		} else {
			jfc.setCurrentDirectory(new File(parent.getLastdir()));
		}

		jfc.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File f) {
				return f == null
						|| f.isDirectory()
						|| f.toString().endsWith(".csv")
						|| f.toString().endsWith(".TXT");
			}
			@Override
			public String getDescription() {return "*.csv,*.TXT";}
		});
		int result;

		if (saveMode) {
			result = jfc.showSaveDialog(parent);
		}
		else {
			result = jfc.showOpenDialog(parent);
		}

		if (result == JFileChooser.APPROVE_OPTION) {
			parent.setLastdir(jfc.getSelectedFile().getPath());

			if (((DefaultComboBoxModel)model.getModel()).getIndexOf(jfc.getSelectedFile().toString()) == -1) {
				model.addItem(jfc.getSelectedFile().toString());
			}
			model.setSelectedItem(jfc.getSelectedFile().toString());

			this.setDependingComponentsEnabled(true);
		}
	}
}
