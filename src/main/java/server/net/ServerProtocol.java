package server.net;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDateTime;


/**
 * This class mainly contains methods to process input to function as a server protocol by checking
 * incoming messages and triggering responses/commands.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */
public class ServerProtocol {


  /**
   * Holds the list of all connected clients.
   */
  public static List<Connection> playerList;

  /**
   * Holds the list of all connected lobbies.
   */

  static List<Lobby> lobbyList;

  public static final Logger LOGGER = LogManager.getLogger("SERVER_LOG");

  /**
   * This method takes the input and looks for specific characters to decide whether or not it is a
   * command. If a command is given, an according response is triggered. If no command is recognized
   * it will be sent as a Chat message. Right now the possible commands are as followed:
   *
   * <p>/lout to log out
   *
   * <p>/dmsg to send a direct message / whisper message
   *
   * <p>/nick to change nickname
   *
   * <p>/ping is soon to be added
   *
   * <p>/list to display a list of all players online
   *
   * <p>/move to process a move
   *
   * <p>/lbby to create a lobby
   *
   * <p>/join to join an existing lobby
   *
   * <p>/leav to leave a lobby
   *
   * <p>/loli to see a list of lobbies.
   *
   * <p>/cast to send a message to all players online.
   *
   * <p>/read to set the player in the lobby to ready.
   *
   * <p>/unrd to set the player in the lobby to not ready.
   *
   * <p>/dqed to disqualify a player in a race.
   *
   * <p>/help to see all available commands.
   *
   * <p>/wins to see your highscore.
   *
   * <p>default is now set to send messages inside the lobby the player is in
   *
   * @param input message/command coming in from the user
   * @param name  name of person giving input
   */

  public static void process(String input, String name) {
    String command = "default";

    if (input.charAt(0) == '/') {
      if (input.length() < 5) {
        ;
      } else {
        command = input.substring(1, 5);
        command.toLowerCase();
      }
    }

    switch (command) {
      case "lout": {
        for (int i = 0; i < playerList.size(); i++) {
          if (playerList.get(i).name.equalsIgnoreCase(name)) {
            logout(i);
          }
        }
        break;
      }
      case "dmsg": {
        int i = input.indexOf(" ", 6);
        sendDirectMessage(name, input.substring(6, i), input.substring(i + 1));
        break;
      }
      case "nick": {
        changeUsername(name, input.substring(6));
        break;
      }
      case "ping": {
        displayPing(name);
        break;
      }
      case "list": {
        getAllPlayers(name);
        break;
      }
      case "move": {
        String str = input.substring(6);
        String[] split = str.split("\\s+");
        int x = Integer.parseInt(split[1]);
        int y = Integer.parseInt(split[3]);
        for (Connection player : playerList) {
          if (player.name.equalsIgnoreCase(name) && player.inLobby != null) {
            player.inLobby.gameState.sendMove(name, x, y);
            LOGGER.info(
                    name + " in lobby '"
                            + player.inLobby.getLobbyName()
                            + "' made a move to: (" + x + ", " + y + ")");
          }
        }
        break;
      }

      case "lbby": {
        try {
          if (input.charAt(6) != ' ') {
            createLobby(input.substring(6), name);
          } else {
            lobbyError(name, "You tried creating a lobby without a name, try again.");
          }
        } catch (Exception e) {
          lobbyError(name, "You tried creating a lobby without a name, try again.");
        }
        break;
      }

      case "join": {
        joinLobby(name, input.substring(6));
        break;
      }

      case "race": {
        startLobbyGame(name);
        break;
      }

      case "leav": {
        leaveLobby(name);
        break;
      }

      case "loli": {
        showLobbies(name);
        break;
      }

      case "cast": {
        broadcastMessage(input.substring(6), name);
        break;
      }

      case "read": {
        for (Connection player : playerList) {
          if (name.equalsIgnoreCase(player.name)) {
            player.inLobby.playerReady(true);
          }
        }
        break;
      }

      case "unrd": {
        for (Connection player : playerList) {
          if (name.equalsIgnoreCase(player.name)) {
            player.inLobby.playerReady(false);
          }
        }
        break;
      }

      case "dqed": {
        for (Connection player : playerList) {
          if (name.equalsIgnoreCase(player.name)) {
            player.inLobby.disqualifyPlayer(name);
          }
        }
        break;
      }

      case "help": {
        for (Connection player : playerList) {
          if (player.name.equalsIgnoreCase(name)) {
            player.output.println("Our commands are: /help \n"
                    + "/lout to log out \n"
                    + "/nick to change nickname \n"
                    + "/ping to see your ping \n"
                    + "/cast to write a message to all online players \n"
                    + "/for everything else, explore the menu."
            );
            player.output.flush();
          }
        }
        break;
      }

      case "wins": {
        showWins(name);
        break;
      }

      default: {
        for (Connection player : playerList) {
          if (player.name.equalsIgnoreCase(name)) {
            if (player.inLobby != null) {
              lobbyMessage(name, input, player.inLobby.getLobbyName());
            } else {
              lobbyError(name, "No lobby to send your message to");
            }
          }
        }
        break;
      }
    }
  }

