package main

import (
	"github.com/gorilla/websocket"
)

type GameManager struct {
	Games     []*WebsocketGame
	IdCounter int
}

func NewGameManager() *GameManager {
	gm := new(GameManager)
	gm.Games = make([]*WebsocketGame, 0)
	gm.IdCounter = 0
	return gm
}

func (gm *GameManager) CreateGame(interp Interpreter, task *Task, master *Player) *WebsocketGame {
	g := new(WebsocketGame)
	g.Game = new(Game)
	g.Id = gm.IdCounter
	g.Interp = interp
	g.Task = task
	g.Master = master
	g.State = GAME_WAITING
	g.Code = interp.PrefixCode()
	g.Players = []*Player{}

	g.SocketPlayer = make(map[*Player]*websocket.Conn)
	g.AddlinesChan = make(chan PlayerAddlineMessage)
	g.AddPlayerChan = make(chan AddPlayerMessage)
	g.RemovePlayerChan = make(chan RemovePlayerMessage)

	gm.IdCounter++
	gm.Games = append(gm.Games, g)
	return g
}

func (gm *GameManager) GetGameById(id int) *WebsocketGame {
	for _, game := range gm.Games {
		if game.Id == id {
			return game
		}
	}
	return nil
}
