import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Orcamentos extends JFrame {
    private Connection conn;
    private JTable tabelaOrcamentos;

    public Orcamentos() {
        setTitle("Painel - Orçamentos");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        connectToDatabase();
        criarTabelaOrcamentos();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(50, 50, 10, 50);

        JScrollPane scrollPane = new JScrollPane(tabelaOrcamentos);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Remover barra de rolagem horizontal
        panel.add(scrollPane, gbc);

        // Adicionar botão "Voltar à Página Inicial"
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

    private void criarTabelaOrcamentos() {
        try {
            String query = "SELECT aluno, mensalidade, status, valortotal FROM java.orcamentos";
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

            tabelaOrcamentos = new JTable(model);
            tabelaOrcamentos.setFillsViewportHeight(true); // Preencher toda a altura da tabela

            // Ajustando a altura das linhas com base no conteúdo
            for (int row = 0; row < tabelaOrcamentos.getRowCount(); row++) {
                int rowHeight = tabelaOrcamentos.getRowHeight();
                for (int column = 0; column < tabelaOrcamentos.getColumnCount(); column++) {
                    Component comp = tabelaOrcamentos.prepareRenderer(tabelaOrcamentos.getCellRenderer(row, column), row, column);
                    rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
                }
                tabelaOrcamentos.setRowHeight(row, rowHeight);
            }

            // Centralizando o conteúdo da célula
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            tabelaOrcamentos.setDefaultRenderer(Object.class, centerRenderer);

        } catch (SQLException e) {
            System.out.println("Erro ao recuperar dados da base de dados: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Orcamentos::new);
    }
}