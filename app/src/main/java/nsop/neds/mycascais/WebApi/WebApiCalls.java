package nsop.neds.mycascais.WebApi;

import nsop.neds.mycascais.Encrypt.MessageEncryption;
import nsop.neds.mycascais.Settings.Settings;

public class WebApiCalls {

    public static String getDashBoard(){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"dashboard\", \"LangCode\":\""+ Settings.LangCode +"\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getDetail(int id, String ssk, String userId){
        String rt;

        if(ssk != "" && userId != ""){
            rt = new MessageEncryption().Encrypt("{ \"ID\":[" + id + "], \"Detail\":\"long\", \"ssk\":\""+ ssk +"\", \"userid\":\""+ userId +"\", \"LangCode\":\"" + Settings.LangCode + "\" }", WebApiClient.SITE_KEY);
        }else{
            rt = new MessageEncryption().Encrypt("{ \"ID\":[" + id + "], \"Detail\":\"long\", \"LangCode\":\"" + Settings.LangCode + "\"}", WebApiClient.SITE_KEY);
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
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"pageAgenda\", \"LangCode\":\"" + Settings.LangCode + "\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getVisit(){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"pageVisit\", \"LangCode\":\"" + Settings.LangCode + "\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getRoute(){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"pageRoutes\", \"LangCode\":\"" + Settings.LangCode + "\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getCategory(int id){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"categoryNodes\",\"Category\":"+ id +", \"LangCode\":\"" + Settings.LangCode + "\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getSearch(String date){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"search\",\"DateEnd\":" + date + ", \"LangCode\":\"" + Settings.LangCode + "\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getSearchByText(String data){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"search\",\"Title\":\"" + data + "\", \"LangCode\":\"" + Settings.LangCode + "\"}", WebApiClient.SITE_KEY);
        return WebApiClient.BASE_URL + "/" + WebApiClient.API.cms + "/" + WebApiClient.METHODS.content + "/" + WebApiClient.SITE_ID + "?rt=" + rt;
    }

    public static String getSearchByMap(String lat, String lng){
        String rt = new MessageEncryption().Encrypt("{\"ContentType\":\"map\",\"Detail\":\"map\", \"LangCode\":\"" + Settings.LangCode + "\", \"CoordLat\": "+ lat +", \"CoordLong\": "+ lng +", \"Radius\": 2500.00 }", WebApiClient.SITE_KEY);
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
