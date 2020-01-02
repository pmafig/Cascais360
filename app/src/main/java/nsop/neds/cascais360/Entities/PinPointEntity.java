package nsop.neds.cascais360.Entities;

import android.graphics.Bitmap;

public class PinPointEntity extends Base {

    private int index;

    private int nid;
    private String title;
    private Bitmap image;

    private Double longitude;
    private Double latitude;

    private String audio;


    public PinPointEntity(int index, int nid, String title, Double longitude, Double latitude){
        this.index = index;
        this.nid = nid;
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public PinPointEntity(int index, int nid, String title, String imageUrl, Double longitude, Double latitude){
        this.index = index;
        this.nid = nid;
        this.title = title;
        this.image = decodeImage(imageUrl);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int Index() { return this.index; }

    public int Nid() { return this.nid; }

    public String Title(){
        return title;
    }

    public Bitmap Image(){
        return image;
    }

    public Double Logitude() { return longitude;}

    public Double Latitude(){
        return latitude;
    }

    public String Audio(){
        return audio;
    }

    public String setAudio(String audio){
        return this.audio = audio;
    }
}
