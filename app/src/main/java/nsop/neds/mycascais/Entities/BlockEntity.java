package nsop.neds.mycascais.Entities;

import android.graphics.Bitmap;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BlockEntity extends Base {

    private int type;
    private int weigth;
    private int nid;
    private String title;
    private String subTitle;
    private String description;
    private Bitmap image;
    private Date date;
    private List<FrameEntity> frameList;
    private List<EventEntity> eventList;
    private List<CategoryEntity> categoryList;

    public BlockEntity(){
        this.frameList = new ArrayList<>();
    }

    public BlockEntity(int type, int weight, int nid, String title, String subTitle, String imageUrl){
        this.type = type;
        this.weigth = weight;
        this.nid = nid;
        this.title = title;
        this.subTitle = subTitle;
        this.image = decodeImage(imageUrl);
    }

    public BlockEntity(int type, int weight, int nid, String title, String subTitle, String imageUrl, Date date){
        this.type = type;
        this.weigth = weight;
        this.nid = nid;
        this.title = title;
        this.subTitle = subTitle;
        this.image = decodeImage(imageUrl);
        this.date = date;
    }

    public BlockEntity(int type, int weight, String title){
        this.type = type;
        this.weigth = weight;
        this.title = title;
        this.frameList = new ArrayList<>();
        this.eventList = new ArrayList<>();
    }

    public int Type() {
        return this.type;
    }

    public int Weight(){
        return weigth;
    }

    public Date Date() { return date; }

    public Bitmap Image(){
        return image;
    }

    public int Nid(){
        return nid;
    }

    public String Title(){
        return title;
    }

    public String Description(){
        return description;
    }

    public String SubTitle(){
        return subTitle;
    }

    public String Month() {
        Format formatter = new SimpleDateFormat("MMM",  new Locale("pt","PT"));
        String d = formatter.format(new Date()).toUpperCase();
        return d;
    }

    public String Day() {
        Format formatter = new SimpleDateFormat("dd",  new Locale("pt","PT"));
        String d = formatter.format(new Date()).toUpperCase();
        return d;
    }

    public List<FrameEntity> FrameList(){ return this.frameList; }

    public List<EventEntity> EventList(){ return this.eventList; }

    public List<CategoryEntity> CategoryList(){
        if(this.categoryList == null){
            this.categoryList = new ArrayList<>();
        }
        return this.categoryList;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDate(String date){
        try {
            this.date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            this.date = null;
        }
    }

    public void setSubTitle(String subTitle){
        this.subTitle = subTitle;
    }

    public void setDescription(String description){
        this.description = description;
    }

}