  /**
   * Sends a message to all connected players and shows who sent the message.
   *
   * @param message gives content of message to be displayed.
   * @param name    name of person sending message
   */
  public static void broadcastMessage(String message, String name) {
    LOGGER.info(name + " broadcasted: " + message);
    for (Connection player : playerList) {
      player.output.println(getTimeMinutes() + name + " broadcasted: " + message);
      player.output.flush();
    }
  }

  /**
   * Sends a message to a chosen player given in the list and shows the Server, from whom to whom
   * the message was sent.
   *
   * @param name      name of person sending message
   * @param recipient name of person receiving
   * @param message   gives content of message to be displayed
   */
  public static void sendDirectMessage(String name, String recipient, String message) {
    LOGGER.info(name + " sent " + recipient + " the direct message: " + message);

    for (Connection player : playerList) {
      if (player.name.equalsIgnoreCase(recipient)) {
        player.output.println(
                getTimeMinutes() + name + " sent you a direct Message: " + message);
        player.output.flush();
      }
      if (player.name.equalsIgnoreCase(name)) {
        player.output.println(
                getTimeMinutes() + "you sent " + recipient + ": " + message);
        player.output.flush();
      }
    }
  }

  /**
   * Shows all lobbies currently available in the server.
   *
   * @param name name of person giving the command
   */

  public static void showLobbies(String name) {
    String list1 = getTimeMinutes() + "Lobbies available: ";
    String list2 = "/list ";
    for (Lobby lobby : lobbyList) {
      list1 = list1.concat(lobby.getLobbyName() + "(" + lobby.getLobbyState() + ")" + ", ");
    }

    for (Lobby lobby : lobbyList) {
      list2 = list2.concat(lobby.getLobbyName() + ", ");
    }

    for (Connection player : playerList) {
      if (player.name.equalsIgnoreCase(name)) {
        player.output.println(list1);
        player.output.flush();
        player.output.println(list2);
        player.output.flush();
        LOGGER.info(name + " wanted to know what lobbies are available: " + list1);
      }
    }
  }

  /**
   * Sends a message inside the current lobby.
   * If no lobby is available the user gets according feedback.
   *
   * @param name      name of the person sending the message
   * @param message   content of the message sent
   * @param lobbyName name of the according lobby
   */

  public static void lobbyMessage(String name, String message, String lobbyName) {
    for (Lobby lobby : lobbyList) {
      if (lobby.getLobbyName().equalsIgnoreCase(lobbyName)) {
        for (Connection player : lobby.getJoinedPlayers()) {
          player.output.println(getTimeMinutes() + name + ": " + message);
          player.output.flush();
          LOGGER.info(name + " sent message: " + message);
        }
      }
    }
  }

  /**
   * Sends a lobby related errormessage to the player causing the notification.
   *
   * @param name    name of the person causing the prompt
   * @param message content of errormessage
   */

  public static void lobbyError(String name, String message) {
    for (Connection player : playerList) {
      if (player.name.equalsIgnoreCase(name)) {
        player.output.println(getTimeMinutes() + message);
        player.output.flush();
        LOGGER.info(name + " caused this message: " + message);
      }
    }
  }

  /**
   * Starts a game in according lobby.
   * @param name the lobby in which to start the game.
   */

  public static void startLobbyGame(String name) {
    for (Connection player : playerList) {
      if (player.name.equalsIgnoreCase(name)) {
        player.inLobby.startGame();
      }
    }
  }

  /**
   * Closes the according lobby.
   *
   * @param closingLobby the lobby to be closed
   */

