package de.yatta.softwarevendor.demo.client.handlers;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IStartup;
import org.osgi.service.prefs.Preferences;

import de.yatta.softwarevendor.demo.VendorDemoPlugin;
import de.yatta.softwarevendor.demo.client.ui.SoftwareVendorPreferencePage;

public class WelcomePageStartup implements IStartup {

  @Override
  public void earlyStartup() {
    final Preferences preferences = InstanceScope.INSTANCE.getNode(VendorDemoPlugin.PLUGIN_ID);
    if (!preferences.getBoolean(SoftwareVendorPreferencePage.DO_NOT_SHOW_WELCOME_PAGE, false)) {
      WelcomeHandler.showWelcomePage();
    }
  }

}
