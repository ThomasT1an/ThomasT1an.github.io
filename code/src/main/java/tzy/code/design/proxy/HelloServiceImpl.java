package tzy.code.design.proxy;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 22:37 2021-03-15
 */
public class HelloServiceImpl implements HelloService{

    @Override
    public String hello(int status, String msg) {
        System.out.println("hello:"+status+" "+msg);
        return msg;
    }
}
