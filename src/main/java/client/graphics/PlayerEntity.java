package client.graphics;

import client.net.Client;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import server.net.ServerProtocol;

/**
 * This class holds information related to graphical representation of a player.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class PlayerEntity extends Rectangle {
  public Vector prevVector;
  public Vector nextVector;
  static int counter;
  private ImageView renderedCar;
  private Image texture;
  private String name;
  private int xpos;
  private int ypos;
  public double currentAngle;

  /**
   * Creates a car model given a position and name.
   *
   * @param initialX starting positionX coordinate of the rendered car model.
   * @param initialY starting positionY coordinate of the rendered car model.
   * @param name     name of the player.
   */

  public PlayerEntity(int initialX, int initialY, String name) {
    setMouseTransparent(true);
    this.name = name;
    xpos = initialX;
    ypos = initialY;

    prevVector = new Vector();

    nextVector = new Vector(xpos, ypos);

    switch (counter) {
      case 0: {
        texture = new Image(getClass().getResource("/car_black.png").toString());
        prevVector.setStroke(new Color(0, 0, 0, 0.2));
        nextVector.setStroke(new Color(0, 0, 0, 1));
        break;
      }
      case 1: {
        texture = new Image(getClass().getResource("/car_blue.png").toString());
        prevVector.setStroke(new Color(0, 0, 1, 0.2));
        nextVector.setStroke(new Color(0, 0, 1, 1));
        break;
      }
      case 2: {
        texture = new Image(getClass().getResource("/car_green.png").toString());
        prevVector.setStroke(new Color(0, 1, 0, 0.2));
        nextVector.setStroke(new Color(0, 1, 0, 1));
        break;
      }
      case 3: {
        texture = new Image(getClass().getResource("/car_yellow.png").toString());
        prevVector.setStroke(new Color(1, 1, 0, 0.2));
        nextVector.setStroke(new Color(1, 1, 0, 1));
        break;
      }
      default: {
        Client.LOGGER.info("Something went wrong");
      }
    }

    renderedCar = new ImageView(texture);
    renderedCar.setPreserveRatio(true);
    renderedCar.setFitWidth(GameNode.TILE_SIZE * 0.7);
    renderedCar.setX(GameNode.TILE_SIZE * (initialX + 0.15));
    renderedCar.setY(GameNode.TILE_SIZE * (initialY - 0.15));
    counter++;
  }

  /**
   * Sets the logical position of a player entity.
   *
   * @param newX new x-coordinate in relation to the track.
   * @param newY new y-coordinate in relation to the track.
   */

  public void setPosition(int newX, int newY) {
    xpos = newX;
    ypos = newY;
    renderedCar.setX(GameNode.TILE_SIZE * (newX + 0.15));
    renderedCar.setY(GameNode.TILE_SIZE * (newY - 0.15));
  }

  public int getXpos() {
    return xpos;
  }

  public int getYpos() {
    return ypos;
  }

  public String positiontoString() {
    return xpos + ", " + ypos;
  }

  public ImageView getRenderedCar() {
    return renderedCar;
  }

  public Vector getPrevVector() {
    return prevVector;
  }

  public Vector getNextVector() {
    return nextVector;
  }

  public String getName() {
    return name;
  }
}