  public static void closeLobby(Lobby closingLobby) {
    for (int i = 0; i < lobbyList.size(); i++) {
      if (lobbyList.get(i).getLobbyName().equalsIgnoreCase(closingLobby.getLobbyName())) {
        String temp = lobbyList.get(i).getLobbyName();
        lobbyList.remove(i);
        LOGGER.info("closed lobby '" + temp + "'");
      }
    }
  }

  /**
   * Creates a lobby with a name.
   *
   * @param lobbyName name the lobby will display
   * @param creator   person who initiated the lobby
   */

  public static void createLobby(String lobbyName, String creator) {
    for (Connection player : playerList) {
      if (player.name.equalsIgnoreCase(creator)) {
        if (player.inLobby != null) {
          lobbyError(creator, "You are already in a Lobby.");
          return;
        }
      }
    }
    for (Lobby lobby : lobbyList) {
      if (lobby.getLobbyName().equalsIgnoreCase(lobbyName)) {
        lobbyError(creator, "Lobbyname is already taken, sorry.");
        return;
      }
    }
    lobbyList.add(new Lobby(lobbyName));
    LOGGER.info("Lobby '" + lobbyName + "' created by " + creator);
    joinLobby(creator, lobbyName);
  }

  /**
   * Lets a person join a specific lobby.
   *
   * @param name      name of the person wanting to join a lobby
   * @param lobbyName name of the lobby to join.
   */

  public static void joinLobby(String name, String lobbyName) {
    for (Connection player : playerList) {
      if (player.name.equalsIgnoreCase(name)) {
        if (player.inLobby != null) {
          lobbyError(name, "You are already in a Lobby.");
          return;
        }
        for (Lobby lobby : lobbyList) {
          if (lobby.getLobbyName().equalsIgnoreCase(lobbyName)) {
            for (Connection lobbyPlayer : lobby.getJoinedPlayers()) {
              lobbyPlayer.output.println(name + " has joined the lobby.");
              lobbyPlayer.output.flush();
            }
            lobby.addPlayer(player);
            player.inLobby = lobby;
            LOGGER.info(name + " has joined lobby '" + lobby.getLobbyName() + "'");
            player.output.println("Lobby joined: " + lobbyName);
            player.output.flush();
            player.output.println("/lbby");
            player.output.flush();
            return;
          }
        }
      }
    }
    if (lobbyName.charAt(0) != ' ') {
      createLobby(lobbyName.substring(0), name);
    } else {
      lobbyError(name, "You tried creating a lobby without a name, try again.");
    }
  }


  /**
   * Allows a player to leave the lobby he is in may result in lobby closing.
   *
   * @param name name of the player leaving a lobby
   */
  public static void leaveLobby(String name) {
    for (Connection player : playerList) {
      if (player.name.equalsIgnoreCase(name)) {

        if (player.inLobby == null) {
          lobbyError(name, "No Lobby to leave.");
          return;
        }

        for (Lobby lobby : lobbyList) {
          if (lobby.lobbyName.equalsIgnoreCase(player.inLobby.lobbyName)) {
            player.output.println("Leaving lobby '" + lobby.getLobbyName() + "'...");
            player.output.flush();
            player.output.println("/lbby");
            player.output.flush();
            lobby.removePlayer(player);
            String temp = lobby.lobbyName;
            LOGGER.info(name + " has left lobby '" + temp + "'");
            if (lobby.joinedPlayers.size() == 0) {
              ServerProtocol.closeLobby(lobby);
              return;
            } else {
              for (Connection lobbyPlayer : lobby.joinedPlayers) {
                lobbyPlayer.output.println(name + " has left the lobby.");
                lobbyPlayer.output.flush();
              }
            }
          }
        }
      }
    }
  }

  /**
   *   Sends a notification to all connected players and shows who prompted the notification.
   *
   * @param notification text of what to notify of.
   */
  public static void sendNotification(String notification) {
    LOGGER.info(notification);
    for (Connection player : playerList) {
      player.output.println(getTimeMinutes() + notification);
      player.output.flush();
    }
  }

  /**
   * is here to send the move made to all players in a lobby.
   *
   * @param move move to send to clients
   */
  public static void sendMove(String move) {
    for (Connection player : playerList) {
      player.output.println(move);
      player.output.flush();
    }
  }

  /**
   * Returns time.
   *
   * @return time exact to the minute.
   */
  public static String getTimeMinutes() {
    LocalDateTime time = new LocalDateTime();

    DecimalFormat df = new DecimalFormat("00");

    String hour = df.format(time.getHourOfDay());
    String minute = df.format(time.getMinuteOfHour());

    return "[" + hour + ":" + minute + "] ";
  }

