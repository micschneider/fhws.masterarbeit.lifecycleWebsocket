package fhws.masterarbeit.lifecycleWebsocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/lifecycleWebsocket")
public class LifecycleWebsocket 
{
	private static String START_TIME = "Start Time";
	private Session session;
	
	@OnOpen
	public void whenOpening(Session session)
	{
		this.session = session;
		session.getUserProperties().put(START_TIME, System.currentTimeMillis());
		this.sendMessage("3:Geöffnet");
	}
	
	@OnMessage
	public void whenGettingAMessage(String message)
	{
		if(message.indexOf("xxx") != -1)
		{
			throw new IllegalArgumentException("Nachricht '" + message + "' mit dem Inhalt 'xxx' ist nicht erlaubt!");
		}
		else if(message.indexOf("Close") != -1)
		{
			try
			{
				this.sendMessage("1:Server hat die Verbindung nach " + this.getConnectionSeconds() + " Sekunden geschlossen");
				session.close();
			}
			catch(IOException e)
			{
				System.out.println("Fehler beim Schließen der Session: " + e.getMessage());
			}
		}
		else
		{
			this.sendMessage("3:Nachricht '" + message + "' verarbeitet!");
		}
	}
	
	private int getConnectionSeconds() 
	{
		long millis = System.currentTimeMillis() - ((Long)this.session.getUserProperties().get(START_TIME));
		return (int)millis/1000;
	}

	@OnError
	public void whenSomethindGoesWrong(Throwable t)
	{
		this.sendMessage("2:Error: " + t.getMessage());
	}
	
	@OnClose
	public void whenClosing()
	{
		System.out.println("GoodBye!");
	}
	
	void sendMessage(String message)
	{
		try
		{
			session.getBasicRemote().sendText(message);
		}
		catch(Throwable t)
		{
			System.out.println("Fehler beim Senden der Nachricht " + t.getMessage());
		}
	}

}
