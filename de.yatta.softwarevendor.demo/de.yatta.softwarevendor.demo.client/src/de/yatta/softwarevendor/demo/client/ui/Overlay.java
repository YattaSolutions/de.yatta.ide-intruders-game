package de.yatta.softwarevendor.demo.client.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

public class Overlay {

  private boolean visible;
  private Composite contentArea;
  private Shell overlay;
  private DisposeListener disposeListener;
  private PaintListener paintListener;
  private ControlListener controlListener;
  private Label headerLabel;
  private Label descriptionLabel;
  private Composite buttons;
  private Composite parent;
  private List<Composite> parents;

  public Overlay(Composite parent, IWorkbenchPage page, IEditorPart editor) {
    this.parent = parent;
    overlay = new Shell(parent.getShell(), SWT.NO_TRIM);
    overlay.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    overlay.setAlpha(500);
    overlay.setLayout(new GridLayout());

    contentArea = new Composite(overlay, SWT.NONE);
    contentArea.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
    contentArea.setLayout(new GridLayout());
    contentArea.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));

    headerLabel = new Label(contentArea, SWT.WRAP);
    FontDescriptor descriptor = FontDescriptor.createFrom(headerLabel.getFont()).setStyle(SWT.BOLD);
    headerLabel.setFont(descriptor.createFont(headerLabel.getDisplay()));
    headerLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_TRANSPARENT));
    headerLabel.setVisible(false);

    descriptionLabel = new Label(contentArea, SWT.WRAP);
    descriptionLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_TRANSPARENT));
    descriptionLabel.setVisible(false);

    buttons = new Composite(contentArea, SWT.NONE);
    buttons.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_TRANSPARENT));
    buttons.setLayout(new FillLayout());

    parents = new ArrayList<>();
    Composite c = parent;
    while (c != null) {
      parents.add(c);
      c = c.getParent();
    }

    disposeListener = evt -> hideOverlay();
    paintListener = evt -> updateOverlay();
    controlListener = new ControlListener() {
      @Override
      public void controlResized(ControlEvent e) {
        updateOverlay();
      }

      @Override
      public void controlMoved(ControlEvent e) {
        updateOverlay();
      }
    };

    overlay.open();
    overlay.setVisible(false);
  }

  public void showOverlay() {
    if (visible) {
      return;
    }

    updateOverlay();

    overlay.setVisible(true);
    overlay.setFocus();

    parent.addDisposeListener(disposeListener);
    parents.forEach(p -> {
      p.addControlListener(controlListener);
      p.addPaintListener(paintListener);
    });

    visible = true;
  }

  public void hideOverlay() {
    if (!visible) {
      return;
    }

    if (!parent.isDisposed()) {
      parent.removeDisposeListener(disposeListener);
    }

    parents.stream()
        .filter(p -> !p.isDisposed())
        .forEach(p -> {
          p.removeControlListener(controlListener);
          p.removePaintListener(paintListener);
        });

    if (!overlay.isDisposed()) {
      overlay.setVisible(false);
    }

    visible = false;
  }

  private void updateOverlay() {
    if (!parent.isVisible()) {
      overlay.setBounds(0, 0, 0, 0);
      return;
    }
    Point point = parent.toDisplay(0, 0);
    Rectangle bounds = parent.getBounds();
    overlay.setBounds(point.x, point.y, bounds.width, bounds.height);
  }

  public void setFocus() {
    contentArea.setFocus();
  }

  public void setHeaderText(String text) {
    headerLabel.setText(text);
    headerLabel.setVisible(text != null);
    headerLabel.requestLayout();
  }

  public void setDescriptionText(String text) {
    descriptionLabel.setText(text);
    descriptionLabel.setVisible(text != null);
    descriptionLabel.requestLayout();
  }

  public Button addButton(String text, Consumer<SelectionEvent> consumer) {
    Button button = new Button(buttons, SWT.NONE);
    button.setText(text);
    button.addSelectionListener(SelectionListener.widgetSelectedAdapter(consumer));
    button.requestLayout();
    return button;
  }

  public boolean isVisible() {
    return visible;
  }
}
