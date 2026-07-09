// package ca.udem.maville.swing_gui;

// import java.awt.BorderLayout;
// import java.awt.FlowLayout;
// import java.awt.GridBagConstraints;
// import java.awt.GridBagLayout;
// import java.awt.GridLayout;
// import java.awt.Insets; // Ensure Work class is imported
// import java.awt.event.ActionListener; // Import ProblemType enum

// import javax.swing.BorderFactory;
// import javax.swing.DefaultListModel;
// import javax.swing.JButton;
// import javax.swing.JComboBox;
// import javax.swing.JLabel;
// import javax.swing.JList;
// import javax.swing.JOptionPane;
// import javax.swing.JPanel;
// import javax.swing.JScrollPane;
// import javax.swing.JTabbedPane;
// import javax.swing.JTable;
// import javax.swing.JTextArea;
// import javax.swing.JTextField;
// import javax.swing.SwingConstants;
// import javax.swing.table.DefaultTableModel;

// import ca.udem.maville.api.MavilleRestClient;
// import ca.udem.maville.model.Problem;
// import ca.udem.maville.model.Resident;
// import ca.udem.maville.model.Work;
// import ca.udem.maville.model.WorkType;

// /**
//  * Panel d'interface graphique pour les résidents municipaux.
//  * 
//  * <p>
//  * Fournit des fonctionnalités permettant aux résidents de :
//  * <ul>
//  * <li>Consulter les travaux en cours et à venir</li>
//  * <li>Filtrer les travaux par type ou quartier</li>
//  * <li>Signaler des problèmes dans leur quartier</li>
//  * <li>Gérer leurs abonnements et notifications</li>
//  * </ul>
//  * 
//  * <p>
//  * Interagit avec le backend via {@link MavilleRestClient} pour récupérer et
//  * mettre à jour les données.
//  */
// public class ResidentPanel extends JPanel {
//     /** Client REST pour communiquer avec le backend. */
//     private MavilleRestClient client;
//     /** Résident actuellement connecté. */
//     private Resident currentResident;
//     /** Onglets principaux de l'interface résident. */
//     private JTabbedPane residentTabs;
//     /** Tableau pour afficher les travaux d'infrastructure. */
//     private JTable worksTable;
//     /** Modèle de données pour le tableau des travaux. */
//     private DefaultTableModel worksTableModel;
//     /** Zone de texte pour la description des problèmes signalés. */
//     private JTextArea problemDescriptionArea;
//     /** Champ de texte pour saisir le type de problème signalé. */
//     private JComboBox<String> problemTypeComboBox;
//     /** Champ de texte pour saisir l'emplacement du problème signalé. */
//     private JTextField problemLocationField;
//     /** Champ de texte pour saisir la rue du problème signalé. */
//     private JTextField problemStreetField;

//     /** Champ de texte pour saisir le quartier du problème signalé. */
//     private JTextField problemNeighbourhoodField;
//     /**
//      * Champ de texte pour saisir les coordonnées du résident signalant le problème.
//      */
//     private JTextField problemContactField;
//     /** Liste pour afficher les notifications et abonnements du résident. */
//     private JList<String> notificationList;
//     /** Modèle de données pour la liste des notifications. */
//     private DefaultListModel<String> notificationListModel;
//     /** Bouton pour revenir au menu de sélection. */
//     private JButton backButton;
//     /** Label d'en-tête pour afficher le nom du résident connecté. */
//     private JLabel headerLabel;

//     /**
//      * Constructeur principal.
//      * 
//      * @param client Client REST initialisé pour les communications backend
//      */
//     public ResidentPanel(MavilleRestClient client) {
//         this.client = client;
//         setLayout(new BorderLayout());
//         initializeComponents();
//     }

//     /**
//      * Définit le résident actuellement connecté.
//      * 
//      * @param resident Résident connecté
//      */
//     public void setCurrentResident(Resident resident) {
//         this.currentResident = resident;
//     }

//     /**
//      * Affiche les informations du résident dans l'en-tête.
//      * 
//      * @param residentName Nom du résident à afficher
//      */
//     private void initializeComponents() {
//         headerLabel = new JLabel("", SwingConstants.CENTER); // Initialize headerLabel
//         headerLabel.setFont(headerLabel.getFont().deriveFont(16f));
//         headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//         add(headerLabel, BorderLayout.NORTH);

