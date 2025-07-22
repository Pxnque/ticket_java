/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import javax.swing.text.*;

/**
 *
 * @author panqu
 */
public class NumericFilters {
    public static class IntegerFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text.matches("\\d+")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    // Decimal filter
    public static class DecimalFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (isValid(string, fb.getDocument().getText(0, fb.getDocument().getLength()), offset)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (isValid(text, fb.getDocument().getText(0, fb.getDocument().getLength()), offset)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValid(String text, String currentText, int offset) {
            StringBuilder newText = new StringBuilder(currentText);
            newText.insert(offset, text);
            return newText.toString().matches("\\d*(\\.\\d{0,2})?"); // allows up to 2 decimals
        }
    }
}
