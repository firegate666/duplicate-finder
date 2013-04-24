/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.behnke.dupchecker.renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Marco Behnke
 */
public class SeparatorListCellRenderer implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (((String)value).equalsIgnoreCase("\t")) {
			return new JLabel("TAB", JLabel.CENTER);
		}

		return new JLabel((String)value, JLabel.CENTER);
	}
}
