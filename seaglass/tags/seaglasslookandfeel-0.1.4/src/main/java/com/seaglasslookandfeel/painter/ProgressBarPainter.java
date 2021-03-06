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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.seaglasslookandfeel.effect.Effect;
import com.seaglasslookandfeel.effect.SeaGlassDropShadowEffect;
import com.seaglasslookandfeel.painter.AbstractRegionPainter.PaintContext.CacheMode;

/**
 * ProgressBarPainter implementation.
 */
public final class ProgressBarPainter extends AbstractRegionPainter {
    public static enum Which {
        BACKGROUND_ENABLED,
        BACKGROUND_DISABLED,

        FOREGROUND_ENABLED,
        FOREGROUND_ENABLED_FINISHED,
        FOREGROUND_ENABLED_INDETERMINATE,
        FOREGROUND_DISABLED,
        FOREGROUND_DISABLED_FINISHED,
        FOREGROUND_DISABLED_INDETERMINATE,
    }

    private static final Color     shadowColor            = Color.black;
    private static final Effect    dropShadow             = new SeaGlassDropShadowEffect();
    private static final Color     INNER_SHADOW_COLOR1    = new Color(0x20000000, true);
    private static final Color     INNER_SHADOW_COLOR2    = new Color(0x10000000, true);

    private static final Color     disabledTrack1         = new Color(0x803f76bf, true);
    private static final Color     disabledTrack2         = new Color(0x804076bf, true);
    private static final Color     disabledTrackInterior  = new Color(0x80ffffff, true);

    private static final Color     enabledTrack1          = new Color(0x3f76bf);
    private static final Color     enabledTrack2          = new Color(0x4076bf);
    private static final Color     enabledTrackInterior   = new Color(0xffffff);

    private static final Color     disabledBar1           = new Color(0x80bccedf, true);
    private static final Color     disabledBar2           = new Color(0x807fa7cd, true);
    private static final Color     disabledBar3           = new Color(0x8082b0d6, true);
    private static final Color     disabledBar4           = new Color(0x80b0daf6, true);
    private static final Color     disabledBarEnd         = new Color(0x804076bf, true);

    private static final Color     disabledIndeterminate1 = new Color(0x80fbfdfe, true);
    private static final Color     disabledIndeterminate2 = new Color(0x80d6eaf9, true);
    private static final Color     disabledIndeterminate3 = new Color(0x80d2e8f8, true);
    private static final Color     disabledIndeterminate4 = new Color(0x80f5fafd, true);

    private static final Color     enabledBar1            = new Color(0xbccedf);
    private static final Color     enabledBar2            = new Color(0x7fa7cd);
    private static final Color     enabledBar3            = new Color(0x82b0d6);
    private static final Color     enabledBar4            = new Color(0xb0daf6);
    private static final Color     enabledBarEnd          = new Color(0x4076bf);

    private static final Color     indeterminate1         = new Color(0xfbfdfe);
    private static final Color     indeterminate2         = new Color(0xd6eaf9);
    private static final Color     indeterminate3         = new Color(0xd2e8f8);
    private static final Color     indeterminate4         = new Color(0xf5fafd);

    private static final Insets    insets                 = new Insets(0, 0, 0, 0);
    private static final Dimension bgDimension            = new Dimension(19, 19);
    private static final Dimension fgDimension            = new Dimension(24, 13);

    private Rectangle2D            rect                   = new Rectangle2D.Double();
    private RoundRectangle2D       roundRect              = new RoundRectangle2D.Double();
    private Path2D                 path                   = new Path2D.Double();

    private Which                  state;
    private PaintContext           ctx;

    public ProgressBarPainter(Which state) {
        super();
        this.state = state;

        Dimension dim = (state == Which.BACKGROUND_DISABLED || state == Which.BACKGROUND_ENABLED) ? bgDimension : fgDimension;
        ctx = new PaintContext(insets, dim, false, CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        switch (state) {
        case BACKGROUND_ENABLED:
            paintTrackEnabled(g, width, height);
            break;
        case BACKGROUND_DISABLED:
            paintTrackDisabled(g, width, height);
            break;
        case FOREGROUND_ENABLED:
            paintBarEnabled(g, width, height, false);
            break;
        case FOREGROUND_ENABLED_FINISHED:
            paintBarEnabled(g, width, height, true);
            break;
        case FOREGROUND_ENABLED_INDETERMINATE:
            paintIndeterminateEnabled(g, width, height);
            break;
        case FOREGROUND_DISABLED:
            paintBarDisabled(g, width, height, false);
            break;
        case FOREGROUND_DISABLED_FINISHED:
            paintBarDisabled(g, width, height, true);
            break;
        case FOREGROUND_DISABLED_INDETERMINATE:
            paintIndeterminateDisabled(g, width, height);
            break;
        }
    }

    @Override
    protected PaintContext getPaintContext() {
        return ctx;
    }

    private void paintTrackDisabled(Graphics2D g, int width, int height) {
        paintTrack(g, width, height, disabledTrack1, disabledTrack2, disabledTrackInterior);
    }

    private void paintTrackEnabled(Graphics2D g, int width, int height) {
        paintTrack(g, width, height, enabledTrack1, enabledTrack2, enabledTrackInterior);
    }

    private void paintBarDisabled(Graphics2D g, int width, int height, boolean isFinished) {
        paintBar(g, width, height, isFinished, disabledBar1, disabledBar2, disabledBar3, disabledBar4, disabledBarEnd);
    }

    private void paintBarEnabled(Graphics2D g, int width, int height, boolean isFinished) {
        paintBar(g, width, height, isFinished, enabledBar1, enabledBar2, enabledBar3, enabledBar4, enabledBarEnd);
    }

    private void paintIndeterminateDisabled(Graphics2D g, int width, int height) {
        paintIndeterminate(g, width, height, disabledBar1, disabledBar2, disabledBar3, disabledBar4, disabledIndeterminate1,
            disabledIndeterminate2, disabledIndeterminate3, disabledIndeterminate4);
    }

    private void paintIndeterminateEnabled(Graphics2D g, int width, int height) {
        paintIndeterminate(g, width, height, enabledBar1, enabledBar2, enabledBar3, enabledBar4, indeterminate1, indeterminate2,
            indeterminate3, indeterminate4);
    }

    private void paintTrack(Graphics2D g, int width, int height, Color color1, Color color2, Color colorInterior) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape s = decodeTrackBorder(width, height);
        g.drawImage(createDropShadowImage(s, width, height), 0, 0, null);
        g.setPaint(decodeTrackGradient(s, color1, color2));
        g.draw(s);

        s = decodeTrackInterior(width, height);
        g.setColor(colorInterior);
        g.fill(s);

        s = decodeTrackInternalShadowBorder2(width, height);
        g.setColor(INNER_SHADOW_COLOR2);
        g.draw(s);
        s = decodeTrackInternalShadowBorder1(width, height);
        g.setColor(INNER_SHADOW_COLOR1);
        g.draw(s);
    }

