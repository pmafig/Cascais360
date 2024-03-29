package nsop.neds.mycascais.WebApi;

public class WebApiMethods {
    public static final String LOGIN = "Login";
    public static final String LOGOUT = "Logout";
    public static final String CREATEACCOUNT = "CreateMobileTemporaryAuthentication";

    public static final String EMAILRECOVER = "EmailRecover";

    public static final String RECOVERACCOUNT = "MobileResetLoginUser";
    public static final String VALIDATESMSTOKEN = "ValidateSmsToken";
    public static final String VALIDATERECOVERSMSTOKEN = "CheckSmsToken";
    public static final String CHANGEPASSWORDSMSTOKEN = "ValidateMobileResetLoginUser";
    public static final String CHANGEPASSWORDEMAILTOKEN = "ValidateMobileVTResetLoginUser";

    public static final String RESETACCOUNTPASSWORDBYEMAIL = "ResetAccountPasswordByEmail";
    public static final String SENDSMSTOKEN = "SendSmsToken";

    public static final String RESETPASSWORD = "ValidateMobileVTResetLoginUser";

    public static final String VALIDATEMOBILETEMPORARYLOGINUSER = "ValidateMobileTemporaryLoginUser";
    public static final String VALIDATEEMAILTEMPORARYACCOUNT = "CheckValidationToken";


    public static final String UPDATECUSTOMERCONTACT = "UpdateCustomerContact";

    public static final String REFRESHLOGINUSER = "RefreshLoginUser";

    public static final String CHANGELOGINPASSWORD = "ChangeLoginPassword";

    public static final String SETSUBSCRIPTION= "setsubscription";
    public static final String SETLIKESTATUS = "setlike";

    public static final String SETDISCLAIMERRESPONSE = "SetDisclaimerResponse";

    public static final String SETCONSENT = "SetConsent";

    //region crm
    public static final String ADDCUSTOMEREMAIL = "AddCustomerEmail";
    public static final String ADDCUSTOMERPHONECONTACT = "AddCustomerPhoneContact";
    public static final String CHANGEENTITYVALIDATIONSTATE = "ChangeEntityValidationState";

    public static final String ADDCUSTOMERLOGIN = "AddCustomerEmail";

    public static final String UPDATESUBSCRIPTIONS= "UpdateSubscriptions";
    //endregion
}
