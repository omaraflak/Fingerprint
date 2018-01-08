package me.aflak.libraries.dialog;

import me.aflak.libraries.R;

/**
 * Created by Omar on 11/07/2017.
 */

public class DialogAnimation {
    public enum Enter {
        BOTTOM,
        TOP,
        LEFT,
        RIGHT,
        APPEAR
    }

    public enum Exit {
        BOTTOM,
        TOP,
        LEFT,
        RIGHT,
        DISAPPEAR
    }

    public static int getStyle(Enter enterAnimation, Exit exitAnimation){
        switch (enterAnimation){
            case BOTTOM:
                switch (exitAnimation){
                    case BOTTOM:
                        return R.style.BottomBottomAnimation;
                    case TOP:
                        return R.style.BottomTopAnimation;
                    case DISAPPEAR:
                        return R.style.EnterFromBottomAnimation;
                }
                break;
            case TOP:
                switch (exitAnimation){
                    case BOTTOM:
                        return R.style.TopBottomAnimation;
                    case TOP:
                        return R.style.TopTopAnimation;
                    case DISAPPEAR:
                        return R.style.EnterFromTopAnimation;
                }
                break;
            case LEFT:
                switch (exitAnimation){
                    case LEFT:
                        return R.style.LeftLeftAnimation;
                    case RIGHT:
                        return R.style.LeftRightAnimation;
                    case DISAPPEAR:
                        return R.style.EnterFromLeftAnimation;
                }
                break;
            case RIGHT:
                switch (exitAnimation){
                    case LEFT:
                        return R.style.RightLeftAnimation;
                    case RIGHT:
                        return R.style.RightRightAnimation;
                    case DISAPPEAR:
                        return R.style.EnterFromRightAnimation;
                }
                break;
            case APPEAR:
                switch (exitAnimation){
                    case BOTTOM:
                        return R.style.ExitToBottomAnimation;
                    case TOP:
                        return R.style.ExitToTopAnimation;
                    case LEFT:
                        return R.style.ExitToLeftAnimation;
                    case RIGHT:
                        return R.style.ExitToRightAnimation;
                }
                break;
        }
        return -1;
    }
}
