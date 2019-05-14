# -*- coding: utf-8 -*-

import json, time
import httplib, urllib, requests
from ml.mlpacket.checkempty import checkEmpty

class Server:

    serverUrl = ""
    appId = ""
    clientKey = ""
    cost = 10
    bonus = 30

    def getConnection(self):
        self.connection = httplib.HTTPConnection(Server.serverUrl, 80)
        self.connection.connect()

    def getUserInfo(self, userId):
        self.getConnection()
        param = urllib.urlencode({'where': json.dumps({
            'username': userId
        })})
        self.connection.request('GET', '/parse/users?%s' % param, '', {
            "X-Parse-Application-Id": Server.appId
        })
        result = json.loads(self.connection.getresponse().read())
        return result

    def placeOrder(self, userId, data):
        self.getConnection()
        rec = data
        post = dict()
        post['orderId'] =rec['orderId']
        post['username'] = rec['username']
        post['orderInfo'] = rec['orderInfo']
        post['orderStatus'] = rec['orderStatus']
        userInfo = self.getUserInfo(userId)
        credit = int(userInfo['results'][0]['credits'])
        if credit < rec['cost']:
            return {'orderStatus':"No enough credits"}
        credit -= rec['cost']
        objectId = userInfo['results'][0]['objectId']
        # update user's credit
        self.postInfo('PUT', '/parse/users/' + objectId, json.dumps({
            'credits': credit
        }))
        # save order information
        result = self.postInfo('POST', '/parse/classes/Order', json.dumps(post))
        result['orderStatus'] = 'Success'
        return result

    def postInfo(self, method, path, data):
        self.getConnection()
        self.connection.request(method, path, data, {
            "X-Parse-Application-Id": Server.appId,
            "X-Parse-Master-Key": Server.clientKey,
            "Content-Type": "application/json"
        })
        result = json.loads(self.connection.getresponse().read())
        return result

    def addCredit(self, userId):
        if self.checkEmptyPlate():
            userInfo = self.getUserInfo(userId)
            credit = int(userInfo['results'][0]['credits']) + Server.bonus
            objectId = userInfo['results'][0]['objectId']
            result = self.postInfo('PUT', '/parse/users/' + objectId, json.dumps({
                'credits': credit
            }))
            result["Status"]="Success"
        else:
            result = {"Status":"Failed"}
        return result

    def checkEmptyPlate(self):
        if not self.sendRequest():
            # Failed to upload image
            return False
        time.sleep(3)
        # return true if the plate is empty
        return checkEmpty()

    def sendRequest(self):
        path = ''
        for i in range(5):
            response = requests.get(path)
            if response.status_code == 200:
                return True
        return False
