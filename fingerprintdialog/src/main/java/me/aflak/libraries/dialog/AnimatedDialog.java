package me.aflak.libraries.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Omar on 07/12/2017.
 */

@SuppressWarnings("unchecked")
public class AnimatedDialog<T extends AnimatedDialog> {
    protected Context context;
    protected String title;
    protected String message;

    protected boolean cancelOnTouchOutside;
    protected boolean cancelOnPressBack;
    protected boolean dimBackground;

    protected DialogAnimation.Enter enterAnimation;
    protected DialogAnimation.Exit exitAnimation;

    LayoutInflater layoutInflater;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    View dialogView;

    public AnimatedDialog(Context context){
        this.context = context;
        this.title = "";
        this.message = "";
        this.cancelOnTouchOutside = false;
        this.cancelOnPressBack = false;
        this.dimBackground = true;
        this.enterAnimation = DialogAnimation.Enter.APPEAR;
        this.exitAnimation = DialogAnimation.Exit.DISAPPEAR;
        this.layoutInflater = LayoutInflater.from(context);
        this.builder = new AlertDialog.Builder(context);
    }

    public T title(String title){
        this.title = title;
        return (T) this;
    }

    public T message(String message){
        this.message = message;
        return (T) this;
    }

    public T title(int resTitle){
        this.title = context.getResources().getString(resTitle);
        return (T) this;
    }

    public T message(int resMessage){
        this.message = context.getResources().getString(resMessage);
        return (T) this;
    }

    public T cancelOnTouchOutside(boolean cancelOnTouchOutside) {
        this.cancelOnTouchOutside = cancelOnTouchOutside;
        return (T) this;
    }

    public T cancelOnPressBack(boolean cancelOnPressBack){
        this.cancelOnPressBack = cancelOnPressBack;
        return (T) this;
    }

    public T dimBackground(boolean dimBackground){
        this.dimBackground = dimBackground;
        return (T) this;
    }

    public T enterAnimation(DialogAnimation.Enter enterAnimation){
        this.enterAnimation = enterAnimation;
        return (T) this;
    }

    public T exitAnimation(DialogAnimation.Exit exitAnimation){
        this.exitAnimation = exitAnimation;
        return (T) this;
    }
}
