package client.graphics;

import java.util.ArrayList;

import javafx.scene.image.Image;

/**
 * This class loads textures necessary to render images onto the tiles.
 *
 * @author Nakarin Srijumrat
 * @author Patrice Delley
 * @version %G%
 */

public class Textures {
  private ArrayList<Image> textures = new ArrayList<>();

  /**
   * Adds various textures to a list using the provided path.
   *
   * <p>It is possible to use texture packs by replacing the
   * corresponding files.
   */

  public Textures() {
    textures.add(new Image("grass.png"));                      //0
    textures.add(new Image("road_asphalt.png"));               //1
    textures.add(new Image("road_asphalt_upEdge.png"));        //2
    textures.add(new Image("road_asphalt_botEdge.png"));       //3
    textures.add(new Image("road_asphalt_leftEdge.png"));      //4
    textures.add(new Image("road_asphalt_rightEdge.png"));     //5
    textures.add(new Image("road_asphalt_innerCurve1.png"));   //6
    textures.add(new Image("road_asphalt_outerCurve1.png"));   //7
    textures.add(new Image("road_asphalt_innerCurve2.png"));   //8
    textures.add(new Image("road_asphalt_outerCurve2.png"));   //9
    textures.add(new Image("road_asphalt_innerCurve3.png"));   //10
    textures.add(new Image("road_asphalt_outerCurve3.png"));   //11
    textures.add(new Image("road_asphalt_innerCurve4.png"));   //12
    textures.add(new Image("road_asphalt_outerCurve4.png"));   //13
    textures.add(new Image("road_asphalt_lineLeft.png"));      //14
    textures.add(new Image("road_asphalt_line.png"));          //15
    textures.add(new Image("road_asphalt_lineRight.png"));     //16
  }

  public Image getTexture(int index) {
    return textures.get(index);
  }
}