package main

import (
	"testing"
)

func TestResult(t *testing.T) {
	t.Skip()
	interp := NewJSInterpreter()
	result, err := interp.Eval(`
	function getMeDaAnswer() {
		var a = 42;
		return a;
	}
	result = ""+getMeDaAnswer();	
`)
	if err != nil {
		t.Fatalf("should not throw any errors: %v", err)
	}
	if result != "42" {
		t.Fatalf("result should be 42, but is %s", result)
	}
}

func TestEndlessLoopDetection(t *testing.T) {
	t.Skip()
	interp := NewJSInterpreter()
	_, err := interp.Eval("while(true);")
	if err == nil {
		t.Fatal("should throw endless loop error")
	}
}
