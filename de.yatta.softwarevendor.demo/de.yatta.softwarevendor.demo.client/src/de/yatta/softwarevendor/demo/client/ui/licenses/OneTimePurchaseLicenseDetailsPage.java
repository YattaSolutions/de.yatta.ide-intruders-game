package de.yatta.softwarevendor.demo.client.ui.licenses;

import de.yatta.platform.marketplace.licensing.client.ui.preferences.LicenseDetailsPage;
import de.yatta.softwarevendor.demo.VendorDemoPlugin;

public class OneTimePurchaseLicenseDetailsPage extends LicenseDetailsPage {

	@Override
	protected String getSolutionId() {
		return VendorDemoPlugin.SOLUTION_ID_ONETIMEPURCHASE;
	}

}
