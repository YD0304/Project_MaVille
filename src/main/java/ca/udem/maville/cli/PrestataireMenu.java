// package ca.udem.maville.cli;

// import java.io.IOException;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.time.format.DateTimeParseException;
// import java.util.List;
// import java.util.Scanner;

// import ca.udem.maville.api.MavilleRestClient;
// import ca.udem.maville.model.Problem;
// import ca.udem.maville.model.Project;
// import ca.udem.maville.model.ProjectStatus;
// import ca.udem.maville.model.Provider;

// public class PrestataireMenu {
//     private final MavilleRestClient client;
//     private final Scanner scanner;

//     public PrestataireMenu(MavilleRestClient client, Scanner scanner) {
//         this.client = client;
//         this.scanner = scanner;
//     }

//     public void displayMenu(Provider provider) {
//         while (true) {
//             System.out.println("\n--- Service Provider Menu (Logged in as: " + provider.getCompanyName() + ") ---");
//             System.out.println("1. View available problems (to submit proposals)");
//             System.out.println("2. Submit a proposal");
//             System.out.println("3. View my proposals & projects");
//             System.out.println("4. Update a proposal (description / end date)");
//             System.out.println("5. Update work progress (start/delay/resume/complete)");
//             System.out.println("6. Manage subscriptions (notifications)");
//             System.out.println("7. Return to main menu");
//             System.out.print("Choose an action: ");
//             String choice = scanner.nextLine().trim();
//             switch (choice) {
//                 case "1" -> viewAvailableProblems();
//                 case "2" -> submitProposal(provider);
//                 case "3" -> viewMyProposalsAndProjects(provider);
//                 case "4" -> updateProposal(provider);
//                 case "5" -> updateWorkProgress(provider);
//                 case "6" -> manageSubscriptions(provider);
//                 case "7" -> { return; }
//                 default -> System.out.println("Invalid option.");
//             }
//         }
//     }

//     // ------------------------------------------------------------------------
//     // 1. View problems that have been assigned a priority (available for proposals)
//     // ------------------------------------------------------------------------
//     private void viewAvailableProblems() {
//         System.out.println("\n--- Available Problems (with assigned priority) ---");
//         try {
//             List<Problem> problems = client.getProblemsAssigned();  // already exists in client
//             if (problems.isEmpty()) {
//                 System.out.println("No available problems at the moment.");
//             } else {
//                 problems.forEach(p -> System.out.println("ID: " + p.getId() + " | " +
//                         p.getDescription() + " | Priority: " + p.getPrioriteType()));
//             }
//         } catch (IOException e) {
//             System.out.println("Error fetching problems: " + e.getMessage());
//         }
//     }

//     // ------------------------------------------------------------------------
//     // 2. Submit a new proposal (creates a Project with status PROPOSAL_SUBMITTED)
//     // ------------------------------------------------------------------------
//     private void submitProposal(Provider provider) {
//         System.out.println("\n--- Submit a New Proposal ---");
//         viewAvailableProblems();

//         Long problemId = readPositiveLong("Problem ID: ");
//         if (problemId == null) return;

//         String title = readNonEmptyString("Proposal title: ");
//         if (title == null) return;

//         String description = readNonEmptyString("Proposal description: ");
//         if (description == null) return;

//         LocalDate startDate = readFutureDate("Start date (YYYY-MM-DD): ");
//         if (startDate == null) return;

//         LocalDate endDate = readDateAfter("End date (YYYY-MM-DD): ", startDate);
//         if (endDate == null) return;

//         double cost = readPositiveDouble("Estimated cost: ");
//         if (cost < 0) return;

//         try {
//             Project project = client.submitProposal(problemId, title, description,
//                     cost, startDate, endDate, provider.getCompanyNumber());
//             System.out.println("✅ Proposal submitted successfully! Project ID: " + project.getId());
//         } catch (IOException e) {
//             System.out.println("❌ Error submitting proposal: " + e.getMessage());
//         }
//     }

//     // ------------------------------------------------------------------------
//     // 3. View my proposals (all projects where this provider is the owner)
//     // ------------------------------------------------------------------------
//     private void viewMyProposalsAndProjects(Provider provider) {
//         System.out.println("\n--- My Proposals and Projects ---");
//         try {
//             List<Project> projects = client.getMyProposals(provider.getCompanyNumber());
//             if (projects.isEmpty()) {
//                 System.out.println("No proposals found.");
//                 return;
//             }
//             System.out.println("ID | Title | Status | Start | End | Cost");
//             for (Project p : projects) {
//                 System.out.printf("%d | %s | %s | %s | %s | %.2f$\n",
//                         p.getId(), p.getTitle(), p.getStatus(),
//                         p.getProposedStartDate(), p.getProposedEndDate(), p.getProposedCost());
//             }
//         } catch (IOException e) {
//             System.out.println("Error fetching projects: " + e.getMessage());
//         }
//     }

