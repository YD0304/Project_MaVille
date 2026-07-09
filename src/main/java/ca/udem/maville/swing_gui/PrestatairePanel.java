// package ca.udem.maville.swing_gui;

// import java.awt.BorderLayout;
// import java.awt.FlowLayout;
// import java.awt.GridBagConstraints;
// import java.awt.GridBagLayout;
// import java.awt.Insets;
// import java.awt.event.ActionListener;
// import java.io.IOException;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.time.format.DateTimeParseException;
// import java.util.List;

// import javax.swing.BorderFactory;
// import javax.swing.DefaultListModel;
// import javax.swing.JButton;
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
// import ca.udem.maville.model.Candidature;
// import ca.udem.maville.model.Provider;
// import ca.udem.maville.model.StatutProjet;
// import ca.udem.maville.model.Problem;

// /**
//  * Panel d'interface graphique pour les prestataires de services municipaux.
//  * 
//  * <p>
//  * Fournit des onglets pour :
//  * <ul>
//  * <li>Consulter les projets disponibles</li>
//  * <li>Soumettre des candidatures</li>
//  * <li>Gérer les projets attribués</li>
//  * <li>Gérer les abonnements et notifications</li>
//  * </ul>
//  * 
//  * <p>
//  * Interagit avec le backend via {@link MavilleRestClient}.
//  */
// public class PrestatairePanel extends JPanel {
//     /** Client REST pour communiquer avec le backend. */
//     private final MavilleRestClient client;
//     /** Prestataire actuellement connecté. */
//     private Provider currentPrestataire;
//     /** Composants de l'interface utilisateur */
//     private JTabbedPane prestataireTabs;
//     /**
//      * Tables pour afficher les projets disponibles, les projets attribués,
//      * abonnements et notifications
//      */
//     private JTable assignedProjectsTable;
//     /** Modèle de table pour les projets attribués */
//     private DefaultTableModel assignedProjectsTableModel;
//     /** Table pour afficher les projets en attente de candidature */
//     private JTable pendingProjectsTable;
//     /** Modèle de table pour les projets en attente */
//     private DefaultTableModel pendingProjectsTableModel;
//     /** Liste pour afficher les notifications et abonnements */
//     private JList<String> notificationList;
//     /** Modèle de liste pour les notifications */
//     private DefaultListModel<String> notificationListModel;

//     /** Bouton pour revenir au menu de sélection */
//     private JButton backButton;
//     /**
//      * Label d'en-tête pour afficher le nom de l'entreprise du prestataire connecté
//      */
//     private JLabel headerLabel;

//     /**
//      * Constructeur principal du panel.
//      * 
//      * @param client Client REST pour interagir avec le backend
//      */
//     public PrestatairePanel(MavilleRestClient client) {
//         this.client = client;
//         setLayout(new BorderLayout());
//         initializeComponents();
//     }

//     /**
//      * Définit le prestataire actuellement connecté.
//      * Met à jour l'en-tête de l'interface.
//      * 
//      * @param prestataire Prestataire connecté
//      */
//     public void setCurrentPrestataire(Provider prestataire) {
//         this.currentPrestataire = prestataire;
//         headerLabel.setText("--- Menu Prestataire (Connecté: " + prestataire.getNomEntreprise() + ") ---");
//     }

//     /**
//      * Définit l'action du bouton de retour.
//      * 
//      * @param listener Gestionnaire d'événements pour le bouton
//      */
//     public void setBackButtonAction(ActionListener listener) {
//         backButton.addActionListener(listener);
//     }

//     /**
//      * Initialise les composants de l'interface utilisateur.
//      * Crée les onglets, tables et boutons nécessaires.
//      */
//     private void initializeComponents() {
//         headerLabel = new JLabel("", SwingConstants.CENTER);
//         headerLabel.setFont(headerLabel.getFont().deriveFont(16f));
//         headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//         add(headerLabel, BorderLayout.NORTH);

//         prestataireTabs = new JTabbedPane();
//         prestataireTabs.addTab("Consulter les projets disponibles", createAvailableProjectsPanel());
//         prestataireTabs.addTab("Soumettre une candidature", createBidSubmissionPanel());
//         prestataireTabs.addTab("Consulter mes projets", createMyProjectsPanel());
//         //prestataireTabs.addTab("Gérer les notifications et abonnements", createNotificationPanel());

