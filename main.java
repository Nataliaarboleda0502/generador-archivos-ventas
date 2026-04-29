import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main {

    public static void main(String[] args) {

        try {
            System.out.println("=== Iniciando generacion de reportes ===");

            Map<Integer, String[]> productos = leerProductos("productos.txt");
            System.out.println("[OK] Productos leidos: " + productos.size());

            Map<Long, String> vendedores = leerVendedores("vendedores.txt");
            System.out.println("[OK] Vendedores leidos: " + vendedores.size());

            Map<Long, Long>    totalVentasPorVendedor   = new HashMap<Long, Long>();
            Map<Integer, Integer> totalCantidadPorProducto = new HashMap<Integer, Integer>();

            File   carpeta  = new File(".");
            File[] archivos = carpeta.listFiles();

            int archivosVentasProcesados = 0;

            for (File archivo : archivos) {
                if (archivo.getName().startsWith("sales_") && archivo.getName().endsWith(".txt")) {
                    procesarArchivoVentas(archivo, productos, totalVentasPorVendedor, totalCantidadPorProducto);
                    System.out.println("[OK] Archivo procesado: " + archivo.getName());
                    archivosVentasProcesados++;
                }
            }

            if (archivosVentasProcesados == 0) {
                System.err.println("[ERROR] No se encontraron archivos de ventas (sales_*.txt)");
                return;
            }

            generarReporteVendedores(totalVentasPorVendedor, vendedores);
            System.out.println("[OK] Reporte de vendedores generado: reporte_vendedores.csv");

            generarReporteProductos(totalCantidadPorProducto, productos);
            System.out.println("[OK] Reporte de productos generado: reporte_productos.csv");

            System.out.println("=== Reportes generados exitosamente ===");

        } catch (IOException e) {
            System.err.println("[ERROR] Ocurrio un problema: " + e.getMessage());
        }
    }

    public static Map<Integer, String[]> leerProductos(String rutaArchivo) throws IOException {

        Map<Integer, String[]> productos = new HashMap<Integer, String[]>();
        int numeroLinea = 0;

        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                String[] partes = linea.split(";");

                if (partes.length < 3) {
                    System.err.println("[ADVERTENCIA] productos.txt, linea " + numeroLinea
                            + ": formato incorrecto, se esperaban 3 campos -> " + linea);
                    continue;
                }

                int    id     = Integer.parseInt(partes[0].trim());
                String nombre = partes[1].trim();
                int    precio = Integer.parseInt(partes[2].trim());

                if (precio <= 0) {
                    System.err.println("[ADVERTENCIA] productos.txt, linea " + numeroLinea
                            + ": precio invalido (" + precio + ") para el producto " + nombre + ", linea ignorada");
                    continue;
                }

                productos.put(id, new String[]{nombre, String.valueOf(precio)});
            }
        }
        return productos;
    }

    public static Map<Long, String> leerVendedores(String rutaArchivo) throws IOException {

        Map<Long, String> vendedores = new HashMap<Long, String>();
        int numeroLinea = 0;

        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                String[] partes = linea.split(";");

                if (partes.length < 4) {
                    System.err.println("[ADVERTENCIA] vendedores.txt, linea " + numeroLinea
                            + ": formato incorrecto, se esperaban 4 campos -> " + linea);
                    continue;
                }

                long   documento      = Long.parseLong(partes[1].trim());
                String nombreCompleto = partes[2].trim() + " " + partes[3].trim();
                vendedores.put(documento, nombreCompleto);
            }
        }
        return vendedores;
    }

    public static void procesarArchivoVentas(
            File archivo,
            Map<Integer, String[]> productos,
            Map<Long, Long> totalVentasPorVendedor,
            Map<Integer, Integer> totalCantidadPorProducto) throws IOException {

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {

            String primeraLinea = lector.readLine();

            if (primeraLinea == null || primeraLinea.trim().isEmpty()) {
                System.err.println("[ADVERTENCIA] " + archivo.getName()
                        + ": el archivo esta vacio o le falta la primera linea");
                return;
            }

            String[] datosVendedor = primeraLinea.split(";");

            if (datosVendedor.length < 2) {
                System.err.println("[ADVERTENCIA] " + archivo.getName()
                        + ": formato incorrecto en la primera linea -> " + primeraLinea);
                return;
            }

            long idVendedor      = Long.parseLong(datosVendedor[1].trim());
            long totalDineroVendedor = 0;
            int  numeroLinea     = 1;

            String linea;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                String[] partes = linea.split(";");

                if (partes.length < 2) {
                    System.err.println("[ADVERTENCIA] " + archivo.getName() + ", linea " + numeroLinea
                            + ": formato incorrecto, se esperaban 2 campos -> " + linea);
                    continue;
                }

                int idProducto = Integer.parseInt(partes[0].trim());
                int cantidad   = Integer.parseInt(partes[1].trim());

                if (cantidad <= 0) {
                    System.err.println("[ADVERTENCIA] " + archivo.getName() + ", linea " + numeroLinea
                            + ": cantidad invalida (" + cantidad + "), linea ignorada");
                    continue;
                }
                if (!productos.containsKey(idProducto)) {
                    System.err.println("[ADVERTENCIA] " + archivo.getName() + ", linea " + numeroLinea
                            + ": el producto con ID " + idProducto + " no existe en productos.txt, linea ignorada");
                    continue;
                }
                long precio = Long.parseLong(productos.get(idProducto)[1]);
                totalDineroVendedor += precio * cantidad;
                int cantidadAnterior = totalCantidadPorProducto.containsKey(idProducto)
                        ? totalCantidadPorProducto.get(idProducto) : 0;
                totalCantidadPorProducto.put(idProducto, cantidadAnterior + cantidad);
            }
            long dineroAnterior = totalVentasPorVendedor.containsKey(idVendedor)
                    ? totalVentasPorVendedor.get(idVendedor) : 0;
            totalVentasPorVendedor.put(idVendedor, dineroAnterior + totalDineroVendedor);
        }
    }
    public static void generarReporteVendedores(
            Map<Long, Long> totalVentasPorVendedor,
            Map<Long, String> vendedores) throws IOException {

        List<Map.Entry<Long, Long>> lista = new ArrayList<Map.Entry<Long, Long>>(
                totalVentasPorVendedor.entrySet());

        Collections.sort(lista, new Comparator<Map.Entry<Long, Long>>() {
            public int compare(Map.Entry<Long, Long> a, Map.Entry<Long, Long> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("reporte_vendedores.csv"))) {
            for (Map.Entry<Long, Long> entrada : lista) {
                long   idVendedor  = entrada.getKey();
                long   totalDinero = entrada.getValue();
                String nombre      = vendedores.containsKey(idVendedor)
                        ? vendedores.get(idVendedor) : "Desconocido";
                escritor.write(nombre + ";" + totalDinero);
                escritor.newLine();
            }
        }
    }
    public static void generarReporteProductos(
            Map<Integer, Integer> totalCantidadPorProducto,
            Map<Integer, String[]> productos) throws IOException {

        List<Map.Entry<Integer, Integer>> lista = new ArrayList<Map.Entry<Integer, Integer>>(
                totalCantidadPorProducto.entrySet());

        Collections.sort(lista, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> a, Map.Entry<Integer, Integer> b) {
                return b.getValue().compareTo(a.getValue());
            }
        });

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("reporte_productos.csv"))) {
            for (Map.Entry<Integer, Integer> entrada : lista) {
                int    idProducto = entrada.getKey();
                int    cantidad   = entrada.getValue();
                String nombre     = productos.get(idProducto)[0];
                String precio     = productos.get(idProducto)[1];
                escritor.write(nombre + ";" + precio + ";" + cantidad);
                escritor.newLine();
            }
        }
    }
}
