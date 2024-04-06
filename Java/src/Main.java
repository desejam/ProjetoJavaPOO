import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Connection conn;

    public Main() {
        setTitle("Gerenciamento - Futebol");
        setSize(350, 350); // Aumentei a altura para acomodar a imagem
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        // imagem no topo (redimensionada para 150x150)
        ImageIcon imageIcon = new ImageIcon("C:\\Users\\mathe\\Desktop\\Programação Orientada a Objetos\\Java\\src\\image.jpg"); // Substitua "image.jpg" pelo caminho da sua imagem
        Image image = imageIcon.getImage(); // Transforma o ImageIcon em Image
        Image newImage = image.getScaledInstance(128, 128, Image.SCALE_SMOOTH); // Redimensiona a imagem
        ImageIcon scaledImageIcon = new ImageIcon(newImage); // Transforma a Image redimensionada em ImageIcon
        JLabel imageLabel = new JLabel(scaledImageIcon);
        panel.add(imageLabel, constraints);

        // Avançando para o próximo componente
        constraints.gridy = 1;

        usernameField = new JTextField();
        usernameField.setBorder(BorderFactory.createTitledBorder("Usuário"));
        usernameField.setPreferredSize(new Dimension(200, 35)); // Definindo o tamanho preferido
        panel.add(usernameField, constraints);

        passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createTitledBorder("Senha"));
        passwordField.setPreferredSize(new Dimension(200, 35)); // Definindo o tamanho preferido
        constraints.gridy = 2;
        panel.add(passwordField, constraints);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        constraints.gridy = 3;
        panel.add(loginButton, constraints);


        add(panel);
        setVisible(true);

        // Conectar ao banco de dados
        connectToDatabase();
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/java"; // Substitua 'seubanco' pelo nome do seu banco de dados
        String user = "administrador"; // Substitua 'seuusuario' pelo nome de usuário do seu banco de dados
        String password = "password1234"; // Substitua 'suasenha' pela senha do seu banco de dados

        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conexão bem-sucedida.");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    private boolean validateLogin(String username, String password) {
        try {
            String query = "SELECT * FROM java.usuarios WHERE usuario=? AND senha=?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Erro ao validar login: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (validateLogin(username, password)) {
                JOptionPane.showMessageDialog(this, "Login bem-sucedido!");
                // Abrir a página inicial após o login
                new PaginaInicial(username);
                // Fechar a janela de login
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Nome de usuário ou senha inválidos.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}