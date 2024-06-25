import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Interface extends JFrame {
    private JTextField idField, ageField, nameField, nationalityField, clubField;
    private JPanel playerPanel;
    private JLabel selectedPlayerLabel;
    private JTextField editNameField, editNationalityField, editClubField;
    private JButton editButton, removeButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String currentBinFileName;
    private Player selectedPlayer;

    private static Interface instance;

    public Interface() {
        instance = this;

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

        // Campos de Busca
        idField = new JTextField(10);
        ageField = new JTextField(10);
        nameField = new JTextField(10);
        nationalityField = new JTextField(10);
        clubField = new JTextField(10);

        // Adicionando os campos e labels alinhados à esquerda
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

        // Definindo um novo painel para os botões com layout FlowLayout
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        // Botão de Busca
        JButton searchButton = new JButton("Procurar");
        searchButton.addActionListener(e -> new Thread(() -> searchPlayers()).start());
        buttonsPanel.add(searchButton);

        // Botão de Edição
        editButton = new JButton("Editar Jogador");
        editButton.addActionListener(e -> new Thread(() -> editPlayer()).start());
        buttonsPanel.add(editButton);

        // Botão de Remoção
        removeButton = new JButton("Remover Jogador");
        removeButton.addActionListener(e -> new Thread(() -> removePlayer()).start());
        buttonsPanel.add(removeButton);

        // Adicionando o painel de botões ao conteúdo principal
        add(buttonsPanel);

        // Selecionar Jogador
        selectedPlayerLabel = new JLabel("Jogador Selecionado: ");
        add(selectedPlayerLabel);

        // Painel do Jogador
        playerPanel = new JPanel(new GridBagLayout());
        JScrollPane scrollPane = new JScrollPane(playerPanel);
        scrollPane.setPreferredSize(new Dimension(580, 200));
        add(scrollPane);

        // Campos de Edição do Jogador
        editNameField = new JTextField(15);
        editNationalityField = new JTextField(15);
        editClubField = new JTextField(15);

        add(new JLabel("Editar Nome:"));
        add(editNameField);
        add(new JLabel("Editar Nacionalidade:"));
        add(editNationalityField);
        add(new JLabel("Editar Clube:"));
        add(editClubField);

        // Conectar com o Server
        connectToServer();

    }

    public static Interface getInstance() {
        return instance;
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
                    if (line.startsWith("Nome do Jogador: ")) {
                        if (player != null) {
                            Player finalPlayer = player;
                            SwingUtilities.invokeLater(() -> addPlayerToPanel(finalPlayer));
                        }
                        player = new Player(line.substring(16), "", "", -1); // Não temos id aqui, apenas o nome
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
            selectedPlayerLabel.setText("Jogador Selecionado: " + player.getName());
            editNameField.setText(player.getName());
            editNationalityField.setText(player.getNationality());
            editClubField.setText(player.getClub());
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

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

            searchCommand.insert(searchCommand.indexOf(";1") + 2, " " + numFields);

            out.println(searchCommand.toString());
            try {
                String line;
                Player player = null;

                SwingUtilities.invokeLater(() -> playerPanel.removeAll());

                while ((line = in.readLine()) != null && !line.equals("<END_OF_MESSAGE>")) {
                    if (line.startsWith("Nome do Jogador: ")) {
                        if (player != null) {
                            Player finalPlayer = player;
                            SwingUtilities.invokeLater(() -> addPlayerToPanel(finalPlayer));
                        }
                        player = new Player(line.substring(16), "", "", -1); // Não temos id aqui, apenas o nome
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

    private void editPlayer() {
        if (selectedPlayer != null && currentBinFileName != null) {
            // Abrir a janela de edição
            new EditPlayerDialog(this, selectedPlayer);
        }
    }

    public void updatePlayer(Player updatedPlayer) {
        // Remover o jogador antigo da lista
        SwingUtilities.invokeLater(() -> {
            Component[] components = playerPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    if (button.getText().equals(selectedPlayer.getName())) {
                        playerPanel.remove(button);
                        break;
                    }
                }
            }

            // Adicionar o jogador atualizado à lista
            addPlayerToPanel(updatedPlayer);

            playerPanel.revalidate();
            playerPanel.repaint();
            selectedPlayerLabel.setText("Jogador atualizado: " + updatedPlayer.getName());
        });
    }

    private void removePlayer() {
        if (selectedPlayer != null && currentBinFileName != null) {
            // Construir comando de remoção usando o ID do jogador
            String removeCommand = String.format("remove;%s;%s;1;id;%d",
                    currentBinFileName,
                    currentBinFileName.replace(".bin", ".idx"),
                    selectedPlayer.getId());

            out.println(removeCommand);

            try {
                String response = in.readLine();
                if (response != null && (response.equals("OK") || !response.contains("Error"))) {
                    // Remover o botão que representa o jogador da interface
                    SwingUtilities.invokeLater(() -> {
                        Component[] components = playerPanel.getComponents();
                        for (Component component : components) {
                            if (component instanceof JButton) {
                                JButton button = (JButton) component;
                                // Aqui precisamos identificar o botão pelo nome (ou outra propriedade única do jogador)
                                if (button.getText().equals(selectedPlayer.getName())) {
                                    playerPanel.remove(button);
                                    break;
                                }
                            }
                        }
                        playerPanel.revalidate();
                        playerPanel.repaint();
                        selectedPlayerLabel.setText("Jogador removido: " + selectedPlayer.getName());
                    });
                } else {
                    SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Falha em remover jogador. Resposta: " + response));
                }
            } catch (IOException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> selectedPlayerLabel.setText("Erro de comunicação com o servidor."));
            }
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
        SwingUtilities.invokeLater(() -> new Interface().setVisible(true));
    }

    class Player {
        private String name;
        private String nationality;
        private String club;
        private int id;

        public Player(String name, String nationality, String club, int id) {
            this.name = name;
            this.nationality = nationality;
            this.club = club;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public String getNationality() {
            return nationality;
        }

        public String getClub() {
            return club;
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
            return "Nome do Jogador: " + name + "\n" +
                   "Nacionalidade do Jogador: " + nationality + "\n" +
                   "Clube do Jogador: " + club;
        }
    }

    public class EditPlayerDialog extends JDialog {
        private JTextField editNameField, editNationalityField, editClubField;
        private Player player; // Referência para o jogador a ser editado
    
        public EditPlayerDialog(JFrame parent, Player player) {
            super(parent, "Editar Jogador", true);
            this.player = player;
    
            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("Nome:"));
            editNameField = new JTextField(player.getName());
            panel.add(editNameField);
            panel.add(new JLabel("Nacionalidade:"));
            editNationalityField = new JTextField(player.getNationality());
            panel.add(editNationalityField);
            panel.add(new JLabel("Clube:"));
            editClubField = new JTextField(player.getClub());
            panel.add(editClubField);
    
            JButton saveButton = new JButton("Salvar");
            saveButton.addActionListener(e -> {
                savePlayer();
                dispose();
            });
    
            panel.add(saveButton);
            getContentPane().add(panel);
            pack();
            setLocationRelativeTo(parent);
            setVisible(true);
        }
    
        private void savePlayer() {
            String newName = editNameField.getText().trim();
            String newNationality = editNationalityField.getText().trim();
            String newClub = editClubField.getText().trim();
    
            // Atualizar os dados do jogador
            player.setName(newName);
            player.setNationality(newNationality);
            player.setClub(newClub);
    
            // Chamar o método da classe principal para atualizar o jogador na interface
            updatePlayer(player);
    
            // Enviar os dados atualizados para o servidor
            sendUpdatedPlayerToServer(player);
        }
    
        private void sendUpdatedPlayerToServer(Player player) {
            // Construir a mensagem para enviar ao servidor
            String updateCommand = String.format("update;%s;%d;%s;%s;%s",
                    Interface.getInstance().getCurrentBinFileName(),
                    player.getId(),
                    player.getName(),
                    player.getNationality(),
                    player.getClub());
    
            // Enviar a mensagem ao servidor via socket
            try {
                Interface.getInstance().getOut().println(updateCommand);
                String response = Interface.getInstance().getIn().readLine();
                if (response != null && (response.equals("OK") || !response.contains("Error"))) {
                    // Atualização bem-sucedida, se necessário, tratar a resposta do servidor
                } else {
                    handleUpdateFailure(response);
                }
            } catch (IOException e) {
                handleCommunicationError(e);
            }
        }
    
        private void handleUpdateFailure(String response) {
            // Lidar com falha na atualização
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Falha na atualização do jogador. Resposta do servidor: " + response,
                        "Erro de Atualização", JOptionPane.ERROR_MESSAGE);
            });
        }
    
        private void handleCommunicationError(IOException e) {
            // Tratar erro de comunicação com o servidor
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Erro de comunicação com o servidor: " + e.getMessage(),
                        "Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    public String getCurrentBinFileName() {
        return currentBinFileName;
    }
    
    public PrintWriter getOut() {
        return out;
    }
    
    public BufferedReader getIn() {
        return in;
    }
    
}