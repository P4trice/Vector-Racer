package client.graphics;

import static client.graphics.GameNode.TILE_SIZE;

import javafx.scene.shape.Line;
import javafx.scene.transform.Translate;

/**
 * This class provides vectors, which are a core game component.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Vector extends Line {
  /**
   * An offset of pixels is required as rendering in JavaFX does not
   * render the shapes in the center of a tile.
   */
  double offset = (double) TILE_SIZE / 2;

  public Vector() { }

  /**
   * Creates a vector with length 0 given a starting point.
   *
   * @param startX positionX-coordinate of the tile the vector starts.
   * @param startY positionY-coordinate of the tile the vector starts.
   */

  public Vector(int startX, int startY) {
    setStartX(startX * TILE_SIZE + offset);
    setStartY(startY * TILE_SIZE + offset);

    setEndX(startX * TILE_SIZE + offset);
    setEndY(startY * TILE_SIZE - TILE_SIZE + offset);
  }

  /**
   * Sets a vector between two given tiles.
   *
   * @param oldTile tile that represents the starting point of the vector.
   * @param newTile tile that represents the ending point of the vector.
   */
  public void bind(RenderedTile oldTile, RenderedTile newTile) {
    setStartX((oldTile.getXpos() * TILE_SIZE) + offset);
    setStartY((oldTile.getYpos() * TILE_SIZE) + offset);

    setEndX((newTile.getXpos() * TILE_SIZE) + offset);
    setEndY((newTile.getYpos() * TILE_SIZE) + offset);
  }

  /**
   * Sets a vector between a tile and an other by using the route taken to said tile.
   * The used route will be added to the current tile. The arrival point of the
   * vector is the sum.
   *
   * @param currentTile tile that represents the starting point of the vector.
   * @param translation route taken to get to the current tile.
   */

  public void bindNext(RenderedTile currentTile, Translate translation) {
    setStartX((currentTile.getXpos() * TILE_SIZE) + offset);
    setStartY((currentTile.getYpos() * TILE_SIZE) + offset);

    setEndX(getStartX() + translation.getX());
    setEndY(getStartY() + translation.getY());
  }
}

