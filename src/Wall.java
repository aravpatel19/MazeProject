import java.awt.*;
import java.util.Collection;

public class Wall {

    private int[] row;
    private int[] col;
    private Color color;
    private GradientPaint paint;
    private String type;
    private int size;
    private Polygon polygon;


    public Wall(int[] row, int[] col, int r, int g, int b, String type, int size){
        this.row = row;
        this.col = col;
        //this.color = color;

        this.type = type;
        this.size = size;

        this.paint = new GradientPaint(col[1], row[1], new Color(r-size, g-size, b-size), col[0], row[0], new Color(r, g, b));
        this.polygon = new Polygon(col, row, row.length);
    }

    public int[] getRow(){
        return row;
    }
    public int[] getCol(){
        return col;
    }

   /* public Color getColor(){
        return color;
    }*/

    public String getType(){
        return type;
    }
    public int getSize(){
        return size;
    }
    public Polygon getPolygon(){

        return polygon;
    }

    public GradientPaint getPaint(){
        return paint;
    }
}
