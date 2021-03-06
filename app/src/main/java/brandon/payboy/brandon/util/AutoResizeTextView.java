package brandon.payboy.brandon.util; /**
 *               DO WHAT YOU WANT TO PUBLIC LICENSE
 *                    Version 2, December 2004
 *
 * Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 *
 * Everyone is permitted to copy and distribute verbatim or modified
 * copies of this license document, and changing it is allowed as long
 * as the name is changed.
 *
 *            DO WHAT YOU WANT TO PUBLIC LICENSE
 *   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  0. You just DO WHAT YOU WANT TO.
 */

import android.content.Context;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Text view that auto adjusts text size to fit within the view.
 * If the text size equals the minimum text size and still does not
 * fit, append with an ellipsis.
 *
 * @author Chase Colburn
 * @since Apr 4, 2011
 */
public class AutoResizeTextView extends TextView {

    // Minimum text size for this text view
    public static final float MIN_TEXT_SIZE = 10;

    // Maximum text size for this text view - if it is 0, then the text acts
// like match_parent
    public static final float MAX_TEXT_SIZE = 0;

    // Our ellipse string
    private static final String mEllipsis = "...";

    // Text size that is set from code. This acts as a starting point for
// resizing
    private float mTextSize;

    // Lower bounds for text size
    private float mMinTextSize = MIN_TEXT_SIZE;

    // Max bounds for text size
    private float mMaxTextSize = MAX_TEXT_SIZE;

    // Text view line spacing multiplier
    private float mSpacingMult = 1.0f;

    // Text view additional line spacing
    private float mSpacingAdd = 0.0f;

    // Add ellipsis to text that overflows at the smallest text size
    private boolean mAddEllipsis = true;

    // Add ellipsis to text that overflows at the smallest text size
    private int heightLimit;
    private int widthLimit;

    // Default constructor override
    public AutoResizeTextView(Context context) {
        this(context, null);
    }

    // Default constructor when inflating from XML file
    public AutoResizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // Default constructor override
    public AutoResizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTextSize = getTextSize();
    }

    /**
     * When text changes resize the text size.
     */
    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        // if we are adding new chars to text
        if (before <= after && after != 1) {
            resizeText(true);
            // now we are deleting chars
        } else {
            resizeText(false);
        }
    }

    /**
     * Override the set text size to update our internal reference values
     */
    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mTextSize = getTextSize();
    }

    /**
     * Override the set text size to update our internal reference values
     */
    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        mTextSize = getTextSize();
    }

    /**
     * Override the set line spacing to update our internal reference values
     */
    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        mSpacingMult = mult;
        mSpacingAdd = add;
    }

    /**
     * Set the lower text size limit and invalidate the view
     *
     * @param minTextSize
     */
    public void setMinTextSize(float minTextSize) {
        mMinTextSize = minTextSize;
        requestLayout();
        invalidate();
    }

    /**
     * Return lower text size limit
     *
     * @return
     */
    public float getMinTextSize() {
        return mMinTextSize;
    }

    /**
     * Set flag to add ellipsis to text that overflows at the smallest text size
     *
     * @param addEllipsis
     */
    public void setAddEllipsis(boolean addEllipsis) {
        mAddEllipsis = addEllipsis;
    }

    /**
     * Return flag to add ellipsis to text that overflows at the smallest text
     * size
     *
     * @return
     */
    public boolean getAddEllipsis() {
        return mAddEllipsis;
    }

    /**
     * Get width and height limits
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (widthLimit == 0 && heightLimit == 0) {
            widthLimit = (right - left) - getCompoundPaddingLeft() - getCompoundPaddingRight();
            heightLimit = (bottom - top) - getCompoundPaddingBottom() - getCompoundPaddingTop();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    public void resizeText(boolean increase) {
        CharSequence text = getText();
        // Do not resize if the view does not have dimensions or there is no
        // text
        if (text == null || text.length() == 0 || heightLimit <= 0 || widthLimit <= 0 || mTextSize == 0) {
            return;
        }

        // Get the text view's paint object
        TextPaint textPaint = getPaint();

        // Get the required text height
        int textHeight = getTextHeight(text, textPaint, widthLimit, mTextSize);


        // If the text length is increased
        // Until we either fit within our text view or we had reached our min
        // text size, incrementally try smaller sizes
        if (increase) {
            while (textHeight > heightLimit && mTextSize > mMinTextSize) {
                mTextSize = Math.max(mTextSize - 2, mMinTextSize);
                textHeight = getTextHeight(text, textPaint, widthLimit, mTextSize);
            }
        }
//      text length has been decreased
        else {
//          if max test size is set then add it to while condition
            if (mMaxTextSize != 0) {
                while (textHeight < heightLimit && mTextSize <= mMaxTextSize) {
                    mTextSize = mTextSize + 2;
                    textHeight = getTextHeight(text, textPaint, widthLimit, mTextSize);
                }
            } else {
                while (textHeight < heightLimit) {
                    mTextSize = mTextSize + 2;
                    textHeight = getTextHeight(text, textPaint, widthLimit, mTextSize);
                }
            }
            mTextSize = textHeight > heightLimit ? mTextSize - 2 : mTextSize;
        }

        // If we had reached our minimum text size and still don't fit, append
        // an ellipsis
        if (mAddEllipsis && mTextSize == mMinTextSize && textHeight > heightLimit) {
            // Draw using a static layout
            TextPaint paint = new TextPaint(textPaint);
            StaticLayout layout = new StaticLayout(text, paint, widthLimit, Alignment.ALIGN_NORMAL, mSpacingMult,
                    mSpacingAdd, false);
            // Check that we have a least one line of rendered text
            if (layout.getLineCount() > 0) {
                // Since the line at the specific vertical position would be cut
                // off,
                // we must trim up to the previous line
                int lastLine = layout.getLineForVertical(heightLimit) - 1;
                // If the text would not even fit on a single line, clear it
                if (lastLine < 0) {
                    setText("");
                }
                // Otherwise, trim to the previous line and add an ellipsis
                else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    float ellipseWidth = paint.measureText(mEllipsis);

                    // Trim characters off until we have enough room to draw the
                    // ellipsis
                    while (widthLimit < lineWidth + ellipseWidth) {
                        lineWidth = paint.measureText(text.subSequence(start, --end + 1).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }

        // Some devices try to auto adjust line spacing, so force default line
        // spacing
        // and invalidate the layout as a side effect
        setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

    }

    // Set the text size of the text paint object and use a static layout to
// render text off screen before measuring
    private int getTextHeight(CharSequence source, TextPaint originalPaint, int width, float textSize) {
        // Update the text paint object
        TextPaint paint = new TextPaint(originalPaint);
        paint.setTextSize(textSize);
        // Measure using a static layout
        StaticLayout layout = new StaticLayout(source, paint, width, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd,
                true);
        return layout.getHeight();
    }

}