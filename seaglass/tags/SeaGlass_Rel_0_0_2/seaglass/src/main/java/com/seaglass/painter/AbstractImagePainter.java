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
package com.seaglass.painter;

import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * An abstraction of an image painter, using AbstractRegionPainter and an image
 * resource.
 * 
 * @author Kathryn Huxtable
 */
public abstract class AbstractImagePainter extends AbstractRegionPainter {

    private static final String IMAGE_DIR = "/com/seaglass/resources/images";

    private PaintContext        ctx;

    protected ImageIcon         image;

    public AbstractImagePainter(PaintContext ctx, int state) {
        super();
        this.ctx = ctx;
        String imageName = getImageName(state);
        if (imageName == null) {
            throw new RuntimeException("No file was found for the state " + state);
        }

        image = getImage(imageName);
    }

    protected ImageIcon getImage(String imageName) {
        StringBuilder imagePath = new StringBuilder(IMAGE_DIR);
        imagePath.append('/');
        imagePath.append(imageName);
        imagePath.append(".png");

        return new ImageIcon(AbstractImagePainter.class.getResource(imagePath.toString()));
    }

    protected abstract String getImageName(int state);

    @Override
    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        image.paintIcon(c, g, 0, 0);
    }

    protected Object[] getExtendedCacheKeys(JComponent c) {
        Object[] extendedCacheKeys = new Object[] {};
        return extendedCacheKeys;
    }

    @Override
    protected final PaintContext getPaintContext() {
        return ctx;
    }
}
