package API;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


import DB.Models.Link;
import Services.Auth.AuthManager;
import Services.Auth.RegistrationManager;
import Services.LinkService;

public class MainUI {

    public static void main(String[] args) {
        // Создаём экземпляры AuthManager и RegistrationManager
        AuthManager authManager = new AuthManager();
        RegistrationManager registrationManager;
        try {
            registrationManager = new RegistrationManager();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Создаём главное окно (JFrame)
        JFrame frame = new JFrame("Авторизация");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new BorderLayout());

        // Создаём основную панель для размещения элементов
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Отступы

        // Текст "Введите UUID для авторизации"
        JLabel label = new JLabel("Введите UUID для авторизации");
        label.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрируем текст

        // Поле ввода (JTextField)
        JTextField uuidInput = new JTextField();
        uuidInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Фиксируем высоту

        // Кнопка "Создать нового пользователя"
        JButton createUserButton = new JButton("Создать нового пользователя");
        createUserButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрируем кнопку

        // Обработчик ввода UUID (нажатие Enter)
        uuidInput.addActionListener(e -> {
            String uuid = uuidInput.getText(); // Получаем введённый UUID
            if (uuid.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "UUID не может быть пустым!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = authManager.login(uuid); // Вызов метода login
            if (success) {
                JOptionPane.showMessageDialog(frame, "Успешная авторизация!", "Информация", JOptionPane.INFORMATION_MESSAGE);
                openUserPage(authManager, frame); // Переход на страницу пользователя
            } else {
                JOptionPane.showMessageDialog(frame, "Не удалось авторизоваться. Проверьте UUID.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Обработчик для кнопки "Создать нового пользователя"
        createUserButton.addActionListener(e -> {
            // Открытие окна для регистрации нового пользователя
            openRegistrationWindow(registrationManager);
        });

        // Добавляем элементы на панель
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Отступ
        panel.add(uuidInput);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Отступ
        panel.add(createUserButton);

        // Добавляем панель в окно
        frame.add(panel, BorderLayout.CENTER);

        // Делаем окно видимым
        frame.setVisible(true);
    }

    private static void openUserPage(AuthManager authManager, JFrame parentFrame) {
        // Закрываем текущее окно
        parentFrame.dispose();

        // Создаём экземпляр LinkService
        LinkService linkService = new LinkService();
        int currentUserId = authManager.getCurrentUserId(); // Здесь нужно использовать реальный ID текущего пользователя

        // Создаём новое окно для пользователя
        JFrame userFrame = new JFrame("Панель управления");
        userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userFrame.setSize(500, 400);
        userFrame.setLayout(new GridLayout(5, 1, 10, 10)); // Сетка с отступами

        // Кнопка "Посмотреть короткие ссылки"
        JButton viewLinksButton = new JButton("Посмотреть короткие ссылки");
        viewLinksButton.addActionListener(e -> {
            try {
                // Получаем список ссылок
                List<Link> links = linkService.getLinksByUserId(currentUserId);

                if (links.isEmpty()) {
                    JOptionPane.showMessageDialog(userFrame, "Нет доступных ссылок.", "Список ссылок", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Создаём массив данных для JTable
                    String[] columnNames = {"Короткая", "Длинная"};
                    String[][] data = new String[links.size()][2];

                    for (int i = 0; i < links.size(); i++) {
                        Link link = links.get(i);
                        data[i][0] = link.getShortLink();
                        data[i][1] = link.getLongLink();
                    }

                    // Создаём JTable
                    JTable table = new JTable(data, columnNames);
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                    // Добавляем возможность копирования текста
                    table.setDefaultEditor(Object.class, null); // Запрет редактирования
                    table.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mousePressed(java.awt.event.MouseEvent evt) {
                            if (evt.getClickCount() == 2 && table.getSelectedRow() != -1) {
                                // Копирование текста при двойном клике
                                int row = table.getSelectedRow();
                                int col = table.getSelectedColumn();
                                String value = table.getValueAt(row, col).toString();
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                                        new java.awt.datatransfer.StringSelection(value), null
                                );
                                JOptionPane.showMessageDialog(userFrame, "Скопировано: " + value, "Информация", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    });

                    // Помещаем таблицу в JScrollPane
                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setPreferredSize(new Dimension(600, 300));

                    // Отображаем таблицу в диалоговом окне
                    JOptionPane.showMessageDialog(userFrame, scrollPane, "Список ссылок", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(userFrame, "Ошибка при получении списка ссылок.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Кнопка "Создать новую ссылку"
        JButton createLinkButton = new JButton("Создать новую ссылку");
        createLinkButton.addActionListener(e -> {
            JTextField longLinkField = new JTextField();
            JTextField usesField = new JTextField();
            int option = JOptionPane.showConfirmDialog(userFrame, new Object[]{
                    "Введите длинную ссылку:", longLinkField,
                    "Количество доступных использований:", usesField
            }, "Создать новую ссылку", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String longLink = longLinkField.getText();
                try {
                    int uses = Integer.parseInt(usesField.getText());
                    String shortLink = linkService.addLink(currentUserId, "uuid", longLink, uses);
                    JOptionPane.showMessageDialog(userFrame, "Короткая ссылка: " + shortLink, "Успешно", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(userFrame, "Неверное значение для количества использований.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Кнопка "Перейти по ссылке"
        JButton goToLinkButton = new JButton("Перейти по ссылке");
        goToLinkButton.addActionListener(e -> {
            String shortLink = JOptionPane.showInputDialog(userFrame, "Введите короткую ссылку для перехода:", "Перейти по ссылке", JOptionPane.QUESTION_MESSAGE);
            if (shortLink != null) {
                try {
                    boolean isSuccessfully = linkService.goToResource(currentUserId, shortLink);
                    if (!isSuccessfully) {
                        JOptionPane.showMessageDialog(
                                userFrame,
                                "Ошибка при переходе по ссылке: закончилось доступное количество переходов или время ее доступности истекло\nСсылка будет удалена!",
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE
                        );
                        linkService.deleteLink(currentUserId, shortLink);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(userFrame, "Ошибка при переходе по ссылке.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Кнопка "Удалить ссылку"
        JButton deleteLinkButton = new JButton("Удалить ссылку");
        deleteLinkButton.addActionListener(e -> {
            String shortLink = JOptionPane.showInputDialog(userFrame, "Введите короткую ссылку для удаления:", "Удалить ссылку", JOptionPane.QUESTION_MESSAGE);
            if (shortLink != null) {
                try {
                    linkService.deleteLink(currentUserId, shortLink);
                    JOptionPane.showMessageDialog(userFrame, "Ссылка успешно удалена.", "Успешно", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(userFrame, "Ошибка при удалении ссылки.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Кнопка "Выйти из профиля"
        JButton logoutButton = new JButton("Выйти из профиля");
        logoutButton.addActionListener(e -> {
            authManager.logout(); // Вызов метода logout
            userFrame.dispose(); // Закрываем текущее окно
            main(null); // Перезапускаем программу
        });

        // Кнопка "Редактировать ссылку"

        JButton editLinkButton = new JButton("Редактировать ссылку");
        editLinkButton.addActionListener(e -> {
            String shortLink = JOptionPane.showInputDialog(userFrame, "Введите короткую ссылку для редактирования:", "Редактировать ссылку", JOptionPane.QUESTION_MESSAGE);
            if (shortLink != null) {
                openEditLinkWindow(linkService, currentUserId, shortLink);
            }
        });

        // Добавляем кнопки в окно
        userFrame.add(viewLinksButton);
        userFrame.add(createLinkButton);
        userFrame.add(goToLinkButton);
        userFrame.add(deleteLinkButton);
        userFrame.add(logoutButton);
        userFrame.add(editLinkButton);

        // Делаем окно видимым
        userFrame.setVisible(true);
    }

    private static void openRegistrationWindow(RegistrationManager registrationManager) {
        JFrame registrationFrame = new JFrame("Регистрация нового пользователя");
        registrationFrame.setSize(400, 250);
        registrationFrame.setLayout(new GridLayout(4, 1, 10, 10)); // Сетка с отступами
        registrationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Поле для ввода имени пользователя
        JLabel usernameLabel = new JLabel("Введите имя пользователя:");
        JTextField usernameInput = new JTextField();

        // Кнопка для подтверждения регистрации
        JButton registerButton = new JButton("Зарегистрировать");

        // Поле для отображения UUID
        JTextField uuidField = new JTextField();
        uuidField.setEditable(false); // Поле только для чтения
        uuidField.setHorizontalAlignment(JTextField.CENTER); // Центрируем текст

        // Кнопка для копирования UUID
        JButton copyButton = new JButton("Копировать UUID");
        copyButton.setEnabled(false); // Кнопка неактивна, пока UUID не будет сгенерирован
        copyButton.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new java.awt.datatransfer.StringSelection(uuidField.getText()), null
            );
            JOptionPane.showMessageDialog(registrationFrame, "UUID скопирован в буфер обмена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        });

        // Добавляем элементы на окно
        registrationFrame.add(usernameLabel);
        registrationFrame.add(usernameInput);
        registrationFrame.add(registerButton);
        registrationFrame.add(uuidField);
        registrationFrame.add(copyButton);

        // Обработчик для кнопки "Зарегистрировать"
        registerButton.addActionListener(event -> {
            String username = usernameInput.getText(); // Получаем имя пользователя
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(registrationFrame, "Имя пользователя не может быть пустым!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Проверяем, зарегистрирован ли уже пользователь
                if (registrationManager.checkAlreadyRegistered(username)) {
                    JOptionPane.showMessageDialog(registrationFrame, "Пользователь с таким именем уже существует!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Регистрируем нового пользователя
                String uuid = registrationManager.registerNewUser(username);
                uuidField.setText(uuid); // Отображаем UUID в текстовом поле
                copyButton.setEnabled(true); // Делаем кнопку "Копировать UUID" активной
                JOptionPane.showMessageDialog(registrationFrame, "Пользователь успешно зарегистрирован!", "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(registrationFrame, "Ошибка при регистрации пользователя.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Показываем окно регистрации
        registrationFrame.setVisible(true);
    }

    private static void openEditLinkWindow(LinkService linkService, int userId, String shortLink) {
        JFrame editFrame = new JFrame("Редактирование ссылки");
        editFrame.setSize(400, 300);
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.setLayout(new GridLayout(4, 2, 10, 10)); // Сетка с отступами

        // Поля для ввода новых значений
        JLabel longLinkLabel = new JLabel("Новая длинная ссылка (опционально):");
        JTextField longLinkField = new JTextField();

        JLabel usesLabel = new JLabel("Количество использований (опционально):");
        JTextField usesField = new JTextField();

        JCheckBox prolongLifeCheckBox = new JCheckBox("Продлить срок действия ссылки");

        // Кнопка для сохранения изменений
        JButton saveButton = new JButton("Сохранить изменения");

        // Добавляем элементы в окно
        editFrame.add(longLinkLabel);
        editFrame.add(longLinkField);

        editFrame.add(usesLabel);
        editFrame.add(usesField);

        editFrame.add(new JLabel()); // Пустая ячейка
        editFrame.add(prolongLifeCheckBox);

        editFrame.add(new JLabel()); // Пустая ячейка
        editFrame.add(saveButton);

        // Обработчик для кнопки "Сохранить изменения"
        saveButton.addActionListener(e -> {
            Optional<String> newLongLink = longLinkField.getText().isEmpty() ? Optional.empty() : Optional.of(longLinkField.getText());
            Optional<Integer> newNumberOfUses = usesField.getText().isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(usesField.getText()));
            Optional<Boolean> prolongLife = Optional.of(prolongLifeCheckBox.isSelected());

            try {
                // Вызов метода editLink
                linkService.editLink(userId, shortLink, newLongLink, newNumberOfUses, prolongLife);
                JOptionPane.showMessageDialog(editFrame, "Изменения успешно сохранены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                editFrame.dispose(); // Закрываем окно после сохранения
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editFrame, "Ошибка при сохранении изменений: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Показываем окно
        editFrame.setVisible(true);
    }

}
