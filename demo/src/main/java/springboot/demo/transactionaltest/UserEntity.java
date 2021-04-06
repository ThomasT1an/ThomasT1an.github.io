package springboot.demo.transactionaltest;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

/**
 * @Author: tzy
 * @Description:
 * @Date: Create in 16:39 2021-04-04
 */
@Entity
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    private String name;
}
