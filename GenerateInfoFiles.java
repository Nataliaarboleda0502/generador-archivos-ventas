import java.io.*;
import java.util.*;

public class GenerateInfoFiles {

    static Random random = new Random();

    public static void main(String[] args) {
        try {
            generarArchivos();
            System.out.println("=== Archivos generados exitosamente ===");
        } catch (IOException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    public static void generarArchivos() throws IOException {

        BufferedWriter prod = new BufferedWriter(new FileWriter("productos.txt"));
        for (int i = 1; i <= 5; i++) {
            prod.write(i + ";Producto" + i + ";" + (10000 * i));
            prod.newLine();
        }
        prod.close();

        BufferedWriter ven = new BufferedWriter(new FileWriter("vendedores.txt"));
        for (int i = 1; i <= 3; i++) {
            ven.write("CC;" + (1000 + i) + ";Vendedor" + i + ";Apellido" + i);
            ven.newLine();
        }
        ven.close();

        for (int i = 1; i <= 3; i++) {
            BufferedWriter sales = new BufferedWriter(new FileWriter("sales_" + i + ".txt"));
            sales.write("CC;" + (1000 + i));
            sales.newLine();

            for (int j = 0; j < 5; j++) {
                int prodId = random.nextInt(5) + 1;
                int cant = random.nextInt(10) + 1;
                sales.write(prodId + ";" + cant);
                sales.newLine();
            }
            sales.close();
        }
    }
}
