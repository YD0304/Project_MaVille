// package ca.udem.maville.swing_gui;

// import java.awt.BorderLayout;
// import java.awt.FlowLayout;
// import java.awt.Font;
// import java.awt.event.ActionListener;
// import java.util.List;

// import javax.swing.BorderFactory; // Add this import
// import javax.swing.DefaultListModel;
// import javax.swing.JButton;
// import javax.swing.JLabel;
// import javax.swing.JList;
// import javax.swing.JOptionPane;
// import javax.swing.JPanel;
// import javax.swing.JScrollPane;
// import javax.swing.JTabbedPane;
// import javax.swing.JTable;
// import javax.swing.SwingConstants;
// import javax.swing.SwingUtilities;
// import javax.swing.table.DefaultTableModel;

// import ca.udem.maville.api.MavilleRestClient;
// import ca.udem.maville.model.Candidature;
// import ca.udem.maville.model.Problem;
// import ca.udem.maville.model.StatutProjet;

// /**
//  * Panel d'interface graphique pour le Service des Travaux Publics et de la
//  * Mobilité (STPM).
//  * 
//  * <p>
//  * Fournit des fonctionnalités permettant au STPM de :
//  * <ul>
//  * <li>Affecter des priorités aux problèmes signalés</li>
//  * <li>Évaluer les projets soumis par les prestataires</li>
//  * <li>Consulter les notifications système</li>
//  * </ul>
//  * 
//  * <p>
//  * L'interface est organisée en onglets thématiques pour une navigation
//  * intuitive.
//  * 
//  * <p>
//  * Interagit avec le backend via {@link MavilleRestClient} pour toutes les
//  * opérations métier.
//  */
// public class STPMPanel extends JPanel {
//     /** Client REST pour communiquer avec le backend. */
//     private final MavilleRestClient client;
//     /** Composants de l'interface utilisateur pour STPM */
//     private JTabbedPane stpmTabs;
//     /** Bouton de retour au menu de sélection */
//     private JButton backButton;
//     /** Composants pour l'affectation des priorités */
//     private DefaultListModel<String> priorityListModel;
//     /** Liste des priorités affichée dans l'onglet d'affectation */
//     private JList<String> priorityList;
//     /** Composants pour l'évaluation des projets */
//     private JTable projectsTable;
//     /** Modèle de table pour afficher les projets en attente d'évaluation */
//     private DefaultTableModel projectsTableModel;
//     /** Composants pour la consultation des notifications */
//     private DefaultListModel<String> notificationListModel;
//     /** Liste des notifications affichée dans l'onglet de consultation */
//     private JList<String> notificationList;

//     /**
//      * Constructeur principal.
//      * 
//      * @param client Client REST initialisé pour les communications backend
//      */
//     public STPMPanel(MavilleRestClient client) {
//         this.client = client;
//         setLayout(new BorderLayout());
//         initializeComponents();
//     }

//     /**
//      * Définit l'action du bouton de retour.
//      * 
//      * @param listener Gestionnaire d'événements pour le bouton
//      */
//     private void initializeComponents() {
//         // Header label
//         JLabel headerLabel = new JLabel("Service des Travaux Publics et de la Mobilité (STPM)", SwingConstants.CENTER);
//         headerLabel.setFont(headerLabel.getFont().deriveFont(16f));
//         headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//         add(headerLabel, BorderLayout.NORTH);

//         // Create tabbed interface
//         stpmTabs = new JTabbedPane();
//         stpmTabs.addTab("Affecter priorités", createPriorityPanel());
//         stpmTabs.addTab("Évaluer projets", createProjectEvaluationPanel());
//         stpmTabs.addTab("Notifications", createNotificationPanel());

//         add(stpmTabs, BorderLayout.CENTER);

//         // Back button at bottom
//         JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//         backButton = new JButton("Retour au menu de sélection");
//         bottomPanel.add(backButton);
//         add(bottomPanel, BorderLayout.SOUTH);
//     }

//     /**
//      * Crée le panneau pour l'affectation automatique des priorités.
//      * 
//      * @return Panneau contenant la liste des priorités et le bouton d'action
//      */
//     private JPanel createPriorityPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Affectation automatique des priorités"));

//         // Result display
//         priorityListModel = new DefaultListModel<>();
//         priorityList = new JList<>(priorityListModel);
//         priorityList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

