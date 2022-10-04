package de.yatta.softwarevendor.demo.client.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;

import com.yattasolutions.platform.marketplace.client.MarketplaceClient;
import com.yattasolutions.platform.marketplace.client.MarketplaceClientPlugin;

import de.yatta.platform.marketplace.licensing.client.LicenseRequest;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse.Validity;
import de.yatta.platform.marketplace.licensing.client.LicensingClient;

public class GameEditor extends BrowserWrapper {

  public static final String EDITOR_ID = "de.yatta.softwarevendor.demo.editors.gameEditor";

  private static final String SOLUTION_ID = "de.softwarevendor.product";
  private static final String SOLUTION_ID_ONETIMEPURCHASE = "de.softwarevendor.product.onetimepurchase";
  private static final String VENDOR_KEY = "g5JE78Z0UIiQrHCAMjTR";

  private Composite parent;
  private Overlay overlay;
  private Link signInLink;

  private ServiceRegistration<?> serviceRegistration;

  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
    initBrowser(parent);

    IEditorInput editorInput = getEditorInput();
    if (editorInput instanceof GameEditorInput) {
      GameEditorInput input = (GameEditorInput) editorInput;

      String gameUrl = input.getGame().getUrl();
      if (!gameUrl.contains(":")) {
        // no protocol specified -> bundled resource
        gameUrl = buildFileUrlForResource(gameUrl);
      }

      getBrowser().setUrl(gameUrl);
      setPartName(input.getName());
    }

    checkLicense();
    String[] topics = { MarketplaceClient.ACCOUNT_LOGGED_IN_EVENT, MarketplaceClient.ACCOUNT_LOGGED_OUT_EVENT };
    serviceRegistration = MarketplaceClientPlugin.getDefault().registerEventHandler(this::afterLoginOrLogout, topics);
  }

  @Override
  public void setFocus() {
    if (overlay != null && overlay.isVisible()) {
      overlay.setFocus();
    } else if (getBrowser() != null) {
      getBrowser().setFocus();
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    serviceRegistration.unregister();
  }

  private void checkLicense() {
    if (!MarketplaceClient.get().isAccountLoggedIn()) {
      showOverlay(true,
          "We couldn't detect a valid license",
          "To play the game, either sign in with an account with a valid license"
              + " or purchase or subscribe for a license.");
      return;
    }

    final LicenseResponse licenseResponse = fetchLicenseStatus(5000);

    if (licenseResponse.getValidity() == Validity.UNLICENSED) {
      showOverlay(false,
          "We couldn't detect a valid license",
          "To play the game, please purchase or subscribe for a license.");
    } else if (licenseResponse.getValidity() == Validity.WAIT) {
      MessageDialog.openInformation(getEditorSite().getShell(), getPartName(),
          "There was an error communicating with the licensing server.");
    } else if (licenseResponse.getValidity() == Validity.LICENSED) {
      hideOverlay();

      parent.getDisplay().asyncExec(() -> {
        IWorkbenchPage page = getEditorSite().getPage();
        IEditorPart editor = page.getActiveEditor();
        if ("Commercial Checkout".equals(editor.getTitle())) {
          page.closeEditor(editor, false);
          // We are using the same info message here that is shown by the Marketplace when
          // requesting the checkout page for a solution id that the user is already
          // subscribed to.
          MessageDialog.openInformation(getEditorSite().getShell(), getPartName(),
              "Good news: You're already subscribed.");
        }
      });
    }
  }

  private void showOverlay(boolean showSignInLink, String headerText, String descriptionText) {
    if (overlay == null) {
      // create overlay with buttons
      overlay = new Overlay(parent, getEditorSite().getPage(), this);
      overlay.addButton("Purchase",
          e -> MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID_ONETIMEPURCHASE));
      overlay.addButton("Subscribe",
          e -> MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID));
      signInLink = overlay.addLink("<a>Sign in</a>",
          e -> MarketplaceClient.get().showSignInPage(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID));
    }

    // update overlay with specified texts and show/hide the sign-in link
    overlay.setHeaderText(headerText);
    overlay.setDescriptionText(descriptionText);
    signInLink.setVisible(showSignInLink);
    overlay.showOverlay();
  }

  private void hideOverlay() {
    if (overlay != null) {
      overlay.hideOverlay();
    }
  }

  private void afterLoginOrLogout(Event event) {
    // after login or logout, check the license and hide/display the license overlay
    parent.getDisplay().syncExec(() -> checkLicense());
  }

  private LicenseResponse fetchLicenseStatus(final long timeoutInMillis) {
    // check if the license is valid
    LicenseResponse licenseResponse = fetchLicense(SOLUTION_ID);
    final long tryUntil = System.currentTimeMillis() + timeoutInMillis;
    // repeat the call for the specified interval while the validity is "WAIT"
    while (licenseResponse.getValidity() == Validity.WAIT && System.currentTimeMillis() <= tryUntil) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      licenseResponse = fetchLicense(SOLUTION_ID);
    }
    return licenseResponse;
  }

  private LicenseResponse fetchLicense(String solutionId) {
    return LicensingClient.get().queryLicense(new LicenseRequest(solutionId, null, 1, VENDOR_KEY));
  }
}
