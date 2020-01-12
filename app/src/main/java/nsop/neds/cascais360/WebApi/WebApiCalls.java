package nsop.neds.cascais360.WebApi;

import nsop.neds.cascais360.Encrypt.MessageEncryption;
import nsop.neds.cascais360.Settings.Settings;

public class WebApiCalls {

    public static String getDashBoard(){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"dashboard\", \"LangCode\":\""+ Settings.LangCode +"\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getDetail(int id, String ssk, String userId){
        String rt;

        if(ssk != "" && userId != ""){
            rt = new MessageEncryption().Encrypt("{ \"ID\":[" + id + "], \"Detail\":\"long\", \"ssk\":\""+ ssk +"\", \"userid\":\""+ userId +"\" }", WebApiClient.SITE_KEY);
        }else{
            rt = new MessageEncryption().Encrypt("{ \"ID\":[" + id + "], \"Detail\":\"long\"}", WebApiClient.SITE_KEY);
        }
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getResources(){
        String json = String.format("{\"ContentType\":\"resources\", \"LangCode\":\"%s\"}", Settings.LangCode);

        String rt = new MessageEncryption().Encrypt(json, WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getWeather(){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"weather\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getAgenda(){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"pageAgenda\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getVisit(){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"pageVisit\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getRoute(){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"pageRoutes\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getCategory(int id){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"categoryNodes\",\"Category\":"+ id +"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getSearch(String date){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"search\",\"DateEnd\":" + date + "}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getSearchByText(String data){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"search\",\"Title\":\"" + data + "\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getSearchByMap(){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"map\",\"Detail\":\"map\", \"CoordLat\": 38.7011317, \"CoordLong\": -9.4315817, \"Radius\": 5000.01 }", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String setNotification(String nid, String ssk, String userId){
        String rt = new MessageEncryption().Encrypt("{\"NID\":" + nid + ",\"ssk\":" + ssk + ",\"userid\":\"" + userId + "\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.setsubscription + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String setRefreshToken(String refreshToken, String ssk, String userId){
        String rt = new MessageEncryption().Encrypt("{\"RefreshToken\":" + refreshToken + ",\"ssk\":" + ssk + ",\"userid\":\"" + userId + "\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.RefreshLoginUser + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }
}
