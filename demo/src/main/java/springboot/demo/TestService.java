package springboot.demo;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 17:35 2021-04-08
 */
@Slf4j
@Service
public class TestService {

    @Scheduled(fixedRate = 10000)
    public void test(){
        log.info("task");
    }
}
