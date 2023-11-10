package brigade.killbill;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img, character;
	int x, y, xPos, yPos, centerScreenX, centerScreenY;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("shroud.jpg");
		character = new Texture("character.jpg");
		x = getWidth();
		y = getHeight();
		centerScreenX = x / 2;
		centerScreenY = y / 2;
		xPos = x / 2;
		yPos = y / 2;
	}

	@Override
	public void render () {
		ScreenUtils.clear(255/255f, 255/255f, 20/255f, 0);
		batch.begin();
		batch.draw(img, 0, 0);
		DrawCharacter();
		if (getKeyboardInput_W()) {
			yPos += 5;
		}
		if (getKeyboardInput_A()) {
			xPos -= 5;
		}
		if (getKeyboardInput_S()) {
			yPos -= 5;
		}
		if (getKeyboardInput_D()) {
			xPos += 5;
		}

		if (getKeyboardInput_F()) {
			Gdx.app.exit();
		}
		if (getKeyboardInput_L()) {
			xPos = centerScreenX;
			yPos = centerScreenY;
		}
		batch.end();
	}
		
	
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}


	//Methods
	public void DrawCharacter() {
		batch.draw(character, xPos, yPos);
	}

	public boolean getKeyboardInput_W() {
		return Gdx.input.isKeyPressed(Keys.W);
	}

	public boolean getKeyboardInput_A() {
		return Gdx.input.isKeyPressed(Keys.A);
	}

	public boolean getKeyboardInput_S() {
		return Gdx.input.isKeyPressed(Keys.S);
	}

	public boolean getKeyboardInput_D() {
		return Gdx.input.isKeyPressed(Keys.D);
	}

	public boolean getKeyboardInput_F() {
		return Gdx.input.isKeyPressed(Keys.F);
	}

	public boolean getKeyboardInput_L() {
		return Gdx.input.isKeyPressed(Keys.L);
	}

	public int getWidth() {
		return Gdx.graphics.getWidth();
	}

	public int getHeight() {
		return Gdx.graphics.getHeight();
	}
}