//         residentTabs = new JTabbedPane();
//         residentTabs.addTab("Travaux en cours", createWorksCurrentPanel());
//         residentTabs.addTab("Travaux à venir", createWorksUpcomingPanel());
//         residentTabs.addTab("Filtrer travaux", createWorksFilterPanel());
//         residentTabs.addTab("Signaler problème", createProblemReportPanel());
//         residentTabs.addTab("Notifications", createNotificationsPanel());

//         add(residentTabs, BorderLayout.CENTER);

//         JPanel bottomPanel = new JPanel(new FlowLayout());
//         backButton = new JButton("Retour au menu de sélection");
//         bottomPanel.add(backButton);
//         add(bottomPanel, BorderLayout.SOUTH);
//     }

//     /**
//      * Met à jour l'en-tête pour afficher le nom du résident connecté.
//      * 
//      * @param residentName Nom du résident connecté
//      */
//     public void setResidentInfo(String residentName) {
//         headerLabel.setText("--- Menu Résident (Connecté: " + residentName + ") ---");
//     }

//     /**
//      * Définit l'action à exécuter lorsque le bouton de retour est cliqué.
//      * 
//      * @param listener ActionListener à associer au bouton de retour
//      */
//     public void setBackButtonAction(ActionListener listener) {
//         backButton.addActionListener(listener);
//     }

//     /**
//      * Crée le panneau pour afficher les travaux en cours.
//      * 
//      * @return JPanel contenant la liste des travaux en cours
//      */
//     private JPanel createWorksCurrentPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Travaux en cours"));

//         // Table for current works
//         worksTableModel = new DefaultTableModel(
//                 new Object[] { "Arrondissement", "Statut", "Motif du travail",
//                         "Catégorie d'intervenant", "Nom de l'intervenant", "Date de fin", "Date de début" },
//                 0);
//         worksTable = new JTable(worksTableModel);
//         JScrollPane scrollPane = new JScrollPane(worksTable);
//         panel.add(scrollPane, BorderLayout.CENTER);

//         // Load current works on initialization
//         loadCurrentWorks();

//         // Refresh button
//         JPanel buttonPanel = new JPanel(new FlowLayout());
//         JButton refreshButton = new JButton("Actualiser");
//         refreshButton.addActionListener(e -> loadCurrentWorks());
//         buttonPanel.add(refreshButton);
//         panel.add(buttonPanel, BorderLayout.SOUTH);

//         return panel;
//     }

//     /**
//      * Crée le panneau pour afficher les travaux à venir.
//      * 
//      * @return JPanel contenant la liste des travaux à venir
//      */
//     private JPanel createWorksUpcomingPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Travaux à venir (3 prochains mois)"));

//         DefaultTableModel upcomingTableModel = new DefaultTableModel(
//                 new Object[] { "Arrondissement", "Statut", "Motif du travail",
//                         "Catégorie d'intervenant", "Nom de l'intervenant", "Date de fin", "Date de début" },
//                 0);
//         JTable upcomingTable = new JTable(upcomingTableModel);
//         JScrollPane scrollPane = new JScrollPane(upcomingTable);
//         panel.add(scrollPane, BorderLayout.CENTER);

//         JPanel buttonPanel = new JPanel(new FlowLayout());
//         JButton refreshButton = new JButton("Actualiser");
//         refreshButton.addActionListener(e -> loadUpcomingWorks(upcomingTableModel));
//         buttonPanel.add(refreshButton);
//         panel.add(buttonPanel, BorderLayout.SOUTH);

//         // Load upcoming works on initialization
//         loadUpcomingWorks(upcomingTableModel);

//         return panel;
//     }

//     /**
//      * Crée le panneau pour filtrer les travaux.
//      * 
//      * @return JPanel contenant les contrôles de filtrage et la table des résultats
//      */
//     private JPanel createWorksFilterPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Filtrer les travaux"));

//         // Filter controls
//         JPanel filterPanel = new JPanel(new FlowLayout());
//         filterPanel.add(new JLabel("Filtrer par:"));

