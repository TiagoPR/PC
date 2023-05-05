package aula10;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try{
        if(args.length<2)
            System.exit(1);
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Socket s = new Socket(host, port);
        BufferedReader in =
            new BufferedReader(new InputStreamReader( s.getInputStream()));
        PrintWriter out = new PrintWriter(s.getOutputStream());
        out.println("Hello world");
        out.flush();
        String res = in.readLine();
        System.out.println(res);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }
}
