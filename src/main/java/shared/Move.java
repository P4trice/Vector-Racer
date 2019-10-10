package shared;

import static client.graphics.GameNode.localPlayerEntity;

import client.graphics.GameNode;
import client.net.Sender;
import server.game.Location;
import server.net.ServerProtocol;

/**
 * This class transmits information on players moves.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Move {
  Location place;
  String name;

  /**
   * Creates an instance of Move assigning a name and a
   * location to the object.
   *
   * @param name name of the player that executes a move.
   * @param place object that holds positionX and positionY coordinates.
   */

  public Move(String name, Location place) {
    this.name = name;
    this.place = place;
  }

  /**
   * Creates an instance of Move assigning a name and a
   * location to the object.
   *
   * @param name name of the player that executes a move.
   * @param x horizontal position.
   * @param y vertical position.
   */

  public Move(String name, int x, int y) {
    this.name = name;
    place = new Location(x, y);
  }

  public String toString() {
    String output = "/move " + place.toString() + " " + name;
    return output;
  }

  /**
   * Calls methods to move the rendered player entity
   * on the screen and transmit the move to the server.
   *
   * @param move Information on the player that has done a move and the new position post-move.
   */

  public void clientMove(Move move) {
    if (!localPlayerEntity.getName().equalsIgnoreCase("practice")) {
      Sender.sendMessage(move.toString());
    }
    GameNode.movePlayerEntity(
            GameNode.tiles.get(GameNode.getTileIndexOfCoordinate(place.positionX, place.positionY)),
            GameNode.tiles.get(GameNode.getTileIndexOfCoordinate(
                    move.place.positionX, move.place.positionY)),
            GameNode.localPlayerEntity);
    place.positionX = move.place.positionX;
    place.positionY = move.place.positionY;
  }

  public void serverMove(Move move) {
    ServerProtocol.sendMove(move.toString());
  }
}
