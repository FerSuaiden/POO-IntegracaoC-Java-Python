import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.io.*;

public class FIFAApp extends JFrame {
    private JTextField idField, ageField, nameField, nationalityField, clubField;
    private JPanel playerPanel;
    private JLabel selectedPlayerLabel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String currentBinFileName;

    public FIFAApp() {
        setTitle("FIFA Player Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadItem = new JMenuItem("Load FIFA File");
        loadItem.addActionListener(e -> new Thread(() -> loadFile()).start());
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JMenuItem listPlayersItem = new JMenuItem("List All Players");
        listPlayersItem.addActionListener(e -> new Thread(() -> listAllPlayers()).start());
        fileMenu.add(listPlayersItem);

        // Search Fields
        idField = new JTextField(10);
        ageField = new JTextField(10);
        nameField = new JTextField(10);
        nationalityField = new JTextField(10);
        clubField = new JTextField(10);

        add(new JLabel("ID:"));
        add(idField);
        add(new JLabel("Age:"));
        add(ageField);
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Nationality:"));
        add(nationalityField);
        add(new JLabel("Club:"));
        add(clubField);

        // Search Button
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> new Thread(() -> searchPlayers()).start());
        add(searchButton);

        // Player Panel
        playerPanel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(playerPanel);
        scrollPane.setPreferredSize(new Dimension(580, 200));
        add(scrollPane);

        // Selected Player Label
        selectedPlayerLabel = new JLabel("Selected Player: ");
        add(selectedPlayerLabel);

        // Connect to Server
        connectToServer();
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String csvFileName = fileChooser.getSelectedFile().getName();
            currentBinFileName = csvFileName.replace(".csv", ".bin");

            out.println("load;" + csvFileName + ";" + currentBinFileName);
            try {
                String response = in.readLine();
                if (response != null) {
                    SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Binary file created: " + currentBinFileName));
                } else {
                    SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Failed to create binary file."));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listAllPlayers() {
        if (currentBinFileName != null) {
            // Envia comando ao servidor para listar todos os jogadores no arquivo binário atual
            out.println("list;" + currentBinFileName);
            try {
                String line;
                Player player = null;

                // Limpa o painel de jogadores antes de adicionar novos registros
                SwingUtilities.invokeLater(() -> playerPanel.removeAll());

                // Lê as linhas da resposta do servidor
                while ((line = in.readLine()) != null && !line.equals("<END_OF_MESSAGE>")) {
                    if (line.startsWith("Nome do Jogador: ")) {
                        // Se já existe um jogador, adiciona ao painel
                        if (player != null) {
                            Player finalPlayer = player;
                            SwingUtilities.invokeLater(() -> addPlayerToPanel(finalPlayer));
                        }
                        // Cria um novo jogador com o nome lido
                        player = new Player(line.substring(16), "", "");
                    } else if (line.startsWith("Nacionalidade do Jogador: ")) {
                        // Atualiza a nacionalidade do jogador atual
                        if (player != null) {
                            player = new Player(player.getName(), line.substring(25), player.getClub());
                        }
                    } else if (line.startsWith("Clube do Jogador: ")) {
                        // Atualiza o clube do jogador atual
                        if (player != null) {
                            player = new Player(player.getName(), player.getNationality(), line.substring(18));
                        }
                    }
                }

                // Adiciona o último jogador ao painel
                if (player != null) {
                    Player finalPlayer = player;
                    SwingUtilities.invokeLater(() -> addPlayerToPanel(finalPlayer));
                }

                // Revalida e repinta o painel de jogadores
                SwingUtilities.invokeLater(() -> {
                    playerPanel.revalidate();
                    playerPanel.repaint();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Exibe uma mensagem se nenhum arquivo binário foi carregado
            SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("No binary file loaded. Please load a CSV file first."));
        }
    }

    private void addPlayerToPanel(Player player) {
    // Cria um botão para o jogador com seu nome
    JButton playerButton = new JButton(player.getName());
    
    // Define a ação ao clicar no botão, atualizando o rótulo de jogador selecionado
    playerButton.addActionListener(e -> selectedPlayerLabel.setText("Selected Player: " + player.getName()));

    // Configurações do layout do GridBagConstraints
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;  // Define a coluna
    gbc.gridy = GridBagConstraints.RELATIVE;  // Adiciona abaixo do último componente adicionado
    gbc.fill = GridBagConstraints.HORIZONTAL;  // Preenche horizontalmente
    gbc.weightx = 1.0;  // Distribui o espaço horizontalmente de forma igual

    // Adiciona o botão ao painel de jogadores
    playerPanel.add(playerButton, gbc);
}

private void searchPlayers() {
    if (currentBinFileName != null) {
        String id = idField.getText().trim();
        String age = ageField.getText().trim();
        String name = nameField.getText().trim();
        String nationality = nationalityField.getText().trim();
        String club = clubField.getText().trim();

        StringBuilder searchCommand = new StringBuilder("search;" + currentBinFileName + ";1");

        int numFields = 0;
        if (!id.isEmpty()) {
            searchCommand.append(" id ").append(id);
            numFields++;
        }
        if (!age.isEmpty()) {
            searchCommand.append(" idade ").append(age);
            numFields++;
        }
        if (!name.isEmpty()) {
            searchCommand.append(" nome \"").append(name).append("\"");
            numFields++;
        }
        if (!nationality.isEmpty()) {
            searchCommand.append(" nacionalidade \"").append(nationality).append("\"");
            numFields++;
        }
        if (!club.isEmpty()) {
            searchCommand.append(" clube \"").append(club).append("\"");
            numFields++;
        }

        // Update num_searches to the actual number of fields being searched
        searchCommand.insert(searchCommand.indexOf(";1") + 2, " " + numFields);

        out.println(searchCommand.toString());
        try {
            StringBuilder response = new StringBuilder();
            String line;
            boolean isFirstLine = true;
            Player player = null;

            // Clear the player panel
            SwingUtilities.invokeLater(() -> playerPanel.removeAll());

            while ((line = in.readLine()) != null && !line.equals("<END_OF_MESSAGE>")) {
                if (line.startsWith("Nome do Jogador: ")) {
                    if (!isFirstLine) {
                        Player finalPlayer = player;
                        SwingUtilities.invokeLater(() -> addPlayerToPanel(finalPlayer));
                    }
                    player = new Player(line.substring(16), "", "");
                    isFirstLine = false;
                } else if (line.startsWith("Nacionalidade do Jogador: ")) {
                    if (player != null) {
                        player = new Player(player.getName(), line.substring(25), player.getClub());
                    }
                } else if (line.startsWith("Clube do Jogador: ")) {
                    if (player != null) {
                        player = new Player(player.getName(), player.getNationality(), line.substring(18));
                    }
                }
            }
            if (player != null) {
                Player finalPlayer = player;
                SwingUtilities.invokeLater(() -> addPlayerToPanel(finalPlayer));
            }

            SwingUtilities.invokeLater(() -> {
                playerPanel.revalidate();
                playerPanel.repaint();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    } else {
        SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("No binary file loaded. Please load a CSV file first."));
    }
}


    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FIFAApp().setVisible(true));
    }
}

class Player {
    private String name;
    private String nationality;
    private String club;

    public Player(String name, String nationality, String club) {
        this.name = name;
        this.nationality = nationality;
        this.club = club;
    }

    public String getName() {
        return name;
    }

    public String getNationality() {
        return nationality;
    }

    public String getClub() {
        return club;
    }

    @Override
    public String toString() {
        return "Nome do Jogador: " + name + "\n" +
               "Nacionalidade do Jogador: " + nationality + "\n" +
               "Clube do Jogador: " + club;
    }
}