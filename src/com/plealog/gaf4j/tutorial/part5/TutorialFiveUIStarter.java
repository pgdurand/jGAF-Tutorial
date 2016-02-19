/*  Copyright 2003-2016 Patrick G. Durand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.plealog.gaf4j.tutorial.part5;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.api.EZGenericApplication;
import com.plealog.genericapp.api.EZUIStarterListener;
import com.plealog.genericapp.api.log.EZLogger;
import com.plealog.genericapp.api.log.EZLoggerManager;
import com.plealog.genericapp.api.log.EZLoggerManager.LogLevel;
import com.plealog.genericapp.api.log.EZSingleLineFormatter;

public class TutorialFiveUIStarter {
  public static void main(String[] args) {
    // This has to be done at the very beginning, i.e. first method call within
    // main().
    EZGenericApplication.initialize("TutorialFiveUIStarter");

    //*** initialize log system - start 
    //EZLogger framework is not a new Logger system: this is just a lightweight wrapper to use
    // standard Java Logging API with a very few methods.
    //Recipes is as follows: call various enableXXX() and setXXX() methods on EZLoggerManager, 
    // as needed, then call EZLoggerManager.initialize().
    //After that, use EZLogger.xxx() methods to log messages. There is no need to use
    // additional Class loggers in your code.

    //turn off Java Logging standard console log
    EZLoggerManager.enableConsoleLogger(false);
    //turn on UI logger; text size limit is set to 2 million characters (when content
    //of UI logger reaches that limit, then UI text component simply reset its content).
    EZLoggerManager.enableUILogger(true, 2);
    //set the Formatter of the UI logger; if not set, default is java.util.logging.SimpleFormatter
    EZLoggerManager.setUILoggerFormatter(new EZSingleLineFormatter(true));
    //Since our UI logger limits log content, we can backup that UI logger with a file logger
    //that will keep all log messages
    try {
      EZLoggerManager.enableFileLogger("logging.txt", true);
    } catch (IOException e) {
      //we can use EZLogger class before EZLoggerManager.initialize(): since EZLogger Framework
      //relies on the standard Java Logging Framework, you'll simply get Java Logging formatted
      //messages on the console
      EZLogger.warn("unable to setup file logger: "+e.toString());
    }
    //set the Formatter of the file logger; if not set, default is java.util.logging.SimpleFormatter
    EZLoggerManager.setFileLoggerFormatter(new EZSingleLineFormatter(true));
    //set log level to INFO
    EZLoggerManager.setLevel(LogLevel.info);
    //setup the logging system
    EZLoggerManager.initialize();
    //*** initialize log system - end 

    EZLogger.info("starting application");

    // Add application branding
    EZApplicationBranding.setAppName("TutorialFiveUIStarter");
    EZApplicationBranding.setAppVersion("1.0");
    EZApplicationBranding.setCopyRight("Created by me");
    EZApplicationBranding.setProviderName("Plealog Software");

    // Add a listener to application startup cycle (see below)
    EZEnvironment.setUIStarterListener(new MyStarterListener());

    // Start the application
    EZGenericApplication.startApplication(args);
  }

  private static class MyStarterListener implements EZUIStarterListener {

    @Override
    public Component getApplicationComponent() {
      // This method is called by the framework to obtain the UI main
      // component to be displayed in the main frame.

      JToolBar jtb = new JToolBar();
      jtb.setFloatable(false);
      jtb.addSeparator();
      //add some actions to set log messages
      jtb.add(new DebugAction());
      jtb.add(new InfoAction());
      jtb.add(new WarnAction());
      jtb.add(new ErrorAction());
      jtb.addSeparator();
      //add some actions to change log level programmatically
      jtb.add(new SetDebugAction());
      jtb.add(new SetInfoAction());
      jtb.add(new SetWarnAction());
      jtb.add(new SetErrorAction());

      JPanel mainPanel = new JPanel(new BorderLayout());
      JPanel tbarPnl1 = new JPanel(new BorderLayout());
      JPanel tbarPnl2 = new JPanel(new BorderLayout());
      tbarPnl1.add(new JLabel("Test log system: "), BorderLayout.WEST);
      tbarPnl1.add(jtb, BorderLayout.EAST);
      tbarPnl2.add(tbarPnl1, BorderLayout.WEST);
      mainPanel.add(tbarPnl2, BorderLayout.NORTH);
      JTabbedPane jpane = new JTabbedPane();
      jpane.addTab("UI Console Logger", EZLoggerManager.getUILogger());
      mainPanel.add(jpane, BorderLayout.CENTER);
      return mainPanel;
    }

    @Override
    public boolean isAboutToQuit() {
      // You can add some code to figure out if application can exit.
      EZLogger.info("leaving application");

      // Return false to prevent application from exiting (e.g. a background
      // task is still running).
      // Return true otherwise.

      // Do not add a Quit dialogue box to ask user confirmation: the framework
      // already does that for you.
      return true;
    }

    @Override
    public void postStart() {
      // This method is called by the framework just before displaying UI
      // (main frame).
    }

    @Override
    public void preStart() {
      // This method is called by the framework at the very beginning of
      // application startup.
    }
  }

  @SuppressWarnings("serial")
  private static class DebugAction extends AbstractAction{
    public DebugAction() {super("debug");}
    public void actionPerformed(ActionEvent event){EZLogger.debug("DebugAction");}
  }
  @SuppressWarnings("serial")
  private static class InfoAction extends AbstractAction{
    public InfoAction() {super("info");}
    public void actionPerformed(ActionEvent event){EZLogger.info("InfoAction");}
  }
  @SuppressWarnings("serial")
  private static class WarnAction extends AbstractAction{
    public WarnAction() {super("warn");}
    public void actionPerformed(ActionEvent event){EZLogger.warn("WarnAction");}
  }
  @SuppressWarnings("serial")
  private static class ErrorAction extends AbstractAction{
    public ErrorAction() {super("error");}
    public void actionPerformed(ActionEvent event){EZLogger.error("ErrorAction");}
  }

  @SuppressWarnings("serial")
  private static class SetDebugAction extends AbstractAction{
    public SetDebugAction() {super("set debug");}
    public void actionPerformed(ActionEvent event){EZLoggerManager.setLevel(LogLevel.debug);}
  }
  @SuppressWarnings("serial")
  private static class SetInfoAction extends AbstractAction{
    public SetInfoAction() {super("set info");}
    public void actionPerformed(ActionEvent event){EZLoggerManager.setLevel(LogLevel.info);}
  }
  @SuppressWarnings("serial")
  private static class SetWarnAction extends AbstractAction{
    public SetWarnAction() {super("set warn");}
    public void actionPerformed(ActionEvent event){EZLoggerManager.setLevel(LogLevel.warn);}
  }
  @SuppressWarnings("serial")
  private static class SetErrorAction extends AbstractAction{
    public SetErrorAction() {super("set error");}
    public void actionPerformed(ActionEvent event){EZLoggerManager.setLevel(LogLevel.error);}
  }
}
