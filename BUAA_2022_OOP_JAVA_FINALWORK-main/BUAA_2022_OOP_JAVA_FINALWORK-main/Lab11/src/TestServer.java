import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Yangtze
 * @version 1.0
 * @date 2022/11/24 22:33
 * ����һ���򵥵�Server����
 */

public class TestServer {
    public static void main(String[] args) {
        try{
            ServerSocket s = new ServerSocket(8888);
            Socket s1 = s.accept();
            OutputStream os = s1.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            InputStream is = s1.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            // start���������߳�
            new MyServerReader(dis).start();
            new MyServerWriter(dos).start();
        } catch(SocketException e){
            e.printStackTrace();
            System.out.println("���������쳣�������˳�");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
