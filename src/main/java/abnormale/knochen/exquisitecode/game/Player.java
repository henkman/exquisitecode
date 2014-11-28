package abnormale.knochen.exquisitecode.game;

// TODO: add to game
public class Player {
	private int id;
	private String name;

	public Player(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
}