//     // ------------------------------------------------------------------------
//     // 4. Update a proposal (only allowed when status = PROPOSAL_SUBMITTED)
//     // ------------------------------------------------------------------------
//     private void updateProposal(Provider provider) {
//         System.out.println("\n--- Update a Proposal ---");
//         Long projectId = readPositiveLong("Project ID to update: ");
//         if (projectId == null) return;

//         System.out.println("What do you want to update?");
//         System.out.println("1. Description");
//         System.out.println("2. End date");
//         System.out.println("3. Both");
//         System.out.print("Choice: ");
//         String choice = scanner.nextLine().trim();

//         try {
//             if (choice.equals("1") || choice.equals("3")) {
//                 String newDesc = readNonEmptyString("New description: ");
//                 if (newDesc != null) {
//                     Project updated = client.updateProposalDescription(projectId, provider.getCompanyNumber(), newDesc);
//                     System.out.println("✅ Description updated.");
//                 }
//             }
//             if (choice.equals("2") || choice.equals("3")) {
//                 LocalDate newEnd = readFutureDate("New end date (YYYY-MM-DD): ");
//                 if (newEnd != null) {
//                     Project updated = client.updateProposalEndDate(projectId, provider.getCompanyNumber(), newEnd);
//                     System.out.println("✅ End date updated.");
//                 }
//             }
//         } catch (IOException e) {
//             System.out.println("❌ Update failed: " + e.getMessage());
//         } catch (IllegalStateException e) {
//             System.out.println("❌ Cannot update: " + e.getMessage());
//         }
//     }

//     // ------------------------------------------------------------------------
//     // 5. Update work progress (start, delay, resume, complete)
//     // ------------------------------------------------------------------------
//     private void updateWorkProgress(Provider provider) {
//         System.out.println("\n--- Update Work Progress ---");
//         System.out.println("Only projects with status PERMIT_ISSUED or PROJECT_ONGOING or PROJECT_DELAYED can be updated.");
//         Long projectId = readPositiveLong("Project ID: ");
//         if (projectId == null) return;

//         System.out.println("Choose action:");
//         System.out.println("1. Start work (PERMIT_ISSUED → PROJECT_ONGOING)");
//         System.out.println("2. Delay work (PROJECT_ONGOING → PROJECT_DELAYED)");
//         System.out.println("3. Resume work (PROJECT_DELAYED → PROJECT_ONGOING)");
//         System.out.println("4. Complete work (PROJECT_ONGOING → PROJECT_FINISHED)");
//         System.out.print("Choice: ");
//         String action = scanner.nextLine().trim();

//         try {
//             Project updated = null;
//             switch (action) {
//                 case "1" -> updated = client.startWork(projectId, provider.getCompanyNumber());
//                 case "2" -> updated = client.delayWork(projectId, provider.getCompanyNumber());
//                 case "3" -> updated = client.resumeWork(projectId, provider.getCompanyNumber());
//                 case "4" -> {
//                     System.out.print("Actual cost (optional, press Enter to skip): ");
//                     String costStr = scanner.nextLine().trim();
//                     Double actualCost = costStr.isEmpty() ? null : Double.parseDouble(costStr);
//                     updated = client.completeWork(projectId, provider.getCompanyNumber(), actualCost);
//                 }
//                 default -> {
//                     System.out.println("Invalid action.");
//                     return;
//                 }
//             }
//             System.out.println("✅ Work status updated. New status: " + updated.getStatus());
//         } catch (IOException e) {
//             System.out.println("❌ Error: " + e.getMessage());
//         } catch (IllegalStateException e) {
//             System.out.println("❌ Invalid state transition: " + e.getMessage());
//         }
//     }

//     // ------------------------------------------------------------------------
//     // 6. Manage subscriptions (to receive notifications about new problems)
//     // ------------------------------------------------------------------------
//     private void manageSubscriptions(Provider provider) {
//         System.out.println("\n--- Subscription Management ---");
//         System.out.println("1. View my subscriptions");
//         System.out.println("2. Add a subscription (by neighbourhood or problem type)");
//         System.out.println("3. Unsubscribe (deactivate)");
//         System.out.print("Choice: ");
//         String choice = scanner.nextLine().trim();

