/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biz.behnke.dupchecker.listener;

import biz.behnke.dupchecker.DupChecker;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

/**
 *
 * @author Marco Behnke
 */
public class WindowPropertyChangeListener implements HierarchyBoundsListener, WindowStateListener {

	protected DupChecker parent;

	public WindowPropertyChangeListener(DupChecker parent) {
		this.parent = parent;
	}

	@Override
	public void ancestorMoved(HierarchyEvent e) {
		parent.updateProperty("window.left", String.valueOf(e.getChanged().getX()));
		parent.updateProperty("window.top", String.valueOf(e.getChanged().getY()));
	}

	@Override
	public void ancestorResized(HierarchyEvent e) {
		parent.updateProperty("window.width", String.valueOf(e.getChanged().getWidth()));
		parent.updateProperty("window.height", String.valueOf(e.getChanged().getHeight()));
	}

	@Override
	public void windowStateChanged(WindowEvent e) {
		parent.updateProperty("window.state", String.valueOf(e.getNewState()));
	}
}
