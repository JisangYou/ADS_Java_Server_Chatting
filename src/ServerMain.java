import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

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
