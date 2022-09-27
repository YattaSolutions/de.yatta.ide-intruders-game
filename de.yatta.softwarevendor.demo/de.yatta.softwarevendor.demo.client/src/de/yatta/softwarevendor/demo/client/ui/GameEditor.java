package de.yatta.softwarevendor.demo.client.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;

public class GameEditor extends BrowserWrapper {

  public static final String EDITOR_ID = "de.yatta.softwarevendor.demo.editors.gameEditor";

  @Override
  public void createPartControl(Composite parent) {
    initBrowser(parent);

    IEditorInput editorInput = getEditorInput();
    if (editorInput instanceof GameEditorInput) {
      GameEditorInput input = (GameEditorInput) editorInput;
      getBrowser().setUrl(input.getGame().getUrl());
      setPartName(input.getName());
    }
  }

  @Override
  public void setFocus() {
    if (getBrowser() != null) {
      getBrowser().setFocus();
    }
  }

}
