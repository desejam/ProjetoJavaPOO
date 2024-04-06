import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PaginaInicial extends JFrame {

    private JLabel labelBemVindo;

    public PaginaInicial(String username) {
        setTitle("Painel Administrativo");
        setSize(400, 150); // Aumentando o tamanho para acomodar os botões
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        criarComponentes(username);

        setVisible(true);
    }

    public PaginaInicial() {

    }

    private void criarComponentes(String username) {
        JPanel panel = new JPanel(new GridLayout(3, 1)); // 3 linhas, 1 coluna

        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        labelBemVindo = new JLabel("Bem-vindo, " + username, SwingConstants.CENTER);
        panelUsuario.add(labelBemVindo);

        JButton botaoLogout = new JButton("Sair");
        botaoLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fechar a janela atual
                // Adicionar o código para voltar para a tela principal (main)
                SwingUtilities.invokeLater(() -> new Main()); // Recarregar a tela principal com o usuário padrão
            }
        });
        panelUsuario.add(botaoLogout);

        panel.add(panelUsuario);

        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // Layout de fluxo centralizado com espaçamento horizontal de 20 pixels entre os componentes

        JButton botaoAlunos = new JButton("Alunos");
        botaoAlunos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir a página de alunos
                new Alunos().setVisible(true);
            }
        });
        panelBotoes.add(botaoAlunos);

        JButton botaoOrcamento = new JButton("Orçamento");
        botaoOrcamento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir a página de orçamento
                new Orcamentos().setVisible(true);
            }
        });
        panelBotoes.add(botaoOrcamento);

        JButton botaoEstoque = new JButton("Estoque");
        botaoEstoque.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir a página de estoque
                new Estoque().setVisible(true);
            }
        });
        panelBotoes.add(botaoEstoque);

        panel.add(panelBotoes);

        add(panel);
    }

    public static void main(String[] args) {
        // Substitua as informações do banco de dados com as suas
        String url = "jdbc:mysql://localhost:3306/java";
        String username = "administrador";
        String password = "password1234";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT usuario FROM usuarios WHERE id = 1"); // Assumindo que o usuário logado tenha o ID 1

            String nomeUsuario;
            if (resultSet.next()) {
                nomeUsuario = resultSet.getString("usuario");
            } else {
                nomeUsuario = "Usuário"; // Mensagem padrão se nenhum usuário estiver logado
            }

            SwingUtilities.invokeLater(() -> new PaginaInicial(nomeUsuario));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}