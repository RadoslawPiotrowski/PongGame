package com.example.pingpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PongView extends SurfaceView implements Runnable{

    // This is our thread
    Thread mGameThread = null;

    // We need a SurfaceHolder object
    // We will see it in action in the draw method soon.
    SurfaceHolder mOurHolder;

    // A boolean which we will set and unset
    // when the game is running- or not
    // It is volatile because it is accessed from inside and outside the thread
    volatile boolean mPlaying;

    // Game is mPaused at the start
    boolean mPaused = true;

    // A Canvas and a Paint object
    Canvas mCanvas;
    Paint mPaint;

    // This variable tracks the game frame rate
    long mFPS;

    // The size of the screen in pixels
    int mScreenX;
    int mScreenY;

    // The players mBat
    Bat mBat;

    // The players mBat
    Bat mBatTwo;

    // A mBall
    Ball mBall;

    // The mScore
    int mScore = 0;
    // the second score

    int mScore2 = 0;

    public PongView(Context context, int x, int y) {

    /*
        The next line of code asks the
        SurfaceView class to set up our object.
    */
        super(context);

        // Set the screen width and height
        mScreenX = x;
        mScreenY = y;

        // Initialize mOurHolder and mPaint objects
        mOurHolder = getHolder();
        mPaint = new Paint();

        // A new mBat
        mBat = new Bat(mScreenX, mScreenY, 0);

        mBatTwo = new Bat(mScreenX, mScreenY, 1);

        // Create a mBall
        mBall = new Ball(mScreenX, mScreenY);


        setupAndRestart();
    }

    public void setupAndRestart(){
        // Put the mBall back to the start
        mBall.reset(mScreenX, mScreenY);

        // if game over reset scores and mScore2
        if(mScore2 == 0) {
            mScore = 0;
            mScore2 = 0;
        }
    }

    @Override
    public void run() {

        while (mPlaying) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if(!mPaused){
                update();
            }

            // Draw the frame
            draw();

        /*
            Calculate the FPS this frame
            We can then use the result to
            time animations in the update methods.
        */
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                mFPS = 1000 / timeThisFrame;
            }
        }
    }
    // Everything that needs to be updated goes in here
// Movement, collision detection etc.
    public void update() {

        // Move the mBat if required
        mBat.update(mFPS);

        mBatTwo.update(mFPS);

        mBall.update(mFPS);

        // Check for mBall colliding with mBat
        if(RectF.intersects(mBat.getRect(), mBall.getRect())) {
            mBall.setRandomXVelocity();
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mBat.getRect().top - 2);

            mBall.increaseVelocity();
        }

        // Check for mBall colliding with second mBat
        if(RectF.intersects(mBatTwo.getRect(), mBall.getRect())) {
            mBall.setRandomXVelocity();
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mBatTwo.getRect().bottom + 2);

            mBall.increaseVelocity();
        }

        // Bounce the mBall back when it hits the bottom of screen
        if(mBall.getRect().bottom > mScreenY){
            mBall.reverseYVelocity();
            mBall.clearObstacleY(mScreenY - 2);

            mScore2++;
            mBall.reset(mScreenX, mScreenY);

            mPaused = true;
        }

        // Bounce the mBall back when it hits the top of screen
        if(mBall.getRect().top < 0){
            mBall.reverseYVelocity();
            mBall.clearObstacleY(12);
            mScore++;
            mBall.reset(mScreenX, mScreenY);
            mPaused = true;
        }

        // If the mBall hits left wall bounce
        if(mBall.getRect().left < 0){
            mBall.reverseXVelocity();
            mBall.clearObstacleX(2);
        }

        // If the mBall hits right wall bounce
        if(mBall.getRect().right > mScreenX){
            mBall.reverseXVelocity();
            mBall.clearObstacleX(mScreenX - 22);
        }
    }

    // Draw the newly updated scene
    public void draw() {

        // Make sure our drawing surface is valid or we crash
        if (mOurHolder.getSurface().isValid()) {

            // Draw everything here

            // Lock the mCanvas ready to draw
            mCanvas = mOurHolder.lockCanvas();

            // Clear the screen with my favorite color
            mCanvas.drawColor(Color.argb(255, 120, 197, 87));

            // Choose the brush color for drawing
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mBat
            mCanvas.drawRect(mBat.getRect(), mPaint);

            // Draw the mBat_Two
            mCanvas.drawRect(mBatTwo.getRect(), mPaint);

            // Draw the mBall
            mCanvas.drawRect(mBall.getRect(), mPaint);


            // Change the drawing color to white
            mPaint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mScore
            mPaint.setTextSize(40);
            mCanvas.drawText("Player 1: " + mScore + "   Player 2: " + mScore2, 10, 50, mPaint);

            // Draw everything to the screen
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    // If the Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    // If the Activity starts/restarts
    // start our thread.
    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }


    // The SurfaceView class implements onTouchListener
// So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                mPaused = false;

                // Is the touch on the right or left?
                if((motionEvent.getX() > mScreenX / 2) && (motionEvent.getY() > mScreenY / 2)){
                    mBat.setMovementState(mBat.RIGHT);
                }
                else if((motionEvent.getX() < mScreenX / 2) && (motionEvent.getY() > mScreenY / 2)){
                    mBat.setMovementState(mBat.LEFT);
                }

                if((motionEvent.getX() > mScreenX / 2) && (motionEvent.getY() < mScreenY / 2)){
                    mBatTwo.setMovementState(mBatTwo.RIGHT);
                }
                else if((motionEvent.getX() < mScreenX / 2) && (motionEvent.getY() < mScreenY / 2)){
                    mBatTwo.setMovementState(mBatTwo.LEFT);
                }
                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                mBat.setMovementState(mBat.STOPPED);
                mBatTwo.setMovementState(mBatTwo.STOPPED);
                break;
        }
        return true;
    }

}
