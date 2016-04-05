package metropolia.edu.jukebox;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


/**
 * Created by petri on 30.3.2016.
 */
public class QueueConnection {

    private Handler mUpdateHandler;
    protected QueueHost mQueueHost;
    protected QueueClient mQueueClent;
    protected Socket mSocket;

    protected static final String TAG = "QueueConnection";
    private int mPort = -1;

    public QueueConnection(Handler handler){
        mUpdateHandler = handler;
        mQueueHost = new QueueHost(this, handler);
    }

    public void tearDown(){
        mQueueHost.tearDown();
        mQueueClent.tearDown();
    }

    public void connectToHost(InetAddress address, int port){
        mQueueClent = new QueueClient(this, address, port);
    }

    public void sendQueueList(String msg){
        if(mQueueClent != null){
            mQueueClent.sendQueueList(msg);
        }
    }

    public int getLocalPort(){
        return mPort;
    }

    public void setLocalPort(int port){
        mPort = port;
    }

    public synchronized void updateQueueList(String msg, boolean local){
        Log.e(TAG, "Updating queuelist: " + msg);

        if (local) {
            msg = "me: " + msg;
        }else{
            msg = "them: " + msg;
        }

        Bundle messageBundle = new Bundle();
        messageBundle.putString("msg", msg);

        Message message = new Message();
        message.setData(messageBundle);
        mUpdateHandler.sendMessage(message);
    }

    protected synchronized void setSocket(Socket socket){
        Log.d(TAG, "setSocket being called.");
        if(socket == null){
            Log.d(TAG, "Setting a null socket.");
        }
        if(mSocket != null){
            if(mSocket.isConnected()){
                try{
                    mSocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        mSocket = socket;
    }

    protected Socket getSocket(){
        return mSocket;
    }

}
