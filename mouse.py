# -*- coding: cp936 -*-
from pymouse import PyMouse
import socket
import json

UDP_IP = '0.0.0.0'
UDP_PORT = 5000

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
sock.settimeout(1)
sock.bind((UDP_IP, UDP_PORT))

m = PyMouse()
x_max, y_max = m.screen_size()

SCALE = 5
X_SCALE = -(1.0/2.0/3.14159) *x_max *SCALE
Y_SCALE = -(1.0/2.0/3.14159) *y_max *SCALE



while True:
    pos = [0,0]
    try:        
        print "\n"
        data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes        data = data.split(',')
        data = "["+data+"]"
        data = json.loads(data)
        pos[0] = int(data[2] * X_SCALE + 0.5*x_max)
        pos[1] = int(data[0] * Y_SCALE + 0.5*y_max)
        print pos
        if data[3]:
            action = m.click
        else:
            action = m.move
        action(pos[0],pos[1])
    except socket.timeout:
        pass
    except:
        raise

