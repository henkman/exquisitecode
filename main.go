package main

import (
	"database/sql"
	"fmt"
	"github.com/gorilla/mux"
	"github.com/gorilla/sessions"
	"github.com/gorilla/websocket"
	_ "github.com/mattn/go-sqlite3"
	"html/template"
	"log"
	"net/http"
	"runtime"
	"strconv"
)

var (
	tmpl       *template.Template
	gm         *GameManager
	db         *sql.DB
	wsUpgrader = websocket.Upgrader{
		ReadBufferSize:  1024,
		WriteBufferSize: 1024,
	}
	cookiestore = sessions.NewCookieStore([]byte("yodawg"))
)

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
	session, _ := cookiestore.Get(r, "exquisite")
	player, ok := session.Values["player"].(*Player)
	if !ok || player == nil {
		a, _ := GetPlayerByName("a")
		b, _ := GetPlayerByName("b")
		c, _ := GetPlayerByName("c")
		players := []*Player{a, b, c}
		for _, p := range players {
			if !game.IsMember(p) {
				player = p
				break
			}
		}
		if player == nil {
			http.NotFound(w, r)
			return
		}
	}
	conn, err := wsUpgrader.Upgrade(w, r, nil)
	if err != nil {
		http.NotFound(w, r)
		return
	}
	if err := <-game.AddPlayer(player, conn); err != nil {
		return
	}
	go game.HandlePlayer(player, conn)
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
	game := gm.CreateGame(interp, task, master)
	go game.Run()
}

func newGameHandler(w http.ResponseWriter, r *http.Request) {
	http.Redirect(w, r, fmt.Sprintf("/game/%d", 0),
		http.StatusSeeOther)
}

func main() {
	runtime.GOMAXPROCS(runtime.NumCPU())

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
