import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabitTracker {
    private static final String FILE_NAME = "habits.txt";
    public static void main(String[] args) {
        JFrame frame = new JFrame("Habit Tracker");
        frame.setSize(700,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel habitListLabel = new JLabel("Habit List");
        habitListLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(habitListLabel, BorderLayout.NORTH);

        DefaultTableModel tableModel = new DefaultTableModel(
                new String[]{"Habit", "Date"}, 0
        );
        JTable habitTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(habitTable);
        panel.add(scrollPane, BorderLayout.CENTER);


        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(1, (date1, date2) -> {
                    LocalDateTime d1 = LocalDateTime.parse(
                            date1.toString(),
                            dateFormatter
                    );

                    LocalDateTime d2 = LocalDateTime.parse(
                            date2.toString(),
                            dateFormatter
                    );
                    return d1.compareTo(d2);
                });

        habitTable.setRowSorter(sorter);
        JPanel inputPanel = new JPanel();
        JLabel habitLabel = new JLabel("Habit:");
        JTextField habitField = new JTextField(25);
        JButton addButton = new JButton("Add");

        inputPanel.add(habitLabel);
        inputPanel.add(habitField);
        inputPanel.add(addButton);

        panel.add(inputPanel, BorderLayout.SOUTH);

        loadData(tableModel);

        addButton.addActionListener(e -> {
            String habit = habitField.getText().trim();
            if (!habit.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                String dateTime = now.format(dateFormatter);

                tableModel.addRow(new Object[]{habit, dateTime});
                saveData(tableModel);
                habitField.setText("");
            }
        });

        frame.add(panel);
        frame.setVisible(true);
        }

        private static void saveData(DefaultTableModel tableModel) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {

                for (int i = 0; i < tableModel.getRowCount(); i++) {

                    String habit = tableModel.getValueAt(i, 0).toString();
                    String date = tableModel.getValueAt(i, 1).toString();

                    writer.println(habit + "|" + date);
                }

            } catch (IOException e) {

                JOptionPane.showMessageDialog(
                        null,
                        "Error saving data: " + e.getMessage()
                );
            }
        }

    private static void loadData(DefaultTableModel tableModel) {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|", 2);

                if (parts.length == 2) {

                    tableModel.addRow(new Object[]{
                            parts[0],
                            parts[1]
                    });
                }
            }

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error loading data: " + e.getMessage()
            );
        }
    }
}