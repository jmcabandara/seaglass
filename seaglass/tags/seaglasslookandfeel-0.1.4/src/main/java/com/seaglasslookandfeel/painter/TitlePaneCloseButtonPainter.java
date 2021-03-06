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

import javax.swing.JComponent;

import com.seaglasslookandfeel.painter.AbstractRegionPainter.PaintContext.CacheMode;

/**
 * Title pane close button implementation.
 */
public final class TitlePaneCloseButtonPainter extends AbstractRegionPainter {
    public static enum Which {
        BACKGROUND_DISABLED,
        BACKGROUND_ENABLED,
        BACKGROUND_MOUSEOVER,
        BACKGROUND_MODIFIED,
        BACKGROUND_MODIFIED_MOUSEOVER,
        BACKGROUND_PRESSED,
        BACKGROUND_ENABLED_WINDOWNOTFOCUSED,
        BACKGROUND_MODIFIED_WINDOWNOTFOCUSED,
        BACKGROUND_PRESSED_WINDOWNOTFOCUSED,
    }

    private static final ButtonColors enabled       = new ButtonColors(new Color(0x16ffffff, true), new Color(0x4cffffff, true), new Color(
                                                        0x66000000, true), new Color(0x33ffffff, true), new Color(0, true), new Color(0,
                                                        true), new Color(0x99000000, true), new Color(0x99ffffff, true));
    private static final ButtonColors modified      = new ButtonColors(new Color(0x16ffffff, true), new Color(0x4cffffff, true), new Color(
                                                        0x66000000, true), new Color(0x33ffffff, true), new Color(0, true), new Color(0,
                                                        true), new Color(0x99000000, true), new Color(0x535353));
    private static final ButtonColors hover         = new ButtonColors(new Color(0xf39493), new Color(0xf7a2a0),
                                                        new Color(0x9e5b0000, true), new Color(0x33ffffff, true), new Color(0xda5452),
                                                        new Color(0xd94645), new Color(0x270908), new Color(0xffffff));
    private static final ButtonColors hoverModified = new ButtonColors(new Color(0xf39493), new Color(0xf7a2a0),
                                                        new Color(0x9e5b0000, true), new Color(0x33ffffff, true), new Color(0xda5452),
                                                        new Color(0xd94645), new Color(0x270908), new Color(0x535353));
    private static final ButtonColors pressed       = new ButtonColors(new Color(0xefa53634, true), new Color(0xf4bb6765, true), new Color(
                                                        0x66000000, true), new Color(0x33ffffff, true), new Color(0xe8e2302d, true),
                                                        new Color(0xe8e2302d, true), new Color(0xf4551211, true), new Color(0xfcfce9e9,
                                                            true));

    private MyPath2D                  path          = new MyPath2D();

    private Which                     state;
    private PaintContext              ctx;

    public TitlePaneCloseButtonPainter(Which state) {
        super();
        this.state = state;
        ctx = new PaintContext(new Insets(0, 0, 0, 0), new Dimension(43, 18), false, CacheMode.FIXED_SIZES, 1.0, 1.0);
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        switch (state) {
        case BACKGROUND_DISABLED:
        case BACKGROUND_ENABLED:
        case BACKGROUND_ENABLED_WINDOWNOTFOCUSED:
            paintCloseEnabled(g, c, width, height);
            break;
        case BACKGROUND_MOUSEOVER:
            paintCloseHover(g, c, width, height);
            break;
        case BACKGROUND_MODIFIED:
        case BACKGROUND_MODIFIED_WINDOWNOTFOCUSED:
            paintCloseModified(g, c, width, height);
            break;
        case BACKGROUND_MODIFIED_MOUSEOVER:
            paintCloseHoverModified(g, c, width, height);
            break;
        case BACKGROUND_PRESSED:
        case BACKGROUND_PRESSED_WINDOWNOTFOCUSED:
            paintClosePressed(g, c, width, height);
            break;
        }
    }

    @Override
    protected PaintContext getPaintContext() {
        return ctx;
    }

    private void paintCloseEnabled(Graphics2D g, JComponent c, int width, int height) {
        paintClose(g, c, width, height, enabled);
    }

    private void paintCloseHover(Graphics2D g, JComponent c, int width, int height) {
        paintClose(g, c, width, height, hover);
    }

    private void paintCloseModified(Graphics2D g, JComponent c, int width, int height) {
        paintClose(g, c, width, height, modified);
    }

    private void paintCloseHoverModified(Graphics2D g, JComponent c, int width, int height) {
        paintClose(g, c, width, height, hoverModified);
    }

    private void paintClosePressed(Graphics2D g, JComponent c, int width, int height) {
        paintClose(g, c, width, height, pressed);
    }

