package com.martinrist.phonebook;


import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App extends Application<PhonebookConfiguration>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @Override
    public void initialize(Bootstrap<PhonebookConfiguration> bootstrap) {}

    @Override
    public void run(PhonebookConfiguration configuration, Environment environment) throws Exception
    {
        LOGGER.info("Method App#run() called");

        for (int i = 0; i < configuration.getMessageRepetitions(); i++) {
            System.out.println(configuration.getMessage());
        }

        System.out.println(configuration.getAdditionalMessage());
    }

    public static void main(String[] args) throws Exception
    {
        new App().run(args);
    }
}
