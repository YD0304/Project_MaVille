package ca.udem.maville.repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class FilePersistenceService<T> {
    private final Gson gson;
    private final String filePath;
    private final Type listType;

    public FilePersistenceService(String fileName, TypeToken<List<T>> typeToken) {
        // Use pretty printing for readable JSON files.
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        // We will store our data files in a 'data' directory at the project root.
        this.filePath = "data/" + fileName;
        this.listType = typeToken.getType();
    }

    public List<T> readData() {
        try (FileReader reader = new FileReader(filePath)) {
            List<T> data = gson.fromJson(reader, listType);
            // If the file is empty or malformed, fromJson might return null.
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            // If the file doesn't exist yet, it's not an error. Return an empty list.
            return new ArrayList<>();
        }
    }

    public void writeData(List<T> data) {
        // Ensure the 'data' directory exists.
        new java.io.File("data").mkdirs();

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, listType, writer);
        } catch (IOException e) {
            System.err.println("FATAL: Could not write to file: " + filePath);
            e.printStackTrace();
        }
    }
}