import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 12345);

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            String classFile = "C:\\\\Users\\\\Irina\\\\cross\\\\laba5_1\\\\class.txt";
            out.writeObject(classFile);

            FileInputStream fis = new FileInputStream(classFile);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.writeObject(b);

            int num = 10; // Assuming num is the input value
            JobOne aJob = new JobOne(num);
            out.writeObject(aJob);

            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            classFile = (String) in.readObject();

            FileOutputStream fos = new FileOutputStream(classFile);
            b = (byte[]) in.readObject();
            fos.write(b);

            Result r = (Result) in.readObject();

            System.out.println("result = " + r.output() + ", time taken = " + r.scoreTime() + "ns");

            fis.close();
            fos.close();
            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
