import sys.argv
import os

muleRunBat = sys.argv[1]
sikuliImagesFolder = sys.argv[2]
curlPostInput = sys.argv[3]
apiHttpHost = sys.argv[4]
apiHttpPort = sys.argv[5]
darkTheme = False

setBundlePath(sikuliImagesFolder)

# Turn off CAPS LOCK if on
if Key.isLockOn(Key.CAPS_LOCK):
    keyDown(Key.CAPS_LOCK)
    
#Open WhatsApp, check if it is dark theme, change it to light theme for this update.
#Will be reverted at the end of the test

type("r", Key.WIN)
wait(2)
type("a", Key.CTRL)
wait(2)
# Add WhatsApp Exe path to the Windows user-defined or system path
type("WhatsApp" + Key.ENTER)

# Logic below make sure whatsapp is up and running and also finds which theme it has.
# If dark theme is on, it will switch it to light since all the logic is based on light themed images

for n in range(20):
    if exists("plusLT.PNG"):
        break
    elif exists("plusDT.PNG"):
        darkTheme = True
        break
    elif exists("useHereLT.PNG"):
        click("useHereLT.PNG")
    elif exists("useHereDT.PNG"):
        click("useHereDT.PNG")
    elif exists("RetryLT.PNG"):
        click("RetryLT.PNG")
    elif exists("RetryDT.PNG"):
        click("RetryDT.PNG")
    else:
        print ("No condition satisfied. Wait and retry")
    wait(5)

if (darkTheme):
    rDT = Region(Region(2,3,670,372))
    rDT.click("menuHDT.PNG")
    wait(1)
    click("settingsDT.PNG")
    wait(1)
    click("themeDT.PNG")
    wait(1)
    click("lightOptionDT.PNG")
    wait(1)
    click("okDT.PNG")
    wait(1)
    click("backLT.PNG")
    wait(2)

#Minimize WhatsApp
if exists("WhatsAppMinimize.PNG"):
    click("WhatsAppMinimize.PNG")
else:
    type(Key.TAB, Key.ALT)

wait(2)

#Logic below calls API calls   
httpCodeStr = "%{http_code}"
jsonContentType = "Content-Type:application/json"
httpStatusCode = "000"

muleAPIBaseURL = "http://" + apiHttpHost + ":" + apiHttpPort + "/whatsapp"

healthCheckAPIReqStr = "curl -s -o /dev/null -I -w " + httpCodeStr + " -X GET " + muleAPIBaseURL + "/healthcheck"
automationAPIReqStr = "curl -H " + jsonContentType + " -d@" + curlPostInput + " -X POST " + muleAPIBaseURL

type ("r", Key.WIN)
wait(1)
type("a", Key.CTRL)
wait(2)
type (muleRunBat + Key.ENTER)
wait(5)

for n in range(20):
    healthCheckReqCurlProcess = os.popen(healthCheckAPIReqStr)
    httpStatusCode = healthCheckReqCurlProcess.read()
    print ("HTTP Status code from calling Mule Healthcheck API: " + httpStatusCode)
    
    if httpStatusCode == "200":
        print ("Mule is up for business")
        break
    else:
        wait(5)

if httpStatusCode != "200":
    print ("Mule Healthcheck API failed to return a success code. Exiting...")
    type (Key.F4, Key.ALT)
    exit(1)
    
automationAPICall = os.popen(automationAPIReqStr)

wait ("DONE.PNG",FOREVER)
wait(2)

type("r", Key.WIN)
wait(5)
type("a", Key.CTRL)
wait(2)
# Add WhatsApp Exe path to the Windows user-defined or system path
type("WhatsApp" + Key.ENTER)
wait(2)
#Switch back WhatsApp to original theme if switched above

if (darkTheme):
    rLT = Region(Region(2,3,670,372))
    rLT.click("menuHLT.PNG")   
    wait(1)
    click("settingsLT.PNG")
    wait(1)
    click("themeLT.PNG")
    wait(1)
    click("darkOptionLT.PNG")
    wait(1)
    click("okLT.PNG")
    wait(1)
    click("backDT.PNG")
    wait(2)

#Minimize WhatsApp
if exists("WhatsAppMinimize.PNG"):
    click("WhatsAppMinimize.PNG")
else:
    type(Key.TAB, Key.ALT)

wait(2)

type (Key.F4, Key.ALT)