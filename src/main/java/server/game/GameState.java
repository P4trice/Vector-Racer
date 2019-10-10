package server.game;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import server.net.Connection;
import server.net.Server;
import server.net.ServerProtocol;

/**
 * This class transmits moves done by players in a game.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class GameState {
  /**
   * Holds a list of participating players of a game.
   */
  public List<Connection> players;
  public List<String> disqualifiedPlayers = new ArrayList<>();
  HashMap<String, Progress> raceState = new HashMap<>();
  public int finishedPlayers = 0;
  public int startingPlayers;

  /**
   *Initializes game with corresponding players.
   *
   * @param players players participating in the race.
   */
  public GameState(List<Connection> players) {
    this.players = players;
    startingPlayers = players.size();
    ranking = new String[players.size()];
    String names = "";
    for (Connection player : players) {
      names = names.concat(player.name + ",");
      raceState.put(player.name, new Progress(player.name, 1));
    }

    for (Connection player : players) {
      player.output.println("/race " + names);
      player.output.flush();
    }
  }

  String[] ranking;

  /**
   * Sends a move done by player to all other players in the game by
   * iterating through the list of players.
   *
   * @param name name of the player who made a move
   * @param x horizontal coordinate of the players new position
   * @param y vertical coordinate of the players new position
   */
  public void sendMove(String name, int x, int y) {
    raceState.get(name).updateProgress(x, y);
    if (raceState.get(name).raceFinished) {
      ranking[finishedPlayers - disqualifiedPlayers.size()] = name;
      finishedPlayers++;
      for (Connection player : players) {
        player.output.println(name + " has finished the race as #"
                + (finishedPlayers - disqualifiedPlayers.size()) + "!");
        player.output.flush();
        if (player.name.equalsIgnoreCase(name)) {
          player.output.println("/done");
          player.output.flush();
        }
      }
    }

    for (Connection player : players) {
      if (!player.name.equalsIgnoreCase(name)) {
        player.output.println("/move " + name + "," + x + "," + y);
        player.output.flush();
      }
    }

    if (finishedPlayers == startingPlayers) {
      endRace(name);
    }
  }

  /**
   * Called upon the end of the race, displays results of
   * the race into the chat of all players. Also sends a
   * command resetting lobby state and activating practice
   * mode.
   *
   * @param name username of the player belonging in the lobby
   *             of the race.
   */

  public void endRace(String name) {
    for (Connection player : players) {
      player.output.println("Race has concluded!");
      for (int i = 0; i < startingPlayers - disqualifiedPlayers.size(); i++) {
        player.output.println("#" + (i + 1) + ": " + ranking[i]);
        if (i == 0) {
          updateHighscore(ranking[i]);
        }
      }

      if (disqualifiedPlayers.size() > 0) {
        player.output.println("DQ'd: " + disqualifiedPlayers.toString());
      }

      player.output.flush();
    }

    for (Connection player : players) {
      if (player.name.equalsIgnoreCase(name)) {
        player.inLobby.endGame();
      }
      player.output.println("Returning to lobby...");
      player.output.flush();
      player.output.println("/fini");
      player.output.flush();
    }
  }

  /**
   * This method keeps the text-file containing the points up to date.
   *
   * @param name name of the player, whose highscore needs to be updated.
   */
  void updateHighscore(String name) {
    try {
      ArrayList<String> lines = Server.readHighscore();

      for (int i = 0; i < lines.size(); i++) {
        String[] temps = lines.get(i).split(" ");
        if (name.equalsIgnoreCase(temps[0])) {
          int currentScore = Integer.parseInt(lines.get(i).substring(60));
          currentScore += 1;
          lines.set(i, lines.get(i).substring(0, 59) + " " + currentScore);
          //ServerProtocol.LOGGER.info(lines.get(i));
        }
      }

      FileWriter fw = new FileWriter(Server.path);

      for (int i = 0; i < lines.size(); i++) {
        try {
          fw.write(lines.get(i) + "\r\n");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      fw.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}