//         JScrollPane scrollPane = new JScrollPane(priorityList);
//         panel.add(scrollPane, BorderLayout.CENTER);

//         // Action button
//         JButton assignButton = new JButton("Affecter les priorités automatiquement");
//         assignButton.addActionListener(e -> affecterPriorites());
//         panel.add(assignButton, BorderLayout.SOUTH);

//         return panel;
//     }

//     /**
//      * Crée le panneau pour l'évaluation des projets de travaux.
//      * 
//      * @return Panneau contenant la table des projets et les boutons d'action
//      */
//     private JPanel createProjectEvaluationPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Évaluation des projets de travaux"));

//         // Projects table
//         projectsTableModel = new DefaultTableModel(
//                 new Object[] { "ID", "Titre", "Soumissionnaire", "Période", "Coût", "Statut" }, 0);
//         projectsTable = new JTable(projectsTableModel);
//         JScrollPane scrollPane = new JScrollPane(projectsTable);
//         panel.add(scrollPane, BorderLayout.CENTER);

//         // Button panel
//         JPanel buttonPanel = new JPanel(new FlowLayout());

//         JButton refreshButton = new JButton("Actualiser");
//         refreshButton.addActionListener(e -> loadPendingProjects());

//         JButton approveButton = new JButton("Approuver");
//         approveButton.addActionListener(e -> evaluerProjet(true));

//         JButton rejectButton = new JButton("Rejeter");
//         rejectButton.addActionListener(e -> evaluerProjet(false));

//         buttonPanel.add(refreshButton);
//         buttonPanel.add(approveButton);
//         buttonPanel.add(rejectButton);

//         panel.add(buttonPanel, BorderLayout.SOUTH);

//         // Load projects on initialization
//         loadPendingProjects();

//         return panel;
//     }

//     /**
//      * Crée le panneau pour la consultation des notifications STPM.
//      * 
//      * @return Panneau contenant la liste des notifications et le bouton d'action
//      */
//     private JPanel createNotificationPanel() {
//         JPanel panel = new JPanel(new BorderLayout());
//         panel.setBorder(BorderFactory.createTitledBorder("Notifications STPM"));

//         // Notification list
//         notificationListModel = new DefaultListModel<>();
//         notificationList = new JList<>(notificationListModel);
//         notificationList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

//         JScrollPane scrollPane = new JScrollPane(notificationList);
//         panel.add(scrollPane, BorderLayout.CENTER);

//         // Action button
//         JButton refreshButton = new JButton("Actualiser les notifications");
//         refreshButton.addActionListener(e -> consulterNotifications());
//         panel.add(refreshButton, BorderLayout.SOUTH);

//         return panel;
//     }

//     private void affecterPriorites() {
//         priorityListModel.clear();
//         priorityListModel.addElement("Chargement des problèmes...");
        
//         try {
//             // First, fetch the list of problems from the server
//             List<Problem> problems = client.getProblemsNotAssigned();
            
//             if (problems.isEmpty()) {
//                 priorityListModel.clear();
//                 priorityListModel.addElement("Aucun problème non assigné trouvé.");
//                 return;
//             }
            
//             // Display problems in a dialog for selection
//             String[] problemOptions = problems.stream()
//                     .map(p -> "ID: " + p.getId() + " - " + p.getDescription())
//                     .toArray(String[]::new);
            
//             String selectedProblem = (String) JOptionPane.showInputDialog(this,
//                     "Sélectionnez un problème:",
//                     "Sélection de problème",
//                     JOptionPane.QUESTION_MESSAGE,
//                     null,
//                     problemOptions,
//                     problemOptions[0]);
            
//             if (selectedProblem != null) {
//                 // Extract problem ID from the selection
//                 int problemId = Integer.parseInt(selectedProblem.split(" ")[1]);
                
//                 // Ask for priority
//                 String[] priorities = {"haute", "moyenne", "basse"};
//                 String priorite = (String) JOptionPane.showInputDialog(this,
//                         "Choisissez la priorité:",
//                         "Affecter priorité",
//                         JOptionPane.QUESTION_MESSAGE,
//                         null,
//                         priorities,
//                         priorities[0]);
                
//                 if (priorite != null) {
//                     Problem updatedProblem = client.assignProblemPriority(problemId, priorite);
//                     priorityListModel.clear();
//                     priorityListModel.addElement("Priorité '" + priorite + "' affectée avec succès au problème " + problemId + "!");
//                 }
//             }
            
