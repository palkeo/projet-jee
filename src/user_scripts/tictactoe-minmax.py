#!/usr/bin/env python3
import sys
import socket
import json
from copy import deepcopy

########################
# From another project #
########################

INFINI = 10**6

def jeu_complet(morpion):
    taille = len(morpion)
    for i in range(taille):
        for j in range(taille):
            if morpion[i][j] is None:
                return False
    return True

def evaluation(morpion, joueur):
    '''
    Retourne l'évaluation d'une position du point de vue du joueur
    '''
    adversaire = not joueur
    n = len(morpion)
    score = 0

    lignes = morpion[:] # lignes
    lignes += [[morpion[i][j] for i in range(n)] for j in range(n)] # colonnes
    lignes.append([morpion[i][i] for i in range(n)]) # première diagonale
    lignes.append([morpion[n-i-1][i] for i in range(n)]) # deuxième diagonale

    for ligne in lignes:
        if ligne == [joueur] * n:
            return INFINI
        if ligne == [not joueur] * n:
            return -INFINI

        if adversaire not in ligne:
            score += len(list(filter(lambda i:i == joueur, ligne)))

        if joueur not in ligne:
            score -= len(list(filter(lambda i:i == adversaire, ligne)))

    return score

def minmax(morpion, joueur, profondeur_max, eval_fn, elagage=True, noeud_joueur=True, alpha_beta=None):
    '''
    Retourne le couple (c, e) avec :
    * c le coup (x,y) optimal à jouer
    * e l'évaluation maximale atteignable par l'algorithme du min max, avec une profondeur maximale profondeur_max.

    morpion est l'état actuel du morpion ;
    joueur indique notre joueur (True ou False) ;
    eval_fn est la fonction d'évaluation ;
    elagage indique s'il faut appliquer l'élagage alpha-beta.

    noeud_joueur et alpha_beta sont des paramètres utiles uniquement lors des appels récursifs :
    * noeud_joueur vaut true si on doit prendre le max, sinon on doit prendre le min.
    * alpha_beta est soit alpha, soit beta, en fonction de noeud_joueur
    '''
    if profondeur_max == 0 or jeu_complet(morpion):
        return (None, eval_fn(morpion, joueur))

    evaluation = eval_fn(morpion, joueur)
    if abs(evaluation) == INFINI:
        return (None, evaluation)

    n = len(morpion)
    evaluation_optimale = None
    coup_optimal = None
    comp = (lambda x,y: x>y) if noeud_joueur else (lambda x,y: x<y)

    for x in range(n):
        for y in range(n):
            if morpion[x][y] is None:
                fils = deepcopy(morpion)
                fils[x][y] = joueur if noeud_joueur else not joueur
                coup, evaluation = minmax(fils, joueur, profondeur_max - 1, eval_fn, elagage, not noeud_joueur, evaluation_optimale)

                if evaluation_optimale is None:
                    evaluation_optimale = evaluation
                    coup_optimal = (x,y)
                elif comp(evaluation, evaluation_optimale):
                    evaluation_optimale = evaluation
                    coup_optimal = (x, y)

                if elagage and alpha_beta is not None:
                    if noeud_joueur and evaluation_optimale >= alpha_beta:
                        return coup_optimal, evaluation_optimale
                    elif not noeud_joueur and evaluation_optimale <= alpha_beta:
                        return coup_optimal, evaluation_optimale

    return coup_optimal, evaluation_optimale

def coups_possibles(morpion):
    coups_possibles = []
    taille = len(morpion)
    for i in range(taille):
        for j in range(taille):
            if morpion[i][j] is None:
                coups_possibles.append((i,j))
    return coups_possibles

####################
# TicTacToe minmax #
####################

def convert(state):
    return [[bool(e) if e != -1 else None for e in col] for col in state]

def run(host, port, player):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((host, port))

    while True:
        # get state
        data = s.recv(1024)
        if not data:
            return

        data = data.decode('latin1')
        state = convert(json.loads(data))
        print(state)

        # play
        x, y = minmax(state, player, 4, evaluation, True)[0]
        print('playing (%s, %s)' % (x, y))
        s.send(bytes(json.dumps((x, y)) + '\n', 'latin1'))

if __name__ == '__main__':
    run(sys.argv[1], int(sys.argv[2]), bool(sys.argv[3]))
