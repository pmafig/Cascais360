package nsop.neds.mycascais.Entities;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class EventDetailEntity extends Base {
    private int nid;
    private String categoryTheme;
    private String title;
    private String category;
    private Bitmap image;
    private String displayDate;
    private String displayLocation;
    private String description;
    private String onlineTicket;
    private String price;
    private double priceValue;
    private List<String> dates;
    private List<PointEntity> points;
    private List<String> tabs;

    private RouteEntity routeEntity;

    private String shareLink;

    private boolean like;
    private boolean notification;

    private boolean isEvent;
    private boolean isPlace;
    private boolean isRoute;

    public EventDetailEntity(){

    }

    //region GETTER
    public int Nid(){ return this.nid; }

    public String CategoryTheme(){ return this.categoryTheme; }

    public String Title(){ return this.title; }

    public String Category() { return this.category; }

    public Bitmap Image() { return this.image; }

    public String DisplayDate(){ return this.displayDate; }

    public String DisplayLocation(){ return this.displayLocation; }

    public String Description() { return this.description; }

    public String OnlineTicket() { return "https://www.bol.pt/Projecto/EntidadesAderentes/3155-casa_santa_maria"; }//this.onlineTicket; }

    public String Price() { return this.price; }

    public double PriceValue(){ return this.priceValue; }

    public String ShareLink() { return this.shareLink; }

    public boolean Like() { return this.like; }

    public boolean Notification() { return this.notification; }

    public List<PointEntity> Points() {
        if(this.points == null){
            this.points = new ArrayList<>();
        }
        return this.points;
    }

    public List<String> Tabs(){
        if(this.tabs == null){
            this.tabs = new ArrayList<>();
        }
        return this.tabs;
    }

    public boolean IsEvent(){
        return this.isEvent;
    }

    public boolean IsPlace(){
        return this.isPlace;
    }

    public boolean IsRoute(){
        return this.isRoute;
    }

    public RouteEntity GetRoute(){ return this.routeEntity; }
    //endregion

    //region SETTER
    public void SetNid(int nid){
        this.nid= nid;
    }

    public void SetCategoryTheme(String categoryTheme){
        this.categoryTheme = categoryTheme;
    }

    public void SetTitle(String title){
        this.title = title;
    }

    public void SetCategory(String category){
        this.category = category;
    }

    public void SetDisplayDate(String displayDate){
        this.displayDate = displayDate;
    }

    public void SetDisplayLocation(String displayLocation){
        this.displayLocation = displayLocation;
    }



    public List<String> Dates(){
        if(this.dates == null){
            this.dates = new ArrayList<>();
        }
        return this.dates;
    }

    public void SetImage(String image){
        this.image = decodeImage(image);
    }

    public void SetDescription(String description){
        this.description = description;
    }

    public void SetOnlineTicket(String onlineTicket){
        this.onlineTicket = onlineTicket;
    }

    public void SetPrice(String price){
        this.price = price;
    }

    public void SetPriceValue(double price){ this.priceValue = price; }

    public void SetShareLink(String shareLink){
        this.shareLink = shareLink;
    }

    public void SetLike(boolean like){ this.like = like; }

    public void SetNotification(boolean notification){ this.notification = notification; }

    public void SetEvent(){
        ClearObjectType();
        this.isEvent = true;
    }

    public void SetPlace(){
        ClearObjectType();
        this.isPlace = true;
    }

    public void SetRoute(){
        ClearObjectType();
        this.isRoute = true;
        this.routeEntity = new RouteEntity();
    }

    private void ClearObjectType(){
        this.isEvent = false;
        this.isPlace = false;
        this.isRoute = false;
    }
    //endregion
}