//         add(prestataireTabs, BorderLayout.CENTER);

//         JPanel bottomPanel = new JPanel(new FlowLayout());
//         backButton = new JButton("Retour au menu de sélection");
//         bottomPanel.add(backButton);
//         add(bottomPanel, BorderLayout.SOUTH);
//     }

//     /**
//      * Crée l'onglet de consultation des projets disponibles.
//      * 
//      * @return Panel configuré
//      */
//     private JPanel createAvailableProjectsPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Projets disponibles pour soumission"));

//         pendingProjectsTableModel = new DefaultTableModel(
//                 new Object[] { "ID", "Type", "Description", "Quartier", "Rue" }, 0);
//         pendingProjectsTable = new JTable(pendingProjectsTableModel);
//         JScrollPane scrollPane = new JScrollPane(pendingProjectsTable);
//         panel.add(scrollPane, BorderLayout.CENTER);

//         JPanel buttonPanel = new JPanel(new FlowLayout());
//         JButton refreshButton = new JButton("Actualiser");
//         refreshButton.addActionListener(e -> loadAvailableProjects());
//         buttonPanel.add(refreshButton);
//         panel.add(buttonPanel, BorderLayout.SOUTH);

//         return panel;
//     }

//     /**
//      * Charge les projets disponibles depuis le backend et les affiche dans la
//      * table.
//      * 
//      * <p>
//      * Récupère la liste des problèmes ouverts et les affiche dans la table.
//      * </p>
//      */
//     private void loadAvailableProjects() {
//         try {
//             pendingProjectsTableModel.setRowCount(0);
//             List<Problem> problemes = client.getAllProblems();

//             for (Problem problem : problemes) {
//                 pendingProjectsTableModel.addRow(new Object[] {
//                         problem.getId(),
//                         problem.getType(),
//                         problem.getDescription(),
//                         problem.getNeighbourhood(),
//                         problem.getStreet()
//                 });
//             }
//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(this,
//                     "Erreur lors du chargement des projets: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Crée le panel pour soumettre une candidature à un projet.
//      * 
//      * @return Panel configuré
//      */
//     private JPanel createBidSubmissionPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Soumettre une candidature"));
    
//         JPanel formPanel = new JPanel(new GridBagLayout());
//         GridBagConstraints gbc = new GridBagConstraints();
//         gbc.insets = new Insets(5, 5, 5, 5);
//         gbc.anchor = GridBagConstraints.WEST;
//         gbc.fill = GridBagConstraints.HORIZONTAL;
    
//         int row = 0;
        
//         JTextField projectIdField = new JTextField(10);
//         JTextField titleField = new JTextField(20);
//         JTextArea descriptionArea = new JTextArea(3, 20);
//         JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
//         JTextField startDateField = new JTextField(15);
//         JTextField endDateField = new JTextField(15);
//         JTextField costField = new JTextField(15);
    
//         gbc.gridx = 0;
//         gbc.gridy = row;
//         formPanel.add(new JLabel("ID du projet:"), gbc);
//         gbc.gridx = 1;
//         formPanel.add(projectIdField, gbc);
    
//         row++;
//         gbc.gridx = 0;
//         gbc.gridy = row;
//         formPanel.add(new JLabel("Titre de la proposition:"), gbc);
//         gbc.gridx = 1;
//         formPanel.add(titleField, gbc);
    
//         row++;
//         gbc.gridx = 0;
//         gbc.gridy = row;
//         formPanel.add(new JLabel("Description:"), gbc);
//         gbc.gridx = 1;
//         formPanel.add(descriptionScroll, gbc);
    
//         row++;
//         gbc.gridx = 0;
//         gbc.gridy = row;
//         formPanel.add(new JLabel("Date de début (YYYY-MM-DD):"), gbc);
//         gbc.gridx = 1;
//         formPanel.add(startDateField, gbc);
    
//         row++;
//         gbc.gridx = 0;
//         gbc.gridy = row;
//         formPanel.add(new JLabel("Date de fin (YYYY-MM-DD):"), gbc);
//         gbc.gridx = 1;
//         formPanel.add(endDateField, gbc);
    
//         row++;
//         gbc.gridx = 0;
//         gbc.gridy = row;
//         formPanel.add(new JLabel("Coût estimé:"), gbc);
//         gbc.gridx = 1;
//         formPanel.add(costField, gbc);
    
