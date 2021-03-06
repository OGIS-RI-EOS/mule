GETTING STARTED WITH MULE
=========================

This file describes the basics of how to get started using Mule.
For full details, see the Getting Started Guide at:
http://www.mulesoft.org/documentation/display/MULE3INTRO/Home


Setting Up Your Environment
===========================

Mule uses the MULE_HOME environment variable to point to the
location of your Mule installation.  It is a good practice to
set this variable in your environment.  However, if it is not
set at startup, Mule will temporarily set it based on the location 
of the startup script.   
You may also want to add the MULE_HOME/bin directory to your path. 
If you are using Windows, you can use the System utility in the 
Control Panel to add the MULE_HOME variable and edit your path. 
Alternatively, you can use the export or set commands (depending 
on your operating system) at the command prompt, as shown in the 
following examples:

Linux/Unix:
    export MULE_HOME=/opt/mule
    export PATH=$PATH:$MULE_HOME/bin

Windows:
    set MULE_HOME=C:\Mule
    set PATH=%PATH%;%MULE_HOME%\bin


Distribution Contents
=====================

The Mule distribution contains the following directories and files:

/bin            Shell and batch scripts for controlling Mule from the command
                line
/conf           Configuration files
/docs           API documentation (Javadoc) for Mule and its sub-projects
/examples       Example applications you can run and try building yourself
/lib/boot       Libraries used by the Java Service Wrapper to boot the server
/lib/endorsed   Endorsed Java libraries used by Mule
/lib/mule       Mule libraries
/lib/opt        Third-party libraries
/lib/user       Your custom classes and libraries. This directory comes before
                /lib/mule on the classpath and can be used to patch the
                distributed Mule classes. You must restart Mule after adding
                files to this directory.
/logs           Log file output when running in background mode
/src            The source code for all Mule modules
LICENSE.txt     License agreement for Mule
README.txt      The Getting Started document you are reading


Running Mule
============

Now that you have installed Mule, you are ready to get started!
This section describes how to run Mule.


Starting with the Examples
--------------------------

The examples directory provides you with several examples of Mule
applications including their configuration files, which you can
use as the starting point for creating your configuration file.

All of the examples come pre-built. To test an example, just copy
its zip file to the applications folder ($MULE_HOME/apps) and
start Mule. For more information see
http://www.mulesoft.org/documentation/display/MULE3INTRO/Examples.


Using the Command Prompt
------------------------

To run Mule, you enter the following command at the command prompt:

    mule

To stop Mule, enter Ctrl-C.

To run Mule in the background as a daemon, enter the following command
instead, using start, stop, or restart as the first parameter as needed:

    mule start|stop|restart

For more information on running Mule, see:
http://www.mulesoft.org/documentation/display/MULE3INTRO/Running+Mule.


Where Do I Go Next?
===================

This document has provided a brief overview of getting started
with Mule. Following is information about where to go next.

- For information on more ways to run Mule and how to get started using
the examples, go to the Getting Started Guide at:
http://www.mulesoft.org/documentation/display/MULE3INTRO/Home

- For complete information on using Mule, go to the Mule User Guide at:
http://www.mulesoft.org/documentation/display/MULE3USER/Home. You will need to
register to view these pages, but registration is free and only takes a few
moments.

- If you want to run the examples you can launch Mule ESB with the command:
$MULE_HOME/bin/mule_examples

- If you need assistance and are a Mule Enterprise customer,
see the support page at:
http://www.mulesoft.com/enterprise-subscriptions-and-support

- If you are evaluating Mule and want to find out about subscription
options, you can submit a request for MuleSoft to contact you by
going to http://www.mulesoft.com/buy-mule-esb.

- All Mule users can subscribe to the Mule mailing lists. You can find
these lists at http://www.mulesoft.org/email-lists

- If you experience problems with the Mule software or documentation,
please log an issue in the MuleSoft issue-tracking system, located at:
http://www.mulesoft.org/jira/browse/MULE
