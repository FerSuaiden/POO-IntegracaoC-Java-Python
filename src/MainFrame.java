package src;
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
        listItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (socketClient != null) {
                    String response = socketClient.sendCommand("buscatodos " + currentFilePath);
                    if (response != null) {
                        JOptionPane.showMessageDialog(MainFrame.this, response, "Lista de Jogadores", JOptionPane.INFORMATION_MESSAGE);
                    }
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

        JPanel searchPanel = new JPanel(new GridLayout(2, 2));
        searchPanel.add(new JLabel("Player Name:"));
        playerNameField = new JTextField();
        searchPanel.add(playerNameField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText();
                List<Player> filteredPlayers = players.stream()
                        .filter(p -> p.getPlayerName().equalsIgnoreCase(playerName))
                        .collect(Collectors.toList());
                updatePlayerList(filteredPlayers);
            }
        });
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        playerListModel = new DefaultListModel<>();
        playerList = new JList<>(playerListModel);
        add(new JScrollPane(playerList), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Age:"));
        ageField = new JTextField();
        inputPanel.add(ageField);

        inputPanel.add(new JLabel("Player Name:"));
        playerNameField = new JTextField();
        inputPanel.add(playerNameField);

        inputPanel.add(new JLabel("Nationality:"));
        nationalityField = new JTextField();
        inputPanel.add(nationalityField);

        inputPanel.add(new JLabel("Club Name:"));
        clubNameField = new JTextField();
        inputPanel.add(clubNameField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(idField.getText());
                int age = Integer.parseInt(ageField.getText());
                String playerName = playerNameField.getText();
                String nationality = nationalityField.getText();
                String clubName = clubNameField.getText();
                Player player = new Player(id, age, playerName, nationality, clubName);
                players.add(player);
                updatePlayerList(players);
            }
        });

        inputPanel.add(addButton);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void updatePlayerList(List<Player> players) {
        playerListModel.clear();
        for (Player player : players) {
            playerListModel.addElement(player);
        }
    }
}