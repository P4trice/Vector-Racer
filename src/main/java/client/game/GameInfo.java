package client.game;

import client.graphics.GameNode;
import java.util.ArrayList;
import java.util.List;

import server.game.Location;

/**
 * This class stores and provides local information of the current game.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class GameInfo {
  public static List<PlayerInfo> lobby = new ArrayList<>();

  /**
   * Creates an instance of GameInfo with a lobby name.
   * Starts the run()-method of the object after initialization of the object variables.
   *
   */

  public GameInfo() { }

  /**
   * Returns a List of legal moves depending on a players x-y location and surroundings.
   *
   * <p>This is done by checking every neighboured tile of the players position. For each
   * tile, the method calls an auxiliary method to check whether the tile is available to
   * select or not.
   *
   * @param player specifies the value of the field that holds the name of the lobby.
   * @return a collection of legal moves the player is able to make in x and y coordinates.
   */

  public static List<Location> showPossibleMoves(PlayerInfo player) {
    List<Location> moves = new ArrayList<>();
    Location nextVector = player.getNextVectorArrivalPoint();

    for (int x = -1; x < 2; x++) {
      for (int y = -1; y < 2; y++) {
        if (isPossibleMove(
                player.nextVectorArrivalPoint.positionX
                        + x, player.nextVectorArrivalPoint.positionY + y)) {
          moves.add(new Location(nextVector.positionX + x, nextVector.positionY + y));
        }
      }
    }
    return moves;
  }

  /**
   * Checks, whether a tile is accessible by asserting the tile's field values and location.
   *
   * <p>This is done by checking a certain object field of a given tile. Also checks, if the
   * chosen tile is out of world bounds.
   *
   * @param x the horizontal coordinate of the tile
   *          in relation to the upper left corner (increases rightward).
   * @param y the vertical coordinate of the tile
   *          in relation to the upper left corner (increases downward).
   * @return whether the specified tile is a legal move or not.
   */

  public static boolean isPossibleMove(int x, int y) {
    try {
      if (GameNode.tiles.get(GameNode.getTileIndexOfCoordinate(x, y)).isFree() && x > 0 && x < 27) {
        return true;
      } else {
        return false;
      }
    } catch (IndexOutOfBoundsException e) {
      return false; //tile is unavailable because it is not part of the world
    }
  }

  /**
   * Adds a player to the local list of players in the game
   *
   * <p>This method is completely WIP.
   *
   * @param player data of the player to be added
   */

  public void addPlayer(PlayerInfo player) {
    lobby.add(player);
  }
}