    private void paintClose(Graphics2D g, JComponent c, int width, int height, ButtonColors colors) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape s = decodeInterior(width, height);
        g.setPaint(decodeCloseGradient(g, s, colors.interiorTop, colors.interiorBottom));
        g.fill(s);
        s = decodeEdge(width, height);
        g.setColor(colors.edge);
        g.draw(s);
        s = decodeShadow(width, height);
        g.setColor(colors.shadow);
        g.draw(s);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(colors.top);
        g.drawLine(0, 0, width - 2, 0);
        g.setColor(colors.left);
        g.drawLine(0, 1, 0, height - 2);
        s = decodeMarkInterior(width, height);
        g.setColor(colors.markInterior);
        g.fill(s);
        s = decodeMarkBorder(width, height);
        g.setColor(colors.markBorder);
        g.draw(s);
    }

    private Paint decodeCloseGradient(Graphics2D g, Shape s, Color top, Color bottom) {
        Rectangle r = s.getBounds();
        int width = r.width;
        int height = r.height;
        return decodeGradient(r.x + width / 2, r.y, r.x + width / 2, r.y + height - 1, new float[] { 0f, 1f }, new Color[] { top, bottom });
    }

    private Shape decodeInterior(int width, int height) {
        path.reset();
        path.moveTo(1, 1);
        path.lineTo(width - 2, 1);
        path.lineTo(width - 2, height - 3);
        path.lineTo(width - 3, height - 2);
        path.lineTo(1, height - 2);
        path.closePath();
        return path;
    }

    private Shape decodeEdge(int width, int height) {
        path.reset();
        path.moveTo(width - 2, 0);
        path.lineTo(width - 2, height - 4);
        path.lineTo(width - 4, height - 2);
        path.lineTo(0, height - 2);
        return path;
    }

    private Shape decodeShadow(int width, int height) {
        path.reset();
        path.moveTo(width - 1, 0);
        path.lineTo(width - 1, height - 4);
        path.lineTo(width - 4, height - 1);
        path.lineTo(0, height - 1);
        return path;
    }

    private Shape decodeMarkBorder(int width, int height) {
        int left = (width - 3) / 2 - 5;
        int top = (height - 2) / 2 - 5;
        path.reset();
        path.moveTo(left + 1, top + 0);
        path.lineTo(left + 3, top + 0);
        path.pointAt(left + 4, top + 1);
        path.pointAt(left + 5, top + 2);
        path.pointAt(left + 6, top + 1);
        path.moveTo(left + 7, top + 0);
        path.lineTo(left + 9, top + 0);
        path.pointAt(left + 10, top + 1);
        path.pointAt(left + 9, top + 2);
        path.pointAt(left + 8, top + 3);
        path.moveTo(left + 7, top + 4);
        path.lineTo(left + 7, top + 5);
        path.pointAt(left + 8, top + 6);
        path.pointAt(left + 9, top + 7);
        path.pointAt(left + 10, top + 8);
        path.moveTo(left + 9, top + 9);
        path.lineTo(left + 7, top + 9);
        path.pointAt(left + 6, top + 8);
        path.pointAt(left + 5, top + 7);
        path.pointAt(left + 4, top + 8);
        path.moveTo(left + 3, top + 9);
        path.lineTo(left + 1, top + 9);
        path.pointAt(left + 0, top + 8);
        path.pointAt(left + 1, top + 7);
        path.pointAt(left + 2, top + 6);
        path.moveTo(left + 3, top + 5);
        path.lineTo(left + 3, top + 4);
        path.pointAt(left + 2, top + 3);
        path.pointAt(left + 1, top + 2);
        path.pointAt(left + 0, top + 1);
        return path;
    }

    private Shape decodeMarkInterior(int width, int height) {
        int left = (width - 3) / 2 - 5;
        int top = (height - 2) / 2 - 5;
        path.reset();
        path.moveTo(left + 1, top + 1);
        path.lineTo(left + 4, top + 1);
        path.lineTo(left + 5, top + 3);
        path.lineTo(left + 7, top + 1);
        path.lineTo(left + 10, top + 1);
        path.lineTo(left + 7, top + 4);
        path.lineTo(left + 7, top + 5);
        path.lineTo(left + 10, top + 9);
        path.lineTo(left + 6, top + 8);
        path.lineTo(left + 5, top + 6);
        path.lineTo(left + 4, top + 9);
        path.lineTo(left + 0, top + 9);
        path.lineTo(left + 4, top + 5);
        path.lineTo(left + 4, top + 4);
        path.closePath();
        return path;
    }

    private static class ButtonColors {
        public Color top;
        public Color left;
        public Color edge;
        public Color shadow;
        public Color interiorTop;
        public Color interiorBottom;
        public Color markBorder;
        public Color markInterior;

        public ButtonColors(Color top, Color left, Color edge, Color shadow, Color interiorTop, Color interiorBottom, Color markBorder,
            Color markInterior) {
            this.top = top;
            this.left = left;
            this.edge = edge;
            this.shadow = shadow;
            this.interiorTop = interiorTop;
            this.interiorBottom = interiorBottom;
            this.markBorder = markBorder;
            this.markInterior = markInterior;
        }

    }
    
    private static class MyPath2D extends Path2D.Double {
        public void pointAt(int x, int y) {
            moveTo(x, y);
            lineTo(x, y);
        }
    }
}
