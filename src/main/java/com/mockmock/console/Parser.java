package com.mockmock.console;


import com.mockmock.Settings;
import org.apache.commons.cli.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class Parser
{
    /**
     * Parses the given parameters and returns the possible options
     * @param args String[]
     */
    public Settings parseOptions(String[] args, Settings settings)
    {
        // define the possible options
        Options options = new Options();
        options.addOption("p", true, "The mail port number to use. Default is 25000.");
        options.addOption("h", true, "The http port number to use. Default is 8282.");
        options.addOption("m", true, "The maximum size of the mail qeueue. Default is 1000.");
        options.addOption("i", true, "The irc server to use in the <server>:<port> format. Default is irc.tweakers.net:6667.");
        options.addOption("n", true, "The irc nickname to use. Default is 'mockmock'.");
        options.addOption("c", true, "Comma separated list of channels. Default is #postman");
        options.addOption("?", false, "Shows this help information");

        // parse the given arguments
        CommandLineParser parser = new PosixParser();

        try
        {
            CommandLine cmd = parser.parse(options, args);

            if(cmd.hasOption("?"))
            {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "java -jar MockMock.jar -p 25000 -h 8282", options );
                System.exit(0);
            }

            parseSmtpPortOption(cmd, settings);
            parseHttpPortOption(cmd, settings);
            parseMailQueueSizeOption(cmd, settings);
            parseIrcServerOption(cmd, settings);
            parseIrcNicknameOption(cmd, settings);
            parseIrcChannelsOption(cmd, settings);

        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return settings;
    }

    protected void parseSmtpPortOption(CommandLine cmd, Settings settings)
    {
        if(cmd.hasOption("p"))
        {
            try
            {
                settings.setSmtpPort(Integer.valueOf(cmd.getOptionValue("p")));
            }
            catch (NumberFormatException e)
            {
                System.err.println("Invalid mail port given, using default " + settings.getSmtpPort());
            }
        }
    }

    protected void parseHttpPortOption(CommandLine cmd, Settings settings)
    {
        if(cmd.hasOption("h"))
        {
            try
            {
                settings.setHttpPort(Integer.valueOf(cmd.getOptionValue("h")));
            }
            catch (NumberFormatException e)
            {
                System.err.println("Invalid http port given, using default " + settings.getHttpPort());
            }
        }
    }

    protected void parseMailQueueSizeOption(CommandLine cmd, Settings settings)
    {
        if(cmd.hasOption("m"))
        {
            try
            {
                settings.setMaxMailQueueSize(Integer.valueOf(cmd.getOptionValue("m")));
            }
            catch (NumberFormatException e)
            {
                System.err.println("Invalid max mail queue size given, using default " + settings.getMaxMailQueueSize());
            }
        }
    }

    protected void parseIrcServerOption(CommandLine cmd, Settings settings)
    {
        if(cmd.hasOption("i"))
        {
            try
            {
                String input = cmd.getOptionValue("i");
                int positionOfSeparator = input.indexOf(":");

                if (positionOfSeparator == -1)
                {
                    settings.setIrcServer(input);
                }
                else
                {
                    String server = input.substring(0, positionOfSeparator);
                    int port = Integer.valueOf(input.substring(positionOfSeparator+1, input.length()));
                    settings.setIrcServer(server);
                    settings.setIrcPort(port);
                }
            }
            catch (NumberFormatException e)
            {
                System.err.println("Invalid irc server given, using default " + settings.getIrcServer() + ":" + settings.getIrcPort());
            }
        }
    }

    protected void parseIrcNicknameOption(CommandLine cmd, Settings settings)
    {
        if(cmd.hasOption("n"))
        {
            try
            {
                settings.setNickname(cmd.getOptionValue("n"));
            }
            catch (NumberFormatException e)
            {
                System.err.println("Invalid nickname given, using default " + settings.getNickname());
            }
        }
    }

    protected void parseIrcChannelsOption(CommandLine cmd, Settings settings)
    {
        if(cmd.hasOption("c"))
        {
            try
            {
                String input = cmd.getOptionValue("c");
                String[] channels = input.split(",");
                settings.setChannels(new HashSet<>(Arrays.asList(channels)));
            }
            catch (NumberFormatException e)
            {
                System.err.println("Invalid channels given, using default " + settings.getChannels());
            }
        }
    }
}
