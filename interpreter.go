package main

import (
	"fmt"
	"github.com/robertkrimen/otto"
	"time"
)

const (
	CODE_MAX_RUNTIME     = 5 * time.Second
	RESULT_VARIABLE_NAME = "result"
)

type Interpreter interface {
	Eval(code string) (string, error)
	PrefixCode() string
}

type JSInterpreter struct {
	PrefixCodeS string
}

func NewJSInterpreter() *JSInterpreter {
	interp := new(JSInterpreter)
	interp.PrefixCodeS = "var result = '';\n"
	return interp
}

func (interp *JSInterpreter) Eval(code string) (string, error) {
	vm := otto.New()
	vm.Interrupt = make(chan func(), 1)
	ended := make(chan interface{})

	var err error
	go func() {
		_, err = vm.Run(code)
		if err != nil {
			ended <- err
		}
		ended <- true
	}()

	timeout := time.After(CODE_MAX_RUNTIME)
loop:
	for {
		select {
		case ok := <-ended:
			if ok != true {
				return "", ok.(error)
			}
			break loop
		case <-timeout:
			vm.Interrupt <- func() {
				ended <- fmt.Errorf("code ran too long")
			}
		}
	}
	result, err := vm.Get(RESULT_VARIABLE_NAME)
	if err != nil {
		return "", err
	}
	if !result.IsString() {
		return "", fmt.Errorf("%s is not a string", RESULT_VARIABLE_NAME)
	}
	return result.String(), nil
}

func (interp *JSInterpreter) PrefixCode() string {
	return interp.PrefixCodeS
}
