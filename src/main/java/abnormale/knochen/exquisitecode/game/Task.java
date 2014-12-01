package abnormale.knochen.exquisitecode.game;

public class Task {
    private int id;
    private String name;
    private String description;
    private String solution;

    public Task(int id, String name, String description, String solution) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.solution = solution;
    }

    public boolean isSolution(String solution) {
        return this.solution == solution;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }
}
