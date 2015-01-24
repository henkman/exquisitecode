package main

import (
	"encoding/json"
	"fmt"
	"github.com/gorilla/websocket"
	"io"
)

type MessageType uint8

const (
	MT_ADDLINE MessageType = iota
	MT_STARTGAME
)

// INTERNAL
type PlayerAddlineMessage struct {
	Player  *Player
	Message AddlineMessage
}

type RemovePlayerMessage struct {
	Player *Player
	Errors chan error
}

type AddPlayerMessage struct {
	Player *Player
	Conn   *websocket.Conn
	Errors chan error
}

// FROM CLIENT
type Message struct {
	Type MessageType
	Data json.RawMessage
}

type AddlineMessage struct {
	Line string
}

type StartGameMessage struct {
	Player *Player
}

// TO CLIENT
type Error struct {
	Error string
}

type StateMessage struct {
	State GameState
}

type CodeResultMessage struct {
	Code   string
	Result string
}

type PlayersMessage struct {
	Players []string
	Master  string
	Current string
}

type WebsocketGame struct {
	*Game
	SocketPlayer     map[*Player]*websocket.Conn
	AddlinesChan     chan PlayerAddlineMessage
	AddPlayerChan    chan AddPlayerMessage
	RemovePlayerChan chan RemovePlayerMessage
	StartGameChan    chan StartGameMessage
}

func (g *WebsocketGame) Run() {
	for g.State != GAME_ENDED {
		select {
		case addline := <-g.AddlinesChan:
			fmt.Println("addline from player", addline.Player.Name)
			conn := g.SocketPlayer[addline.Player]
			if !g.IsCurrent(addline.Player) {
				err := conn.WriteJSON(Error{"not your turn"})
				if err != nil {
					conn.Close()
				}
				continue
			}
			err := g.AddLine(addline.Message.Line)
			if err != nil {
				err := conn.WriteJSON(Error{err.Error()})
				if err != nil {
					conn.Close()
				}
				continue
			}
			g.BroadcastCodeResult()
			g.BroadcastState()
			if g.State != GAME_ENDED {
				g.BroadcastPlayers()
			}
		case startgame := <-g.StartGameChan:
			conn := g.SocketPlayer[startgame.Player]
			if !g.IsMaster(startgame.Player) {
				err := conn.WriteJSON(Error{"you are not the master"})
				if err != nil {
					conn.Close()
				}
				continue
			}
			err := g.Start()
			if err != nil {
				err := conn.WriteJSON(Error{err.Error()})
				if err != nil {
					conn.Close()
				}
				continue
			}
			g.BroadcastState()
		case addplayer := <-g.AddPlayerChan:
			g.SocketPlayer[addplayer.Player] = addplayer.Conn
			err := g.Game.AddPlayer(addplayer.Player)
			addplayer.Errors <- err
			if err == nil {
				g.BroadcastPlayers()
			}
		case removeplayer := <-g.RemovePlayerChan:
			delete(g.SocketPlayer, removeplayer.Player)
			err := g.Game.RemovePlayer(removeplayer.Player)
			removeplayer.Errors <- err
			if err == nil {
				g.BroadcastPlayers()
			}
		}
	}
}

func (g *WebsocketGame) BroadcastPlayers() {
	players := make([]string, len(g.Players))
	for i := range players {
		players[i] = g.Players[i].Name
	}
	var master string
	if g.Master == nil {
		master = ""
	} else {
		master = g.Master.Name
	}
	var current string
	if g.current() == nil {
		current = ""
	} else {
		current = g.current().Name
	}
	g.BroadcastMessage(PlayersMessage{
		players,
		master,
		current})
}

func (g *WebsocketGame) BroadcastState() {
	g.BroadcastMessage(StateMessage{g.State})
}

func (g *WebsocketGame) BroadcastCodeResult() {
	g.BroadcastMessage(CodeResultMessage{g.Code, g.Result})
}

func (g *WebsocketGame) BroadcastMessage(msg interface{}) {
	tokill := make([]*Player, 0)
	for player, conn := range g.SocketPlayer {
		err := conn.WriteJSON(msg)
		if err != nil {
			tokill = append(tokill, player)
		}
	}
	for _, player := range tokill {
		g.RemovePlayer(player)
	}
}

func (g *WebsocketGame) AddPlayer(p *Player, conn *websocket.Conn) chan error {
	fmt.Println("adding player", p.Name)
	errchan := make(chan error)
	go func() {
		g.AddPlayerChan <- AddPlayerMessage{p, conn, errchan}
	}()
	return errchan
}

func (g *WebsocketGame) RemovePlayer(p *Player) chan error {
	fmt.Println("removing player", p.Name)
	errchan := make(chan error)
	go func() {
		g.RemovePlayerChan <- RemovePlayerMessage{p, errchan}
	}()
	return errchan
}

func (g *WebsocketGame) HandlePlayer(p *Player, conn *websocket.Conn) {
	conn.WriteJSON(CodeResultMessage{g.Code, g.Result})
	conn.WriteJSON(StateMessage{g.State})
	for g.State != GAME_ENDED {
		var msg Message
		err := conn.ReadJSON(&msg)
		if err != nil {
			if err == io.EOF {
				<-g.RemovePlayer(p)
				break
			}
			continue
		}
		switch msg.Type {
		case MT_ADDLINE:
			var addline PlayerAddlineMessage
			err := json.Unmarshal(msg.Data, &addline.Message)
			if err != nil {
				continue
			}
			addline.Player = p
			g.AddlinesChan <- addline
		case MT_STARTGAME:
			g.StartGameChan <- StartGameMessage{p}
		default:
			continue
		}
	}
}
