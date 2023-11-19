import java.util.Scanner;
public class Main {
    public static void main(String[] args)
    {
        Scanner input= new Scanner(System.in);
        int n=0,i=1;
        n=input.nextInt();
        int k=n/2+1;//k为第一行输出的空格数+1   n=5 k=3
        for(i=1;i<=k;i++){
            int p=k-i;
            while(p!=0){
                System.out.print(" ");p--;
            }
            int o=2*i-1;
            while(o!=0){
                System.out.print("*");o--;
            }
            System.out.print("\r\n");
        }
        for(i=1;i<=k-1;i++){
            int p=i;
            while(p!=0){
                System.out.print(" ");p--;
            }
            int o=n-2*i;
            while(o!=0){
                System.out.print("*");o--;
            }
            System.out.print("\r\n");
        }

    }
}