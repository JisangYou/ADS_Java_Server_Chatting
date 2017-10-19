import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
	public static void main(String[] args) {
		Server server = new Server(10004);
		server.start();
	}
}

class Server extends Thread{
	ServerSocket server;
	public boolean runFlag = true;
	// 0. 서버소켓 생성
	public Server(int port){
		try{
			server = new ServerSocket(port);
		}catch(Exception e){}
	}
	public void run(){
		System.out.println("server is running...");
		while(runFlag){
			try{
				// 1. 클라이언트의 요청을 대기
				Socket client = server.accept(); // 아래쪽 코드는 접속요청을 받기 전까지는 실행되지 않는다
				new ClientProcess(client).start();
			}catch(Exception e){}
		}
	}
}
// 클라이언트 요청을 개별 thread로 처리하는 클래스
class ClientProcess extends Thread{
	Socket client;
	public ClientProcess(Socket client){
		this.client = client;
	}
	public void run(){
		try{
			// 1. client와 stream을 열고
			InputStreamReader isr = new InputStreamReader(client.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String msg = "";
			// 2. exit가 아닐때까지 한줄씩 읽어서 내용을 출력 
			while(!"exit".equals(msg=br.readLine())){
				if(msg == null)
					break;
				System.out.println(client.getInetAddress()+":"+msg);
			}
			// 연결닫기
			br.close();
			isr.close();
			client.close();
		}catch(Exception e){
			
		}
	}
}





































