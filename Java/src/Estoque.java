import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Estoque extends JFrame {
    private Connection conn;
    private JTable tabelaEstoque;

    public Estoque() {
        setTitle("Painel - Estoque");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        connectToDatabase();
        criarTabelaEstoque();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(50, 50, 10, 50); // Alteração das margens para acomodar o botão "Voltar"

        JScrollPane scrollPane = new JScrollPane(tabelaEstoque);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Remover barra de rolagem horizontal
        panel.add(scrollPane, gbc);

        JButton backButton = new JButton("Voltar");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fechar a janela atual
                // Adicionar o código para voltar para a tela principal (main)
                SwingUtilities.invokeLater(() -> new PaginaInicial()); // Recarregar a tela principal com o usuário padrão
            }
        });
        gbc.gridy++;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(backButton, gbc);

        add(panel);
        setVisible(true);
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/java";
        String user = "administrador";
        String password = "password1234";

        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    private void criarTabelaEstoque() {
        try {
            String query = "SELECT camisas, chuteiras, bolas, cones FROM estoque";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int numColumns = metaData.getColumnCount();

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (int i = 1; i <= numColumns; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            while (resultSet.next()) {
                Object[] row = new Object[numColumns];
                for (int i = 1; i <= numColumns; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                model.addRow(row);
            }

            tabelaEstoque = new JTable(model);
            tabelaEstoque.setFillsViewportHeight(true); // Preencher toda a altura da tabela

            // Ajustando a altura das linhas com base no conteúdo
            for (int row = 0; row < tabelaEstoque.getRowCount(); row++) {
                int rowHeight = tabelaEstoque.getRowHeight();
                for (int column = 0; column < tabelaEstoque.getColumnCount(); column++) {
                    Component comp = tabelaEstoque.prepareRenderer(tabelaEstoque.getCellRenderer(row, column), row, column);
                    rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
                }
                tabelaEstoque.setRowHeight(row, rowHeight);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao recuperar dados da base de dados: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Estoque::new);
    }
}