//         } catch (Exception e) {
//             priorityListModel.clear();
//             priorityListModel.addElement("Erreur: " + e.getMessage());
//             JOptionPane.showMessageDialog(this, 
//                     "Erreur: " + e.getMessage(),
//                     "Erreur", JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Charge les projets en attente d'évaluation depuis le backend.
//      * 
//      * <p>
//      * Met à jour le modèle de table avec les projets récupérés.
//      * </p>
//      */
//     private void loadPendingProjects() {
//         try {
//             projectsTableModel.setRowCount(0); // Clear existing data
//             List<Candidature> allProposals = client.viewAllProposals();
            
//             // Filter for pending proposals
//             List<Candidature> projetsEnAttente = allProposals.stream()
//                     .filter(p -> p.getStatus() == StatutProjet.PROPOSAL_SUBMITED) // Assuming getStatut() returns StatutProjet
//                     .toList();

//             if (projetsEnAttente.isEmpty()) {
//                 // Add empty row with 6 columns
//                 projectsTableModel.addRow(new Object[] { "", "Aucun projet en attente", "", "", "", "" });
//             } else {
//                 for (Candidature projet : projetsEnAttente) {
//                     projectsTableModel.addRow(new Object[] {
//                             projet.getId(),
//                             projet.getTitle(),
//                             projet.getPrestataire() != null ? projet.getPrestataire() : "Inconnu",
//                             projet.getStartDate() + " - " + projet.getEndDate(),
//                             String.format("%.2f $", projet.getCost()), // Assuming 'getCost()' is the correct method
//                             projet.getStatus().toString()
//                     });
//                 }
//             }
//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(this,
//                     "Erreur lors du chargement des projets: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Évalue le projet sélectionné dans la table.
//      * 
//      * <p>
//      * Met à jour le statut du projet en fonction de l'évaluation (approuvé ou
//      * rejeté).
//      * </p>
//      * 
//      * @param approved Indique si le projet est approuvé (true) ou rejeté (false)
//      */
//     private void evaluerProjet(boolean approved) {
//         int selectedRow = projectsTable.getSelectedRow();
//         if (selectedRow < 0) {
//             JOptionPane.showMessageDialog(this,
//                     "Veuillez sélectionner un projet",
//                     "Aucune sélection",
//                     JOptionPane.WARNING_MESSAGE);
//             return;
//         }

//         int projectId = (int) projectsTableModel.getValueAt(selectedRow, 0);
//         String projectName = (String) projectsTableModel.getValueAt(selectedRow, 1);

//         try {
//             // Convert boolean to StatutProjet enum
//             StatutProjet status = approved ? StatutProjet.PERMIT_ISSUED : StatutProjet.PROPOSAL_REFUSED;
            
//             client.evaluateProposal(projectId, status);

//             // Update status at index 5 (6th column)
//             projectsTableModel.setValueAt(status.toString(), selectedRow, 5);

//             JOptionPane.showMessageDialog(this,
//                     "Le projet \"" + projectName + "\" a été " + (approved ? "approuvé" : "rejeté"),
//                     "Évaluation enregistrée",
//                     JOptionPane.INFORMATION_MESSAGE);

//         } catch (Exception e) {
//             JOptionPane.showMessageDialog(this,
//                     "Erreur lors de l'évaluation du projet: " + e.getMessage(),
//                     "Erreur",
//                     JOptionPane.ERROR_MESSAGE);
//         }
//     }

//     /**
//      * Consulte les notifications STPM.
//      * 
//      * <p>
//      * Met à jour la liste des notifications avec les données récupérées.
//      * </p>
//      */
//     private void consulterNotifications() {
//         notificationListModel.clear();
//         notificationListModel.addElement("Chargement des notifications...");
        
//         // For now, just show a placeholder message
//         // You would need to implement actual notification fetching in the client
//         SwingUtilities.invokeLater(() -> {
//             notificationListModel.clear();
//             notificationListModel.addElement("Aucun système de notifications disponible pour le moment.");
//             notificationListModel.addElement("Cette fonctionnalité sera implémentée dans une version future.");
//         });
//     }

//     /**
//      * Définit l'action à exécuter lorsque le bouton de retour est cliqué.
//      * 
//      * @param listener ActionListener à associer au bouton de retour
//      */
//     public void setBackButtonAction(ActionListener listener) {
//         backButton.addActionListener(listener);
//     }
// }