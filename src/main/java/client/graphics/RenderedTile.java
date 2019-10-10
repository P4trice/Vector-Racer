package client.graphics;

import client.net.Client;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javax.imageio.ImageIO;

/**
 * This class represents a tile (a positionX-positionY position) on the playing field.
 *
 * <p>The object fields are necessary to enforce the game rules and mechanics.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class RenderedTile extends StackPane {

  private BufferedImage tileSheet;
  private Image texture;
  private int tileSize;
  private int xpos;
  private int ypos;
  private boolean road;
  public boolean playerOnTile;
  public boolean clickable = false;
  public Rectangle block;

  /**
   * Creates a tile based on its intended position and texturizes it using a tile sheet.
   *
   * <p>This method checks the hex-red value of a pixel on the tile sheet. The pixel checked
   * is determined by the positionX and positionY value of the tile. After the red value is parsed,
   * the corresponding texture image is loaded onto the tile.
   *
   * @param x              horizontal coordinate of the tile.
   * @param y              vertical coordinate of the tile.
   * @param tileSheetTrack path of the image file that holds the tile sheet for the track.
   * @param tileSize       width and height of a square-shaped tile.
   */

  public RenderedTile(int x, int y, String tileSheetTrack, int tileSize) {
    xpos = x;
    ypos = y;

    try {
      tileSheet = ImageIO.read(getClass().getResource("/" + tileSheetTrack));
    } catch (IOException e) {
      e.printStackTrace();
    }

    int width = tileSheet.getWidth();
    int height = tileSheet.getHeight();
    int[] colorTileSheet = tileSheet.getRGB(0, 0, width, height, null, 0, width);
    int red = (colorTileSheet[x + y * width] >> 16) & 0xFF;

    texture = GameNode.textures.getTexture(red);

    if (red > 0) {
      road = true;
    } else {
      road = false;
    }

    this.tileSize = tileSize;

    block = new Rectangle(tileSize, tileSize);
    block.setFill(new ImagePattern(texture));

    setOnMousePressed(event -> {
      //Client.LOGGER.info(clickable); //remove for debugging
      if (clickable) {
        Client.localPlayerInfo.movePlayer(x, y);
      }
    });


    getChildren().addAll(block);
  }

  public int getXpos() {
    return xpos;
  }

  public int getYpos() {
    return ypos;
  }

  public boolean isRoad() {
    return road;
  }

  /**
   * Checks, whether the tile would be a legal move if a player wanted to access it.
   *
   * <p>This is achieved by checking the properties that describe, whether the tile is
   * part of the track and if a player is currently on that tile.
   *
   * @return whether the tile would be a legal move or not.
   */

  public boolean isFree() {
    return (road && !playerOnTile);
  }

  public void setIsClickable(boolean input) {
    clickable = input;
  }
}
