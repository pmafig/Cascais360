package nsop.neds.cascais360.Entities;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class FrameEntity extends Base{
    private int nid;
    private String title;
    private String subTitle;
    private Bitmap image;
    private String displayDate;
    private String legend;
    private List<String> categories;

    private String local;
    private int date;

    private int difficulty;
    private double distance;

    private String difficulty_label;
    private String distance_label;

    private int price;

    public FrameEntity(int nid, String title, String imageUrl, String date){
        this.nid = nid;
        this.title = title;
        this.image = decodeImage(imageUrl);
        this.displayDate = date;
    }

    public FrameEntity(int nid, String title, String subTitle){
        this.nid = nid;
        this.title = title;
        this.subTitle = subTitle;
    }

    public FrameEntity(String imageUrl, String legend){
        this.image = decodeImage(imageUrl);
        this.legend= legend;
    }

    public FrameEntity(String title, String date, boolean themeBlock){
        this.title = title;
        this.displayDate= date;
    }

    public int Nid() { return this.nid; }

    public Bitmap Image(){
        return image;
    }

    public String Title(){
        return title;
    }

    public String DisplayDate(){
        return displayDate;
    }

    public String Legend(){
        return legend;
    }

    public String Local() { return local;}

    public void setDate(int _date){
        date = _date;
    }

    public int Date() { return date;}

    public int Difficulty() { return difficulty;}

    public void setDifficulty(int _difficulty){
        difficulty = _difficulty;
    }

    public String DifficultyLabel() { return difficulty_label;}

    public void setDifficultyLabel(String _difficulty_label){
        difficulty_label = _difficulty_label;
    }

    public double Distance() { return distance;}

    public void setDistance(double _distance){
        distance = _distance;
    }

    public String DistanceLabel() { return distance_label;}

    public void setDistanceLabel(String _distance_label){
        distance_label = _distance_label;
    }

    public int Price() { return price; }

    public void setPrice(int _price){
        price = _price;
    }

    public List<String> Categories(){
        if(this.categories == null){
            this.categories = new ArrayList<>();
        }
        return this.categories;
    }
}
