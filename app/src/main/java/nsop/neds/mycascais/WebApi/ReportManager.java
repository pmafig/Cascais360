package nsop.neds.mycascais.WebApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReportManager {
    /*public static void postFailure(Context context){
        popMessage(context,"Lamentamos, ocorreu um erro.\nPor favor tente mais tarde.");
    }

    public static void popMessage(Context context, String message){
        AlertDialog.Builder alertMessage = new AlertDialog.Builder(context, R.style.AlertMessageDialog);
        alertMessage.setMessage(message);
        alertMessage.show();
    }*/

    public static String getReportList(String json){
        String message = "";
        try {
            JSONObject data =  new JSONObject(json);
            JSONArray reportList = data.getJSONArray("ReportList");

            if(reportList.length() > 0) {
                for(int i = 0; i < reportList.length(); i++) {
                    message += reportList.getJSONObject(i).getString("Description") + "\n";
                }
            }
        } catch (JSONException e) {
            message = "";
        }finally {

            if(message == null || message.isEmpty()){
                message = "";
            }


            return message;
        }
    }

    public static String getSuccessReportList(String json){
        return getReportList(json, 0);
    }

    public static String getErrorReportList(String json){
        return getReportList(json, 1) + getReportList(json, 2) + getReportList(json, 3) + getReportList(json, 4);
    }

    public static String getReportList(String json, int type){
        String message = "";
        try {
            JSONObject data =  new JSONObject(json);
            JSONArray reportList = data.getJSONArray("ReportList");

                if(reportList.length() > 0) {
                for(int i = 0; i < reportList.length(); i++) {
                    String severity = reportList.getJSONObject(i).getString("Severity");

                    boolean a = severity != null;
                    boolean b = !severity.isEmpty();
                    boolean c =  Integer.parseInt(severity) == type;

                    try {
                        if (severity != null && !severity.isEmpty() && Integer.parseInt(severity) == type) {
                            message += reportList.getJSONObject(i).getString("Description") + "\n";
                        }
                    }catch (Exception ex){
                        message += "Erro desconhecido.";
                    }
                }
            }
        } catch (JSONException e) {
            message = "";
        }finally {
            return message;
        }
    }

    public static Boolean isAuthenticated(String json){
        Boolean authenticated = false;
        try {
            JSONObject data =  new JSONObject(json);

            JSONObject responseData = data.getJSONObject("ResponseData");
            authenticated = responseData.getBoolean("IsAuthenticated");

        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return authenticated;
        }
    }

    public static String getUserID(String json){
        return getVariable(json, "MyCascaisID");
    }

    public static boolean emailSent(String json){
        return Boolean.valueOf(getVariable(json, "EmailSent"));
    }

    public static boolean operationSuccess(String json){
        return Boolean.valueOf(getVariable(json, "OperationSuccess"));
    }

    public static String getSSk(String json){
        return getVariable(json, "SSK");
    }

    public static boolean IsValid(String json){
        return Boolean.valueOf(getVariable(json, "IsValid"));
    }

    public static boolean IsValidated(String json){
        return Boolean.valueOf(getVariable(json, "IsValidated"));
    }


    public static String getAppContact(String json) throws JSONException {
        JSONObject data = new JSONObject(json);
        JSONObject responseData = data.getJSONObject("ResponseData");
        JSONArray contacts =  responseData.getJSONArray("PhoneContacts");
        String phoneNumber = contacts.getJSONObject(0).getString("Number");
        return phoneNumber;
    }

    private static String getVariable(String json, String variable){
        String result = "";
        try {
            JSONObject data =  new JSONObject(json);

            JSONObject responsedata = data.getJSONObject("ResponseData");
            result = responsedata.getString(variable);

        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    public static Boolean isSessionOver(String json) {
        Boolean sessionOver = false;
        try {
            JSONObject data =  new JSONObject(json);

            JSONObject responseData = data.getJSONObject("ResponseData");
            JSONObject body = responseData.getJSONObject("Body");
            JSONObject logoutUserResponseInfo = body.getJSONObject("LogoutUserResponseInfo");

            sessionOver = logoutUserResponseInfo.getBoolean("SessionOver");

        } catch (JSONException e) {
            e.printStackTrace();
            sessionOver = true;
        }finally {
            return sessionOver;
        }
    }

    public static boolean success(String json){
        return Boolean.valueOf(getVariable(json, "Success"));
    }

    public static boolean smsSent(String json){
        return Boolean.valueOf(getVariable(json, "SmsSent"));
    }

    public static boolean asContacts(String json) {
        return Boolean.valueOf(getVariable(json, "FoundPhoneContacts"));
    }

    public static boolean mobileApp(String json) {
        return Boolean.valueOf(getVariable(json, "MobileApp"));
    }

    public static String getDisplayname(String json){
        return getVariable(json, "DisplayName");
    }

    public static String getDisplayvalidation(String json){ return getVariable(json, "DisplayValidation"); }

    public static String getFullName(String json){ return getDisclaimerVariable(json, "Nome"); }

    public static String getEmail(String json){ return getDisclaimerVariable(json, "E-mail"); }

    public static String getMobileNumber(String json){ return getDisclaimerVariable(json, "Contato"); }

    /*public static String getMobileNumber(String json){
        try{
            JSONObject data = new JSONObject(json);
            JSONObject responseData = data.getJSONObject("ResponseData");
            JSONArray contacts =  responseData.getJSONArray("PhoneContacts");
            if(contacts.length() > 0) {
                for(int i = 0; i < contacts.length(); i++) {

                    boolean isNumberApp = contacts.getJSONObject(i).getBoolean("SendSMS");

                    if(isNumberApp) {
                        return contacts.getJSONObject(i).getString("Number");
                    }
                }
                return "";
            }
        }catch (Exception ex){
            return "";
        }
        return "";
    }*/

    public static String getAddress(String json){ return getDisclaimerVariable(json, "Morada"); }

    /*public static String getAddress(String json){
        try{
            JSONObject data = new JSONObject(json);
            JSONObject responseData = data.getJSONObject("ResponseData");
            JSONArray contacts =  responseData.getJSONArray("PhoneContacts");
            if(contacts.length() > 0) {
                for(int i = 0; i < contacts.length(); i++) {

                    boolean isNumberApp = contacts.getJSONObject(i).getBoolean("SendSMS");

                    if(isNumberApp) {
                        return contacts.getJSONObject(i).getString("Address");
                    }
                }
                return "";
            }
        }catch (Exception ex){
            return "";
        }
        return "";
    }*/

    private static String getDisclaimerVariable(String json, String variable){
        try{
            JSONObject data = new JSONObject(json);
            JSONObject responseData = data.getJSONObject("ResponseData");
            JSONArray variables =  responseData.getJSONArray("FullDisclaimer");
            if(variables.length() > 0) {
                for(int i = 0; i < variables.length(); i++) {

                    String var = variables.getJSONObject(i).getString("Name");

                    if(var.equals(variable)) {
                        return variables.getJSONObject(i).getString("Description");
                    }
                }
                return "";
            }
        }catch (Exception ex){
            return "";
        }
        return "";
    }

    public static ArrayList<String> getPhoneContacts(String json) throws JSONException {
        JSONObject data = new JSONObject(json);
        JSONObject responseData = data.getJSONObject("ResponseData");
        JSONArray contacts =  responseData.getJSONArray("PhoneContacts");

        try{
            if(contacts.length() > 0) {
                ArrayList<String> list = new ArrayList<String>();
                for(int i = 0; i < contacts.length(); i++) {

                    String countryCode = contacts.getJSONObject(i).getString("CountryCode");
                    String number = contacts.getJSONObject(i).getString("Number");

                    list.add(countryCode + " " + number);
                }
                return list;
            }
        }catch (Exception ex){
            return null;
        }
        return null;
    }

    public static boolean invalidSession(String json){
        try {
            String invalidSession = getVariable(json, "InvalidSession");
            return invalidSession != null && !invalidSession.isEmpty() && Boolean.valueOf(invalidSession);
        }catch (Exception e){
            return false;
        }
    }

    public static String getRefreshToken(String json) {
        return getVariable(json, "RefreshToken");
    }

    public static boolean IsPasswordReset(String json) {
        return Boolean.valueOf(getVariable(json, "IsReset"));
    }

    public static boolean IsCreateUserValid(String json) {
        return Boolean.valueOf(getVariable(json, "IsValid"));
    }

    public static boolean IsSmsTokenValidation(String json) {
        try {
            String phone = getVariable(json, "PhoneNumber");

            String smsToken = getVariable(json, "SmsToken");

            return !phone.isEmpty() && !smsToken.isEmpty();
        }catch (Exception ex){
            return false;
        }
    }

    public static String getPhoneNumber(String json){

        return getVariable(json, "PhoneNumber");
    }

    public static Boolean getIsSuccess(String json){
        try {
            JSONObject data =  new JSONObject(json);
            return data.getBoolean("ResponseData");
        }catch (JSONException e){
            return false;
        }
    }

    public static String getDisclaimers(String json){
        try {
            JSONObject data =  new JSONObject(json);
            JSONObject responsedata = data.getJSONObject("ResponseData");
            return "{ Disclaimers: "+ responsedata.getJSONArray("Disclaimers").toString() + " }";
        }catch (JSONException e){
            return "";
        }
    }

    public static List<String> getFullDisclaimerValues(List<String> fields, String json){
        try {
            List<String> values = new ArrayList<String>();

            JSONObject data =  new JSONObject(json);
            JSONArray fulldisclaimer = data.getJSONArray("FullDisclaimer");

            if(fulldisclaimer.length() > 0 && fields.size() > 0){
                for(int i = 0; i < fields.size(); i++) {
                    String bid = fields.get(i).split("_")[0];
                    for(int v = 0; v < fulldisclaimer.length(); v++) {
                        String _bid = fulldisclaimer.getJSONObject(v).getString("BitwiseID");
                        if(_bid.equals(bid)){
                            values.add(fulldisclaimer.getJSONObject(v).getString("Description"));
                            break;
                        }
                    }
                }
            }

            return values;
        }catch (JSONException e){
            return null;
        }
    }

    public static String getFullDisclaimer(String json){
        try {
            JSONObject data =  new JSONObject(json);
            JSONObject responsedata = data.getJSONObject("ResponseData");
            return "{ FullDisclaimer: "+ responsedata.getJSONArray("FullDisclaimer").toString() + " }";
        }catch (JSONException e){
            return "";
        }
    }

    public static String getAppKeys(String json){
        try {
            JSONObject data =  new JSONObject(json);
            JSONObject responsedata = data.getJSONObject("ResponseData");
            return "{ AppList: "+ responsedata.getJSONArray("AppList").toString() + " }";
        }catch (JSONException e){
            return "";
        }
    }

    public static List<String> getDisclaimer(String json, int externalsiteid){
        try{

            List<String> fields = new ArrayList<String>();

            JSONObject data = new JSONObject(json);
            JSONArray disclaimers =  data.getJSONArray("Disclaimers");
            if(disclaimers.length() > 0) {
                for(int i = 0; i < disclaimers.length(); i++) {
                    int siteid = disclaimers.getJSONObject(i).getInt("SiteID");

                    if(siteid == externalsiteid) {
                        JSONArray disclaimerFields =  disclaimers.getJSONObject(i).getJSONArray("DisclaimerFields");
                        for(int f = 0; f < disclaimerFields.length(); f++) {
                            String id = disclaimerFields.getJSONObject(f).getString("BitwiseID");
                            String field = disclaimerFields.getJSONObject(f).getString("Name");
                            fields.add(id + "_" +field);
                        }
                        return fields;
                    }
                }
            }
        }catch (Exception ex){
            return null;
        }
        return null;
    }

    public static String getKey(String json, int externalsiteid){
        try{

            JSONObject data = new JSONObject(json);
            JSONObject responseData = data.getJSONObject("ResponseData");
            JSONArray appList =  responseData.getJSONArray("AppList");
            if(appList.length() > 0) {
                for(int i = 0; i < appList.length(); i++) {
                    int siteid = appList.getJSONObject(i).getInt("ID");

                    if(siteid == externalsiteid) {
                        String key =  appList.getJSONObject(i).getString("Key");
                        return key;
                    }
                }
            }
        }catch (Exception ex){
            return null;
        }
        return null;
    }
}
