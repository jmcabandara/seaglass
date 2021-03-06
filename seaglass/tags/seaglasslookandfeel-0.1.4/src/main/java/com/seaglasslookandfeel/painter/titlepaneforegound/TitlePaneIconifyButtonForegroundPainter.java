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
package com.seaglasslookandfeel.painter.titlepaneforegound;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

public class TitlePaneIconifyButtonForegroundPainter {

    private static Color enabledBorder   = new Color(0x99000000, true);
    private static Color enabledInterior = new Color(0x99ffffff, true);
    private static Color hoverBorder     = new Color(0xe5101010, true);
    private static Color hoverInterior   = new Color(0xffffff);
    private static Color pressedBorder   = new Color(0xe50e0e0e, true);
    private static Color pressedInterior = new Color(0xe6e6e6);

    public void paintEnabled(Graphics2D g, JComponent c, int width, int height) {
        paint(g, c, width, height, enabledBorder, enabledInterior);
    }

    public void paintHover(Graphics2D g, JComponent c, int width, int height) {
        paint(g, c, width, height, hoverBorder, hoverInterior);
    }

    public void paintPressed(Graphics2D g, JComponent c, int width, int height) {
        paint(g, c, width, height, pressedBorder, pressedInterior);
    }

    private void paint(Graphics2D g, JComponent c, int width, int height, Color border, Color interior) {
        int top = (height - 2) / 2;
        int left = (width - 3) / 2 - 4;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setColor(interior);
        g.fillRect(left + 1, top + 1, 10, 3);

        g.setColor(border);
        g.drawLine(left + 1, top + 0, left + 9, top + 0);
        g.drawLine(left + 1, top + 4, left + 9, top + 4);
        g.drawLine(left + 0, top + 1, left + 0, top + 3);
        g.drawLine(left + 10, top + 1, left + 10, top + 3);
    }
}
