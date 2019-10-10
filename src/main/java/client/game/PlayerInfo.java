package client.game;

import static client.graphics.GameNode.localPlayerEntity;

import client.graphics.GameNode;

import java.util.ArrayList;
import java.util.List;

import server.game.Location;
import shared.Move;



/**
 * This class stores and provides local information of a player.
 *
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class PlayerInfo {
  public Location currentLocation;
  public Location prevLocation;
  public Location nextVectorArrivalPoint;
  public String name;
  public Move move;
  private List<Location> possibleMoves;

  /**
   * Creates an instance of PlayerInfo with a player name and sets the initial speed and position.
   *
   * <p>Also initializes object fields necessary for the game logic and mechanics.
   *
   * @param name specifies the value of the field that holds the name of the player.
   * @param x specifies the initial x position.
   * @param y specifies the initial y position.
   */

  public PlayerInfo(String name, int x, int y) {
    possibleMoves = new ArrayList<>();
    this.name = name;
    prevLocation = new Location();
    currentLocation = new Location(x, y);

    /**
     * Holds an object required to transmit moves to the server.
     */

    move = new Move(name, currentLocation);

    nextVectorArrivalPoint =
            new Location(
                    currentLocation.positionX, currentLocation.positionY - 1);
    //sets starting speed at 1
  }

  /**
   * Updates the position of a player and previous/next vector.
   *
   * @param x specifies the horizontal coordinate of the new tile in
   *          relation to the upper left corner (increases rightward).
   * @param y specifies the horizontal coordinate of the new tile in
   *          relation to the upper left corner (increases downward).
   */

  public void movePlayer(int x, int y) {
    GameNode.removeCircles();
    GameNode.resetClickableTiles();
    prevLocation.positionX = currentLocation.getPositionX();
    prevLocation.positionY = currentLocation.getPositionY();
    move.clientMove(new Move(GameNode.localPlayerEntity.getName(), x, y));
    setCurrentLocation(x, y);
    nextVectorArrivalPoint.positionX =
            (currentLocation.positionX - prevLocation.positionX) + currentLocation.positionX;
    nextVectorArrivalPoint.positionY =
            (currentLocation.positionY - prevLocation.positionY) + currentLocation.positionY;
    possibleMoves = GameInfo.showPossibleMoves(this);
    GameNode.drawPossibleMoves(possibleMoves, nextVectorArrivalPoint);
  }

  /**
   * Examines movement.
   */
  public void checkMoves() {
    GameNode.removeCircles();
    GameNode.resetClickableTiles();
    possibleMoves = GameInfo.showPossibleMoves(this);
    GameNode.drawPossibleMoves(possibleMoves, nextVectorArrivalPoint);
  }

  /**
   * Updates the position of a player without altering vectors.
   *
   * @param x specifies the horizontal coordinate of the new tile in
   *          relation to the upper left corner (increases rightward).
   * @param y specifies the horizontal coordinate of the new tile in
   *          relation to the upper left corner (increases downward).
   */

  public void setCurrentLocation(int x, int y) {
    currentLocation.positionX = x;
    currentLocation.positionY = y;
  }

  /**
   * Updates the position of a player without altering vectors.
   *
   * @param loc specifies the positionX-positionY coordinate of the new tile in relation to
   *            the upper left corner (positionX increases rightward and positionY downward).
   */

  public void setCurrentLocation(Location loc) {
    currentLocation.positionX = loc.positionX;
    currentLocation.positionY = loc.positionY;
  }

  /**
   * Getter for the x-y coordinate of the end of the vector that is added to the current position.
   *
   * @return x-y coordinate of the next vectors arrival point.
   */

  public Location getNextVectorArrivalPoint() {
    return nextVectorArrivalPoint;
  }

  /**
   * Getter for the positionX-positionY coordinate of the players position.
   *
   * @return x-y coordinate of the next vectors arrival point.
   */

  public Location getCurrentLocation() {
    return currentLocation;
  }
}