//         JButton showAllButton = new JButton("Tout afficher");
//         JButton filterByNeighborhoodButton = new JButton("Par quartier");
//         JButton filterByTypeButton = new JButton("Par type");

//         filterPanel.add(showAllButton);
//         filterPanel.add(filterByNeighborhoodButton);
//         filterPanel.add(filterByTypeButton);

//         panel.add(filterPanel, BorderLayout.NORTH);

//         // Results table
//         DefaultTableModel filteredTableModel = new DefaultTableModel(
//                 new Object[] { "Arrondissement", "Statut", "Motif du travail",
//                         "Catégorie d'intervenant", "Nom de l'intervenant", "Date de fin", "Date de début" },
//                 0);
//         JTable filteredTable = new JTable(filteredTableModel);
//         JScrollPane scrollPane = new JScrollPane(filteredTable);
//         panel.add(scrollPane, BorderLayout.CENTER);

//         // Event handlers for filter buttons
//         showAllButton.addActionListener(e -> loadAllWorks(filteredTableModel));
//         filterByNeighborhoodButton.addActionListener(e -> filterWorksByNeighborhood(filteredTableModel));
//         filterByTypeButton.addActionListener(e -> filterWorksByType(filteredTableModel));

//         return panel;
//     }

//     /**
//      * Crée le panneau pour signaler un problème.
//      * 
//      * @return JPanel contenant le formulaire de signalement de problème
//      */

//     private JPanel createProblemReportPanel() {
//         JPanel panel = new JPanel(new GridBagLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Signaler un problème"));

//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(5, 5, 5, 5);
//         gbc.anchor = GridBagConstraints.WEST;

//         // Quartier
//         gbc.gridx = 0;
//         gbc.gridy = 0;
//         panel.add(new JLabel("Quartier du problème:"), gbc);
//         gbc.gridx = 1;
//         // Rue
//         gbc.gridx = 0;
//         gbc.gridy = 1;
//         panel.add(new JLabel("Rue du problème:"), gbc);
//         gbc.gridx = 1;
//         problemStreetField = new JTextField(20);
//         panel.add(problemStreetField, gbc);

//         // Quartier
//         gbc.gridx = 0;
//         gbc.gridy = 2;
//         panel.add(new JLabel("Quartier du problème:"), gbc);
//         gbc.gridx = 1;
//         problemNeighbourhoodField = new JTextField(20);
//         panel.add(problemNeighbourhoodField, gbc);
//         gbc.gridx = 1;
//         problemStreetField = new JTextField(20);
//         panel.add(problemStreetField, gbc);

//         // Type
//         gbc.gridx = 0;
//         gbc.gridy = 2;
//         panel.add(new JLabel("Type de problème:"), gbc);
//         gbc.gridx = 1;
//         problemTypeComboBox = new JComboBox<>(new String[] {
//                 "TRAVAUX_ROUTIER",
//                 "TRAVAUX_GAZ_ELECTRICITE",
//                 "CONSTRUCTION_RENOVATION",
//                 "ENTRETIEN_PAYSAGER",
//                 "TRANSPORTS_EN_COMMUN",
//                 "SIGNALISATION_ECLAIRAGE",
//                 "TRAVAUX_SOUTERRAINS",
//                 "TRAVAUX_RESIDENTIEL",
//                 "ENTRETIEN_URBAIN",
//                 "RESEAUX_TELECOMMUNICATION" });
//         panel.add(problemTypeComboBox, gbc);

//         // Description
//         gbc.gridx = 0;
//         gbc.gridy = 3;
//         panel.add(new JLabel("Description:"), gbc);
//         gbc.gridx = 1;
//         problemDescriptionArea = new JTextArea(4, 20);
//         panel.add(new JScrollPane(problemDescriptionArea), gbc);

//         // Contact
//         gbc.gridx = 0;
//         gbc.gridy = 4;
//         panel.add(new JLabel("Vos coordonnées:"), gbc);
//         gbc.gridx = 1;
//         problemContactField = new JTextField(20);
//         panel.add(problemContactField, gbc);

