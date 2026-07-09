// package ca.udem.maville.swing_gui;

// import java.awt.BorderLayout;
// import java.awt.CardLayout;
// import java.awt.FlowLayout;
// import java.awt.GridBagConstraints;
// import java.awt.GridBagLayout;
// import java.awt.Insets;
// import java.io.IOException;

// import javax.swing.BorderFactory;
// import javax.swing.DefaultListModel;
// import javax.swing.JButton;
// import javax.swing.JComboBox;
// import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JList;
// import javax.swing.JOptionPane;
// import javax.swing.JPanel;
// import javax.swing.JPasswordField;
// import javax.swing.JScrollPane;
// import javax.swing.JTextField;
// import javax.swing.SwingConstants;
// import javax.swing.SwingUtilities;

// import ca.udem.maville.api.MavilleRestClient;
// import ca.udem.maville.model.Provider;
// import ca.udem.maville.model.Resident;
// import ca.udem.maville.model.User;

// public class MaVilleGUI extends JFrame {
//     private MavilleRestClient client;
//     private String currentUserType;
//     private Object currentUser;

//     private CardLayout cardLayout;
//     private JPanel mainPanel;

//     private static final String LOGIN_SCREEN = "login";
//     private static final String REGISTER_SCREEN = "register";
//     private static final String WELCOME_SCREEN = "welcome";
//     private static final String PROFILE_TYPE_SCREEN = "profileType";
//     private static final String RESIDENT_SCREEN = "resident";
//     private static final String PRESTATAIRE_SCREEN = "prestataire";
//     private static final String STPM_SCREEN = "stpm";

//     private JPanel loginPanel;
//     private JPanel registerPanel;
//     private JPanel welcomePanel;
//     private JPanel profileTypePanel;
//     private JPanel userSelectionPanel;

//     private JTextField loginUsernameField;
//     private JPasswordField loginPasswordField;
//     private JButton loginButton;
//     private JButton goToRegisterButton;

//     private JTextField registerUsernameField;
//     private JPasswordField registerPasswordField;
//     private JTextField registerEmailField;
//     private JTextField registerNeighbourhoodField;
//     private JTextField companyField;
//     private JTextField companyNumberField;
//     private JTextField submitterCategoryField;
//     private JButton registerButton;
//     private JButton goToLoginButton;

//     private JButton residentButton;
//     private JButton prestataireButton;
//     private JButton stpmButton;
//     private JButton quitButton;

//     private JList<Object> userList;
//     private DefaultListModel<Object> userListModel;
//     private JButton selectUserButton;
//     private JButton backToProfileButton;

//     private ResidentPanel residentPanel;
//     private PrestatairePanel prestatairePanel;
//     private STPMPanel stpmPanel;

//     public MaVilleGUI() {
//         super("Application MaVille");
//         initializeComponents();
//         setupLayout();
//         setupEventHandlers();
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setSize(1000, 700);
//         setLocationRelativeTo(null);
//         showLoginScreen();
//         setVisible(true);
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             new MaVilleGUI();
//         });
//     }

//     private void initializeComponents() {
//         client = new MavilleRestClient("http://localhost:7070");
//         cardLayout = new CardLayout();
//         mainPanel = new JPanel(cardLayout);
    
//         createWelcomeScreen();
//         createLoginScreen();
//         createRegisterScreen();
    
//         residentPanel = new ResidentPanel(client);
//         prestatairePanel = new PrestatairePanel(client);
//         stpmPanel = new STPMPanel(client);
//     }
    
//     private void createLoginScreen() {
//         loginPanel = new JPanel(new GridBagLayout());
//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(10, 10, 10, 10);
//         gbc.fill = GridBagConstraints.HORIZONTAL;

//         JLabel titleLabel = new JLabel("Connexion à MaVille", SwingConstants.CENTER);
//         titleLabel.setFont(titleLabel.getFont().deriveFont(24f));
//         gbc.gridx = 0;
//         gbc.gridy = 0;
//         gbc.gridwidth = 2;
//         loginPanel.add(titleLabel, gbc);

//         gbc.gridwidth = 1;
//         gbc.gridy = 1;
//         loginPanel.add(new JLabel("Nom d'utilisateur:"), gbc);

//         gbc.gridy = 2;
//         loginUsernameField = new JTextField(20);
//         loginPanel.add(loginUsernameField, gbc);

