package gruppe3.convoy.functionality;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialisering {

  public static void gem(Object obj, String filnavn) throws IOException {
    FileOutputStream datastream = new FileOutputStream(filnavn);
    ObjectOutputStream objektstream = new ObjectOutputStream(datastream);
    objektstream.writeObject(obj);
    objektstream.close();
  }

  public static Object hent(String filnavn) throws Exception {
    FileInputStream datastream = new FileInputStream(filnavn);
    ObjectInputStream objektstream = new ObjectInputStream(datastream);
    Object obj = objektstream.readObject();
    objektstream.close();
    return obj;
  }
}