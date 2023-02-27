package de.yatta.ideintruders.client.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

public class BrowserWrapper extends EditorPart
{

   public static final String EDITOR_ID = "de.yatta.ideintruders.editors.browserWrapper";

   private Browser browser;

   private LocationListener locationListener;
   private Image titleImage;

   @Override
   public void doSave(IProgressMonitor monitor)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void doSaveAs()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException
   {
      setSite(site);
      setInput(input);
   }

   @Override
   public boolean isDirty()
   {
      return false;
   }

   @Override
   public boolean isSaveAsAllowed()
   {
      return false;
   }

   @Override
   public void createPartControl(Composite parent)
   {
      initBrowser(parent);

      IEditorInput editorInput = getEditorInput();
      setPartName(editorInput.getName());
      if (editorInput instanceof BrowserWrapperInput)
      {
         BrowserWrapperInput input = (BrowserWrapperInput)editorInput;
         browser.setUrl(input.getUrl());

         String titleImagePath = input.getTitleImagePath();
         if (titleImagePath != null)
         {
            if (titleImage != null)
            {
               titleImage.dispose();
            }
            titleImage = ImageDescriptor.createFromFile(getClass(), titleImagePath).createImage();
            setTitleImage(titleImage);
         }
      }

      openExternalSitesInExternalBrowser(true);
   }

   protected void initBrowser(Composite parent)
   {
      browser = new Browser(parent, SWT.NONE);
   }

   @Override
   public void setFocus()
   {
      browser.setFocus();
   }

   public Browser getBrowser()
   {
      return browser;
   }

   public static String buildFileUrlForResource(String resource)
   {
      try
      {
         ClassLoader classLoader = BrowserWrapper.class.getClassLoader();
         int index = resource.indexOf("/", 1) + 1;
         if (index > 1 && index < resource.length())
         {
            // get base folder first; the content is extracted to a cache if necessary
            // (e.g. if the files are located inside the jar)
            String base = resource.substring(0, index);
            FileLocator.toFileURL(classLoader.getResource(base));
         }
         URL url = FileLocator.toFileURL(classLoader.getResource(resource));
         return url.toExternalForm();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   protected void openExternalSitesInExternalBrowser(boolean enable)
   {
      if (locationListener != null)
      {
         // remove existing listener
         browser.removeLocationListener(locationListener);
         locationListener = null;
      }

      if (enable)
      {
         locationListener = LocationListener.changingAdapter(event -> {
            if (!event.location.startsWith("file:") && !event.location.startsWith("mailto:"))
            {
               // location is not local
               // stop navigation on embedded browser
               event.doit = false;
               try
               {
                  // open location in external browser instead
                  PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(event.location));
               }
               catch (PartInitException | MalformedURLException e)
               {
                  throw new RuntimeException(e);
               }
            }
         });
         browser.addLocationListener(locationListener);
      }
   }

}
