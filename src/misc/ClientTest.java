package misc;
import javax.swing.JFrame;

public class ClientTest {

	public static void main(String[] args) {
		//Client client = new Client("172.248.147.18");
		Client client = new Client("127.0.0.1");
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.startRunning();
	}

}
