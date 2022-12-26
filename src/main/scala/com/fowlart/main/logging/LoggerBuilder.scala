package com.fowlart.main.logging

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.{Filter, LoggerContext}
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.{AppenderComponentBuilder, ConfigurationBuilder, ConfigurationBuilderFactory}
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration

import java.io.{IOException, InputStream}
import java.nio.file.{Files, Paths}
import java.util.Properties
import org.apache.logging.log4j.core.Logger

object LoggerBuilder {

  private lazy val loggerContext = {

     val propertiesPath = "src/main/resources/application.properties"

     def getPropertiesFromFile(filePath: String) = {
      val propsFromFile = new Properties
      try {
        val input = Files.newInputStream(Paths.get(filePath))
        try propsFromFile.load(input)
        catch {
          case ex: IOException =>
            ex.printStackTrace()
        } finally if (input != null) input.close()
      }
      propsFromFile
    }

    val builder = ConfigurationBuilderFactory.newConfigurationBuilder
    builder.setStatusLevel(Level.ALL)
    builder.setConfigurationName("BOTLog4JConfigs")
    builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).addAttribute("level", Level.INFO))

    /** <h3>CONSOLE appender <h3> */
    val consoleAppenderBuilder = builder.newAppender("Stdout", "CONSOLE").addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
    consoleAppenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"))
    consoleAppenderBuilder.add(builder.newFilter("MarkerFilter", Filter.Result.DENY, Filter.Result.NEUTRAL).addAttribute("marker", "FLOW"))
    builder.add(consoleAppenderBuilder)

    /** <h3>FILE appender <h3> */
    val pathToLogFile = s"${getPropertiesFromFile(propertiesPath).getProperty("logging.folder.path.tg")}${java.time.LocalDate.now.toString}.txt"
    val fileAppenderBuilder = builder.newAppender("FileAppender", "File")
    fileAppenderBuilder.addAttribute("fileName", pathToLogFile)
    fileAppenderBuilder.add(builder.newLayout("PatternLayout").addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"))
    builder.add(fileAppenderBuilder)

    //configure Root logger as FileLogger
    builder.add(builder
      .newRootLogger(Level.INFO)
      //switch loggers HERE
      .add(builder.newAppenderRef("FileAppender")))

    
    Configurator.initialize(builder.build)
  }

  def getFileLogger: Logger = loggerContext.getRootLogger
  
}
