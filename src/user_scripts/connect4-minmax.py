#!/usr/bin/env python3
import sys
import socket
import json
from copy import deepcopy

COLS = 7
ROWS = 6
INFINITE = 10000

def complete(state):
    return [col + [None] * (ROWS - len(col)) for col in state]

def game_full(state):
    return all(state[i][ROWS-1] is not None for i in range(COLS))

def play(state, player, move):
    assert(state[move][ROWS-1] is None)
    i = ROWS-1

    while i >= 1 and state[move][i-1] is None:
        i -= 1

    state[move][i] = player

def evaluation(state, player):
    lines = state[:] # cols
    lines += [[state[j][i] for j in range(COLS)] for i in range(ROWS)] # rows

    # diags /
    for (x, y) in [(0 if i <= 2 else i - 2, 0 if i >= 2 else 2 - i) for i in range(6)]:
        lines.append([state[x+i][y+i] for i in range(min(6 - y, 7 - x))])

    # diags \
    for (x, y) in [(6 if i >= 3 else 3 + i, 0 if i <= 3 else i - 3) for i in range(6)]:
        lines.append([state[x-i][y+i] for i in range(min(6 - y, 1 + x))])

    point = 0
    for line in lines:
        last = None
        count = 0

        for e in line:
            if e == last:
                count += 1
            else:
                last = e
                count = 1

            if count >= 2 and last is not None:
                signe = 1 if last == player else -1
                if count >= 4:
                    return signe * INFINITE

                point += signe * (count - 1)

    return point

def minmax(state, player, max_depth, player_node=True):
    opponent = 1 if player == 0 else 0

    if max_depth == 0 or game_full(state):
        return (None, evaluation(state, player))

    eval_ = evaluation(state, player)
    if abs(eval_) == INFINITE:
        return (None, eval_)

    best_eval = None
    best_move = None
    comp = (lambda x,y: x>y) if player_node else (lambda x,y: x<y)
    
    for i in range(COLS):
        if state[i][ROWS-1] is None:
            child = deepcopy(state)
            play(child, player if player_node else opponent, i)
            move, eval_ = minmax(child, player, max_depth - 1, not player_node)

            if best_move is None or comp(eval_, best_eval):
                best_eval = eval_
                best_move = i

    return (best_move, best_eval)

def run(host, port, player):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host, port))

    while True:
        # get state
        data = s.recv(1024)
        if not data:
            return

        data = data.decode('latin1')
        print(data)
        state = complete(json.loads(data))

        # play
        move, eval_ = minmax(state, player, 4)
        print('playing %s' % move)
        s.send(bytes(json.dumps(move) + '\n', 'latin1'))

if __name__ == '__main__':
    run(sys.argv[1], int(sys.argv[2]), int(sys.argv[3]))