//         try {
//             switch (choice) {
//                 case "1" -> {
//                     var subs = client.getProviderSubscriptions(provider.getCompanyNumber());
//                     if (subs.isEmpty()) System.out.println("No active subscriptions.");
//                     else subs.forEach(s -> System.out.println(s.getId() + " | " + s.getType() + " | " + s.getValue() + " | active=" + s.isActive()));
//                 }
//                 case "2" -> {
//                     System.out.print("Subscribe by (neighbourhood / problemType): ");
//                     String typeStr = scanner.nextLine().trim().toUpperCase();
//                     System.out.print("Value (e.g., 'Rosemont' or 'ROAD'): ");
//                     String value = scanner.nextLine().trim();
//                     var sub = client.subscribeProvider(provider.getCompanyNumber(),
//                             ca.udem.maville.model.AbonnementType.valueOf(typeStr), value);
//                     System.out.println("✅ Subscribed with ID " + sub.getId());
//                 }
//                 case "3" -> {
//                     System.out.print("Subscription ID to deactivate: ");
//                     Long subId = Long.parseLong(scanner.nextLine().trim());
//                     client.unsubscribeProvider(subId);
//                     System.out.println("✅ Subscription deactivated.");
//                 }
//                 default -> System.out.println("Invalid choice.");
//             }
//         } catch (Exception e) {
//             System.out.println("Error: " + e.getMessage());
//         }
//     }

//     // ---------- Helper input methods ----------
//     private Long readPositiveLong(String prompt) {
//         while (true) {
//             System.out.print(prompt);
//             String input = scanner.nextLine().trim();
//             if (input.isEmpty()) {
//                 System.out.println("❌ Value cannot be empty.");
//                 continue;
//             }
//             try {
//                 long val = Long.parseLong(input);
//                 if (val <= 0) throw new NumberFormatException();
//                 return val;
//             } catch (NumberFormatException e) {
//                 System.out.println("❌ Please enter a positive number.");
//             }
//         }
//     }

//     private String readNonEmptyString(String prompt) {
//         while (true) {
//             System.out.print(prompt);
//             String input = scanner.nextLine().trim();
//             if (input.isEmpty()) {
//                 System.out.println("❌ This field cannot be empty.");
//                 continue;
//             }
//             return input;
//         }
//     }

//     private LocalDate readFutureDate(String prompt) {
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//         LocalDate today = LocalDate.now();
//         while (true) {
//             System.out.print(prompt);
//             String input = scanner.nextLine().trim();
//             if (input.isEmpty()) {
//                 System.out.println("❌ Date cannot be empty.");
//                 continue;
//             }
//             try {
//                 LocalDate date = LocalDate.parse(input, formatter);
//                 if (!date.isAfter(today)) {
//                     System.out.println("❌ Date must be after today (" + today + ").");
//                     continue;
//                 }
//                 return date;
//             } catch (DateTimeParseException e) {
//                 System.out.println("❌ Invalid format. Use YYYY-MM-DD.");
//             }
//         }
//     }

//     private LocalDate readDateAfter(String prompt, LocalDate minDate) {
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//         while (true) {
//             System.out.print(prompt);
//             String input = scanner.nextLine().trim();
//             if (input.isEmpty()) {
//                 System.out.println("❌ Date cannot be empty.");
//                 continue;
//             }
//             try {
//                 LocalDate date = LocalDate.parse(input, formatter);
//                 if (!date.isAfter(minDate)) {
//                     System.out.println("❌ Date must be after " + minDate + ".");
//                     continue;
//                 }
//                 return date;
//             } catch (DateTimeParseException e) {
//                 System.out.println("❌ Invalid format. Use YYYY-MM-DD.");
//             }
//         }
//     }

//     private double readPositiveDouble(String prompt) {
//         while (true) {
//             System.out.print(prompt);
//             String input = scanner.nextLine().trim();
//             if (input.isEmpty()) {
//                 System.out.println("❌ Cost cannot be empty.");
//                 continue;
//             }
//             try {
//                 double val = Double.parseDouble(input);
//                 if (val < 0) throw new NumberFormatException();
//                 return val;
//             } catch (NumberFormatException e) {
//                 System.out.println("❌ Please enter a non‑negative number.");
//             }
//         }
//     }
// }