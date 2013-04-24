/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.behnke.dupchecker.renderer;

import java.awt.Component;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Marco Behnke
 */
public class FileNameListCellRenderer implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value == null) {
			return new JLabel("-");
		}
		File f = new File((String)value);
		JLabel item =  new JLabel(f.getName());
		item.setToolTipText(f.getPath());
		return item;
	}
}