//         JButton submitButton = new JButton("Soumettre candidature");
//         submitButton.addActionListener(e -> {
//             try {
//                 int projectId = Integer.parseInt(projectIdField.getText().trim());
//                 String title = titleField.getText().trim();
//                 String description = descriptionArea.getText().trim();
//                 String dateDebut = startDateField.getText().trim();
//                 String dateFin = endDateField.getText().trim();
//                 String input = costField.getText().trim().replace(",", "");
//                 double cout = Double.parseDouble(input);
    
//                 // Validate dates
//                 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                 LocalDate debutDate = LocalDate.parse(dateDebut, formatter);
//                 LocalDate finDate = LocalDate.parse(dateFin, formatter);
//                 LocalDate today = LocalDate.now();
    
//                 if (!debutDate.isAfter(today)) {
//                     JOptionPane.showMessageDialog(this, "La date de début doit être postérieure à aujourd'hui.",
//                             "Erreur", JOptionPane.ERROR_MESSAGE);
//                     return;
//                 }
    
//                 if (!finDate.isAfter(debutDate)) {
//                     JOptionPane.showMessageDialog(this, "La date de fin doit être postérieure à la date de début.",
//                             "Erreur", JOptionPane.ERROR_MESSAGE);
//                     return;
//                 }
    
//                 if (cout < 0) {
//                     JOptionPane.showMessageDialog(this, "Le coût ne peut pas être négatif.", "Erreur",
//                             JOptionPane.ERROR_MESSAGE);
//                     return;
//                 }
    
//                 // Call the client method with all required parameters
//                 Candidature submittedCandidature = client.submitProposal(
//                     projectId, 
//                     title, 
//                     description, 
//                     dateDebut, 
//                     dateFin, 
//                     cout, 
//                     currentPrestataire
//                 );
                
//                 JOptionPane.showMessageDialog(this, "Candidature soumise avec succès! ID: " + 
//                     (submittedCandidature != null ? submittedCandidature.getId() : "N/A"), 
//                     "Succès", JOptionPane.INFORMATION_MESSAGE);
    
//                 // Clear form
//                 projectIdField.setText("");
//                 titleField.setText("");
//                 descriptionArea.setText("");
//                 startDateField.setText("");
//                 endDateField.setText("");
//                 costField.setText("");
    
//             } catch (NumberFormatException ex) {
//                 JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides.", "Erreur",
//                         JOptionPane.ERROR_MESSAGE);
//             } catch (DateTimeParseException ex) {
//                 JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez YYYY-MM-DD.", "Erreur",
//                         JOptionPane.ERROR_MESSAGE);
//             } catch (IOException ex) {
//                 JOptionPane.showMessageDialog(this, "Erreur de connexion: " + ex.getMessage(), "Erreur",
//                         JOptionPane.ERROR_MESSAGE);
//             } catch (Exception ex) {
//                 JOptionPane.showMessageDialog(this, "Erreur lors de la soumission: " + ex.getMessage(), "Erreur",
//                         JOptionPane.ERROR_MESSAGE);
//             }
//         });
    
//         gbc.gridx = 1;
//         gbc.gridy = ++row;
//         gbc.anchor = GridBagConstraints.EAST;
//         formPanel.add(submitButton, gbc);
    
//         panel.add(formPanel, BorderLayout.CENTER);
//         return panel;
//     }

//     /**
//      * Crée le panel pour consulter les projets attribués au prestataire.
//      * 
//      * @return Panel configuré
//      */
//     private JPanel createMyProjectsPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Mes projets"));

//         assignedProjectsTableModel = new DefaultTableModel(
//                 new Object[] { "ID", "Titre", "Description", "Statut", "Date fin" }, 0);
//         assignedProjectsTable = new JTable(assignedProjectsTableModel);
//         JScrollPane scrollPane = new JScrollPane(assignedProjectsTable);
//         panel.add(scrollPane, BorderLayout.CENTER);

//         JPanel buttonPanel = new JPanel(new FlowLayout());
//         JButton refreshButton = new JButton("Actualiser");
//         refreshButton.addActionListener(e -> loadMyProjects());

//         JButton updateStatusButton = new JButton("Mettre à jour l'état");
//         updateStatusButton.addActionListener(e -> updateProjectStatus());