//         gbc.gridx = 1;
//         gbc.gridy = 5;
//         gbc.anchor = GridBagConstraints.EAST;
//         JButton submitButton = new JButton("Signaler le problème");
//         submitButton.addActionListener(e -> submitProblem());
//         panel.add(submitButton, gbc);

//         return panel;
//     }

//     /**
//      * Crée le panneau pour gérer les notifications et abonnements.
//      * 
//      * @return JPanel contenant les contrôles de notification
//      */
//     private JPanel createNotificationsPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Gérer les notifications et abonnements"));

//         // Buttons for different notification actions
//         JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
//         buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//         JButton viewSubscriptionsButton = new JButton("Consulter abonnements");
//         JButton subscribeButton = new JButton("S'abonner");
//         JButton unsubscribeButton = new JButton("Se désabonner");
//         JButton viewAllNotificationsButton = new JButton("Consulter toutes les notifications");

//         buttonPanel.add(viewSubscriptionsButton);
//         buttonPanel.add(subscribeButton);
//         buttonPanel.add(unsubscribeButton);
//         buttonPanel.add(viewAllNotificationsButton);

//         panel.add(buttonPanel, BorderLayout.NORTH);

//         // Notifications display area - changed to String
//         notificationListModel = new DefaultListModel<>(); // String type
//         notificationList = new JList<>(notificationListModel); // String type
//         JScrollPane scrollPane = new JScrollPane(notificationList);
//         scrollPane.setBorder(BorderFactory.createTitledBorder("Résultats"));
//         panel.add(scrollPane, BorderLayout.CENTER);

//         /** 
//         // Add event handlers for notification buttons
//         viewSubscriptionsButton.addActionListener(e -> viewSubscriptions());
//         subscribeButton.addActionListener(e -> subscribe());
//         unsubscribeButton.addActionListener(e -> unsubscribe());
//         viewAllNotificationsButton.addActionListener(e -> viewAllNotifications());
//          */
//         return panel;
//     }
//     /**
//      * Charge les travaux en cours depuis le backend et les affiche dans le tableau.
//      */
//     private void loadCurrentWorks() {
//         try {
//             worksTableModel.setRowCount(0);
//             java.util.List<Work> travaux = client.getInProgressWorks();

//             for (Work t : travaux) {
//                 worksTableModel.addRow(new Object[] {
//                         t.getBorough(), t.getStatus(),
//                         t.getCategory(), t.getServiceProvider(),
//                         t.getStartDate(), t.getEndDate()
//                 });
//             }
//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(null,
//                     "Erreur lors du chargement des travaux: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Charge les travaux à venir depuis le backend et les affiche dans le tableau.
//      * 
//      * @param tableModel Modèle de données du tableau pour les travaux à venir
//      */
//     private void loadUpcomingWorks(DefaultTableModel tableModel) {
//         try {
//             tableModel.setRowCount(0);
//             java.util.List<Work> travaux = client.getUpcomingWorks();

//             for (Work t : travaux) {
//                 tableModel.addRow(new Object[] {
//                     t.getBorough(), t.getStatus(),
//                     t.getCategory(), t.getServiceProvider(),
//                     t.getStartDate(), t.getEndDate()
//                 });
//             }
//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(null,
//                     "Erreur lors du chargement des travaux à venir: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Charge tous les travaux depuis le backend et les affiche dans le tableau.
//      * 
//      * @param tableModel Modèle de données du tableau pour tous les travaux
//      */
//     private void loadAllWorks(DefaultTableModel tableModel) {
//         try {
//             tableModel.setRowCount(0);
//             java.util.List<Work> travaux = client.getAllWorks();

//             for (Work t : travaux) {
//                 tableModel.addRow(new Object[] {
//                     t.getBorough(), t.getStatus(),
//                     t.getCategory(), t.getServiceProvider(),
//                     t.getStartDate(), t.getEndDate()
//                 });
//             }

