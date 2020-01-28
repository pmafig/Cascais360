package nsop.neds.mycascais.Entities;

public class PointEntity {

    private int idCouncil;
    private String council;

    private int id;
    private String title;
    private String town;
    private Double latitude;
    private Double longitude;
    private String address;

    public PointEntity(int id, String title, String town, String address, Double latitude, Double longitude){
        this.id = id;
        this.title = title;
        this.town = town;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int Id() { return this.id; }

    public String Title() { return this.title; }

    public String Town() { return this.town; }

    public String Address() { return this.address; }

    public Double Latitude() { return this.latitude; }

    public Double Longitude() { return this.longitude; }
}
