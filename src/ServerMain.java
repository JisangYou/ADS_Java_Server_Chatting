import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

	// �� ������ ����� ���ô�~
	// ���������� ���� ���� �������α׷������� request�� ��û
	public static void main(String[] args) {
		try{
		// 1. ������ ����
		ServerSocket server = new ServerSocket(10004);
		// 2. ��û�� ���
		Socket client = server.accept(); //<-- ��ġ scanner�� next()ó�� ��û�� ����������
						                 //    ���ٿ��� ���
		// 3. ���ӵ� client�� stream�� �����Ѵ�
		InputStreamReader isr = new InputStreamReader(client.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		String temp = "";
		StringBuilder sb = new StringBuilder();
		// 4. ���پ� �о ����
		while((temp=br.readLine()) != null){
			sb.append(temp).append("\n");
		}
		// 5. ���� ���� ���
		System.out.println(sb.toString());
		// ����ݱ�
		br.close();
		isr.close();
		client.close();
		server.close();
		}catch(Exception e){e.printStackTrace();}
	}

}

/*
 * 1. ��Ʈ��ȣ�� �ִ� ��� : �ݷ� ������ ��Ʈ ��ȣ http://openapi.seoul.go.kr:8088/
 * 
 * 2. ��Ʈ��ȣ�� ���� ��� : ���������� default ��Ʈ��ȣ http: 80 https : 443
 * https://www.naver.com/
 * 
 * 
 * - ���� = i��(�ּ�) + ��Ʈ��ȣ
 */
