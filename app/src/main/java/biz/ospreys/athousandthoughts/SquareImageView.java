package biz.ospreys.athousandthoughts;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class SquareImageView extends AppCompatImageView {
    public SquareImageView(Context context) { super(context); }
    public SquareImageView(Context context, AttributeSet attrs) { super(context, attrs); }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, widthSpec);
    }
}