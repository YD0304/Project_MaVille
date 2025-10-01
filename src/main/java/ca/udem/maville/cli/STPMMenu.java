package ca.udem.maville.cli;

import java.util.List;
import java.util.Scanner;

import ca.udem.maville.api.client.MavilleRestClient;
import ca.udem.maville.enums.Priorite;
import ca.udem.maville.enums.StatutProjet;
import ca.udem.maville.model.Candidature;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Work;


public class STPMMenu {
    private final Scanner scanner;
    private final MavilleRestClient mavilleRestClient;


    public STPMMenu(MavilleRestClient mavilleRestClient, Scanner scanner) {
        this.mavilleRestClient = mavilleRestClient;
        this.scanner = scanner;
    }

    // --- STPM Profile Section ---
    public void displayMenu(){
        while (true) {
            System.out.println("\n--- Menu STPM ---");
            System.out.println("1. List all problems reported");
            System.out.println("2. Evaluate problems reported");
            System.out.println("3. List all projects proposal");
            System.out.println("3. Evaluate projects proposal submitted");

            System.out.println("4. Retour au menu principal");
            System.out.print("Choose an action: ");
            String choice = scanner.nextLine();
            try{
            switch (choice) {
                case "1" -> mavilleRestClient.getAllProblems().forEach(p -> 
                    System.out.println("ID: " + p.getId() + ", Neibourhood: " + p.getNeigbourhood() + "Street" +
                    p.getStreet() + ", Description: " + p.getDescription() + ", Statut: " + p.getPrioriteType()));
                case "2" -> handleProblemsEvaluation();
                case "3" -> {mavilleRestClient.viewAllProposals().forEach(p -> 
                    System.out.println(p.getId() + ", Title: " + p.getTitle() + ", Description: " + 
                    p.getDescription() + ", Cost: " + p.getCost() + ", Status: " + p.getStatus()));}
                case "4" -> {handleProposalsEvaluation();}
                default -> System.out.println("Option invalide.");
            }
        } catch (java.io.IOException e) { 
            // Inform the user that a network/connection issue occurred
            System.err.println("❌ Connection Error: Could not reach the API service or read data.");
            System.err.println("Details: " + e.getMessage());
            // The loop continues, letting the user choose another option
        }
    }
    }

    private void handleProblemsEvaluation() {
            System.out.println("\n---  ---");
            try {
                List<Problem> unassignProblems = mavilleRestClient.getProblemsNotAssigned();
    
                if (unassignProblems.isEmpty()) {
                    System.out.println("No problems to evaluate.");
                    return;
                }
    
                System.out.println("Reported problems:");
                for (Problem p : unassignProblems) {
                    System.out.println(p);
                }
    
                System.out.print("\nSelect project ID to evaluate: ");
                int id = Integer.parseInt(scanner.nextLine());
                String decision = scanner.nextLine().trim();
    
                if ("1".equals(decision)) {
                    // Show priority options and let user choose
                    System.out.println("\nChoisissez une priorité:");
                    System.out.println("1. FAIBLE");
                    System.out.println("2. MOYENNE");
                    System.out.println("3. ELEVEE");
                    System.out.print("4.REFUSE");
                    System.out.print("Select your choice: ");
                    String priorityChoice = scanner.nextLine();

                    String prioriteString; 
                    
                    switch (priorityChoice) {
                        case "1" -> prioriteString = Priorite.FAIBLE.name();
                        case "2" -> prioriteString = Priorite.MOYENNE.name();
                        case "3" -> prioriteString = Priorite.ELEVEE.name();
                        case "4" -> prioriteString = Priorite.REFUSED.name(); // Explicitly include REFUSED
                        default -> {
                            System.out.println("❌ Choix de priorité invalide. Annulation de l'opération.");
                            return;
                        }
                    }
                
                    // Assign the chosen priority
                    Problem updatedProblem = mavilleRestClient.assignProblemPriority(id, prioriteString);
                    if (updatedProblem != null) {
                        System.out.println("Priority " + prioriteString + " assigned to project" +  updatedProblem.getId());
                    } else {
                        System.out.println("Priority Assignation failed.");
                    }}
                    
                } catch (NumberFormatException e) {
                    System.err.println("❌ Invalid input. Please enter a valid proposal ID.");
                } catch (Exception e) {
                    System.err.println("❌ Error during proposal evaluation: " + e.getMessage());
                    // e.printStackTrace(); // Keep this for debugging if necessary
                }
            }
            

        private void handleProposalsEvaluation() {
            System.out.println("\n--- Project Proposal Evaluation ---");
            try {
                List<Candidature> proposals = mavilleRestClient.viewAllProposals();
                
                // Check if there are proposals to evaluate (assuming proposals is not null)
                if (proposals == null || proposals.isEmpty()) {
                    System.out.println("ℹ️ No proposals found to evaluate.");
                    return;
                }
        
                // --- Display Proposals (Implicit Step) ---
                // You should probably display the proposals here so the user knows the IDs
                // For example: 
                // proposals.forEach(p -> System.out.println("ID: " + p.getId() + ", Title: " + p.getTitle()));
                
                System.out.print("\nSelect proposal ID to evaluate: ");
                // 1. Read the Proposal ID
                int id = Integer.parseInt(scanner.nextLine().trim()); 
                
                // 2. Read the Evaluation Choice (e.g., "ACCEPT" or "REJECT")
                System.out.print("Enter evaluation choice (e.g., ACCEPT/REJECT): ");
                String choice = scanner.nextLine().trim().toUpperCase(); 
        
                // --- Evaluation Logic Placeholder ---
                
                StatutProjet status;
                if ("ACCEPT".equals(choice)) {
                    status = StatutProjet.PERMIT_ISSUED;
                } else if ("REJECT".equals(choice)) {
                    status = StatutProjet.PROPOSAL_REFUSED;
                } else {
                    System.err.println("❌ Invalid evaluation choice. Please enter ACCEPT or REJECT.");
                    return;
                }
        
                // Call the evaluation method
                Work result = mavilleRestClient.evaluateProposal(id, status);
                
                if (result != null) {
                    System.out.println("✅ Proposal ID " + id + " successfully evaluated and marked as " + status + ".");
                } else {
                     System.err.println("❌ Evaluation failed. Proposal not found, or not in the correct state for evaluation.");
                }
        
            } catch (NumberFormatException e) {
                System.err.println("❌ Invalid input. Please enter a valid proposal ID.");
            } catch (Exception e) {
                System.err.println("❌ Error during proposal evaluation: " + e.getMessage());
                // e.printStackTrace(); // Keep this for debugging if necessary
            }
}}
