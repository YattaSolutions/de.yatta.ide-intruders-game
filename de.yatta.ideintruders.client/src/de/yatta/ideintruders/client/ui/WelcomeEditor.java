package de.yatta.ideintruders.client.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.yatta.ideintruders.client.handlers.Game;

public class WelcomeEditor extends BrowserWrapper
{

   public static final String EDITOR_ID = "de.yatta.ideintruders.editors.welcomeEditor";

   @Override
   public void createPartControl(Composite parent)
   {
      super.createPartControl(parent);

      new BrowserFunction(getBrowser(), "startGame") {
         @Override
         public Object function(Object[] arguments)
         {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            try
            {
               page.openEditor(new GameEditorInput(Game.IDE_INTRUDERS), GameEditor.EDITOR_ID);
            }
            catch (PartInitException e)
            {
               MessageDialog.openError(window.getShell(), Game.IDE_INTRUDERS.toString(), "Game could not be started. Please try again.");
            }
            return null;
         }
      };
   }

}
