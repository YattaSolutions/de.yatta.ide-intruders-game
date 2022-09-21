package de.yatta.softwarevendor.demo.client.handlers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.yattasolutions.platform.marketplace.client.MarketplaceClient;
import com.yattasolutions.platform.marketplace.client.MarketplaceClientPlugin;

import de.yatta.platform.marketplace.licensing.client.LicenseRequest;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse.Validity;
import de.yatta.platform.marketplace.licensing.client.LicensingClient;

public class DemoHandler extends AbstractHandler implements EventHandler {

  private static final String BROWSER_ID = "VENDOR_GAME_BROWSER";
  private static final String SOLUTION_ID = "de.softwarevendor.product";
  private static final String SOLUTION_ID_ONETIMEPURCHASE = "de.softwarevendor.product.onetimepurchase";
  private static final String VENDOR_KEY = "g5JE78Z0UIiQrHCAMjTR";

  private int checkoutDuration = 1; // Time in minutes how long the checkout token is valid
  private Game game = Game.CLUMSY_BIRD;
  private IWorkbenchWindow window = null;
  private boolean startGameAfterLogin;
  private ServiceRegistration<?> serviceRegistration;

  public DemoHandler() {
    String[] topics = { MarketplaceClient.ACCOUNT_LOGGED_IN_EVENT, MarketplaceClient.ACCOUNT_LOGGED_OUT_EVENT };
    serviceRegistration = MarketplaceClientPlugin.getDefault().registerEventHandler(this, topics);
  }

  @Override
  public void dispose() {
    super.dispose();
    serviceRegistration.unregister();
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

    if (MarketplaceClient.get().isAccountLoggedIn()) {
      checkLicenseAndStartGame();
    } else {
      MessageDialog.openInformation(window.getShell(), game.toString(),
          "Please sign in to play " + game.toString() + ".");
      closeCheckoutTab();

      startGameAfterLogin = true;
      MarketplaceClient.get().showSignInPage(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID);
    }
    return null;
  }

  private void checkLicenseAndStartGame() {
    final LicenseResponse licenseResponse = fetchLicenseStatus(5000);

    if (licenseResponse.getValidity() == Validity.UNLICENSED) {
      String[] buttons = { "Purchase", "Subscribe", "Cancel" };
      MessageDialog dialog = new MessageDialog(window.getShell(), game.toString(), null,
          "We couldn't detect a valid license, please go ahead and purchase or subscribe for a license.",
          MessageDialog.INFORMATION, buttons, 2);
      int buttonIndex = dialog.open();

      if (buttonIndex == 0) {
        closeCheckoutTab();
        MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID_ONETIMEPURCHASE);
      } else if (buttonIndex == 1) {
        closeCheckoutTab();
        MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), SOLUTION_ID);
      }
    } else if (licenseResponse.getValidity() == Validity.WAIT) {
      MessageDialog.openInformation(window.getShell(), game.toString(),
          "There was an error communicating with the licensing server.");
    } else if (licenseResponse.getValidity() == Validity.LICENSED) {
      startGame();
    }
  }

  private LicenseResponse fetchLicense(String solutionId) {
    return LicensingClient.get().queryLicense(new LicenseRequest(solutionId, null, checkoutDuration, VENDOR_KEY));
  }

  private void startGame() {
    closeCheckoutTab();
    try {
      // @formatter:off
      IWebBrowser browser = window.getWorkbench().getBrowserSupport().createBrowser(
          IWorkbenchBrowserSupport.AS_EDITOR,
          BROWSER_ID,
          game.toString(),
          game.toString());
      // @formatter:on

      browser.openURL(new URL(game.getUrl()));
    } catch (PartInitException | MalformedURLException e) {
      MessageDialog.openError(window.getShell(), game.toString(), "Game could not be started. Please try again.");
    }
  }

  @Override
  public void handleEvent(Event event) {
    if (MarketplaceClient.ACCOUNT_LOGGED_IN_EVENT.equals(event.getTopic()) && startGameAfterLogin) {
      Display.getDefault().syncExec(() -> checkLicenseAndStartGame());
      startGameAfterLogin = false;
      return;
    }

    if (MarketplaceClient.ACCOUNT_LOGGED_OUT_EVENT.equals(event.getTopic())) {
      closeCheckoutTab();
      return;
    }
  }

  private LicenseResponse fetchLicenseStatus(final long timeoutInMillis) {
    LicenseResponse licenseResponse = fetchLicense(SOLUTION_ID);
    final long tryUntil = System.currentTimeMillis() + timeoutInMillis;
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

  private void closeCheckoutTab() {
    findCheckoutTab().ifPresent(checkoutTab -> {
      Display.getDefault().syncExec(() -> {
        window.getActivePage().closeEditor(checkoutTab.getEditor(false), false);
      });
    });
  }

  private Optional<IEditorReference> findCheckoutTab() {
    return Arrays.stream(window.getActivePage().getEditorReferences())
        .filter(editor -> "Commercial Checkout".equals(editor.getTitle())).findFirst();
  }

}