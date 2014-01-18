#!/usr/bin/env python3
import sys
import socket
import json

def run(host, port):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host, port))

    data = s.recv(1024)
    if not data:
        return

    s.send(bytes(json.dumps(-1) + '\n', 'latin1'))

if __name__ == '__main__':
    run(sys.argv[1], int(sys.argv[2]))
