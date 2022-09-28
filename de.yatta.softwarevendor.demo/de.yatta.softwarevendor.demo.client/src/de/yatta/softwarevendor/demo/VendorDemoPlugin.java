package de.yatta.softwarevendor.demo;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class VendorDemoPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "de.yatta.softwarevendor.demo.client";

  private static VendorDemoPlugin instance;

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    instance = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    instance = null;
    super.stop(context);
  }

  public static VendorDemoPlugin getDefault() {
    return instance;
  }
}