//         JButton updateDescButton = new JButton("Mettre à jour la description");
//         updateDescButton.addActionListener(e -> updateProjectDescription());

//         JButton updateDateButton = new JButton("Mettre à jour la date");
//         updateDateButton.addActionListener(e -> updateProjectDate());

//         buttonPanel.add(refreshButton);
//         buttonPanel.add(updateStatusButton);
//         buttonPanel.add(updateDescButton);
//         buttonPanel.add(updateDateButton);
//         panel.add(buttonPanel, BorderLayout.SOUTH);

//         return panel;
//     }

//     /**
//      * Charge les projets attribués au prestataire connecté.
//      * 
//      * <p>
//      * Récupère la liste des candidatures du prestataire et les affiche dans la
//      * table.
//      * </p>
//      */
//     private void loadMyProjects() {
//         if (currentPrestataire == null)
//             return;

//         try {
//             assignedProjectsTableModel.setRowCount(0);
//             List<Candidature> projets = client.viewMyProposals(currentPrestataire.getId());

//             for (Candidature projet : projets) {
//                 System.out.println("Projet " + projet.getId() + ": " + projet.getStatus()); // debug ici
//                 assignedProjectsTableModel.addRow(new Object[] {
//                         projet.getId(),
//                         projet.getTitle(),
//                         projet.getDescription(),
//                         projet.getStatus(),
//                         projet.getEndDate() != null ? projet.getEndDate().toString() : "N/A"
//                 });
//             }
//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(this,
//                     "Erreur lors du chargement des projets: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Met à jour le statut du projet sélectionné.
//      * 
//      * <p>
//      * Affiche une boîte de dialogue pour choisir le nouveau statut et met à jour le
//      * projet.
//      * </p>
//      */
//     private void updateProjectStatus() {
//         int selectedRow = assignedProjectsTable.getSelectedRow();
//         if (selectedRow < 0) {
//             JOptionPane.showMessageDialog(this, "Veuillez sélectionner un projet", "Aucune sélection",
//                     JOptionPane.WARNING_MESSAGE);
//             return;
//         }
    
//         int projectId = (int) assignedProjectsTableModel.getValueAt(selectedRow, 0);
    
//         // Use StatutProjet enum values directly
//         StatutProjet[] options = { StatutProjet.PROJECT_FINISHED, StatutProjet.PROJECT_DELAYED, StatutProjet.PERMIT_ISSUED, StatutProjet.PROPOSAL_REFUSED, StatutProjet.PROPOSAL_SUBMITED };
//         StatutProjet newStatus = (StatutProjet) JOptionPane.showInputDialog(this,
//                 "Choisissez le nouvel état:",
//                 "Mise à jour de l'état",
//                 JOptionPane.QUESTION_MESSAGE,
//                 null,
//                 options,
//                 options[0]);
    
//         if (newStatus != null) {
//             try {
//                 client.updateWorkStatus(projectId, currentPrestataire, newStatus);
//                 JOptionPane.showMessageDialog(this, "État mis à jour avec succès!", "Succès",
//                         JOptionPane.INFORMATION_MESSAGE);
//                 loadMyProjects(); // Refresh table
//             } catch (Exception e) {
//                 JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour: " + e.getMessage(), "Erreur",
//                         JOptionPane.ERROR_MESSAGE);
//             }
//         }
//     }

//     /**
//      * Met à jour la description du projet sélectionné.
//      * 
//      * <p>
//      * Affiche une boîte de dialogue pour saisir la nouvelle description et met à
//      * jour le projet.
//      * </p>
//      */
//     private void updateProjectDescription() {
//         int selectedRow = assignedProjectsTable.getSelectedRow();
//         if (selectedRow < 0) {
//             JOptionPane.showMessageDialog(this, "Veuillez sélectionner un projet", "Aucune sélection",
//                     JOptionPane.WARNING_MESSAGE);
//             return;
//         }

//         int projectId = (int) assignedProjectsTableModel.getValueAt(selectedRow, 0);

//         String newDescription = JOptionPane.showInputDialog(this, "Nouvelle description:",
//                 "Mise à jour de la description", JOptionPane.QUESTION_MESSAGE);

