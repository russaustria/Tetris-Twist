package example;

import java.awt.Color;
import java.util.Random;
import java.awt.*;

public class Tetris {

  static class Shape {

    private Block pieceShape;
    private int[][] coords;

    public Shape() {
      coords = new int[4][2];
      setShape(Block.None);
    }

    public void setShape(Block shape) {
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 2; ++j) {
          coords[i][j] = shape.position[i][j];
        }
      }

      pieceShape = shape;
    }

    private void setX(int index, int x) {
      coords[index][0] = x;
    }

    private void setY(int index, int y) {
      coords[index][1] = y;
    }

    public int x(int index) {
      return coords[index][0];
    }

    public int y(int index) {
      return coords[index][1];
    }

    public Block getShape() {
      return pieceShape;
    }

    public void setRandomShape() {
      Random r = new Random();
      int x = Math.abs(r.nextInt()) % 7 + 1;
      Block[] values = Block.values();
      setShape(values[x]);
    }

    /* start */

    public void setRandomShape(boolean special) {
      Random r = new Random();
      int add = (int) (Math.random() * 2 + 1);
      Block[] values = Block.values();
      setShape(values[7 + add]);
    }

    /* end */

    public int minX() {
      int m = coords[0][0];

      for (int i = 0; i < 4; i++) {
        m = Math.min(m, coords[i][0]);
      }

      return m;
    }

    public int minY() {
      int m = coords[0][1];

      for (int i = 0; i < 4; i++) {
        m = Math.min(m, coords[i][1]);
      }

      return m;
    }

    public Shape rotateLeft() {
      if (pieceShape == Block.SquareShape)
        return this;

      Shape result = new Shape();
      result.pieceShape = pieceShape;

      for (int i = 0; i < 4; i++) {
        result.setX(i, y(i));
        result.setY(i, -x(i));
      }

      return result;
    }

    public Shape rotateRight() {
      if (pieceShape == Block.SquareShape)
        return this;

      Shape result = new Shape();
      result.pieceShape = pieceShape;

      for (int i = 0; i < 4; i++) {
        result.setX(i, -y(i));
        result.setY(i, x(i));
      }

      return result;
    }
  }

  static enum Block {
    None(new int[][] { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } }, new Color(0, 0, 0)),
    ZShape(new int[][] { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, new Color(204, 102, 102)),
    SShape(new int[][] { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }, new Color(102, 204, 102)),
    LineShape(new int[][] { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, new Color(102, 102, 204)),
    TShape(new int[][] { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } }, new Color(204, 204, 102)),
    SquareShape(new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, new Color(204, 102, 204)),
    LShape(new int[][] { { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, new Color(102, 204, 204)),
    MirroredLShape(new int[][] { { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, new Color(218, 170, 0)),
    /* start */
    Bomb(new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, new Color(255, 255, 255)),
    LineBomb(new int[][] { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, new Color(255, 255, 255));
    /* end */

    public int[][] position;
    public Color color;

    private Block(int[][] coords, Color c) {
      this.position = coords;
      color = c;
    }
  }

  static class TetrisBlock {

    private int[][] shape;
    private Color color;
    private int x, y;
    private int[][][] shapes;
    private int currentRotation;

    public TetrisBlock(int[][] shape, Color color) {

      this.shape = shape;
      this.color = color;

      initShapes();
    }

    private void initShapes() {
      shapes = new int[4][][];

      for (int i = 0; i < 4; i++) {
        int row = shape[0].length;
        int column = shape.length;

        shapes[i] = new int[row][column];

        for (int y = 0; y < row; y++) {
          for (int x = 0; x < column; x++) {
            shapes[i][y][x] = shape[column - x - 1][y];
          }
        }

        shape = shapes[i];
      }
    }

    public void spawn(int gridWidth) {

      currentRotation = 0;
      shape = shapes[currentRotation];

      y = -getHeight();
      x = (gridWidth = getWidth()) / 2;
    }

    public int[][] getShape() {
      return shape;
    }

    public Color getColor() {
      return color;
    }

    public int getHeight() {
      return shape.length;
    }

    public int getWidth() {
      return shape[0].length;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    public void moveDown() {
      y++;
    }

    public void moveLeft() {
      x--;
    }

    public void moveRight() {
      x++;
    }

    public void rotate() {
      currentRotation++;
      if (currentRotation > 3)
        currentRotation = 0;
      shape = shapes[currentRotation];
    }

    public int BottomEdge() {
      return y + getHeight();
    }

    public int LeftEdge() {
      return x;
    }

    public int RightEdge() {
      return x + getWidth();
    }
  }
}
