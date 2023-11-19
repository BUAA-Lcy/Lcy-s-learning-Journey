import java.util.*;
import java.io.*;

public class Main {
    public static String mystrscat (String...args)
    {
        String a="0";
        for(String b:args){
           a=a+b;
        }
        return a;

    }
    public static void main(String[] args)
    {
        Scanner input =new Scanner(System.in);
        System.out.println("Hello world!");
        String a=mystrscat("a","b","c","d");
        System.out.println(a);
    }
}