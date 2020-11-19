import sys.argv
import os

muleRunBat = sys.argv[1]
sikuliImagesFolder = sys.argv[2]
curlPostInput = sys.argv[3]
apiHttpHost = sys.argv[4]
apiHttpPort = sys.argv[5]

setBundlePath(sikuliImagesFolder)

if Key.isLockOn(Key.CAPS_LOCK):
    keyDown(Key.CAPS_LOCK)
    
httpCodeStr = "%{http_code}"
jsonContentType = "Content-Type:application/json"
httpStatusCode = "000"

muleAPIBaseURL = "http://" + apiHttpHost + ":" + apiHttpPort + "/whatsapp"

healthCheckAPIReqStr = "curl -s -o /dev/null -I -w " + httpCodeStr + " -X GET " + muleAPIBaseURL + "/healthcheck"
automationAPIReqStr = "curl -H " + jsonContentType + " -d@" + curlPostInput + " -X POST " + muleAPIBaseURL

type ("r", Key.WIN)
wait(1)

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

type (Key.F4, Key.ALT)