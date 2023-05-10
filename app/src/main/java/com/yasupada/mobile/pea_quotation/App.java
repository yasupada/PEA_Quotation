package com.yasupada.mobile.pea_quotation;

public class App {
        static public String PROCESS_ACTION="process_action";
        static public String QR_LOGIN ="qr_login";

    static private App object;
    public String resultQRCode = "";
    public static final String KEEP_USERNAME = "keep_username";
    public static final String KEEP_PASSWORD = "keep_password";
    static public final String DATA_PROVIDER_path = "com.projectth.mobile.securityarea";
    public static final String MY_PREFS = "projectth_scm";
    String SERVER_URI = "https://scm.yasupada.com/index.php/";
    public String USER_LOGIN =  "authen";
    public String CUST_LOGIN =  "cust_login.php";

    public String Id = "";
    public String Name = "-";
    public String Surname = "-";
    public String LicenseKey = "";
    public String BusinessId = "";
    public String HeaderPage = "";
    public String FootPage = "";
    public String Shift = "";


    static public App getInstance(){
        if(object == null){
            object = new App();
        }

        return object;
    }
}
