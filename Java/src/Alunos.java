import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Alunos extends JFrame {
    private Connection conn;
    private JTable tabelaAlunos;

    public Alunos() {
        setTitle("Painel - Alunos");
        setSize(600, 300); // Aumentando a largura da janela para acomodar o novo botão
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        connectToDatabase();
        criarTabelaAlunos();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 50, 10, 50); // Margens para a tabela

        JScrollPane scrollPane = new JScrollPane(tabelaAlunos);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, gbc);

        JButton backButton = new JButton("Voltar");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new PaginaInicial();
            }
        });

        JButton otherButton = new JButton("Editar");
        otherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tabelaAlunos.getSelectedRow();
                if (selectedRow != -1) { // Verifica se uma linha está selecionada
                    // Obtém os dados da linha selecionada
                    String aluno = (String) tabelaAlunos.getValueAt(selectedRow, 0);
                    int idade = (int) tabelaAlunos.getValueAt(selectedRow, 1);
                    // Remove o prefixo "R$" e espaços em branco e tenta converter a mensalidade para Double
                    String mensalidadeString = ((String) tabelaAlunos.getValueAt(selectedRow, 2)).replaceAll("[^0-9.]", "");
                    Double mensalidade = null;
                    try {
                        mensalidade = Double.parseDouble(mensalidadeString);
                    } catch (NumberFormatException ex) {
                        // Tratamento de erro se a mensalidade não puder ser convertida em Double
                        ex.printStackTrace();
                    }
                    String contato = (String) tabelaAlunos.getValueAt(selectedRow, 3);

                    // Abre uma nova janela de edição com os dados
                    new AtualizarAlunos(aluno, idade, mensalidade, contato);
                } else {
                    JOptionPane.showMessageDialog(Alunos.this, "Por favor, selecione um aluno para editar.", "Nenhum aluno selecionado", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        // Definindo uma largura preferencial para os botões
        Dimension buttonDimension = new Dimension(100, 30);
        backButton.setPreferredSize(buttonDimension);
        otherButton.setPreferredSize(buttonDimension);
        refreshButton.setPreferredSize(buttonDimension);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0)); // Alterado para GridLayout com 3 colunas
        buttonPanel.add(backButton);
        buttonPanel.add(otherButton);
        buttonPanel.add(refreshButton); // Adicionando o novo botão Refresh

        gbc.gridy++;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 50, 10, 50);
        panel.add(buttonPanel, gbc);

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

    private void criarTabelaAlunos() {
        try {
            String query = "SELECT aluno, idade, mensalidade, contato FROM alunos";
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

            tabelaAlunos = new JTable(model);
            tabelaAlunos.setFillsViewportHeight(true);

            for (int row = 0; row < tabelaAlunos.getRowCount(); row++) {
                int rowHeight = tabelaAlunos.getRowHeight();
                for (int column = 0; column < tabelaAlunos.getColumnCount(); column++) {
                    Component comp = tabelaAlunos.prepareRenderer(tabelaAlunos.getCellRenderer(row, column), row, column);
                    rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
                }
                tabelaAlunos.setRowHeight(row, rowHeight);
            }

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            tabelaAlunos.setDefaultRenderer(Object.class, centerRenderer);

        } catch (SQLException e) {
            System.out.println("Erro ao recuperar dados da base de dados: " + e.getMessage());
        }
    }

    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) tabelaAlunos.getModel();
        model.setRowCount(0); // Limpa todos os dados da tabela

        // Recupera os dados do banco de dados novamente e atualiza a tabela
        try {
            String query = "SELECT aluno, idade, mensalidade, contato FROM alunos";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getString("aluno"),
                        resultSet.getInt("idade"),
                        String.valueOf(resultSet.getDouble("mensalidade")), // Convertendo para String
                        resultSet.getString("contato")
                };
                model.addRow(row);
            }

            // Atualiza a altura das linhas da tabela
            for (int row = 0; row < tabelaAlunos.getRowCount(); row++) {
                int rowHeight = tabelaAlunos.getRowHeight();
                for (int column = 0; column < tabelaAlunos.getColumnCount(); column++) {
                    Component comp = tabelaAlunos.prepareRenderer(tabelaAlunos.getCellRenderer(row, column), row, column);
                    rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
                }
                tabelaAlunos.setRowHeight(row, rowHeight);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao recuperar dados da base de dados: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Alunos::new);
    }
}