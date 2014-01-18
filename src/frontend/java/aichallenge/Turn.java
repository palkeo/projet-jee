package aichallenge;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Turn
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Match match;

    private int turn;

    private String state;

    @Override
    public String toString()
    {
        return "Turn(" + this.match + ", " + this.turn + ", " + this.state + ")";
    }

    public String getState() { return this.state; }
}
