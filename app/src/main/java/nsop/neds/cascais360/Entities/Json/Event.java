package nsop.neds.cascais360.Entities.Json;

import java.util.List;

public class Event {
    public int ID;
    public String CategoryTheme;
    public String WebURL;
    public String Title;
    public List<SubTitle> SubTitle;
    public String OnlineTicket;
    public List<Category> Category;

    public String CustomHours;
    public List<String> NextDates;
    public List<String> Images;

    public String Description;

    public List<Point> Points;

    public OfficeHours OfficeHours;

    public Price Price;

    public List<Tabs> Tabs;
    
}
