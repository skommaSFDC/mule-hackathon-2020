**Mule Hackathon 2020 - WhatsApp Messaging Automation using Sikuli, MuleSoft, and Windows Batch** <br>By Sreenivasa Komma & Venkatarami Kondudula

For those who may not know what WhatsApp is, link: https://www.whatsapp.com/?lang=en.
A couple of tasks I wanted to automate.
1)	Sending Birthday/Anniversary/Festival/etc. wishes to an individual or a group that you are part of
2)	Setting WhatsApp Profile picture

Since WhatsApp does not seem to offer Public APIs (As far as I know it only offers Business API), I chose WhatsApp desktop connected to my mobile phone to automate the actions on, using Sikuli desktop image-driven automation workflow, Windows batch scripts & task scheduler app, and of course, Mule API & integration solution. 

**Below is the high level diagram.**

![High Level Diagram](https://github.com/srkomma/mule-hackathon-2020/blob/master/High-Level-Diagram.PNG)

**Setup Steps**:
1) Clone this repository
2) Copy sikulix folder to directly under C drive (Later an env variable SIKULIX_HOME on Windows will point to this)
3) Then proceed to these installs

| Install type | Version | Link | Comments |
| :----------- | :------ | :--- | :------- |
| Windows | 10 | | 64 Bit |
| MuleSoft Anypoint Studio | 7.7|https://www.mulesoft.com/lp/dl/studio | Extract to your machine |
| Mule Standalone Runtime|4.3 | https://www.mulesoft.com/lp/dl/mule-esb-enterprise | Extract to your machine |
| Maven | 3.6.3 | https://maven.apache.org/download.cgi?Preferred=https%3A%2F%2Fapache.osuosl.org%2F | Extract to your machine |
| Sikulix IDE | 2.0.4 | https://raiman.github.io/SikuliX1/downloads.html <br> Click **Download the ready to use sikulix.jar (SikuliX IDE)** | **Rename** the jar to **sikulixide.jar** <br> and copy to **C:\sikulix** folder. <br> Could not add this to Git due to size. |
| Jython standalone jar | 2.7.1|https://raiman.github.io/SikuliX1/downloads.html <br> Click **The Jython interpreter 2.7.1 for python scripting (the default)** | copy to **C:\sikulix** folder <br> Could not add this to Git due to size. |
| Sikulix API | 2.0.4 | No need to intall | Added as dependency in https://github.com/srkomma/mule-hackathon-2020/blob/master/whatsapp-automation/pom.xml) |
| json | 20200518 | No need to intall|Added as dependency in https://github.com/srkomma/mule-hackathon-2020/blob/master/whatsapp-automation/pom.xml) |
| JDK | 8 | https://adoptopenjdk.net/ | |
| CURL | 7.55.1 (Windows) | | Windows 10 usually has this exe under C:\Windows\System32. If not install |
| GIT | 2.29.2.windows | https://gitforwindows.org/| |
| WHatsApp Desktop | | https://www.whatsapp.com/download/ <br> Click **DOWNLOAD FOR WINDOWS (64-BIT)** | |

4) **How to log on to WhatsApp Desktop**:
  > * Look at the link: https://faq.whatsapp.com/general/download-and-installation/how-to-log-in-or-out/?lang=en

5) Define **Windows Environment Variables**:
  > * **JAVA_HOME**: Where your JDK 8 is located (Path above bin folder. Example: C:\Program Files\AdoptOpenJDK\jdk-8.0.222.10-hotspot)
  > * **MAVEN_HOME**: Where your Maven is located (Path above bin folder)
  > * **MULE_HOME**: Where Mule ESB runtime is located. (Path above bin folder. Example: C:\mule-enterprise-standalone-4.3.0)
  > * **SIKULIX_HOME**: Where sikulix folder from this repo exists on your machine. C:\sikulix)
  
6) **Windows Path changes - Add the following**:
  > * **%JAVA_HOME%\bin**
  > * **%MULE_HOME%\bin**
  > * **%MAVEN_HOME%\bin**
  > * **Folder having WhatsApp.exe** (for Example: %USERPROFILE%\AppData\Local\WhatsApp) to the path. <br>Sikuli and Java code invoke WhatsApp.exe without the full path. That's why you need to add the folder to the path.

