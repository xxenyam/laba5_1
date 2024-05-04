import java.io.*;
import java.net.*;
import java.math.BigInteger;

interface Executable extends Serializable {
    Object execute();
}

interface Result extends Serializable {
    Object output();
    double scoreTime();
}
class JobOne implements Executable {
    private int n;

    public JobOne(int n) {
        this.n = n;
    }
    @Override
    public Object execute() {
        // Perform the task, for example, calculating the factorial of a number
        BigInteger res = BigInteger.ONE;
        for (int i = 1; i <= n; i++) {
            res = res.multiply(BigInteger.valueOf(i));
        }
        return res;
    }
}

class ResultImpl implements Result {
    private Object output;
    private double scoreTime;

    public ResultImpl(Object output, double scoreTime) {
        this.output = output;
        this.scoreTime = scoreTime;
    }

    @Override
    public Object output() {
        return output;
    }

    @Override
    public double scoreTime() {
        return scoreTime;
    }
}

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Сервер запущено. Очікування клієнтів...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Підключився новий клієнт: " + clientSocket.getInetAddress().getHostAddress());

                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                String classFile = (String) in.readObject();
                byte[] b = (byte[]) in.readObject();
                FileOutputStream fos = new FileOutputStream(classFile);
                fos.write(b);
                System.out.println("Отримано файл класу від клієнта: " + classFile);

                Executable ex = (Executable) in.readObject();

                double startTime = System.nanoTime();
                Object output = ex.execute();
                double endTime = System.nanoTime();
                double completionTime = (endTime - startTime) / 1_000_000.0; // переведемо час з наносекунд в мілісекунди

                ResultImpl r = new ResultImpl(output, completionTime);
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.writeObject(classFile);
                FileInputStream fis = new FileInputStream(classFile);
                byte[] bo = new byte[fis.available()];
                fis.read(bo);
                out.writeObject(bo);
                out.writeObject(r);

                System.out.println("Результат оброблено. Час виконання: " + completionTime + " мс");

                fos.close();
                fis.close();
                in.close();
                out.close();
                clientSocket.close();
                System.out.println("Клієнт відключився.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


