package main

import (
	"fmt"
	"math/rand"
)

type GameState uint8

const (
	GAME_WAITING GameState = iota
	GAME_RUNNING
	GAME_ENDED
)

var (
	gameIdCounter int = 0
)

type Game struct {
	Id           int
	Interp       Interpreter
	Master       *Player
	Players      []*Player
	CurrentIndex int
	Code         string
	Result       string
	Task         *Task
	State        GameState
}

func (g *Game) AddPlayer(p *Player) error {
	if g.State != GAME_WAITING {
		return fmt.Errorf("game is not in waiting state")
	}
	if g.IsMember(p) {
		return fmt.Errorf("already member of game")
	}
	g.Players = append(g.Players, p)
	return nil
}

func (g *Game) RemovePlayer(p *Player) error {
	pi := g.GetMemberIndex(p)
	if pi == -1 {
		return fmt.Errorf("not a member of the game")
	}
	isCurrent := g.IsCurrent(p)
	l := len(g.Players) - 1
	g.Players[pi], g.Players[l] = g.Players[l], nil
	g.Players = g.Players[:l]
	if len(g.Players) == 0 {
		g.State = GAME_ENDED
		return nil
	}
	if g.State == GAME_RUNNING && len(g.Players) < 2 {
		g.State = GAME_ENDED
		return nil
	}
	if isCurrent {
		g.nextPlayer()
	}
	if g.IsMaster(p) {
		n := pi + 1
		g.Master = g.Players[n%len(g.Players)]
	}
	return nil
}

func (g *Game) Start() error {
	if g.State != GAME_WAITING {
		return fmt.Errorf("game is not in waiting state")
	}
	if len(g.Players) < 2 {
		return fmt.Errorf("need at least 2 players to start the game")
	}
	for i := range g.Players {
		j := rand.Intn(i + 1)
		if i == j {
			continue
		}
		g.Players[i], g.Players[j] = g.Players[j], g.Players[i]
	}
	g.State = GAME_RUNNING
	return nil
}

func (g *Game) AddLine(line string) error {
	if g.State != GAME_RUNNING {
		return fmt.Errorf("game not running")
	}
	code := g.Code + "\n" + line
	result, err := g.Interp.Eval(code)
	if err != nil {
		return err
	}
	g.Code = code
	g.Result = result
	if g.Task.IsSolution(g.Result) {
		g.State = GAME_ENDED
	} else {
		g.nextPlayer()
	}
	return nil
}

func (g *Game) IsCurrent(p *Player) bool {
	return g.current().Id == p.Id
}

func (g *Game) IsMaster(p *Player) bool {
	return g.Master.Id == p.Id
}

func (g *Game) GetMemberIndex(p *Player) int {
	for i, m := range g.Players {
		if m.Id == p.Id {
			return i
		}
	}
	return -1
}

func (g *Game) IsMember(p *Player) bool {
	return g.GetMemberIndex(p) != -1
}

func (g *Game) StateString() string {
	return map[GameState]string{
		GAME_ENDED:   "Ended",
		GAME_RUNNING: "Running",
		GAME_WAITING: "Waiting for Players",
	}[g.State]
}

func (g *Game) current() *Player {
	if g.CurrentIndex >= len(g.Players) {
		return nil
	}
	return g.Players[g.CurrentIndex]
}

func (g *Game) nextPlayer() {
	if len(g.Players) == 0 {
		g.CurrentIndex = 0
		return
	}
	g.CurrentIndex = (g.CurrentIndex + 1) % len(g.Players)
}