//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(null,
//                     "Erreur lors du chargement de tous les travaux: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Filtre les travaux par quartier et met à jour le tableau.
//      * 
//      * @param tableModel Modèle de données du tableau pour les travaux filtrés
//      */
//     private void filterWorksByNeighborhood(DefaultTableModel tableModel) {
//         // Create components for custom dialog
//         JTextField neighborhoodField = new JTextField(20);
//         JPanel panel = new JPanel(new BorderLayout(5, 5));
//         panel.add(new JLabel("Entrez le nom du quartier:"), BorderLayout.NORTH);
//         panel.add(neighborhoodField, BorderLayout.CENTER);

//         // Show input dialog
//         int result = JOptionPane.showConfirmDialog(
//                 null,
//                 panel,
//                 "Filtrer par quartier",
//                 JOptionPane.OK_CANCEL_OPTION,
//                 JOptionPane.PLAIN_MESSAGE);

//         if (result != JOptionPane.OK_OPTION)
//             return;

//         String neighborhood = neighborhoodField.getText().trim();
//         if (neighborhood.isEmpty()) {
//             JOptionPane.showMessageDialog(null,
//                     "Veuillez entrer un nom de quartier",
//                     "Entrée invalide",
//                     JOptionPane.WARNING_MESSAGE);
//             return;
//         }

//         try {
//             tableModel.setRowCount(0);
//             java.util.List<Work> travaux = client.getWorksByNeighbourhood(neighborhood);

//             if (travaux.isEmpty()) {
//                 JOptionPane.showMessageDialog(null,
//                         "Aucun travail trouvé pour: " + neighborhood
//                                 + "\nVérifiez l'orthographe ou essayez un quartier différent",
//                         "Aucun résultat",
//                         JOptionPane.INFORMATION_MESSAGE);
//                 return;
//             }

//             for (Work t : travaux) {
//                 tableModel.addRow(new Object[] {
//                     t.getBorough(), t.getStatus(),
//                     t.getCategory(), t.getServiceProvider(),
//                     t.getStartDate(), t.getEndDate()
//                 });
//             }

//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(null,
//                     "Erreur lors du filtrage par quartier: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Filtre les travaux par type et met à jour le tableau.
//      * 
//      * @param tableModel Modèle de données du tableau pour les travaux filtrés
//      */
//     private void filterWorksByType(DefaultTableModel tableModel) {
//         // Define the work types
//         String[] workTypes = {
//                 "TRAVAUX_ROUTIER",
//                 "TRAVAUX_GAZ_ELECTRICITE",
//                 "CONSTRUCTION_RENOVATION",
//                 "ENTRETIEN_PAYSAGER",
//                 "TRANSPORTS_EN_COMMUN",
//                 "SIGNALISATION_ECLAIRAGE",
//                 "TRAVAUX_SOUTERRAINS",
//                 "TRAVAUX_RESIDENTIEL",
//                 "ENTRETIEN_URBAIN",
//                 "RESEAUX_TELECOMMUNICATION"
//         };

//         // Create combobox with work types
//         JComboBox<String> typeComboBox = new JComboBox<>(workTypes);
//         typeComboBox.setSelectedIndex(0); // Select first item by default

//         // Show selection dialog
//         int result = JOptionPane.showConfirmDialog(
//                 null,
//                 typeComboBox,
//                 "Choisir le type de travail",
//                 JOptionPane.OK_CANCEL_OPTION,
//                 JOptionPane.QUESTION_MESSAGE);

//         if (result == JOptionPane.OK_OPTION) {
//             String selectedType = (String) typeComboBox.getSelectedItem();

//             try {
//                 tableModel.setRowCount(0);
//                 java.util.List<Work> travaux = client.getWorksByType(selectedType);
//                 for (Work t : travaux) {
//                     tableModel.addRow(new Object[] {
//                         t.getBorough(), t.getStatus(),
//                         t.getCategory(), t.getServiceProvider(),
//                         t.getStartDate(), t.getEndDate()
//                     });
//                 }

//                 if (tableModel.getRowCount() == 0) {
//                     JOptionPane.showMessageDialog(null,
//                             "Aucun travail trouvé pour ce type",
//                             "Information",
//                             JOptionPane.INFORMATION_MESSAGE);
//                 }
//             } catch (Exception e) {
//                 JOptionPane.showMessageDialog(null,
//                         "Erreur lors du filtrage par type: " + e.getMessage(),
//                         "Erreur",
//                         JOptionPane.ERROR_MESSAGE);
//             }
//         }
//     }

