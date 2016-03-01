/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.kit;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Daniel
 */
public class FormFieldKit {

    private static final class EnsureVisibleOnScrollPaneFocusListener extends FocusAdapter {

        @Override
        public void focusGained(FocusEvent e) {
            Component focused = e.getComponent();
            ((JPanel) focused.getParent()).scrollRectToVisible(focused.getBounds());
        }
    };

    private static final class HighlighOnFocusListener extends FocusAdapter {

        private Color previousColor;

        @Override
        public void focusGained(FocusEvent e) {
            Component focused = e.getComponent();
            previousColor = focused.getBackground();
            focused.setBackground(SystemColor.info);
        }

        @Override
        public void focusLost(FocusEvent e) {
            Component focusedComponent = e.getComponent();
            focusedComponent.setBackground(previousColor);
        }

    };

    private static final class CaretUpNavigatesAsTabAction extends AbstractAction {

        private final Action originalAction;

        private CaretUpNavigatesAsTabAction(Action originalAction) {
            this.originalAction = originalAction;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                if (ev.getSource() instanceof JTextArea) {
                    JTextArea focused = (JTextArea) ev.getSource();
                    int caretpos = focused.getCaretPosition();
                    int linenum = focused.getLineOfOffset(caretpos);
                    if (linenum == 0) {
                        
                        final Component prev = focused.getFocusCycleRootAncestor().getFocusTraversalPolicy().getComponentBefore(focused.getFocusCycleRootAncestor(), focused);
                        if (prev instanceof JTextComponent) {
                            focused.transferFocusBackward();
                        }
                        return;
                    }
                } else if (ev.getSource() instanceof JTextField) {
                    JTextField focused = (JTextField) ev.getSource();
                    focused.transferFocusBackward();
                    return;
                }
                if (originalAction != null) {
                    originalAction.actionPerformed(ev);
                }
            } catch (BadLocationException e) {
                UIManager.getLookAndFeel().provideErrorFeedback((Component) ev.getSource());
            }
        }
    };

    private static final class CaretDownNavigatesAsTabAction extends AbstractAction {

        private final Action originalAction;

        private CaretDownNavigatesAsTabAction(Action originalAction) {
            this.originalAction = originalAction;
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                if (ev.getSource() instanceof JTextArea) {
                    JTextArea focused = (JTextArea) ev.getSource();
                    int caretpos = focused.getCaretPosition();
                    int linenum = focused.getLineOfOffset(caretpos);
                    if (linenum == focused.getRows() - 1) {
                        final Component next = focused.getFocusCycleRootAncestor().getFocusTraversalPolicy().getComponentAfter(focused.getFocusCycleRootAncestor(), focused);
                        if (next instanceof JTextComponent) {
                            focused.transferFocus();
                        }
                        return;
                    }
                } else if (ev.getSource() instanceof JTextField) {
                    JTextField focused = (JTextField) ev.getSource();
                    focused.transferFocus();
                    return;
                }
                if (originalAction != null) {
                    originalAction.actionPerformed(ev);
                }
            } catch (BadLocationException e) {
                UIManager.getLookAndFeel().provideErrorFeedback((Component) ev.getSource());
            }
        }
    };

    public static void ensureVisibleOnScrollPane(Component field) {
        field.addFocusListener(new EnsureVisibleOnScrollPaneFocusListener());
    }

    public static void highlighOnFocus(Component field) {
        field.addFocusListener(new HighlighOnFocusListener());
    }

    public static void upDownNavigateAsTabs(JComponent field) {
        final KeyStroke keyStrokeDown = KeyStroke.getKeyStroke("DOWN");
        final KeyStroke keyStrokeUp = KeyStroke.getKeyStroke("UP");
        final String actionCaretDown = "caret-down";
        final String actionCaretUp = "caret-up";
        final InputMap inputMap = field.getInputMap();

        if (field instanceof JTextComponent && inputMap.get(keyStrokeDown) == null) {
            inputMap.put(keyStrokeDown, actionCaretDown);
        }
        if (field instanceof JTextComponent && inputMap.get(keyStrokeUp) == null) {
            inputMap.put(keyStrokeUp, actionCaretUp);
        }
        field.getActionMap().put(actionCaretDown, new CaretDownNavigatesAsTabAction(field.getActionMap().get(actionCaretDown)));
        field.getActionMap().put(actionCaretUp, new CaretUpNavigatesAsTabAction(field.getActionMap().get(actionCaretUp)));
    }

    public static void applyTabDefaultBehavior(JTextArea field) {
        field.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        field.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
    }
}
