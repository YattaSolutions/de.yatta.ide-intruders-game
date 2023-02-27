package de.yatta.ideintruders.client.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.yatta.ideintruders.client.ui.BrowserWrapper;
import de.yatta.ideintruders.client.ui.BrowserWrapperInput;
import de.yatta.ideintruders.client.ui.WelcomeEditor;

public class WelcomeHandler extends AbstractHandler
{

   private static final String BROWSER_TITLE = "Checkout Demo";

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException
   {
      showWelcomePageWithThemeListener();
      return null;
   }

   public static void showWelcomePage()
   {
      Job job = new Job("Show welcome page") {
         @Override
         protected IStatus run(IProgressMonitor monitor)
         {
            PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
               try
               {
                  String theme = ThemeHandler.isDarkMode() ? "-dark" : "";
                  String url = BrowserWrapper.buildFileUrlForResource("welcome/index" + theme + ".html");

                  IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                  IWorkbenchPage page = window.getActivePage();

                  BrowserWrapperInput input = new BrowserWrapperInput(BROWSER_TITLE, url,
                        "/welcome/img/yatta_16x16.png");
                  IEditorPart editor = page.findEditor(input);
                  if (editor != null)
                  {
                     page.closeEditor(editor, false);
                  }
                  page.openEditor(input, WelcomeEditor.EDITOR_ID);
               }
               catch (PartInitException e)
               {
                  throw new RuntimeException(e);
               }
            });
            return Status.OK_STATUS;
         }

      };
      job.schedule();

   }

   public static void showWelcomePageWithThemeListener()
   {
      PlatformUI.getWorkbench().getThemeManager().addPropertyChangeListener(new IPropertyChangeListener() {
         @Override
         public void propertyChange(PropertyChangeEvent event)
         {
            // if (IThemeManager.CHANGE_CURRENT_THEME.equals(event.getProperty()))
            if ("org.eclipse.ui.workbench.DARK_BACKGROUND".equals(event.getProperty()))
            {
               showWelcomePage();
            }
         }
      });
      showWelcomePage();
   }

}