//     /**
//      * Soumet un problème signalé par le résident.
//      * 
//      * <p>
//      * Valide les champs du formulaire et envoie les données au backend.
//      * Affiche un message de succès ou d'erreur selon le résultat de la soumission.
//      */
//     private void submitProblem() {
//         String street = problemStreetField.getText().trim();
//         String neighbourhood = problemNeighbourhoodField.getText().trim();
//         String type = (String) problemTypeComboBox.getSelectedItem();
//         String description = problemDescriptionArea.getText().trim();
//         String contact = problemContactField.getText().trim(); // Assurez-vous d'avoir ce champ
    
//         if (neighbourhood.isEmpty() || street.isEmpty() || type == null || 
//             description.isEmpty() || contact.isEmpty()) {
//             JOptionPane.showMessageDialog(null,
//                     "Veuillez remplir tous les champs.",
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//             return;
//         }
    
//         try {
//             // Créer l'objet Problem
//             Problem problem = new Problem();
//             problem.setStreet(street);
//             problem.setNeighbourhood(neighbourhood);
//             problem.setType(WorkType.valueOf(type.toUpperCase())); // Adapter selon votre enum
//             problem.setDescription(description);
    
//             // Appeler la méthode avec l'objet Problem
//             Problem submittedProblem = client.submitProblem(problem);
    
//             // Clear form
//             problemStreetField.setText("");
//             problemNeighbourhoodField.setText("");
//             problemTypeComboBox.setSelectedIndex(0);
//             problemDescriptionArea.setText("");
//             problemContactField.setText("");
    
//             JOptionPane.showMessageDialog(null,
//                     "Problème signalé avec succès!",
//                     "Succès",
//                     JOptionPane.INFORMATION_MESSAGE);
//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(null,
//                     "Soumission du problème échouée: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /** Affiche les abonnements du résident connecté.

//     private void viewSubscriptions() {
//         if (currentResident == null) {
//             JOptionPane.showMessageDialog(null, "Aucun résident connecté", "Erreur", JOptionPane.ERROR_MESSAGE);
//             return;
//         }

//         try {
//             java.util.List<Abonnement> abonnements = client.getAbonnementsResident(currentResident.getId());

//             notificationListModel.clear();
//             if (abonnements.isEmpty()) {
//                 notificationListModel.addElement("Aucun abonnement actif");
//             } else {
//                 notificationListModel.addElement("=== Vos abonnements actifs ===");
//                 for (Abonnement ab : abonnements) {
//                     String status = ab.isActive() ? "Actif" : "Inactif";
//                     notificationListModel.addElement(String.format(
//                             "ID: %d | Type: %s | Sujet: %s | Statut: %s",
//                             ab.getId(), ab.getType(), ab.getSujet(), status));
//                 }
//             }
//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(null,
//                     "Erreur lors de la récupération des abonnements: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Permet au résident de s'abonner à un type de notification.
//      * 
//      * <p>
//      * Affiche une boîte de dialogue pour choisir le type d'abonnement (quartier ou
//      * rue)
//      * et saisir la valeur correspondante. Envoie la demande d'abonnement au
//      * backend.

//     private void subscribe() {
//         if (currentResident == null) {
//             JOptionPane.showMessageDialog(null, "Aucun résident connecté", "Erreur", JOptionPane.ERROR_MESSAGE);
//             return;
//         }

//         String[] types = { "QUARTIER", "RUE" };
//         String type = (String) JOptionPane.showInputDialog(
//                 null,
//                 "Choisissez le type d'abonnement:",
//                 "Nouvel abonnement",
//                 JOptionPane.QUESTION_MESSAGE,
//                 null,
//                 types,
//                 types[0]);

//         if (type != null) {
//             String prompt = type.equals("QUARTIER") ? "Entrez le nom du quartier:" : "Entrez le nom de la rue:";
//             String valeur = JOptionPane.showInputDialog(null, prompt);

