package fantasyteam.sw2.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientTest extends Server{

    public String server_hash;
    
	public ClientTest()
	{
		super();
                this.server_hash = "";
	}

	
	public static void main(String args[])
	{
		int run = 1;
		ClientTest server = new ClientTest();
		BufferedReader readConsole = null;
		try
		{
			readConsole = new BufferedReader (new InputStreamReader(System.in));
		}
		catch(Exception e)
		{
			System.out.println("Failed to create buffered reader for the console LOL");
		}
		while(run == 1)
		{
			System.out.println("Connect? ");
			String option = "";
			try
			{
				option = readConsole.readLine();
			}
			catch(IOException e)
			{
				System.out.println("fek off");
			}
			if(option.compareTo("Y") == 0)
			{
				try
				{
					server.setPort(23231);
					server.server_hash = server.addSocketByIp("127.0.0.1");
				}
				catch(IOException e)
				{
					System.out.println(e.getMessage());
				}
				run++;
                                try{
				server.print();
                                }
                                catch(IOException e){
                                    
                                }
                                try{
				server.print("LOL");
                                }
                                catch(IOException e){
                                    
                                }
                                try{
				server.print("\t");
                                }
                                catch(IOException e){
                                    
                                }
				while(run == 2)
				{
					System.out.println("Type your message: ");
					String message = "";
					try
					{
						message = readConsole.readLine();
					}
					catch(IOException e)
					{
						System.out.println("fek off");
					}
					try
					{
						server.getSocketList().get(server.server_hash).getSocket().sendMessage(message);
					}
					catch(IOException e)
					{
						System.out.println("ERROR LULZ: "+e.getMessage());
					}
					try
					{
						System.out.println(server.getSocketList().get(server.server_hash).getSocket().readMessage());
					}
					catch(IOException e)
					{
						System.out.println("Couldn't read from socket WHOOPS");
					}/*
					try
					{
						Socket TempS = new Socket("127.0.0.1",23231);
						Sock s = new Sock(TempS);
						server.getSocketList().get(server.server_hash).setSocket(s);
					}
					catch(IOException e)
					{
						System.out.println(e.getMessage());
					}*/
				}
			}
		}
	}
	
        @Override
	public void handleMessage(String message)
	{
		System.out.println(message);
	}
}