package ca.udem.maville.seed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import ca.udem.maville.model.AbonnementType;
import ca.udem.maville.model.Priorite;
import ca.udem.maville.model.Problem;
import ca.udem.maville.model.Project;
import ca.udem.maville.model.ProjectStatus;
import ca.udem.maville.model.Provider;
import ca.udem.maville.model.ProviderSubscription;
import ca.udem.maville.model.Resident;
import ca.udem.maville.model.ResidentSubscription;
import ca.udem.maville.model.WorkType;
import ca.udem.maville.repository.ProblemRepository;
import ca.udem.maville.repository.ProjectRepository;
import ca.udem.maville.repository.ProviderRepository;
import ca.udem.maville.repository.ProviderSubscriptionRepository;
import ca.udem.maville.repository.ResidentRepository;
import ca.udem.maville.repository.ResidentSubscriptionRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ResidentRepository residentRepository;
    private final ProviderRepository providerRepository;
    private final ProblemRepository problemRepository;
    private final ResidentSubscriptionRepository residentSubscriptionRepository;
    private final ProviderSubscriptionRepository providerSubscriptionRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ResidentRepository residentRepository,
                      ProviderRepository providerRepository,
                      ProblemRepository problemRepository,
                      ResidentSubscriptionRepository residentSubscriptionRepository,
                      ProviderSubscriptionRepository providerSubscriptionRepository,
                      ProjectRepository projectRepository,
                      PasswordEncoder passwordEncoder) {
        this.residentRepository = residentRepository;
        this.providerRepository = providerRepository;
        this.problemRepository = problemRepository;
        this.residentSubscriptionRepository = residentSubscriptionRepository;
        this.providerSubscriptionRepository = providerSubscriptionRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (residentRepository.count() == 0 && providerRepository.count() == 0) {
            System.out.println("🌱 Seeding test data...");

            // ---------- Residents ----------
            Resident alice = new Resident();
            alice.setEmail("alice@example.com");
            alice.setPassword(passwordEncoder.encode("alice123"));
            alice.setFirst_name("Alice");
            alice.setLast_name("Wonderland");
            alice.setRole("RESIDENT");
            alice.setNeighbourhood("Plateau");
            residentRepository.save(alice);

            Resident bob = new Resident();
            bob.setEmail("bob@example.com");
            bob.setPassword(passwordEncoder.encode("bob123"));
            bob.setFirst_name("Bob");
            bob.setLast_name("Builder");
            bob.setRole("RESIDENT");
            bob.setNeighbourhood("Mile End");
            residentRepository.save(bob);

            // ---------- Admin ----------
            Resident admin = new Resident();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirst_name("Admin");
            admin.setLast_name("User");
            admin.setRole("ADMIN");
            residentRepository.save(admin);

            // ---------- Providers ----------
            Provider roadWorks = new Provider();
            roadWorks.setEmail("roadworks@example.com");
            roadWorks.setCompanyNumber("RW-001");
            roadWorks.setPassword(passwordEncoder.encode("provider123"));
            roadWorks.setRole("PROVIDER");
            roadWorks.setCompanyName("RoadWorks Inc.");
            providerRepository.save(roadWorks);

            Provider greenFix = new Provider();
            greenFix.setEmail("greenfix@example.com");
            greenFix.setCompanyNumber("GF-002");
            greenFix.setPassword(passwordEncoder.encode("provider123"));
            greenFix.setRole("PROVIDER");
            greenFix.setCompanyName("GreenFix Gardening");
            providerRepository.save(greenFix);

            // ---------- Problems ----------
            Problem pothole = new Problem();
            pothole.setResident(alice);
            pothole.setNeighbourhood("Plateau");
            pothole.setStreet("Rue Sainte-Catherine");
            pothole.setType(WorkType.CONSTRUCTION_RENOVATION);
            pothole.setDescription("Large pothole near corner, dangerous for cyclists");
            pothole.setPrioriteType(Priorite.ELEVEE);   // elevated after agent review
            problemRepository.save(pothole);

            Problem brokenLight = new Problem();
            brokenLight.setResident(bob);
            brokenLight.setNeighbourhood("Mile End");
            brokenLight.setStreet("Avenue du Parc");
            brokenLight.setType(WorkType.TRAVAUX_GAZ_ELECTRICITE);
            brokenLight.setDescription("Streetlight flickering for a week");
            brokenLight.setPrioriteType(Priorite.FAIBLE);
            problemRepository.save(brokenLight);

            // ---------- Resident Subscriptions ----------
            ResidentSubscription sub1 = new ResidentSubscription(alice, AbonnementType.QUARTIER, "Plateau", true);
            ResidentSubscription sub2 = new ResidentSubscription(bob, AbonnementType.TYPE_PROBLEME, "Pothole", true);
            residentSubscriptionRepository.saveAll(List.of(sub1, sub2));

            // ---------- Provider Subscriptions ----------
            ProviderSubscription provSub1 = new ProviderSubscription(roadWorks, AbonnementType.TYPE_PROBLEME, "Pothole", true);
            ProviderSubscription provSub2 = new ProviderSubscription(greenFix, AbonnementType.QUARTIER, "Mile End", true);
            providerSubscriptionRepository.saveAll(List.of(provSub1, provSub2));

            // ---------- Projects (linked to problems) ----------
            Project project1 = new Project();
            project1.setProblem(pothole);                     // link to problem
            project1.setTitle("Fix Pothole on Ste-Catherine");
            project1.setDescription("Fill the large pothole and repave 10m2");
            project1.setProposedCost(1250.00);
            project1.setProposedStartDate(LocalDate.now().plusDays(7));
            project1.setProposedEndDate(LocalDate.now().plusDays(14));
            project1.setProvider(greenFix);
            project1.setStatus(ProjectStatus.PERMIT_ISSUED);
            project1.setReportedCount(0);                     // required non-null field
            project1.setLastUpdate(LocalDateTime.now());
            projectRepository.save(project1);

            Project project2 = new Project();
            project2.setProblem(brokenLight);
            project2.setTitle("Replace Streetlight Bulb");
            project2.setDescription("Change the faulty LED bulb and check electrical box");
            project2.setProposedCost(350.00);
            project2.setProposedStartDate(LocalDate.now().plusDays(3));
            project2.setProposedEndDate(LocalDate.now().plusDays(5));
            project2.setProvider(greenFix);
            project2.setStatus(ProjectStatus.PERMIT_ISSUED);
            project2.setReportedCount(0);
            project2.setLastUpdate(LocalDateTime.now());
            projectRepository.save(project2);

            // Add this after creating the other projects
Project project3 = new Project();
project3.setProblem(pothole); // or another problem
project3.setTitle("Repair Sidewalk on Main St");
project3.setDescription("Fix broken concrete slabs");
project3.setProposedCost(800.00);
project3.setProposedStartDate(LocalDate.now().plusDays(5));
project3.setProposedEndDate(LocalDate.now().plusDays(10));
project3.setProvider(roadWorks); // assign to roadWorks
project3.setStatus(ProjectStatus.PROPOSAL_SUBMITTED);
project3.setReportedCount(0);
project3.setLastUpdate(LocalDateTime.now());
projectRepository.save(project3);

            System.out.println("✅ Seeding completed! Test data inserted.");
        } else {
            System.out.println("ℹ️ Database already contains data. Seeding skipped.");
        }
    }
}