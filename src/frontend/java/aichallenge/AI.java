package aichallenge;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AI
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private String name;

    private String filename;

    @ManyToOne
    private Pidgin pidgin;

    @ManyToOne
    private Game game;

    private int elo;

    @Override
    public String toString()
    {
        return "AI(" + this.name + ", " + this.filename + ", " + this.pidgin + ", " + this.game + ", " + this.elo + ")";
    }

    public long getId() { return this.id; }
}