//             if (valeur != null && !valeur.trim().isEmpty()) {
//                 try {
//                     client.ajouterAbonnementResident(currentResident.getId(), type, valeur.trim().toUpperCase());
//                     JOptionPane.showMessageDialog(null,
//                             "Abonnement créé avec succès!",
//                             "Succès",
//                             JOptionPane.INFORMATION_MESSAGE);
//                 } catch (Exception e) {
//                     JOptionPane.showMessageDialog(null,
//                             "Erreur lors de la création de l'abonnement: " + e.getMessage(),
//                             "Erreur",
//                             JOptionPane.ERROR_MESSAGE);
//                 }
//             }
//         }
//     }

//     /**
//      * Permet au résident de se désabonner d'un abonnement existant.
//      * 
//      * <p>
//      * Affiche une liste des abonnements actifs pour que le résident puisse en
//      * choisir un à supprimer.
//      * Envoie la demande de suppression au backend.
  

//     private void unsubscribe() {
//         if (currentResident == null) {
//             JOptionPane.showMessageDialog(null, "Aucun résident connecté", "Erreur", JOptionPane.ERROR_MESSAGE);
//             return;
//         }

//         try {
//             java.util.List<Abonnement> abonnements = client.getAbonnementsResident(currentResident.getId());

//             if (abonnements.isEmpty()) {
//                 JOptionPane.showMessageDialog(null, "Aucun abonnement à supprimer", "Information",
//                         JOptionPane.INFORMATION_MESSAGE);
//                 return;
//             }

//             // Create array of subscription descriptions for selection
//             String[] options = new String[abonnements.size()];
//             for (int i = 0; i < abonnements.size(); i++) {
//                 Abonnement ab = abonnements.get(i);
//                 options[i] = String.format("ID: %d - %s: %s", ab.getId(), ab.getType(), ab.getSujet());
//             }

//             String selected = (String) JOptionPane.showInputDialog(
//                     null,
//                     "Choisissez l'abonnement à supprimer:",
//                     "Suppression d'abonnement",
//                     JOptionPane.QUESTION_MESSAGE,
//                     null,
//                     options,
//                     options[0]);

//             if (selected != null) {
//                 // Extract ID from selected string
//                 int id = Integer.parseInt(selected.split(" - ")[0].replace("ID: ", ""));

//                 client.supprimerAbonnement(id);
//                 JOptionPane.showMessageDialog(null,
//                         "Abonnement supprimé avec succès!",
//                         "Succès",
//                         JOptionPane.INFORMATION_MESSAGE);
//             }
//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(null,
//                     "Erreur lors de la suppression de l'abonnement: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Affiche toutes les notifications du résident connecté.
//      * 
//      * <p>
//      * Récupère les notifications depuis le backend et les affiche dans la liste.
//      * Inclut le sujet de l'abonnement associé à chaque notification.
    
//     private void viewAllNotifications() {
//         if (currentResident == null) {
//             JOptionPane.showMessageDialog(null, "Aucun résident connecté", "Erreur", JOptionPane.ERROR_MESSAGE);
//             return;
//         }

//         try {
//             java.util.List<Notification> notifications = client.consulterAllNotifications(currentResident.getId());

//             notificationListModel.clear();
//             if (notifications.isEmpty()) {
//                 notificationListModel.addElement("Aucune notification trouvée");
//             } else {
//                 notificationListModel
//                         .addElement("=== Toutes les notifications (" + notifications.size() + " trouvées) ===");

//                 // Get subscriptions to match with notifications
//                 java.util.List<Abonnement> abonnements = client.getAbonnementsResident(currentResident.getId());

//                 for (int i = 0; i < notifications.size(); i++) {
//                     Notification notification = notifications.get(i);

//                     // Find corresponding subscription
//                     String sujet = abonnements.stream()
//                             .filter(a -> a.getType().equals(notification.getAbonnementType()))
//                             .findFirst()
//                             .map(Abonnement::getSujet)
//                             .orElse("N/A");

//                     notificationListModel.addElement(String.format(
//                             "%d. [%s] %s (Sujet: %s)",
//                             i + 1,
//                             notification.getAbonnementType() != null ? notification.getAbonnementType() : "N/A",
//                             notification.getMessage(),
//                             sujet));
//                 }
//             }
//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(null,
//                     "Erreur lors de la récupération des notifications: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     } */
    
//     }