package de.yatta.ideintruders.client.ui.licenses;

import de.yatta.ideintruders.VendorDemoPlugin;
import de.yatta.platform.marketplace.licensing.client.ui.preferences.LicenseDetailsPage;

public class SubscriptionLicenseDetailsPage extends LicenseDetailsPage {

	@Override
	protected String getSolutionId() {
        return VendorDemoPlugin.SOLUTION_ID;
	}

}