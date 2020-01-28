package nsop.neds.mycascais.Entities;

import android.graphics.Bitmap;

public class CategoryNodeEntity extends Base {
    private int nid;
    private String title;
    private String subtitle;

    private Bitmap image;
    private String local;
    private int date;
    private int price;
    private int difficulty;
    private int distance;
    private nodeType type;

    enum nodeType{
        local,
        museu,
        distance
    }

    public CategoryNodeEntity(int nid, String title, String subtitle){
        this.nid = nid;
        this.title = title;
        this.subtitle = subtitle;
    }

    public CategoryNodeEntity(int nid, String title, String image, String subtitle, String local, int value, nodeType type){
        this.nid = nid;
        this.title = title;
        this.image = decodeImage(image);
        this.subtitle = subtitle;
        this.local = local;
        this.date = value;
        this.type = type;
    }

    public int Nid(){
        return this.nid;
    }

    public String Title(){
        return this.title;
    }

    public String SubTitle(){
        return  this.subtitle;
    }

    public Bitmap Image(){
        return image;
    }

    public String Local(){ return this.local; }

    public int Date() { return this.date; }

    public int Price() { return this.price; }

    public int Difficulty() { return this.difficulty; }

    public int Distance() { return this.distance; }
}

