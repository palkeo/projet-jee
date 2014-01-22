package aichallenge;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Date;
import javax.persistence.OrderBy;

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
    private String error;
    private Date creationDate;

    public static enum State
    {
        NOT_STARTED,
        STARTED,
        FINISHED,
    };

    @Enumerated(EnumType.ORDINAL)
    private State state;

    @OneToMany(mappedBy="match")
    @OrderBy("turn ASC")
    private List<Turn> turns;

    @Override
    public String toString()
    {
        return "Match(" + this.ai1 + ", " + this.score1 + ", " + this.ai2 + ", " + this.score2 + ", " + this.game + ", " + this.worker + ", " + this.error + ", " + this.state + ")";
    }

    public long getId() { return this.id; }
    public List<Turn> getTurns() { return this.turns; }
    public Game getGame() { return this.game; }
    public String getWorker() { return this.worker; }
    public String getError() { return this.error; }
    public AI getAi1() { return this.ai1; }
    public AI getAi2() { return this.ai2; }
    public int getScore1() { return this.score1; }
    public int getScore2() { return this.score2; }
    public Date getCreationDate() { return this.creationDate; }
    public State getState() { return this.state; }
}
