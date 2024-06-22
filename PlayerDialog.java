import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerDialog extends JDialog {
    private JTextField idField, ageField, playerNameField, nationalityField, clubNameField;
    private Player player;
    private boolean isRemoved = false;

    public PlayerDialog(Frame owner, Player player) {
        super(owner, "Edit Player", true);
        this.player = player;

        setLayout(new GridLayout(7, 2));

        add(new JLabel("ID:"));
        idField = new JTextField(String.valueOf(player.getId()));
        add(idField);

        add(new JLabel("Age:"));
        ageField = new JTextField(String.valueOf(player.getAge()));
        add(ageField);

        add(new JLabel("Player Name:"));
        playerNameField = new JTextField(player.getPlayerName());
        add(playerNameField);

        add(new JLabel("Nationality:"));
        nationalityField = new JTextField(player.getNationality());
        add(nationalityField);

        add(new JLabel("Club Name:"));
        clubNameField = new JTextField(player.getClubName());
        add(clubNameField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.setId(Integer.parseInt(idField.getText()));
                player.setAge(Integer.parseInt(ageField.getText()));
                player.setPlayerName(playerNameField.getText());
                player.setNationality(nationalityField.getText());
                player.setClubName(clubNameField.getText());
                dispose();
            }
        });
        add(saveButton);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isRemoved = true;
                dispose();
            }
        });
        add(removeButton);

        setSize(300, 250);
        setLocationRelativeTo(owner);
    }

    public boolean isRemoved() {
        return isRemoved;
    }
}
