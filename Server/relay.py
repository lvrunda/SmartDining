# -*- coding: utf-8 -*-

from flask import Flask
from flask import request
import os

app = Flask(__name__)

@app.route('/api/camera', methods=['GET'])
def handleRequest():
    try:

        if request.method == 'GET':
            if uploadIamge():
                return "Image uploaded", 200, {"content-type": "text/plain; charset: utf-8"}
            else:
                return "Image upload failed", 403, {"content-type": "text/plain; charset: utf-8"}
        else:
            return "Invalid method " + request.method, 501, {"content-type": "text/plain; charset: utf-8"}

    except Exception as e:
        print("Got exception = ", e)


def uploadIamge():
    os.system('fswebcam -r 1280x720 --jpeg 100 -D 3 -S 13 "test.jpg"')
    os.system('')
    return True

if __name__ == '__main__':
    try:
        app.run(host= '')
    except KeyboardInterrupt:
        exit()
