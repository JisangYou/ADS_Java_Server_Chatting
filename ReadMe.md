# ADS04 Java 

## 수업 내용
- 서버 개념 학습
- 구현해보는 예제를 학습

## Code Review

1. 웹서버 main

```Java
// 웹 서버를 만들어 봅시다~
	// 브라우저에서 내가 만든 서버프로그램쪽으로 request를 요청
	public static void main(String[] args) {
		try{
		// 1. 서버를 생성
		ServerSocket server = new ServerSocket(10004);
		// 2. 요청을 대기
		Socket client = server.accept(); //<-- 마치 scanner의 next()처럼 요청이 있을때까지
						                 //    이줄에서 대기
		// 3. 접속된 client와 stream을 생성한다
		InputStreamReader isr = new InputStreamReader(client.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String temp = "";
		StringBuilder sb = new StringBuilder();
		// 4. 한줄씩 읽어서 저장
		while((temp=br.readLine()) != null){
			sb.append(temp).append("\n");
		}
		// 5. 최종 내용 출력
		System.out.println(sb.toString());
		// 연결닫기
		br.close();
		isr.close();
		client.close();
		server.close();
		}catch(Exception e){e.printStackTrace();}
	}

}

/*
 * 1. 포트번호가 있는 경우 : 콜론 다음이 포트 번호 http://openapi.seoul.go.kr:8088/
 * 
 * 2. 포트번호가 없는 경우 : 프로토콜의 default 포트번호 http: 80 https : 443
 * https://www.naver.com/
 * 
 * 
 * - 소켓 = iㅔ(주소) + 포트번호
 */
```
- 서버를 구축하는 핵심이 되는 틀

2. chatting server

```Java
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
```

3. chatting client

```Java
public class ChatClient {

	public static void main(String[] args) {
		// 특정 ip와 port를 가진 서버에 접속해서 메시지를 전송하는 프로그램
		Client client = new Client("192.168.1.120", 10004);
		client.start();
	}
}

class Client extends Thread {
	public boolean runFlag = true;
	String ip;
	int port;

	public Client(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void run() {
		try {
			// 1. 서버와 접속
			Socket socket = new Socket(ip, port);
			OutputStream os = socket.getOutputStream();
			// 2. 내 입력을 받을 스캐너 생성
			Scanner scanner = new Scanner(System.in);
			while (runFlag) {
				String msg = scanner.nextLine();
				// 3. 내가 입력한 값이 exit이면
				if ("exit".equals(msg))
					runFlag = false;
				msg = msg + "\r\n";
				os.write(msg.getBytes());
				os.flush();
			}
			os.close();
			socket.close();
		} catch (Exception e) {
		}
	}
}
```


4. HttpServer

```Java
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
``` 



## 보충설명

### Network

- 여러대의 컴퓨터를 통신 회선으로 연결한 것
- 서버: 서비스를 제공하는 프로그램
- 클라이언트 : 서비를 받는 프로그램 
- 클라이언트는 서비스를 받기위해 연결을 요청(Request), 서버는 클라이언트가 요청하는 내용을 처리해주고, 응답(Response)을 클라이언트로 보낸다.
- IP : 컴퓨터 고유의 주소 PORT : 어떤 서버,프로그램과 통신할 지 결정을 내릴 수 있는 번호 
>> 한대의 컴퓨터에는 다양한 서버프로그램들이 실행 될 수 있다. 예를 들어, 웹서버, 데이터베이스 관리시스템, FTP서버 등이 하나의 IP주소를 갖는 컴퓨터에서 동시에 실행될 수 있다. 이 경우 클라이언트는 어떤 서버와 통신을 해야할 지 결정해야한다. IP는 컴퓨터의 네트워크 어댑터까지만 갈 수 있는 정보이기 때문에 컴퓨터 내에서 실행하는 서버를 선택하기 위해서는 추가적인 정보가 필요하다. 그게 바로 포트번호이다. 
#### 출처: [이것이 자바다]
- 네트워크 프로그래밍이란 서로 떨어져 있는 호스트(컴퓨터)들 간에 데이터를 주고 받을 수 있도록 프로그램을 구현하는 것입니다. 다만 통신할 대상이 멀리 떨어져 있기 때문에 소프트웨어 차원에서 호스트들간에 연결해주는 장치가 필요하고 이러한 기능을 해주는 장치를 소켓(socket)이라고 합니다. 일반적으로 소켓 프로그래밍과 네트워크 프로그래밍이라는 용어는 같은 의미로 사용됩니다.



### Socket

- TCP는 연결 지향적 프로토콜, 클라이언트와 서버가 연결된 상태에서 데이터를 주고 받는 포로토콜을 말함.

- TCP는 데이터를 정확하고 안정적으로 전달하는 장점이 있는데 반해, 데이터를 보내기 전에 반드시 연결이 형성되어야 함.

