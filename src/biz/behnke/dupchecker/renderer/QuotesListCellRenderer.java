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
public class QuotesListCellRenderer implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (((String)value).equalsIgnoreCase("\0")) {
			return new JLabel("-leer-", JLabel.CENTER);
		}

		return new JLabel((String)value, JLabel.CENTER);
	}
}
