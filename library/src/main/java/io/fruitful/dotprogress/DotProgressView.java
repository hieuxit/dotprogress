package io.fruitful.dotprogress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hieuxit on 6/29/16.
 */

public class DotProgressView extends ImageView {

    public static final int AUTO = 0;
    public static final int MANUAL = 1;

    public static final int SCALE = 0;

    @IntDef({AUTO, MANUAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    @IntDef({SCALE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimMode {
    }

    /**
     * Mode is one of auto or manual.
     * Auto: View starts animation automatically after inflate
     * Manual: View does not start animation automatically. Want to animate call {@link #start()}
     */
    @Mode
    private int mode;

    /**
     * Build-in animation
     */
    @AnimMode
    private int animMode;

    /**
     * Dot color
     */
    private int color;

    /**
     * Current animated drawable
     */
    private AnimatedVectorDrawableCompat animatedDrawable;

    public DotProgressView(Context context) {
        this(context, null);
    }

    public DotProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DotProgressView,
                    defStyleAttr, 0);
            setColor(a.getColor(R.styleable.DotProgressView_dp_color, Color.WHITE));
            //noinspection WrongConstant
            setMode(a.getInt(R.styleable.DotProgressView_dp_mode, AUTO));
            //noinspection WrongConstant
            setAnimMode(a.getInt(R.styleable.DotProgressView_dp_anim_mode, SCALE));
            a.recycle();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Mode
    public int getMode() {
        return mode;
    }

    public void setMode(@Mode int mode) {
        this.mode = mode;
    }

    @AnimMode
    public int getAnimMode() {
        return animMode;
    }

    public void setAnimMode(@AnimMode int animMode) {
        this.animMode = animMode;
        AnimatedVectorDrawableCompat drawable = null;
        switch (animMode) {
            case SCALE:
                drawable = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.dot_scale);
                break;
        }
        // if is animating, save and auto play this new one
        boolean running = animatedDrawable == null ? (mode == AUTO) : (animatedDrawable.isRunning());
        // stop current animating if needed
        if (running && animatedDrawable != null) {
            animatedDrawable.stop();
        }

        animatedDrawable = drawable;
        setImageDrawable(animatedDrawable);
        if (running) {
            // We do not need to call start immediately
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    animatedDrawable.start();
                }
            }, 500);

        }
    }

    public int getColor() {
        return color;
    }

    /**
     * Tint the dot with {@code color}
     *
     * @param color Integer E.g 0xffff0000 - red, etc
     */
    public void setColor(int color) {
        this.color = color;
        if (animatedDrawable != null) {
            animatedDrawable.setTint(color);
        }
    }

    /**
     * Start animation
     */
    public void start() {
        if (animatedDrawable == null)
            throw new IllegalStateException("AnimatedDrawable must be set before calling start()");
        if (animatedDrawable.isRunning()) return;
        animatedDrawable.start();
    }

    /**
     * Stop animation
     */
    public void stop() {
        if (animatedDrawable == null)
            throw new IllegalStateException("AnimatedDrawable must be set before calling start()");
        if (animatedDrawable.isRunning()) {
            animatedDrawable.stop();
        }
    }

    /**
     * @return true if dot is animating and otherwise
     */
    public boolean isRunning() {
        if (animatedDrawable == null) return false;
        return animatedDrawable.isRunning();
    }
}
