import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Yangtze
 * @version 1.0
 * @date 2022/11/24 22:51
 * 该线程用来写
 */

public class MyServerWriter extends Thread{
    private DataOutputStream dos;
    public MyServerWriter(DataOutputStream dos){
        this.dos = dos;
    }

    public void run(){
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String info;
        try{
            while (true){
                info = br.readLine();
                dos.writeUTF(info);
                if(info.equals("bye")){
                    System.out.println("您已下线，程序退出");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
