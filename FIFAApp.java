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
            String csvFileName = fileChooser.getSelectedFile().getName();
            currentBinFileName = csvFileName.replace(".csv", ".bin");

            out.println("load;" + csvFileName + ";" + currentBinFileName);
            try {
                String response = in.readLine();
                if (response != null) {
                    SwingUtilities.invokeLater(() -> resultArea.setText("Binary file created: " + currentBinFileName));
                } else {
                    SwingUtilities.invokeLater(() -> resultArea.setText("Failed to create binary file."));
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
                StringBuilder response = new StringBuilder();
                String line;

                // Read lines until the end-of-message token is encountered
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

            out.println("search;" + currentBinFileName + ";" + id + ";" + age + ";" + name + ";" + nationality + ";" + club);
            try {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
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