//         gbc.gridy = 3;
//         loginPanel.add(new JLabel("Mot de passe:"), gbc);

//         gbc.gridy = 4;
//         loginPasswordField = new JPasswordField(20);
//         loginPanel.add(loginPasswordField, gbc);

//         JPanel buttonPanel = new JPanel(new FlowLayout());
//         loginButton = new JButton("Se connecter");
//         goToRegisterButton = new JButton("S'inscrire");

//         buttonPanel.add(loginButton);
//         buttonPanel.add(goToRegisterButton);

//         gbc.gridy = 5;
//         gbc.gridwidth = 2;
//         loginPanel.add(buttonPanel, gbc);
//     }

//     private void createRegisterScreen() {
//         registerPanel = new JPanel(new GridBagLayout());
//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(10, 10, 10, 10);
//         gbc.fill = GridBagConstraints.HORIZONTAL;

//         JLabel titleLabel = new JLabel("Inscription à MaVille", SwingConstants.CENTER);
//         titleLabel.setFont(titleLabel.getFont().deriveFont(24f));
//         gbc.gridx = 0;
//         gbc.gridy = 0;
//         gbc.gridwidth = 2;
//         registerPanel.add(titleLabel, gbc);

//         gbc.gridwidth = 1;
        
//         // Row 1: Username
//         gbc.gridy = 1;
//         registerPanel.add(new JLabel("Nom d'utilisateur:"), gbc);
//         gbc.gridy = 2;
//         registerUsernameField = new JTextField(20);
//         registerPanel.add(registerUsernameField, gbc);

//         // Row 2: Email
//         gbc.gridy = 3;
//         registerPanel.add(new JLabel("Email:"), gbc);
//         gbc.gridy = 4;
//         registerEmailField = new JTextField(20);
//         registerPanel.add(registerEmailField, gbc);

//         // Row 3: Password
//         gbc.gridy = 5;
//         registerPanel.add(new JLabel("Mot de passe:"), gbc);
//         gbc.gridy = 6;
//         registerPasswordField = new JPasswordField(20);
//         registerPanel.add(registerPasswordField, gbc);

//         // Row 4: Neighbourhood
//         gbc.gridy = 7;
//         registerPanel.add(new JLabel("Quartier:"), gbc);
//         gbc.gridy = 8;
//         registerNeighbourhoodField = new JTextField(20);
//         registerPanel.add(registerNeighbourhoodField, gbc);

//         // Row 5: Role selection
//         gbc.gridy = 9;
//         registerPanel.add(new JLabel("Type de compte:"), gbc);
//         gbc.gridy = 10;
//         JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Resident", "Prestataire"});
//         registerPanel.add(roleComboBox, gbc);

//         // Row 6: Company name (only for Prestataire)
//         gbc.gridy = 11;
//         JLabel companyLabel = new JLabel("Nom de l'entreprise:");
//         companyLabel.setVisible(false);
//         registerPanel.add(companyLabel, gbc);
//         gbc.gridy = 12;
//         companyField = new JTextField(20);
//         companyField.setVisible(false);
//         registerPanel.add(companyField, gbc);

//         // Row 7: Company number (only for Prestataire)
//         gbc.gridy = 13;
//         JLabel companyNumberLabel = new JLabel("Numéro d'entreprise:");
//         companyNumberLabel.setVisible(false);
//         registerPanel.add(companyNumberLabel, gbc);
//         gbc.gridy = 14;
//         companyNumberField = new JTextField(20);
//         companyNumberField.setVisible(false);
//         registerPanel.add(companyNumberField, gbc);

//         // Row 8: Submitter category (only for Prestataire)
//         gbc.gridy = 15;
//         JLabel submitterCategoryLabel = new JLabel("Catégorie:");
//         submitterCategoryLabel.setVisible(false);
//         registerPanel.add(submitterCategoryLabel, gbc);
//         gbc.gridy = 16;
//         submitterCategoryField = new JTextField(20);
//         submitterCategoryField.setVisible(false);
//         registerPanel.add(submitterCategoryField, gbc);

