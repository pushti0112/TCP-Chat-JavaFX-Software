import java.net.*;
import java.util.*;
public class MyServer
{
      public static void main(String[] st)
	{
	try
	{
	DatagramSocket server=new DatagramSocket(7778);
	String msg="";
	byte b[]=new byte[1024];
	DatagramPacket pack=null;
 
           pack=new DatagramPacket(b,b.length);
	server.receive(pack);
	b=pack.getData();
	msg=new String(b);
	msg=msg.trim();

	String result="";
	StringTokenizer str=new StringTokenizer(msg);
	int op1=Integer.parseInt(str.nextToken(" "));
	String op=str.nextToken(" ");
	int op2=Integer.parseInt(str.nextToken());
           if(op.equals("+"))
	{
                   int a=op1+op2;
                   result=Integer.toString(a);		
	}
	System.out.println("Sending result....");
	
	b=result.getBytes();
	InetAddress ip=InetAddress.getByName("localhost");
	pack=new DatagramPacket(b,b.length,ip,7777);
	server.send(pack); 
          
          server.close();
	}
	catch(Exception e)
	{
		System.out.println("Server Error:"+e.getMessage());
	}
	}
}