    private void paintBar(Graphics2D g, int width, int height, boolean isFinished, Color color1, Color color2, Color color3, Color color4,
        Color colorEnd) {
        Shape s = decodeBar(width, height);
        g.setPaint(decodeBarGradient(s, color1, color2, color3, color4));
        g.fill(s);

        if (!isFinished) {
            g.setColor(colorEnd);
            g.drawLine(width - 1, 0, width - 1, height - 1);
        }
    }

    private void paintIndeterminate(Graphics2D g, int width, int height, Color dark1, Color dark2, Color dark3, Color dark4, Color light1,
        Color light2, Color light3, Color light4) {
        Shape s = decodeIndeterminatePathLight(width, height);
        g.setPaint(decodeBarGradient(s, light1, light2, light3, light4));
        g.fill(s);
        s = decodeIndeterminateDark(width, height);
        g.setPaint(decodeBarGradient(s, dark1, dark2, dark3, dark4));
        g.fill(s);
    }

    /**
     * Create a drop shadow image.
     * 
     * @param s
     *            the shape to use as the shade.
     */
    private BufferedImage createDropShadowImage(Shape s, int width, int height) {
        BufferedImage bimage = SeaGlassDropShadowEffect.createBufferedImage(width, height, true);
        Graphics2D gbi = bimage.createGraphics();
        gbi.setColor(shadowColor);
        gbi.fill(s);
        return dropShadow.applyEffect(bimage, null, width, height);
    }

    private Shape decodeTrackInternalShadowBorder1(int width, int height) {
        path.reset();
        path.moveTo(4, (height - 5) / 2 - 1);
        path.quadTo(5, 3, 8, 3);
        path.lineTo(width - 11, 3);
        path.quadTo(width - 5, 3, width - 5, (height - 5) / 2 - 1);
        return path;
    }

    private Shape decodeTrackInternalShadowBorder2(int width, int height) {
        path.reset();
        path.moveTo(4, (height - 5) / 2);
        path.quadTo(5, 4, 8, 4);
        path.lineTo(width - 11, 4);
        path.quadTo(width - 5, 4, width - 5, (height - 5) / 2);
        return path;
    }

    private Shape decodeTrackBorder(int width, int height) {
        roundRect.setRoundRect(2, 2, width - 5, height - 5, height - 4, height - 4);
        return roundRect;
    }

    private Shape decodeTrackInterior(int width, int height) {
        roundRect.setRoundRect(3, 3, width - 6, height - 6, height - 6, height - 6);
        return roundRect;
    }

    private Shape decodeBar(int width, int height) {
        rect.setRect(0, 0, width, height);
        return rect;
    }

    private Shape decodeIndeterminateDark(int width, int height) {
        double half = width / 2.0;
        path.reset();
        path.moveTo(0, 0);
        path.lineTo(3, 0);
        path.lineTo(half, height);
        path.lineTo(0, height);
        path.closePath();
        path.moveTo(half + 3, 0);
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.closePath();
        return path;
    }

    private Shape decodeIndeterminatePathLight(int width, int height) {
        double half = width / 2.0;
        path.reset();
        path.moveTo(3, 0);
        path.lineTo(half + 3, 0);
        path.lineTo(width, height);
        path.lineTo(half, height);
        path.closePath();
        return path;
    }

    private Paint decodeTrackGradient(Shape s, Color color1, Color color2) {
        Rectangle r = s.getBounds();
        int x = r.x + r.width / 2;
        int y1 = r.y;
        int y2 = r.y + r.height;
        return decodeGradient(x, y1, x, y2, new float[] { 0f, 1f }, new Color[] { color1, color2 });
    }

    private Paint decodeBarGradient(Shape s, Color color1, Color color2, Color color3, Color color4) {
        Rectangle r = s.getBounds();
        int x = r.x + r.width / 2;
        int y1 = r.y;
        int y2 = r.y + r.height;
        return decodeGradient(x, y1, x, y2, new float[] { 0f, 0.45f, 0.6f, 1f }, new Color[] { color1, color2, color3, color4 });
    }
}
