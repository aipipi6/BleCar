package com.freelink.blecar.bean;

/**
 * Created by chenjun on 2018/8/1.
 */

public class RockerConfig {

    private boolean leftGravity;

    private boolean leftLockX;

    private boolean leftLockY;

    private boolean leftRecoverY;

    private boolean rightGravity;

    private boolean rightLockX;

    private boolean rightLockY;

    private boolean rightRecoverY;

    public RockerConfig() {
        leftGravity = false;
        leftLockX = false;
        leftLockY = false;
        leftRecoverY = true;
        rightGravity = false;
        rightLockX = false;
        rightLockY = false;
        rightRecoverY = true;
    }

    public boolean isLeftGravity() {
        return leftGravity;
    }

    public void setLeftGravity(boolean leftGravity) {
        this.leftGravity = leftGravity;
    }

    public boolean isLeftLockX() {
        return leftLockX;
    }

    public void setLeftLockX(boolean leftLockX) {
        this.leftLockX = leftLockX;
    }

    public boolean isLeftLockY() {
        return leftLockY;
    }

    public void setLeftLockY(boolean leftLockY) {
        this.leftLockY = leftLockY;
    }

    public boolean isLeftRecoverY() {
        return leftRecoverY;
    }

    public void setLeftRecoverY(boolean leftRecoverY) {
        this.leftRecoverY = leftRecoverY;
    }

    public boolean isRightGravity() {
        return rightGravity;
    }

    public void setRightGravity(boolean rightGravity) {
        this.rightGravity = rightGravity;
    }

    public boolean isRightLockX() {
        return rightLockX;
    }

    public void setRightLockX(boolean rightLockX) {
        this.rightLockX = rightLockX;
    }

    public boolean isRightLockY() {
        return rightLockY;
    }

    public void setRightLockY(boolean rightLockY) {
        this.rightLockY = rightLockY;
    }

    public boolean isRightRecoverY() {
        return rightRecoverY;
    }

    public void setRightRecoverY(boolean rightRecoverY) {
        this.rightRecoverY = rightRecoverY;
    }

}
