
package com.brahma.update;

import com.brahma.update.ThreadTask;
import com.brahma.update.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.Locale;
import org.json.JSONObject;

public class CheckingTask extends ThreadTask {
    private final static String TAG = "CheckingTask";

    public static final int ERROR_UNDISCOVERY_NEW_VERSION = 1;

    public static final int ERROR_NETWORK_UNAVAIBLE = 2;

    public static final int ERROR_UNKNOWN = 3;

    Context mContext;

    Handler mServiceHandler;

    public static String XML_DOWNLOAD_DIRECTORY = "/mnt/sdcard/Download";

    public static String XML_NAME = "ota_update.xml";
    
    private static final String COMMAND="update_with_inc_ota";

    private long mDownloadId;

    private static final int HANDLE_XML_DOWNLOAD_FINISH = 100;

    private static final int HANDLE_XML_DOWNLOAD_FAIL = 101;
    
    private static final int CHECK_TIMEOUT=10*1000;

    private String command;
    
    private String force;

    private String zipUrl;

    private String sha256;

    private String description = null;

    private String country = null;

    private DownloadManager mDownload;

    private UpdaterInfo mUpdaterInfo;

    private Preferences mPreferences;

    public CheckingTask(Context context, Handler handler) {
        mServiceHandler = handler;
        mContext = context;
        mUpdaterInfo = new UpdaterInfo(mContext);
        mPreferences = new Preferences(mContext);
    }

//    private void handleDownloadResult(int msg, Object obj) {
//        switch (msg) {
//            case HANDLE_XML_DOWNLOAD_FINISH:
//                if (Utils.DEBUG)
//                    Log.i(TAG, "xml " + obj.toString() + " download finish");
//                parserXml(XML_DOWNLOAD_DIRECTORY, XML_NAME);
//                mDownload.remove(mDownloadId);
//                mErrorCode = NO_ERROR;
//                break;
//            case HANDLE_XML_DOWNLOAD_FAIL:
//                mDownload.remove(mDownloadId);
//                if (Utils.DEBUG)
//                    Log.i(TAG, "xml download fail");
//                mErrorCode = ERROR_NETWORK_UNAVAIBLE;
//                break;
//        }
//    }
//
//    private void downloadXML(String url, String xmlName) {
//        if (Utils.DEBUG)
//            Log.i(TAG, "start download a xml file:" + xmlName);
//        Uri uri = Uri.parse(url);
//        Request request = new DownloadManager.Request(uri);
//        File file = new File(XML_DOWNLOAD_DIRECTORY + "/" + xmlName);
//        File dir = new File(XML_DOWNLOAD_DIRECTORY);
//        Log.i(TAG, "file:" + file.getAbsolutePath());
//        if (file.exists()) {
//            file.delete();
//        }
//        Log.i(TAG,
//                "dir:" + dir.getAbsolutePath() + " exixts:" + dir.exists() + " mkdir:"
//                        + dir.mkdirs());
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, xmlName);
//        request.setShowRunningNotification(false);
//        mDownloadId = mDownload.enqueue(request);
//        while (true) {
//            try {
//                Cursor c = mDownload.query(new DownloadManager.Query().setFilterById(mDownloadId));
//                if (c != null) {
//                    c.moveToFirst();
//                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                        handleDownloadResult(HANDLE_XML_DOWNLOAD_FINISH, xmlName);
//                        break;
//                    } else if (status == DownloadManager.STATUS_FAILED) {
//                        handleDownloadResult(HANDLE_XML_DOWNLOAD_FAIL, null);
//                        break;
//                    }
//                    c.close();
//                }
//                Thread.sleep(300);
//            } catch (InterruptedException e) {
//                mErrorCode = ERROR_UNKNOWN;
//                e.printStackTrace();
//            }
//        }
//    }

    protected void onRunning() {
        mDownload = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
        sendPost();
    }


