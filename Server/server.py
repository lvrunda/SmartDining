# -*- coding: utf-8 -*-

from flask import Flask
from flask import request
import json
import business
import time

app = Flask(__name__)
server = business.Server()

@app.route('/api/user/<userId>', methods=['GET'])
def getCredits(userId):
    try:

        if request.method == 'GET':
            result = server.getUserInfo(userId)
            return json.dumps(result), 200, {"content-type": "application/json; charset: utf-8"}
        else:
            return "Invalid method " + request.method, 501, {"content-type": "text/plain; charset: utf-8"}

    except Exception as e:
        print("Got exception = ", e)


@app.route('/api/order/<userId>', methods=['POST'])
def placeOrder(userId):
    data = request.get_json()
    #print(data)
    #data = json.loads(data)
    res = dict()
    timestamp = time.time()
    res['orderId'] = userId + 'X' + str(int(timestamp) - 1557104500)
    res['username'] = data['username']
    res['orderStatus'] = 'In Progress'
    res['cost'] = 0
    res['orderInfo'] = ''
    for item in data['orderDetail']['items']:
        res['cost'] += item['cost']
        res['orderInfo'] += item['name']+'/ '
    res['orderInfo'] += data['orderDetail']['comments']
    try:
        if request.method == 'POST':
            result = server.placeOrder(userId,res)
            return json.dumps(result), 200, {"content-type": "application/json; charset: utf-8"}
        else:
            return "Invalid method " + request.method, 501, {"content-type": "text/plain; charset: utf-8"}

    except Exception as e:
        print("Got exception = ", e)


@app.route('/api/plate/<userId>', methods=['GET'])
def checkEmptyPlate(userId):
    try:
        if request.method == 'GET':
            result = server.addCredit(userId)
            return json.dumps(result), 200, {"content-type": "application/json; charset: utf-8"}
        else:
            return "Invalid method " + request.method, 501, {"content-type": "text/plain; charset: utf-8"}

    except Exception as e:
        print("Got exception = ", e)


def parse_args():
    try:
        if request.data:
            body = json.loads(request.data)
        else:
            body = None
    except Exception as e:
        print("Got exception = ", e)
        body = None
    return body

if __name__ == '__main__':
    app.run('')
