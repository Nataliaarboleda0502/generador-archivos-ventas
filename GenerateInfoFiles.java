import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateInfoFiles {

    private static final String[] NOMBRES = {
            "Carlos", "Maria", "Andres", "Luisa", "Juan", "Sofia",
            "Miguel", "Valentina", "Santiago", "Camila", "Jorge", "Isabella",
            "David", "Natalia", "Felipe", "Daniela", "Alejandro", "Juliana"
    };

    private static final String[] APELLIDOS = {
            "Garcia", "Rodriguez", "Martinez", "Lopez", "Gonzalez", "Perez",
            "Sanchez", "Ramirez", "Torres", "Flores", "Rivera", "Gomez",
            "Diaz", "Reyes", "Morales", "Jimenez", "Cruz", "Vargas"
    };

    private static final String[] NOMBRES_PRODUCTOS = {
            "Laptop", "Mouse", "Teclado", "Monitor", "Audífonos",
            "Webcam", "Tablet", "Impresora", "Disco Duro", "Memoria USB",
            "Celular", "Cargador", "Micrófono", "Silla Ergonómica"
    };

    private static final String[] TIPOS_DOCUMENTO = { "CC", "TI", "PPT" };

    private static final Random random = new Random();

    public static void main(String[] args) {

        int cantidadVendedores = 5;
        int cantidadProductos  = 10;
        int ventasPorVendedor  = 6;

        try {
            System.out.println("=== Iniciando generación de archivos de prueba ===");

            createProductsFile(cantidadProductos);
            System.out.println("[OK] Archivo de productos generado: productos.txt");

            createSalesManInfoFile(cantidadVendedores);
            System.out.println("[OK] Archivo de vendedores generado: vendedores.txt");

            for (int i = 0; i < cantidadVendedores; i++) {
                String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
                long   id     = 10000000L + random.nextInt(90000000);
                createSalesMenFile(ventasPorVendedor, nombre, id, cantidadProductos);
                System.out.println("[OK] Archivo de ventas generado: sales_" + nombre + "_" + id + ".txt");
            }

            System.out.println("=== Generación de archivos finalizada exitosamente ===");

        } catch (IOException e) {
            System.err.println("[ERROR] Ocurrió un problema al generar los archivos: " + e.getMessage());
        }
    }

    public static void createSalesMenFile(int randomSalesCount, String name, long id, int totalProducts)
            throws IOException {

        String nombreArchivo = "sales_" + name + "_" + id + ".txt";

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(nombreArchivo))) {
            String tipoDocumento = TIPOS_DOCUMENTO[random.nextInt(TIPOS_DOCUMENTO.length)];
            escritor.write(tipoDocumento + ";" + id);
            escritor.newLine();

            for (int i = 0; i < randomSalesCount; i++) {
                int idProducto = random.nextInt(totalProducts) + 1;
                int cantidad   = random.nextInt(20) + 1;
                escritor.write(idProducto + ";" + cantidad + ";");
                escritor.newLine();
            }
        }
    }

    public static void createProductsFile(int productsCount) throws IOException {

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("productos.txt"))) {
            for (int i = 1; i <= productsCount; i++) {
                String nombreProducto = NOMBRES_PRODUCTOS[(i - 1) % NOMBRES_PRODUCTOS.length];
                int precio = (random.nextInt(300) + 5) * 10000;
                escritor.write(i + ";" + nombreProducto + ";" + precio);
                escritor.newLine();
            }
        }
    }

    public static void createSalesManInfoFile(int salesmanCount) throws IOException {

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("vendedores.txt"))) {
            for (int i = 0; i < salesmanCount; i++) {
                String tipoDocumento   = TIPOS_DOCUMENTO[random.nextInt(TIPOS_DOCUMENTO.length)];
                long   numeroDocumento = 10000000L + random.nextInt(90000000);
                String nombre          = NOMBRES[random.nextInt(NOMBRES.length)];
                String apellido        = APELLIDOS[random.nextInt(APELLIDOS.length)];
                escritor.write(tipoDocumento + ";" + numeroDocumento + ";" + nombre + ";" + apellido);
                escritor.newLine();
            }
        }
    }
}
