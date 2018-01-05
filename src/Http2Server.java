public class Http2Server {

//	public static void main(String[] args) {
//		WebServer server = new WebServer(8089);
//		server.start();
//	}
//
//}
//
//class WebServer extends Thread {
//
//	public void run() {
//		// 1. 연결을 기다리는 서버소켓
//		ServerSocket server = new ServerSocket(8089);
//		while (true) {
//			// 2. 요청이 있을 떄까지 연결 대기
//			Socket client = server.accept();
//			// 3. 스트림을 연결 후 요청확인
//			// 프로토콜에서 uri를 꺼내는 함수를 제공한는 것까지 웹서버가 해준다.
//			// uri ; /(인증키)/json/GangseoFoodHygieneBizBakery/1/5/
//			String cmd = uri.split("/");
//			cmd[1] = "인증키";
//			cmd[2] = "json";
//			cmd[3] = "서비스이름";
//			cmd[4] = 시작인덱스;
//			cmd[5] = 종료인덱스;
//			
//			//4. 데이터베이스 연결
//			//5. 쿼리 생성 gn tlfgod
//			String query = "select * from GangseoFoodHygieneBizBakery where ... limit";
//			Cursor cursor = db.execute(query);
//			while(cursor.moveToNext()){
//				
//			}
//			
//		}
//	}
//}
//
//class webclient extends Thread {
//
//	public void run(){
//		//1. 소켓 생성
//		Socket clinet = new Socket("ip,ip,ip,ip")
//				
//		//2. 스트림 열결후 데이터 통신
//	}
}
