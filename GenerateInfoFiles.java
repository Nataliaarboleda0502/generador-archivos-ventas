import java.io.*;
public class SistemaVentas {

    static Random random = new Random();

    public static void main(String[] args) throws IOException {
        generarArchivos();
        procesarVentas();
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

    public static void procesarVentas() throws IOException {
        Map<Integer, Integer> ventasProducto = new HashMap<>();

        for (int i = 1; i <= 3; i++) {
            BufferedReader br = new BufferedReader(new FileReader("sales_" + i + ".txt"));
            br.readLine();
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                int id = Integer.parseInt(datos[0]);
                int cant = Integer.parseInt(datos[1]);

                ventasProducto.put(id, ventasProducto.getOrDefault(id, 0) + cant);
            }
            br.close();
        }

        System.out.println("=== REPORTE DE VENTAS ===");
        for (int id : ventasProducto.keySet()) {
            System.out.println("Producto " + id + ": " + ventasProducto.get(id));
        }
    }
}
