package de.yatta.ideintruders.client.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.yatta.ideintruders.client.ui.GameEditor;
import de.yatta.ideintruders.client.ui.GameEditorInput;

public class DemoHandler extends AbstractHandler {

  private Game game = Game.IDE_INTRUDERS;
  private IWorkbenchWindow window = null;

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
    try {
      window.getActivePage().openEditor(new GameEditorInput(game), GameEditor.EDITOR_ID);
    } catch (PartInitException e) {
      MessageDialog.openError(window.getShell(), game.toString(), "Game could not be started. Please try again.");
    }
    return null;
  }
}