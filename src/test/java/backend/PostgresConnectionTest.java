package backend;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.pabis.backend.config.DataSourceConfig;

@SpringBootTest(classes = DataSourceConfig.class)
public class PostgresConnectionTest {
	
	@Autowired
	DataSource dataSource;
	
	@Test @DisplayName("Testing connection with Postgre")
	public void connectionTest() {
		assertDoesNotThrow(() -> {
			dataSource.getConnection();
		});
	}
	
}
