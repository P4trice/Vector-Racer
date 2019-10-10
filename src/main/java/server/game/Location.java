package server.game;

/**
 * This class stores information on position.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Location {
  public int positionX;
  public int positionY;

  public Location(int positionX, int positionY) {
    this.positionX = positionX;
    this.positionY = positionY;
  }

  public Location() {
    this.positionX = 0;
    this.positionY = 0;
  }

  public String toString() {
    return "X: " + this.positionX + " " + "Y: " + this.positionY;
  }

  public int getPositionX() {
    return positionX;
  }

  public int getPositionY() {
    return positionY;
  }
}
