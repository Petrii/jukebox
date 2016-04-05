package metropolia.edu.jukebox.queue;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by petri on 30.3.2016.
 */
public class QueueClient {
    private QueueConnection queueConnection;
    private InetAddress mAddress;
    private int PORT;

    private final String CLIENT_TAG = "QueueClient";

    private Thread mSendThread;
    private Thread mRecThread;

    public QueueClient(QueueConnection queueConnection, InetAddress address, int port) {
        this.queueConnection = queueConnection;
        Log.d(CLIENT_TAG, "Creating QueueClient");
        mAddress = address;
        PORT = port;

        mSendThread = new Thread(new SendingThread());
        mSendThread.start();
    }

    class SendingThread implements Runnable {

        BlockingQueue<String> mMessageQueue;
        private int QUEUE_CAPACITY = 10;

        public SendingThread() {
            mMessageQueue = new ArrayBlockingQueue<String>(QUEUE_CAPACITY);
        }

        @Override
        public void run() {
            try {
                if (queueConnection.getSocket() == null) {
                    queueConnection.setSocket(new Socket(mAddress, PORT));
                    Log.d(CLIENT_TAG, "Client-side socket initialized.");
                } else {
                    Log.d(CLIENT_TAG, "Socket already initialized. skipping!");
                }

                mRecThread = new Thread(new ReceivingThread());
                mRecThread.start();

            } catch (UnknownHostException e) {
                Log.d(CLIENT_TAG, "Initializing socket failed, UHE", e);
            } catch (IOException e) {
                Log.d(CLIENT_TAG, "Initializing socket failed, IOE.", e);
            }

            while (true) {
                try {
                    String msg = mMessageQueue.take();
                    sendQueueList(msg);
                } catch (InterruptedException ie) {
                    Log.d(CLIENT_TAG, "Message sending loop interrupted, exiting");
                }
            }
        }
    }

    class ReceivingThread implements Runnable {

        @Override
        public void run() {

            BufferedReader input;
            try {
                input = new BufferedReader(new InputStreamReader(
                        queueConnection.mSocket.getInputStream()));
                while (!Thread.currentThread().isInterrupted()) {

                    String messageStr = null;
                    messageStr = input.readLine();
                    if (messageStr != null) {
                        Log.d(CLIENT_TAG, "Read from the stream: " + messageStr);
                        queueConnection.updateQueueList(messageStr, false);
                    } else {
                        Log.d(CLIENT_TAG, "The nulls! The nulls!");
                        break;
                    }
                }
                input.close();

            } catch (IOException e) {
                Log.e(CLIENT_TAG, "Server loop error: ", e);
            }
        }
    }

    public void tearDown() {
        try {
            queueConnection.getSocket().close();
        } catch (IOException ioe) {
            Log.e(CLIENT_TAG, "Error when closing server socket.");
        }
    }

    public void sendQueueList(String msg) {
        try {
            Socket socket = queueConnection.getSocket();
            if (socket == null) {
                Log.d(CLIENT_TAG, "Socket is null, wtf?");
            } else if (socket.getOutputStream() == null) {
                Log.d(CLIENT_TAG, "Socket output stream is null, wtf?");
            }

            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(queueConnection.getSocket().getOutputStream())), true);
            out.println(msg);
            out.flush();
            queueConnection.updateQueueList(msg, true);
        } catch (UnknownHostException e) {
            Log.d(CLIENT_TAG, "Unknown Host", e);
        } catch (IOException e) {
            Log.d(CLIENT_TAG, "I/O Exception", e);
        } catch (Exception e) {
            Log.d(CLIENT_TAG, "Error3", e);
        }
        Log.d(CLIENT_TAG, "Client sent message: " + msg);
    }
}
