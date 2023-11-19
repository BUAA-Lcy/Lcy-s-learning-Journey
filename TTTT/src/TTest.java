import java.util.Scanner;

public class TTest {
     public static double getPi(int n){
         if(n<0)return 0;
         else{
             int i=0;
             double answer=0.0;
             for(i=0;i<=n;i++){
                 if(i%2==0)answer+=4/(2*i+1);
                 else answer-=4/(2*i+1);
             }
             return answer;
         }
     }
     public static void main(String[] args){
         Scanner input= new Scanner(System.in);
         int n=0;
         n=input.nextInt();
         double answer=getPi(n);
         System.out.printf("%lf",answer);


     }


}
