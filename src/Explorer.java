import java.awt.*;

public class Explorer {

    private Location loc;
    private int size;
    private Color colorSquare;
    private Color colorDot;
    private int direction;
    private int totalSteps = 0;

    public Explorer(Location loc, int direction, int size, Color colorSquare, Color colorDot){
        this.loc = loc;
        this.direction = direction;
        this.size = size;
        this.colorSquare = colorSquare;
        this.colorDot = colorDot;
    }

    public Color getColorSquare(){
        return colorSquare;
    }
    public Location getLoc(){
        return loc;
    }
    public int getDirection(){
        return direction;
    }
    public void move(int key, char[][] maze){

        int c=getLoc().getC();
        int r=getLoc().getR();
        if(key == 38){   //forward

            if (direction == 0) {  //up
                if(r>0 && (maze[r-1][c] == ' ' || maze[r-1][c] == 'p')){
                    getLoc().setR(-1);
                    totalSteps++;
                }
            }
            if(direction == 1) {   //right
                if(c<maze[0].length-1 && (maze[r][c+1] == ' ' || maze[r][c+1] == 'p')){
                    getLoc().setC(1);
                    totalSteps++;
                }
            }
            if(direction == 2){   // down
                if(r<maze.length-1 && (maze[r+1][c] ==' ' || maze[r+1][c] == 'p')){
                    getLoc().setR(1);
                    totalSteps++;
                }
            }
            if(direction == 3){   //left
                if(c>0 && (maze[r][c-1] == ' ' || maze[r][c-1] == 'p')){
                    getLoc().setC(-1);
                    totalSteps++;
                }
            }
        }

        if(key == 37){  //rotate left
            direction--;
            if(direction < 0){
                direction = 3;
            }
        }

        if(key == 39){    //rotate right
            direction++;
            if(direction>3){
                direction = 0;
            }
        }
    }

    public Rectangle getRect(){
        int r = getLoc().getR();
        int c = getLoc().getC();
        return new Rectangle(c*size+size, r*size+size, size, size);
    }

    public int getTotalSteps(){
        return totalSteps;
    }
}
