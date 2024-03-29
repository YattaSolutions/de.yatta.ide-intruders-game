package de.yatta.ideintruders.client.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;

import com.yattasolutions.platform.marketplace.client.MarketplaceClient;
import com.yattasolutions.platform.marketplace.client.MarketplaceClientPlugin;

import de.yatta.ideintruders.VendorDemoPlugin;
import de.yatta.platform.marketplace.licensing.client.LicenseRequest;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse;
import de.yatta.platform.marketplace.licensing.client.LicenseResponse.Validity;
import de.yatta.platform.marketplace.licensing.client.LicensingClient;

public class GameEditor extends BrowserWrapper
{

   public static final String EDITOR_ID = "de.yatta.ideintruders.editors.gameEditor";

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
            resetDemo();
            return null;
         }
      };
      new BrowserFunction(getBrowser(), "subscribeGame") {
         @Override
         public Object function(Object[] arguments)
         {
            MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), VendorDemoPlugin.PRODUCT_ID, VendorDemoPlugin.ENVIRONMENT);
            checkLicense();
            return null;
         }
      };

      new BrowserFunction(getBrowser(), "purchaseGame") {
         @Override
         public Object function(Object[] arguments)
         {
            MarketplaceClient.get().openCheckout(MarketplaceClientPlugin.getDisplay(), VendorDemoPlugin.PRODUCT_ID_ONETIMEPURCHASE, VendorDemoPlugin.ENVIRONMENT);
            checkLicense();
            return null;
         }
      };

      new BrowserFunction(getBrowser(), "signIn") {
         @Override
         public Object function(Object[] arguments)
         {
            MarketplaceClient.get().showSignInPage(MarketplaceClientPlugin.getDisplay(), VendorDemoPlugin.PRODUCT_ID, VendorDemoPlugin.ENVIRONMENT);
            checkLicense();
            return null;
         }
      };

   }

   private void resetDemo()
   {
      LicenseResponse licenseResponse = fetchLicenseStatus(5000);
      if (licenseResponse.getLicenseTypeId().endsWith(VendorDemoPlugin.PRODUCT_ID_ONETIMEPURCHASE))
      {
         MarketplaceClient.get().showDeleteDemoOrderDialog(parent.getDisplay(),
               VendorDemoPlugin.PRODUCT_ID_ONETIMEPURCHASE, VendorDemoPlugin.ENVIRONMENT, true);
      }
      else
      {
         MarketplaceClient.get().showCancelDialog(parent.getDisplay(), VendorDemoPlugin.PRODUCT_ID, VendorDemoPlugin.ENVIRONMENT, true);
      }
      licenseResponse = resetLicense();
      checkLicense();
   }

   @Override
   public void setFocus()
   {
      checkLicense();
      getBrowser().setFocus();
   }

   @Override
   public void dispose()
   {
      super.dispose();
      if (getTitleImage() != null)
      {
         getTitleImage().dispose();
      }
      if (serviceRegistration != null)
      {
         serviceRegistration.unregister();
      }
   }

   private void checkLicense()
   {

      Job updateJob = new Job(String.format("Query product license")) {
         @Override
         protected IStatus run(IProgressMonitor monitor)
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

            Display.getDefault().asyncExec(new Runnable() {
               public void run()
               {
                  if (isLicensed)
                  {
                     hideOverlay();
                  }
                  else
                  {
                     showOverlay(isNotLoggedIn);
                  }
               }
            });

            return Status.OK_STATUS;
         }
      };
      updateJob.setSystem(true);
      updateJob.schedule();

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
      LicenseResponse licenseResponse = fetchLicense(VendorDemoPlugin.PRODUCT_ID);
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
         licenseResponse = fetchLicense(VendorDemoPlugin.PRODUCT_ID);
      }
      return licenseResponse;
   }

   private LicenseResponse resetLicense()
   {
      LicenseRequest licenseRequest = new LicenseRequest(VendorDemoPlugin.PRODUCT_ID, VendorDemoPlugin.ENVIRONMENT, null, 1, VendorDemoPlugin.VENDOR_KEY);
      licenseRequest.setForceRefresh(true);
      return LicensingClient.get().queryLicense(licenseRequest);
   }

   private LicenseResponse fetchLicense(String solutionId)
   {
      LicenseRequest licenseRequest = new LicenseRequest(solutionId, VendorDemoPlugin.ENVIRONMENT, null, 1, VendorDemoPlugin.VENDOR_KEY);
      return LicensingClient.get().queryLicense(licenseRequest);
   }
}
