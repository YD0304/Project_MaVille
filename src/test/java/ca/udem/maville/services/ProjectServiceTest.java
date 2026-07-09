// package ca.udem.maville.services;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.time.LocalDate;
// import java.util.Optional;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import ca.udem.maville.model.Priorite;
// import ca.udem.maville.model.Problem;
// import ca.udem.maville.model.Project;
// import ca.udem.maville.model.Provider;
// import ca.udem.maville.repository.ProblemRepository;
// import ca.udem.maville.repository.ProjectRepository;
// import ca.udem.maville.repository.ProviderRepository;

// @ExtendWith(MockitoExtension.class)
// class ProjectServiceTest {

//     @Mock
//     private ProjectRepository projectRepository;

//     @Mock
//     private ProblemRepository problemRepository;

//     @Mock
//     private ProviderRepository providerRepository;

//     @Mock
//     private NotificationService notificationService;

//     @InjectMocks
//     private ProjectService projectService;

//     @Test
//     void submitProposalUsesCompanyNumberToFindProvider() {
//         Problem problem = new Problem();
//         problem.setId(1L);
//         problem.setPrioriteType(Priorite.ELEVEE);

//         Provider provider = new Provider();
//         provider.setCompanyNumber("RW-001");

//         when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
//         when(providerRepository.findByCompanyNumber("RW-001")).thenReturn(Optional.of(provider));

//         Project savedProject = new Project(
//                 "Repair road",
//                 "Patch the pothole",
//                 1000.0,
//                 LocalDate.now(),
//                 LocalDate.now().plusDays(3),
//                 problem,
//                 provider);
//         when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

//         Project result = projectService.submitProposal(
//                 1L,
//                 "Repair road",
//                 "Patch the pothole",
//                 1000.0,
//                 LocalDate.now(),
//                 LocalDate.now().plusDays(3),
//                 "RW-001");

//         assertNotNull(result);
//         assertEquals(provider, result.getProvider());
//         verify(providerRepository).findByCompanyNumber("RW-001");
//     }
// }
