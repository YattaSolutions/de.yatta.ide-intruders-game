package de.yatta.softwarevendor.demo.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

public class Overlay {

  private Composite contentArea;
  private Shell overlay;
  private DisposeListener disposeListener;
  private PaintListener paintListener;
  private ControlListener controlListener;

  public Overlay(Composite parent, IWorkbenchPage page, IEditorPart editor) {
    overlay = new Shell(parent.getShell(), SWT.NO_TRIM);
    overlay.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    overlay.setAlpha(500);
    overlay.setLayout(new GridLayout());

    contentArea = new Composite(overlay, SWT.NONE);
    contentArea.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
    contentArea.setLayout(new GridLayout());
    contentArea.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));

    List<Composite> parents = new ArrayList<>();
    Composite c = parent;
    while (c != null) {
      parents.add(c);
      c = c.getParent();
    }

    disposeListener = evt -> removeOverlay(parent, page, parents);
    paintListener = evt -> updateOverlay(parent);
    controlListener = new ControlListener() {
      @Override
      public void controlResized(ControlEvent e) {
        updateOverlay(parent);
      }

      @Override
      public void controlMoved(ControlEvent e) {
        updateOverlay(parent);
      }
    };

    parent.addDisposeListener(disposeListener);
    parents.forEach(p -> p.addControlListener(controlListener));
    parents.forEach(p -> p.addPaintListener(paintListener));

    overlay.open();
    overlay.setFocus();
  }

  private void removeOverlay(Composite parent, IWorkbenchPage page, List<Composite> parents) {
    if (!parent.isDisposed()) {
      parent.removeDisposeListener(disposeListener);
      parents.forEach(p -> p.removeControlListener(controlListener));
      parents.forEach(p -> p.removePaintListener(paintListener));
    }
    overlay.dispose();
  }

  private void updateOverlay(Composite parent) {
    if (!parent.isVisible()) {
      overlay.setBounds(0, 0, 0, 0);
      return;
    }
    Point point = parent.toDisplay(0, 0);
    Rectangle bounds = parent.getBounds();
    overlay.setBounds(point.x, point.y, bounds.width, bounds.height);
  }

  public Composite getContentArea() {
    return contentArea;
  }

  public void setFocus() {
    contentArea.setFocus();
  }
}
