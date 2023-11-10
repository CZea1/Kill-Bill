package brigade.killbill;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Gdx;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture house;
	Texture bg;
	Texture ball;
	int x;
	int y;
	int x_inc;
	int y_inc;
	int ball_x;
	int ball_y;
	int ball_x_inc;
	int ball_y_inc;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		house = new Texture("house.jpg");
		bg = new Texture("office.jpg");
		ball = new Texture("ball.png");
		x_inc = 10;
		y_inc = 10;
		ball_x_inc = -25;
		ball_y_inc = -25;
		ball_x = Gdx.graphics.getWidth();
		ball_y = Gdx.graphics.getHeight();
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);
		batch.begin();
		batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.draw(house, x, y, 250, 250);
		batch.draw(ball, ball_x, ball_y, 150, 150);
		x += x_inc;
		y += y_inc;
		
		if (x >= Gdx.graphics.getWidth() - 250) {
			x_inc = -10;
		}
		else if (x <= 0) {
			x_inc = 10;
		}

		if (y >= Gdx.graphics.getHeight() - 250) {
			y_inc = -10;
		}
		else if (y <= 0) {
			y_inc = 10;
		}

		ball_x += ball_x_inc;
		ball_y += ball_y_inc;
		
		if (ball_x >= Gdx.graphics.getWidth() - 150) {
			ball_x_inc = -25;
		}
		else if (ball_x <= 0) {
			ball_x_inc = 25;
		}

		if (ball_y >= Gdx.graphics.getHeight() - 150) {
			ball_y_inc = -25;
		}
		else if (ball_y <= 0) {
			ball_y_inc = 25;
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		house.dispose();
	}
}
