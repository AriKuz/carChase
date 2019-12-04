package com.example.catchtheball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    //Frames
    private FrameLayout gameFrame;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startLayout;
    //Images
    private ImageView player, black, coin, heart, heart2, heart3;
    private Drawable imageBoxLeft,imageBoxRight;
    //Size
    private int playerSize;
    //Position

    private float playerX, playerY;
    private float blackX, blackY;
    private float coinX, coinY;
    //Score
    private TextView scoreLabel, highScoreLabel;
    private int score, highScore, timeCount;
    private SharedPreferences settings;
    //Class
    private Timer timer;
    private Handler handler = new Handler();
    //Status
    private boolean start_flag = false;
    private boolean action_flag = false;

    //Buttons
    private Button startButton;
    private Button quitButton;
    private Button moveRight;
    private Button moveLeft;
    //hit
    int hit=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameFrame = findViewById(R.id.gameFrame);
        startLayout = findViewById(R.id.startLayout);
        player = findViewById(R.id.player);
        black = findViewById(R.id.black);
        coin = findViewById(R.id.coin);
        heart = findViewById(R.id.heart);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);

        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);
        imageBoxLeft =  getResources().getDrawable(R.drawable.box_left);
        imageBoxRight =  getResources().getDrawable(R.drawable.box_right);

        startButton = findViewById(R.id.startButton);
        quitButton = findViewById(R.id.quitButton);
        moveLeft = findViewById(R.id.moveLeft);
        moveRight = findViewById(R.id.moveRight);



        // High Score
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE", 0);
        highScoreLabel.setText("High Score : " + highScore);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(v);
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });



    }

    public void changePos(){


    //Add time count
    timeCount += 20;

    //Hearts
        heart.setY(18);
        heart2.setY(18);
        heart2.setX(120.0f);
        heart3.setY(18);
        heart3.setX(240.0f);
        ImageView[] heartArray = {heart3,heart2,heart};

    //Coin
    coinY += 12;
    float coinCenterX = coinX + coin.getWidth() / 2;
    float coinCenterY = coinY + coin.getHeight() / 2;
    int[] xPosArray = {0,370,740};
    int randChoice=0, randChoice2 =0;

    if(hitCheck(coinCenterX, coinCenterY)){
        coinY = frameHeight + 100;
        score += 10;
    }

    if(coinY > frameHeight){
        coinY = -100;
     //   coinX = (float)Math.floor(Math.random()* (frameWidth - coin.getWidth()));
        randChoice = (int)(Math.random()*3);
        coinX = xPosArray[randChoice];
    }
    coin.setX(coinX);
    coin.setY(coinY);

    //Black


        blackY += 18;
        float blackCenterX = blackX + black.getWidth() / 2;
        float blackCenterY = blackY + black.getHeight() / 2;

        if(hitCheck(blackCenterX, blackCenterY)) {
            blackY = frameHeight + 100;
            //Hearts down
            if(hit < 3) {
                heartArray[hit++].setVisibility(View.INVISIBLE);
                System.out.println(hit);
            }

            if(hit == 3){
                gameOver();
            }

        }
        if(blackY > frameHeight){
                blackY = -100;
                //   coinX = (float)Math.floor(Math.random()* (frameWidth - coin.getWidth()));
                randChoice2 = (int)(Math.random()*3);
                if(randChoice2 != randChoice)
                    blackX = xPosArray[randChoice2];
                else
                    randChoice2 = (int)(Math.random()*randChoice);
                blackX = xPosArray[randChoice2];
        }

        black.setX(blackX);
        black.setY(blackY);


        moveLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerX -= 370;
            }
        });

        moveRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerX += 370;
            }
        });
        //check player position
        if(playerX < 0){
            playerX = 3;
        }

        if(frameWidth - playerSize < playerX){
            playerX = frameWidth - playerSize - 7;
        }

        player.setX(playerX);
        scoreLabel.setText("Score :  " + score);
    }


    public boolean hitCheck(float x, float y){
        if(playerX <= x && x<= playerX + playerSize  && playerY <= y && y<= frameHeight)
            return true;
        return false;
    }

    public void gameOver(){
        timer.cancel();
        timer = null;
        start_flag = false;

        // Before showing startLayout, sleep 1 second.
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        hit = 0;
        startLayout.setVisibility(View.VISIBLE);
        player.setVisibility(View.INVISIBLE);
        coin.setVisibility(View.INVISIBLE);
        black.setVisibility(View.INVISIBLE);
        moveRight.setVisibility(View.INVISIBLE);
        moveLeft.setVisibility(View.INVISIBLE);

        // Update High Score
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score : " + highScore);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", highScore);
            editor.commit();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(start_flag){
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                action_flag = true;
            else if(event.getAction() == MotionEvent.ACTION_UP)
                action_flag = false;
        }
        return true;
    }

    public void startGame(View view){
        start_flag = true;
        startLayout.setVisibility(View.INVISIBLE);

        if(frameHeight == 0){
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            playerSize = player.getHeight();
            playerX = player.getX();
            playerY = player.getY();
        }

        player.setX(0.0f);
        heart.setY(3000.0f);
        heart2.setY(3000.0f);
        heart3.setY(3000.0f);

        black.setY(3000.0f);
        coin.setY(3000.0f);

        blackY = black.getY();
        coinY = coin.getY();


        player.setVisibility(View.VISIBLE);
        heart.setVisibility(View.VISIBLE);
        heart2.setVisibility(View.VISIBLE);
        heart3.setVisibility(View.VISIBLE);
        black.setVisibility(View.VISIBLE);
        coin.setVisibility(View.VISIBLE);
        moveLeft.setVisibility(View.VISIBLE);
        moveRight.setVisibility(View.VISIBLE);

        timeCount = 0;
        score = 0;
        scoreLabel.setText("Score : 0");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(start_flag){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        },0, 20);
    }

}