  /**
   * Checks whether a chosen name is already taken. If so, the method will append a number to the
   * chosen name.
   *
   * @param name name to check for.
   * @return the new name assigned to the player.
   */
  public static String checkName(String name) {
    int counter = -1;
    boolean check = true;

    while (check) {
      for (int i = 0; i < playerList.size(); i++) {
        if (playerList.get(i).name.equalsIgnoreCase(name)) {
          counter += 1;
        }
      }

      if (counter > 0) {
        name = name + "_NameStealer";
        counter = 0;
      } else {
        check = false;
      }
    }

    try {
      ArrayList<String> lines = Server.readHighscore();

      for (int i = 0; i < lines.size(); i++) {
        String[] temps = lines.get(i).split(" ");
        if (name.equalsIgnoreCase(temps[0])) {
          return name;
        }
      }

      String line = name;
      for (int i = 0; i < 60 - name.length(); i++) {
        line = line + " ";
      }

      line = line + "0";

      lines.add(line);

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
    return name;
  }


  /**
   * If called, the name of the user is changed to the newName parameter. The requested name is then
   * checked for duplicates.
   *
   * @param name    player to change the username.
   * @param newName what the name will be changed to.
   */
  public static void changeUsername(String name, String newName) {
    for (Connection player : playerList) {
      if (player.name.equalsIgnoreCase(name)) {
        player.name = newName;
        player.name = checkName(newName);
        player.output.println("/name " + player.name);
        player.output.flush();
      }
    }
    String msg = "changed username from ";
    msg = msg.concat(name);
    msg = msg.concat(" to ");
    msg = msg.concat(newName);
    sendNotification(msg);
  }

  /**
   * This method logs out a connected client, closing the connection.
   *
   * @param playerIndex number of player in the list.
   */

  public static void logout(int playerIndex) {
    String name = playerList.get(playerIndex).name;
    try {
      sendNotification(name + " has disconnected");
      playerList.get(playerIndex).output.println("/dced"); //disconnected
      playerList.get(playerIndex).output.flush();
      leaveLobby(name);
      playerList.get(playerIndex).playerSocket.close();
      playerList.remove(playerIndex);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Displays the ping of the player on request.
   *
   * @param name of the client
   */
  public static void displayPing(String name) {
    for (Connection player : playerList) {
      if (player.name.equalsIgnoreCase(name)) {
        player.output.println(
                getTimeMinutes() + "Server to Client ping " + player.latestPing + "ms");
        player.output.flush();
      }
    }
  }

  /**
   * Creates a list of all players online which is sent to the person causing the prompt.
   *
   * @param name name of person prompting the method.
   */

  public static void getAllPlayers(String name) {
    String list1 = "Players online: ";
    String list2 = "/list ";
    for (Connection player : playerList) {
      list1 = list1.concat(player.name + ", ");
      list2 = list2.concat(player.name + ", ");
    }
    for (Connection player : playerList) {
      if (player.name.equalsIgnoreCase(name)) {
        player.output.println(list1);
        player.output.flush();
        player.output.println(list2);
        player.output.flush();
        LOGGER.info(name + " wanted to know who is online: \n" + list1);
      }
    }
  }

  /**
   * Initializing the player list for all connected players.
   *
   * @param list list to initialize.
   */

  public static void setPlayerList(List<Connection> list) {
    playerList = list;
    LOGGER.info("playerList set to " + list);
  }

  /**
   * Initializing the lobby list for all lobbies.
   *
   * @param list list, that should be assigned.
   */
  public static void setLobbyList(List<Lobby> list) {
    lobbyList = list;
    LOGGER.info("lobbyList set to " + list);
  }

  /**
   * Shows highscore to the person wanting to see it.
   *
   * @param name to whom to show their highscore to.
   */
  public static void showWins(String name) {
    try {
      ArrayList<String> lines = Server.readHighscore();

      for (int i = 0; i < lines.size(); i++) {
        String[] temps = lines.get(i).split(" ");
        if (name.equalsIgnoreCase(temps[0])) {
          String text = lines.get(i).substring(60);
          for (Connection player : playerList) {
            if (player.name.equalsIgnoreCase(name)) {
              player.output.println("You have won " + text + " games!");
              player.output.flush();
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}