package de.yatta.softwarevendor.demo.client.handlers;

public enum Game {

  TILES_2048("2048", "http://axilleasiv.github.io/vue2048/"),
  CLUMSY_BIRD("Clumsy Bird", "http://ellisonleao.github.io/clumsy-bird/"),
  PACMAN("Pacman", "https://pacman.platzh1rsch.ch/"),
  SPACE_INVADERS("Space Invaders", "/space-invaders/index.html/");

  private final String url;
  private final String name;

  Game(String name, String url) {
    this.url = url;
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  public String getUrl() {
    return url;
  }

}