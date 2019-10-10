package server.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * This class stores information on progress
 * for each player on the track.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Progress {
  String name;
  int lapCondition;
  int lapCounter = 0;
  boolean checkpoint1 = false;
  boolean checkpoint2 = false;
  boolean checkpoint3 = false;
  boolean raceFinished = false;

  /**
   * Creates a progress object with the name of the
   * racer/player and required laps to finish the race.
   *
   * @param name name of the player.
   * @param raceLength required laps to finish the race.
   */

  public Progress(String name, int raceLength) {
    this.name = name;
    lapCondition = raceLength;
  }

  /**
   * Checks for every move, whether a checkpoint has been passed or not.
   * If all three checkpoints are passed and the player lands in the finish
   * zone, the lap will be counted as finished.
   *
   * @param x new x-position.
   * @param y new y-position.
   */

  void updateProgress(int x, int y) {
    if (!checkpoint1 && x > 9 && x < 18 && y > 7 && y < 13) {
      checkpoint1 = true;
    } else if (checkpoint1 && !checkpoint2 && x > 21 && x < 27 &&  y < 7) {
      checkpoint2 = true;
    } else if (checkpoint2 && !checkpoint3 && x > 9 && x < 18 &&  y > 15 && y < 21) {
      checkpoint3 = true;
    } else if (checkpoint3 && x < 6 &&  y > 3 && y < 10) {
      checkpoint1 = false;
      checkpoint2 = false;
      checkpoint3 = false;
      lapCounter++;

      if (lapCounter == lapCondition) {
        raceFinished = true;
      }
    }
  }
}
