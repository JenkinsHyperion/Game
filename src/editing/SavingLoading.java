package editing;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import entities.*;
import engine.*;

public class SavingLoading {
	private final static String rootPath = "Levels";
	//private ArrayList<EntityStatic> staticEntitiesList;
	
	public SavingLoading(BoardAbstract board) {
		//staticEntitiesList = board.getStaticEntities();
		makeLevelFolder();
	}
	/** Used to create the main "Level" directory, mostly for first-time users.
	 *  If it exists already then nothing will happen.
	 */
	public void makeLevelFolder() {	
		try {
			//test for if folder exists or not:
			if (new File(rootPath).exists())
				System.out.println("Already exists!");
			else
				Files.createDirectories(Paths.get(rootPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Testing out creating several levels at once. Functionality isn't very useful as is,
	 * but could later be modified to enable you to copy levels.
	 * @param levelFolderName specified level name. Level name is the folder.
	 * @param size 
	 */
	public void writeSeriesOfLevels(String levelFolderName, int levelCount) {
		for (int i = 0; i < levelCount; i++)
		{
			if (new File(rootPath + File.separator + levelFolderName + Integer.toString(i)).exists())
				System.out.println("Level folder already exists. Skipping...");
			else 
			{
				try {
					String finalDirectory = rootPath + File.separator + levelFolderName + Integer.toString(i) + File.separator;
					Files.createDirectories(Paths.get(finalDirectory));
					FileOutputStream fileOut = new FileOutputStream(finalDirectory + "test.txt");
					PrintWriter printout = new PrintWriter(fileOut);
					printout.println("hey");
					printout.close();
					fileOut.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		}
	}
	
	/**Main level-creating function. Will create a folder with your level name,
	 * and populate(serialize) it with all the current entities in the list provided as an argument
	 * @param staticEntities The ArrayList to operate on. Will save(serialize) the state of each element.
	 * @param levelFolderName Your level's name
	 */
	public void writeLevel(ArrayList<EntityStatic> staticEntities, String levelFolderName) {
		FileOutputStream fileOut = null;
		//PrintWriter printout = null;
		ObjectOutputStream serialOut = null;
		String finalDirectory = rootPath + File.separator + levelFolderName + File.separator;
		//initial check makes sure levels aren't overwritten
		if ( new File(rootPath + File.separator + levelFolderName).exists() )
			//folder already exists; don't want to overwrite it.
			JOptionPane.showMessageDialog(null, "Level folder already exists. Skipping...");
		else // path doesn't exist, OK to create new one
		{
			if (staticEntities.size() == 0) { //can't save if there are no entities in list
				JOptionPane.showMessageDialog(null, "Nothing to save!");
			}
			else //everything's good, OK to serialize entities to new path
			{
				try {
					Files.createDirectories(Paths.get(finalDirectory));
					for (int i = 0; i < staticEntities.size(); i++) {
						fileOut = new FileOutputStream(finalDirectory + staticEntities.get(i).name + ".ser");
						serialOut = new ObjectOutputStream(fileOut);
						serialOut.writeObject(staticEntities.get(i));
						fileOut.close();
						serialOut.close();
					}
					fileOut.close();
					serialOut.close();
					System.out.println(Files.list(Paths.get(finalDirectory)).count());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
		}	
	}
	
	public void loadLevel(ArrayList<EntityStatic> staticEntities, String levelFolderName) {
		//int fileCount = 0; //amount of files in the folder
		File fileArray[] = null;
		FileInputStream fileIn = null;
		ObjectInputStream serialIn = null;
		String finalDirectory = rootPath + File.separator + levelFolderName + File.separator;

		if ( new File(rootPath + File.separator + levelFolderName).exists() ) 
		{
			//populate arraylist from the serialized files
			try {
				//fileCount = (int)Files.list(Paths.get(finalDirectory)).count();
				fileArray = new File(finalDirectory).listFiles(); //an array that contains each file in the current folder
				//clear out the old entity list first
				staticEntities.clear();

				for (int i = 0; i < fileArray.length; i++) 
				{
					fileIn = new FileInputStream(finalDirectory + fileArray[i].getName() );
					serialIn = new ObjectInputStream(fileIn);
					EntityStatic temp = null;
					temp = (EntityStatic)serialIn.readObject();
					staticEntities.add(temp);
					fileIn.close();
					serialIn.close();
				}
				System.out.println("load successful");
			} catch (FileNotFoundException f) {
				f.printStackTrace();
			} catch (IOException io) {
				io.printStackTrace();
			} catch (ClassNotFoundException c) {
				System.err.println("serialized entity file not found");
				c.printStackTrace();
			}	
		}
		else
			JOptionPane.showMessageDialog(null, "No files to load");
	}
	/*
	public static void main(String[] aaa) {
		CreateDirectory instance = new CreateDirectory();
		instance.makeLevelFolder();
		System.out.println("Attempting to create level...");
		instance.writeLevel("mylevel", instance.test.size());
		instance.loadLevel(instance.test, "mylevel");
	}
	*/
}