//         // Show/hide Prestataire-specific fields based on role selection
//         roleComboBox.addActionListener(e -> {
//             boolean isPrestataire = "Prestataire".equals(roleComboBox.getSelectedItem());
//             companyLabel.setVisible(isPrestataire);
//             companyField.setVisible(isPrestataire);
//             companyNumberLabel.setVisible(isPrestataire);
//             companyNumberField.setVisible(isPrestataire);
//             submitterCategoryLabel.setVisible(isPrestataire);
//             submitterCategoryField.setVisible(isPrestataire);
//             registerPanel.revalidate();
//             registerPanel.repaint();
//         });

//         // Buttons
//         JPanel buttonPanel = new JPanel(new FlowLayout());
//         registerButton = new JButton("S'inscrire");
//         goToLoginButton = new JButton("Retour à la connexion");

//         registerButton.addActionListener(e -> {
//             String username = registerUsernameField.getText().trim();
//             String email = registerEmailField.getText().trim();
//             String password = new String(registerPasswordField.getPassword());
//             String neighbourhood = registerNeighbourhoodField.getText().trim();
//             String role = (String) roleComboBox.getSelectedItem();
            
//             if (username.isEmpty() || email.isEmpty() || password.isEmpty() || neighbourhood.isEmpty()) {
//                 JOptionPane.showMessageDialog(this, 
//                     "Veuillez remplir tous les champs obligatoires", 
//                     "Erreur", 
//                     JOptionPane.ERROR_MESSAGE);
//                 return;
//             }
            
//             if ("Prestataire".equals(role)) {
//                 if (companyField.getText().trim().isEmpty()) {
//                     JOptionPane.showMessageDialog(this, 
//                         "Le nom de l'entreprise est obligatoire pour les prestataires", 
//                         "Erreur", 
//                         JOptionPane.ERROR_MESSAGE);
//                     return;
//                 }
//                 performRegister(username, email, password, neighbourhood, role, 
//                                companyField.getText().trim(), 
//                                companyNumberField.getText().trim(), 
//                                submitterCategoryField.getText().trim());
//             } else {
//                 performRegister(username, email, password, neighbourhood, role, "", "", "");
//             }
//         });

//         goToLoginButton.addActionListener(e -> showLoginScreen());

//         buttonPanel.add(registerButton);
//         buttonPanel.add(goToLoginButton);

//         gbc.gridy = 17;
//         gbc.gridwidth = 2;
//         registerPanel.add(buttonPanel, gbc);
//     }

//     private void performRegister(String username, String email, String password, 
//                                 String neighbourhood, String role, String companyName, 
//                                 String companyNumber, String submittercategory) {
//         try {
//             User newUser;
            
//             if ("Resident".equals(role)) {
//                 newUser = new Resident();
//                 newUser.setUsername(username);
//                 newUser.setPassword(password);
//                 newUser.setEmail(email);
//                 newUser.setQuartier(neighbourhood);
//             } else { // Prestataire
//                 newUser = new Provider();
//                 newUser.setUsername(username);
//                 newUser.setPassword(password);
//                 newUser.setEmail(email);
//                 newUser.setQuartier(neighbourhood);
                
//                 Provider prestataire = (Provider) newUser;
//                 prestataire.setNomEntreprise(companyName);
//                 prestataire.setNumeroEntreprise(companyNumber.isEmpty() ? "N/A" : companyNumber);
//                 prestataire.setSubmittercategory(submittercategory.isEmpty() ? "default" : submittercategory);
//             }
            
//             boolean success = client.register(newUser);
//             if (success) {
//                 JOptionPane.showMessageDialog(this, 
//                     "Inscription réussie! Vous pouvez maintenant vous connecter.", 
//                     "Succès", 
//                     JOptionPane.INFORMATION_MESSAGE);
//                 showLoginScreen();
                
//                 // Clear registration fields
//                 registerUsernameField.setText("");
//                 registerEmailField.setText("");
//                 registerPasswordField.setText("");
//                 registerNeighbourhoodField.setText("");
//                 companyField.setText("");
//                 companyNumberField.setText("");
//                 submitterCategoryField.setText("");
//             } else {
//                 JOptionPane.showMessageDialog(this, 
//                     "Erreur lors de l'inscription. L'utilisateur existe peut-être déjà.", 
//                     "Erreur", 
//                     JOptionPane.ERROR_MESSAGE);
//             }
//         } catch (IOException ex) {
//             JOptionPane.showMessageDialog(this, 
//                 "Erreur de connexion au serveur: " + ex.getMessage(), 
//                 "Erreur", 
//                 JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     private void createWelcomeScreen() {
//         welcomePanel = new JPanel(new BorderLayout());
//         JPanel centerPanel = new JPanel(new GridBagLayout());
//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(20, 20, 20, 20);

