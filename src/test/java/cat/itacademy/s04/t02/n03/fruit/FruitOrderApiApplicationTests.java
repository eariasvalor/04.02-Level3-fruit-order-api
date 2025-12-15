package cat.itacademy.s04.t02.n03.fruit;

import cat.itacademy.s04.t02.n03.fruit.config.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestContainersConfiguration.class)
class FruitOrderApiApplicationTests {
    @Autowired
    private MongoTemplate mongoTemplate;


    @Test
    void contextLoads() {
        assertThat(mongoTemplate).isNotNull();
    }

    @Test
    void mongoDbConnectionIsEstablished() {
        String databaseName = mongoTemplate.getDb().getName();
        assertThat(databaseName).isEqualTo("fruit_orders_test");
    }
}
