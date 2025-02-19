package fairytale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"fairytale", "com.common"})
@EntityScan(basePackages = {"com/common/entity"})
public class FairytaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(FairytaleApplication.class, args);
    }

}