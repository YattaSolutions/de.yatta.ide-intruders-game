package de.yatta.softwarevendor.demo.client.handlers;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.yatta.softwarevendor.demo.client.ui.BrowserWrapper;
import de.yatta.softwarevendor.demo.client.ui.BrowserWrapperInput;

public class WelcomeHandler extends AbstractHandler {

  private static final String BROWSER_TITLE = "Introduction to in-app purchases";

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    showWelcomePage();
    return null;
  }

  public static void showWelcomePage() {
    new Job("Show welcome page") {
      @Override
      protected IStatus run(IProgressMonitor monitor) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
          try {
            URL base = FileLocator.toFileURL(getClass().getResource("/welcome"));
            URL url = new URL(base, "index.html");
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            IWorkbenchPage page = window.getActivePage();
            page.openEditor(new BrowserWrapperInput(BROWSER_TITLE, url), BrowserWrapper.EDITOR_ID);
          } catch (PartInitException | IOException e) {
            throw new RuntimeException(e);
          }
        });
        return Status.OK_STATUS;
      }
    }.schedule();
  }

}
