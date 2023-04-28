package de.yatta.ideintruders.client.ui.licenses;

import com.yattasolutions.platform.marketplace.client.CheckoutEnvironment;

import de.yatta.ideintruders.VendorDemoPlugin;
import de.yatta.platform.marketplace.licensing.client.ui.preferences.LicenseDetailsPage;

public class SubscriptionLicenseDetailsPage extends LicenseDetailsPage {

	@Override
	protected String getProductId() {
        return VendorDemoPlugin.PRODUCT_ID;
	}

	@Override
	protected CheckoutEnvironment getEnvironment() {
	   return VendorDemoPlugin.ENVIRONMENT;
	}

}