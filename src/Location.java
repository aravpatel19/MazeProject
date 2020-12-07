public class Location {

    private int r;
    private int c;
    public Location(int r, int c){
        this.r = r;
        this.c = c;
    }

    public void setC(int x){
        c+=x;
    }
    public void setR(int y){
        r+=y;
    }
    public int getC(){
        return c;
    }
    public int getR(){
        return r;
    }
}
