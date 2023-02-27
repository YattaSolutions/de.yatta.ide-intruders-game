package de.yatta.ideintruders.client.handlers;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IStartup;
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
      }
   }
}
