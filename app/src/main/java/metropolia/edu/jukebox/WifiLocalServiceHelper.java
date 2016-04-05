package metropolia.edu.jukebox;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

/**
 * Created by petri on 30.3.2016.
 *
 * Setup NSD Manager
 * Setup RegistrationListener
 * Register Service
 *
 * The Network Service Discovery Manager class provides the API to discover services on a network.
 * Enabling communication with other devices on the same local network
 *
 * Android 4.1 or higher
 */
public class WifiLocalServiceHelper {
    Context mContext;
    NsdManager mNsdManager;
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;
    public String mServiceName = "Server Device";
    public static final String TAG = "WifiLocalServiceHelper";
    public static final String SERVICE_TYPE = "_http._tcp.";

    NsdServiceInfo mServiceInfo;

    public WifiLocalServiceHelper(Context context){
        mContext = context;
        mNsdManager = (NsdManager)context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeNsd() {
        initializeResolveListener();
        initializeDiscoveryListener();
        initializeRegistrationListener();
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener(){

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service discovery success: " + serviceInfo);
                if(serviceInfo.getServiceType().equals(SERVICE_TYPE)){
                    Log.d(TAG, "Unknown Service Type: " + serviceInfo.getServiceType());
                }else if(serviceInfo.getServiceName().equals(mServiceName)){
                    Log.d(TAG, "Same machine: " + mServiceName);
                }else if(serviceInfo.getServiceName().contains(mServiceName)){
                    mNsdManager.resolveService(serviceInfo, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Service lost " + serviceInfo);
                if(mServiceInfo == serviceInfo){
                    mServiceInfo = null;
                }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error Code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener(){
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if(serviceInfo.getServiceName().equals(mServiceName)){
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mServiceInfo = serviceInfo;
            }
        };
    }

    /**
     * RegistrationListener interface contains callbacks used by Android to alert
     * your application of the success or failure of service registration and unregistration.
     */
    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener(){
            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                mServiceName = serviceInfo.getServiceName();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            }
        };
    }

    /**
     *  The name is visible to any device on the network that is using NSD to look for local services.
     *  If two devices on the network both have the NsdChat application installed,
     *  one of them changes the service name automatically.
     *  The service uses HTTP protocol running over TCP.
     *  NOTE: This method is asynchronous
     *
     * @param port
     */
    public void registerService(int port){
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener
        );
    }

    public void discoverServices(){
        try{
            mNsdManager.discoverServices(
                    SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener
            );
        }catch(Exception e){
            Log.d(TAG, "discoverServices failed.", e);
        }

    }

    public void stopDiscovery(){
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    public NsdServiceInfo getmServiceInfo(){
        return mServiceInfo;
    }

    public void tearDown(){
        mNsdManager.unregisterService(mRegistrationListener);
    }
}