//         if (newDescription != null && !newDescription.trim().isEmpty()) {
//             try {
//                 client.updateWorkDescription(projectId, currentPrestataire, newDescription.trim());
//                 JOptionPane.showMessageDialog(this, "Description mise à jour avec succès!", "Succès",
//                         JOptionPane.INFORMATION_MESSAGE);
//                 loadMyProjects(); // Refresh table
//             } catch (Exception e) {
//                 JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour: " + e.getMessage(), "Erreur",
//                         JOptionPane.ERROR_MESSAGE);
//             }
//         }
//     }

//     /**
//      * Met à jour la date de fin du projet sélectionné.
//      * 
//      * <p>
//      * Affiche une boîte de dialogue pour saisir la nouvelle date et met à jour le
//      * projet.
//      * </p>
//      */
//     private void updateProjectDate() {
//         int selectedRow = assignedProjectsTable.getSelectedRow();
//         if (selectedRow < 0) {
//             JOptionPane.showMessageDialog(this, "Veuillez sélectionner un projet", "Aucune sélection",
//                     JOptionPane.WARNING_MESSAGE);
//             return;
//         }
    
//         int projectId = (int) assignedProjectsTableModel.getValueAt(selectedRow, 0);
    
//         String newDateStr = JOptionPane.showInputDialog(this, "Nouvelle date de fin (YYYY-MM-DD):",
//                 "Mise à jour de la date", JOptionPane.QUESTION_MESSAGE);
    
//         if (newDateStr != null && !newDateStr.trim().isEmpty()) {
//             try {
//                 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                 LocalDate newDate = LocalDate.parse(newDateStr.trim(), formatter);
//                 LocalDate today = LocalDate.now();
    
//                 if (newDate.isBefore(today)) {
//                     JOptionPane.showMessageDialog(this, "La date doit être postérieure à aujourd'hui.", "Erreur",
//                             JOptionPane.ERROR_MESSAGE);
//                     return;
//                 }
    
//                 // Convert LocalDate to String in yyyy-MM-dd format
//                 String formattedDate = newDate.format(formatter);
                
//                 // Pass the string to the client method
//                 client.updateWorkEndDate(projectId, currentPrestataire, formattedDate);
                
//                 JOptionPane.showMessageDialog(this, "Date mise à jour avec succès!", "Succès",
//                         JOptionPane.INFORMATION_MESSAGE);
//                 loadMyProjects(); // Refresh table
//             } catch (DateTimeParseException e) {
//                 JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez YYYY-MM-DD.", "Erreur",
//                         JOptionPane.ERROR_MESSAGE);
//             } catch (Exception e) {
//                 JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour: " + e.getMessage(), "Erreur",
//                         JOptionPane.ERROR_MESSAGE);
//             }
//         }
//     }

//     /**
//      * Crée le panel pour gérer les notifications et abonnements.
//      * 
//      * @return Panel configuré
     

//     private JPanel createNotificationPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Gérer les notifications et abonnements"));

//         // Create main menu with buttons
//         JPanel menuPanel = new JPanel(new GridLayout(2, 3, 10, 10));
//         menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//         JButton consultAbonnementsBtn = new JButton("1. Consulter mes abonnements");
//         JButton consultNotificationsBtn = new JButton("2. Consulter mes notifications");
//         JButton addAbonnementBtn = new JButton("3. S'abonner à un type de notification");
//         JButton removeAbonnementBtn = new JButton("4. Se désabonner d'un type de notification");

//         consultAbonnementsBtn.addActionListener(e -> viewSubscriptions());
//         consultNotificationsBtn.addActionListener(e -> showNotifications());
//         addAbonnementBtn.addActionListener(e -> showAddAbonnementMenu());
//         removeAbonnementBtn.addActionListener(e -> unsubscribe());

//         menuPanel.add(consultAbonnementsBtn);
//         menuPanel.add(consultNotificationsBtn);
//         menuPanel.add(addAbonnementBtn);
//         menuPanel.add(removeAbonnementBtn);

//         // Create notification display area
//         notificationListModel = new DefaultListModel<>();
//         notificationList = new JList<>(notificationListModel);
//         notificationList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
//         notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

//         JScrollPane scrollPane = new JScrollPane(notificationList);
//         scrollPane.setPreferredSize(new Dimension(600, 300));
//         scrollPane.setBorder(BorderFactory.createTitledBorder("Résultats"));

//         panel.add(menuPanel, BorderLayout.NORTH);
//         panel.add(scrollPane, BorderLayout.CENTER);

