package metropolia.edu.jukebox;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Created by petri on 30.3.2016.
 */
class QueueHost {
    private QueueConnection queueConnection;
    ServerSocket mServerSocket = null;
    Thread mThread = null;

    public QueueHost(QueueConnection queueConnection, Handler handler) {
        this.queueConnection = queueConnection;
        mThread = new Thread(new ServerTheard());
        mThread.start();
    }

    public void tearDown() {
        mThread.interrupt();
        try {
            mServerSocket.close();
        } catch (IOException ioe) {
            Log.e(QueueConnection.TAG, "Error when closing server socket.");
        }
    }

    private class ServerTheard implements Runnable {

        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(0);
                queueConnection.setLocalPort(mServerSocket.getLocalPort());

                while (!Thread.currentThread().isInterrupted()) {
                    Log.d(QueueConnection.TAG, "ServerSocket Created, awaiting connection");
                    queueConnection.setSocket(mServerSocket.accept());
                    if (queueConnection.mQueueClent == null) {
                        int port = queueConnection.mSocket.getPort();
                        InetAddress address = queueConnection.mSocket.getInetAddress();
                        queueConnection.connectToHost(address, port);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
