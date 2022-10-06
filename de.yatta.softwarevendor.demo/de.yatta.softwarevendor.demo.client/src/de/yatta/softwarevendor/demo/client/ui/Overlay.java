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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
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
  private List<Composite> ancestors;

  public Overlay(Composite parent, IWorkbenchPage page, IEditorPart editor) {
    this.parent = parent;

    // create overlay contents
    overlay = new Shell(parent.getShell(), SWT.NO_TRIM);
    overlay.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_TRANSPARENT));
    overlay.setAlpha(480);
    overlay.setLayout(new GridLayout());

    contentArea = new Composite(overlay, SWT.NONE);
    contentArea.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
    GridLayout contentLayout = new GridLayout();
    contentLayout.marginLeft = 15;
    contentLayout.marginTop = 12;
    contentArea.setLayout(contentLayout);

    headerLabel = new Label(contentArea, SWT.WRAP);
    FontDescriptor descriptor = FontDescriptor.createFrom(headerLabel.getFont()).setStyle(SWT.BOLD);
    headerLabel.setFont(descriptor.createFont(headerLabel.getDisplay()));
    headerLabel.setVisible(false);

    descriptionLabel = new Label(contentArea, SWT.WRAP);
    descriptionLabel.setVisible(false);

    buttons = new Composite(contentArea, SWT.NONE);
    RowLayout buttonsLayout = new RowLayout();
    buttonsLayout.center = true;
    buttonsLayout.spacing = 6;
    buttons.setLayout(buttonsLayout);

    // determine ancestor composites
    ancestors = new ArrayList<>();
    Composite c = parent;
    while (c != null) {
      ancestors.add(c);
      c = c.getParent();
    }

    // create listeners for disposal, paint, resize and move (not yet adding them)
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

  /**
   * Show the overlay.
   */
  public void showOverlay() {
    if (visible) {
      return;
    }

    // make overlay visible and re-calculate its size
    updateOverlay();

    overlay.setVisible(true);
    overlay.setFocus();

    // add required listeners to parent and ancestors
    parent.addDisposeListener(disposeListener);
    ancestors.forEach(p -> {
      p.addControlListener(controlListener);
      p.addPaintListener(paintListener);
    });

    visible = true;
  }

  /**
   * Hide the overlay.
   */
  public void hideOverlay() {
    if (!visible) {
      return;
    }

    // remove listeners
    if (!parent.isDisposed()) {
      parent.removeDisposeListener(disposeListener);
    }

    ancestors.stream()
        .filter(p -> !p.isDisposed())
        .forEach(p -> {
          p.removeControlListener(controlListener);
          p.removePaintListener(paintListener);
        });

    // hide the overlay
    if (!overlay.isDisposed()) {
      overlay.setVisible(false);
    }

    visible = false;
  }

  /**
   * Resize the overlay to match the parent composite.
   */
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

  /**
   * Set the header text. Specifying a non-null value makes the header label
   * visible, providing {@code null} on the other hand hides the label.
   * 
   * @param text the text to be displayed inside the header, can be null
   */
  public void setHeaderText(String text) {
    headerLabel.setText(text);
    headerLabel.setVisible(text != null);
    headerLabel.requestLayout();
  }

  /**
   * Set the description text. Specifying a non-null value makes the description
   * label visible, providing {@code null} on the other hand hides the label.
   * 
   * @param text the text to be displayed inside the description, can be null
   */
  public void setDescriptionText(String text) {
    descriptionLabel.setText(text);
    descriptionLabel.setVisible(text != null);
    descriptionLabel.requestLayout();
  }

  /**
   * Add a button with the specified text and action to the <i>buttons</i> section
   * of the overlay.
   * 
   * @param text     the text to be displayed on the button
   * @param consumer the action to perform on selection
   * @return the button that was created
   */
  public Button addButton(String text, Consumer<SelectionEvent> consumer) {
    Button button = new Button(buttons, SWT.NONE);
    button.setText(text);
    button.addSelectionListener(SelectionListener.widgetSelectedAdapter(consumer));
    button.requestLayout();
    return button;
  }

  /**
   * Add a link with the specified text and action to the <i>buttons</i> section
   * of the overlay.
   * 
   * @param text     the text to be displayed on the link
   * @param consumer the action to perform on selection
   * @return the link that was created
   */
  public Link addLink(String text, Consumer<SelectionEvent> consumer) {
    Link link = new Link(buttons, SWT.NONE);
    link.setText(text);
    link.addSelectionListener(SelectionListener.widgetSelectedAdapter(consumer));
    link.requestLayout();
    return link;
  }

  public boolean isVisible() {
    return visible;
  }

  public int getPanelHeight() {
    return contentArea.getBounds().height;
  }
}