//         JLabel titleLabel = new JLabel("Bienvenue dans l'Application MaVille", SwingConstants.CENTER);
//         titleLabel.setFont(titleLabel.getFont().deriveFont(24f));
//         gbc.gridx = 0;
//         gbc.gridy = 0;
//         centerPanel.add(titleLabel, gbc);

//         JLabel subtitleLabel = new JLabel("Système de gestion municipale", SwingConstants.CENTER);
//         subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(16f));
//         gbc.gridy = 1;
//         centerPanel.add(subtitleLabel, gbc);

//         if (client.getCurrentUser() != null) {
//             JLabel userLabel = new JLabel("Connecté en tant que: " + client.getCurrentUser().getUsername(),
//                     SwingConstants.CENTER);
//             userLabel.setFont(userLabel.getFont().deriveFont(14f));
//             gbc.gridy = 2;
//             centerPanel.add(userLabel, gbc);
//         }

//         JButton startButton = new JButton("Commencer");
//         startButton.setFont(startButton.getFont().deriveFont(16f));
//         startButton.addActionListener(e -> showProfileTypeScreen());
//         gbc.gridy = 3;
//         gbc.insets = new Insets(40, 20, 20, 20);
//         centerPanel.add(startButton, gbc);

//         JButton logoutButton = new JButton("Déconnexion");
//         logoutButton.addActionListener(e -> {
//             client = new MavilleRestClient("http://localhost:7070");
//             showLoginScreen();
//         });
//         gbc.gridy = 4;
//         gbc.insets = new Insets(10, 20, 20, 20);
//         centerPanel.add(logoutButton, gbc);

//         welcomePanel.add(centerPanel, BorderLayout.CENTER);
//     }

//     private void createProfileTypeScreen() {
//         profileTypePanel = new JPanel(new BorderLayout());
//         JPanel centerPanel = new JPanel(new GridBagLayout());
//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(10, 20, 10, 20);
//         gbc.fill = GridBagConstraints.HORIZONTAL;

//         JLabel titleLabel = new JLabel("=== Application MaVille ===", SwingConstants.CENTER);
//         titleLabel.setFont(titleLabel.getFont().deriveFont(20f));
//         gbc.gridx = 0;
//         gbc.gridy = 0;
//         gbc.gridwidth = 2;
//         centerPanel.add(titleLabel, gbc);

//         gbc.gridwidth = 1;
//         gbc.insets = new Insets(20, 20, 10, 20);

//         residentButton = new JButton("1. Profil Résident");
//         residentButton.setFont(residentButton.getFont().deriveFont(16f));
//         gbc.gridy = 1;
//         centerPanel.add(residentButton, gbc);

//         prestataireButton = new JButton("2. Profil Prestataire");
//         prestataireButton.setFont(prestataireButton.getFont().deriveFont(16f));
//         gbc.gridy = 2;
//         centerPanel.add(prestataireButton, gbc);

//         stpmButton = new JButton("3. Profil STPM");
//         stpmButton.setFont(stpmButton.getFont().deriveFont(16f));
//         gbc.gridy = 3;
//         centerPanel.add(stpmButton, gbc);

//         quitButton = new JButton("4. Quitter");
//         quitButton.setFont(quitButton.getFont().deriveFont(16f));
//         quitButton.addActionListener(e -> System.exit(0));
//         gbc.gridy = 4;
//         gbc.insets = new Insets(30, 20, 10, 20);
//         centerPanel.add(quitButton, gbc);

//         profileTypePanel.add(centerPanel, BorderLayout.CENTER);

//         JLabel instructionLabel = new JLabel("Choisissez votre profil:", SwingConstants.CENTER);
//         instructionLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
//         profileTypePanel.add(instructionLabel, BorderLayout.SOUTH);
//     }

//     private void createUserSelectionScreen() {
//         userSelectionPanel = new JPanel(new BorderLayout());

//         JLabel titleLabel = new JLabel("--- Sélection du Profil ---", SwingConstants.CENTER);
//         titleLabel.setFont(titleLabel.getFont().deriveFont(18f));
//         titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
//         userSelectionPanel.add(titleLabel, BorderLayout.NORTH);

