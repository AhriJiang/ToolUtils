package InterfaceTestUtils;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.nio.ByteBuffer;

public class WebSoecketClientUtils extends WebSocketClient{
	
	public WebSoecketClientUtils(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public WebSoecketClientUtils(URI serverURI) {
		super(serverURI);
	}
	
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("new connection opened");
	}
	
	@Override	
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("closed with exit code " + code + " additional info: " + reason);
	}
	
	@Override
	public void onMessage(String message) {
		System.out.println("received message: " + message);
	}
	
	@Override
	public void onMessage(ByteBuffer message) {
		System.out.println("received ByteBuffer");
	}
	
	@Override
	public void onError(Exception ex) {
		System.err.println("an error occurred:" + ex);
	}
	
	public void sendJson(JSONObject jsonObject){
		send(jsonObject.toString());
	}
	
	public static void main(String[] args) throws URISyntaxException {		
		WebSoecketClientUtils client = new WebSoecketClientUtils(new URI("ws://localhost:8887"));
		client.connect();
		client.send("Hello!");
		client.sendJson(new JSONObject(""));
		client.close();
	}
}
