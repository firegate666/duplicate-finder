package biz.behnke.dupchecker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JComboBox;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class ChooseFileActionListener implements ActionListener {

	protected DupChecker parent;
	protected JComboBox model;
	protected boolean saveMode = false;

	public ChooseFileActionListener(DupChecker parent, JComboBox model, boolean saveMode) {
		this.parent = parent;
		this.model = model;
		this.saveMode = saveMode;
	}

	public ChooseFileActionListener(DupChecker parent, JComboBox model) {
		this(parent, model, false);
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser jfc = new JFileChooser();
		jfc.setSelectedFile(new File(parent.getLastdir()));
		jfc.setFileFilter(new FileFilter(){
			public boolean accept(File f) {return f.isDirectory() || f.toString().endsWith(".csv") || f.toString().endsWith(".TXT");}
			public String getDescription() {return "*.csv,*.TXT";}
		});
		int result;

		if (saveMode)
			result = jfc.showSaveDialog(parent);
		else
			result = jfc.showOpenDialog(parent);

		if (result == JFileChooser.APPROVE_OPTION) {
			parent.setLastdir(jfc.getSelectedFile().getPath());
			//model.setText(jfc.getSelectedFile().toString());

			model.addItem(jfc.getSelectedFile().toString());
		}
	}
}