//         return panel;
//     }

//     /**
//      * Affiche les abonnements du prestataire connecté.
//      * 
//      * <p>
//      * Récupère les abonnements actifs et les affiche dans la liste.
//      * </p>
     
//     private void viewSubscriptions() {
//         if (currentPrestataire == null) {
//             JOptionPane.showMessageDialog(this, "Aucun prestataire connecté", "Erreur", JOptionPane.ERROR_MESSAGE);
//             return;
//         }

//         try {
//             List<Abonnement> abonnements = client.getAbonnementsPrestataire(currentPrestataire.getId());

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
//             notificationListModel.clear();
//             notificationListModel.addElement("Erreur lors de la récupération des abonnements: " + e.getMessage());
//         }
//     }

//     /**
//      * Affiche toutes les notifications du prestataire connecté.
//      * 
//      * <p>
//      * Récupère les notifications et les affiche dans la liste.
//      * </p>
     
//     private void showNotifications() {
//         if (currentPrestataire == null) {
//             JOptionPane.showMessageDialog(this, "Aucun prestataire connecté", "Erreur", JOptionPane.ERROR_MESSAGE);
//             return;
//         }

//         try {
//             List<Notification> notifications = client.consulterAllNotifications(currentPrestataire.getId());

//             notificationListModel.clear();
//             if (notifications.isEmpty()) {
//                 notificationListModel.addElement("Aucune notification trouvée");
//             } else {
//                 notificationListModel
//                         .addElement("=== Toutes les notifications (" + notifications.size() + " trouvées) ===");

//                 // Get subscriptions to match with notifications
//                 List<Abonnement> abonnements = client.getAbonnementsPrestataire(currentPrestataire.getId());

//                 for (int i = 0; i < notifications.size(); i++) {
//                     Notification notification = notifications.get(i);
//                     AbonnementType type = notification.getAbonnementType();

//                     // Null-safe matching using Objects.equals()
//                     String sujet = abonnements.stream()
//                             .filter(a -> Objects.equals(a.getType(), type))
//                             .findFirst()
//                             .map(Abonnement::getSujet)
//                             .orElse("N/A"); // Default if no match

//                     notificationListModel.addElement(String.format(
//                             "%d. [%s] %s (Sujet: %s)",
//                             i + 1,
//                             type != null ? type : "N/A", // Handle null type
//                             notification.getMessage(),
//                             sujet));
//                 }
//             }
//         } catch (Exception e) {
//             notificationListModel.clear();
//             notificationListModel.addElement("Erreur lors de la récupération des notifications: " + e.getMessage());
//         }
//     }

//     /**
//      * Affiche le menu pour ajouter un abonnement.
//      * 
//      * <p>
//      * Permet de choisir entre s'abonner à un quartier ou à un type de problème.
//      * </p>
     
//     private void showAddAbonnementMenu() {
//         if (currentPrestataire == null) {
//             JOptionPane.showMessageDialog(this, "Aucun prestataire connecté", "Erreur", JOptionPane.ERROR_MESSAGE);
//             return;
//         }

//         String[] typeOptions = { "Abonnement à un quartier", "Abonnement à un type de problème" };
//         String typeChoice = (String) JOptionPane.showInputDialog(this,
//                 "Types d'abonnement disponibles:",
//                 "Ajouter abonnement",
//                 JOptionPane.QUESTION_MESSAGE,
//                 null,
//                 typeOptions,
//                 typeOptions[0]);

//         if (typeChoice == null)
//             return;

//         if (typeChoice.contains("quartier")) {
//             subscribeToQuartier();
//         } else {
//             subscribeToProblemeType();
//         }
//     }

//     /**
//      * Abonne le prestataire au quartier spécifié.
//      * 
//      * <p>
//      * Affiche une boîte de dialogue pour saisir le nom du quartier et crée
//      * l'abonnement.
//      * </p>
     
//     private void subscribeToQuartier() {
//         String valeur = JOptionPane.showInputDialog(this, "Entrez le nom du quartier:");

//         if (valeur != null && !valeur.trim().isEmpty()) {
//             try {
//                 client.ajouterAbonnementPrestataire(currentPrestataire.getId(), "QUARTIER", valeur.trim());

