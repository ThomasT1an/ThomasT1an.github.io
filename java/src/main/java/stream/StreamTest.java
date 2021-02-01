package stream;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 15:55 2021-02-01
 */
public class StreamTest {
    public static void main(String[] args) throws Exception {
        List<Person> personList = new ArrayList<Person>();
        personList.add(new Person("Tom", 8900, 23, "male", "New York"));
        personList.add(new Person("Jack", 7000, 25, "male", "Washington"));
        personList.add(new Person("Lily", 7800, 21, "female", "Washington"));
        personList.add(new Person("Anni", 8200, 24, "female", "New York"));
        personList.add(new Person("Owen", 9500, 25, "male", "New York"));
        personList.add(new Person("Alisa", 7900, 26, "female", "New York"));


        List<String> list = Arrays.asList("adnm", "admmt", "pot", "xbangd", "weoujgsd");
        "https://juejin.cn/post/6900424495937355783"
        Optional<String> max = list.stream().max(Comparator.comparing(String::length));
        System.out.println(max.get());
    }
}
