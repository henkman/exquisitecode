package abnormale.knochen.exquisitecode.game;

public class Task {
    private String name;
    private String description;
    private String solution;

    public Task(String name, String description, String solution) {
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
}
