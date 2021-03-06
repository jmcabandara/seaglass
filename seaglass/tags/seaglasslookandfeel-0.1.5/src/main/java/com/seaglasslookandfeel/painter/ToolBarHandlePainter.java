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
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import com.seaglasslookandfeel.painter.AbstractRegionPainter.PaintContext.CacheMode;
import com.seaglasslookandfeel.painter.util.ShapeUtil;
import com.seaglasslookandfeel.painter.util.ShapeUtil.CornerSize;
import com.seaglasslookandfeel.util.PlatformUtils;

/**
 * ToolBarPainter implementation.
 * 
 * Parts taken from Nimbus to draw drag handle.
 * 
 * @author Ken Orr
 * @author Modified by Kathryn Huxtable for SeaGlass
 */
public class ToolBarHandlePainter extends AbstractRegionPainter {
    public static enum Which {
        HANDLEICON_ENABLED,
    };

    private Color             macHandleColor = new Color(0xc8191919, true);

    private ButtonStateColors enabledColors  = new ButtonStateColors(new Color(0xfbfdfe), new Color(0xd6eaf9), new Color(0xd2e8f8),
                                                 new Color(0xf5fafd), 0.46f, 0.62f, new Color(0x88ade0), new Color(0x5785bf));

    private PaintContext      ctx;

    public ToolBarHandlePainter(Which state) {
        super();
        this.ctx = new PaintContext(CacheMode.FIXED_SIZES);
    }

    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        if (PlatformUtils.isMac()) {
            paintMacHandleIconEnabled(g, width, height);
        } else {
            paintNonMacHandleIconEnabled(g, width, height);
        }
    }

    protected PaintContext getPaintContext() {
        return ctx;
    }

    private void paintMacHandleIconEnabled(Graphics2D g, int width, int height) {
        g.setColor(macHandleColor);
        g.drawLine(4, 2, 4, height - 3);
        g.drawLine(6, 2, 6, height - 3);
    }

    private void paintNonMacHandleIconEnabled(Graphics2D g, int width, int height) {
        paintNonMacHandleIcon(g, width, height, enabledColors);
    }

    private void paintNonMacHandleIcon(Graphics2D g, int width, int height, ButtonStateColors colors) {
        Shape s = decodeNonMacHandleBorder(width, height);
        g.setPaint(decodeGradientBackground(s, colors.background1, colors.background2));
        g.fill(s);

        s = decodeNonMacHandleInside(width, height);
        g.setPaint(decodeGradientInner(s, colors.inner1, colors.inner2, colors.inner3, colors.inner4, colors.midpoint2, colors.midpoint3));
        g.fill(s);
    }

    private Shape decodeNonMacHandleBorder(int width, int height) {
        return ShapeUtil.createRoundRectangle(4, 2, width - 4, height - 4, CornerSize.ROUND_WIDTH);
    }

    private Shape decodeNonMacHandleInside(int width, int height) {
        return ShapeUtil.createRoundRectangle(5, 3, width - 6, height - 6, CornerSize.ROUND_WIDTH);
    }

    /**
     * Create the gradient for the background of the button. This creates the
     * border.
     * 
     * @param s
     * @param color1
     * @param color2
     * @return
     */
    private Paint decodeGradientBackground(Shape s, Color color1, Color color2) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float) bounds.getX();
        float y = (float) bounds.getY();
        float w = (float) bounds.getWidth();
        float h = (float) bounds.getHeight();
        return decodeGradient(x, (0.5f * h) + y, x + w, (0.5f * h) + y, new float[] { 0f, 1f }, new Color[] { color1, color2 });
    }

    /**
     * Create the gradient for the shine at the bottom of the button.
     * 
     * @param color1
     * @param color2
     * @param color3
     *            TODO
     * @param color4
     *            TODO
     * @param midpoint2
     * @param midpoint3
     *            TODO
     */
    private Paint decodeGradientInner(Shape s, Color color1, Color color2, Color color3, Color color4, float midpoint2, float midpoint3) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float) bounds.getX();
        float y = (float) bounds.getY();
        float w = (float) bounds.getWidth();
        float h = (float) bounds.getHeight();
        return decodeGradient(x, (0.5f * h) + y, x + w, (0.5f * h) + y, new float[] { 0f, midpoint2, midpoint3, 1f }, new Color[] {
            color1,
            color2,
            color3,
            color4 });
    }

    /**
     * A set of colors to use for the button.
     */
    public class ButtonStateColors {

        public Color inner1;
        public Color inner2;
        public Color inner3;
        public Color inner4;
        public float midpoint2;
        public float midpoint3;
        public Color background1;
        public Color background2;

        public ButtonStateColors(Color inner1, Color inner2, Color inner3, Color inner4, float midpoint2, float midpoint3,
            Color background1, Color background2) {
            this.inner1 = inner1;
            this.inner2 = inner2;
            this.inner3 = inner3;
            this.inner4 = inner4;
            this.midpoint2 = midpoint2;
            this.midpoint3 = midpoint3;
            this.background1 = background1;
            this.background2 = background2;
        }
    }
}
