/*
 * Copyright (c) 2009 Kathryn Huxtable and Kenneth Orr.
 *
 * This file is part of the SeaGlass Pluggable Look and Feel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id$
 */
package com.seaglasslookandfeel.painter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import com.seaglasslookandfeel.painter.AbstractRegionPainter.PaintContext.CacheMode;

/**
 * SpinnerNextButtonPainter implementation.
 */
public final class SpinnerNextButtonPainter extends AbstractRegionPainter {
    public static enum Which {
        BACKGROUND_DISABLED,
        BACKGROUND_ENABLED,
        BACKGROUND_FOCUSED,
        BACKGROUND_PRESSED_FOCUSED,
        BACKGROUND_PRESSED,
        FOREGROUND_DISABLED,
        FOREGROUND_ENABLED,
        FOREGROUND_FOCUSED,
        FOREGROUND_PRESSED_FOCUSED,
        FOREGROUND_PRESSED,
    }

    private static final Color     OUTER_FOCUS_COLOR        = new Color(0x8072a5d2, true);
    private static final Color     INNER_FOCUS_COLOR        = new Color(0x73a4d1);

    private static final Color     DISABLED_TOP_INTERIOR    = new Color(0xeaebf1f7, true);
    private static final Color     DISABLED_BOTTOM_INTERIOR = new Color(0xdbe2e9f2, true);
    private static final Color     DISABLED_TOP_BORDER      = new Color(0x80a2c2ed, true);
    private static final Color     DISABLED_BOTTOM_BORDER   = new Color(0x807ea4d7, true);

    private static final Color     ENABLED_TOP_INTERIOR     = new Color(0xbccedf);
    private static final Color     ENABLED_BOTTOM_INTERIOR  = new Color(0x85abcf);
    private static final Color     ENABLED_TOP_BORDER       = new Color(0x4f7bbf);
    private static final Color     ENABLED_BOTTOM_BORDER    = new Color(0x4779bf);

    private static final Color     PRESSED_TOP_INTERIOR     = new Color(0xacbdd0);
    private static final Color     PRESSED_BOTTOM_INTERIOR  = new Color(0x6e92b6);
    private static final Color     PRESSED_TOP_BORDER       = new Color(0x4f7bbf);
    private static final Color     PRESSED_BOTTOM_BORDER    = new Color(0x4879bf);

    private static final Color     ENABLED_ARROW_COLOR  = new Color(0x000000);
    private static final Color     DISABLED_ARROW_COLOR = new Color(0x9ba8cf);

    private static final Insets    insets                   = new Insets(0, 0, 0, 0);
    private static final Dimension dimension                = new Dimension(22, 13);
    private static final CacheMode cacheMode                = CacheMode.FIXED_SIZES;
    private static final Double    maxH                     = 1.0;
    private static final Double    maxV                     = Double.POSITIVE_INFINITY;

    private Path2D                 path                     = new Path2D.Double();

    private Which                  state;
    private PaintContext           ctx;
    private boolean                focused;

    public SpinnerNextButtonPainter(Which state) {
        super();
        this.state = state;

        ctx = new PaintContext(insets, dimension, false, cacheMode, maxH, maxV);
        focused = (state == Which.BACKGROUND_FOCUSED || state == Which.BACKGROUND_PRESSED_FOCUSED);
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        switch (state) {
        case BACKGROUND_DISABLED:
            paintButtonDisabled(g, width, height);
            break;
        case BACKGROUND_ENABLED:
        case BACKGROUND_FOCUSED:
            paintButtonEnabled(g, width, height);
            break;
        case BACKGROUND_PRESSED_FOCUSED:
        case BACKGROUND_PRESSED:
            paintButtonPressed(g, width, height);
            break;
        case FOREGROUND_DISABLED:
            paintArrowDisabled(g, width, height);
            break;
        case FOREGROUND_ENABLED:
        case FOREGROUND_FOCUSED:
        case FOREGROUND_PRESSED_FOCUSED:
        case FOREGROUND_PRESSED:
            paintArrowEnabled(g, width, height);
            break;
        }
    }

    @Override
    protected PaintContext getPaintContext() {
        return ctx;
    }

    private void paintButtonDisabled(Graphics2D g, int width, int height) {
        paintButton(g, width, height, DISABLED_TOP_BORDER, DISABLED_BOTTOM_BORDER, DISABLED_TOP_INTERIOR, DISABLED_BOTTOM_INTERIOR);
    }

    private void paintButtonEnabled(Graphics2D g, int width, int height) {
        paintButton(g, width, height, ENABLED_TOP_BORDER, ENABLED_BOTTOM_BORDER, ENABLED_TOP_INTERIOR, ENABLED_BOTTOM_INTERIOR);
    }

    private void paintButtonPressed(Graphics2D g, int width, int height) {
        paintButton(g, width, height, PRESSED_TOP_BORDER, PRESSED_BOTTOM_BORDER, PRESSED_TOP_INTERIOR, PRESSED_BOTTOM_INTERIOR);
    }

    private void paintArrowDisabled(Graphics2D g, int width, int height) {
        paintArrow(g, width, height, DISABLED_ARROW_COLOR);
    }

    private void paintArrowEnabled(Graphics2D g, int width, int height) {
        paintArrow(g, width, height, ENABLED_ARROW_COLOR);
    }

    private void paintButton(Graphics2D g, int width, int height, Color topBorder, Color bottomBorder, Color topInterior,
        Color bottomInterior) {
        Shape s;

        if (focused) {
            s = setButtonShape(0, 0, width, height, 6);
            g.setColor(OUTER_FOCUS_COLOR);
            g.fill(s);

            s = setButtonShape(0, 1, width - 1, height - 1, 5);
            g.setColor(INNER_FOCUS_COLOR);
            g.fill(s);
        }

        s = setButtonShape(0, 2, width - 2, height - 2, 4);
        g.setPaint(decodeGradient(s, topBorder, bottomBorder));
        g.fill(s);

        s = setButtonShape(1, 3, width - 4, height - 4, 3);
        g.setPaint(decodeGradient(s, topInterior, bottomInterior));
        g.fill(s);
    }

    private void paintArrow(Graphics2D g, int width, int height, Color color) {
        Shape s = setArrowShape(width, height);
        g.setColor(color);
        g.fill(s);
    }

    private Shape setButtonShape(int left, int top, int width, int height, double arc) {
        int right = left + width;
        int bottom = top + height;
        path.reset();
        path.moveTo(left, top);
        path.lineTo(right - arc, top);
        path.quadTo(right, top, right, top + arc);
        path.lineTo(right, bottom);
        path.lineTo(left, bottom);
        path.closePath();
        return path;
    }
    
    private Shape setArrowShape(int left, int height) {
        int centerX = 8;
        int centerY = height / 2;
        path.reset();
        path.moveTo(centerX + 2, centerY);
        path.lineTo(centerX + 4, centerY + 3);
        path.lineTo(centerX, centerY + 3);
        path.closePath();
        return path;
    }

    private Paint decodeGradient(Shape s, Color color1, Color color2) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float) bounds.getX();
        float y = (float) bounds.getY();
        float w = (float) bounds.getWidth();
        float h = (float) bounds.getHeight();
        return decodeGradient((0.5f * w) + x, y, (0.5f * w) + x, h + y, new float[] { 0f, 1f }, new Color[] { color1, color2 });
    }
}
