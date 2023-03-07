package de.yatta.ideintruders.client.handlers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.Preferences;

import de.yatta.ideintruders.VendorDemoPlugin;
import de.yatta.ideintruders.client.ui.SoftwareVendorPreferencePage;

public class WelcomePageStartup implements IStartup
{

   @Override
   public void earlyStartup()
   {
      final Preferences preferences = InstanceScope.INSTANCE.getNode(VendorDemoPlugin.PLUGIN_ID);
      if (!preferences.getBoolean(SoftwareVendorPreferencePage.DO_NOT_SHOW_WELCOME_PAGE, false))
      {
         WelcomeHandler.showWelcomePageWithThemeListener();

         Job job = new Job("Resize window") {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
               PlatformUI.getWorkbench().getDisplay().asyncExec(() -> {
                  IWorkbench workbench = PlatformUI.getWorkbench();
                  Shell shell = workbench.getWorkbenchWindows()[0].getShell();
                  Point size = shell.getSize();
                  if (size.x < 1300)
                  {
                     size.x = 1300;
                  }
                  if (size.y < 900)
                  {
                     size.y = 900;
                  }
                  shell.setSize(size);
                  shell.requestLayout();
               });
               return Status.OK_STATUS;
            }

         };
         job.schedule();
      }

   }
}