//    private void parserXml(String xmlPath, String xmlName) {
//        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
//        DocumentBuilder domBuilder;
//        try {
//            domBuilder = domfac.newDocumentBuilder();
//            InputStream in = new FileInputStream(xmlPath + "/" + xmlName);
//            Document doc = domBuilder.parse(in);
//            Element root = doc.getDocumentElement();
//            NodeList nodelist_1 = root.getChildNodes();
//            if (nodelist_1 != null) {
//                for (int i = 0; i < nodelist_1.getLength(); i++) {
//                    Node node_1 = nodelist_1.item(i);
//                    if (node_1.getNodeName().equals("command")) {
//                        command = node_1.getAttributes().getNamedItem("name").getNodeValue();
//                        force = node_1.getAttributes().getNamedItem("force").getNodeValue();
//                        if (Utils.DEBUG){
//                        	 Log.i(TAG, "get xml command:" + command);
//                        	 Log.i(TAG, "get xml force:" + force);
//                        }
//                        if(!command.equals(COMMAND)){
//                        	mErrorCode = ERROR_UNDISCOVERY_NEW_VERSION;
//                            return;
//                        }
//                        NodeList nodelist_2 = node_1.getChildNodes();
//                        if (nodelist_2 != null) {
//                            for (int j = 0; j < nodelist_2.getLength(); j++) {
//                                Node node_2 = nodelist_2.item(j);
//                                if (node_2.getNodeName().equals("url")) {
//                                    zipUrl = node_2.getFirstChild().getNodeValue();
//                                    if (Utils.DEBUG)
//                                        Log.i(TAG, "get xml zipUrl:" + zipUrl);
//                                }
//                                if (node_2.getNodeName().equals("md5")) {
//                                    md5 = node_2.getFirstChild().getNodeValue();
//                                    if (Utils.DEBUG)
//                                        Log.i(TAG, "get xml md5:" + md5);
//                                }
//                                if (node_2.getNodeName().equals("description")) {
//                                    country = node_2.getAttributes().getNamedItem("country")
//                                            .getNodeValue();
//                                    if (description == null) {
//
//                                        //if (country.equals(mUpdaterInfo.country)
//                                        //        || country.equals("ELSE"))
//                                        //    description = node_2.getFirstChild().getNodeValue();
//
//                                    }
//                                }
//                            }
//                            if (Utils.DEBUG) {
//                                Log.i(TAG, "get xml description:" + description);
//                            }
//                        }
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            mErrorCode = ERROR_UNDISCOVERY_NEW_VERSION;
//            return;
//        }
//        String[] checkInfo = new String[4];
//        checkInfo[0] = md5;
//        checkInfo[1] = zipUrl;
//        checkInfo[2] = description;
//        checkInfo[3] = force;
//        mResult = checkInfo;
//    }

    private void sendPost() {
    	Log.v(TAG,"send post to server " + UpdaterInfo.postUrl);
        HttpPost post = new HttpPost(UpdaterInfo.postUrl);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ab_update", UpdaterInfo.ab_update));
        params.add(new BasicNameValuePair("updating_apk_version", UpdaterInfo.updating_apk_version));
        params.add(new BasicNameValuePair("brand", UpdaterInfo.brand));
        params.add(new BasicNameValuePair("device", UpdaterInfo.device));
        params.add(new BasicNameValuePair("android_version", UpdaterInfo.android_version));
        params.add(new BasicNameValuePair("brahmaos_version", UpdaterInfo.brahmaos_version));
        params.add(new BasicNameValuePair("language", Locale.getDefault().getLanguage()));
        params.add(new BasicNameValuePair("model", UpdaterInfo.model));
        params.add(new BasicNameValuePair("mac_addr", UpdaterInfo.mac_addr));
        params.add(new BasicNameValuePair("firmware_version", UpdaterInfo.firmware_version));
        params.add(new BasicNameValuePair("build_time", UpdaterInfo.build_time));
        params.add(new BasicNameValuePair("build_type", UpdaterInfo.build_type));
        params.add(new BasicNameValuePair("fingerprint", UpdaterInfo.fingerprint));
        params.add(new BasicNameValuePair("brahma_account", UpdaterInfo.brahma_account));
        
        Log.i(TAG, "params:  " + params);
        
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(httpParameters,CHECK_TIMEOUT);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(post);
            Log.i(TAG, "response status:  " + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String msg = EntityUtils.toString(entity);
                Log.i(TAG, "get data:  " + msg);
                JSONObject jsonresp = new JSONObject(msg);
                int respret = jsonresp.getInt("ret");
                if (respret == -1)
                    mErrorCode = ERROR_UNKNOWN;
                else if (respret == 0)
                    mErrorCode = ERROR_UNDISCOVERY_NEW_VERSION;
                else if (respret == 1){
                    String newversion = jsonresp.getJSONObject("newversion").getString("version");
                    String releaseinfo = jsonresp.getJSONObject("newversion").getString("releaseinfo");
                    sha256 = jsonresp.getJSONObject("updateinfo").getString("sha256");
                    zipUrl = jsonresp.getJSONObject("updateinfo").getString("url");
                    command = jsonresp.getJSONObject("updateinfo").getString("command");
                    Log.i(TAG, "Get newversion: " + newversion);
                    Log.i(TAG, "Release info: " + releaseinfo);
                    String[] checkInfo = new String[4];
                    checkInfo[0] = sha256;
                    checkInfo[1] = zipUrl;
                    checkInfo[2] = releaseinfo;
                    checkInfo[3] = command;
                    mResult = checkInfo;
                    mErrorCode = NO_ERROR;
                    // Shownewversion(newversion, release info)

                }

                /*(
                Log.i(TAG, "version: " + jsonresp.getJSONObject("newversion").getString("version"));
                Log.i(TAG, "get data:  " + msg);
                String url[] = msg.split("=");
                if (url.length==2&&url[0].equals("url")&&url[1].length()>10) {
                    if (Utils.DEBUG)Log.i(TAG, "xml url:" + url[1]);
                    url[1] = url[1].replace(" ", "");
                    url[1] = url[1].replace("\r\n", "");
                    downloadXML(url[1], XML_NAME);
                } else {
                    Log.i(TAG, "Can'n find new firmware");
                    mErrorCode = ;
                }*/

            }else{
            	mErrorCode = ERROR_UNKNOWN;
            }
        } catch (Exception e) {
            mErrorCode = ERROR_UNKNOWN;
            e.printStackTrace();
        }

    }

    protected void onStop() {
    	Log.v(TAG,"ErrorCode="+mErrorCode);
        if (mErrorCode == NO_ERROR) {
            NotificationManager notificationManager;
            Notification notification;
            Log.v(TAG, "Discover new version");
            mPreferences.setDownloadTarget(UpdateService.DOWNLOAD_OTA_PATH);
            String[] result = (String[]) mResult;
            mPreferences.setDownloadURL(result[1]);
            mPreferences.setSha256(result[0]);
            Intent intent= new Intent(mContext,DownloadActivity.class);
            intent.setAction(DownloadActivity.ACTION_DOWNLOAD);
            notificationManager = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notification = new Notification(R.drawable.ic_title,
                    mContext.getString(R.string.check_succeed), System.currentTimeMillis());
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, 0);
            notification.setLatestEventInfo(mContext, "Update",
                    mContext.getString(R.string.check_succeed), pi);
            notificationManager.notify(R.drawable.ic_title, notification);
            mPreferences.setPackageDescriptor(result[2]);
        }
    }
}