- 자바는 TCP네트워킹을 위해 Socket클래스를 제공

- 소켓은 소프트웨어로 작성된 통신 접속점이라고 할 수 있는데 네트웍 응용 프로그램은 소켓을 통하여 통신망으로 데이터를 송수신하게 된다

- 소켓은 이렇게 어플리케이션에게 네트워크 접속을 위한 연결장치, 인터페이스 역할을 하는것입니다. 네트워크 어플리케이션이 보낸 데이터를 소켓을 거쳐 운영체제상에 존재하는 TCP/IP 소프웨어에게 전달하게 됩니다. 다시 하드웨어 상인 랜카드를 거쳐 네트워크에 전달하게 됩니다. 서버의 경우는 클라이언트와 정 반대되는 개념입니다. 네트워크는 서버에게 보낸데이터를 서버의 랜카드에게 보내지게 됩니다. 다시 운영체제의 TCP/IP소프트웨어를 거쳐 어플리케이션과 연결개념인 소켓을 통해 최종적으로 서버 어플리케이션에게 전달되는것입니다.소켓은 이렇게 어플리케이션과 TCP/IP 사이에 존재 하고 있습니다.


![서버,클라이언트](http://cfile22.uf.tistory.com/image/2375F64D56FCCE5B252A2A)

- 서버 예제 코드

```Java
public class Server {
    public static void main(String... args){
        //자동 close
        try(ServerSocket server = new ServerSocket()){
            // 서버 초기화
            InetSocketAddress ipep = new InetSocketAddress(9999);
            server.bind(ipep);
             
            System.out.println("Initialize complate");
             
            //LISTEN 대기
            Socket client = server.accept();
            System.out.println("Connection");
             
            //send,reciever 스트림 받아오기
            //자동 close
            try(OutputStream sender = client.getOutputStream();
                InputStream reciever = client.getInputStream();){
                //클라이언트로 hello world 메시지 보내기
                //11byte 데이터
                String message = "hello world";
                byte[] data = message.getBytes();
                sender.write(data, 0, data.length);
                 
                //클라이언트로부터 메시지 받기
                //2byte 데이터
                data = new byte[2];
                reciever.read(data, 0, data.length);
                 
                //수신 메시지 출력
                message = new String(data);
                String out = String.format("recieve - %s", message);
                System.out.println(out);
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }
}

```
- ※ 스트림 소켓은 양방향으로 바이트 스트림을 전송 할 수 있는 연결 지향형 소켓으로 양쪽 어플리케이션이 모두 데이터를 주고 받을 수 있다는것을 의미한다. 스트림소켓은 오류수정, 전송처리, 흐름제어등을 보장해 주며 송신된 순서에 따른 중복되지 않은 데이터를 수신하게 된다. 이 소켓은 각 메세지를 보내기 위해 별도의 연결을 맺는 행위를 하므로 약간의 오버헤드가 존재한다. 그러므로 소량의 데이터보다는 대량의 데이터를 보내는 경우에 적당하다. 스트림소켓은 이러한 품질의 통신을 수행하기 위해서 TCP를 사용한다.





- 클라이언트 예제 코드 

```Java
public class Client {
    public static void main(String... args){
        //자동 close
        try(Socket client = new Socket()){
            //클라이언트 초기화
            InetSocketAddress ipep = new InetSocketAddress("127.0.0.1", 9999);
            //접속
            client.connect(ipep);
             
            //send,reciever 스트림 받아오기
            //자동 close
            try(OutputStream sender = client.getOutputStream();
                InputStream receiver = client.getInputStream();){
                //서버로부터 데이터 받기
                //11byte
                byte[] data = new byte[11];
                receiver.read(data,0,11);
                 
                //수신메시지 출력
                String message = new String(data);
                String out = String.format("recieve - %s", message);
                System.out.println(out);
                 
                //서버로 데이터 보내기
                //2byte
                message = "OK";
                data = message.getBytes();
                sender.write(data, 0, data.length);
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }
}

```

- 서버는 클라이언트를 기다리고 있고, 클라이언트는 연결되었다는 것을 connect()함수로 코드상으로 알려줘야함.  

### Thread

>> 정의 : 스레드(thread)는 어떠한 프로그램 내에서, 특히 프로세스 내에서 실행되는 흐름의 단위를 말한다. 일반적으로 한 프로그램은 하나의 스레드를 가지고 있지만, 프로그램 환경에 따라 둘 이상의 스레드를 동시에 실행할 수 있다. 
출처: 위키백과

- 운영체제에서는 실행 중인 하나의 애플리케이션을 프로세스라고 부른다. 사용자가 애플리케이션을 실행하면 운영체제로부터 실행에 필요한 메모리를 할당받아 애플리케이션의 코드를 실행하는데 이것이 프로세스이다. 
ex) 크롬브라우저를 두개 실행 했다면 두개의 크롬 프로세스가 생성된 것

