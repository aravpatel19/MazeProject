import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class MazeProject extends JPanel implements KeyListener {

    JFrame frame;
    int c=1;
    int r=1;
    int size = 15;
    int direction = 1;
    int factor = 50;
    int view = 5;
    int startFOV;
    int endFOV;

    ArrayList<Location> sprayLocations = new ArrayList<>();

    int sprayLimit = 10;

    boolean flashlight = false;
    boolean atStart = false;
    boolean useSpray = false;

    boolean atEnd = false;

    int totalSteps = 0;

    boolean draw3D = false;

    Explorer explorer;

    ArrayList<Wall> walls;


    char[][] maze = new char[20][70];

    public MazeProject(){

        explorer = new Explorer(new Location(r, c), direction, size, Color.RED, Color.GREEN);
        setBoard();
        frame = new JFrame("A-Mazing Program");
        frame.add(this);
        frame.setSize(1200, 700);

        frame.addKeyListener(this);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public void paintComponent (Graphics g){

        super.paintComponent(g); //giant eraser
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        if(!draw3D) {
            g2.setColor(Color.GRAY);
            for(int c=0; c<maze[0].length; c++){
                for(int r=0; r<maze.length; r++){
                    if(maze[r][c] == ' '){
                        g2.fillRect(c*size+size, r*size+size, size, size);
                    }
                    else{
                        g2.drawRect(c*size+size, r*size+size, size, size);
                    }
                    if(maze[r][c] == 'p'){
                        g2.setColor(Color.GREEN);
                        g2.fillRect(c*size+size, r*size+size, size, size);
                        g2.setColor(Color.GRAY);
                    }
                }
            }

            g2.setColor(explorer.getColorSquare());
            g2.fill(explorer.getRect());
        }
        else{
            createWalls();

            for(Wall w: walls){
                //g2.setColor(Color.WHITE);
                g2.setPaint(w.getPaint());
                g2.fillPolygon(w.getPolygon());
                g2.setColor(Color.BLACK);
                g2.draw(w.getPolygon());
            }

            int fontSize = 15;
            Font font = new Font("Arial", Font.BOLD, fontSize);
            g2.setFont(font);
            String on_off = "";
            if(flashlight)
                on_off = "On";
            else
                on_off = "Off";
            g2.drawString("Flashlight: "+on_off, 470, 20);
            g2.drawString("Press 'F' for Flashlight", 450, 40);
            g2.drawString("Total Steps: "+totalSteps, 200, 25);
            g2.drawString("Sprays: "+sprayLimit, 650, 20);
            g2.drawString("Press 'S' to Spray", 630, 40);

            if(atStart){
                for(int n=0;n<startFOV;n++){
                    g2.setFont(new Font("BANGERS", Font.CENTER_BASELINE, 80-startFOV*15));
                    g2.drawString("START", 400, 350);
                }
            }
            if(atEnd){
                for(int n=0;n<endFOV;n++){
                    g2.setFont(new Font("BANGERS", Font.CENTER_BASELINE, 40-endFOV*8));
                    g2.drawString("Great job!", 385, 335);
                    g2.drawString("You Finished the Maze!", 355, 375);
                }
            }
        }
    }
    public void createWalls(){

        walls = new ArrayList<>();

        for(int fov=0; fov<view; fov++){

            walls.add(getLeftPathway(fov));
            walls.add(getRightPathway(fov));
            walls.add(getFloor(fov));
            walls.add(getCeiling(fov));
        }
        int rr = explorer.getLoc().getR();
        int cc = explorer.getLoc().getC();
        direction = explorer.getDirection();

        switch (direction) {
            case 0: //up
                atStart = false;
                atEnd = false;

                for(int fov=0; fov<view; fov++){
                    try{

                        //spray floor
                        if(maze[rr-fov][cc] == 'p'){
                            walls.add(sprayFloor(fov));
                        }
                        //Left walls
                        if(maze[rr-fov][cc-1] == '#'){
                            walls.add(getLeft(fov));
                        }
                        //left triangle walls
                        else{
                            walls.add(getTopLeftTriangle(fov));
                            walls.add(getBotLeftTriangle(fov));
                        }
                        //right walls
                        if(maze[rr-fov][cc+1] == '#'){
                            walls.add(getRight(fov));
                        }
                        //right triangle walls
                        else{
                            walls.add(getTopRightTriangle(fov));
                            walls.add(getBotRightTriangle(fov));
                        }
                        //front wall
                        if(maze[rr-fov][cc] == '#'){
                            walls.add(getInFront(fov));
                            break;
                        }

                    }catch(ArrayIndexOutOfBoundsException e){
                    }
                }
                break;

            case 1:  //right
                atStart = false;
                atEnd = false;

                for(int fov=0; fov<view; fov++){
                    try{
                        //spray floor
                        if(maze[rr][cc+fov] == 'p'){
                            walls.add(sprayFloor(fov));
                        }
                        //left wall
                        if(maze[rr-1][cc+fov] == '#'){
                            walls.add(getLeft(fov));
                        }
                        //left triangle walls
                        else{
                            walls.add(getTopLeftTriangle(fov));
                            walls.add(getBotLeftTriangle(fov));
                        }
                        //right wall
                        if(maze[rr+1][cc+fov] == '#'){
                            walls.add(getRight(fov));
                        }
                        //right triangle walls
                        else{
                            walls.add(getTopRightTriangle(fov));
                            walls.add(getBotRightTriangle(fov));
                        }
                        //front walls
                        if(maze[rr][cc+fov] == '#'){
                            walls.add(getInFront(fov));
                            break;
                        }

                    }catch(ArrayIndexOutOfBoundsException e){
                    }
                }

                break;

            case 2:  //down
                atStart = false;
                for(int fov=0; fov<view; fov++){
                    try{
                        //Finish Maze
                        if(maze[rr+fov][cc] == 'e'){
                            atEnd = true;
                            endFOV = fov;
                        }

                        //spray floor
                        if(maze[rr+fov][cc] == 'p'){
                            walls.add(sprayFloor(fov));
                        }
                        //Left wall
                        if(maze[rr+fov][cc+1] == '#'){
                            walls.add(getLeft(fov));
                        }
                        //left triangle walls
                        else{
                            walls.add(getTopLeftTriangle(fov));
                            walls.add(getBotLeftTriangle(fov));
                        }
                        //right wall
                        if(maze[rr+fov][cc-1] == '#'){
                            walls.add(getRight(fov));
                        }
                        //right triangle walls
                        else{
                            walls.add(getTopRightTriangle(fov));
                            walls.add(getBotRightTriangle(fov));
                        }
                        //front walls
                        if(maze[rr+fov][cc] == '#'){
                            walls.add(getInFront(fov));
                            break;
                        }

                    }catch(ArrayIndexOutOfBoundsException e){
                    }
                }

                break;

            case 3:  //left
                atEnd = false;

                for(int fov=0; fov<view; fov++){
                    try{
                        //Start of maze
                        if(maze[rr][cc-fov] == 's') {
                            atStart = true;
                            startFOV = fov;
                        }

                        //spray floor
                        if(maze[rr][cc-fov] == 'p'){
                            walls.add(sprayFloor(fov));
                        }
                        //left walls
                        if(maze[rr+1][cc-fov] == '#'){
                            walls.add(getLeft(fov));
                        }
                        //left triangle walls
                        else {
                            walls.add(getTopLeftTriangle(fov));
                            walls.add(getBotLeftTriangle(fov));
                        }
                        //right wall
                        if(maze[rr-1][cc-fov] == '#'){
                            walls.add(getRight(fov));
                        }
                        //right triangle walls
                        else{
                            walls.add(getTopRightTriangle(fov));
                            walls.add(getBotRightTriangle(fov));
                        }
                        //front wall
                        if(maze[rr][cc-fov] == '#'){
                            walls.add(getInFront(fov));
                            break;
                        }

                    }catch(ArrayIndexOutOfBoundsException e){
                    }
                }
                break;
        }
    }

    //Left trapezoids
    // n=fov
    public Wall getLeft(int n){
        int num = factor*n;

        int[] rLocs = new int[]{num, 50+num, 650-num, 700-num};
        int[] cLocs = new int[]{100+num, 150+num, 150+num, 100+num};

        if(flashlight) {
            num = 30 * n;
        }
        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "Left", factor);
    }

    //rectangle
    // n = fov
    //Left side
    public Wall getLeftPathway(int n){
        int num = (factor*n);

        int[] rLocs = new int[]{50+num, 50+num, 650-num, 650-num};
        int[] cLocs = new int[]{100+num, 150+num, 150+num, 100+num};
        if(flashlight) {
            num = 30 * n;
        }
        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "LeftPath", factor);
    }

    //triangle
    public Wall getTopLeftTriangle(int n){
        int num = factor*n;

        int[] rLocs = new int[]{num, 50+num, 50+num};
        int[] cLocs = new int[]{100+num, 100+num, 150+num};
        if(flashlight) {
            num = 30 * n;
        }
        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "TopLeft", factor);
    }
    public Wall getBotLeftTriangle(int n){
        int num = factor*n;

        int[] rLocs = new int[]{700-num, 650-num, 650-num};
        int[] cLocs = new int[]{100+num, 100+num, 150+num};
        if(flashlight) {
            num = 30 * n;
        }
        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "BottomLeft", factor);
    }

    //Right Trapezoids       n = fov
    public Wall getRight(int n){
        int num = factor*n;

        int[] rLocs = new int[]{num, 50+num, 650-num, 700-num};
        int[] cLocs = new int[]{900-num, 850-num, 850-num, 900-num};

        if(flashlight) {
            num = 30 * n;
        }
        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "Right", factor);
    }

    //Right Rectangles    n = fov
    public Wall getRightPathway(int n){
        int num = factor*n;

        int[] rLocs = new int[]{50+num, 50+num, 650-num, 650-num};
        int[] cLocs = new int[]{900-num, 850-num, 850-num, 900-num};

        if(flashlight) {
            num = 30 * n;
        }
        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "RightPath", factor);
    }

    //triangles
    public Wall getTopRightTriangle(int n){
        int num = factor*n;

        int[] rLocs = new int[]{num, 50+num, 50+num};
        int[] cLocs = new int[]{900-num, 900-num, 850-num};

        if(flashlight) {
            num = 30 * n;
        }
        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "TopRight", factor);
    }
    public Wall getBotRightTriangle(int n){
        int num = factor*n;

        int[] rLocs = new int[]{700-num, 650-num, 650-num};
        int[] cLocs = new int[]{900-num, 900-num, 850-num};

        if(flashlight) {
            num = 30 * n;
        }
        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "BottomRight", factor);
    }


    public Wall getCeiling(int n){
        int num = factor*n;

        int[] rLocs = new int[]{num, 50+num, 50+num, num};
        int[] cLocs = new int[]{100+num, 150+num, 850-num, 900-num};

        if(flashlight) {
            num = 30 * n;
        }
        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "Ceiling", 20);
    }

    public Wall getFloor(int n){
        int num = factor*n;

        int[] rLocs = new int[]{700-num, 650-num, 650-num, 700-num};
        int[] cLocs = new int[]{100+num, 150+num, 850-num, 900-num};
        if(flashlight) {
            num = 30 * n;
        }

        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "Floor", factor);
    }

    public Wall sprayFloor(int n){
        int num = factor*n;

        int[] rLocs = new int[]{700-num, 650-num, 650-num, 700-num};
        int[] cLocs = new int[]{100+num, 150+num, 850-num, 900-num};
        if(flashlight) {
            num = 30 * n;
        }
        return new Wall(rLocs, cLocs, 255-num,50, 50, "Floor", factor);
    }

    public Wall getInFront(int n){
        int num = factor*n;

        int[] rLocs = new int[]{num, num, 700-num, 700-num};
        int[] cLocs = new int[]{100+num, 900-num, 900-num, 100+num};
        if(flashlight) {
            num = 30 * n;
        }

        return new Wall(rLocs, cLocs, 255-num, 255-num, 255-num, "Front", factor);
    }

    public void setBoard(){
        File file = new File("/Users/aravpatel/IntellijProjects/MazeProject/src/MazeFile.txt");

        try {
            BufferedReader input = new BufferedReader(new FileReader(file));
            String text;
            int r=0;

            while((text = input.readLine()) != null){
                for(int c=0;c<text.length(); c++){
                    maze[r][c] = text.charAt(c);
                }
                r++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(draw3D){
            createWalls();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        //sends arrow key
        explorer.move(e.getKeyCode(), maze);

        totalSteps = explorer.getTotalSteps();

        //spacebar
        if(e.getKeyCode() == 32){
            draw3D = !draw3D;
        }
        //F for flashlight
        if(e.getKeyCode() == 70){
            flashlight = !flashlight;
        }
        //S for spray
        if(e.getKeyCode() == 83){
            if(sprayLimit > 0) {
                int rLoc = explorer.getLoc().getR();
                int cLoc = explorer.getLoc().getC();
                maze[rLoc][cLoc] = 'p';
                sprayLimit--;
            }
        }
        if(flashlight)
            view = 6;
        else
            view = 5;

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[]args){
        MazeProject app = new MazeProject();
    }

}
