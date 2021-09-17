package me.bytebeats.mns.ui.swing;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created on 2021/9/11 18:53
 * @Version 1.0
 * @Description JTextField with hint supported.
 */

public class JHintTextField extends JTextField implements FocusListener {
    private final Font gainFont;
    private final Font lostFont;
    private final String hint;

    public JHintTextField(final String hint) {
        this.hint = hint;
        Font font = getFont();
        gainFont = new Font(font.getFontName(), font.getStyle(), font.getSize());
        lostFont = new Font(font.getFontName(), Font.ITALIC, font.getSize());
        setText(hint);
        setFont(lostFont);
        setForeground(JBColor.GRAY);
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (super.getText().equals(hint)) {
            setText("");
            setFont(gainFont);
        } else {
            setText(super.getText());
            setFont(gainFont);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (super.getText().equals(hint) || super.getText().isEmpty()) {
            super.setText(hint);
            setFont(lostFont);
            setForeground(JBColor.GRAY);
        } else {
            setText(super.getText());
            setFont(gainFont);
            setForeground(JBColor.BLACK);
        }
    }

    @Override
    public String getText() {
        if (super.getText().equals(hint)) {
            return "";
        } else {
            return super.getText();
        }
    }
}
