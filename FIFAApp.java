import javax.swing.*;
import java.awt.*;
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
    private Player selectedPlayer;

    public FIFAApp() {
        setTitle("FIFA Player Manager");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Arquivos");
        JMenuItem loadItem = new JMenuItem("Carregar Dados CSV");
        loadItem.addActionListener(e -> new Thread(() -> loadFile()).start());
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JMenuItem listPlayersItem = new JMenuItem("Listar Todos Jogadores");
        listPlayersItem.addActionListener(e -> new Thread(() -> listAllPlayers()).start());
        fileMenu.add(listPlayersItem);

        // Search Fields
        idField = new JTextField(20);
        ageField = new JTextField(20);
        nameField = new JTextField(20);
        nationalityField = new JTextField(20);
        clubField = new JTextField(20);

        add(new JLabel("Id:"));
        add(idField);
        add(new JLabel("Idade:"));
        add(ageField);
        add(new JLabel("Nome:"));
        add(nameField);
        add(new JLabel("Nacionalidade:"));
        add(nationalityField);
        add(new JLabel("Clube:"));
        add(clubField);

        // Search Button
        JButton searchButton = new JButton("Buscar");
        searchButton.addActionListener(e -> new Thread(() -> searchPlayers()).start());
        add(searchButton);

        // Player Panel
        playerPanel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(playerPanel);
        scrollPane.setPreferredSize(new Dimension(580, 200));
        add(scrollPane);

        // Selected Player Label
        selectedPlayerLabel = new JLabel("Jogador Selecionado: ");
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
                    SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Arquivo binário criado: " + currentBinFileName));
                } else {
                    SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Falha em criar arquivo binário."));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listAllPlayers() {
        if (currentBinFileName != null) {
            out.println("list;" + currentBinFileName);
            try {
                String line;
                Player player = null;

                SwingUtilities.invokeLater(() -> playerPanel.removeAll());

                while ((line = in.readLine()) != null && !line.equals("<END_OF_MESSAGE>")) {
                    if (line.startsWith("ID jogador: ")) {
                        if (player != null) {
                            Player finalPlayer = player;
                            SwingUtilities.invokeLater(() -> addPlayerToPanel(finalPlayer));
                        }
                        player = new Player(Integer.parseInt(line.substring(12)), "", "", "");
                    } else if (line.startsWith("Nome do Jogador: ")) {
                        if (player != null) {
                            player.setName(line.substring(16));
                        }
                    } else if (line.startsWith("Nacionalidade do Jogador: ")) {
                        if (player != null) {
                            player.setNationality(line.substring(25));
                        }
                    } else if (line.startsWith("Clube do Jogador: ")) {
                        if (player != null) {
                            player.setClub(line.substring(18));
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
            SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Nenhum arquivo binário carregado. Por favor, carregue o arquivo CSV."));
        }
    }

    private void addPlayerToPanel(Player player) {
        JButton playerButton = new JButton(player.getName());
        playerButton.addActionListener(e -> {
            selectedPlayer = player;
            showPlayerDialog(player);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        playerPanel.add(playerButton, gbc);
    }

    private void showPlayerDialog(Player player) {
        JDialog dialog = new JDialog(this, "Detalhes do Jogador", true);
        dialog.setLayout(new GridLayout(6, 1));

        JLabel idLabel = new JLabel("ID: " + player.getId());
        JLabel nameLabel = new JLabel("Nome: " + player.getName());
        JLabel nationalityLabel = new JLabel("Nacionalidade: " + player.getNationality());
        JLabel clubLabel = new JLabel("Clube: " + player.getClub());

        dialog.add(idLabel);
        dialog.add(nameLabel);
        dialog.add(nationalityLabel);
        dialog.add(clubLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton insertButton = new JButton("Inserir");
        insertButton.addActionListener(e -> {
            // Insert player logic
            dialog.dispose();
        });

        JButton removeButton = new JButton("Remover");
        removeButton.addActionListener(e -> {
            new Thread(() -> removePlayer(player)).start();
            dialog.dispose();
        });

        JButton updateButton = new JButton("Alterar");
        updateButton.addActionListener(e -> {
            // Update player logic
            dialog.dispose();
        });

        buttonPanel.add(insertButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        dialog.add(buttonPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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

            searchCommand.insert(searchCommand.indexOf(";1") + 2, " " + numFields);

            out.println(searchCommand.toString());
            try {
                String line;
                Player player = null;

                SwingUtilities.invokeLater(() -> playerPanel.removeAll());

                while ((line = in.readLine()) != null && !line.equals("<END_OF_MESSAGE>")) {
                    if (line.startsWith("ID jogador: ")) {
                        if (player != null) {
                            Player finalPlayer = player;
                            SwingUtilities.invokeLater(() -> addPlayerToPanel(finalPlayer));
                        }
                        player = new Player(Integer.parseInt(line.substring(12)), "", "", "");
                    } else if (line.startsWith("Nome do Jogador: ")) {
                        if (player != null) {
                            player.setName(line.substring(16));
                        }
                    } else if (line.startsWith("Nacionalidade do Jogador: ")) {
                        if (player != null) {
                            player.setNationality(line.substring(25));
                        }
                    } else if (line.startsWith("Clube do Jogador: ")) {
                        if (player != null) {
                            player.setClub(line.substring(18));
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
            SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Nenhum arquivo binário carregado. Por favor, carregue o arquivo CSV."));
        }
    }

    private void removePlayer(Player player) {
        if (currentBinFileName != null) {
            SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Jogador removido: " + player.getName()));

            StringBuilder removeCommand = new StringBuilder("remove;" + currentBinFileName + ";1");
    

            removeCommand.insert(removeCommand.indexOf(";1") + 2, "\n" + "1" + " ");
            removeCommand.append("id ").append(player.getId());

            String commandString = removeCommand.toString().trim();
            System.out.println("Sending remove command: " + commandString);
            out.println(commandString);
            try {
                String response = in.readLine();
                System.out.println("Remove response: " + response);
                if (response != null) {
                    SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Jogadores correspondentes removidos!"));
                } else {
                    SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Falha ao remover jogador."));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Nenhum jogador selecionado ou arquivo binário carregado."));
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
    private int id;
    private String name;
    private String nationality;
    private String club;

    public Player(int id, String name, String nationality, String club) {
        this.id = id;
        this.name = name;
        this.nationality = nationality;
        this.club = club;
    }

    public int getId() {
        return id;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setClub(String club) {
        this.club = club;
    }

    @Override
    public String toString() {
        return "ID do Jogador: " + id + "\n" +
               "Nome do Jogador: " + name + "\n" +
               "Nacionalidade do Jogador: " + nationality + "\n" +
               "Clube do Jogador: " + club;
    }
}
