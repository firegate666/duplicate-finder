/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.behnke.dupchecker.listener;

import biz.behnke.dupchecker.DupChecker;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Marco Behnke
 */
public class WindowClosingAdapter extends WindowAdapter {

	protected DupChecker parent;

	public WindowClosingAdapter(DupChecker parent) {
		this.parent = parent;

		parent.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent, "Möchten Sie das Programm wirklich beenden?", "Programm beenden?", JOptionPane.YES_NO_OPTION)) {
			parent.setVisible(false);

			parent.updateProperty("filelist_in_1", parent.getFilelistIn1());
			parent.updateProperty("filelist_in_1_selected", String.valueOf(parent.getFilelistIn1Selected()));

			parent.updateProperty("filelist_in_2", parent.getFilelistIn2());
			parent.updateProperty("filelist_in_2_selected", String.valueOf(parent.getFilelistIn2Selected()));

			parent.updateProperty("filelist_out", parent.getFilelistOut());
			parent.updateProperty("filelist_out_selected", String.valueOf(parent.getFilelistOutSelected()));

			System.gc();
			System.exit(0);
		}
	}
}
