package de.yatta.softwarevendor.demo.client.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;

import com.yattasolutions.platform.marketplace.client.MarketplaceClient;
import com.yattasolutions.platform.marketplace.client.MarketplaceClientPlugin;
import com.yattasolutions.platform.marketplace.client.account.AccountManager;

import de.yatta.platform.marketplace.licensing.client.LicenseRequest;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse.Validity;
import de.yatta.platform.marketplace.licensing.client.LicensingClient;
import de.yatta.softwarevendor.demo.VendorDemoPlugin;

public class GameEditor extends BrowserWrapper
{

   public static final String EDITOR_ID = "de.yatta.softwarevendor.demo.editors.gameEditor";

   private Composite parent;
   private boolean isLicensed = false;
   private boolean isNotLoggedIn = true;
   private ServiceRegistration<?> serviceRegistration;

   @Override
   public void createPartControl(Composite parent)
   {
      this.parent = parent;
      initBrowser(parent);

      IEditorInput editorInput = getEditorInput();
      if (editorInput instanceof GameEditorInput)
      {
         GameEditorInput input = (GameEditorInput)editorInput;

         String gameUrl = input.getGame().getUrl();
         if (!gameUrl.contains(":"))
         {
            // no protocol specified -> bundled resource
            gameUrl = buildFileUrlForResource(gameUrl);
         }
         getBrowser().setUrl(gameUrl);
         setPartName(input.getName());
         String titleImage = input.getGame().getTitleImage();
         if (titleImage != null)
         {
            setTitleImage(ImageDescriptor.createFromFile(getClass(), titleImage).createImage());
         }
      }

      checkLicense();
      String[] topics = { MarketplaceClient.ACCOUNT_LOGGED_IN_EVENT, MarketplaceClient.ACCOUNT_LOGGED_OUT_EVENT };
      serviceRegistration = MarketplaceClientPlugin.getDefault().registerEventHandler(this::afterLoginOrLogout, topics);

      getBrowser().addProgressListener(ProgressListener.completedAdapter(e -> {
         openExternalSitesInExternalBrowser(true);
         checkLicense();
      }));

      new BrowserFunction(getBrowser(), "resetDemo") {
         @Override
         public Object function(Object[] arguments)
         {
            LicenseResponse licenseResponse = fetchLicenseStatus(5000);
            if (VendorDemoPlugin.SOLUTION_ID_ONETIMEPURCHASE.equals(licenseResponse.getLicenseTypeId()))
            {
               MarketplaceClient.get().showDeleteDemoOrderDialog(parent.getDisplay(), VendorDemoPlugin.SOLUTION_ID_ONETIMEPURCHASE, true);
            }
            else
            {
               MarketplaceClient.get().showCancelDialog(parent.getDisplay(), VendorDemoPlugin.SOLUTION_ID, true);
            }
            AccountManager.get().signOut();
            return null;
         }
      };
      new BrowserFunction(getBrowser(), "subscribeGame") {
         @Override
         public Object function(Object[] arguments)
         {
            MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), VendorDemoPlugin.SOLUTION_ID);
            return null;
         }
      };

      new BrowserFunction(getBrowser(), "purchaseGame") {
         @Override
         public Object function(Object[] arguments)
         {
            MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), VendorDemoPlugin.SOLUTION_ID_ONETIMEPURCHASE);
            return null;
         }
      };

      new BrowserFunction(getBrowser(), "signIn") {
         @Override
         public Object function(Object[] arguments)
         {
            MarketplaceClient.get().showSignInPage(MarketplaceClientPlugin.getDisplay(), VendorDemoPlugin.SOLUTION_ID);
            return null;
         }
      };

   }

   @Override
   public void setFocus()
   {
      getBrowser().setFocus();
   }

   @Override
   public void dispose()
   {
      super.dispose();
      if (serviceRegistration != null)
      {
         serviceRegistration.unregister();
      }
   }

   private void checkLicense()
   {
      isNotLoggedIn = !MarketplaceClient.get().isAccountLoggedIn();
      if (isNotLoggedIn)
      {
         isLicensed = false;
      }
      else
      {

         final LicenseResponse licenseResponse = fetchLicenseStatus(5000);

         if (licenseResponse.getValidity() == Validity.LICENSED)
         {
            isLicensed = true;
         }
         else
         {
            isLicensed = false;
         }
      }

      if (isLicensed)
      {
         hideOverlay();
      }
      else
      {
         showOverlay(isNotLoggedIn);
      }
   }

   private void showOverlay(boolean showSignInLink)
   {
      getBrowser().execute("showOverlay(" + (showSignInLink ? "true" : "false") + ")");
   }

   private void hideOverlay()
   {

      getBrowser().execute("hideOverlay()");
   }

   private void afterLoginOrLogout(Event event)
   {
      // after login or logout, check the license and hide/display the license overlay
      parent.getDisplay().syncExec(() -> checkLicense());
   }

   private LicenseResponse fetchLicenseStatus(final long timeoutInMillis)
   {
      // check if the license is valid
      LicenseResponse licenseResponse = fetchLicense(VendorDemoPlugin.SOLUTION_ID);
      final long tryUntil = System.currentTimeMillis() + timeoutInMillis;
      // repeat the call for the specified interval while the validity is "WAIT"
      while (licenseResponse.getValidity() == Validity.WAIT && System.currentTimeMillis() <= tryUntil)
      {
         try
         {
            Thread.sleep(500);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
         licenseResponse = fetchLicense(VendorDemoPlugin.SOLUTION_ID);
      }
      return licenseResponse;
   }

   private LicenseResponse fetchLicense(String solutionId)
   {
      return LicensingClient.get().queryLicense(new LicenseRequest(solutionId, null, 1, VendorDemoPlugin.VENDOR_KEY));
   }
}
