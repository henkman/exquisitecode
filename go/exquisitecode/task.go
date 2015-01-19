package main

type Task struct {
	Id          uint64
	Description string
	Solution    string
}

func (t *Task) IsSolution(result string) bool {
	return t.Solution == result
}

func GetRandomTask() (*Task, error) {
	/*
		r := db.QueryRow(`SELECT id, description, solution
	FROM task
	ORDER BY RANDOM() LIMIT 1`)
	*/
	r := db.QueryRow(`SELECT id, description, solution
FROM task
WHERE id=2`)
	task := new(Task)
	err := r.Scan(&task.Id, &task.Description, &task.Solution)
	if err != nil {
		return nil, err
	}
	return task, nil
}
