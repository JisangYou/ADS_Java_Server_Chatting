import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {
	public static void main(String[] args) {
		WebServer server = new WebServer(8089);
		server.start();
	}
}

class WebServer extends Thread {
	ServerSocket server;
	public boolean runFlag = true;
	public WebServer(int port){
		try{
			server = new ServerSocket(port);
		}catch(Exception e){e.printStackTrace();}
	}
	public void run(){
		while(runFlag){
			// 1. 클라이언트 연결 대기
			try{
				Socket client = server.accept();
				// 2. 요청에 대한 처리를 새로운 thread에서 해준다
				new Thread(){
					public void run(){
						try{
							// 3. 스트림을 연결
							InputStreamReader isr = new InputStreamReader(client.getInputStream());
							BufferedReader br = new BufferedReader(isr);
							// 4. 웹브라우저에서 요청한 주소로 줄단위의 명령어가 날라오는 것을 꺼내서 처리
							String line = br.readLine();
							System.out.println("line="+line);
							// 5. 요청된 명령어의 첫 줄만 parsing 해서 동작을 결정
							// Method[ ]로컬자원(도메인을제외한주소)[ ]프로토콜의버전
							String cmd[] = line.split(" ");
							if("/hello".equals(cmd[1])){
								String msg = "<h1>Hello!~~~~~~~~~~</h1>";
								OutputStream os = client.getOutputStream();
								// 화면에는 보이지 않는 메타정보
								os.write("HTTP/1.0 200 OK \r\n".getBytes());
								os.write("Content-Type: text/html \r\n".getBytes());
								os.write(("Content-Length: "+msg.getBytes().length+"\r\n").getBytes());
								// 헤더와 바디 구분자를 전송
								os.write("\r\n".getBytes());
								// 실제 전달되는 데이터
								os.write(msg.getBytes());
								os.flush();
							}else{
								String dir = "c:/temp";
								// Path를 사용한 파일처리
								Path path = Paths.get(dir+"/"+cmd[1]);
								byte content[] = Files.readAllBytes(path);
								
								if(Files.exists(path)){
									OutputStream os = client.getOutputStream();
									String mimeType = Files.probeContentType(path);
									// 화면에는 보이지 않는 메타정보
									os.write("HTTP/1.0 200 OK \r\n".getBytes());
									if("plain/text".equals(mimeType)){
										os.write("Content-Type: text/html \r\n".getBytes());
									}else{
										os.write(("Content-Type: "+mimeType+" \r\n").getBytes());
									}
									// 파일을 읽고 byte 배열로 변환한후 사이즈를
									int size = content.length;
									os.write(("Content-Length: "+size+"\r\n").getBytes());
									// 헤더와 바디 구분자를 전송
									os.write("\r\n".getBytes());
									// 실제 전달되는 데이터
									os.write(content);
									os.flush();
									os.close();
								}
							}
							client.close();
						}catch(Exception e){e.getStackTrace();}
					}
				}.start();
			}catch(Exception e){e.printStackTrace();}
		}
	}
}

