package main

import (
	"crypto/sha512"
	"encoding/gob"
)

type Player struct {
	Id   uint64
	Name string
}

func init() {
	gob.Register(&Player{})
}

func GetPlayerByNameAndPassword(name, password string) (*Player, error) {
	h := sha512.Sum512([]byte(password))
	r := db.QueryRow(`SELECT id, name
FROM player
WHERE name=? AND password=?`, name, h[0:])
	player := new(Player)
	err := r.Scan(&player.Id, &player.Name)
	if err != nil {
		return nil, err
	}
	return player, nil
}

func GetPlayerByName(name string) (*Player, error) {
	r := db.QueryRow(`SELECT id, name
FROM player
WHERE name=?`, name)
	player := new(Player)
	err := r.Scan(&player.Id, &player.Name)
	if err != nil {
		return nil, err
	}
	return player, nil
}

// Check first if player with name exists using GetPlayerByName
func CreatePlayer(name, password string) (*Player, error) {
	h := sha512.Sum512([]byte(password))
	r, err := db.Exec(`INSERT INTO player(name, password)
VALUES (?, ?)`, name, h[0:])
	if err != nil {
		return nil, err
	}
	id, err := r.LastInsertId()
	if err != nil {
		return nil, err
	}
	player := new(Player)
	player.Id = uint64(id)
	player.Name = name
	return player, nil
}