//                 notificationListModel.clear();
//                 notificationListModel.addElement("Abonnement au quartier '" + valeur.trim() + "' créé avec succès!");
//                 notificationListModel.addElement("");
//                 notificationListModel.addElement("Vous recevrez maintenant des notifications pour ce quartier.");

//             } catch (Exception e) {
//                 notificationListModel.clear();
//                 notificationListModel.addElement("Erreur lors de la création de l'abonnement: " + e.getMessage());
//             }
//         }
//     }

//     /**
//      * Abonne le prestataire à un type de problème spécifique.
//      * 
//      * <p>
//      * Affiche une boîte de dialogue pour choisir le type de problème et crée
//      * l'abonnement.
//      * </p>
     
//     private void subscribeToProblemeType() {
//         // Create array of problem types from enum
//         ProblemeType[] problemeTypes = ProblemeType.values();
//         String[] typeNames = new String[problemeTypes.length];
//         for (int i = 0; i < problemeTypes.length; i++) {
//             typeNames[i] = problemeTypes[i].name();
//         }

//         String selectedType = (String) JOptionPane.showInputDialog(this,
//                 "Types de problèmes disponibles:",
//                 "Choisir type de problème",
//                 JOptionPane.QUESTION_MESSAGE,
//                 null,
//                 typeNames,
//                 typeNames[0]);

//         if (selectedType != null) {
//             try {
//                 client.ajouterAbonnementPrestataire(currentPrestataire.getId(), "TYPE_PROBLEME", selectedType);

//                 notificationListModel.clear();
//                 notificationListModel
//                         .addElement("Abonnement au type de problème '" + selectedType + "' créé avec succès!");
//                 notificationListModel.addElement("");
//                 notificationListModel
//                         .addElement("Vous recevrez maintenant des notifications pour ce type de problème.");

//             } catch (Exception e) {
//                 notificationListModel.clear();
//                 notificationListModel.addElement("Erreur lors de la création de l'abonnement: " + e.getMessage());
//             }
//         }
//     }

//     /**
//      * Désabonne le prestataire de l'abonnement sélectionné.
//      * 
//      * <p>
//      * Affiche une liste des abonnements actifs et permet de choisir lequel
//      * supprimer.
//      * </p>
     
//     private void unsubscribe() {
//         if (currentPrestataire == null) {
//             JOptionPane.showMessageDialog(this, "Aucun prestataire connecté", "Erreur", JOptionPane.ERROR_MESSAGE);
//             return;
//         }

//         try {
//             List<Abonnement> abonnements = client.getAbonnementsPrestataire(currentPrestataire.getId());

//             if (abonnements.isEmpty()) {
//                 notificationListModel.clear();
//                 notificationListModel.addElement("Aucun abonnement à supprimer");
//                 return;
//             }

//             // Create array of subscription descriptions for selection
//             String[] options = new String[abonnements.size()];
//             for (int i = 0; i < abonnements.size(); i++) {
//                 Abonnement ab = abonnements.get(i);
//                 options[i] = String.format("ID: %d - %s: %s", ab.getId(), ab.getType(), ab.getSujet());
//             }

//             String selected = (String) JOptionPane.showInputDialog(
//                     this,
//                     "Choisissez l'abonnement à supprimer:",
//                     "Suppression d'abonnement",
//                     JOptionPane.QUESTION_MESSAGE,
//                     null,
//                     options,
//                     options[0]);

//             if (selected != null) {
//                 // Extract ID from selected string
//                 int id = Integer.parseInt(selected.split(" - ")[0].replace("ID: ", ""));

//                 int confirmResult = JOptionPane.showConfirmDialog(this,
//                         "Êtes-vous sûr de vouloir supprimer cet abonnement ?",
//                         "Confirmer suppression",
//                         JOptionPane.YES_NO_OPTION);

//                 if (confirmResult == JOptionPane.YES_OPTION) {
//                     client.supprimerAbonnement(id);

//                     notificationListModel.clear();
//                     notificationListModel.addElement("Abonnement ID " + id + " supprimé avec succès!");
//                     notificationListModel.addElement("");
//                     notificationListModel.addElement("L'abonnement a été retiré de votre liste.");
//                 }
//             }
//         } catch (Exception e) {
//             notificationListModel.clear();
//             notificationListModel.addElement("Erreur lors de la suppression de l'abonnement: " + e.getMessage());
//         }
//     } */}