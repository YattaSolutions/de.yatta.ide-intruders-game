package de.yatta.softwarevendor.demo.client.handlers;

public enum Game {

  TILES_2048("2048", "http://axilleasiv.github.io/vue2048/"),
  CLUMSY_BIRD("Clumsy Bird", "http://ellisonleao.github.io/clumsy-bird/"),
  PACMAN("Pacman", "https://pacman.platzh1rsch.ch/"),
  SPACE_INVADERS("Space Invaders", "space-invaders/index.html", "/icons/vendor-icon-16x16.png");

  private final String url;
  private final String name;
  private final String titleImage;

  Game(String name, String url) {
    this(name, url, null);
  }

  Game(String name, String url, String titleImage) {
    this.url = url;
    this.name = name;
    this.titleImage = titleImage;
  }

  @Override
  public String toString() {
    return name;
  }

  public String getUrl() {
    return url;
  }
  
  public String getTitleImage() {
    return titleImage;
  }

}