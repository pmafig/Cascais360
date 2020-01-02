package nsop.neds.cascais360.Entities;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class ResourceEntity {
    private JSONArray jcategories;
    private Map<String, String> categories;
    private JSONArray jplaces;
    private Map<String, String> places;
    private JSONArray jroutes;
    private Map<String, String> routes;
    private JSONArray jtowncouncil;
    private Map<String, String> townCouncils;

    private String crc;

    public ResourceEntity(){

    }

    public String CRC(){
        return crc;
    }

    public void setCRC(String _crc){
        crc = _crc;
    }
    public Map<String, String> Categories(){

        if(this.categories == null){
            this.categories = new HashMap<String, String>();
        }

        return this.categories;
    }

    public Map<String, String> Places(){

        if(this.places == null){
            this.places = new HashMap<String, String>();
        }

        return this.places;
    }

    public Map<String, String> Routes(){

        if(this.routes == null){
            this.routes = new HashMap<String, String>();
        }

        return this.routes;
    }

    public Map<String, String> TownCouncil(){

        if(this.townCouncils == null){
            this.townCouncils = new HashMap<String, String>();
        }

        return this.townCouncils;
    }

    public void setJcategories(JSONArray categories){
        this.jcategories = categories;
    }

    public JSONArray getJcategories(){
        return this.jcategories;
    }

    public void setJplaces(JSONArray places){
        this.jplaces = places;
    }

    public JSONArray getJplaces(){
        return this.jplaces;
    }

    public void setJroutes(JSONArray routes){
        this.jroutes = routes;
    }

    public JSONArray getJroutes(){
        return this.jroutes;
    }

    public void setJtowncouncil(JSONArray towncouncil){
        this.jtowncouncil = towncouncil;
    }

    public JSONArray getJtouwncouncil(){
        return this.getJtouwncouncil();
    }


}
