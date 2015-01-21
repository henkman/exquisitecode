package main

import (
	"testing"
)

func TestSimpleGame(t *testing.T) {
	interp := NewJSInterpreter()
	task := new(Task)
	task.Id = 0
	task.Description = "Description"
	task.Solution = "correctresult"
	master := new(Player)
	master.Id = 0
	master.Name = "master"
	other := new(Player)
	other.Id = 1
	other.Name = "other"
	g := NewGameManager().CreateGame(interp, task, master)
	err := g.AddPlayer(master)
	if err != nil {
		t.Fatal(err)
	}
	err = g.AddPlayer(other)
	if err != nil {
		t.Fatal(err)
	}
	err = g.RemovePlayer(other)
	if err != nil {
		t.Fatal(err)
	}
	err = g.AddPlayer(other)
	if err != nil {
		t.Fatal(err)
	}
	err = g.Start()
	if err != nil {
		t.Fatal(err)
	}

	var current, next *Player
	if g.IsCurrent(other) {
		current = other
		next = master
	} else {
		current = master
		next = other
	}

	err = g.AddLine("result = 'result1';")
	if err != nil {
		t.Fatal(err)
	}
	if !g.IsCurrent(next) {
		t.Fatal("should have advanced to next player")
	}
	current, next = next, current

	err = g.AddLine("result = 'result2';")
	if err != nil {
		t.Fatal(err)
	}
	if !g.IsCurrent(next) {
		t.Fatal("should have advanced to next player")
	}

	err = g.AddLine("result = 'correctresult';")
	if err != nil {
		t.Fatal(err)
	}

	err = g.AddLine("result = 'testme';")
	if err == nil {
		t.Fatal("should complain that the game has already ended")
	}
}
