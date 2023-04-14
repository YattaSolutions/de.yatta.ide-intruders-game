package de.yatta.ideintruders;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.yattasolutions.platform.marketplace.client.CheckoutEnvironment;

public class VendorDemoPlugin extends AbstractUIPlugin {

  public static final CheckoutEnvironment ENVIRONMENT = CheckoutEnvironment.LIVE;
  public static final String PRODUCT_ID = "de.softwarevendor.product";
  public static final String PRODUCT_ID_ONETIMEPURCHASE = "de.softwarevendor.product.onetimepurchase";
  public static final String VENDOR_KEY = "DEMO";

  public static final String PLUGIN_ID = "de.yatta.ideintruders.client";

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
