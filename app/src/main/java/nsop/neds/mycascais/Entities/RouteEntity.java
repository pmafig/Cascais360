package nsop.neds.mycascais.Entities;

public class RouteEntity extends Base {
    private String distance;
    private String duration;
    private String difficulty;
    private String price;
    private String points;


    public RouteEntity(){

    }

    //region GETTER
    public String Distance(){ return this.distance; }

    public String Duration(){ return this.duration; }

    public String Difficulty(){ return this.difficulty; }

    public String Price() { return this.price; }

    public String Points() { return this.points; }
    //endregion

    //region SETTER
    public void SetDistance(String distance){
        this.distance = distance;
    }

    public void SetDuration(String duration){
        this.duration = duration;
    }

    public void SetDifficulty(String difficulty){
        this.difficulty = difficulty;
    }

    public void SetPrice(String price){
        this.price = price;
    }

    public void SetPoints(String points){
        this.points = points;
    }
    //endregion
}
