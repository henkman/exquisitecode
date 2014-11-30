// Write your code in the same way as for native WebSocket:
var ws = new WebSocket('ws://localhost:8887');
ws.onopen = function() {
	console.log('open');
	ws.send('Hello');  // Sends a message.
}
ws.onmessage = function(e) {
	// Receives a message.
	console.log('message', e.data);
}
ws.onclose = function() {
	console.log('close');
}
