package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.util.Arrays.asList;

public class UnicornDBManager {
    private final MongoDatabase database;
    private final Scanner scanner;

    public UnicornDBManager(MongoDatabase database) {
        this.database = database;
        this.scanner = new Scanner(System.in);
    }

    public void addUnicorn() {
        try {
            System.out.println("Introduce los datos del unicornio (nombre, color, ubicación, poder mágico):");
            System.out.print("Nombre: ");
            String name = scanner.nextLine();
            System.out.print("Color: ");
            String color = scanner.nextLine();
            System.out.print("Ubicación: ");
            String location = scanner.nextLine();
            System.out.print("Poder Mágico: ");
            String magicPower = scanner.nextLine();

            MongoCollection<Document> collection = database.getCollection("unicorns");
            Document unicorn = new Document("name", name)
                    .append("color", color)
                    .append("location", location)
                    .append("magicPower", magicPower);
            collection.insertOne(unicorn);
            System.out.println("Unicornio añadido con éxito.");
        } catch (Exception e) {
            System.err.println("Error al añadir el unicornio: " + e.getMessage());
        }
    }

    public void updateUnicorn() {
        try {
            System.out.println("Introduce el nombre del unicornio y la nueva ubicación:");
            System.out.print("Nombre: ");
            String name = scanner.nextLine();
            System.out.print("Nueva Ubicación: ");
            String newLocation = scanner.nextLine();

            MongoCollection<Document> collection = database.getCollection("unicorns");
            collection.updateOne(Filters.eq("name", name), Updates.set("location", newLocation));
            System.out.println("Unicornio actualizado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el unicornio: " + e.getMessage());
        }
    }

    public void replaceUnicorn() {
        try {
            System.out.println("Introduce los datos para reemplazar el unicornio (nombre, nuevo color, nueva ubicación, nuevo poder mágico):");
            System.out.print("Nombre: ");
            String name = scanner.nextLine();
            System.out.print("Nuevo Color: ");
            String newColor = scanner.nextLine();
            System.out.print("Nueva Ubicación: ");
            String newLocation = scanner.nextLine();
            System.out.print("Nuevo Poder Mágico: ");
            String newMagicPower = scanner.nextLine();

            MongoCollection<Document> collection = database.getCollection("unicorns");
            Document newUnicorn = new Document("name", name)
                    .append("color", newColor)
                    .append("location", newLocation)
                    .append("magicPower", newMagicPower);
            collection.replaceOne(Filters.eq("name", name), newUnicorn);
            System.out.println("Unicornio reemplazado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al reemplazar el unicornio: " + e.getMessage());
        }
    }

    public void deleteUnicorn() {
        try {
            System.out.println("Introduce el nombre del unicornio a eliminar:");
            System.out.print("Nombre: ");
            String name = scanner.nextLine();

            MongoCollection<Document> collection = database.getCollection("unicorns");
            collection.deleteOne(Filters.eq("name", name));
            System.out.println("Unicornio eliminado con éxito.");
        } catch (Exception e) {
            System.err.println("Error al eliminar el unicornio: " + e.getMessage());
        }
    }

    public void showAllUnicorns() {
        try {
            MongoCollection<Document> collection = database.getCollection("unicorns");
            for (Document unicorn : collection.find()) {
                System.out.println(unicorn.toJson());
            }
        } catch (Exception e) {
            System.err.println("Error al mostrar los unicornios: " + e.getMessage());
        }
    }

    public void searchUnicornsByColor() {
        try {
            System.out.println("Introduce el color para buscar unicornios:");
            System.out.print("Color: ");
            String color = scanner.nextLine();

            MongoCollection<Document> collection = database.getCollection("unicorns");
            for (Document unicorn : collection.find(Filters.eq("color", color))) {
                System.out.println(unicorn.toJson());
            }
        } catch (Exception e) {
            System.err.println("Error al buscar unicornios por color: " + e.getMessage());
        }
    }

