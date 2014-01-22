package aichallenge;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToOne;

@Entity
public class AI
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private String name;

    private String filename;

    @Column(columnDefinition="TEXT")
    private String description;

    @ManyToOne
    private Pidgin pidgin;

    @ManyToOne
    private Game game;

    private int elo;

    private AI(){}

    public AI(String name, String filename, String description, Pidgin pidgin, Game game)
    {
        this.name = name;
        this.filename = filename;
        this.description = description;
        this.pidgin = pidgin;
        this.game = game;
        this.elo = 1000; // this is not a random value
    }

    @Override
    public String toString()
    {
        return "AI(" + this.name + ", " + this.filename + ", " + this.description + ", " + this.pidgin + ", " + this.game + ", " + this.elo + ")";
    }

    public long getId() { return this.id; }
    public String getName() { return this.name; }
    public String getFilename() { return this.filename; }
    public String getDescription() { return this.description; }
    public Pidgin getPidgin() { return this.pidgin; }
    public Game getGame() { return this.game; }
    public int getElo() { return this.elo; }
}
