// package ca.udem.maville.cli;

// import java.util.List;
// import java.util.Scanner;

// import ca.udem.maville.api.MavilleRestClient;
// import ca.udem.maville.model.Priorite;
// import ca.udem.maville.model.Problem;
// import ca.udem.maville.model.Project;

// public class STPMMenu {
//     private final Scanner scanner;
//     private final MavilleRestClient client;

//     public STPMMenu(MavilleRestClient client, Scanner scanner) {
//         this.client = client;
//         this.scanner = scanner;
//     }

//     public void displayMenu() {
//         while (true) {
//             System.out.println("\n=== STPM City Services Menu ===");
//             System.out.println("1. View All Reported Problems");
//             System.out.println("2. Evaluate Problems (Assign Priority)");
//             System.out.println("3. View All Project Proposals (submitted)");
//             System.out.println("4. Evaluate Project Proposals (accept/reject)");
//             System.out.println("5. Logout");
//             System.out.print("Choose an action: ");
//             String choice = scanner.nextLine();

//             try {
//                 switch (choice) {
//                     case "1" -> viewAllProblems();
//                     case "2" -> handleProblemsEvaluation();
//                     case "3" -> viewAllProposals();
//                     case "4" -> handleProposalsEvaluation();
//                     case "5" -> {
//                         System.out.println("Logging out from STPM account...");
//                         return;
//                     }
//                     default -> System.out.println("Invalid option. Please try again.");
//                 }
//             } catch (java.io.IOException e) {
//                 System.err.println("❌ Connection Error: Could not reach the API service.");
//                 System.err.println("Details: " + e.getMessage());
//             }
//         }
//     }

//     // ------------------------------------------------------------------------
//     // 1. View all reported problems (unchanged)
//     // ------------------------------------------------------------------------
//     private void viewAllProblems() throws java.io.IOException {
//         System.out.println("\n--- All Reported Problems ---");
//         List<Problem> problems = client.getAllProblems();
//         if (problems.isEmpty()) {
//             System.out.println("No problems reported.");
//         } else {
//             problems.forEach(p ->
//                 System.out.printf("ID: %d | Neighbourhood: %s | Street: %s | Description: %s | Priority: %s%n",
//                     p.getId(), p.getNeighbourhood(), p.getStreet(), p.getDescription(),
//                     p.getPrioriteType()));
//         }
//     }

//     // ------------------------------------------------------------------------
//     // 2. Assign priority to a problem (unchanged)
//     // ------------------------------------------------------------------------
//     private void handleProblemsEvaluation() throws java.io.IOException {
//         System.out.println("\n--- Problem Evaluation (Assign Priority) ---");
//         List<Problem> unassignedProblems = client.getProblemsNotAssigned();

//         if (unassignedProblems.isEmpty()) {
//             System.out.println("No problems to evaluate.");
//             return;
//         }

//         System.out.println("Problems awaiting evaluation:");
//         unassignedProblems.forEach(p ->
//             System.out.printf("ID: %d - %s (Reported in: %s)%n",
//                 p.getId(), p.getDescription(), p.getNeighbourhood()));

//         System.out.print("\nEnter Problem ID to evaluate: ");
//         int problemId;
//         try {
//             problemId = Integer.parseInt(scanner.nextLine());
//         } catch (NumberFormatException e) {
//             System.err.println("❌ Invalid Problem ID format.");
//             return;
//         }

//         System.out.println("\nSelect priority level:");
//         System.out.println("1. LOW (FAIBLE)");
//         System.out.println("2. MEDIUM (MOYENNE)");
//         System.out.println("3. HIGH (ELEVEE)");
//         System.out.println("4. REFUSE");
//         System.out.print("Choose priority: ");
//         String priorityChoice = scanner.nextLine();

//         String priority;
//         switch (priorityChoice) {
//             case "1" -> priority = Priorite.FAIBLE.name();
//             case "2" -> priority = Priorite.MOYENNE.name();
//             case "3" -> priority = Priorite.ELEVEE.name();
//             case "4" -> priority = "REFUSED";
//             default -> {
//                 System.out.println("❌ Invalid priority choice.");
//                 return;
//             }
//         }

//         Problem updatedProblem = client.assignProblemPriority(problemId, priority);
//         if (updatedProblem != null) {
//             System.out.println("✅ Priority " + priority + " assigned to Problem ID " + problemId);
//         } else {
//             System.out.println("❌ Failed to assign priority.");
//         }
//     }

//     // ------------------------------------------------------------------------
//     // 3. View all submitted proposals (projects with status PROPOSAL_SUBMITTED)
//     // ------------------------------------------------------------------------
//     private void viewAllProposals() throws java.io.IOException {
//         System.out.println("\n--- All Submitted Project Proposals ---");
//         List<Project> proposals = client.getSubmittedProposals();
//         if (proposals.isEmpty()) {
//             System.out.println("No project proposals found.");
//         } else {
//             proposals.forEach(p ->
//                 System.out.printf("ID: %d | Title: %s | Description: %s | Cost: %.2f | Status: %s%n",
//                     p.getId(), p.getTitle(), p.getDescription(), p.getProposedCost(), p.getStatus()));
//         }
//     }

//     // ------------------------------------------------------------------------
//     // 4. Accept or reject a submitted proposal
//     // ------------------------------------------------------------------------
//     private void handleProposalsEvaluation() throws java.io.IOException {
//         System.out.println("\n--- Project Proposal Evaluation (Accept/Reject) ---");
//         List<Project> proposals = client.getSubmittedProposals();

//         if (proposals.isEmpty()) {
//             System.out.println("No proposals to evaluate.");
//             return;
//         }

//         System.out.println("Pending proposals for evaluation:");
//         proposals.forEach(p ->
//             System.out.printf("ID: %d | Title: %s | Cost: %.2f%n",
//                 p.getId(), p.getTitle(), p.getProposedCost()));

//         System.out.print("\nEnter Proposal ID to evaluate: ");
//         long proposalId;
//         try {
//             proposalId = Long.parseLong(scanner.nextLine().trim());
//         } catch (NumberFormatException e) {
//             System.err.println("❌ Invalid Proposal ID format.");
//             return;
//         }

//         System.out.print("Evaluate as (ACCEPT/REJECT): ");
//         String decision = scanner.nextLine().trim().toUpperCase();

//         Project result = null;
//         if ("ACCEPT".equals(decision)) {
//             result = client.acceptProposal(proposalId);
//         } else if ("REJECT".equals(decision)) {
//             result = client.rejectProposal(proposalId);
//         } else {
//             System.err.println("❌ Invalid decision. Use ACCEPT or REJECT.");
//             return;
//         }

//         if (result != null) {
//             System.out.println("✅ Proposal ID " + proposalId + " evaluated as: " + decision);
//         } else {
//             System.err.println("❌ Evaluation failed.");
//         }
//     }
// }