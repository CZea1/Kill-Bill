package brigade.killbill;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("shroud.jpg");
			boolean isExtAvailable = Gdx.files.isExternalStorageAvailable(); // Checks to see if external storage is available
			boolean isLocAvailable = Gdx.files.isLocalStorageAvailable();	 // Check to see if the local stoarge is available
		
			System.out.println(isExtAvailable); // Returns true, external storage is available
			System.out.println(isLocAvailable); // Returns true, local storage is available

			// I've decided that the loot tables will be text files in the assets folder, so it'll be a local folder that I need to access
			FileHandle file = Gdx.files.internal("weapon_loot_table.txt"); // the .internal references the local assets folder, where a file named "weapon_loot_table.txt" is located

			//System.out.println(handle.reader()); // Attempting to output the contents of the text file, currently just some temporary words
			String text = file.readString(); 	// Declaring string to contain the contents of the file
			System.out.println(text);			// Prints out the string, which pulls from the file

			
	}
	@Override
	public void render () {
		ScreenUtils.clear(1, 0, 0, 1);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
