package pers.qh.collection;

import org.assertj.core.util.Lists;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionDemo {
    public static void main(String[] args) {
        Product product1 = new Product().setId(1).setNum(10).setPrice(100).setName("华为").setCategory("手机");
        Product product2 = new Product().setId(2).setNum(20).setPrice(200).setName("联想").setCategory("电脑");
        Product product3 = new Product().setId(3).setNum(30).setPrice(300).setName("波音747").setCategory("飞机");
        Product product4 = new Product().setId(4).setNum(40).setPrice(400).setName("奔驰").setCategory("汽车");
        Product product5 = new Product().setId(5).setNum(50).setPrice(500).setName("江诗丹顿").setCategory("手表");
        ArrayList<Product> productArrayList = Lists.newArrayList(product1, product2, product3, product4, product5);
        //productArrayList.forEach(product -> System.out.println("product = " + product));
//        Iterator<Product> productIterator = productArrayList.iterator();
//        productIterator.forEachRemaining(product -> System.out.println("product = " + product));
//        productArrayList.stream().forEach(product -> System.out.println("product = " + product));

        Stream<Product> productStream = productArrayList.stream();
//        Stream<Product> limit = productStream.limit(3);
//        limit.forEach(product -> System.out.println("product = " + product));

//        Stream<Product> skip = productStream.skip(3);
//        skip.forEach(product -> System.out.println("product = " + product));

//        List<String> mapList = productStream.map(Product::getCategory).collect(Collectors.toList());
//        mapList.forEach(category -> System.out.println("category = " + category));

//        Map<String, List<Product>> listMap = productStream.collect(Collectors.groupingBy(Product::getCategory));
//        Set<Map.Entry<String, List<Product>>> entriedSet = listMap.entrySet();
//        entriedSet.forEach(stringListEntry -> {
//            System.out.println("key = " + stringListEntry.getKey());
//            System.out.println("value = " + stringListEntry.getValue());
//        });

        ArrayList<String> newedArrayList = Lists.newArrayList("a", "b", "c", "d", "e", "f");
//        ArrayList<String> arrayList = new ArrayList<>();
//        newedArrayList.forEach(s -> {
//            String sUpperCase = s.toUpperCase();
//            arrayList.add(sUpperCase);
//        });
//        arrayList.forEach(s -> System.out.println("s = " + s));

        List<String> stringList = newedArrayList.stream().map(String::toUpperCase).collect(Collectors.toList());
        stringList.forEach(s -> System.out.println("s = " + s));
    }
}
