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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.yatta.softwarevendor.demo.client.ui.BrowserWrapperInput;

public class WelcomeHandler extends AbstractHandler {

  private static final String BROWSER_TITLE = "Introduction to in-app purchases";
  private static final String EDITOR_ID = "de.yatta.softwarevendor.demo.editors.browserWrapper";

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    new Job("Show welcome page") {
      @Override
      protected IStatus run(IProgressMonitor monitor) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
          try {
            URL base = FileLocator.toFileURL(getClass().getResource("/welcome"));
            URL url = new URL(base, "index.html");
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            page.openEditor(new BrowserWrapperInput(BROWSER_TITLE, url), EDITOR_ID);
          } catch (PartInitException | IOException e) {
            throw new RuntimeException(e);
          }
        });
        return Status.OK_STATUS;
      }
    }.schedule();
    return null;
  }

}
