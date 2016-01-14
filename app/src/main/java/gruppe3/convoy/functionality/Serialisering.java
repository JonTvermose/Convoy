package gruppe3.convoy.functionality;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Serialisering extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

  protected void onHandleIntent(Intent intent) {

        switch (intent.getStringExtra("funktion")) {
            case "gem":
                System.out.println("gem");
                gem(new String(), this.getFilesDir() + "/spots.ser");

                break;
            case "hent":
                System.out.println("hent");
                //SingleTon.hentedeSpots = (ArrayList) hent(this.getFilesDir() + "/spots.ser");

                break;
            default:
                System.out.println("default");
        }
    }


    public static void gem(Object obj, String filnavn) {
        try {
            FileOutputStream datastream = new FileOutputStream(filnavn);
            ObjectOutputStream objektstream = new ObjectOutputStream(datastream);
            objektstream.writeObject(obj);
            objektstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Object hent(String filnavn) {
        try {
            FileInputStream datastream = new FileInputStream(filnavn);
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