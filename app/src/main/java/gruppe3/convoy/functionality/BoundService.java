package gruppe3.convoy.functionality;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Christian on 14-01-2016.
 */
public class BoundService extends Service{
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        BoundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BoundService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("onBind");
        return mBinder;
    }

    public void gem(final Object obj, final String filnavn) {
        final String fileName = this.getFilesDir() + "/"+filnavn+".ser";
        new Thread(new Runnable() {
            public void run() {
                try {
                    FileOutputStream datastream = new FileOutputStream(fileName);
                    ObjectOutputStream objektstream = new ObjectOutputStream(datastream);
                    objektstream.writeObject(obj);
                    objektstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Object hent(String filnavn) {
        final String fileName = this.getFilesDir() + "/"+filnavn+".ser";
        try {
            FileInputStream datastream = new FileInputStream(fileName);
            ObjectInputStream objektstream = new ObjectInputStream(datastream);
            Object obj = objektstream.readObject();
            objektstream.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
