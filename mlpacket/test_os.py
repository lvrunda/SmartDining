import os
os.system('fswebcam -r 1280x720 --jpeg 100 -D 3 -S 13 "test.jpg"')
os.system('scp -i biot.pem -r test.jpg ec2-user@ec2-54-152-189-230.compute-1.amazonaws.com:test.jpg')
