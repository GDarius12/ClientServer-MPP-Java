package Darius;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@ComponentScan({"Darius", "Darius.repository"})
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean(name = "props")
    @Primary
    public Properties getBdProperties(){
        Properties props = new Properties();
        try {
            System.out.println("Cautam fisierul bd.config "+((new File(".")).getAbsolutePath()));
            props.load(Main.class.getClassLoader().getResourceAsStream("bd.config"));
        } catch (IOException e) {
            System.err.println("Fisierul de configurare bd.config nu a fost gasit" + e);

        }
        return props;
    }
}