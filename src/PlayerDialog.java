package src;
import javax.swing.*;
import java.awt.*;

public class PlayerDialog extends JDialog {
    private JTextField idField, ageField, playerNameField, nationalityField, clubNameField;
    private JButton okButton, cancelButton;
    private boolean okPressed = false;

    public PlayerDialog(Frame owner) {
        super(owner, "Player Details", true);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("ID:"));
        idField = new JTextField();
        add(idField);

        add(new JLabel("Age:"));
        ageField = new JTextField();
        add(ageField);

        add(new JLabel("Player Name:"));
        playerNameField = new JTextField();
        add(playerNameField);

        add(new JLabel("Nationality:"));
        nationalityField = new JTextField();
        add(nationalityField);

        add(new JLabel("Club Name:"));
        clubNameField = new JTextField();
        add(clubNameField);

        okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            okPressed = true;
            setVisible(false);
        });
        add(okButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        add(cancelButton);

        pack();
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    public Player getPlayer() {
        int id = Integer.parseInt(idField.getText());
        int age = Integer.parseInt(ageField.getText());
        String playerName = playerNameField.getText();
        String nationality = nationalityField.getText();
        String clubName = clubNameField.getText();
        return new Player(id, age, playerName, nationality, clubName);
    }
}
