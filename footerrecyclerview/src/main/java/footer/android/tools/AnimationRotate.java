package footer.android.tools;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


public class AnimationRotate {
    public AnimationRotate() {
        super();
    }

    public static void rotatebolowImage(ImageView imageView) {
        RotateAnimation ra = new RotateAnimation(0, 359,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);
        ra.setRepeatMode(Animation.RESTART);
        LinearInterpolator lir = new LinearInterpolator();
        ra.setInterpolator(lir);
        imageView.setAnimation(ra);
    }

}
