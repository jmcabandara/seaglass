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
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import com.seaglasslookandfeel.painter.AbstractRegionPainter.PaintContext.CacheMode;

/**
 * PopupMenuSeparatorPainter implementation.
 * 
 * Based on Nimbus's PopupMenuSepatorPainter.
 */
public final class PopupMenuSeparatorPainter extends AbstractRegionPainter {
    public static enum Which {
        BACKGROUND_ENABLED
    }

    private Which                  state;
    private PaintContext           ctx;

    private Rectangle2D            rect      = new Rectangle2D.Float(0, 0, 0, 0);

    private Color                  color1    = new Color(0xdddddd);

    private static final Insets    insets    = new Insets(1, 1, 1, 1);
    private static final Dimension dimension = new Dimension(3, 3);
    private static final CacheMode cacheMode = CacheMode.NO_CACHING;
    private static final Double    maxH      = 1.0;
    private static final Double    maxV      = 1.0;

    public PopupMenuSeparatorPainter(Which state) {
        super();
        this.state = state;
        this.ctx = new PaintContext(insets, dimension, true, cacheMode, maxH, maxV);
    }

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        switch (state) {
        case BACKGROUND_ENABLED:
            paintBackgroundEnabled(g, width, height);
            break;
        }
    }

    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }

    private void paintBackgroundEnabled(Graphics2D g, int width, int height) {
        rect = decodeRect1(width, height);
        g.setPaint(color1);
        g.fill(rect);

    }

    private Rectangle2D decodeRect1(int width, int height) {
        rect.setRect(0, height / 2, width, 1);
        return rect;
    }
}
