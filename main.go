package main

import (
	"database/sql"
	"fmt"
	"github.com/gorilla/mux"
	"github.com/gorilla/securecookie"
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
	cookiestore = sessions.NewCookieStore(securecookie.GenerateRandomKey(32))
)

func indexHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/html")
	session, err := cookiestore.Get(r, "exquisite")
	var player *Player
	if err == nil {
		p, ok := session.Values["player"].(*Player)
		if ok && p != nil {
			player = p
		}
	}

	tmpl.ExecuteTemplate(w, "index.html", struct {
		Games  []*WebsocketGame
		Player *Player
	}{
		gm.Games,
		player,
	})
}

func logoutHandler(w http.ResponseWriter, r *http.Request) {
	session, _ := cookiestore.Get(r, "exquisite")
	session.Options = &sessions.Options{MaxAge: -1}
	session.Save(r, w)
	http.Redirect(w, r, "/", http.StatusFound)
}

func loginHandler(w http.ResponseWriter, r *http.Request) {
	name := r.FormValue("name")
	pass := r.FormValue("pass")
	if name == "" || pass == "" {
		http.Error(w, "no credentials specified", http.StatusUnauthorized)
		return
	}
	session, _ := cookiestore.Get(r, "exquisite")
	player, err := GetPlayerByNameAndPassword(name, pass)
	if err != nil {
		http.Error(w, "bad credentials", http.StatusUnauthorized)
		return
	}
	session.Values["player"] = player
	session.Save(r, w)
	http.Redirect(w, r, "/", http.StatusFound)
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
	session, err := cookiestore.Get(r, "exquisite")
	if err != nil {
		http.Redirect(w, r, "/", http.StatusUnauthorized)
		return
	}
	player, ok := session.Values["player"].(*Player)
	if !ok || player == nil {
		http.Redirect(w, r, "/", http.StatusUnauthorized)
		return
	}
	w.Header().Set("Content-Type", "text/html")
	tmpl.ExecuteTemplate(w, "game.html", struct {
		Game   *WebsocketGame
		Player *Player
	}{
		game,
		player,
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
		http.Error(w, "invalid session", http.StatusUnauthorized)
		return
	}
	if !game.CanAddPlayer(player) {
		http.Error(w, "invalid session", http.StatusUnauthorized)
		return
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
	game := gm.CreateGame(interp, task)
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
	post.HandleFunc("/login", loginHandler)
	post.HandleFunc("/logout", logoutHandler)
	http.Handle("/", r)

	log.Fatal(http.ListenAndServe(":8080", nil))
}
