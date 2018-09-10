
package com.brahma.update;

import android.app.ActivityManagerNative;
import android.content.Context;
//import android.net.ethernet.EthernetNative;
import android.net.InterfaceConfiguration;
import android.os.Build;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.content.Context;

public class UpdaterInfo {
    /**
     * info include: 1. id --> ro.build.id 2. brand --> ro.product.brand 3.
     * device --> ro.product.device 4. board --> Build.BOARD 5. mac -->
     * EthernetNative.getEthHwaddr("eth0") 6. android open version -->
     * ro.build.version.release 7. build time --> Build.UTC 8. builder -->
     * ro.build.user 9. fingerprint --> Build.FINGERPRINT
     */
    public static final String UNKNOWN = "unknown";

    public static final String postUrl = Utils.SERVER_URL;

    public static String ab_update = "false";

    public static String updating_apk_version;// = "1";

    public static String brand;// = "brahma";

    public static String device;// = "G10";

    public static String android_version;

    public static String brahmaos_version;

    public static String model;

    public static String mac_addr;

    public static String firmware_version;

    public static String build_time;// = "20120301.092251";

    public static String build_type;// = "userdebug"

    public static String fingerprint;// = "brahmas/apollo_mele/G10:2.3.4/GRJ22/eng.ygwang.20120301.092251:eng/test-keys";

    public static String brahma_account;



    private Context mContext;

    public UpdaterInfo(Context mContext) {
        this.mContext = mContext;
        onInit();
    }

    /*
    private void getcountry() {
        try {
            country = ActivityManagerNative.getDefault().getConfiguration().locale.getCountry();
        } catch (RemoteException e) {

        }
    }

    */
    public static String makePostString() {
        return null;
    }

    private static String getString(String property) {
        return SystemProperties.get(property, UNKNOWN);
    }
    
    private String getMacAddr(){
        IBinder b = ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE);
        INetworkManagementService networkManagement = INetworkManagementService.Stub.asInterface(b);
        if(networkManagement != null){
            InterfaceConfiguration iconfig = null;
            try{
                iconfig= networkManagement.getInterfaceConfig("wlan0");
            }catch(Exception e){            	 
                e.printStackTrace();
            }finally{
                if(iconfig != null){
               	 	return iconfig.getHardwareAddress();
              	}else{
              		return "";
              	}
            }
        }else{
            return "";
        }
    }
    private String getVersionCode(){
	String packageName = mContext.getPackageName();
	int versionCode = 0;
	try{
	    versionCode = mContext.getPackageManager().getPackageInfo(
			packageName, 0).versionCode;
	}catch(Exception e){
            
	}
	return String.valueOf(versionCode);
    }

    private String getBrahma_account(){

        final UserManager um = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        int userId = UserHandle.myUserId();
        return um.getUserBrahmaAccount(userId);

    }

    private void onInit(){
    	//getcountry();
        ab_update = getString("ro.build.ab_update");
        updating_apk_version = getVersionCode();
        brand = getString("ro.product.brand");
    	device = getString("ro.product.device");
        android_version = getString("ro.build.version.release");
        brahmaos_version = getString("ro.build.id");
        model = getString("ro.product.model");
    	mac_addr = getMacAddr();
    	firmware_version = getString("ro.product.firmware");
    	build_time = getString("ro.build.date.utc");
    	build_type = getString("ro.build.type");
    	fingerprint=getString("ro.build.fingerprint");
    	brahma_account = getBrahma_account();
    }
}
