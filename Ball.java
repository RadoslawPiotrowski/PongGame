package com.example.pingpong;

import android.graphics.RectF;

import java.util.Random;

public class Ball {

    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;
    private float screenY;

    public Ball(int screenX, int screenY){

        // Make the mBall size relative to the screen resolution
        mBallWidth = screenX / 100;
        mBallHeight = mBallWidth;

    /*
        Start the ball travelling straight up
        at a quarter of the screen height per second
    */
        this.screenY = screenY;
        mYVelocity = this.screenY / 8;
        mXVelocity = mYVelocity / 2;

        // Initialize the Rect that represents the mBall
        mRect = new RectF();
    }

    // Give access to the Rect
    public RectF getRect(){
        return mRect;
    }

    // Change the position each frame
    public void update(long fps){
        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top - mBallHeight;
    }

    // Reverse the vertical heading
    public void reverseYVelocity(){
        mYVelocity = -mYVelocity;
    }

    // Reverse the horizontal heading
    public void reverseXVelocity(){
        mXVelocity = -mXVelocity;
    }

    public void setRandomXVelocity(){

        // Generate a random number either 0 or 1
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    // Speed up by 10%
// A score of over 20 is quite difficult
// Reduce or increase 10 to make this easier or harder
    public void increaseVelocity(double mSpeed){
        double mXAbsoluteVelocity = Math.abs(mXVelocity);
        double mYAbsoluteVelocity = Math.abs(mYVelocity);
        mXAbsoluteVelocity += mSpeed;
        mYAbsoluteVelocity += mSpeed;
        if (mXVelocity < 0){
            mXVelocity = (float) (mXAbsoluteVelocity * (-1));
        }
        else{
            mXVelocity = (float) (mXAbsoluteVelocity);
        }
        if (mYVelocity < 0){
            mYVelocity = (float) (mYAbsoluteVelocity * (-1));
        }
        else{
            mYVelocity = (float) (mYAbsoluteVelocity);
        }
    }

    public void clearObstacleY(float y){
        mRect.bottom = y;
        mRect.top = y - mBallHeight;
    }

    public void clearObstacleX(float x){
        mRect.left = x;
        mRect.right = x + mBallWidth;
    }

    public void reset(int x, int y, int direction){
        mRect.left = x / 2;
        mRect.top = y/2;
        mRect.right = x / 2 + mBallWidth;
        mRect.bottom = y/2;
        if (direction == 0){
            mYVelocity = -this.screenY / 8;
        }
        else{
            mYVelocity = this.screenY / 8;
        }
        mXVelocity = mYVelocity / 2;
    }


}
