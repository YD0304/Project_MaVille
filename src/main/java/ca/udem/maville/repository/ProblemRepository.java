package ca.udem.maville.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.udem.maville.model.Problem;

@Repository
public class ProblemRepository {
    private static final String FILE_PATH = "problemes.json";
    private ObjectMapper objectMapper;
    private List<Problem> problems;
    private int problemIdCounter = 1;

    public ProblemRepository() {
        this.objectMapper = new ObjectMapper();
        this.problems = new ArrayList<>();
        loadProblemsFromFile(); // Actually load from file
    }

    /**
     * Load problems from JSON file
     */
    private void loadProblemsFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                problems = objectMapper.readValue(file, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Problem.class));
                // Update counter based on existing problems
                problemIdCounter = problems.stream()
                    .mapToInt(Problem::getId)
                    .max()
                    .orElse(0) + 1;
            }
        } catch (IOException e) {
            System.err.println("Error loading problems from JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Problem> getAllProblems() {
        return new ArrayList<>(problems);
    }

    private void saveProblems() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), problems);
        } catch (IOException e) {
            System.err.println("Error saving problems to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addProblem(Problem problem) {
        // Assign ID if not set
        if (problem.getId() <= 0) {
            problem.setId(problemIdCounter++);
        }
        problems.add(problem);
        saveProblems();
    }

    public void updateProblem(Problem problem) {
        if (problem.getId() <= 0) {
            problem.setId(problemIdCounter++);
        } else {
            problemIdCounter = Math.max(problemIdCounter, problem.getId() + 1);
        }
        
        findById(problem.getId()).ifPresent(problems::remove);
        addProblem(problem);
    }

    public void deleteProblem(int id) {
        problems.removeIf(p -> p.getId() == id);
        saveProblems();
    }

    public Optional<Problem> findById(int id) {
        return problems.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    public List<Problem> findByResident(int residentId) {
        List<Problem> result = new ArrayList<>();
        for (Problem p : problems) {
            if (p.getResident().getId() == residentId) {
                result.add(p);
            }
        }
        return result;
    }
}
