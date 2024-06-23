import javax.swing.*;
import java.net.Socket;
import java.io.*;

public class FIFAApp extends JFrame {
    private JTextField idField, ageField, nameField, nationalityField, clubField;
    private JTextArea resultArea;
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
        loadItem.addActionListener(e -> new Thread(this::loadFile).start());
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JMenuItem listPlayersItem = new JMenuItem("List All Players");
        listPlayersItem.addActionListener(e -> new Thread(this::listAllPlayers).start());
        fileMenu.add(listPlayersItem);

        // Search Fields
        idField = new JTextField(10);
        ageField = new JTextField(10);
        nameField = new JTextField(10);
        nationalityField = new JTextField(10);
        clubField = new JTextField(10);

        add(new JLabel("id:"));
        add(idField);
        add(new JLabel("idade:"));
        add(ageField);
        add(new JLabel("nome:"));
        add(nameField);
        add(new JLabel("nacionalidade:"));
        add(nationalityField);
        add(new JLabel("clube:"));
        add(clubField);

        // Search Button
        JButton searchButton = new JButton("buscar");
        searchButton.addActionListener(e -> new Thread(this::searchPlayers).start());
        add(searchButton);

        // Remove Button
        JButton removeButton = new JButton("remover");
        removeButton.addActionListener(e -> new Thread(this::removePlayers).start());
        add(removeButton);

        // Results Area
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea));

        // Connect to Server
        connectToServer();
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String csvFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            currentBinFileName = csvFilePath.replace(".csv", ".bin");

            out.println("load;" + csvFilePath + ";" + currentBinFileName);
            try {
                String response = in.readLine();
                if (response != null) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("Binary file created: " + currentBinFileName);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("Failed to create binary file.");
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listAllPlayers() {
        if (currentBinFileName != null) {
            System.out.println("Sending list command to server with binary file: " + currentBinFileName);
            out.println("list;" + currentBinFileName);
            try {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("<END_OF_MESSAGE>")) {
                        break;
                    }
                    response.append(line).append("\n");
                }
                SwingUtilities.invokeLater(() -> resultArea.setText(response.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SwingUtilities.invokeLater(() -> resultArea.setText("No binary file loaded. Please load a CSV file first."));
        }
    }

    private void searchPlayers() {
        if (currentBinFileName != null) {
            String id = idField.getText();
            String age = ageField.getText();
            String name = nameField.getText();
            String nationality = nationalityField.getText();
            String club = clubField.getText();

            System.out.println("Sending search command to server with parameters: id=" + id + ", age=" + age + ", name=" + name + ", nationality=" + nationality + ", club=" + club);
            out.println("search;" + currentBinFileName + ";" + id + ";" + age + ";" + name + ";" + nationality + ";" + club);
            try {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equals("<END_OF_MESSAGE>")) {
                        break;
                    }
                    response.append(line).append("\n");
                }
                SwingUtilities.invokeLater(() -> resultArea.setText(response.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SwingUtilities.invokeLater(() -> resultArea.setText("No binary file loaded. Please load a CSV file first."));
        }
    }

    private void removePlayers() {
        if (currentBinFileName != null) {
            String id = idField.getText();
            String age = ageField.getText();
            String name = nameField.getText();
            String nationality = nationalityField.getText();
            String club = clubField.getText();

            System.out.println("Sending remove command to server with parameters: id=" + id + ", age=" + age + ", name=" + name + ", nationality=" + nationality + ", club=" + club);
            out.println("remove;" + currentBinFileName + ";" + id + ";" + age + ";" + name + ";" + nationality + ";" + club);
            try {
                String response = in.readLine();
                if (response != null && response.equals("Player removed successfully.")) {
                    SwingUtilities.invokeLater(() -> resultArea.setText("Player removed successfully."));
                } else {
                    SwingUtilities.invokeLater(() -> resultArea.setText("Failed to remove player."));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            SwingUtilities.invokeLater(() -> resultArea.setText("No binary file loaded. Please load a CSV file first."));
        }
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FIFAApp().setVisible(true));
    }
}
