package server.net;

import java.util.ArrayList;
import java.util.List;

import server.game.GameState;

/**
 * This class functions as a game lobby with internal chat and game.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Lobby {

  public List<Connection> joinedPlayers = new ArrayList<>();
  String lobbyName;
  LobbyState state = LobbyState.OPEN;
  public GameState gameState;
  int readyPlayers;

  enum LobbyState {
    OPEN, FULL, RUNINNG;
  }

  public Lobby(String lobbyName) {
    this.lobbyName = lobbyName;
    readyPlayers = 0;
  }

  /**
   * This method allows to add a Player to a lobby.
   *
   * @param connection to know whom to add.
   */

  public void addPlayer(Connection connection) {
    if (state == LobbyState.OPEN) {
      joinedPlayers.add(connection);

      if (joinedPlayers.size() == 4) {
        state = LobbyState.FULL;
      }
    } else {
      ServerProtocol.lobbyError(connection.name, "The Lobby is full. You were not able to join.");
    }
  }

  /**
   * This method allows to remove a player from a lobby.
   *
   * @param connection to know whom to disconnect.
   */

  public void removePlayer(Connection connection) {
    for (Connection player : joinedPlayers) {
      if (player.name.equalsIgnoreCase(connection.name)) {
        connection.inLobby = null;
        joinedPlayers.remove(connection);
        if (gameState != null) {
          state = LobbyState.OPEN;
          disqualifyPlayer(connection.name);
        }
        return;
      }
    }
  }

  public List<Connection> getJoinedPlayers() {
    return joinedPlayers;
  }

  public String getLobbyName() {
    return lobbyName;
  }

  public String getLobbyState() {
    return state.toString();
  }

  /**
   * Starts a game by setting the game state to running.
   */

  public void startGame() {
    state = LobbyState.RUNINNG;
    gameState = new GameState(joinedPlayers);
  }

  /**
   * Ends a game by setting the lobby state to running
   * and resetting the gameState field.
   */

  public void endGame() {
    //Tony Stark dies
    state = LobbyState.OPEN;
    gameState = null;
    readyPlayers = 0;
  }

  /**
   * Increments or decreases the number of ready players in the lobby..
   *
   * @param ready whether the value should be increased or decreased.
   */

  public void playerReady(boolean ready) {
    if (ready) {
      readyPlayers++;
      for (Connection player : joinedPlayers) {
        player.output.println(readyPlayers + "/" + joinedPlayers.size() + " players ready.");
        player.output.flush();
      }
      if (readyPlayers == joinedPlayers.size()) {
        startGame();
      }
    } else {
      readyPlayers--;
      for (Connection player : joinedPlayers) {
        player.output.println(readyPlayers + "/" + joinedPlayers.size() + " players ready.");
        player.output.flush();
      }
    }
  }

  /**
   * Disqualifies a participant of the race following the
   * leaving of the track.
   *
   * @param name chosen username of the player to be disqualified.
   */

  public void disqualifyPlayer(String name) {
    gameState.finishedPlayers++;
    gameState.disqualifiedPlayers.add(name);
    for (Connection player : joinedPlayers) {
      player.output.println(name + " has been disqualified!");
      player.output.flush();
    }
    if (gameState.finishedPlayers == gameState.startingPlayers) {
      gameState.endRace(name);
    }
  }
}