//         userListModel = new DefaultListModel<>();
//         userList = new JList<>(userListModel);
//         userList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
//         JScrollPane scrollPane = new JScrollPane(userList);
//         scrollPane.setBorder(BorderFactory.createTitledBorder("Sélectionnez votre profil:"));
//         userSelectionPanel.add(scrollPane, BorderLayout.CENTER);

//         JPanel buttonPanel = new JPanel(new FlowLayout());
//         selectUserButton = new JButton("Sélectionner");
//         backToProfileButton = new JButton("Retour");

//         buttonPanel.add(backToProfileButton);
//         buttonPanel.add(selectUserButton);
//         userSelectionPanel.add(buttonPanel, BorderLayout.SOUTH);
//     }

//     private void setupLayout() {
//         mainPanel.add(loginPanel, LOGIN_SCREEN);
//         mainPanel.add(registerPanel, REGISTER_SCREEN);
//         mainPanel.add(residentPanel, RESIDENT_SCREEN);
//         mainPanel.add(prestatairePanel, PRESTATAIRE_SCREEN);
//         mainPanel.add(stpmPanel, STPM_SCREEN);
//         add(mainPanel, BorderLayout.CENTER);
//     }
    
//     private void setupEventHandlers() {
//         loginButton.addActionListener(e -> performLogin());
//         goToRegisterButton.addActionListener(e -> showRegisterScreen());
//     }

//     private void performLogin() {
//         String username = loginUsernameField.getText().trim();
//         String password = new String(loginPasswordField.getPassword());
    
//         if (username.isEmpty() || password.isEmpty()) {
//             JOptionPane.showMessageDialog(this,
//                     "Veuillez remplir tous les champs",
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//             return;
//         }
    
//         try {
//             boolean success = client.login(username, password);
    
//             if (success) {
//                 User user = client.getCurrentUser();
    
//                 if (user instanceof Resident) {
//                     showResidentScreen((Resident) user);
//                 } 
//                 else if (user instanceof Provider) {
//                     showPrestataireScreen((Provider) user);
//                 } 
//                 else {
//                     showSTpmScreen();
//                 }
//             } else {
//                 JOptionPane.showMessageDialog(this,
//                         "Nom d'utilisateur ou mot de passe incorrect",
//                         "Erreur de connexion",
//                         JOptionPane.ERROR_MESSAGE);
//             }
//         } catch (IOException ex) {
//             JOptionPane.showMessageDialog(this,
//                     "Erreur de connexion au serveur: " + ex.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }
    
//     private void showLoginScreen() {
//         cardLayout.show(mainPanel, LOGIN_SCREEN);
//     }

//     private void showRegisterScreen() {
//         cardLayout.show(mainPanel, REGISTER_SCREEN);
//     }

//     private void showWelcomeScreen() {
//         cardLayout.show(mainPanel, WELCOME_SCREEN);
//     }

//     private void showProfileTypeScreen() {
//         if (currentUserType != null && currentUserType.equals("RESIDENT")) {
//             Resident dummyResident = new Resident();
//             dummyResident.setName("Default Resident");
//             showResidentScreen(dummyResident);
//         }
//         else if (currentUserType != null && currentUserType.equals("PRESTATAIRE")) {
//             Provider dummyPrestataire = new Provider();
//             dummyPrestataire.setNomEntreprise("Default Prestataire");
//             showPrestataireScreen(dummyPrestataire);
//         } else if (currentUserType != null && currentUserType.equals("STPM")) {
//             showSTpmScreen();
//         } else {
//             cardLayout.show(mainPanel, PROFILE_TYPE_SCREEN);
//         }
//     }

//     private void showResidentScreen(Resident resident) {
//         residentPanel.setCurrentResident(resident);
//         residentPanel.setResidentInfo(resident.getName());
//         cardLayout.show(mainPanel, RESIDENT_SCREEN);
//     }

//     private void showPrestataireScreen(Provider prestataire) {
//         prestatairePanel.setCurrentPrestataire(prestataire);
//         cardLayout.show(mainPanel, PRESTATAIRE_SCREEN);
//     }

//     private void showSTpmScreen() {
//         cardLayout.show(mainPanel, STPM_SCREEN);
//     }
// }