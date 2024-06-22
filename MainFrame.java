import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {
    private JTextField idField, ageField, playerNameField, nationalityField, clubNameField;
    private JList<Player> playerList;
    private DefaultListModel<Player> playerListModel;
    private List<Player> players;
    private String currentFilePath;
    private SocketClient socketClient;

    public MainFrame() {
        setTitle("FIFA Player Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    currentFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                    players = FileHandler.loadPlayersFromFile(currentFilePath);
                    updatePlayerList(players);
                }
            }
        });
        fileMenu.add(openItem);

        JMenuItem listItem = new JMenuItem("Lista dos Jogadores:");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    currentFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                    players = FileHandler.loadPlayersFromFile(currentFilePath);
                    updatePlayerList(players);
                }
            }
        });        
        fileMenu.add(listItem);

        JMenuItem connectItem = new JMenuItem("Connect to Server");
        connectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String address = JOptionPane.showInputDialog(MainFrame.this, "Enter server address:");
                if (address != null) {
                    socketClient = new SocketClient(address);
                }
            }
        });
        fileMenu.add(connectItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel searchPanel = new JPanel(new GridLayout(6, 2));

        searchPanel.add(new JLabel("id:"));
        idField = new JTextField();
        searchPanel.add(idField);

        searchPanel.add(new JLabel("idade:"));
        ageField = new JTextField();
        searchPanel.add(ageField);

        searchPanel.add(new JLabel("nomeJogador:"));
        playerNameField = new JTextField();
        searchPanel.add(playerNameField);

        searchPanel.add(new JLabel("nacionalidade:"));
        nationalityField = new JTextField();
        searchPanel.add(nationalityField);

        searchPanel.add(new JLabel("nomeClube:"));
        clubNameField = new JTextField();
        searchPanel.add(clubNameField);

        JButton searchButton = new JButton("Buscar");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPlayers();
            }
        });
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        playerListModel = new DefaultListModel<>();
        playerList = new JList<>(playerListModel);
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && playerList.getSelectedValue() != null) {
                PlayerDialog dialog = new PlayerDialog(MainFrame.this, playerList.getSelectedValue());
                dialog.setVisible(true);
                if (dialog.isRemoved()) {
                    players.remove(playerList.getSelectedValue());
                }
                FileHandler.savePlayersToFile(currentFilePath, players);
                updatePlayerList(players);
            }
        });

        add(new JScrollPane(playerList), BorderLayout.CENTER);

        setVisible(true);
    }

    private void updatePlayerList(List<Player> players) {
        playerListModel.clear();
        for (Player player : players) {
            playerListModel.addElement(player);
        }
    }

    private void searchPlayers() {
        List<Player> filteredPlayers = players.stream().filter(player -> {
            boolean matches = true;
            if (!idField.getText().isEmpty()) {
                matches &= String.valueOf(player.getId()).contains(idField.getText());
            }
            if (!ageField.getText().isEmpty()) {
                matches &= String.valueOf(player.getAge()).contains(ageField.getText());
            }
            if (!playerNameField.getText().isEmpty()) {
                matches &= player.getPlayerName().toLowerCase().contains(playerNameField.getText().toLowerCase());
            }
            if (!nationalityField.getText().isEmpty()) {
                matches &= player.getNationality().toLowerCase().contains(nationalityField.getText().toLowerCase());
            }
            if (!clubNameField.getText().isEmpty()) {
                matches &= player.getClubName().toLowerCase().contains(clubNameField.getText().toLowerCase());
            }
            return matches;
        }).collect(Collectors.toList());
        updatePlayerList(filteredPlayers);
    }
}