- Thread는 프로세스내에 있는 것으로, 프로세스내에서 동작하는 코드의 실행 흐름.

![Thread](https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Multithreaded_process.svg/220px-Multithreaded_process.svg.png)

- 멀티쓰레드는 하나의 프로세스에서 여러개의 다른 작업을 하는 것을 뜻함. 예를 들어, 미디어 플레이어같은 경우 동영상재생과 음악재생. 하나의 프로세스 내에서 두가지 이상의 기능이 동시에 처리 됨.

- 멀티 쓰레드는 예외처리가 굉장히 중요함. 채팅프로그램같은 경우에 예외가 발생하면 아예, 프로세스자체가 종료되기 때문에 채팅 스레드도 같이 종료된다. 

- 멀티 쓰레드는 다양한 곳에서 사용됨. 

  1. 대용량 데이터의 처리 시간을 줄이기 위해 데이터를 분할해 병렬 처리
  2. UI를 가지고 있는 애플리케이션에서 네트워크 통신을 하기 위해 사용되기도 함.
  3. 다수의 클라이언트의 요청을 처리하는 서버를 개발할 때도 사용됨.

- 예제 코드

1. 쓰레드 구현방법

```Java
1) Thread 클래스를 상속

class MyThread extends thread{

public void run(){/*작업 내용*/}// Thread 클래스의 run()을 오버라이딩

}



2) Runnable 인터페이스를 구현

class MyThread implements Runnable{

	public void run(){/*작업 내용*/}// Runnable인터페이스의 추상메서드 run()을 구현

}

```
2. 구현 예제

```Java
class ThreadEx1 {

	public static void main(String args[]) {

		A t1 = new A();



		Runnable r = new B();

		Thread t2 = new  Thread(r); // 생성자 Thread(Runnable target)

		// Runnable 인터페이스를 구현한 경우, Runnable 인터페이스를 구현한 클래스의 인스턴스를 생성한 다음, 이 인스턴스를 가지고 Thread 클래스의 인스턴스를 생성할 때 생성자의 매개변수로 제공해야 한다.

		// 이 때 사용되는 Thread 클래스의 생성자는 Thread(Runnable target)로 호출시에 Runnable인터페이스를 구현한 클래스의 인스턴스를 넘겨줘야 한다.

		t1.start();// 쓰레드를 생성한 다음에는 start()를 호출해야한 비로소 작업을 시작하게 된다.

		t2.start();

		//한 번 사용한 쓰레드는 다시 재사용할 수 없다. 즉 하나의 쓰레드에 대해 start()가 한 번만 호출될  수 있다는 뜻이다.

		//그래서 쓰레드의 작업이 한 번 더 수행되기를 원한다면 오른쪽의 코드와 같이 새로운 쓰레드를 생성한 다음에 start()를 호출해야 한다.

		

	}

}



class A extends Thread {

	public void run() {

		for(int i=0; i < 5; i++) {

			System.out.println(getName()); // 조상인 Thread의 getName()을 호출, 즉 쓰레드의 이름을 반환한다.

		}

	}

}



class B implements Runnable {

	public void run() {

		for(int i=0; i < 5; i++) {

			// Thread.currentThread() - 현재 실행중인 Thread를 반환한다.

		      System.out.println(Thread.currentThread().getName());

		     // Thread 클래스를 상속받으면, Thread 클래스의 메서드를 직접 호출할 수 있지만, Runnable을 구현하면 Thread클래스의 static 메서드인 currentThread()를 호출하여 쓰레드에 대한 참조를 얻어 

		     // 와야만 호출이 가능하다.

		}

	}

}

```
- 이 밖에도 멀티쓰레드는 다룰 부분이 많기에, 다른 프로젝트에서 관련 설명 보완할 것임.

#### 출처: 소켓프로그래밍이란 소켓이란 소켓접속이란 socket 정의|작성자 겨울봄비
#### 출처: http://nowonbun.tistory.com/315 [명월 일지]
#### 출처: http://elena90.tistory.com/entry/Java-파일-입출력스트림InputStreamOutputStreamReaderWriter [오니님의짱꺤뽀]
#### 출처: http://devbox.tistory.com/entry/Java-쓰레드 [장인개발자를 꿈꾸는 :: 기록하는 공간]
## TODO

- 네트워킹 쪽 전반적인 공부
- TCP, UDP
- 웹과 관련된 용어 공부\
- Multi Thread 및 Thread pool 개념 및 깊이 있게 보기
- Multi Thread 관련 예외처리 이슈

## Retrospect

- networking 및 Thread부분이 있어 굉장히 어려웠음. 그러나 백 엔드 개발까지도 생각해볼 수 있는 수업이여서 유익했음.

## Output
- 생략