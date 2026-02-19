package com.example.bankcards.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class DataSourceConfig {

  @Bean
  @Primary
  public DataSource dataSource(
          @Value("${SPRING_DATASOURCE_URL}") String url,
          @Value("${SPRING_DATASOURCE_USERNAME_FILE:}") String usernameFile,
          @Value("${SPRING_DATASOURCE_PASSWORD_FILE:}") String passwordFile,
          @Value("${SPRING_DATASOURCE_DRIVER_CLASS_NAME}") String driverClassName,
          @Value("${SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT:30000}") long connectionTimeout,
          @Value("${SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE:10}") int maximumPoolSize) throws Exception {

    System.out.println("=================================");
    System.out.println("DataSourceConfig INITIALIZATION");
    System.out.println("=================================");
    System.out.println("URL: " + url);
    System.out.println("Username file: " + usernameFile);
    System.out.println("Password file: " + passwordFile);

    String username;
    String password;

    // Читаем username из файла секрета
    if (usernameFile != null && !usernameFile.isEmpty()) {
      Path path = Paths.get(usernameFile);
      System.out.println("Username file exists: " + Files.exists(path));

      if (Files.exists(path)) {
        username = Files.readString(path).trim();
        System.out.println("Username loaded: " + username);
      } else {
        System.err.println("Username file NOT FOUND: " + usernameFile);
        username = System.getenv("SPRING_DATASOURCE_USERNAME");
        System.out.println("Using env username: " + username);
      }
    } else {
      System.out.println("Username file not specified, using env");
      username = System.getenv("SPRING_DATASOURCE_USERNAME");
    }

    // Читаем password из файла секрета
    if (passwordFile != null && !passwordFile.isEmpty()) {
      Path path = Paths.get(passwordFile);
      System.out.println("Password file exists: " + Files.exists(path));

      if (Files.exists(path)) {
        password = Files.readString(path).trim();
        System.out.println("Password loaded, length: " + password.length());
      } else {
        System.err.println("Password file NOT FOUND: " + passwordFile);
        password = System.getenv("SPRING_DATASOURCE_PASSWORD");
        System.out.println("Using env password");
      }
    } else {
      System.out.println("Password file not specified, using env");
      password = System.getenv("SPRING_DATASOURCE_PASSWORD");
    }

    System.out.println("Creating DataSource with username: " + username);

    HikariDataSource dataSource = DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .driverClassName(driverClassName)
            .url(url)
            .username(username)
            .password(password)
            .build();

    dataSource.setConnectionTimeout(connectionTimeout);
    dataSource.setMaximumPoolSize(maximumPoolSize);

    System.out.println("DataSource created successfully!");
    System.out.println("=================================");

    return dataSource;
  }
}