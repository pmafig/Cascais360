package nsop.neds.cascais360.Entities;

import java.net.URI;
import java.net.URISyntaxException;

public class WeatherEntity{

    private String current;
    private int min;
    private int max;
    private String info;
    private String icon;

    public WeatherEntity(String current, int min, int max, String info, String icon){
        this.current = current;
        this.min = min;
        this.max = max;
        this.info = info;
        this.icon = icon;
    }

    public String getCurrent(){
        return this.current;
    }

    public int getMin(){
        return this.min;
    }

    public int getMax(){
        return this.max;
    }

    public String getInfo(){
        return this.info;
    }

    public String getIcon() throws URISyntaxException {
        URI uri = new URI(this.icon);
        String path = uri.getPath();
        String idStr = path.substring( path.lastIndexOf('/')+1);

        String s = idStr.replace(".svg", "");

        return "ic_" + s;
    }
}
