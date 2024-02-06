package org.example;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase("UnicornDB");
            UnicornDBManager manager = new UnicornDBManager(database);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nMenú de Opciones:");
                System.out.println("1. Añadir Unicornio");
                System.out.println("2. Actualizar Ubicación de Unicornio");
                System.out.println("3. Reemplazar Unicornio");
                System.out.println("4. Eliminar Unicornio");
                System.out.println("5. Mostrar Todos los Unicornios");
                System.out.println("6. Buscar Unicornios por Color");
                System.out.println("8. Contar Unicornios por Color");
                System.out.println("9. Exportar Datos (CSV/JSON)");
                System.out.println("10. Importar Datos (CSV/JSON)");
                System.out.println("11. Salir");
                System.out.print("Elige una opción: ");

                int option = scanner.nextInt();

                switch (option) {
                    case 1:
                        manager.addUnicorn();
                        break;
                    case 2:
                        manager.updateUnicorn();
                        break;
                    case 3:
                        manager.replaceUnicorn();
                        break;
                    case 4:
                        manager.deleteUnicorn();
                        break;
                    case 5:
                        manager.showAllUnicorns();
                        break;
                    case 6:
                        manager.searchUnicornsByColor();
                        break;
                    case 7:
                        manager.deleteAllUnicorns();
                        break;
                    case 8:
                        manager.countUnicornsByColor();
                        break;
                    case 9:
                        System.out.print("Formato para exportar (csv/json): ");
                        scanner.nextLine();
                        String exportFormat = scanner.nextLine();
                        manager.exportData(exportFormat);
                        break;

                    case 10:
                        System.out.print("Formato para importar (csv/json): ");
                        scanner.nextLine();
                        String importFormat = scanner.nextLine();
                        System.out.print("Ruta del archivo: ");
                        String filePath = scanner.nextLine();
                        manager.importData(importFormat, filePath);
                        break;
                    case 11:
                        System.out.println("Saliendo del programa.");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Opción no válida. Por favor, intenta de nuevo.");
                }
            }
        }
    }
}