7) **Files needing changes based on your system**:
  > * **sikulix/WindowsWakeup.bat** (Supply values for http.host)
  > * **sikulix/curlPostInput.json** (Supply values for system_default_pictures_folder & whatsapp_desktop_exe)
  > * **sikulix/input_data.csv** (Replace with your data for greetings and WhatsApp Ids/Grps)
  > * **%MULE_HOME%\conf\wrapper.conf**  (Add the following at the end of the file & supply values for <..> properties)
 
    wrapper.java.additional.994="-Dhttp.host=<http.host>" Example: localhost
    wrapper.java.additional.994.stripquotes=TRUE
    wrapper.java.additional.995="-Dhttp.port=<http.port>" Example: 8081  If it is already used on your system, use a different one. This should match with the http.host in WindowsWakeup.bat
    wrapper.java.additional.995.stripquotes=TRUE
    wrapper.java.additional.996="-Dsmtp.host=<smtp.host>" Example: smtp.gmail.com
    wrapper.java.additional.996.stripquotes=TRUE
    wrapper.java.additional.997="-Dsmtp.port=<smtp.port>" Example: 587
    wrapper.java.additional.997.stripquotes=TRUE
    wrapper.java.additional.998="-Dsmtp.user=<smtp.user>" Example: your gmail ID
    wrapper.java.additional.998.stripquotes=TRUE
    wrapper.java.additional.999="-Dsmtp.password=<smtp.password>" Example: Application password. Please see step (9) for how to generate this.
    wrapper.java.additional.999.stripquotes=TRUE
    wrapper.java.additional.1000="-Dsmtp.toEmail=<smtp.toEmail>" Example: Recipient email
    wrapper.java.additional.1000.stripquotes=TRUE

8) In case you need to look at SikuliX Jython script on your machine using SikuliX IDE and/or make code changes: <br> **How to open sikulix/Start-Mule-And-Trigger-API.sikuli/Start-Mule-And-Trigger-API.py on your computer**:
  > * Command to open SikuliX IDE: **java -jar C:\sikulix\sikulixide.jar**
  > * Then file --> open --> select **sikulix/Start-Mule-And-Trigger-API.sikuli/Start-Mule-And-Trigger-API.py** file

9) Generate application password for Mule application using your EMAIL provider's instructions. In our case, we followed this link to do it from GMAIL.
  > * **Link**: https://support.google.com/accounts/answer/185833?hl=en <br> Mule flow has code to email the log file. Please note, password is NOT your email account's password. You will have to generate for Mule Application using custom for application choice. Please note that App Passwords can only be used with accounts that have 2-Step Verification turned on.
  
10) **One time setup to get Windows Sleep and WakeUp working as expected**:
  > * Open **cmd as administrator** on Windows and run this command **powercfg -h off** (Turns off hibernation)
  > * Select this option on your computer: **Windows settings --> Sign-in options --> Select Never for “Require sign-in”**

11) Define **Two tasks in Windows Task Scheduler** <br> Additional README document (Link given below) has a video on how to create the tasks.
  > * **WakeUp**
  > * **SleepAfterWhatsAppUpdate**
  
12) Lastly, you need to package the Mule code. Go to **mule-hackathon-2020\whatsapp-automation** folder under your GIT root dir and run this command:
  > * **mvn clean package -DskipTests**
  > * This should prepare Mule deployable jar file in **mule-hackathon-2020\whatsapp-automation\target** folder.<br> Copy the jar to apps folder in Mule runtime i.e., **%MULE_HOME%/apps** folder
  
**Setup video**

[![Setup](http://img.youtube.com/vi/TK-v-a2Phhc/0.jpg)](http://www.youtube.com/watch?v=TK-v-a2Phhc)
  
  
**Additional README document with useful information**: https://github.com/srkomma/mule-hackathon-2020/blob/master/Additional-README.docx

**DEMO video**

[![Demo](http://img.youtube.com/vi/Zv620Y1FJ3s/0.jpg)](http://www.youtube.com/watch?v=Zv620Y1FJ3s)

