package tzy.code.design.proxy;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 22:38 2021-03-15
 */
public class HelloServiceProxy implements HelloService{

    private HelloService helloService;

    public HelloServiceProxy(HelloService helloService){
        this.helloService = helloService;
    }

    @Override
    public String hello(int status, String msg) {
        System.out.println("before");
        String res = helloService.hello(status,msg);
        System.out.println("after");
        return res;
    }
}
