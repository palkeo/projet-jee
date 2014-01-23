#!/usr/bin/env python3
import sys
import socket
import json

def run(host, port):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host, port))

    while True:
        # get state
        data = s.recv(1024)
        if not data:
            return

        data = data.decode('latin1')
        state = json.loads(data)
        print(data)

        # play
        x = y = None
        while x is None or y is None or state[x][y] != -1:
            x = int(input('x : '))
            y = int(input('y : '))

        s.send(bytes(json.dumps((x, y)) + '\n', 'latin1'))

if __name__ == '__main__':
    run(sys.argv[1], int(sys.argv[2]))
