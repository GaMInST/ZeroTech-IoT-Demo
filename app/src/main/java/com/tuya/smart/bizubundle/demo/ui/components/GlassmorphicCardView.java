package com.tuya.smart.bizubundle.demo.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.tuya.smart.bizubundle.demo.R;

/**
 * Custom glassmorphic card view with blur effects and neon glow
 * Implements the modern design system with glassmorphism effects
 */
public class GlassmorphicCardView extends CardView {

    private Paint glowPaint;
    private Paint borderPaint;
    private float glowRadius = 8f;
    private int glowColor;
    private int borderColor;
    private float cornerRadius;
    private boolean showGlow = true;

    public GlassmorphicCardView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public GlassmorphicCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GlassmorphicCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Read custom attributes
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GlassmorphicCardView);
            glowRadius = ta.getDimension(R.styleable.GlassmorphicCardView_glowRadius, 8f);
            glowColor = ta.getColor(R.styleable.GlassmorphicCardView_glowColor,
                    getResources().getColor(R.color.glass_border));
            borderColor = ta.getColor(R.styleable.GlassmorphicCardView_borderColor,
                    getResources().getColor(R.color.glass_border));
            showGlow = ta.getBoolean(R.styleable.GlassmorphicCardView_showGlow, true);
            ta.recycle();
        } else {
            glowColor = getResources().getColor(R.color.glass_border);
            borderColor = getResources().getColor(R.color.glass_border);
        }

        // Initialize paints
        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(2f);
        glowPaint.setColor(glowColor);
        glowPaint.setShadowLayer(glowRadius, 0, 0, glowColor);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1f);
        borderPaint.setColor(borderColor);

        // Set default card properties
        setCardBackgroundColor(getResources().getColor(R.color.glass_primary));
        setRadius(getResources().getDimension(R.dimen.radius_large));
        setCardElevation(getResources().getDimension(R.dimen.elevation_medium));

        // Enable hardware acceleration for better performance
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (showGlow) {
            // Draw glow effect
            Path glowPath = new Path();
            RectF rect = new RectF(glowRadius, glowRadius,
                    getWidth() - glowRadius, getHeight() - glowRadius);
            glowPath.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);
            canvas.drawPath(glowPath, glowPaint);
        }

        // Draw border
        Path borderPath = new Path();
        RectF borderRect = new RectF(1, 1, getWidth() - 1, getHeight() - 1);
        borderPath.addRoundRect(borderRect, cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.drawPath(borderPath, borderPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cornerRadius = getRadius();
    }

    // Public methods for customization
    public void setGlowRadius(float radius) {
        this.glowRadius = radius;
        glowPaint.setShadowLayer(glowRadius, 0, 0, glowColor);
        invalidate();
    }

    public void setGlowColor(int color) {
        this.glowColor = color;
        glowPaint.setColor(glowColor);
        glowPaint.setShadowLayer(glowRadius, 0, 0, glowColor);
        invalidate();
    }

    public void setBorderColor(int color) {
        this.borderColor = color;
        borderPaint.setColor(borderColor);
        invalidate();
    }

    public void setShowGlow(boolean show) {
        this.showGlow = show;
        invalidate();
    }

    public void setGlowIntensity(float intensity) {
        glowPaint.setShadowLayer(glowRadius * intensity, 0, 0, glowColor);
        invalidate();
    }
}
