package me.aflak.libraries;

/**
 * Created by Omar on 11/07/2017.
 */

public class DialogAnimation {
    public final static int ENTER_FROM_BOTTOM=0, ENTER_FROM_TOP=1, ENTER_FROM_LEFT=2, ENTER_FROM_RIGHT=3;
    public final static int EXIT_TO_BOTTOM=0, EXIT_TO_TOP=1, EXIT_TO_LEFT=2, EXIT_TO_RIGHT=3;
    public final static int NO_ANIMATION=4;

    public static int getStyle(int enterAnimation, int exitAnimation){
        switch (enterAnimation){
            case ENTER_FROM_BOTTOM:
                switch (exitAnimation){
                    case EXIT_TO_BOTTOM:
                        return R.style.BottomBottomAnimation;
                    case EXIT_TO_TOP:
                        return R.style.BottomTopAnimation;
                    case NO_ANIMATION:
                        return R.style.EnterFromBottomAnimation;
                }
                break;
            case ENTER_FROM_TOP:
                switch (exitAnimation){
                    case EXIT_TO_BOTTOM:
                        return R.style.TopBottomAnimation;
                    case EXIT_TO_TOP:
                        return R.style.TopTopAnimation;
                    case NO_ANIMATION:
                        return R.style.EnterFromTopAnimation;
                }
                break;
            case ENTER_FROM_LEFT:
                switch (exitAnimation){
                    case EXIT_TO_LEFT:
                        return R.style.LeftLeftAnimation;
                    case EXIT_TO_RIGHT:
                        return R.style.LeftRightAnimation;
                    case NO_ANIMATION:
                        return R.style.EnterFromLeftAnimation;
                }
                break;
            case ENTER_FROM_RIGHT:
                switch (exitAnimation){
                    case EXIT_TO_LEFT:
                        return R.style.RightLeftAnimation;
                    case EXIT_TO_RIGHT:
                        return R.style.RightRightAnimation;
                    case NO_ANIMATION:
                        return R.style.EnterFromRightAnimation;
                }
                break;
            case NO_ANIMATION:
                switch (exitAnimation){
                    case EXIT_TO_BOTTOM:
                        return R.style.ExitToBottomAnimation;
                    case EXIT_TO_TOP:
                        return R.style.ExitToTopAnimation;
                    case EXIT_TO_LEFT:
                        return R.style.ExitToLeftAnimation;
                    case EXIT_TO_RIGHT:
                        return R.style.ExitToRightAnimation;
                }
                break;
        }
        return -1;
    }
}
