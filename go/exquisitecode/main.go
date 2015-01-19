package main

import (
	"database/sql"
	"fmt"
	"github.com/gorilla/mux"
	"github.com/gorilla/websocket"
	_ "github.com/mattn/go-sqlite3"
	"html/template"
	"log"
	"net/http"
	"strconv"
	"time"
)

var (
	tmpl       *template.Template
	gm         *GameManager
	db         *sql.DB
	wsUpgrader = websocket.Upgrader{
		ReadBufferSize:  1024,
		WriteBufferSize: 1024,
	}
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
	g.Players = []*Player{master}
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

func indexHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/html")
	tmpl.ExecuteTemplate(w, "index.html", struct {
		Games []*WebsocketGame
	}{
		gm.Games,
	})
}

func gameHandler(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	sid := vars["id"]
	id, err := strconv.Atoi(sid)
	if err != nil {
		http.NotFound(w, r)
		return
	}
	game := gm.GetGameById(id)
	if game == nil {
		http.NotFound(w, r)
		return
	}
	w.Header().Set("Content-Type", "text/html")
	tmpl.ExecuteTemplate(w, "game.html", struct {
		Game *WebsocketGame
	}{
		game,
	})
}

func gameSockHandler(w http.ResponseWriter, r *http.Request) {
	conn, err := wsUpgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Println(err)
		return
	}
	go func() {
		for {
			messageType, p, err := conn.ReadMessage()
			if err != nil {
				return
			}
			time.Sleep(time.Second)
			conn.WriteMessage(messageType, p)
		}
	}()
}

func blah() {
	interp := NewJSInterpreter()
	task, err := GetRandomTask()
	if err != nil {
		log.Println(err)
		return
	}
	master, err := GetPlayerByName("a")
	if err != nil {
		log.Println(err)
		return
	}
	gm.CreateGame(interp, task, master)

}

func newGameHandler(w http.ResponseWriter, r *http.Request) {
	http.Redirect(w, r, fmt.Sprintf("/game/%d", 0),
		http.StatusSeeOther)
}

func main() {
	var err error
	tmpl, err = template.ParseGlob("./tmpl/*.html")
	if err != nil {
		log.Fatal(err)
	}
	db, err = sql.Open("sqlite3", "./exquisite.db")
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	gm = new(GameManager)
	blah()
	r := mux.NewRouter()
	get := r.Methods("GET").Subrouter()
	get.HandleFunc("/", indexHandler)
	get.HandleFunc("/game/{id:[0-9]+}", gameHandler)
	get.HandleFunc("/gamesock/{id:[0-9]+}", gameSockHandler)
	get.PathPrefix("/").Handler(http.FileServer(http.Dir("./htdocs/")))
	post := r.Methods("POST").Subrouter()
	post.HandleFunc("/newgame", newGameHandler)
	http.Handle("/", r)

	log.Fatal(http.ListenAndServe(":8080", nil))
}
