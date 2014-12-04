function Game(port) {
	this.ws = new WebSocket('ws://localhost:'+port);
	var that = this;
	this.ws.onopen = function() {
		that.ws.send("{'type':'login'}");
	}
	this.ws.onmessage = function(e) {
		console.log('message', e.data);
	}
	this.ws.onclose = function() {
		console.log('close');
	}
}
