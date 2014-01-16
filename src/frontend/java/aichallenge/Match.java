package aichallenge;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

@Entity
public class Match
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @ManyToOne
    private AI ai1;

    private int score1;

    @ManyToOne
    private AI ai2;

    private int score2;

    @ManyToOne
    private Game game;

    private String worker;

    public enum State
    {
        NOT_STARTED,
        STARTED,
        FINISHED,
    };

    @Enumerated(EnumType.ORDINAL)
    private State state;

    @Override
    public String toString()
    {
        return "Match(" + this.ai1 + ", " + this.score1 + ", " + this.ai2 + ", " + this.score2 + ", " + this.game + ", " + this.worker + ", " + this.state + ")";
    }
}
