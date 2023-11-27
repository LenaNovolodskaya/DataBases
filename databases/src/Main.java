import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {

    /**
     * Считывает данные о товарах из файла и возвращает карту с ID товаров и их наименованиями.
     *
     * @param fileName
     *            имя файла с данными о товарах
     * @return карта с ID товаров и их наименованиями
     */
    private static Map<Integer, String> readProducts(String fileName) {
        Map<Integer, String> products = new HashMap<>();
        try {
            // Считываем данные из файла
            String content = new Scanner(new File(fileName)).useDelimiter("Z").next();
            JSONArray productsArray = new JSONArray(content);

            // Записываем данные в карту
            int totalProducts = productsArray.length();
            for (int i = 0; i < totalProducts; i++) {
                JSONObject product = productsArray.getJSONObject(i);
                int id = product.getInt("product_id");
                String name = product.getString("product_name");
                products.put(id, name);
            }

        }

         catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    /**
    * Считывает данные о продавцах из файла и возвращает карту с ID продавцов и их фамилиями и именами.
    *
    * @param fileName
    *            имя файла с данными о продавцах
    * @return карта с ID продавцов и их фамилиями и именами
    */
    private static Map<Integer, String> readSellers(String fileName) {
        Map<Integer, String> sellers = new HashMap<>();
        try {
            // Считываем данные из файла
            String content = new Scanner(new File(fileName)).useDelimiter("Z").next();
            JSONArray sellersArray = new JSONArray(content);

            // Записываем данные в карту
            int totalSellers = sellersArray.length();
            for (int i = 0; i < totalSellers; i++) {
                JSONObject seller = sellersArray.getJSONObject(i);
                int id = seller.getInt("seller_id");
                String lastName = seller.getString("last_name");
                String firstName = seller.getString("first_name");
                sellers.put(id, lastName + " " + firstName);
            }

        }

         catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return sellers;
    }

    /**
     * Считывает данные о наличии товаров у продавцов из файла и возвращает карту с ID продавцов и картами с ID товаров и информацией о них.
     *
     * @param fileName
     *            имя файла с данными о наличии товаров у продавцов
     * @return карта с ID продавцов и картами с ID товаров и информацией о них
     */
    private static Map<Integer, Map<Integer, ProductInfo>> readProductsInfo(String fileName) {
        Map<Integer, Map<Integer, ProductInfo>> productsInfo = new HashMap<>();
        try {
            // Считываем данные из файла
            String content = new Scanner(new File(fileName)).useDelimiter("Z").next();
                    JSONArray productsInfoArray = new JSONArray(content);

            // Записываем данные в карту
            int totalProductsInfo = productsInfoArray.length();
            for (int i = 0; i < totalProductsInfo; i++) {
                JSONObject productInfo = productsInfoArray.getJSONObject(i);
                int sellerId = productInfo.getInt("seller_id");
                int productId = productInfo.getInt("product_id");
                int price = productInfo.getInt("product_price");
                int amount = productInfo.getInt("product_count");

                // Создаем карту с информацией о товаре
                Map<Integer, ProductInfo> sellerProducts = productsInfo.get(sellerId);
                if (sellerProducts == null)
                    sellerProducts = new HashMap<>();
                productsInfo.put(sellerId, sellerProducts);

                // Добавляем информацию о товаре в карту
                ProductInfo info = new ProductInfo(price, amount);
                sellerProducts.put(productId, info);
            }

        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return productsInfo;
    }

    /**
     * Считывает данные о продажах из файла и возвращает массив с информацией о продажах.
     *
     * @param fileName
     *            имя файла с данными о продажах
     * @return массив с информацией о продажах
     */
    private static JSONArray readSales(String fileName) {
        JSONArray sales = null;
        try {
            // Считываем данные из файла
            String content = new Scanner(new File(fileName)).useDelimiter("Z").next();
                    sales = new JSONArray(content);
        }

        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return sales;
    }


    public static void main(String[] args) {
        // Считываем данные из файлов
        Map<Integer, String> products = readProducts("products.json");
        Map<Integer, String> sellers = readSellers("sellers.json");
        Map<Integer, Map<Integer, ProductInfo>> productsInfo = readProductsInfo("stocks.json");
        JSONArray sales = readSales("sales.json");

        // Создаем новый файл для записи результатов
        File output = new File("output.xml");

        try {
            // Создаем объект для записи в файл
            FileWriter writer = new FileWriter(output);

            // Записываем общее количество товара каждого типа в наличии
            writer.write("<products>");
            for (int productId : products.keySet()) {
                int totalAmount = 0;
                for (Map<Integer, ProductInfo> sellerProducts : productsInfo.values()) {
                    if (sellerProducts.containsKey(productId)) {
                        totalAmount += sellerProducts.get(productId).getAmount();
                    }
                }
                writer.write("<product>");
                writer.write("<id>" + productId + "</id>");
                writer.write("<name>" + products.get(productId) + "</name>");
                writer.write("<total_amount>" + totalAmount + "</total_amount>");
                writer.write("</product>");
            }
            writer.write("</products>");


            // Считаем среднее количество проданных товаров в день
            int totalSales = sales.length();
            int totalSoldAmount = 0;
            for (int i = 0; i < totalSales; i++) {
                JSONObject sale = sales.getJSONObject(i);
                int productId = sale.getInt("product_id");
                int amount = sale.getInt("product_count_sold");
                totalSoldAmount += amount;
            }
            double averageSoldAmount = (double) totalSoldAmount / totalSales;

            // Записываем среднее количество проданных товаров в день в файл
            writer.write("<average_sold_amount>" + averageSoldAmount + "</average_sold_amount>");

            // Закрываем объект для записи в файл
            writer.close();
        }

         catch (IOException e) {
             e.printStackTrace();
         } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}