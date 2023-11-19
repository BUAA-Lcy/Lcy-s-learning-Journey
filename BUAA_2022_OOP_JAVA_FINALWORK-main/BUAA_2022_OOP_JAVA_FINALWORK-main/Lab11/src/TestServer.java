import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Yangtze
 * @version 1.0
 * @date 2022/11/24 22:33
 * 这是一个简单的Server程序
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

            // start用来开启线程
            new MyServerReader(dis).start();
            new MyServerWriter(dos).start();
        } catch(SocketException e){
            e.printStackTrace();
            System.out.println("网络连接异常，程序退出");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
