package org.example;

public class MyHashMapDemo {
    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap<>(4);

        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        System.out.println(map.get("a")); // 1
        System.out.println(map.get("x")); // null

        map.put("b", 20);
        System.out.println(map.get("b")); // 20

        map.remove("c");
        System.out.println(map.containsKey("c")); // false
    }
}
