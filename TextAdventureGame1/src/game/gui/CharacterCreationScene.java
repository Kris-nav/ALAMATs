package game.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CharacterCreationScene extends JFrame {

    private JTextField nameField;
    private JTextField ageField;
    private JTextField genderField;
    private JLabel errorLabel;

    private String finalName   = "";
    private int    finalAge    = 0;
    private String finalGender = "";
    private boolean confirmed  = false;

    public CharacterCreationScene() {
        setTitle("ALAMAT - Character Creation");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        int W = 600, H = 420;
        JPanel root = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18, 12, 5));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(170, 120, 50));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 16, 16);
                g2.setColor(new Color(60, 38, 8));
                g2.fillRoundRect(10, 10, getWidth() - 20, 56, 16, 16);
                g2.fillRect(10, 36, getWidth() - 20, 30);
                g2.setColor(new Color(255, 215, 90));
                g2.setFont(new Font("Monospaced", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String title = "WELCOME TO ALAMAT";
                g2.drawString(title,
                        (getWidth() - fm.stringWidth(title)) / 2, 50);
                g2.setColor(new Color(170, 120, 50));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(30, 68, getWidth() - 30, 68);
            }
        };
        root.setPreferredSize(new Dimension(W, H));

        // Name
        addLabel(root, "Name:", 30, 100);
        nameField = new JTextField();
        styleField(nameField);
        nameField.setBounds(180, 88, 380, 38);
        root.add(nameField);

        // Age
        addLabel(root, "Age:", 30, 162);
        ageField = new JTextField();
        styleField(ageField);
        ageField.setBounds(180, 150, 380, 38);
        root.add(ageField);

        // Gender
        addLabel(root, "Gender (M/F):", 30, 224);
        genderField = new JTextField();
        styleField(genderField);
        genderField.setBounds(180, 212, 380, 38);
        root.add(genderField);

        // Error label
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(new Color(220, 60, 60));
        errorLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        errorLabel.setBounds(30, 262, 540, 20);
        root.add(errorLabel);

        // Hints
        String[] hints = {
                "• Name must not contain numbers",
                "• Age must be 18 or older",
                "• Type M or F for gender"
        };
        int hy = 284;
        for (String h : hints) {
            JLabel hl = new JLabel(h);
            hl.setForeground(new Color(140, 140, 140));
            hl.setFont(new Font("Monospaced", Font.PLAIN, 11));
            hl.setBounds(30, hy, 540, 16);
            root.add(hl);
            hy += 16;
        }

        // BEGIN ADVENTURE button
        JButton okBtn = new JButton("BEGIN ADVENTURE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()
                        ? new Color(30, 100, 40)
                        : getModel().isRollover()
                          ? new Color(50, 160, 70)
                          : new Color(40, 130, 55));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Monospaced", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        okBtn.setOpaque(false);
        okBtn.setContentAreaFilled(false);
        okBtn.setBorderPainted(false);
        okBtn.setFocusPainted(false);
        okBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okBtn.setBounds(150, 348, 300, 46);
        okBtn.addActionListener(e -> tryConfirm());
        getRootPane().setDefaultButton(okBtn);
        root.add(okBtn);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    private void addLabel(JPanel panel, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(220, 180, 80));
        lbl.setFont(new Font("Monospaced", Font.BOLD, 14));
        lbl.setBounds(x, y - 12, 160, 38);
        panel.add(lbl);
    }

    private void styleField(JTextField field) {
        field.setBackground(new Color(30, 20, 8));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Monospaced", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 80, 20), 2),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    private void tryConfirm() {
        String name   = nameField.getText().trim();
        String ageStr = ageField.getText().trim();
        String gender = genderField.getText().trim().toUpperCase();

        if (name.isEmpty()) {
            errorLabel.setText("Please enter your name.");
            return;
        }
        if (name.matches(".*\\d.*")) {
            errorLabel.setText("Name must not contain numbers.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            errorLabel.setText("Age must be a number.");
            return;
        }
        if (age < 18) {
            errorLabel.setText("You must be 18 or older to play.");
            return;
        }

        if (!gender.equals("M") && !gender.equals("F")) {
            errorLabel.setText("Please enter M or F for gender.");
            return;
        }

        finalName   = name;
        finalAge    = age;
        finalGender = gender.equals("M") ? "Male" : "Female";
        confirmed   = true;

        synchronized (this) { notify(); }
    }

    public void waitForConfirmation() {
        synchronized (this) {
            while (!confirmed) {
                try { wait(); } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public String getPlayerName()   { return finalName; }
    public int    getPlayerAge()    { return finalAge; }
    public String getPlayerGender() { return finalGender; }
}