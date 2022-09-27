package de.yatta.softwarevendor.demo.client.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class BrowserWrapper extends EditorPart {

  public static final String EDITOR_ID = "de.yatta.softwarevendor.demo.editors.browserWrapper";

  private Browser browser;

  @Override
  public void doSave(IProgressMonitor monitor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void doSaveAs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    setSite(site);
    setInput(input);
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void createPartControl(Composite parent) {
    initBrowser(parent);

    IEditorInput editorInput = getEditorInput();
    if (editorInput instanceof BrowserWrapperInput) {
      BrowserWrapperInput input = (BrowserWrapperInput) editorInput;
      browser.setUrl(input.getUrl().toExternalForm());
      setPartName(input.getName());
    }
  }

  protected void initBrowser(Composite parent) {
    browser = new Browser(parent, SWT.NONE);
  }

  @Override
  public void setFocus() {
    browser.setFocus();
  }

  public Browser getBrowser() {
    return browser;
  }

}
