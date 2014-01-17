#!/usr/bin/env python3
import sys
import socket
import json
from random import randint

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
        move = 0
        while len(state[move]) >= 6:
            move += 1

        print('playing %s' % move)
        s.send(bytes(json.dumps(move) + '\n', 'latin1'))

if __name__ == '__main__':
    run(sys.argv[1], int(sys.argv[2]))
