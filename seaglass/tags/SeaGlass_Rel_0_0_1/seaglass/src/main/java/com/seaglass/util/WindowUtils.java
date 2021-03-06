/*
 * Copyright (c) 2009 Kathryn Huxtable and Kenneth Orr.
 *
 * This file is part of the Aqvavit Pluggable Look and Feel.
 *
 * Aqvavit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.

 * Aqvavit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Aqvavit.  If not, see
 *     <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package com.seaglass.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class WindowUtils {

    /**
     * Try's to make the given {@link Window} non-opqaue (transparent) across platforms and JREs. This method is not
     * guaranteed to succeed, and will fail silently if the given {@code Window} cannot be made non-opaque.
     * <p/>
     * This method is useful, for example, when creating a HUD style window that is semi-transparent, and thus doesn't
     * want the window background to be drawn.
     *
     * @param window the {@code Window} to make non-opaque.
     */
    public static void makeWindowNonOpaque(Window window) {
        // on the mac, simply setting the window's background color to be fully transparent makes the window non-opaque.
        window.setBackground(new Color(0, 0, 0, 0));
        // on non-mac platforms, try to use the facilities of Java 6 update 10.
        if (!PlatformUtils.isMac()) {
            quietlyTryToMakeWindowNonOqaque(window);
        }
    }

    /**
     * Trys to invoke {@code com.sun.awt.AWTUtilities.setWindowOpaque(window,false)} on the given {@link Window}. This
     * will only work when running with JRE 6 update 10 or higher. This method will silently fail if the method cannot
     * be invoked.
     *
     * @param window the {@code Window} to try and make non-opaque.
     */
    @SuppressWarnings("unchecked")
    private static void quietlyTryToMakeWindowNonOqaque(Window window) {
        try {
            Class clazz = Class.forName("com.sun.awt.AWTUtilities");
            Method method = clazz.getMethod("setWindowOpaque", java.awt.Window.class, Boolean.TYPE);
            method.invoke(clazz, window, false);
        } catch (Exception e) {
            // silently ignore this exception.
        }
    }

    public static WindowFocusListener createAndInstallRepaintWindowFocusListener(Window window) {
        // create a WindowFocusListener that repaints the window on focus changes.
        WindowFocusListener windowFocusListener = new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent e) {
                e.getWindow().repaint();
            }

            public void windowLostFocus(WindowEvent e) {
                e.getWindow().repaint();
            }
        };

        window.addWindowFocusListener(windowFocusListener);

        return windowFocusListener;
    }

    /**
     * {@code true} if the given {@link Component}'s parent {@link Window} is currently active (focused).
     *
     * @param component the {@code Component} to check the parent {@code Window}'s focus for.
     * @return {@code true} if the given {@code Component}'s parent {@code Window} is currently active.
     */
    public static boolean isParentWindowFocused(Component component) {
        Window window = SwingUtilities.getWindowAncestor(component);
        return window != null && window.isFocused();
    }

    /**
     * Installs a {@link WindowFocusListener} on the given {@link JComponent}'s parent {@link Window}. If the
     * {@code JComponent} doesn't yet have a parent, then the listener will be installed when the component is added to
     * a container.
     *
     * @param component     the component who's parent frame to listen to focus changes on.
     * @param focusListener the {@code WindowFocusListener} to notify when focus changes.
     */
    public static void installWeakWindowFocusListener(JComponent component, WindowFocusListener focusListener) {
        WindowListener weakFocusListener = createWeakWindowFocusListener(focusListener);
        AncestorListener ancestorListener = createAncestorListener(component, weakFocusListener);
        component.addAncestorListener(ancestorListener);
    }

    private static WindowListener createWeakWindowFocusListener(WindowFocusListener windowFocusListener) {
        final WeakReference<WindowFocusListener> weakReference =
                new WeakReference<WindowFocusListener>(windowFocusListener);
        return new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                // TODO if the WeakReference's object is null, remove the WeakReference as a FocusListener.
                if (weakReference.get() != null) {
                    weakReference.get().windowGainedFocus(e);
                }
            }

            public void windowDeactivated(WindowEvent e) {
                if (weakReference.get() != null) {
                    weakReference.get().windowLostFocus(e);
                }
            }
        };
    }

    /**
     * Installs a listener on the given {@link JComponent}'s parent {@link Window} that repaints
     * the given component when the parent window's focused state changes. If the given component
     * does not have a parent at the time this method is called, then an ancestor listener will be
     * installed that installs a window listener when the components parent changes.
     *
     * @param component the {@code JComponent} to add the repaint focus listener to.
     */
    public static void installJComponentRepainterOnWindowFocusChanged(JComponent component) {
        // TODO check to see if the component already has an ancestor.
        WindowListener windowListener = createWeakWindowFocusListener(createRepaintWindowListener(component));
        AncestorListener ancestorListener = createAncestorListener(component, windowListener);
        component.addAncestorListener(ancestorListener);
    }

    private static AncestorListener createAncestorListener(
            JComponent component, final WindowListener windowListener) {
        final WeakReference<JComponent> weakReference = new WeakReference<JComponent>(component);
        return new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                // TODO if the WeakReference's object is null, remove the WeakReference as an AncestorListener.
                Window window = weakReference.get() == null
                        ? null : SwingUtilities.getWindowAncestor(weakReference.get());
                if (window != null) {
                    window.removeWindowListener(windowListener);
                    window.addWindowListener(windowListener);
                }
            }

            public void ancestorRemoved(AncestorEvent event) {
                Window window = weakReference.get() == null
                        ? null : SwingUtilities.getWindowAncestor(weakReference.get());
                if (window != null) {
                    window.removeWindowListener(windowListener);
                }
            }

            public void ancestorMoved(AncestorEvent event) {
                // no implementation.
            }
        };
    }

    private static WindowFocusListener createRepaintWindowListener(final JComponent component) {
        return new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                component.repaint();
            }

            public void windowDeactivated(WindowEvent e) {
                component.repaint();
            }
        };
    }

}
