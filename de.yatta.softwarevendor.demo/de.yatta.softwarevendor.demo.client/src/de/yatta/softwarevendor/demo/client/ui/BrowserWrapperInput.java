package de.yatta.softwarevendor.demo.client.ui;

import java.util.Objects;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class BrowserWrapperInput implements IEditorInput {

  private final String title;
  private final String url;

  public BrowserWrapperInput(String title, String url) {
    super();
    this.title = title;
    this.url = url;
  }

  @Override
  public <T> T getAdapter(Class<T> adapter) {
    return null;
  }

  @Override
  public boolean exists() {
    return false;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return ImageDescriptor.getMissingImageDescriptor();
  }

  @Override
  public String getName() {
    return title;
  }

  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return getName();
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, url);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    BrowserWrapperInput other = (BrowserWrapperInput) obj;
    return Objects.equals(title, other.title)
        && Objects.equals(url, other.url);
  }

}
