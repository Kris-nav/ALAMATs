package game.gui;

import javax.swing.*;
import java.awt.*;

public class CharacterCreationScene extends JFrame {
    private JTextField nameField;
    private JTextField ageField;
    private String selectedGender = "";

    public CharacterCreationScene() {
        setTitle("ALAMAT - Character Creation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Load your info.png
        ImageIcon icon = new ImageIcon("resources/Texture/info.png");
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(width, height));
        setContentPane(layeredPane);

        // 1. Background Scroll Image
        JLabel background = new JLabel(icon);
        background.setBounds(0, 0, width, height);
        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);

        // 2. Name Field - Lowered to Y=220
        nameField = new JTextField();
        nameField.setBounds(370, 220, 280, 40);
        nameField.setFont(new Font("Monospaced", Font.BOLD, 20));
        nameField.setOpaque(false);
        nameField.setBorder(null);
        layeredPane.add(nameField, JLayeredPane.PALETTE_LAYER);

        // 3. Age Field - Lowered to Y=350
        ageField = new JTextField();
        ageField.setBounds(370, 350, 280, 40);
        ageField.setFont(new Font("Monospaced", Font.BOLD, 20));
        ageField.setOpaque(false);
        ageField.setBorder(null);
        layeredPane.add(ageField, JLayeredPane.PALETTE_LAYER);

        // 4. Gender Buttons - Lowered to Y=480
        JButton maleBtn = new JButton("M");
        maleBtn.setBounds(390, 480, 80, 40);
        maleBtn.addActionListener(e -> selectedGender = "Male");
        layeredPane.add(maleBtn, JLayeredPane.PALETTE_LAYER);

        JButton femaleBtn = new JButton("F");
        femaleBtn.setBounds(540, 480, 80, 40);
        femaleBtn.addActionListener(e -> selectedGender = "Female");
        layeredPane.add(femaleBtn, JLayeredPane.PALETTE_LAYER);

        // 5. Continue Button - Lowered to Y=600
        JButton continueBtn = new JButton("Continue");
        continueBtn.setBounds(420, 600, 150, 50);
        continueBtn.addActionListener(e -> {
            if(nameField.getText().isEmpty() || selectedGender.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please complete your details!");
            } else {
                System.out.println("Adventurer Created: " + nameField.getText());
                this.dispose();
                // Add logic here to start the next part of the game
            }
        });
        layeredPane.add(continueBtn, JLayeredPane.PALETTE_LAYER);

        pack();
        setLocationRelativeTo(null);
    }
}