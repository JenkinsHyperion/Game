package engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Console {

	private BoardAbstract board;
	
	private String inputField = "";
	private Point fieldPosition;
	private Character selected;
	private int cursorIndex = 0;
	private Rectangle cursor;
	private Color cursorColor = new Color(255,255,255,100);
	private Font defaultFont = new Font("Helvetica", Font.PLAIN, 16);
	
	private ArrayList<String> enteredLines = new ArrayList<String>();

	protected Console( int x , int y, BoardAbstract board){
		this.board = board;
		fieldPosition = new Point( x ,y );
		cursor = new Rectangle( x,y, 8,15);
	}
	
	protected void drawConsole( Graphics2D g2 ){

		g2.setColor(Color.WHITE);
		g2.setFont( defaultFont);
		g2.drawString("Console: ", fieldPosition.x , fieldPosition.y );
		g2.drawString( inputField , fieldPosition.x , fieldPosition.y +15 );
		g2.setColor( cursorColor );
		g2.fill(cursor);
		
		for ( int i = 0 ; i < enteredLines.size() ; i++ ){
			g2.drawString( enteredLines.get( enteredLines.size()-1-i) , fieldPosition.x+15, fieldPosition.y + 30 + i*15 );
		}
	}
	
	protected void inputEvent( KeyEvent e ){
		
		char input = e.getKeyChar();
		int key = e.getKeyCode();
		
		if ( Character.isLetterOrDigit(input)  ){

			insertCharacterAtCursor( input ); 	//split string in halves and add character between
			cursorRight(input); 				//move cursor the distance of the deleted character
			cursorIndex++;						//increment cursor to next index
		}
		else if( key == KeyEvent.VK_BACK_SPACE ){
			if ( cursorIndex > 0 ){
				cursorLeft(inputField.charAt(cursorIndex-1));
				pullCharacterAtIndex(cursorIndex);
				cursorIndex--;
			}
		}
		else if( key == KeyEvent.VK_DELETE ){
			if ( cursorIndex < inputField.length() ){
				pullCharacterAtIndex(cursorIndex+1); 	//same as delete but without moving the cursor
			}
		}
		else if( key == KeyEvent.VK_SPACE){
			
			char whitespace = Character.SPACE_SEPARATOR;
			insertCharacterAtCursor( whitespace );
			cursorRight( whitespace );
			cursorIndex++;
		}
		else if( key == KeyEvent.VK_LEFT ){
			if ( cursorIndex > 0 ){
				cursorLeft( inputField.charAt(cursorIndex-1) );
				cursorIndex--;
			}
		}
		else if( key == KeyEvent.VK_RIGHT ){
			if ( cursorIndex < inputField.length() ){
				cursorRight( inputField.charAt(cursorIndex) );
				cursorIndex++;
			}
		}
		else if (key == KeyEvent.VK_ENTER ){
			enteredLines.add( "'"+ new String(inputField) + "' is not a recognized command" ); //command checking here
			clearInputField();
		}
	}
	
	private void insertCharacterAtCursor( char character ){
		
		if ( inputField.length() > 0 ){
			
			String frontHalf = inputField.substring( 0 , cursorIndex );
			String endHalf =  inputField.substring( cursorIndex , inputField.length() );
			
			inputField = frontHalf + Character.toString(character) + endHalf;
		}
		else{ 
			inputField = inputField + Character.toString(character);
		}
	}
	
	private void pullCharacterAtIndex( int index ){
			
			String frontHalf = inputField.substring( 0 , index-1 );
			String endHalf =  inputField.substring( index , inputField.length() );
			
			inputField = frontHalf + endHalf;
	}
	
	private void clearInputField(){
		inputField = "";
		cursorIndex = 0;
		cursor.setLocation(fieldPosition.x, fieldPosition.y);
	}
	
	private void cursorLeft( Character character ){	//these methods use Graphic2Ds Font Metrics to determine the pixel width of a character
		cursor.setLocation( (int)cursor.getMinX() - board.getFontMetrics(defaultFont).charWidth(character) , (int)cursor.getMinY() );
	}
	private void cursorRight( Character character ){
		cursor.setLocation( (int)cursor.getMinX() + board.getFontMetrics(defaultFont).charWidth(character) , (int)cursor.getMinY() );
	}
	
	
	
}
