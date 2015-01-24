var messagetypes = {
	"MT_ADDLINE":0,
	"MT_STARTGAME":1,
};
var gamestate = {
	"WAITING":0,
	"RUNNING":1,
	"ENDED":2
};
function Game(myself, gameid) {
	this.myself = myself;
	this.clearerror = null;
	this.$players = document.getElementById("players");
	this.$error = document.getElementById("error");
	this.$running = document.getElementById("running");
	this.$waiting = document.getElementById("waiting");
	this.$ended = document.getElementById("ended");
	this.$code = document.getElementsByTagName("code")[0];
	this.$result = document.getElementById("result");
	var self = this;
	this.socket = new WebSocket("ws://localhost:8080/gamesock/"+gameid);
	this.socket.onopen = function(openev) {
		console.log("we are open");
	};
	this.socket.onerror = function(errorev) {
		console.log(errorev);
		self.$error.innerHTML = "socket closed";
	};
	this.socket.onclose = function(closeev) {
		self.$error.innerHTML = "socket closed";
	};
	this.socket.onmessage = function(msgev) {
		console.log("got message: " + msgev.data);
		var msg = {};
		try {
			msg = JSON.parse(msgev.data);
		} catch(e) {
			console.log("json parse: ", e);
		}
		if(typeof msg.Error !== 'undefined') {
			self.$error.innerHTML = msg.Error;
			self.clearerror = window.setTimeout(function() {
				self.$error.innerHTML = "";
			}, 3000);
		} else if(typeof msg.State !== 'undefined') {
			self.handleStateMessage(msg);
		} else if(typeof msg.Code !== 'undefined' &&
			typeof msg.Result !== 'undefined') {
			self.handleCodeResultMessage(msg);
		} else if(typeof msg.Players !== 'undefined' &&
			typeof msg.Master !== 'undefined' &&
			typeof msg.Current !== 'undefined') {
			self.handlePlayerMessage(msg);
		} else {
			console.log("unknown message: " + msg);	
		}
	};
}
Game.prototype = {
	addLine: function(line) {
		if(this.clearerror) {
			window.clearTimeout(this.clearerror);
		}
		this.$error.innerHTML = "";
		var msg = {"Type":messagetypes.MT_ADDLINE,"Data":{"Line":line}};
		this.socket.send(JSON.stringify(msg));
	},
	startGame: function() {
		if(this.clearerror) {
			window.clearTimeout(this.clearerror);
		}
		this.$error.innerHTML = "";
		var msg = {"Type":messagetypes.MT_STARTGAME,"Data":{}};
		this.socket.send(JSON.stringify(msg));
	},
	handleStateMessage: function(msg) {
		if(msg.State === gamestate.RUNNING) {
			this.$waiting.style.display="none";
			this.$running.style.display="block";
			this.$ended.style.display="none";
			console.log("RUNNING");
		} else if(msg.State === gamestate.ENDED) {
			this.$waiting.style.display="none";
			this.$running.style.display="none";
			this.$ended.style.display="block";
			console.log("ENDED");
		} else {
			this.$waiting.style.display="block";
			this.$running.style.display="none";
			this.$ended.style.display="none";
			console.log("WAITING");
		}
	},
	handleCodeResultMessage: function(msg) {
		this.$code.innerHTML = msg.Code;
		this.$result.innerHTML = msg.Result;
	},
	handlePlayerMessage: function(msg) {
		while (this.$players.firstChild) {
		    this.$players.removeChild(this.$players.firstChild);
		}
		for(var i = 0; i < msg.Players.length; i++) {
			var player = msg.Players[i];
			var li = document.createElement("li");
			li.innerHTML = player;
			if(player === msg.Master) {
				li.classList.add("master");
			}
			if(player === msg.Current) {
				li.classList.add("current");
			}
			this.$players.appendChild(li);
		}
		var $startgame = document.getElementById("startgame");
		if(this.myself === msg.Master && msg.Players.length >= 2) {
			$startgame.style.display="block";
		} else {
			$startgame.style.display="none";
		}
		var $addline = document.getElementById("addline");
		if(this.myself === msg.Current) {
			$addline.style.display="block";
		} else {
			$addline.style.display="none";
		}
	}
}
