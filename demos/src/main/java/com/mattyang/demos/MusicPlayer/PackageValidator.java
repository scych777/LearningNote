package com.mattyang.demos.MusicPlayer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.os.Process;
import android.util.Base64;

import com.mattyang.demos.MusicPlayer.utils.LogHelper;
import com.mattyang.demos.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PackageValidator {
    private static final String TAG = LogHelper.makeLogTag(PackageValidator.class);
    private final Map<String,ArrayList<CallerInfo>> mValidCertificates;

    public PackageValidator(Context context) {
        mValidCertificates = readValidCertificates(context.getResources().getXml(R.xml.allowed_media_browser_callers));
    }

    private Map<String,ArrayList<CallerInfo>> readValidCertificates(XmlResourceParser parser){
        HashMap<String,ArrayList<CallerInfo>> validCertificates = new HashMap<>();
        try {
            int eventType = parser.next();
            while (eventType != XmlResourceParser.END_DOCUMENT){
                if(eventType == XmlResourceParser.START_TAG && parser.getName().equals("signing_certificate")){
                    String name = parser.getAttributeValue(null,"name");
                    String packageName = parser.getAttributeValue(null,"package");
                    boolean isRelease = parser.getAttributeBooleanValue(null,"release",false);
                    String certificate = parser.nextText().replaceAll("\\s|\\n","");
                    CallerInfo info = new CallerInfo(name,packageName,isRelease);
                    ArrayList<CallerInfo> infos = validCertificates.get(certificate);
                    if(infos == null){
                        infos = new ArrayList<>();
                        validCertificates.put(certificate,infos);
                    }
                    LogHelper.v(TAG,"Adding allowed caller: ",info.name," package=",info.packageName," release=",info.release," certificate=",certificate);
                    infos.add(info);
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            LogHelper.e(TAG, e, "Could not read allowed callers from XML.");
        }
        return validCertificates;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCallerAllowed(Context context, String callingPackage, int callingUid){
        if(Process.SYSTEM_UID == callingUid || Process.myUid() == callingUid){
            return true;
        }
        if(isPlatformSigned(context, callingPackage)){
            return true;
        }
        PackageInfo packageInfo = getPackageInfo(context,callingPackage);
        if(packageInfo == null){
            return false;
        }
        if(packageInfo.signatures.length != 1){
            return false;
        }
        String signature = Base64.encodeToString(packageInfo.signatures[0].toByteArray(),Base64.NO_WRAP);
        ArrayList<CallerInfo> validCallers = mValidCertificates.get(signature);
        if(validCallers == null){
            LogHelper.v(TAG, "Signature for caller ", callingPackage, " is not valid: \n"
                    , signature);
            if(mValidCertificates.isEmpty()){
                LogHelper.w(TAG, "The list of valid certificates is empty. Either your file ",
                        "res/xml/allowed_media_browser_callers.xml is empty or there was an error ",
                        "while reading it. Check previous log messages.");
            }
            return false;
        }
        StringBuffer expectedPackages = new StringBuffer();
        for (CallerInfo info : validCallers){
            if(callingPackage.equals(info.packageName)){
                return true;
            }
            expectedPackages.append(info.packageName).append(' ');
        }
        LogHelper.i(TAG, "Caller has a valid certificate, but its package doesn't match any ",
                "expected package for the given certificate. Caller's package is ", callingPackage,
                ". Expected packages as defined in res/xml/allowed_media_browser_callers.xml are (",
                expectedPackages, "). This caller's certificate is: \n", signature);
        return false;
    }

    private boolean isPlatformSigned(Context context, String pkgName){
        PackageInfo platformPackageInfo = getPackageInfo(context,"android");
        if(platformPackageInfo == null || platformPackageInfo.signatures == null || platformPackageInfo.signatures.length == 0){
            return false;
        }
        PackageInfo clientPackageInfo = getPackageInfo(context,pkgName);
        return (clientPackageInfo != null && clientPackageInfo.signatures != null && clientPackageInfo.signatures.length >0
                && platformPackageInfo.signatures[0].equals(clientPackageInfo.signatures[0]));
    }

    private PackageInfo getPackageInfo(Context context, String pkgName){
        final PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(pkgName,PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            LogHelper.w(TAG, e, "Package manager can't find package: ", pkgName);
        }
        return null;
    }


    private final static class CallerInfo{
        final String name;
        final String packageName;
        final boolean release;

        public CallerInfo(String name, String packageName, boolean release) {
            this.name = name;
            this.packageName = packageName;
            this.release = release;
        }
    }
}
