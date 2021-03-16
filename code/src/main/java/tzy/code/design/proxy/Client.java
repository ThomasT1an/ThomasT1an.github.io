package tzy.code.design.proxy;

import org.junit.Test;
import sun.misc.ProxyGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.Arrays;
import java.io.File;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 22:45 2021-03-15
 */
public class Client {

    /**
     * 静态代理
     */
    void test1(){
        HelloService target = new HelloServiceImpl();
        HelloService helloService = new HelloServiceProxy(target);
        helloService.hello(0,"success");
    }

    /**
     * 动态代理
     */
    void test2(){
        HelloService target = new HelloServiceImpl();

        HelloService helloService = (HelloService) Proxy.newProxyInstance(Client.class.getClassLoader(), new Class[]{HelloService.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //前置处理 打印参数
                System.out.println(Arrays.deepToString(args));
                //实际调用
                return method.invoke(target,args);
            }
        });
        helloService.hello(0,"success");
    }


    public static void main(String[] args) {
        Client client = new Client();
        //client.test1();
        client.test2();
    }

    @Test
    public void createProxyClass() throws Exception{
        byte[] bytes = ProxyGenerator.generateProxyClass("HelloService$Proxy",new Class[]{HelloService.class});
        Files.write(new File("/Users/tianzhongyi/Desktop/私人/docsify-blog/code/target/classes/tzy/code/design/proxy/HelloService$proxy.class").toPath(),bytes);
    }
}
