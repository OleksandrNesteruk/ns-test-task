package org.example;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    static class User {
        private String name;
        private int age;

        private User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public static User createUser(String name, int age) {
            return new User(name, age);
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    abstract static class Product {
        private String name;
        private double price;

        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public double getPrice() {
            return price;
        }

        public String getName() {
            return name;
        }
    }

    static class RealProduct extends Product {
        private int size;
        private int weight;

        public RealProduct(String name, double price, int size, int weight) {
            super(name, price);
            this.size = size;
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return "RealProduct{" +
                    "size=" + size +
                    ", weight=" + weight +
                    ", name='" + getName() + '\'' +
                    ", price=" + getPrice() +
                    '}';
        }
    }

    static class VirtualProduct extends Product {
        private String code;
        private LocalDate expirationDate;

        public VirtualProduct(String name, double price, String code, LocalDate expirationDate) {
            super(name, price);
            this.code = code;
            this.expirationDate = expirationDate;
        }

        @Override
        public String toString() {
            return "VirtualProduct{" +
                    "code='" + code + '\'' +
                    ", expirationDate=" + expirationDate +
                    ", name='" + getName() + '\'' +
                    ", price=" + getPrice() +
                    '}';
        }
    }

    static class ProductFactory {
        public static Product createRealProduct(String name, double price, int size, int weight) {
            return new RealProduct(name, price, size, weight);
        }

        public static Product createVirtualProduct(String name, double price, String code, LocalDate date) {
            return new VirtualProduct(name, price, code, date);
        }
    }

    static class Order {
        private User user;
        private List<Product> products;

        private Order(User user, List<Product> products) {
            this.user = user;
            this.products = products;
        }

        public static Order createOrder(User user, List<Product> products) {
            return new Order(user, products);
        }

        public List<Product> getProducts() {
            return products;
        }

        public User getUser() {
            return user;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "user=" + user;
        }
    }

    public static void main(String[] args) {
        User user1 = User.createUser("Alice", 32);
        User user2 = User.createUser("Bob", 19);
        User user3 = User.createUser("Charlie", 20);
        User user4 = User.createUser("John", 27);

        Product realProduct1 = ProductFactory.createRealProduct("Product A", 20.50, 10, 25);
        Product realProduct2 = ProductFactory.createRealProduct("Product B", 50, 6, 17);

        Product virtualProduct1 = ProductFactory.createVirtualProduct("Product C", 100, "xxx", LocalDate.of(2023, 5, 12));
        Product virtualProduct2 = ProductFactory.createVirtualProduct("Product D", 81.25, "yyy", LocalDate.of(2024, 6, 20));

        List<Order> orders = new ArrayList<>() {{
            add(Order.createOrder(user1, List.of(realProduct1, virtualProduct1, virtualProduct2)));
            add(Order.createOrder(user2, List.of(realProduct1, realProduct2)));
            add(Order.createOrder(user3, List.of(realProduct1, virtualProduct2)));
            add(Order.createOrder(user4, List.of(virtualProduct1, virtualProduct2, realProduct1, realProduct2)));
        }};

        class VirtualProductCodeManager {
            private static VirtualProductCodeManager instance;
            private Set<String> usedCodes;

            private VirtualProductCodeManager() {
                usedCodes = new HashSet<>();
            }

            public static VirtualProductCodeManager getInstance() {
                if (instance == null) {
                    instance = new VirtualProductCodeManager();
                }
                return instance;
            }

            public void useCode(String code) {
                usedCodes.add(code);
            }

            public boolean isCodeUsed(String code) {
                return usedCodes.contains(code);
            }
        }
        System.out.println("1. Create singleton class VirtualProductCodeManager \n");
        VirtualProductCodeManager virtualProductCodeManager = VirtualProductCodeManager.getInstance();
        virtualProductCodeManager.useCode("xxx");
        System.out.println("Is code used: " + virtualProductCodeManager.isCodeUsed("xxx") + "\n");
        System.out.println("Is code used: " + virtualProductCodeManager.isCodeUsed("yyy") + "\n");

        Product mostExpensive = getMostExpensiveProduct(orders);
        System.out.println("2. Most expensive product: " + mostExpensive + "\n");

        Product mostPopular = getMostPopularProduct(orders);
        System.out.println("3. Most popular product: " + mostPopular + "\n");

        double averageAge = calculateAverageAge(realProduct2, orders);
        System.out.println("4. Average age is: " + averageAge + "\n");

        Map<Product, List<User>> productUserMap = getProductUserMap(orders);
        System.out.println("5. Map with products as keys and list of users as value \n");
        productUserMap.forEach((key, value) -> System.out.println("key: " + key + " " + "value: " + value + "\n"));

        List<Product> productsByPrice = sortProductsByPrice(List.of(realProduct1, realProduct2, virtualProduct1, virtualProduct2));
        System.out.println("6. a) List of products sorted by price: " + productsByPrice + "\n");
        List<Order> ordersByUserAgeDesc = sortOrdersByUserAgeDesc(orders);
        System.out.println("6. b) List of orders sorted by user agge in descending order: " + ordersByUserAgeDesc + "\n");

        Map<Order, Integer> result = calculateWeightOfEachOrder(orders);
        System.out.println("7. Calculate the total weight of each order \n");
        result.forEach((key, value) -> System.out.println("order: " + key + " " + "total weight: " + value + "\n"));
    }

    private static Product getMostExpensiveProduct(List<Order> orders) {
        return orders.stream()
                .map(Order::getProducts)
                .flatMap(Collection::stream)
                .max(Comparator.comparing(Product::getPrice))
                .orElse(null);
    }

    private static Product getMostPopularProduct(List<Order> orders) {
        Map<Product, Long> map = orders.stream()
                .map(Order::getProducts)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static double calculateAverageAge(Product product, List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getProducts().contains(product))
                .map(Order::getUser)
                .mapToDouble(User::getAge)
                .average()
                .orElseThrow(RuntimeException::new);
    }

    private static Map<Product, List<User>> getProductUserMap(List<Order> orders) {
        return orders.stream()
                .map(Order::getProducts)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toMap(product -> product, product -> orders.stream()
                        .filter(order -> order.getProducts().contains(product))
                        .map(Order::getUser)
                        .toList()));
    }

    private static List<Product> sortProductsByPrice(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparing(Product::getPrice))
                .toList();
    }

    private static List<Order> sortOrdersByUserAgeDesc(List<Order> orders) {
        List<Order> sortedOrders = orders.stream()
                .sorted(Comparator.comparing(order -> order.getUser().getAge()))
                .collect(Collectors.toList());
        Collections.reverse(sortedOrders);
        return sortedOrders;
    }

    private static Map<Order, Integer> calculateWeightOfEachOrder(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.toMap(order -> order, order -> order.getProducts().stream()
                        .filter(product -> product instanceof RealProduct)
                        .mapToInt(product -> ((RealProduct) product).getWeight())
                        .sum()));
    }
}
