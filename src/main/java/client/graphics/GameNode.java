package client.graphics;

import client.game.GameInfo;
import client.game.PlayerInfo;
import client.net.Client;
import client.net.Sender;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.apache.commons.lang3.ArrayUtils;

import server.game.Location;


/**
 * This class starts and displays the window, where the game and the chat interactions take place.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class GameNode extends Application {

  private static final int LEVEL_HEIGHT = 22;
  private static final int LEVEL_WIDTH = 28;
  public static final int TILE_SIZE = 40;
  public static List<RenderedTile> tiles = new ArrayList<>();
  public static TextArea chatWindow = new TextArea();
  public static AnchorPane world = new AnchorPane();
  public static PlayerEntity localPlayerEntity;
  public static MediaPlayer musicPlayer;
  public static MediaPlayer music2Player;
  public static MediaPlayer diedPlayer;
  public static Textures textures;
  public static boolean finished = false;
  public static boolean disqualified = false;
  public static boolean tutorialActive = true;
  public static Text tutorialText = new Text();
  public static int tutorialProgress = 0;
  public static MenuItem leaveLobby;
  static Text practiceText;
  static Button resetPractice;
  static Text gameOver = new Text("DISQUALIFIED");
  static boolean playerReady = false;
  static MenuItem joinLobby;
  static ToggleButton ready = new ToggleButton();
  /**
   * Required to render shapes as intended in the center of a tile.
   */

  public static double offset;

  public static ArrayList<PlayerEntity> currentGamePlayers = new ArrayList<>();

  /**
   * Starts methods needed for the display of the whole window.
   *
   * @param args input arguments for the window to be created.
   */

  public static void main(String[] args) {
    launch();
  }

  /**
   * This method defines the window properties and content and displays them.
   */

  public void start(Stage primaryStage) {
    primaryStage.setTitle("Vector Racer");
    primaryStage.setOnCloseRequest(event -> {
      event.consume();
      Sender.sendMessage("/lout");
    });
    primaryStage.setScene(new Scene(loadIntro(primaryStage)));
    primaryStage.toFront();
    primaryStage.show();
  }

  /**
   * Updates the location of the rendered car of the player independent of the game state.
   *
   * <p>By providing an old and a new tile as positions, the rendered car will be translated
   * to said new position. Also plays back an audio file of a car engine.
   *
   * @param oldTile describes the position of the rendered car before a move
   * @param newTile describes the position of the rendered car after a move
   * @param player  describes the "owner" of the car that is moved
   */

  public static void movePlayerEntity(
          RenderedTile oldTile, RenderedTile newTile, PlayerEntity player) {
    String carFile = GameNode.class.getClassLoader().getResource("car.wav").toString();
    Media car = new Media(carFile);
    MediaPlayer carPlayer = new MediaPlayer(car);

    carPlayer.play();

    player.setPosition(newTile.getXpos(), newTile.getYpos());

    RotateTransition rotation = new RotateTransition(new Duration(200));
    rotation.setFromAngle(player.currentAngle);

    double oldY = oldTile.getYpos();
    double newY = newTile.getYpos();
    double oldX = oldTile.getXpos();
    double newX = newTile.getXpos();

    boolean animAlreadySet = false;

    double newAngle;

    if (player.currentAngle == 0) { //from straight up
      if (oldX > newX) { //left
        if (newY == oldY) { //straight left
          rotation.setNode(player.getRenderedCar());
          rotation.setToAngle(-90);
          rotation.play();
          animAlreadySet = true;
          newAngle = 270;
        } else {
          rotation.setNode(player.getRenderedCar());
          newAngle = Math.toDegrees(Math.atan((oldY - newY) / (oldX - newX))) + 270;
          rotation.setToAngle(newAngle - 360);
          rotation.play();
          animAlreadySet = true;
        }
      }
    }

    if (oldX == newX) {
      if (newY > oldY) { //straight down
        newAngle = 180;
      } else { //straight up
        if (player.currentAngle > 180) {
          newAngle = 360;
        } else {
          newAngle = 0;
        }
      }
    } else if (oldX > newX) { //left
      if (newY == oldY) { //straight left
        newAngle = 270;
      } else if (newY > oldY) { //left-down
        newAngle = Math.toDegrees(Math.atan((oldX - newX) / (newY - oldY))) + 180;
      } else { //left up
        newAngle = Math.toDegrees(Math.atan((oldY - newY) / (oldX - newX))) + 270;
      }
    } else {
      newAngle = Math.toDegrees(Math.atan((oldY - newY) / (oldX - newX))) + 90;
    }


    rotation.setToAngle(newAngle);
    player.currentAngle = newAngle;
    rotation.setNode(player.getRenderedCar());
    if (!animAlreadySet) {
      rotation.play();
    }
    if (player.currentAngle == 360) {
      player.currentAngle = 0;
    }

    Translate translation = new Translate(
            (newTile.getXpos() - oldTile.getXpos()) * TILE_SIZE,
            (newTile.getYpos() - oldTile.getYpos()) * TILE_SIZE
    );

    player.getPrevVector().bind(oldTile, newTile);
    player.getNextVector().bindNext(newTile, translation);
    oldTile.playerOnTile = false;
    newTile.playerOnTile = true;

    if (tutorialActive) {
      tutorialProgress++;

      tutorialText.setY((localPlayerEntity.getYpos() + 2) * (TILE_SIZE));

      switch (tutorialProgress) {
        case 1: {
          tutorialText.setText("If a circle is red, the\ncorresponding tile\n"
                  + "is blocked by a player\nor terrain. (2/4)");
          break;
        }
        case 2: {
          tutorialText.setText("Location of the circles are\n"
                  + "determined by the spatial\ndifference between you\n"
                  + "and your latest move.\n(3/4)");
          break;
        }
        case 3: {
          tutorialText.setText("Disqualification is\ntriggered by having no\n"
                  + "green circle available to\nclick.\n\nIn practice mode,"
                  + "\nyou can restart by\npressing the reset button."
                  + "\n\nComplete the tutorial by\nresetting your position!\n(4/4)");
          tutorialActive = false;
          break;
        }
        default: {
          break;
          //something went wrong
        }
      }
    }
  }

  /**
   * Tiles are stored in a one-dimensional array, this method is used to
   * provide the index of a requested tile by
   * using the corresponding x and y coordinates.
   *
   * @param x the horizontal coordinate of the tile
   *          in relation to the upper left corner (increases rightward).
   * @param y the vertical coordinate of the tile
   *          in relation to the upper left corner (increases downward).
   * @return the index of the stored tile in the list of all tiles.
   */

  public static int getTileIndexOfCoordinate(int x, int y) {
    return y * LEVEL_WIDTH + x;
  }

  /**
   * Displays the legal moves of a player provided in a list.
   *
   * <p>By providing a point and a next vector, the method draws circles of all neighboring
   * tiles of the arrival point of the new vector. Depending on whether the tile is a legal move
   * the circle will be either green or red.
   *
   * <p>If no legal move is possible, the player will be notified by a text animation.
   *
   * @param moves                  list of legal and clickable tiles for a player.
   * @param nextVectorArrivalPoint the coordinates of the next vector.
   */

  public static void drawPossibleMoves(List<Location> moves, Location nextVectorArrivalPoint) {
    if (moves.isEmpty() && !disqualified) {
      musicPlayer.stop();

      gameOver.setFill(Color.RED);
      gameOver.setFont(Font.font("Garamond", 60));
      gameOver.setX((LEVEL_WIDTH * TILE_SIZE * 0.5) - 200);
      gameOver.setY(LEVEL_HEIGHT * TILE_SIZE * 0.5);
      try {
        world.getChildren().add(gameOver);
      } catch (IllegalArgumentException e) {
        //does not need to be caught, it is intended that it displays once
      }

      diedPlayer.play();
      disqualified = true;

      ScaleTransition translateSize = new ScaleTransition(Duration.millis(200));
      translateSize.setFromX(1.0f);
      translateSize.setToX(2.0f);
      translateSize.setFromY(1.0f);
      translateSize.setToY(2.0f);
      translateSize.setNode(gameOver);
      translateSize.play();


      if (!localPlayerEntity.getName().equalsIgnoreCase("practice")) {
        Sender.sendMessage("/dqed");
      }
    }
    offset = TILE_SIZE * 0.5;
    for (int x = -1; x < 2; x++) {
      for (int y = -1; y < 2; y++) {
        Circle circle = new Circle(((nextVectorArrivalPoint.positionX + x) * TILE_SIZE)
                + offset, ((nextVectorArrivalPoint.positionY + y) * TILE_SIZE) + offset, 5);
        circle.setMouseTransparent(true);
        circle.setStroke(Color.RED);
        circle.setFill(null);
        Location temp = new Location(
                nextVectorArrivalPoint.positionX + x, nextVectorArrivalPoint.positionY + y);
        for (Location location : moves) {
          if (location.toString().equalsIgnoreCase(temp.toString())) {
            GameNode.tiles.get(getTileIndexOfCoordinate(
                    location.getPositionX(), location.getPositionY())).setIsClickable(true);
            circle.setStroke(Color.GREEN);
          }
        }
        if (!finished) {
          world.getChildren().add(circle);
        }
      }
    }
  }

  /**
   * Displays incoming messages onto the TextArea. Plays a pop sound upon display.
   *
   * @param message content of the incoming message.
   */

  public static void receiveMessage(String message) {

    String chatSfxFile = GameNode.class.getClassLoader().getResource("pop.wav").toString();
    Media chatSfx = new Media(chatSfxFile);
    MediaPlayer chatSfxPlayer = new MediaPlayer(chatSfx);

    chatSfxPlayer.play();

    chatWindow.appendText(message + "\n");
  }

  /**
   * Displays possible moves in the first turn of a player.
   * Necessary, because the usual drawPossibleMoves is only
   * called after a move has been made. This is not the case
   * at the beginning of a game.
   *
   * @param entity player whose initial possible moves will be displayed.
   */

  public static void drawInitialPossibleMoves(PlayerEntity entity) {
    offset = TILE_SIZE * 0.5;
    for (int x = -1; x < 2; x++) {
      for (int y = -1; y < 2; y++) {
        Circle circle = new Circle(
                ((entity.getXpos() + x) * TILE_SIZE)
                        + offset, ((entity.getYpos() + y - 1) * TILE_SIZE) + offset, 5);
        circle.setMouseTransparent(true);
        circle.setFill(null);
        Location temp = new Location(entity.getXpos() + x, entity.getYpos() - 1 + y);
        tiles.get(
                getTileIndexOfCoordinate(entity.getXpos()
                        + x, entity.getYpos() - 1 + y)).setIsClickable(true);
        circle.setStroke(Color.GREEN);

        if (x == 0 && y == 1) {
          tiles.get(
                  getTileIndexOfCoordinate(localPlayerEntity.getXpos(),
                          localPlayerEntity.getYpos())).setIsClickable(false);
          circle.setStroke(Color.RED);
        }

        world.getChildren().add(circle);
      }
    }
  }

  /**
   * Graphically removes all drawn circles.
   * For some reason required to iterate over all children 4
   * times in order to achieve this.
   */
  public static void removeCircles() {
    for (int j = 0; j < 4; j++) {
      for (int i = 0; i < world.getChildren().size(); i++) {
        if (world.getChildren().get(i).toString().substring(0, 6).equalsIgnoreCase("circle")) {
          world.getChildren().remove(i);
        }
      }
    }
  }


  /**
   * Sets all tiles to non-legal moves.
   * Required to calculate next legal moves.
   */

  public static void resetClickableTiles() {
    for (RenderedTile tile : tiles) {
      tile.setIsClickable(false);
    }
  }

  /**
   * This method effectively creates the content.
   *
   * <p>First it sets the size of the world. Afterwards it defines the
   * elements and functions needed for things such as
   * the music and the chat. What follows next is the arrangement of
   * the individual elements created before.
   * In addition the textures are called and rendered and the position
   * of the player is constantly being kept updated
   * and eventually the world with all its content is being returned.
   *
   * @return the pane, that contains all Nodes necessary for the game.
   */

  private Parent createContent() {
    world.setPrefSize(LEVEL_WIDTH * TILE_SIZE + 304, LEVEL_HEIGHT * TILE_SIZE);

    String musicFile = getClass().getResource("/soundtrack.wav").toString();
    Media music = new Media(musicFile);
    musicPlayer = new MediaPlayer(music);

    String music2File = getClass().getResource("/soundtrack2.wav").toString();
    Media music2 = new Media(music2File);
    music2Player = new MediaPlayer(music2);

    String diedFile = getClass().getResource("/died.wav").toString();
    Media died = new Media(diedFile);
    diedPlayer = new MediaPlayer(died);

    practiceText = new Text("PRACTICE MODE");
    practiceText.setFill(Color.LIGHTGRAY);
    practiceText.setFont(Font.font("Bahnschrift", 24));
    practiceText.setX((LEVEL_WIDTH * TILE_SIZE * 0.05));
    practiceText.setY(LEVEL_HEIGHT * TILE_SIZE * 0.99);

    resetPractice = new Button("Reset");
    resetPractice.setLayoutX(LEVEL_WIDTH * TILE_SIZE * 0.25);
    resetPractice.setLayoutY(LEVEL_HEIGHT * TILE_SIZE * 0.965);
    resetPractice.setOnAction(event -> {
      practiceStop();
      practiceStart();
    });


    textures = new Textures();

    for (int y = 0; y < LEVEL_HEIGHT; y++) {
      for (int x = 0; x < LEVEL_WIDTH; x++) {
        tiles.add(new RenderedTile(x, y, "track.png", TILE_SIZE));
      }
    }

    for (RenderedTile tile : tiles) {
      tile.setTranslateX(tile.getXpos() * TILE_SIZE);
      tile.setTranslateY(tile.getYpos() * TILE_SIZE);
      world.getChildren().add(tile);
    }

    Image coneUp = new Image("cone_straight.png");
    ImageView coneUpView = new ImageView(coneUp);
    coneUpView.setFitWidth(TILE_SIZE - 15);
    coneUpView.setPreserveRatio(true);
    coneUpView.setX(TILE_SIZE * 6 + 5);
    coneUpView.setY(TILE_SIZE * 6 + 5);
    coneUpView.setRotate(-5);
    world.getChildren().add(coneUpView);

    ImageView coneUpView2 = new ImageView(coneUp);
    coneUpView2.setFitWidth(TILE_SIZE - 15);
    coneUpView2.setPreserveRatio(true);
    coneUpView2.setX(TILE_SIZE * 21 + 5);
    coneUpView2.setY(TILE_SIZE * 6 + 5);
    world.getChildren().add(coneUpView2);

    Image coneDown = new Image("cone_down.png");
    ImageView coneDownView = new ImageView(coneDown);
    coneDownView.setFitWidth(TILE_SIZE - 15);
    coneDownView.setPreserveRatio(true);
    coneDownView.setX(TILE_SIZE * 20 + 5);
    coneDownView.setY(TILE_SIZE * 6 + 5);
    coneDownView.setRotate(20);
    world.getChildren().add(coneDownView);

    Image bush = new Image("tree_small.png");
    ImageView bush1 = new ImageView(bush);
    bush1.setFitWidth(TILE_SIZE * 1.5);
    bush1.setPreserveRatio(true);
    bush1.setX(TILE_SIZE * 7);
    bush1.setY(TILE_SIZE * 14);
    bush1.setRotate(25);
    world.getChildren().add(bush1);

    ImageView bush2 = new ImageView(bush);
    bush2.setFitWidth(TILE_SIZE * 1.5);
    bush2.setPreserveRatio(true);
    bush2.setX(TILE_SIZE * 20);
    bush2.setY(TILE_SIZE * 13);
    world.getChildren().add(bush2);

    Image redBarrier = new Image("barrier_red_race.png");
    ImageView redBarrierView = new ImageView(redBarrier);
    redBarrierView.setFitWidth(4 * TILE_SIZE);
    redBarrierView.setPreserveRatio(true);
    redBarrierView.setX(TILE_SIZE * 10);
    redBarrierView.setY(TILE_SIZE * 15 - 15);
    world.getChildren().add(redBarrierView);

    Image whiteBarrier = new Image("barrier_white_race.png");
    ImageView whiteBarrierView = new ImageView(whiteBarrier);
    whiteBarrierView.setFitWidth(4 * TILE_SIZE);
    whiteBarrierView.setPreserveRatio(true);
    whiteBarrierView.setX(TILE_SIZE * 14 + 10);
    whiteBarrierView.setY(TILE_SIZE * 15 - 15);
    world.getChildren().add(whiteBarrierView);

    Image tireStack = new Image("tire_stack.png");
    ImageView tireStackView = new ImageView(tireStack);
    tireStackView.setX(TILE_SIZE * 13.25);
    tireStackView.setY(TILE_SIZE * 2);
    tireStackView.setFitWidth(TILE_SIZE * 1.5);
    tireStackView.setPreserveRatio(true);
    world.getChildren().add(tireStackView);

    Image lowerBoundaries = new Image("lower_boundaries.png");
    ImageView lowerBoundariesView = new ImageView(lowerBoundaries);
    lowerBoundariesView.setY(TILE_SIZE * 15.8);
    lowerBoundariesView.setFitWidth(TILE_SIZE * LEVEL_WIDTH);
    lowerBoundariesView.setPreserveRatio(true);
    world.getChildren().add(lowerBoundariesView);

    world.getChildren().add(createChat());

    practiceStart();

    return world;
  }

  /**
   * Draws sprite entities for all players in the lobby, thus starting the game.
   * The position of each sprite is determined by the index of the player in the
   * lobby-internal list of players. After that, the local player will be bound to
   * one of the drawn sprite entities - his/her controlled car.
   *
   * @param players array of names of the lobby members. Required for generating
   *                entity sprites.
   */

  public static void initializeGame(String[] players) {
    disqualified = false;
    leaveLobby.setDisable(true);
    practiceStop();
    music2Player.play();

    for (int i = 0; i < players.length; i++) {
      currentGamePlayers.add(i, new PlayerEntity(2 + i, 10, players[i]));
      world.getChildren().add(currentGamePlayers.get(i).getRenderedCar());
    }

    localPlayerEntity = currentGamePlayers.get(ArrayUtils.indexOf(players, Client.name));

    Client.localPlayerInfo = new PlayerInfo(
            localPlayerEntity.getName(),
            localPlayerEntity.getXpos(),
            localPlayerEntity.getYpos()
    );
    Client.game = new GameInfo();
    Client.game.addPlayer(Client.localPlayerInfo);

    for (int i = 0; i < players.length; i++) {
      if (!Client.name.equalsIgnoreCase(currentGamePlayers.get(i).getName())) {
        Client.game.addPlayer(new PlayerInfo(
                currentGamePlayers.get(i).getName(),
                currentGamePlayers.get(i).getXpos(),
                currentGamePlayers.get(i).getYpos()
        ));
      }
    }

    drawInitialPossibleMoves(localPlayerEntity);
    world.getChildren().addAll(localPlayerEntity.getNextVector(),
            localPlayerEntity.getPrevVector());
  }

  /**
   * This resets the playingfield.
   */
  public static void clearField() {
    for (PlayerEntity entity : currentGamePlayers) {
      tiles.get(getTileIndexOfCoordinate(entity.getXpos(), entity.getYpos())).playerOnTile = false;
      world.getChildren().remove(entity.getRenderedCar());
    }

    world.getChildren().removeAll(
            localPlayerEntity.getRenderedCar(),
            localPlayerEntity.getNextVector(),
            localPlayerEntity.getPrevVector()
    );

    currentGamePlayers = new ArrayList<>();
    localPlayerEntity = null;
    Client.localPlayerInfo = null;
    Client.game = null;
    PlayerEntity.counter = 0;

    resetClickableTiles();
    removeCircles();
    practiceStart();
  }

  /**
   * This method updates the position of entities and recalculates legal moves
   * for the local player.
   *
   * @param moveInfo contains information about the player who made a move
   *                 and his/her new location.
   */

  public static void updateNode(String[] moveInfo) {
    String name = moveInfo[0];
    int x = Integer.parseInt(moveInfo[1]);
    int y = Integer.parseInt(moveInfo[2]);

    for (PlayerEntity playerEntity : currentGamePlayers) {
      if (name.equalsIgnoreCase(playerEntity.getName())) {
        movePlayerEntity(tiles.get(
                getTileIndexOfCoordinate(playerEntity.getXpos(), playerEntity.getYpos())),
                tiles.get(getTileIndexOfCoordinate(x, y)), playerEntity);
        Client.localPlayerInfo.checkMoves();
      }
    }
  }

  /**
   * Starts practice mode with a single car.
   */

  public static void practiceStart() {
    music2Player.stop();
    finished = false;
    musicPlayer.play();
    disqualified = false;
    localPlayerEntity = new PlayerEntity(2, 10, "practice");

    Client.localPlayerInfo = new PlayerInfo(
            localPlayerEntity.getName(),
            localPlayerEntity.getXpos(),
            localPlayerEntity.getYpos()
    );

    world.getChildren().addAll(localPlayerEntity.getRenderedCar(),
            localPlayerEntity.getNextVector(),
            localPlayerEntity.getPrevVector(),
            practiceText,
            resetPractice
    );

    drawInitialPossibleMoves(localPlayerEntity);

    if (tutorialActive) {
      tutorialText.setText("Click on a green circle to\n"
              + "move your car to the\ncorresponding tile. (1/4)");
      tutorialText.setFill(Color.ROYALBLUE);
      tutorialText.setFont(Font.font("Bahnschrift", 16));
      tutorialText.setY((localPlayerEntity.getYpos() + 2) * (TILE_SIZE));
      tutorialText.setX(localPlayerEntity.getXpos() * TILE_SIZE - 30);

      world.getChildren().add(tutorialText);
    }
  }

  /**
   * Stops practice mode by clearing the screen.
   */

  public static void practiceStop() {
    tutorialActive = false;

    world.getChildren().removeAll(
            localPlayerEntity.getRenderedCar(),
            localPlayerEntity.getNextVector(),
            localPlayerEntity.getPrevVector(),
            practiceText,
            resetPractice,
            gameOver,
            tutorialText
    );

    tiles.get(getTileIndexOfCoordinate(localPlayerEntity.getXpos(),
            localPlayerEntity.getYpos())).playerOnTile = false;

    localPlayerEntity = null;
    Client.localPlayerInfo = null;
    PlayerEntity.counter = 0;
    removeCircles();
    resetClickableTiles();

    musicPlayer.stop();
    diedPlayer.stop();
  }

  /**
   * This method creates a BorderPane layout that is needed for the menu and chat.
   *
   * @return returns a child containing chat and menubar.
   */
  private Pane createChat() {

    TextField input = new TextField();
    input.setPromptText("Type your message here...");
    input.setOnAction(event -> {
      Sender.sendMessage(input.getText());
      input.clear();
    });

    chatWindow.setEditable(false);
    chatWindow.setPrefHeight(825); //height of chat window

    VBox chat = new VBox(15, chatWindow, input); //space between boxes
    chat.setPrefSize(300, 840); //height of right side


    HBox button = new HBox(50);
    button.setPadding(new Insets(15, 5, 10, 5));

    ready = new ToggleButton();
    ready.setPrefWidth(300);
    ready.setStyle("-fx-background-color: linear-gradient(#ff5400, #be1d00);"
            + "    -fx-background-radius: 30;"
            + "    -fx-background-insets: 0;"
            + "    -fx-text-fill: white;");
    ready.setText("I Want To Be Ready!");
    ready.setOnAction(event -> {
      if (Client.inLobby && localPlayerEntity.getName().equalsIgnoreCase("practice")) {
        if (playerReady) {
          ready.setStyle("-fx-background-color: linear-gradient(#ff5400, #be1d00);"
                  + "    -fx-background-radius: 30;"
                  + "    -fx-background-insets: 0;"
                  + "    -fx-text-fill: white;");
          ready.setText("I Want To Be Ready!");
          playerReady = false;
          Sender.sendMessage("/unrd");
        } else {
          ready.setStyle("-fx-background-color: linear-gradient(#00FF00, #228B22);"
                  + "    -fx-background-radius: 30;"
                  + "    -fx-background-insets: 0;"
                  + "    -fx-text-fill: white;");
          ready.setText("Unready Me!");
          playerReady = true;
          Sender.sendMessage("/read");
        }
      }
    });

    button.getChildren().addAll(ready);


    //Lobby menu items
    MenuItem showLobbies = new MenuItem("Show Lobbies");
    showLobbies.setOnAction(event -> Sender.sendMessage("/loli"));

    joinLobby = new MenuItem("Join Lobby...");
    joinLobby.setOnAction(event -> {
      Sender.sendMessage("/loli");
      try {
        Thread.sleep(300);
      } catch (Exception e) {
        //does not need to be caught
      }
      String answer = LobbyAlertBox.display("Lobbies", "Choose one of these Lobbies");
      if (!answer.equalsIgnoreCase("/listempty")) {
        Sender.sendMessage("/join " + answer);
      }
    });
    joinLobby.setDisable(false);

    leaveLobby = new MenuItem("Leave Lobby");
    leaveLobby.setOnAction(event -> Sender.sendMessage("/leav"));
    leaveLobby.setDisable(true);


    //General menu items
    MenuItem showPlayers = new MenuItem("Show Online Players");
    showPlayers.setOnAction(event -> Sender.sendMessage("/list"));

    MenuItem directMessage = new MenuItem("Send Direct Message...");
    directMessage.setOnAction(event -> {
      Sender.sendMessage("/list");
      String answer = DirectMsgAlertBox.display("Players", "Choose a player to send DM to");
      if (!answer.equalsIgnoreCase("/listempty")) {
        Sender.sendMessage("/dmsg " + answer);
      }
    });

    MenuItem winscore = new MenuItem("Wins");
    winscore.setOnAction(event -> Sender.sendMessage("/wins"));

    MenuItem help = new MenuItem("Help");
    help.setOnAction(event -> Sender.sendMessage("/help"));

    MenuItem exit = new MenuItem("Exit Program");
    exit.setOnAction(event -> Sender.sendMessage("/lout"));


    //Lobby menu
    Menu lobbyMenu = new Menu("Lobby");
    Menu gameMenu = new Menu("Game");
    Menu generalMenu = new Menu("General");


    //Adding items to generalMenu
    generalMenu.getItems().add(help);
    generalMenu.getItems().add(new SeparatorMenuItem());
    generalMenu.getItems().add(exit);


    //Adding items to lobbyMenu
    lobbyMenu.getItems().add(showLobbies);
    lobbyMenu.getItems().add(joinLobby);
    lobbyMenu.getItems().add(new SeparatorMenuItem());
    lobbyMenu.getItems().add(leaveLobby);

    //Adding items to Game
    gameMenu.getItems().add(showPlayers);
    gameMenu.getItems().add(directMessage);
    gameMenu.getItems().add(new SeparatorMenuItem());
    gameMenu.getItems().add(winscore);


    //Main menu bar
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(lobbyMenu, gameMenu, generalMenu);

    BorderPane borderPane = new BorderPane(); //complete right side
    borderPane.setCenter(chat);
    borderPane.setBottom(button);
    borderPane.setTop(menuBar);
    borderPane.setPrefSize(300, 880);

    borderPane.setTranslateX(LEVEL_WIDTH * TILE_SIZE + 2);

    return borderPane;
  }

  /**
   * The method is enabling and disabling MenuItems.
   */

  public static void setReady() {
    if (Client.inLobby) {
      leaveLobby.setDisable(false);
      joinLobby.setDisable(true);
    } else {
      leaveLobby.setDisable(true);
      joinLobby.setDisable(false);
    }
  }

  /**
   *  Resets the state of the readyButton.
   */

  public static void resetReadyButton() {
    ready.setStyle("-fx-background-color: linear-gradient(#ff5400, #be1d00);"
            + "    -fx-background-radius: 30;"
            + "    -fx-background-insets: 0;"
            + "    -fx-text-fill: white;");
    ready.setText("I Want To Be Ready!");
    playerReady = false;
  }

  /**
   * This methods loads the intro.
   *
   * @param primaryStage The main window.
   * @return the pane that the logo and button.
   */
  private BorderPane loadIntro(Stage primaryStage) {
    Button skipButton = new Button("Skip Intro");
    skipButton.setOnAction(e -> {
      primaryStage.setScene(new Scene(createContent()));
      primaryStage.toFront();
      primaryStage.show();
    });

    String logo = getClass().getResource("/logo.png").toString();
    Image img = new Image(logo);
    ImageView imgView = new ImageView(img);

    FadeTransition ft = new FadeTransition(Duration.millis(2000), imgView);
    ft.setFromValue(0.0);
    ft.setToValue(1.0);
    ft.setCycleCount(5);
    ft.setAutoReverse(true);

    HBox bottomBox = new HBox();
    HBox centerBox = new HBox();

    bottomBox.getChildren().addAll(skipButton);

    centerBox.getChildren().addAll(imgView);

    ft.play();

    BorderPane pane = new BorderPane();
    pane.setPadding(new Insets(20, 20, 20, 20));
    pane.setBottom(bottomBox);
    pane.setCenter(centerBox);

    return pane;
  }
}