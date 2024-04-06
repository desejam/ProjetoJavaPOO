import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class AtualizarAlunos extends JFrame {
    private JTextField alunoField;
    private JTextField idadeField;
    private JTextField mensalidadeField;
    private JTextField contatoField;
    private Connection connection;
    private List<AtualizacaoListener> listeners = new ArrayList<>();

    public AtualizarAlunos(String aluno, int idade, double mensalidade, String contato) {
        setTitle("Editar Aluno");
        setSize(350, 200); // Ajustando a largura para acomodar melhor os campos
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10)); // 0 rows means unlimited rows

        // Labels e campos de texto
        addFormField(panel, "Nome do Aluno:", aluno);
        addFormField(panel, "Idade:", Integer.toString(idade));
        addFormField(panel, "Mensalidade (R$):", Double.toString(mensalidade));
        addFormField(panel, "Contato (Telefone/Email):", contato);

        // Botões
        JButton salvarButton = new JButton("Salvar");
        salvarButton.addActionListener(e -> {
            // Lógica para salvar as alterações no banco de dados
            atualizarBancoDeDados(alunoField.getText(), Integer.parseInt(idadeField.getText()), Double.parseDouble(mensalidadeField.getText()), contatoField.getText());
            // Notificar os ouvintes sobre a atualização bem-sucedida
            notifyListeners();
            // Após salvar, exibir mensagem de sucesso e fechar esta janela
            JOptionPane.showMessageDialog(this, "Dados atualizados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        panel.add(new JLabel()); // Espaço em branco para alinhar com os campos
        panel.add(salvarButton);

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(e -> dispose());
        panel.add(new JLabel()); // Espaço em branco para alinhar com os campos
        panel.add(cancelarButton);

        add(panel);
        setVisible(true);
    }

    // Método para adicionar rótulos e campos de texto ao painel
    private void addFormField(JPanel panel, String labelText, String defaultValue) {
        JLabel label = new JLabel(labelText);
        panel.add(label);

        JTextField textField = new JTextField(defaultValue);
        panel.add(textField);

        if (labelText.equals("Nome do Aluno:")) {
            alunoField = textField;
        } else if (labelText.equals("Idade:")) {
            idadeField = textField;
        } else if (labelText.equals("Mensalidade (R$):")) {
            mensalidadeField = textField;
        } else if (labelText.equals("Contato (Telefone/Email):")) {
            contatoField = textField;
        }
    }

    // Método para atualizar o banco de dados
    private void atualizarBancoDeDados(String aluno, int idade, double mensalidade, String contato) {
        try {
            // Carregar o driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Estabelecer a conexão com o banco de dados
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java", "administrador", "password1234");
            // Consulta SQL para atualizar os dados do aluno
            String sql = "UPDATE alunos SET idade = ?, mensalidade = ?, contato = ? WHERE aluno = ?";
            // Preparar a declaração SQL
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, idade);
            statement.setDouble(2, mensalidade);
            statement.setString(3, contato);
            statement.setString(4, aluno);
            // Executar a atualização
            statement.executeUpdate();
            // Fechar a declaração e a conexão
            statement.close();
            connection.close();
            System.out.println("Dados do aluno atualizados com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para adicionar ouvintes
    public void addListener(AtualizacaoListener listener) {
        listeners.add(listener);
    }

    // Método para notificar os ouvintes sobre a atualização bem-sucedida
    private void notifyListeners() {
        for (AtualizacaoListener listener : listeners) {
            listener.atualizacaoBemSucedida();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Exemplo de uso
            new AtualizarAlunos("João", 25, 150.0, "joao@example.com");
        });
    }
}

interface AtualizacaoListener {
    void atualizacaoBemSucedida();
}