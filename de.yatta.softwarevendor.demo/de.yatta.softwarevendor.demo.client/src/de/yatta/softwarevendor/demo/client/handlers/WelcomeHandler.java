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
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import de.yatta.softwarevendor.demo.VendorDemoPlugin;
import de.yatta.softwarevendor.demo.client.ui.BrowserWrapper;
import de.yatta.softwarevendor.demo.client.ui.BrowserWrapperInput;
import de.yatta.softwarevendor.demo.client.ui.SoftwareVendorPreferencePage;

public class WelcomeHandler extends AbstractHandler {

  private static final String BROWSER_TITLE = "Checkout Demo";

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
            BrowserWrapper browserWrapper = (BrowserWrapper) page
                .openEditor(new BrowserWrapperInput(BROWSER_TITLE, url), BrowserWrapper.EDITOR_ID);
            Browser browser = browserWrapper.getBrowser();

            // activate checkbox if setting is active
            final Preferences preferences = InstanceScope.INSTANCE.getNode(VendorDemoPlugin.PLUGIN_ID);
            if (preferences.getBoolean(SoftwareVendorPreferencePage.DO_NOT_SHOW_WELCOME_PAGE, false)) {
              String script = "document.getElementById('" + SoftwareVendorPreferencePage.DO_NOT_SHOW_WELCOME_PAGE
                  + "').checked=true";
              browser.addProgressListener(ProgressListener.completedAdapter(evt -> browser.execute(script)));
            }

            // toggle setting when checkbox is checked/unchecked
            new BrowserFunction(browser, "doNotShowWelcomePageChecked") {
              @Override
              public Object function(Object[] arguments) {
                if (arguments != null && arguments.length > 0) {
                  boolean value = Boolean.TRUE.equals(arguments[0]);
                  preferences.putBoolean(SoftwareVendorPreferencePage.DO_NOT_SHOW_WELCOME_PAGE, value);
                  try {
                    preferences.flush();
                  } catch (BackingStoreException e) {
                    throw new RuntimeException(e);
                  }
                }
                return true;
              }
            };
          } catch (PartInitException | IOException e) {
            throw new RuntimeException(e);
          }
        });
        return Status.OK_STATUS;
      }
    }.schedule();
  }

}
