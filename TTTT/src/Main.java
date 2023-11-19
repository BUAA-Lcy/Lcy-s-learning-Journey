import java.util.*;
import java.io.*;

public class Main {
    public static double getPi(int n){
        if(n<0)return 0;
        else{
            double i=0;
            double answer=0.0;
            for(i=0;i<=n;i++){
                if(i%2==0)answer+=4.0/(2.0*i+1.0);
                else answer-=4.0/(2.0*i+1.0);
            }
            return answer;
        }
    }
    public static void main(String[] args){
        Scanner input= new Scanner(System.in);
        int n=0;
        n=input.nextInt();
        double answer=Main.getPi(n);
        System.out.println(answer);
        System.out.printf("%f",answer);
    }


}