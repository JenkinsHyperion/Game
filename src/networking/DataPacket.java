package networking;

import java.awt.Point;

public class DataPacket {

	char[] testString;
	
	Point clientPlayerPosition;
	Point serverPlayerPosition;
	
	DataPacket( String inputString , Point clientPlayerPosition ){
		testString = inputString.toCharArray();
	}
	
}