    public void deleteAllUnicorns() {
        try {
            MongoCollection<Document> collection = database.getCollection("unicorns");
            collection.deleteMany(new Document());
            System.out.println("Todos los unicornios han sido eliminados.");
        } catch (Exception e) {
            System.err.println("Error al eliminar todos los unicornios: " + e.getMessage());
        }
    }

    public void countUnicornsByColor() {
        try {
            MongoCollection<Document> collection = database.getCollection("unicorns");
            AggregateIterable<Document> result = collection.aggregate(List.of(
                    Aggregates.group("$color", Accumulators.sum("count", 1))
            ));

            for (Document doc : result) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e) {
            System.err.println("Error al contar unicornios por color: " + e.getMessage());
        }
    }

    public void exportData(String format) {
        try {
            MongoCollection<Document> collection = database.getCollection("unicorns");
            FindIterable<Document> documents = collection.find();
            List<Document> docList = new ArrayList<>();
            documents.into(docList);

            switch (format.toLowerCase()) {
                case "json":
                    exportToJson(docList);
                    System.out.println("Datos exportados con éxito en formato " + format.toUpperCase() + ".");
                    break;
                case "csv":
                    exportToCsv(docList);
                    System.out.println("Datos exportados con éxito en formato " + format.toUpperCase() + ".");
                    break;
                default:
                    System.err.println("Formato no soportado.");
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error al exportar datos: " + e.getMessage());
        }
    }

    private void exportToJson(List<Document> documents) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(documents);
            try (FileWriter writer = new FileWriter("unicorns.json")) {
                writer.write(json);
            }
        } catch (Exception e) {
            System.err.println("Error al exportar datos a JSON: " + e.getMessage());
        }
    }

    private void exportToCsv(List<Document> documents) {
        try (FileWriter writer = new FileWriter("unicorns.csv")) {
            // Escribir la cabecera del CSV si es necesario
            writer.write("Nombre,Color,Ubicación,Poder Mágico\n");

            for (Document doc : documents) {
                String name = doc.getString("name");
                String color = doc.getString("color");
                String location = doc.getString("location");
                String magicPower = doc.getString("magicPower");

                // Escapar comas y caracteres especiales si es necesario

                String csvLine = name + "," + color + "," + location + "," + magicPower + "\n";
                writer.write(csvLine);
            }
        } catch (Exception e) {
            System.err.println("Error al exportar datos a CSV: " + e.getMessage());
        }
    }


    public void importData(String format, String filePath) {
        try {
            switch (format.toLowerCase()) {
                case "json":
                    importFromJson(filePath);
                    System.out.println("Datos importados con éxito desde " + filePath + ".");
                    break;
                case "csv":
                    importFromCsv(filePath);
                    System.out.println("Datos importados con éxito desde " + filePath + ".");
                    break;
                default:
                    System.err.println("Formato no soportado.");
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error al importar datos desde " + filePath + ": " + e.getMessage());
        }
    }

    private void importFromJson(String filePath) {
        try {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(filePath)) {
                List<Document> documents = gson.fromJson(reader, new TypeToken<List<Document>>() {
                }.getType());

                MongoCollection<Document> collection = database.getCollection("unicorns");
                collection.insertMany(documents);
            }
        } catch (Exception e) {
            System.err.println("Error al importar datos desde JSON: " + e.getMessage());
        }
    }


    private void importFromCsv(String filePath) { //comprobar tu Gabriela
        //comprobado*
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            MongoCollection<Document> collection = database.getCollection("unicorns");
            List<Document> documents = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); // Asegúrate de que este delimitador coincida con tu archivo CSV
                // Asumiendo que sabes el orden y tipo de los datos en el CSV
                Document doc = new Document("campo1", data[0])
                        .append("campo2", data[1])
                        .append("campo3", data[2]); // y así sucesivamente
                documents.add(doc);
            }

            if (!documents.isEmpty()) {
                collection.insertMany(documents);
                System.out.println("Datos importados con éxito desde " + filePath + ".");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
